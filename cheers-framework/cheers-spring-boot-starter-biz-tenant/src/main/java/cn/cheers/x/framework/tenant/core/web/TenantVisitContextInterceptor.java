package cn.cheers.x.framework.tenant.core.web;

import cn.hutool.core.util.ObjUtil;
import cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.cheers.x.framework.security.core.LoginUser;
import cn.cheers.x.framework.security.core.service.SecurityFrameworkService;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.framework.tenant.config.TenantProperties;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.framework.web.core.util.WebFrameworkUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception0;

@RequiredArgsConstructor
@Slf4j
public class TenantVisitContextInterceptor implements HandlerInterceptor {

    private static final String PERMISSION = "system:tenant:visit";

    private final TenantProperties tenantProperties;

    private final SecurityFrameworkService securityFrameworkService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果和当前租户编号一致，则直接跳过
        Long visitTenantId = WebFrameworkUtils.getVisitTenantId(request);
        if (visitTenantId == null) {
            return true;
        }
        if (ObjUtil.equal(visitTenantId, TenantContextHolder.getTenantId())) {
            return true;
        }
        // 必须是登录用户
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return true;
        }

        // 系统账号的家在平台。登录页选的业务租户只决定这次看哪份数据。
        // 业务账号仍要有切换租户的权限，不能靠请求头把自己送进别的租户。
        boolean systemAccount = loginUser.getInfo() != null
                && LoginUser.ACCOUNT_SCOPE_PLATFORM.equals(loginUser.getInfo().get(LoginUser.INFO_KEY_ACCOUNT_SCOPE));
        if (!systemAccount && !securityFrameworkService.hasAnyPermissions(PERMISSION)) {
            throw exception0(GlobalErrorCodeConstants.FORBIDDEN.getCode(), "您无权切换租户");
        }

        // 【重点】切换租户编号
        loginUser.setVisitTenantId(visitTenantId);
        TenantContextHolder.setTenantId(visitTenantId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 【重点】清理切换，换回原租户编号
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser != null && loginUser.getTenantId() != null) {
            TenantContextHolder.setTenantId(loginUser.getTenantId());
        }
    }

}
