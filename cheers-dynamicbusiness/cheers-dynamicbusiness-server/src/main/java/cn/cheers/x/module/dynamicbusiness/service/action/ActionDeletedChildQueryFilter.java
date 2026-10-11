package cn.cheers.x.module.dynamicbusiness.service.action;

import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 查询动作结构时，子动作那一行已经标成删除的，不放进这次结果。
 *
 * <p>负责：动作详情和动作列表里的正式动作组成员，以及检查项步骤树中的动作引用。</p>
 * <p>不负责：删除动作时去改父动作；不把查询结果写回已保存的父动作。</p>
 */
@Component
public class ActionDeletedChildQueryFilter {

    private static final ObjectMapper JSON = new ObjectMapper();

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private EntityTableNameHandler entityTableNameHandler;

    /**
     * 当前动作表里已删除行的编码。查不到表时返回空集，调用方就按原结果返回。
     */
    public Set<String> deletedCodes() {
        String table = entityTableNameHandler.resolvePhysicalTableName(ActionFieldCodes.ENTITY_TYPE_CODE);
        if (!table.matches("[a-z][a-z0-9_]*")) {
            return Set.of();
        }
        List<String> codes = jdbcTemplate.queryForList(
                "SELECT code FROM " + table + " WHERE deleted = true AND code IS NOT NULL",
                String.class);
        Set<String> deleted = new LinkedHashSet<>();
        for (String code : codes) {
            if (code != null && !code.isBlank()) {
                deleted.add(code.trim());
            }
        }
        return Set.copyOf(deleted);
    }

    /**
     * 详情查询返回前，从步骤树里去掉已删除动作。
     */
    public void omitFromBaseFields(Map<String, Object> baseFields, Set<String> deletedCodes) {
        if (baseFields == null || deletedCodes == null || deletedCodes.isEmpty()) {
            return;
        }
        omitKey(baseFields, ActionFieldCodes.ACTION_TREE_JSON, deletedCodes);
    }

    static Object omitValue(Object raw, Set<String> deletedCodes) {
        if (raw == null || deletedCodes == null || deletedCodes.isEmpty()) {
            return raw;
        }
        if (raw instanceof String text) {
            return omitJsonText(text, deletedCodes);
        }
        if (raw instanceof List<?> list) {
            return omitList(list, deletedCodes);
        }
        if (raw instanceof Map<?, ?> map) {
            return omitMap(map, deletedCodes);
        }
        return raw;
    }

    private static void omitKey(Map<String, Object> baseFields, String key, Set<String> deletedCodes) {
        if (!baseFields.containsKey(key)) {
            return;
        }
        baseFields.put(key, omitValue(baseFields.get(key), deletedCodes));
    }

    private static Object omitJsonText(String text, Set<String> deletedCodes) {
        String trimmed = text.trim();
        if (trimmed.isEmpty() || (!trimmed.startsWith("[") && !trimmed.startsWith("{"))) {
            return text;
        }
        try {
            Object parsed = JSON.readValue(trimmed, Object.class);
            return JSON.writeValueAsString(omitValue(parsed, deletedCodes));
        } catch (Exception ignored) {
            return text;
        }
    }

    private static List<Object> omitList(List<?> list, Set<String> deletedCodes) {
        List<Object> out = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof String code) {
                String trimmed = code.trim();
                if (!trimmed.isEmpty() && !deletedCodes.contains(trimmed)) {
                    out.add(trimmed);
                }
                continue;
            }
            if (item instanceof Map<?, ?> map) {
                String code = actionCodeOf(map);
                if (code != null && deletedCodes.contains(code)) {
                    continue;
                }
                out.add(omitMap(map, deletedCodes));
                continue;
            }
            out.add(item);
        }
        return out;
    }

    private static Map<String, Object> omitMap(Map<?, ?> map, Set<String> deletedCodes) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = String.valueOf(entry.getKey());
            Object value = entry.getValue();
            if ("actionTree".equals(key) || "stepTree".equals(key)
                    || "children".equals(key) || "members".equals(key)) {
                out.put(key, omitValue(value, deletedCodes));
                continue;
            }
            if ("action_tree_json".equals(key) || "step_tree_json".equals(key)) {
                out.put(key, omitValue(value, deletedCodes));
                continue;
            }
            if ("methods".equals(key) && value instanceof List<?> methods) {
                List<Object> nextMethods = new ArrayList<>();
                for (Object method : methods) {
                    nextMethods.add(method instanceof Map<?, ?> methodMap
                            ? omitMap(methodMap, deletedCodes)
                            : method);
                }
                out.put(key, nextMethods);
                continue;
            }
            out.put(key, value);
        }
        return out;
    }

    private static String actionCodeOf(Map<?, ?> map) {
        for (String key : List.of("actionId", "refCode", "actionCode", "code")) {
            Object code = map.get(key);
            if (code == null) {
                continue;
            }
            String text = String.valueOf(code).trim();
            if (text.isEmpty() || text.chars().allMatch(Character::isDigit)) {
                continue;
            }
            return text;
        }
        return null;
    }
}
