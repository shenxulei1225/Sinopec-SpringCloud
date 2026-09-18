package cn.cheers.x.module.dynamicbusiness.service.strategy;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo.ConditionStrategyRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo.ConditionStrategySaveReqVO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyTriggerEventDTO;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 把页面上勾的条件和动作参数收成结构化 JSON。不解释脚本。
 */
final class ConditionStrategyCodec {

    private ConditionStrategyCodec() {
    }

    static String eventTypeName(String eventType) {
        if (StrategyTriggerEventDTO.EVENT_COLLECTION_RECEIVED.equals(eventType)) {
            return "采集结果到了";
        }
        if (StrategyTriggerEventDTO.EVENT_EXECUTION_START.equals(eventType)) {
            return "人点了开始";
        }
        return eventType;
    }

    static String compareOpName(String op) {
        if (!StringUtils.hasText(op)) {
            return "";
        }
        return switch (op.trim()) {
            case "EQ" -> "等于";
            case "NEQ" -> "不等于";
            case "GT" -> "大于";
            case "GTE" -> "大于等于";
            case "LT" -> "小于";
            case "LTE" -> "小于等于";
            default -> op;
        };
    }

    static String encodeCondition(ConditionStrategySaveReqVO req) {
        List<JSONObject> all = new ArrayList<>();
        if (Boolean.TRUE.equals(req.isRequireAlways())) {
            JSONObject item = new JSONObject();
            item.put("type", "ALWAYS");
            all.add(item);
        }
        if (Boolean.TRUE.equals(req.isRequireExecutionRecord())) {
            JSONObject item = new JSONObject();
            item.put("type", "HAS_EXECUTION_RECORD");
            all.add(item);
        }
        if (Boolean.TRUE.equals(req.isRequireStepUpdates())) {
            JSONObject item = new JSONObject();
            item.put("type", "HAS_STEP_UPDATES");
            all.add(item);
        }
        if (Boolean.TRUE.equals(req.isRequireExecutionStatus())) {
            JSONObject item = new JSONObject();
            item.put("type", "HAS_EXECUTION_STATUS");
            all.add(item);
        }
        if (StringUtils.hasText(req.getCompareField())) {
            if (!StringUtils.hasText(req.getCompareOp()) || !StringUtils.hasText(req.getCompareValue())) {
                throw new ServiceException(400, "要比哪个数时，比较方式和阈值都要填");
            }
            JSONObject item = new JSONObject();
            item.put("type", "FIELD_COMPARE");
            item.put("field", req.getCompareField().trim());
            item.put("op", req.getCompareOp().trim());
            item.put("value", req.getCompareValue().trim());
            all.add(item);
        }
        if (all.isEmpty()) {
            throw new ServiceException(400, "至少勾一条条件");
        }
        JSONObject root = new JSONObject();
        root.put("all", all);
        return root.toJSONString();
    }

    static String encodeActionParams(ConditionStrategySaveReqVO req) {
        if (RegisteredActions.RAISE_ALARM.equals(req.getActionCode())) {
            if (req.getAlarmTypeId() == null || !StringUtils.hasText(req.getAlarmLevel())
                    || req.getDeviceId() == null || req.getLocationId() == null) {
                throw new ServiceException(400, "新增一条告警必须填告警类型、级别、设备和位置，不能空着让系统猜");
            }
            JSONObject params = new JSONObject();
            params.put("alarmTypeId", req.getAlarmTypeId());
            params.put("alarmLevel", req.getAlarmLevel().trim());
            params.put("deviceId", req.getDeviceId());
            params.put("locationId", req.getLocationId());
            return params.toJSONString();
        }
        if (RegisteredActions.UPDATE_EXECUTION_STATUS.equals(req.getActionCode())
                && StringUtils.hasText(req.getExecutionStatus())) {
            JSONObject params = new JSONObject();
            params.put("executionStatus", req.getExecutionStatus().trim());
            return params.toJSONString();
        }
        if (RegisteredActions.SEND_NOTIFICATION.equals(req.getActionCode())
                && StringUtils.hasText(req.getRecipientUserIds())) {
            JSONObject params = new JSONObject();
            params.put("recipientUserIds", req.getRecipientUserIds().trim());
            return params.toJSONString();
        }
        return null;
    }

    static void fillFromStored(ConditionStrategyRespVO vo, String conditionJson, String actionParamsJson) {
        vo.setRequireAlways(false);
        vo.setRequireExecutionRecord(false);
        vo.setRequireStepUpdates(false);
        vo.setRequireExecutionStatus(false);
        if (StringUtils.hasText(conditionJson)) {
            JSONObject root = JSON.parseObject(conditionJson);
            JSONArray all = root == null ? null : root.getJSONArray("all");
            if (all != null) {
                for (int i = 0; i < all.size(); i++) {
                    JSONObject item = all.getJSONObject(i);
                    if (item == null) {
                        continue;
                    }
                    if ("ALWAYS".equals(item.getString("type"))) {
                        vo.setRequireAlways(true);
                    }
                    if ("HAS_EXECUTION_RECORD".equals(item.getString("type"))) {
                        vo.setRequireExecutionRecord(true);
                    }
                    if ("HAS_STEP_UPDATES".equals(item.getString("type"))) {
                        vo.setRequireStepUpdates(true);
                    }
                    if ("HAS_EXECUTION_STATUS".equals(item.getString("type"))) {
                        vo.setRequireExecutionStatus(true);
                    }
                    if ("FIELD_COMPARE".equals(item.getString("type"))) {
                        vo.setCompareField(item.getString("field"));
                        vo.setCompareOp(item.getString("op"));
                        vo.setCompareValue(item.getString("value"));
                    }
                }
            }
        }
        if (StringUtils.hasText(actionParamsJson)) {
            JSONObject params = JSON.parseObject(actionParamsJson);
            if (params != null) {
                vo.setAlarmTypeId(params.getLong("alarmTypeId"));
                vo.setAlarmLevel(params.getString("alarmLevel"));
                vo.setDeviceId(params.getLong("deviceId"));
                vo.setLocationId(params.getLong("locationId"));
                vo.setExecutionStatus(params.getString("executionStatus"));
                vo.setRecipientUserIds(params.getString("recipientUserIds"));
            }
        }
        vo.setConditionText(conditionText(vo));
    }

    static String conditionText(ConditionStrategyRespVO vo) {
        List<String> parts = new ArrayList<>();
        if (vo.isRequireAlways()) {
            parts.add("只要这件事发生");
        }
        if (vo.isRequireExecutionRecord()) {
            parts.add("有这次执行的账本编号");
        }
        if (vo.isRequireStepUpdates()) {
            parts.add("有要对回的某一步");
        }
        if (vo.isRequireExecutionStatus()) {
            parts.add("有这次执行的新状态");
        }
        if (StringUtils.hasText(vo.getCompareField())) {
            parts.add(vo.getCompareField() + " " + compareOpName(vo.getCompareOp()) + " " + vo.getCompareValue());
        }
        return String.join("，且", parts);
    }
}
