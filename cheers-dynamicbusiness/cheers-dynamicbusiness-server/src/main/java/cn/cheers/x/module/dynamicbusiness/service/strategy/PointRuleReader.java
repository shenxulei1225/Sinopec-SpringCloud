package cn.cheers.x.module.dynamicbusiness.service.strategy;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.strategy.ConditionStrategyDO;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.util.StringUtils;

/**
 * 从已保存的策略里读出点位规则。
 * 巡检用的执行账条件读不出来，交给原来的策略运行时。
 * 不比较读数。
 */
public final class PointRuleReader {

    private PointRuleReader() {
    }

    public static PointProcessingRule read(ConditionStrategyDO row) {
        if (row == null || row.getId() == null || !StringUtils.hasText(row.getConditionJson())) {
            return null;
        }
        JSONObject root = JSON.parseObject(row.getConditionJson());
        JSONArray all = root == null ? null : root.getJSONArray("all");
        if (all == null) {
            return null;
        }
        String field = null;
        String op = null;
        String value = null;
        boolean pointField = false;
        for (int i = 0; i < all.size(); i++) {
            JSONObject item = all.getJSONObject(i);
            if (item == null) {
                continue;
            }
            String type = item.getString("type");
            if ("FIELD_COMPARE".equals(type) || "POINT_FIELD".equals(type)) {
                field = item.getString("field");
                op = item.getString("op");
                value = item.getString("value");
                pointField = "POINT_FIELD".equals(type);
            }
        }
        if (!StringUtils.hasText(field)) {
            return null;
        }
        JSONObject params = StringUtils.hasText(row.getActionParamsJson())
                ? JSON.parseObject(row.getActionParamsJson()) : new JSONObject();
        if (params == null) {
            params = new JSONObject();
        }
        String kind = kindOf(row.getActionCode(), params.getString("signalKind"), op, value, pointField);
        if (kind == null) {
            return null;
        }
        Double threshold = parseThreshold(value);
        if (!PointProcessingRule.STATUS.equals(kind) && threshold == null) {
            return null;
        }
        if (PointProcessingRule.THRESHOLD.equals(kind) || PointProcessingRule.ALARM_FLAG.equals(kind)) {
            if (!RegisteredActions.RAISE_ALARM.equals(row.getActionCode())) {
                return null;
            }
            if (params.getLong("alarmTypeId") == null || !StringUtils.hasText(params.getString("alarmLevel"))) {
                return null;
            }
        }
        return new PointProcessingRule(
                row.getId(),
                field.trim(),
                kind,
                op,
                threshold,
                params.getLong("alarmTypeId"),
                params.getString("alarmLevel"),
                params.getLong("deviceId"),
                params.getLong("locationId"));
    }

    private static String kindOf(String action, String signalKind, String op, String value, boolean pointField) {
        if (PointProcessingRule.THRESHOLD.equals(signalKind)
                || PointProcessingRule.ALARM_FLAG.equals(signalKind)
                || PointProcessingRule.STATUS.equals(signalKind)) {
            return signalKind;
        }
        if (RegisteredActions.REFRESH_DEVICE_STATUS.equals(action) || pointField) {
            return PointProcessingRule.STATUS;
        }
        if (!RegisteredActions.RAISE_ALARM.equals(action)) {
            return null;
        }
        if ("EQ".equals(op) && "1".equals(value == null ? null : value.trim())) {
            return PointProcessingRule.ALARM_FLAG;
        }
        return PointProcessingRule.THRESHOLD;
    }

    private static Double parseThreshold(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
