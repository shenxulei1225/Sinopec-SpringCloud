package cn.cheers.x.module.dynamicbusiness.service.push;

import cn.cheers.x.framework.common.enums.UserTypeEnum;
import cn.cheers.x.framework.security.core.LoginUser;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 启动时把推送主题、推送订阅两本目录准备出来，数据管理里才能看到分组「消息」。
 * 准备时记在超级管理员名下。找不到这个账号就停下来，不另编一个用户。
 * 不负责写当前订阅行。那要等页面真的订上。
 */
@Component
@Order(120)
@Slf4j
public class PushCatalogBootstrapRunner implements ApplicationRunner {

    private static final String SUPER_USERNAME = "super";

    @Resource
    private PushCatalogEnsure pushCatalogEnsure;
    @Resource
    private TenantFrameworkService tenantFrameworkService;
    @Resource
    private AdminUserApi adminUserApi;

    @Override
    public void run(ApplicationArguments args) {
        AdminUserRespDTO user;
        try {
            user = adminUserApi.getUser(1L).getCheckedData();
        } catch (Exception ex) {
            log.error("[PushCatalogBootstrap][没有读到超级管理员，推送目录没有准备]", ex);
            return;
        }
        if (user == null || user.getId() == null || !SUPER_USERNAME.equals(user.getUsername())) {
            log.error("[PushCatalogBootstrap][超级管理员账号对不上，推送目录没有准备]");
            return;
        }
        List<Long> tenantIds = tenantFrameworkService.getTenantIds();
        if (CollUtil.isEmpty(tenantIds)) {
            log.error("[PushCatalogBootstrap][没有可用租户，推送目录没有准备]");
            return;
        }
        LoginUser loginUser = new LoginUser();
        loginUser.setId(user.getId());
        loginUser.setUserType(UserTypeEnum.ADMIN.getValue());
        for (Long tenantId : tenantIds) {
            try {
                TenantUtils.execute(tenantId, () -> {
                    SecurityFrameworkUtils.setLoginUser(loginUser, null);
                    try {
                        pushCatalogEnsure.ensure();
                    } finally {
                        SecurityContextHolder.clearContext();
                    }
                });
            } catch (Exception ex) {
                log.error("[PushCatalogBootstrap][租户的推送目录没有准备好][tenantId={}]", tenantId, ex);
            }
        }
    }
}
