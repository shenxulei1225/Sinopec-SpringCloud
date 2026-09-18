package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.framework.tenant.core.service.TenantFrameworkService;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 启动时把「已开能力」的数据目录再走一遍开能力补列。
 *
 * 负责：迁移或种子只写了开关、没走过保存入口时，补列、写用途、写挂载。
 * 不负责：改开关；读详情时再补一列；另写一份 SQL 猜列名。
 */
@Component
@Order(110)
@Slf4j
public class EntityTypeCapabilityProvisionBootstrap implements ApplicationRunner {

    @Resource
    private EntityTypeCapabilityService entityTypeCapabilityService;
    @Resource
    private TenantFrameworkService tenantFrameworkService;

    @Override
    public void run(ApplicationArguments args) {
        List<Long> tenantIds = tenantFrameworkService.getTenantIds();
        if (CollUtil.isEmpty(tenantIds)) {
            log.warn("[EntityTypeCapabilityProvision] 跳过：无可用租户");
            return;
        }
        int successTenantCount = 0;
        for (Long tenantId : tenantIds) {
            try {
                TenantUtils.execute(tenantId, entityTypeCapabilityService::provisionEnabledCapabilities);
                successTenantCount++;
            } catch (Exception ex) {
                log.warn("[EntityTypeCapabilityProvision] 租户补列失败 tenantId={}", tenantId, ex);
            }
        }
        log.info("[EntityTypeCapabilityProvision] 完成 tenantCount={}", successTenantCount);
    }
}
