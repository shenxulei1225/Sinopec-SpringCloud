package cn.cheers.x.module.dynamicbusiness.devicehistory;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 按租户读取动作、设备和点位对照里的写点。
 * 表名只认数据目录上的专用表。缺目录就拒绝，不猜表。
 * 不写采样，也不下发。
 */
@Service
public class IndustrialPointBooks {

    private static final Pattern TABLE_NAME = Pattern.compile("^ent_[a-z0-9_]+$");

    private final JdbcTemplate jdbcTemplate;

    public IndustrialPointBooks(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record ActionRow(String domain, String attrsJson) {
    }

    /** 没有这个动作时返回 null。 */
    public ActionRow action(long tenantId, String actionCode) {
        String table = table(tenantId, "action");
        if (table == null) {
            return null;
        }
        List<ActionRow> rows = jdbcTemplate.query("""
                SELECT action_domain, attrs::text AS attrs
                FROM %s
                WHERE tenant_id = ? AND code = ? AND deleted = false
                """.formatted(table), (rs, row) -> new ActionRow(rs.getString("action_domain"), rs.getString("attrs")),
                tenantId, actionCode);
        return rows.size() == 1 ? rows.get(0) : null;
    }

    /** 没有这台设备时返回 null。 */
    public String equipmentModelCode(long tenantId, long equipmentId) {
        String table = table(tenantId, "equipment");
        if (table == null) {
            return null;
        }
        List<String> codes = jdbcTemplate.query("""
                SELECT model.code
                FROM %s equipment
                JOIN dynamic_model model ON model.id = equipment.model_id AND model.deleted = false
                WHERE equipment.id = ? AND equipment.tenant_id = ? AND equipment.deleted = false
                """.formatted(table), (rs, row) -> rs.getString("code"), equipmentId, tenantId);
        return codes.size() == 1 ? codes.get(0) : null;
    }

    /** 这台设备的写点。这个租户没有点位对照时拒绝。 */
    public List<IndustrialWritePlan.BoundWrite> writeRows(long tenantId, long equipmentId) {
        String table = table(tenantId, "point_binding");
        if (table == null) {
            throw new ServiceException(400, "这个租户还没有点位对照");
        }
        return jdbcTemplate.query("""
                SELECT point_field_code, code
                FROM %s
                WHERE tenant_id = ? AND equipment_id = ? AND deleted = false
                  AND btrim(read_write) = 'Write'
                  AND point_field_code IS NOT NULL AND code IS NOT NULL
                """.formatted(table), (rs, row) -> new IndustrialWritePlan.BoundWrite(
                rs.getString("point_field_code"), rs.getString("code")), tenantId, equipmentId);
    }

    private String table(long tenantId, String code) {
        List<String> names = jdbcTemplate.query("""
                SELECT dedicated_table_name
                FROM dynamic_entity_type
                WHERE tenant_id = ? AND code = ? AND deleted = false
                  AND dedicated_table_name IS NOT NULL
                """, (rs, row) -> rs.getString(1), tenantId, code);
        if (names.size() != 1 || !TABLE_NAME.matcher(names.get(0)).matches()) {
            return null;
        }
        return names.get(0);
    }
}
