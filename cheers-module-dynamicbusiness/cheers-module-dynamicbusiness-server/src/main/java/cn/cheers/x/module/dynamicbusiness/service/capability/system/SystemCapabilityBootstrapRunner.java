package cn.cheers.x.module.dynamicbusiness.service.capability.system;

import cn.cheers.x.module.dynamicbusiness.service.capability.BusinessCapabilityService;
import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.tenant.core.service.TenantFrameworkService;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 启动时为每个租户注册系统业务能力（写入 business_capability + 投影）。
 *
 * <p><b>interim 策略</b>：system 与 dynamic 共用 {@code TenantBaseDO} 与同一张能力表；
 * 各租户各持有一份内容相同的 system 能力副本（catalog 一致，{@code tenant_id} 不同）。
 * 待平台级 system 能力存储定稿后，再改为平台一份、dynamic 仍按租户隔离。</p>
 *
 * <p>{@code businessCategory=system} 仅表示业务分类，不表示绕过租户插件。</p>
 */
@Component
@Order(100)
@Slf4j
public class SystemCapabilityBootstrapRunner implements ApplicationRunner {

    @Resource
    private BusinessCapabilityService businessCapabilityService;
    @Resource
    private TenantFrameworkService tenantFrameworkService;

    @Override
    public void run(ApplicationArguments args) {
        List<Long> tenantIds = tenantFrameworkService.getTenantIds();
        if (CollUtil.isEmpty(tenantIds)) {
            log.warn("[SystemCapabilityBootstrap][跳过：无可用租户]");
            return;
        }

        int successTenantCount = 0;
        for (Long tenantId : tenantIds) {
            try {
                TenantUtils.execute(tenantId, businessCapabilityService::rebuildAllSystemCapabilities);
                successTenantCount++;
            } catch (Exception ex) {
                log.warn("[SystemCapabilityBootstrap][租户注册失败][tenantId={}]", tenantId, ex);
            }
        }
        log.info("[SystemCapabilityBootstrap][完成][tenantCount={}][capabilityPerTenant={}]",
                successTenantCount, SystemCapabilityCatalog.all().size());
    }
}
