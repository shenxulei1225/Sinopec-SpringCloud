package cn.cheers.x.module.dynamicbusiness.devicehistory;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 单台设备的工况、远程，以及能不能下发。
 * 也能标出这一批里哪些挂了工况读点、哪些能点开关。在线只看历史库里有没有采样。
 * 设备图层的分区树和设备名单走设备管理同一套分类查询，不在这里另查。
 * 工况和远程只认点位说明和历史里的读数。没有读数就空着。
 * 不订写点，不猜标签，不下发。
 */
@Service
public class IndustrialEquipmentBoard {

    private static final Pattern TABLE_NAME = Pattern.compile("^ent_[a-z0-9_]+$");

    private final JdbcTemplate jdbcTemplate;
    private final DevicePointHistoryService history;

    public IndustrialEquipmentBoard(JdbcTemplate jdbcTemplate, DevicePointHistoryService history) {
        this.jdbcTemplate = jdbcTemplate;
        this.history = history;
    }

    public record Command(String actionCode, String label) {
    }

    public record Status(String name, String remoteText, boolean canControl, List<String> conditions, List<Command> commands) {
    }

    /**
     * 挂了工况读点，或能点开关。
     * online 为 true 表示历史库里已经有过采样，false 表示还没有。
     * null 表示历史库没配好，界面不要画在线或不在线。
     */
    public record Capability(long id, boolean monitorable, boolean controllable, Boolean online) {
    }

    /** 这台设备的工况、远程，以及可以点的动作。历史没有时不编一个状态。 */
    public Status status(long tenantId, long equipmentId) {
        Device device = device(tenantId, equipmentId);
        if (device == null) {
            throw new ServiceException(404, "没有这台设备");
        }
        List<Command> commands = commands(device, writeFieldsOf(tenantId, equipmentId), actions(tenantId));
        List<ReadPoint> points = readPoints(tenantId, equipmentId);
        Readings readings = latestValues(tenantId, equipmentId);
        if (readings.gap() != null) {
            return new Status(device.name(), readings.gap(), false, List.of(), commands);
        }
        return assemble(device.name(), points, valuesOf(readings), commands);
    }

    /**
     * 这一批设备里，哪些挂了工况读点，哪些能点开关。
     * 工况不含名为「远程」「远控」的读点。能点开关沿用单台状态里的动作匹配。
     * 在线看历史库有没有采样。历史库没配好时都不算在线，不编一个在线。
     * 不决定现在能不能下发。
     */
    public List<Capability> capabilities(long tenantId, List<Long> equipmentIds) {
        List<Long> ids = equipmentIds == null ? List.of() : equipmentIds.stream()
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();
        if (ids.isEmpty()) {
            return List.of();
        }
        String equipmentTable = table(tenantId, "equipment");
        String pointTable = table(tenantId, "point_binding");
        if (equipmentTable == null || pointTable == null) {
            return List.of();
        }
        Set<Long> monitorable = monitorableIds(pointTable, equipmentTable, tenantId, ids);
        Map<Long, String> models = modelCodes(equipmentTable, tenantId, ids);
        Map<Long, Set<String>> writes = writeFieldsOf(pointTable, equipmentTable, tenantId, ids);
        List<ActionRecipe> actions = actions(tenantId);
        List<Long> watched = new ArrayList<>();
        List<boolean[]> flags = new ArrayList<>();
        for (Long id : ids) {
            boolean canMonitor = monitorable.contains(id);
            String modelCode = models.get(id);
            boolean canControl = modelCode != null
                    && !commands(new Device(id, "", modelCode, null), writes.getOrDefault(id, Set.of()), actions).isEmpty();
            if (canMonitor || canControl) {
                watched.add(id);
                flags.add(new boolean[] { canMonitor, canControl });
            }
        }
        Set<Long> online = history.devicesWithSamples(tenantId, watched);
        List<Capability> rows = new ArrayList<>();
        for (int index = 0; index < watched.size(); index++) {
            Long id = watched.get(index);
            boolean[] flag = flags.get(index);
            Boolean presence = online == null ? null : online.contains(id);
            rows.add(new Capability(id, flag[0], flag[1], presence));
        }
        return rows;
    }

    /** 下发前再看一次远程。不在远程，或还没有读数，就拒绝。 */
    public void requireControl(long tenantId, long equipmentId) {
        Status status = status(tenantId, equipmentId);
        if (!status.canControl()) {
            throw new ServiceException(400, status.remoteText());
        }
    }

    private Status assemble(String name, List<ReadPoint> points, Map<String, Double> values, List<Command> commands) {
        String remoteText = "没有远程状态";
        Boolean allows = null;
        List<String> conditions = new ArrayList<>();
        for (ReadPoint point : points) {
            Double value = values.get(point.fieldCode());
            if (isRemote(point.name())) {
                remoteText = remoteText(point, value);
                allows = allows(point, value);
                continue;
            }
            conditions.add(point.name() + "：" + conditionText(point, value));
        }
        boolean canControl = Boolean.TRUE.equals(allows) && !commands.isEmpty();
        return new Status(name, remoteText, canControl, List.copyOf(conditions), List.copyOf(commands));
    }

    private static boolean isRemote(String name) {
        return "远程".equals(name) || "远控".equals(name);
    }

    private static String remoteText(ReadPoint point, Double value) {
        if (value == null) {
            return "还没有远程状态";
        }
        IndustrialPointReading.Line line = IndustrialPointReading.read(point.description(), value);
        if (line == null) {
            return "远程读数不能判断";
        }
        return line.text();
    }

    private static Boolean allows(ReadPoint point, Double value) {
        if (value == null) {
            return null;
        }
        IndustrialPointReading.Line line = IndustrialPointReading.read(point.description(), value);
        return line == null ? null : line.allowsControl();
    }

    private static String conditionText(ReadPoint point, Double value) {
        if (value == null) {
            return "还没有读数";
        }
        IndustrialPointReading.Line line = IndustrialPointReading.read(point.description(), value);
        return line == null ? "读数不能判断" : line.text();
    }

    private Readings latestValues(long tenantId, long equipmentId) {
        try {
            Map<String, Double> values = new HashMap<>();
            for (DevicePointSample sample : history.latest(tenantId, equipmentId)) {
                values.put(sample.getPointCode(), sample.getEngineeringValue());
            }
            return new Readings(values, null);
        } catch (ServiceException ex) {
            if ("历史库未配置".equals(ex.getMessage())) {
                return new Readings(Map.of(), ex.getMessage());
            }
            throw ex;
        }
    }

    private static Map<String, Double> valuesOf(Readings readings) {
        return readings.values();
    }

    private List<Command> commands(Device device, Set<String> writeFields, List<ActionRecipe> actions) {
        List<Command> commands = new ArrayList<>();
        for (ActionRecipe action : actions) {
            if (matches(device.modelCode(), writeFields, action.recipe())) {
                commands.add(new Command(action.code(), action.name()));
            }
        }
        return commands;
    }

    private static boolean matches(String modelCode, Set<String> writeFields, IndustrialWriteRecipe recipe) {
        if (recipe == null || recipe.writes().isEmpty() || !recipe.appliesTo(modelCode)) {
            return false;
        }
        for (IndustrialWriteRecipe.Write write : recipe.writes()) {
            if (!writeFields.contains(write.fieldCode())) {
                return false;
            }
        }
        return true;
    }

    private Device device(long tenantId, long equipmentId) {
        String equipmentTable = table(tenantId, "equipment");
        if (equipmentTable == null) {
            return null;
        }
        List<Device> rows = jdbcTemplate.query("""
                SELECT equipment.id, equipment.name, model.code
                FROM %s equipment
                JOIN dynamic_model model ON model.id = equipment.model_id AND model.deleted = false
                WHERE equipment.tenant_id = ? AND equipment.id = ? AND equipment.deleted = false
                """.formatted(equipmentTable), (rs, row) -> new Device(
                rs.getLong("id"), rs.getString("name"), rs.getString("code"), null), tenantId, equipmentId);
        return rows.size() == 1 ? rows.get(0) : null;
    }

    private Set<Long> monitorableIds(String pointTable, String equipmentTable, long tenantId, List<Long> ids) {
        Set<Long> monitorable = new HashSet<>();
        for (List<Long> chunk : chunks(ids)) {
            List<Object> args = chunkArgs(tenantId, chunk);
            jdbcTemplate.query("""
                    SELECT binding.equipment_id, field.name
                    FROM %s binding
                    JOIN dynamic_field field
                      ON field.code = binding.point_field_code AND field.tenant_id = binding.tenant_id
                     AND field.deleted = false
                    JOIN %s equipment
                      ON equipment.id = binding.equipment_id AND equipment.deleted = false
                    WHERE binding.tenant_id = ? AND binding.deleted = false
                      AND btrim(binding.read_write) = 'Read'
                      AND binding.equipment_id IN (%s)
                    """.formatted(pointTable, equipmentTable, placeholders(chunk.size())),
                    (rs, row) -> {
                        if (!isRemote(rs.getString("name"))) {
                            monitorable.add(rs.getLong("equipment_id"));
                        }
                        return null;
                    }, args.toArray());
        }
        return monitorable;
    }

    private Map<Long, String> modelCodes(String equipmentTable, long tenantId, List<Long> ids) {
        Map<Long, String> models = new HashMap<>();
        for (List<Long> chunk : chunks(ids)) {
            List<Object> args = chunkArgs(tenantId, chunk);
            jdbcTemplate.query("""
                    SELECT equipment.id, model.code
                    FROM %s equipment
                    JOIN dynamic_model model ON model.id = equipment.model_id AND model.deleted = false
                    WHERE equipment.tenant_id = ? AND equipment.deleted = false
                      AND equipment.id IN (%s)
                    """.formatted(equipmentTable, placeholders(chunk.size())),
                    (rs, row) -> {
                        models.put(rs.getLong("id"), rs.getString("code"));
                        return null;
                    }, args.toArray());
        }
        return models;
    }

    private Map<Long, Set<String>> writeFieldsOf(String pointTable, String equipmentTable, long tenantId, List<Long> ids) {
        Map<Long, Set<String>> fields = new HashMap<>();
        for (List<Long> chunk : chunks(ids)) {
            List<Object> args = chunkArgs(tenantId, chunk);
            jdbcTemplate.query("""
                    SELECT binding.equipment_id, binding.point_field_code
                    FROM %s binding
                    JOIN %s equipment
                      ON equipment.id = binding.equipment_id AND equipment.deleted = false
                    WHERE binding.tenant_id = ? AND binding.deleted = false
                      AND btrim(binding.read_write) = 'Write' AND binding.point_field_code IS NOT NULL
                      AND binding.equipment_id IN (%s)
                    """.formatted(pointTable, equipmentTable, placeholders(chunk.size())),
                    (rs, row) -> {
                        fields.computeIfAbsent(rs.getLong("equipment_id"), key -> new HashSet<>())
                                .add(rs.getString("point_field_code"));
                        return null;
                    }, args.toArray());
        }
        return fields;
    }

    private static List<List<Long>> chunks(List<Long> ids) {
        List<List<Long>> chunks = new ArrayList<>();
        for (int start = 0; start < ids.size(); start += 400) {
            chunks.add(ids.subList(start, Math.min(start + 400, ids.size())));
        }
        return chunks;
    }

    private static List<Object> chunkArgs(long tenantId, List<Long> chunk) {
        List<Object> args = new ArrayList<>();
        args.add(tenantId);
        args.addAll(chunk);
        return args;
    }

    private static String placeholders(int count) {
        return "?,".repeat(count).substring(0, count * 2 - 1);
    }

    private Set<String> writeFieldsOf(long tenantId, long equipmentId) {
        String pointTable = table(tenantId, "point_binding");
        if (pointTable == null) {
            return Set.of();
        }
        return new HashSet<>(jdbcTemplate.query("""
                SELECT point_field_code
                FROM %s
                WHERE tenant_id = ? AND equipment_id = ? AND deleted = false
                  AND btrim(read_write) = 'Write' AND point_field_code IS NOT NULL
                """.formatted(pointTable), (rs, row) -> rs.getString(1), tenantId, equipmentId));
    }

    private List<ReadPoint> readPoints(long tenantId, long equipmentId) {
        String pointTable = table(tenantId, "point_binding");
        if (pointTable == null) {
            return List.of();
        }
        return jdbcTemplate.query("""
                SELECT binding.point_field_code, field.name, field.description
                FROM %s binding
                JOIN dynamic_field field
                  ON field.code = binding.point_field_code AND field.tenant_id = binding.tenant_id
                 AND field.deleted = false
                WHERE binding.tenant_id = ? AND binding.equipment_id = ? AND binding.deleted = false
                  AND btrim(binding.read_write) = 'Read'
                ORDER BY field.name
                """.formatted(pointTable), (rs, row) -> new ReadPoint(
                rs.getString("point_field_code"), rs.getString("name"), rs.getString("description")),
                tenantId, equipmentId);
    }

    private List<ActionRecipe> actions(long tenantId) {
        String actionTable = table(tenantId, "action");
        if (actionTable == null) {
            return List.of();
        }
        return jdbcTemplate.query("""
                SELECT code, name, attrs::text AS attrs
                FROM %s
                WHERE tenant_id = ? AND deleted = false AND action_domain = 'DEVICE'
                """.formatted(actionTable), (rs, row) -> {
            IndustrialWriteRecipe recipe = IndustrialWriteRecipe.parse(rs.getString("attrs"));
            if (recipe == null) {
                return null;
            }
            return new ActionRecipe(rs.getString("code"), rs.getString("name"), recipe);
        }, tenantId).stream().filter(action -> action != null).toList();
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

    private record Device(long id, String name, String modelCode, String cabinName) {
    }

    private record ReadPoint(String fieldCode, String name, String description) {
    }

    private record ActionRecipe(String code, String name, IndustrialWriteRecipe recipe) {
    }

    private record Readings(Map<String, Double> values, String gap) {
    }
}
