package cn.cheers.x.module.dynamicbusiness.devicehistory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 读各租户点位对照里的读点和写点。
 * 表名从数据目录的专用表来。不写采样，不猜表名。
 * 读点给订阅。写点只给同一条会话记下节点，禁止放进订阅。
 */
@Service
public class GatewayPointCatalog {

    private static final Logger log = LoggerFactory.getLogger(GatewayPointCatalog.class);
    private static final Pattern TABLE_NAME = Pattern.compile("^ent_[a-z0-9_]+$");

    private final JdbcTemplate jdbcTemplate;
    private final GatewaySubscriptionProperties properties;

    public GatewayPointCatalog(JdbcTemplate jdbcTemplate, GatewaySubscriptionProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
    }

    /** 这一台 Gateway 上、读写方式为读的行。没有目录时返回空列表。 */
    public List<GatewayPointRef> loadReadPoints() {
        return load("Read");
    }

    /** 这一台 Gateway 上、读写方式为写的行。调用方不得拿去建订阅。 */
    public List<GatewayPointRef> loadWritePoints() {
        return load("Write");
    }

    private List<GatewayPointRef> load(String readWrite) {
        List<GatewayPointRef> rows = new ArrayList<>();
        for (CatalogTable table : tables()) {
            rows.addAll(readRows(table, readWrite));
        }
        return List.copyOf(rows);
    }

    private List<CatalogTable> tables() {
        String sql = """
                SELECT tenant_id, dedicated_table_name
                FROM dynamic_entity_type
                WHERE code = 'point_binding' AND deleted = false
                  AND dedicated_table_name IS NOT NULL
                """;
        List<Long> tenants = properties.getTenantIds();
        if (!tenants.isEmpty()) {
            sql = sql + " AND tenant_id IN (" + tenantList(tenants) + ")";
        }
        List<CatalogTable> found = new ArrayList<>();
        for (CatalogTable table : jdbcTemplate.query(sql, (rs, row) ->
                new CatalogTable(rs.getLong("tenant_id"), rs.getString("dedicated_table_name")))) {
            if (!TABLE_NAME.matcher(table.name()).matches()) {
                log.error("点位对照表名不能用，这一份不订。租户 {}", table.tenantId());
                continue;
            }
            found.add(table);
        }
        if (found.isEmpty()) {
            log.error("没有可用的点位对照，订不上 Gateway");
        }
        return found;
    }

    private static String tenantList(List<Long> tenants) {
        StringBuilder ids = new StringBuilder();
        for (Long tenantId : tenants) {
            if (tenantId == null) {
                continue;
            }
            if (!ids.isEmpty()) {
                ids.append(',');
            }
            ids.append(tenantId);
        }
        return ids.isEmpty() ? "-1" : ids.toString();
    }

    private List<GatewayPointRef> readRows(CatalogTable table, String readWrite) {
        String sql = """
                SELECT equipment_id, point_field_code, code, zone_code, tag_name, zone_id
                FROM %s
                WHERE tenant_id = ? AND deleted = false AND btrim(read_write) = ?
                  AND equipment_id IS NOT NULL AND point_field_code IS NOT NULL
                  AND zone_code IS NOT NULL AND tag_name IS NOT NULL AND code IS NOT NULL
                """.formatted(table.name());
        return jdbcTemplate.query(sql, (rs, row) -> {
            long zone = rs.getLong("zone_id");
            Long zoneId = rs.wasNull() ? null : zone;
            return new GatewayPointRef(
                    table.tenantId(),
                    rs.getLong("equipment_id"),
                    rs.getString("point_field_code"),
                    rs.getString("code"),
                    rs.getString("zone_code"),
                    rs.getString("tag_name"),
                    zoneId);
        }, table.tenantId(), readWrite);
    }

    private record CatalogTable(long tenantId, String name) {
    }
}
