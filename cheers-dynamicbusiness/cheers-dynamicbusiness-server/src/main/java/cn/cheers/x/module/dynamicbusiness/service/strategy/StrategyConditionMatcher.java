package cn.cheers.x.module.dynamicbusiness.service.strategy;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyTriggerEventDTO;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 按表单化条件判断事件是否命中。不解释脚本。
 */
final class StrategyConditionMatcher {

    private StrategyConditionMatcher() {
    }

    static boolean matches(String conditionJson, StrategyTriggerEventDTO event) {
        if (!StringUtils.hasText(conditionJson)) {
            throw new ServiceException(400, "策略缺少条件");
        }
        JSONObject root = JSON.parseObject(conditionJson);
        if (root == null) {
            throw new ServiceException(400, "策略条件不是合法对象");
        }
        JSONArray all = root.getJSONArray("all");
        if (all == null || all.isEmpty()) {
            throw new ServiceException(400, "策略条件至少要有一条");
        }
        for (int i = 0; i < all.size(); i++) {
            JSONObject item = all.getJSONObject(i);
            if (item == null || !matchOne(item, event)) {
                return false;
            }
        }
        return true;
    }

    private static boolean matchOne(JSONObject item, StrategyTriggerEventDTO event) {
        String type = item.getString("type");
        if ("ALWAYS".equals(type)) {
            return true;
        }
        if ("HAS_EXECUTION_RECORD".equals(type)) {
            return event.getExecutionRecordId() != null;
        }
        if ("HAS_STEP_UPDATES".equals(type)) {
            return event.getStepUpdates() != null && !event.getStepUpdates().isEmpty();
        }
        if ("HAS_EXECUTION_STATUS".equals(type)) {
            return StringUtils.hasText(event.getExecutionStatus());
        }
        if ("FIELD_COMPARE".equals(type)) {
            return compareField(item, event);
        }
        throw new ServiceException(400, "不支持的条件类型：" + type);
    }

    private static boolean compareField(JSONObject item, StrategyTriggerEventDTO event) {
        String field = item.getString("field");
        String op = item.getString("op");
        if (!StringUtils.hasText(field) || !StringUtils.hasText(op)) {
            throw new ServiceException(400, "数到了哪一档必须写字段和比较方式");
        }
        Object actual = readField(event, field.trim());
        if (actual == null) {
            return false;
        }
        Object expected = item.get("value");
        int cmp = compareValues(actual, expected);
        return switch (op.trim()) {
            case "EQ" -> cmp == 0;
            case "NEQ" -> cmp != 0;
            case "GT" -> cmp > 0;
            case "GTE" -> cmp >= 0;
            case "LT" -> cmp < 0;
            case "LTE" -> cmp <= 0;
            default -> throw new ServiceException(400, "不支持的比较方式：" + op);
        };
    }

    private static Object readField(StrategyTriggerEventDTO event, String field) {
        if (event.getFields() != null && event.getFields().containsKey(field)) {
            return event.getFields().get(field);
        }
        if (event.getFields() != null) {
            for (Map.Entry<String, Object> e : event.getFields().entrySet()) {
                if (field.equalsIgnoreCase(e.getKey())) {
                    return e.getValue();
                }
            }
        }
        return null;
    }

    private static int compareValues(Object actual, Object expected) {
        if (actual instanceof Number || expected instanceof Number) {
            BigDecimal left = toDecimal(actual);
            BigDecimal right = toDecimal(expected);
            if (left == null || right == null) {
                return String.valueOf(actual).compareTo(String.valueOf(expected));
            }
            return left.compareTo(right);
        }
        return String.valueOf(actual).compareTo(String.valueOf(expected));
    }

    private static BigDecimal toDecimal(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number number) {
            return new BigDecimal(number.toString());
        }
        try {
            return new BigDecimal(String.valueOf(raw).trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
