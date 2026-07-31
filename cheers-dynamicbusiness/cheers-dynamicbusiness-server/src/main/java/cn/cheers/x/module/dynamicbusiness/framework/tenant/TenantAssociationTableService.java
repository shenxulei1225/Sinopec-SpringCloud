package cn.cheers.x.module.dynamicbusiness.framework.tenant;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 确保当前租户的关联物理表存在（{@code LIKE} 无后缀模板表）。
 */
@Service
@Slf4j
public class TenantAssociationTableService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Transactional(rollbackFor = Exception.class)
    public void ensureCurrentTenantAssociationTables() {
        Long tenantId = TenantPhysicalTableNames.requireTenantId();
        ensureAssociationTables(tenantId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void ensureAssociationTables(Long tenantId) {
        if (tenantId == null || tenantId < 0) {
            return;
        }
        for (String base : TenantPhysicalTableNames.ISOLATED_ASSOCIATION_BASE_TABLES) {
            ensureOne(base, tenantId);
        }
    }

    private void ensureOne(String base, Long tenantId) {
        String physical = TenantPhysicalTableNames.requirePhysical(base, tenantId);
        if (tableExists(physical)) {
            return;
        }
        if (!tableExists(base)) {
            log.warn("[tenant-table] 模板表 {} 不存在，无法为租户 {} 创建 {}", base, tenantId, physical);
            return;
        }
        String sql = String.format(
                "CREATE TABLE IF NOT EXISTS %s (LIKE %s INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES)",
                physical, base);
        jdbcTemplate.execute(sql);
        ensureIdSequence(physical);
        log.info("[tenant-table] 已创建关联表 {}", physical);
    }

    private void ensureIdSequence(String tableName) {
        String safe = tableName.toLowerCase();
        String seq = "dynamicbusiness." + safe + "_id_seq";
        try {
            Integer hasId = jdbcTemplate.queryForObject(
                    """
                    SELECT COUNT(*) FROM information_schema.columns
                    WHERE table_schema IN (current_schema(), 'dynamicbusiness')
                      AND table_name = ? AND column_name = 'id'
                    """,
                    Integer.class,
                    safe);
            if (hasId == null || hasId == 0) {
                return;
            }
            jdbcTemplate.execute("CREATE SEQUENCE IF NOT EXISTS " + seq);
            jdbcTemplate.execute(
                    "ALTER TABLE dynamicbusiness." + safe
                            + " ALTER COLUMN id SET DEFAULT nextval('" + seq + "'::regclass)");
            try {
                jdbcTemplate.execute("ALTER SEQUENCE " + seq + " OWNED BY dynamicbusiness." + safe + ".id");
            } catch (Exception ignored) {
                // ignore
            }
            jdbcTemplate.queryForObject(
                    "SELECT setval(?::regclass, COALESCE((SELECT MAX(id) FROM dynamicbusiness." + safe + "), 1),"
                            + " EXISTS (SELECT 1 FROM dynamicbusiness." + safe + "))",
                    Long.class,
                    seq);
        } catch (Exception e) {
            log.warn("[tenant-table] 校准序列失败 table={}: {}", safe, e.getMessage());
        }
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*) FROM information_schema.tables
                WHERE table_schema IN (current_schema(), 'dynamicbusiness')
                  AND table_name = ?
                """,
                Integer.class,
                tableName.toLowerCase());
        return count != null && count > 0;
    }
}
