package cn.cheers.x.module.dynamicbusiness.service.emergencycommand;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 应急事件上的脉络和当前预案状态。
 *
 * 管什么：追加启动、变级、步骤开始、步骤完成、信息上报、进入后期处置和结案。级别变化只改当前级别。
 * 不管什么：预案原文。步骤正文不写进脉络。
 * 禁止：换级时删掉原级别已经写下的完成和上报，或改写已有记录。
 */
public final class EmergencyCommandRecords {

    public static final String KIND_INTAKE = "intake";
    public static final String KIND_ASSESS = "assess";
    public static final String KIND_AFTERMATH = "enter_aftermath";
    public static final String KIND_START = "start_plan";
    public static final String KIND_LEVEL = "level_change";
    public static final String KIND_STEP_STARTED = "step_started";
    public static final String KIND_STEP_COMPLETED = "step_completed";
    public static final String KIND_REPORT = "submit_report";
    /** 结案。没有附加栏。 */
    public static final String KIND_CLOSE = "close_case";

    private EmergencyCommandRecords() {
    }

    public static JSONArray read(Object raw) {
        JSONArray parsed = asArray(raw);
        if (parsed == null) {
            return new JSONArray();
        }
        return JSON.parseArray(parsed.toJSONString());
    }

    /**
     * 追加一条新记录。已有记录保持原样。
     */
    public static JSONArray append(JSONArray current, String kind, Map<String, String> fields) {
        JSONArray next = read(current);
        JSONObject row = new JSONObject();
        row.put("recordId", UUID.randomUUID().toString());
        row.put("kind", kind);
        row.put("at", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
        if (fields != null) {
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isBlank()) {
                    row.put(entry.getKey(), entry.getValue());
                }
            }
        }
        next.add(row);
        return next;
    }

    public static Map<String, String> fields(String... pairs) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            fields.put(pairs[i], pairs[i + 1]);
        }
        return fields;
    }

    public static boolean hasStepRecord(JSONArray timeline, String kind, String level, String nodeKey) {
        JSONArray rows = read(timeline);
        for (int i = 0; i < rows.size(); i++) {
            JSONObject row = rows.getJSONObject(i);
            if (row == null) {
                continue;
            }
            if (kind.equals(text(row.get("kind")))
                    && level.equals(text(row.get("level")))
                    && nodeKey.equals(text(row.get("nodeKey")))) {
                return true;
            }
        }
        return false;
    }

    public static JSONArray currentState(String planId, String level) {
        JSONObject row = new JSONObject();
        row.put("planId", planId);
        row.put("currentLevel", level);
        row.put("currentStage", "应急响应");
        JSONArray state = new JSONArray();
        state.add(row);
        return state;
    }

    public static String startedAt(Object timelineRaw) {
        JSONArray rows = read(timelineRaw);
        for (int i = 0; i < rows.size(); i++) {
            JSONObject row = rows.getJSONObject(i);
            if (row != null && KIND_START.equals(text(row.get("kind")))) {
                return text(row.get("at"));
            }
        }
        return "";
    }

    private static JSONArray asArray(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof JSONArray array) {
            return array;
        }
        if (raw instanceof String text && !text.isBlank()) {
            String trimmed = text.trim();
            if (trimmed.startsWith("[")) {
                return JSON.parseArray(trimmed);
            }
            return null;
        }
        return JSON.parseArray(JSON.toJSONString(raw));
    }

    private static String text(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }
}
