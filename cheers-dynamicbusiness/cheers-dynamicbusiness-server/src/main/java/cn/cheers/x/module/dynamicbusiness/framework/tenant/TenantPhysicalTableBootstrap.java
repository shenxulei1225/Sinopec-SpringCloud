package cn.cheers.x.module.dynamicbusiness.framework.tenant;

import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 启动时为已有租户补齐关联物理表（{@code *_t{tenantId}}）。
 * 实体专用表由类型创建 / Flyway V37 负责；此处只保证关联表模板已分表。
 */
@Component
@Order(50)
@Slf4j
public class TenantPhysicalTableBootstrap implements ApplicationRunner {

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private TenantAssociationTableService tenantAssociationTableService;

    @Override
    public void run(ApplicationArguments args) {
        Set<Long> tenantIds = discoverTenantIds();
        if (tenantIds.isEmpty()) {
            log.info("[tenant-table] 未发现租户，跳过关联表补齐");
            return;
        }
        for (Long tenantId : tenantIds) {
            try {
                TenantUtils.execute(tenantId, () -> tenantAssociationTableService.ensureAssociationTables(tenantId));
            } catch (Exception e) {
                log.warn("[tenant-table] 租户 {} 关联表补齐失败: {}", tenantId, e.getMessage());
            }
        }
        log.info("[tenant-table] 关联表补齐完成，tenants={}", tenantIds);
    }

    private Set<Long> discoverTenantIds() {
        Set<Long> ids = new HashSet<>();
        try {
            List<Long> fromTypes = jdbcTemplate.queryForList(
                    """
                    SELECT DISTINCT tenant_id FROM dynamicbusiness.dynamic_entity_type
                    WHERE deleted = false AND tenant_id IS NOT NULL AND tenant_id > 0
                    """,
                    Long.class);
            ids.addAll(fromTypes);
        } catch (Exception e) {
            log.debug("[tenant-table] 读取 entity_type 租户失败: {}", e.getMessage());
        }
        return ids;
    }
}
