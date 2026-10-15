package cn.cheers.x.module.dynamicbusiness.service.execution;

import cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 更新任务时，把还没开始的执行账从库里物理删掉。
 * <p>管什么：这条执行账、它的步骤、这些行发出或指向它们的实体关系、分类挂靠。
 * <p>不管什么：不打 deleted 标记。不管已经开始或已经执行的账。不管其它实体的日常删除。
 */
@Component
public class PendingExecutionPhysicalDeleter {

    static final String RECORD_TYPE = TaskExecutionSessionServiceImpl.DEFAULT_RECORD_TYPE;
    static final String STEP_TYPE = TaskExecutionSessionServiceImpl.STEP_ENTITY_TYPE;

    private final JdbcTemplate jdbcTemplate;

    public PendingExecutionPhysicalDeleter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 先删关系和分类挂靠，再删步骤行和执行账行。
     * 步骤编号为空时仍删掉这条执行账自己的关系行和账行。
     */
    public void delete(Long executionRecordId, List<Long> stepIds) {
        List<Long> steps = stepIds == null ? List.of() : List.copyOf(stepIds);
        String relationTable = TenantPhysicalTableNames.requirePhysical("dynamic_entity_relation");
        String categoryTable = TenantPhysicalTableNames.requirePhysical("dynamic_entity_category_relation");
        String stepTable = TenantPhysicalTableNames.entityPhysicalTable(STEP_TYPE);
        String recordTable = TenantPhysicalTableNames.entityPhysicalTable(RECORD_TYPE);

        deleteRelations(relationTable, executionRecordId, steps);
        deleteCategoryLinks(categoryTable, executionRecordId, steps);
        if (!steps.isEmpty()) {
            jdbcTemplate.update(deleteByIds(stepTable, steps.size()), steps.toArray());
        }
        jdbcTemplate.update("DELETE FROM " + recordTable + " WHERE id = ?", executionRecordId);
    }

    private void deleteRelations(String table, Long executionRecordId, List<Long> steps) {
        List<Object> args = new ArrayList<>();
        StringBuilder sql = new StringBuilder("DELETE FROM ").append(table).append(" WHERE ");
        sql.append("(source_entity_type_code = ? AND source_entity_id = ?)");
        args.add(RECORD_TYPE);
        args.add(executionRecordId);
        sql.append(" OR (target_entity_type_code = ? AND target_entity_id = ?)");
        args.add(RECORD_TYPE);
        args.add(executionRecordId);
        if (!steps.isEmpty()) {
            sql.append(" OR (source_entity_type_code = ? AND source_entity_id IN (")
                    .append(placeholders(steps.size())).append("))");
            args.add(STEP_TYPE);
            args.addAll(steps);
            sql.append(" OR (target_entity_type_code = ? AND target_entity_id IN (")
                    .append(placeholders(steps.size())).append("))");
            args.add(STEP_TYPE);
            args.addAll(steps);
        }
        jdbcTemplate.update(sql.toString(), args.toArray());
    }

    private void deleteCategoryLinks(String table, Long executionRecordId, List<Long> steps) {
        List<Object> args = new ArrayList<>();
        StringBuilder sql = new StringBuilder("DELETE FROM ").append(table).append(" WHERE ");
        sql.append("(entity_type_code = ? AND entity_id = ?)");
        args.add(RECORD_TYPE);
        args.add(executionRecordId);
        if (!steps.isEmpty()) {
            sql.append(" OR (entity_type_code = ? AND entity_id IN (")
                    .append(placeholders(steps.size())).append("))");
            args.add(STEP_TYPE);
            args.addAll(steps);
        }
        jdbcTemplate.update(sql.toString(), args.toArray());
    }

    private static String deleteByIds(String table, int count) {
        return "DELETE FROM " + table + " WHERE id IN (" + placeholders(count) + ")";
    }

    private static String placeholders(int count) {
        return String.join(",", java.util.Collections.nCopies(count, "?"));
    }
}
