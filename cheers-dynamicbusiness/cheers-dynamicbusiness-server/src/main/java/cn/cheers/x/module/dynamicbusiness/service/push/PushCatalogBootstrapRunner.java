package cn.cheers.x.module.dynamicbusiness.service.push;

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
 * 启动时把事件主题、事件订阅两本目录准备出来，数据管理里才能看到分组「事件」。
 * 准备时记在超级管理员名下。读不到这个账号时仍用编号 1，不另编一个用户。
 * 已有订阅行在这时改成订阅方名称，并补上事件主题引用。
 */
@Component
@Order(120)
@Slf4j
public class PushCatalogBootstrapRunner implements ApplicationRunner {

    private static final String SUPER_USERNAME = "super";

    @Resource
    private PushCatalogEnsure pushCatalogEnsure;
    @Resource
    private PushSubscriptionMirrorService pushSubscriptionMirrorService;
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
                log.error("[PushCatalogBootstrap][超级管理员账号对不上，仍用编号 1 准备事件目录]");
            }
        } catch (Exception ex) {
            log.error("[PushCatalogBootstrap][没有读到超级管理员，仍用编号 1 准备事件目录]", ex);
        }
        List<Long> tenantIds = tenantFrameworkService.getTenantIds();
        if (CollUtil.isEmpty(tenantIds)) {
            log.error("[PushCatalogBootstrap][没有可用租户，推送目录没有准备]");
            return;
        }
        LoginUser loginUser = new LoginUser();
        loginUser.setId(userId);
        loginUser.setUserType(UserTypeEnum.ADMIN.getValue());
        for (Long tenantId : tenantIds) {
            try {
                TenantUtils.execute(tenantId, () -> {
                    // 启动时没有浏览器请求。登录用户直接放进上下文。
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList()));
                    try {
                        pushCatalogEnsure.ensure();
                        pushSubscriptionMirrorService.repairSubscriberRows();
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
