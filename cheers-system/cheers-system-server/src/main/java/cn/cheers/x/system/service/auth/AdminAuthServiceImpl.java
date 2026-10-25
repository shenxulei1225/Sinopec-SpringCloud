package cn.cheers.x.system.service.auth;

import cn.hutool.core.util.ObjectUtil;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.enums.UserTypeEnum;
import cn.cheers.x.framework.common.util.monitor.TracerUtils;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.framework.common.util.servlet.ServletUtils;
import cn.cheers.x.framework.common.util.validation.ValidationUtils;
import cn.cheers.x.framework.datapermission.core.annotation.DataPermission;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.cheers.x.module.dynamicbusiness.api.directory.UserAccountApi;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.UserAccountDTO;
import cn.cheers.x.system.api.logger.dto.LoginLogCreateReqDTO;
import cn.cheers.x.system.api.sms.SmsCodeApi;
import cn.cheers.x.system.api.sms.dto.code.SmsCodeUseReqDTO;
import cn.cheers.x.system.api.social.dto.SocialUserBindReqDTO;
import cn.cheers.x.system.api.social.dto.SocialUserRespDTO;
import cn.cheers.x.system.controller.admin.auth.vo.*;
import cn.cheers.x.system.convert.auth.AuthConvert;
import cn.cheers.x.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import cn.cheers.x.system.enums.logger.LoginLogTypeEnum;
import cn.cheers.x.system.enums.logger.LoginResultEnum;
import cn.cheers.x.system.enums.oauth2.OAuth2ClientConstants;
import cn.cheers.x.system.enums.sms.SmsSceneEnum;
import cn.cheers.x.system.service.logger.LoginLogService;
import cn.cheers.x.system.service.member.MemberService;
import cn.cheers.x.system.service.oauth2.OAuth2TokenService;
import cn.cheers.x.system.service.social.SocialUserService;
import cn.cheers.x.system.service.tenant.TenantService;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.service.CaptchaService;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import jakarta.validation.Validator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.framework.common.util.servlet.ServletUtils.getClientIP;
import static cn.cheers.x.system.enums.ErrorCodeConstants.*;

/**
 * 登录。
 *
 * 业务账号只属于一个业务租户。登录页选哪个租户，就只在那个租户里认账号。
 * 系统账号属于平台。所选租户里没有这个账号时，再到平台认。令牌记在平台，
 * 选中的业务租户通过 visitTenantId 交给前端。
 *
 * 不负责：把系统账号复制进每个租户。平台本身不能当作登录目标。
 */
@Service
@Slf4j
public class AdminAuthServiceImpl implements AdminAuthService {

    @Resource
    private UserAccountApi userAccountApi;
    @Resource
    private LoginCredentialService loginCredentialService;
    @Resource
    private TenantService tenantService;
    @Resource
    private LoginLogService loginLogService;
    @Resource
    private OAuth2TokenService oauth2TokenService;
    @Resource
    private SocialUserService socialUserService;
    @Resource
    private MemberService memberService;
    @Resource
    private Validator validator;
    @Resource
    private CaptchaService captchaService;
    @Resource
    private ArithmeticCaptchaService arithmeticCaptchaService;
    @Resource
    private SmsCodeApi smsCodeApi;

    /**
     * 验证码的开关，默认为 true
     */
    @Value("${cheers.captcha.enable:true}")
    @Setter // 为了单测：开启或者关闭验证码
    private Boolean captchaEnable;

    @Override
    public UserAccountDTO authenticate(String username, String password) {
        final LoginLogTypeEnum logTypeEnum = LoginLogTypeEnum.LOGIN_USERNAME;
        UserAccountDTO user = findUserAccount(username);
        if (user == null) {
            createLoginLog(null, username, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        if (!loginCredentialService.matches(user.getId(), password)) {
            createLoginLog(user.getId(), username, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        // 用户目录只认 1=启用。状态缺失或其它值都拒绝，不猜成启用。
        if (!Integer.valueOf(1).equals(user.getStatus())) {
            createLoginLog(user.getId(), username, logTypeEnum, LoginResultEnum.USER_DISABLED);
            throw exception(AUTH_LOGIN_USER_DISABLED);
        }
        return user;
    }

    @Override
    @DataPermission(enable = false)
    public AuthLoginRespVO login(AuthLoginReqVO reqVO) {
        // 校验验证码
        validateCaptcha(reqVO);

        Long selectedTenantId = TenantContextHolder.getTenantId();
        if (tenantService.isPlatformTenant(selectedTenantId)) {
            throw exception(AUTH_LOGIN_TENANT_NOT_BUSINESS);
        }
        // 所选业务租户先查用户目录。没有时再查平台租户的用户目录。
        UserAccountDTO user = findUserAccount(reqVO.getUsername());
        Long accountTenantId = selectedTenantId;
        if (user == null) {
            accountTenantId = tenantService.getPlatformTenantId();
            user = authenticatePlatformUser(reqVO.getUsername(), reqVO.getPassword());
        } else {
            user = authenticate(reqVO.getUsername(), reqVO.getPassword());
        }

        // 如果 socialType 非空，说明需要绑定社交用户
        if (reqVO.getSocialType() != null) {
            socialUserService.bindSocialUser(new SocialUserBindReqDTO(user.getId(), getUserType().getValue(),
                    reqVO.getSocialType(), reqVO.getSocialCode(), reqVO.getSocialState()));
        }
        return issueLoginToken(user, reqVO.getUsername(), accountTenantId, selectedTenantId);
    }

    /**
     * 所选业务租户里没有这个账号时，到平台认系统账号。
     * 密码不对、账号停用，和普通登录同一句失败提示。平台上没有这个账号，也按账号不存在处理。
     */
    private UserAccountDTO authenticatePlatformUser(String username, String password) {
        Long platformTenantId = tenantService.getPlatformTenantId();
        UserAccountDTO platformUser = TenantUtils.execute(platformTenantId,
                () -> findUserAccount(username));
        if (platformUser == null) {
            createLoginLog(null, username, LoginLogTypeEnum.LOGIN_USERNAME, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        return TenantUtils.execute(platformTenantId, () -> authenticate(username, password));
    }

    /**
     * 令牌写在账号所属租户上。系统账号选了业务租户时，把选中的租户放进 visitTenantId。
     * 前端之后用 tenant-id 对齐令牌，用 visit-tenant-id 读选中租户的数据。
     */
    private AuthLoginRespVO issueLoginToken(UserAccountDTO user, String username,
                                            Long accountTenantId, Long selectedTenantId) {
        boolean crossTenant = selectedTenantId != null
                && accountTenantId != null
                && !selectedTenantId.equals(accountTenantId);
        if (crossTenant && (user.getVisitTenantIds() == null
                || !user.getVisitTenantIds().contains(selectedTenantId))) {
            throw new ServiceException(403, "这个平台用户不能进入所选业务租户");
        }
        AuthLoginRespVO resp = crossTenant
                ? TenantUtils.execute(accountTenantId, () ->
                createTokenAfterLoginSuccess(user.getId(), username, LoginLogTypeEnum.LOGIN_USERNAME))
                : createTokenAfterLoginSuccess(user.getId(), username, LoginLogTypeEnum.LOGIN_USERNAME);
        resp.setTenantId(accountTenantId != null ? accountTenantId : selectedTenantId);
        if (crossTenant) {
            resp.setVisitTenantId(selectedTenantId);
        }
        return resp;
    }

    @Override
    public void sendSmsCode(AuthSmsSendReqVO reqVO) {
        // 如果是重置密码场景，需要校验图形验证码是否正确
        if (Objects.equals(SmsSceneEnum.ADMIN_MEMBER_RESET_PASSWORD.getScene(), reqVO.getScene())) {
            ResponseModel response = doValidateCaptcha(reqVO);
            if (!response.isSuccess()) {
                throw exception(AUTH_REGISTER_CAPTCHA_CODE_ERROR, response.getRepMsg());
            }
        }

        // 登录场景，验证是否存在
        if (findUserAccountByMobile(reqVO.getMobile()) == null) {
            throw exception(AUTH_MOBILE_NOT_EXISTS);
        }
        // 发送验证码
        smsCodeApi.sendSmsCode(AuthConvert.INSTANCE.convert(reqVO).setCreateIp(getClientIP()));
    }

    @Override
    public AuthLoginRespVO smsLogin(AuthSmsLoginReqVO reqVO) {
        // 校验验证码
        smsCodeApi.useSmsCode(AuthConvert.INSTANCE.convert(reqVO, SmsSceneEnum.ADMIN_MEMBER_LOGIN.getScene(), getClientIP())).checkError();

        // 获得用户信息
        UserAccountDTO user = findUserAccountByMobile(reqVO.getMobile());
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        if (!Integer.valueOf(1).equals(user.getStatus())) {
            throw exception(AUTH_LOGIN_USER_DISABLED);
        }

        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user.getId(), reqVO.getMobile(), LoginLogTypeEnum.LOGIN_MOBILE);
    }

    private void createLoginLog(Long userId, String username,
                                LoginLogTypeEnum logTypeEnum, LoginResultEnum loginResult) {
        // 插入登录日志
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setLogType(logTypeEnum.getType());
        reqDTO.setTraceId(TracerUtils.getTraceId());
        reqDTO.setUserId(userId);
        reqDTO.setUserType(getUserType().getValue());
        reqDTO.setUsername(username);
        reqDTO.setUserAgent(ServletUtils.getUserAgent());
        reqDTO.setUserIp(ServletUtils.getClientIP());
        reqDTO.setResult(loginResult.getResult());
        loginLogService.createLoginLog(reqDTO);
    }

    @Override
    public AuthLoginRespVO socialLogin(AuthSocialLoginReqVO reqVO) {
        // 使用 code 授权码，进行登录。然后，获得到绑定的用户编号
        SocialUserRespDTO socialUser = socialUserService.getSocialUserByCode(UserTypeEnum.ADMIN.getValue(), reqVO.getType(),
                reqVO.getCode(), reqVO.getState());
        if (socialUser == null || socialUser.getUserId() == null) {
            throw exception(AUTH_THIRD_LOGIN_NOT_BIND);
        }

        // 获得用户
        UserAccountDTO user = userAccountApi.getUserAccount(socialUser.getUserId()).getCheckedData();

        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user.getId(), user.getAccount(), LoginLogTypeEnum.LOGIN_SOCIAL);
    }

    @VisibleForTesting
    void validateCaptcha(AuthLoginReqVO reqVO) {
        ResponseModel response = doValidateCaptcha(reqVO);
        // 校验验证码
        if (!response.isSuccess()) {
            // 创建登录失败日志（验证码不正确)
            createLoginLog(null, reqVO.getUsername(), LoginLogTypeEnum.LOGIN_USERNAME, LoginResultEnum.CAPTCHA_CODE_ERROR);
            throw exception(AUTH_LOGIN_CAPTCHA_CODE_ERROR, response.getRepMsg());
        }
    }

    private ResponseModel doValidateCaptcha(CaptchaVerificationReqVO reqVO) {
        // 如果验证码关闭，则不进行校验
        if (!captchaEnable) {
            return ResponseModel.success();
        }
        ValidationUtils.validate(validator, reqVO, CaptchaVerificationReqVO.CodeEnableGroup.class);
        boolean success = arithmeticCaptchaService.validateCaptchaVerification(reqVO.getCaptchaVerification(), true);
        return success ? ResponseModel.success() : ResponseModel.errorMsg("验证码错误或已过期");
    }

    private AuthLoginRespVO createTokenAfterLoginSuccess(Long userId, String username, LoginLogTypeEnum logType) {
        // 插入登陆日志
        createLoginLog(userId, username, logType, LoginResultEnum.SUCCESS);
        // 创建访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createAccessToken(userId, getUserType().getValue(),
                OAuth2ClientConstants.CLIENT_ID_DEFAULT, null);
        // 构建返回结果
        return BeanUtils.toBean(accessTokenDO, AuthLoginRespVO.class);
    }

    @Override
    public AuthLoginRespVO refreshToken(String refreshToken) {
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.refreshAccessToken(refreshToken, OAuth2ClientConstants.CLIENT_ID_DEFAULT);
        return BeanUtils.toBean(accessTokenDO, AuthLoginRespVO.class);
    }

    @Override
    public void logout(String token, Integer logType) {
        // 删除访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.removeAccessToken(token);
        if (accessTokenDO == null) {
            return;
        }
        // 删除成功，则记录登出日志
        createLogoutLog(accessTokenDO.getUserId(), accessTokenDO.getUserType(), logType);
    }

    private void createLogoutLog(Long userId, Integer userType, Integer logType) {
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setLogType(logType);
        reqDTO.setTraceId(TracerUtils.getTraceId());
        reqDTO.setUserId(userId);
        reqDTO.setUserType(userType);
        if (ObjectUtil.equal(getUserType().getValue(), userType)) {
            reqDTO.setUsername(getUsername(userId));
        } else {
            reqDTO.setUsername(memberService.getMemberUserMobile(userId));
        }
        reqDTO.setUserAgent(ServletUtils.getUserAgent());
        reqDTO.setUserIp(ServletUtils.getClientIP());
        reqDTO.setResult(LoginResultEnum.SUCCESS.getResult());
        loginLogService.createLoginLog(reqDTO);
    }

    private String getUsername(Long userId) {
        if (userId == null) {
            return null;
        }
        return userAccountApi.getUserAccount(userId).getCheckedData().getAccount();
    }

    /**
     * 在当前租户按账号找用户目录。只有“账号不存在”返回空。
     * 远程失败和其它数据错误继续抛出，禁止把服务异常伪装成平台账号回退。
     */
    private UserAccountDTO findUserAccount(String username) {
        try {
            return userAccountApi.getUserAccountByAccount(username).getCheckedData();
        } catch (ServiceException ex) {
            if (Objects.equals(ex.getCode(), 404)) {
                return null;
            }
            throw ex;
        }
    }

    private UserAccountDTO findUserAccountByMobile(String mobile) {
        try {
            return userAccountApi.getUserAccountByMobile(mobile).getCheckedData();
        } catch (ServiceException ex) {
            if (Objects.equals(ex.getCode(), 404)) {
                return null;
            }
            throw ex;
        }
    }

    private UserTypeEnum getUserType() {
        return UserTypeEnum.ADMIN;
    }

    @Override
    public AuthLoginRespVO register(AuthRegisterReqVO registerReqVO) {
        throw new ServiceException(400, "请在用户目录创建用户");
    }

    @VisibleForTesting
    void validateCaptcha(AuthRegisterReqVO reqVO) {
        ResponseModel response = doValidateCaptcha(reqVO);
        // 验证不通过
        if (!response.isSuccess()) {
            throw exception(AUTH_REGISTER_CAPTCHA_CODE_ERROR, response.getRepMsg());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(AuthResetPasswordReqVO reqVO) {
        UserAccountDTO userByMobile = findUserAccountByMobile(reqVO.getMobile());
        if (userByMobile == null) {
            throw exception(USER_MOBILE_NOT_EXISTS);
        }

        smsCodeApi.useSmsCode(new SmsCodeUseReqDTO()
                .setCode(reqVO.getCode())
                .setMobile(reqVO.getMobile())
                .setScene(SmsSceneEnum.ADMIN_MEMBER_RESET_PASSWORD.getScene())
                .setUsedIp(getClientIP())
        ).checkError();

        loginCredentialService.replace(userByMobile.getId(), reqVO.getPassword());
    }
}
