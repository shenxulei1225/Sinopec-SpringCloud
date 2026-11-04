package cn.cheers.x.module.dynamicbusiness.devicehistory;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 读型号上标成采集点的字段。
 * 不管采样。采样在历史库，点的编号是字段编号。
 * 禁止按设备名称猜一个点，也禁止把台账字段当成采集点。
 */
@Service
public class DeviceCollectionPointQuery {

    private static final int BAD_REQUEST = 400;
    private static final String EQUIPMENT = "equipment";
    private static final Pattern TABLE_NAME = Pattern.compile("^[a-z][a-z0-9_]*$");

    private final JdbcTemplate jdbcTemplate;
    private final EntityTableNameHandler tableNames;

    public DeviceCollectionPointQuery(JdbcTemplate jdbcTemplate, EntityTableNameHandler tableNames) {
        this.jdbcTemplate = jdbcTemplate;
        this.tableNames = tableNames;
    }

    /**
     * 型号上标了采集点的设备。验证页按名称列出这些设备。没有采集点的设备不出现。
     * 不补设备，也不按名称猜哪一台。
     */
    public List<CollectionDevice> listDevices(long tenantId) {
        String equipment = tableNames.resolvePhysicalTableName(EQUIPMENT);
        if (!TABLE_NAME.matcher(equipment).matches()) {
            throw new ServiceException(BAD_REQUEST, "设备表名不可用");
        }
        return jdbcTemplate.query("""
                SELECT e.id, e.name, COALESCE(m.name, '') AS model_name
                FROM %s e
                LEFT JOIN dynamicbusiness.dynamic_model m
                  ON m.id = e.model_id AND m.deleted = false
                WHERE e.tenant_id = ? AND e.deleted = false
                  AND EXISTS (
                    SELECT 1
                    FROM dynamicbusiness.dynamic_model_field_assignment a
                    WHERE a.model_id = e.model_id
                      AND a.tenant_id = e.tenant_id
                      AND a.deleted = false
                      AND a.collection_point = true
                  )
                ORDER BY e.name, e.id
                """.formatted(equipment), (rs, rowNum) -> new CollectionDevice(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("model_name")), tenantId);
    }

    /** 一台能上报采集读数的设备。名称给人看，编号给后续查询用。 */
    public record CollectionDevice(long deviceId, String deviceName, String modelName) {
    }

    /**
     * 这台设备所属型号上的采集点。没有采集点就返回空列表。
     */
    public List<DeviceCollectionPoint> list(long tenantId, long deviceId) {
        long modelId = requireModelId(tenantId, deviceId);
        return listOnModel(tenantId, deviceId, modelId);
    }

    /**
     * 写入采样前核对这个点属于这台设备的型号。对不上就拒绝。
     */
    public DeviceCollectionPoint require(long tenantId, long deviceId, String pointCode) {
        if (pointCode == null || pointCode.isBlank()) {
            throw new ServiceException(BAD_REQUEST, "没有点");
        }
        long modelId = requireModelId(tenantId, deviceId);
        List<DeviceCollectionPoint> matched = jdbcTemplate.query("""
                SELECT a.field_code, COALESCE(NULLIF(btrim(a.alias), ''), f.name) AS point_name, f.unit
                FROM dynamicbusiness.dynamic_model_field_assignment a
                JOIN dynamicbusiness.dynamic_field f
                  ON f.id = a.field_id AND f.deleted = false
                WHERE a.tenant_id = ? AND a.model_id = ? AND a.field_code = ?
                  AND a.deleted = false AND a.collection_point = true
                """, (rs, rowNum) -> new DeviceCollectionPoint(
                deviceId,
                rs.getString("field_code"),
                rs.getString("point_name"),
                rs.getString("unit")), tenantId, modelId, pointCode);
        if (matched.isEmpty()) {
            throw new ServiceException(BAD_REQUEST, "这台设备没有这个点");
        }
        return matched.get(0);
    }

    /**
     * 各型号上、读写方式为读的布尔采集点。
     * 网关把布尔量记成 1 或 0。策略判断用这个工程值。
     * 说明只用来生成给人看的那一句。写点不在这里。没有这种点时返回空列表。
     */
    public List<BooleanReadPoint> listBooleanReadPoints(long tenantId) {
        String equipment = tableNames.resolvePhysicalTableName(EQUIPMENT);
        String binding = tableNames.resolvePhysicalTableName("point_binding");
        if (!TABLE_NAME.matcher(equipment).matches() || !TABLE_NAME.matcher(binding).matches()) {
            throw new ServiceException(BAD_REQUEST, "设备或点位表名不可用");
        }
        return jdbcTemplate.query("""
                SELECT DISTINCT m.id AS model_id, m.name AS model_name, f.code AS field_code,
                       f.name AS field_name, f.description AS description
                FROM dynamicbusiness.dynamic_model_field_assignment a
                JOIN dynamicbusiness.dynamic_field f
                  ON f.id = a.field_id AND f.deleted = false
                JOIN dynamicbusiness.dynamic_model m
                  ON m.id = a.model_id AND m.deleted = false
                JOIN %s e
                  ON e.model_id = m.id AND e.deleted = false AND e.tenant_id = a.tenant_id
                JOIN %s b
                  ON b.equipment_id = e.id AND b.deleted = false
                 AND b.point_field_code = f.code AND btrim(b.read_write) = 'Read'
                WHERE a.tenant_id = ? AND a.deleted = false AND a.collection_point = true
                  AND upper(f.type) = 'BOOLEAN'
                ORDER BY m.name, f.name
                """.formatted(equipment, binding), (rs, rowNum) -> new BooleanReadPoint(
                rs.getLong("model_id"),
                rs.getString("model_name"),
                rs.getString("field_code"),
                rs.getString("field_name"),
                rs.getString("description")), tenantId);
    }

    /** 一个型号上的读方向布尔采集点。工程值只有 0 和 1。说明是给人看的那一句。 */
    public record BooleanReadPoint(
            long modelId, String modelName, String fieldCode, String fieldName, String description) {
    }

    private List<DeviceCollectionPoint> listOnModel(long tenantId, long deviceId, long modelId) {
        return jdbcTemplate.query("""
                SELECT a.field_code, COALESCE(NULLIF(btrim(a.alias), ''), f.name) AS point_name, f.unit
                FROM dynamicbusiness.dynamic_model_field_assignment a
                JOIN dynamicbusiness.dynamic_field f
                  ON f.id = a.field_id AND f.deleted = false
                WHERE a.tenant_id = ? AND a.model_id = ?
                  AND a.deleted = false AND a.collection_point = true
                ORDER BY a.sort, a.field_code
                """, (rs, rowNum) -> new DeviceCollectionPoint(
                deviceId,
                rs.getString("field_code"),
                rs.getString("point_name"),
                rs.getString("unit")), tenantId, modelId);
    }

    private long requireModelId(long tenantId, long deviceId) {
        String table = tableNames.resolvePhysicalTableName(EQUIPMENT);
        if (!TABLE_NAME.matcher(table).matches()) {
            throw new ServiceException(BAD_REQUEST, "设备表名不可用");
        }
        try {
            Long modelId = jdbcTemplate.queryForObject(
                    "SELECT model_id FROM " + table + " WHERE id = ? AND tenant_id = ? AND deleted = false",
                    Long.class, deviceId, tenantId);
            if (modelId == null) {
                throw new ServiceException(BAD_REQUEST, "这台设备没有型号");
            }
            return modelId;
        } catch (EmptyResultDataAccessException ex) {
            throw new ServiceException(BAD_REQUEST, "没有这台设备");
        }
    }
}
