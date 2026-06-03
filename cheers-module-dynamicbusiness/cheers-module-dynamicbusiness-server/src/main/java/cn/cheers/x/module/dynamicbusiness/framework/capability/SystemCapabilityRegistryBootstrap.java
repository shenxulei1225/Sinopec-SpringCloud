package cn.cheers.x.module.dynamicbusiness.framework.capability;

import cn.cheers.x.module.dynamicbusiness.service.capability.CapabilityRegistryRebuildService;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 应用启动时注册 System 模块固定资源能力（与 systemListAdapters 对齐）。
 */
@Component
@Order(100)
@Slf4j
public class SystemCapabilityRegistryBootstrap implements ApplicationRunner {

    private static final long BOOTSTRAP_TENANT_ID = 1L;

    @Resource
    private CapabilityRegistryRebuildService capabilityRegistryRebuildService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            TenantUtils.execute(BOOTSTRAP_TENANT_ID, capabilityRegistryRebuildService::rebuildAllSystemCapabilities);
            log.info("[SystemCapabilityRegistryBootstrap] system capabilities registered");
        } catch (Exception ex) {
            log.warn("[SystemCapabilityRegistryBootstrap] failed to register system capabilities: {}", ex.getMessage());
        }
    }
}
