package cn.cheers.x.module.dynamicbusiness.service.capability;

import cn.cheers.x.module.dynamicbusiness.dal.mysql.capability.InstanceCapabilityRegistryMapper;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 启动时补全动态业务能力注册（dynamic-model / dynamic-entity）。
 * System 能力由 {@link SystemCapabilityBootstrapRunner} 负责；动态侧需按业务类型 + 模型重建。
 */
@Component
@Slf4j
public class DynamicCapabilityBootstrapRunner implements ApplicationRunner {

    private static final long BOOTSTRAP_TENANT_ID = 1L;

    @Resource
    private InstanceCapabilityRegistryMapper registryMapper;
    @Resource
    private CapabilityRegistryRebuildService rebuildService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            TenantUtils.execute(BOOTSTRAP_TENANT_ID, () -> {
                long dynamicEntityCount = registryMapper.selectSummaryList(null, "dynamic-entity").size();
                if (dynamicEntityCount > 0) {
                    return;
                }
                log.info("[DynamicCapabilityBootstrap] registry 中无 dynamic-entity 能力，开始重建全部动态业务能力…");
                rebuildService.rebuildAllDynamicCapabilities();
                long modelCount = registryMapper.selectSummaryList(null, "dynamic-model").size();
                dynamicEntityCount = registryMapper.selectSummaryList(null, "dynamic-entity").size();
                log.info("[DynamicCapabilityBootstrap] 完成：dynamic-model={}，dynamic-entity={}",
                        modelCount, dynamicEntityCount);
            });
        } catch (Exception ex) {
            log.warn("[DynamicCapabilityBootstrap] 初始化动态业务能力失败（不影响服务启动）: {}", ex.getMessage());
        }
    }
}
