package cn.iocoder.yudao.module.member.convert.auth;

import cn.iocoder.yudao.module.member.controller.app.auth.vo.*;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialUserUnbindReqVO;
import cn.iocoder.yudao.module.member.controller.app.user.vo.AppMemberUserResetPasswordReqVO;
import cn.cheers.x.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenRespDTO;
import cn.cheers.x.system.api.sms.dto.code.SmsCodeSendReqDTO;
import cn.cheers.x.system.api.sms.dto.code.SmsCodeUseReqDTO;
import cn.cheers.x.system.api.sms.dto.code.SmsCodeValidateReqDTO;
import cn.cheers.x.system.api.social.dto.SocialUserBindReqDTO;
import cn.cheers.x.system.api.social.dto.SocialUserUnbindReqDTO;
import cn.cheers.x.system.api.social.dto.SocialWxJsapiSignatureRespDTO;
import cn.cheers.x.system.enums.sms.SmsSceneEnum;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthConvert {

    AuthConvert INSTANCE = Mappers.getMapper(AuthConvert.class);

    SocialUserBindReqDTO convert(Long userId, Integer userType, AppAuthSocialLoginReqVO reqVO);
    SocialUserUnbindReqDTO convert(Long userId, Integer userType, AppSocialUserUnbindReqVO reqVO);

    SmsCodeSendReqDTO convert(AppAuthSmsSendReqVO reqVO);
    SmsCodeUseReqDTO convert(AppMemberUserResetPasswordReqVO reqVO, SmsSceneEnum scene, String usedIp);
    SmsCodeUseReqDTO convert(AppAuthSmsLoginReqVO reqVO, Integer scene, String usedIp);

    AppAuthLoginRespVO convert(OAuth2AccessTokenRespDTO bean, String openid);

    SmsCodeValidateReqDTO convert(AppAuthSmsValidateReqVO bean);

    SocialWxJsapiSignatureRespDTO convert(SocialWxJsapiSignatureRespDTO bean);

}
