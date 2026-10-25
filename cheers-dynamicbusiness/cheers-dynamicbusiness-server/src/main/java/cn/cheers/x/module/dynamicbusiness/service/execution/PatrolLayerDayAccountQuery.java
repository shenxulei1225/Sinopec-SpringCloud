package cn.cheers.x.module.dynamicbusiness.service.execution;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionDayAccountDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionDayAccountPageDTO;
import cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 巡检图层侧栏：当前站场、所选日期的执行账。
 *
 * <p>管什么：按账上已经写下的计划时刻收这一天。时刻原文是北京时间，例如
 * {@code 2026-10-24T09:00:00+08:00}。只取出名称、任务、计划时刻和执行状态。</p>
 * <p>不管什么：不写执行账，不读步骤树，不走通用实体场景查询。</p>
 * <p>禁止：缺站时扫全表；把这一天的名单做成数据目录的通用列表。</p>
 */
@Component
public class PatrolLayerDayAccountQuery {

    static final String RECORD_TYPE = TaskExecutionSessionServiceImpl.DEFAULT_RECORD_TYPE;
    static final int MAX_PAGE_SIZE = 100;

    private final JdbcTemplate jdbcTemplate;

    public PatrolLayerDayAccountQuery(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 所选日期的执行账。日期是侧栏选中的那一天。含当天零点，不含下一天零点。
     * 没有计划时刻的不在这一页里。
     */
    public TaskExecutionDayAccountPageDTO page(long facilityId, String day, int pageNo, int pageSize) {
        if (facilityId <= 0) {
            throw new ServiceException(400, "站场级数据须指定所属场站，请先选择要管理的场站");
        }
        if (pageNo < 1 || pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
            throw new ServiceException(400, "当天巡检账分页超出范围");
        }
        DayRange range = beijingDayRange(day);
        String table = TenantPhysicalTableNames.entityPhysicalTable(RECORD_TYPE);
        Long total = jdbcTemplate.queryForObject(
                countSql(table), Long.class, facilityId, range.inclusiveStart(), range.exclusiveEnd());
        List<TaskExecutionDayAccountDTO> rows = jdbcTemplate.query(
                selectSql(table),
                (rs, rowNum) -> {
                    TaskExecutionDayAccountDTO row = new TaskExecutionDayAccountDTO();
                    row.setId(rs.getLong("id"));
                    row.setName(rs.getString("name"));
                    row.setTaskId(rs.getString("task_id"));
                    row.setPlannedStart(rs.getString("planned_start"));
                    row.setExecutionStatus(rs.getString("execution_status"));
                    return row;
                },
                facilityId,
                range.inclusiveStart(),
                range.exclusiveEnd(),
                pageSize,
                (long) (pageNo - 1) * pageSize);
        TaskExecutionDayAccountPageDTO page = new TaskExecutionDayAccountPageDTO();
        page.setList(rows);
        page.setTotal(total == null ? 0L : total);
        return page;
    }

    /**
     * 所选日期在北京时间上的起止。与账上 {@code yyyy-MM-dd'T'HH:mm:ss+08:00} 的文字顺序一致。
     */
    public static DayRange beijingDayRange(String day) {
        if (day == null || !day.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new ServiceException(400, "日期须为所选的那一天");
        }
        final LocalDate date;
        try {
            date = LocalDate.parse(day);
        } catch (DateTimeParseException ex) {
            throw new ServiceException(400, "日期须为所选的那一天");
        }
        return new DayRange(date + "T00:00:00+08:00", date.plusDays(1) + "T00:00:00+08:00");
    }

    /**
     * 这条已写下的计划时刻是否落在当天起止里。没有时刻的不算。
     * 比较方式和库里的文字比较相同。
     */
    public static boolean includesStoredClock(DayRange range, String storedPlannedStart) {
        if (storedPlannedStart == null || storedPlannedStart.isBlank()) {
            return false;
        }
        return storedPlannedStart.compareTo(range.inclusiveStart()) >= 0
                && storedPlannedStart.compareTo(range.exclusiveEnd()) < 0;
    }

    /** 表名只接受租户物理表工具给出的名字。站场和起止用参数，不拼进 SQL。 */
    static String selectSql(String table) {
        return "SELECT id, name, "
                + "custom_fields->>'task_id' AS task_id, "
                + "custom_fields->>'planned_start' AS planned_start, "
                + "custom_fields->>'execution_status' AS execution_status "
                + "FROM " + table + " "
                + whereSql()
                + "ORDER BY custom_fields->>'planned_start' ASC, id ASC "
                + "LIMIT ? OFFSET ?";
    }

    static String countSql(String table) {
        return "SELECT COUNT(1) FROM " + table + " " + whereSql();
    }

    private static String whereSql() {
        return "WHERE deleted = false "
                + "AND facility_id = ? "
                + "AND custom_fields->>'planned_start' >= ? "
                + "AND custom_fields->>'planned_start' < ? "
                + "AND custom_fields->>'execution_status' IN "
                + "('pending', 'in_progress', 'completed', 'fault', 'failed') ";
    }

    /** 北京时间当天起止。含起始，不含结束。 */
    public record DayRange(String inclusiveStart, String exclusiveEnd) {
    }
}
