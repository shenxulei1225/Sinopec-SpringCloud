package cn.cheers.x.module.dynamicbusiness.service.useraccount;

import cn.cheers.x.framework.common.enums.UserTypeEnum;
import cn.cheers.x.framework.security.core.LoginUser;
import cn.cheers.x.framework.tenant.core.service.TenantFrameworkService;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.cheers.x.system.api.user.AdminUserApi;
import cn.cheers.x.system.api.user.dto.AdminUserRespDTO;
import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 启动时为每个租户把用户目录准备成站场级，并补上所属场站。
 * 准备时记在超级管理员名下。读不到这个账号时仍用编号 1。
 * 打开数据管理页面不调用这里。
 */
@Component
@Order(140)
@Slf4j
public class UserAccountCatalogBootstrapRunner implements ApplicationRunner {

    private static final String SUPER_USERNAME = "super";

    @Resource
    private UserAccountCatalogEnsure userAccountCatalogEnsure;
    @Resource
    private TenantFrameworkService tenantFrameworkService;
    @Resource
    private AdminUserApi adminUserApi;

    @Override
    public void run(ApplicationArguments args) {
        long userId = 1L;
        try {
            AdminUserRespDTO user = adminUserApi.getUser(1L).getCheckedData();
            if (user != null && user.getId() != null && SUPER_USERNAME.equals(user.getUsername())) {
                userId = user.getId();
            } else {
                log.error("超级管理员账号对不上，仍用编号 1 准备用户目录");
            }
        } catch (Exception ex) {
            log.error("没有读到超级管理员，仍用编号 1 准备用户目录", ex);
        }
        List<Long> tenantIds = tenantFrameworkService.getTenantIds();
        if (CollUtil.isEmpty(tenantIds)) {
            log.error("没有可用租户，用户目录没有准备");
            return;
        }
        LoginUser loginUser = new LoginUser();
        loginUser.setId(userId);
        loginUser.setUserType(UserTypeEnum.ADMIN.getValue());
        for (Long tenantId : tenantIds) {
            try {
                TenantUtils.execute(tenantId, () -> {
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList()));
                    try {
                        userAccountCatalogEnsure.ensureModelId();
                    } finally {
                        SecurityContextHolder.clearContext();
                    }
                });
            } catch (Exception ex) {
                log.error("租户 {} 的用户目录没有准备好：{}", tenantId, ex.getMessage());
            }
        }
    }
}
