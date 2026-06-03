package cn.cheers.x.module.dynamicbusiness.service.capability;

import cn.cheers.x.module.dynamicbusiness.dal.mysql.capability.InstanceCapabilityRegistryMapper;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 本地/空库时自动写入 System 固定资源能力（system:dept 等），避免设计器首次打开即 400。
 */
@Component
@Slf4j
public class SystemCapabilityBootstrapRunner implements ApplicationRunner {

    /** 本地 bootstrap 默认租户（与 mock 登录默认 tenant-id 对齐） */
    private static final long BOOTSTRAP_TENANT_ID = 1L;

    @Resource
    private InstanceCapabilityRegistryMapper registryMapper;
    @Resource
    private CapabilityRegistryRebuildService rebuildService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            TenantUtils.execute(BOOTSTRAP_TENANT_ID, () -> {
                if (!registryMapper.selectSummaryList(null, "system").isEmpty()) {
                    return;
                }
                log.info("[SystemCapabilityBootstrap] registry 中无 system 能力，开始初始化…");
                rebuildService.rebuildAllSystemCapabilities();
                log.info("[SystemCapabilityBootstrap] 已初始化 system 能力 {} 条", SystemCapabilityCatalog.all().size());
            });
        } catch (Exception ex) {
            log.warn("[SystemCapabilityBootstrap] 初始化 system 能力失败（不影响服务启动）: {}", ex.getMessage());
        }
    }
}
