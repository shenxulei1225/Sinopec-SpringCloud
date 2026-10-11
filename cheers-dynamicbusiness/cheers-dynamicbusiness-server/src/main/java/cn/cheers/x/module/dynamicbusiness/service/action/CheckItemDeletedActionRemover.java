package cn.cheers.x.module.dynamicbusiness.service.action;

import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * 删除动作时，从检查项检查方法里去掉引用这一动作的步骤。
 *
 * <p>负责：检查项权威列 {@code step_tree_json}，以及旧列 {@code action_tree_json}。
 * <p>不负责：任务上已经生成的步骤图。不把这一步改写成别的动作。
 * <p>禁止：删不掉就在生成任务时猜一个替代动作。
 */
@Component
public class CheckItemDeletedActionRemover {

    private static final String ITEM_TYPE = "inspection_item";
    private static final ObjectMapper JSON = new ObjectMapper();

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private EntityTableNameHandler entityTableNameHandler;

    /**
     * 检查项各巡检方式的步骤里，动作编号等于这一条的，从已保存的检查方法中去掉。
     * 没有引用则不写。
     */
    public void remove(String actionCode) {
        if (!StringUtils.hasText(actionCode)) {
            return;
        }
        String code = actionCode.trim();
        String table = entityTableNameHandler.resolvePhysicalTableName(ITEM_TYPE);
        if (!table.matches("[a-z][a-z0-9_]*")) {
            return;
        }
        List<ItemRow> rows = jdbcTemplate.query(
                "SELECT id, step_tree_json::text, custom_fields::text FROM " + table
                        + " WHERE deleted = false AND ("
                        + " position(? in coalesce(step_tree_json::text, '')) > 0"
                        + " OR position(? in coalesce(custom_fields::text, '')) > 0)",
                (rs, rowNum) -> new ItemRow(rs.getLong("id"), rs.getString(2), rs.getString(3)),
                code,
                code);
        Set<String> codes = Set.of(code);
        for (ItemRow row : rows) {
            String nextStep = strip(row.stepTreeText, codes);
            String nextCustom = strip(row.customFieldsText, codes);
            if (same(row.stepTreeText, nextStep) && same(row.customFieldsText, nextCustom)) {
                continue;
            }
            jdbcTemplate.update(
                    "UPDATE " + table
                            + " SET step_tree_json = ?::jsonb, custom_fields = ?::jsonb,"
                            + " update_time = CURRENT_TIMESTAMP WHERE id = ?",
                    nextStep,
                    nextCustom,
                    row.id);
        }
    }

    private static String strip(String raw, Set<String> codes) {
        if (!StringUtils.hasText(raw)) {
            return raw;
        }
        Object next = ActionDeletedChildQueryFilter.omitValue(raw, codes);
        return next instanceof String text ? text : raw;
    }

    private static boolean same(String before, String after) {
        if (before == null || after == null) {
            return before == after;
        }
        if (before.equals(after)) {
            return true;
        }
        try {
            JsonNode left = JSON.readTree(before);
            JsonNode right = JSON.readTree(after);
            return left.equals(right);
        } catch (Exception ex) {
            return false;
        }
    }

    private record ItemRow(long id, String stepTreeText, String customFieldsText) {
    }
}
