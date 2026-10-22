package cn.cheers.x.module.dynamicbusiness.service.execution;

import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionUnfinishedOptionDTO;
import cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 协议监控的任务下拉：只查这台设备还没结束的执行账。
 * <p>设备编号写在执行账快照 {@code standard_snapshot.logicalDeviceId}。
 * 状态只认待执行和正在执行。
 * <p>只取出名称、状态、计划时刻、收流地址和模板编号。
 * 模板编号从指令包条目里抽出，不把指令包正文交给调用方。
 * <p>不负责：选定后的整本账、开跑时挑哪一条、已经结束的账。
 */
@Component
public class OpenExecutionAccountQuery {

    static final String RECORD_TYPE = TaskExecutionSessionServiceImpl.DEFAULT_RECORD_TYPE;

    private final JdbcTemplate jdbcTemplate;

    public OpenExecutionAccountQuery(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 这台设备上还没结束的执行账。设备编号空着时返回空列表，不扫全表。
     */
    public List<TaskExecutionUnfinishedOptionDTO> listUnfinishedForDevice(String deviceId) {
        if (!StringUtils.hasText(deviceId)) {
            return List.of();
        }
        String table = TenantPhysicalTableNames.entityPhysicalTable(RECORD_TYPE);
        return jdbcTemplate.query(sql(table), (rs, rowNum) -> {
            TaskExecutionUnfinishedOptionDTO row = new TaskExecutionUnfinishedOptionDTO();
            row.setExecutionRecordId(rs.getLong("id"));
            row.setRecordName(rs.getString("name"));
            row.setExecutionStatus(rs.getString("execution_status"));
            row.setPlannedStart(rs.getString("planned_start"));
            row.setMediaStreamUrl(rs.getString("media_stream_url"));
            row.setCommandTemplateId(rs.getString("command_template_id"));
            return row;
        }, deviceId.trim());
    }

    /**
     * 表名只接受租户物理表工具给出的名字。设备编号用参数，不拼进 SQL。
     */
    static String sql(String table) {
        return "SELECT id, name, "
                + "custom_fields->>'execution_status' AS execution_status, "
                + "custom_fields->>'planned_start' AS planned_start, "
                + "custom_fields->'standard_snapshot'->>'taskRecordIngestUrl' AS media_stream_url, "
                + "(SELECT piece->>'templateId' FROM jsonb_array_elements("
                + "CASE WHEN jsonb_typeof(custom_fields #> '{standard_snapshot,dispatchDownlinkPieces}') = 'array' "
                + "THEN custom_fields #> '{standard_snapshot,dispatchDownlinkPieces}' "
                + "ELSE '[]'::jsonb END) AS piece "
                + "WHERE lower(piece->>'kind') = 'package' "
                + "AND coalesce(piece->>'templateId', '') <> '' LIMIT 1) AS command_template_id "
                + "FROM " + table + " "
                + "WHERE deleted = false "
                + "AND custom_fields->>'execution_status' IN ('pending', 'in_progress') "
                + "AND custom_fields->'standard_snapshot'->>'logicalDeviceId' = ?";
    }
}
