package cn.cheers.x.module.dynamicbusiness.service.emergencycommand;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 从预案步骤树里取出某一级，并在响应副本上标记完成。
 *
 * 管什么：启动时复制哪一棵树，指挥页完成哪一步。
 * 不管什么：预案原文。完成步骤只改响应上的副本。
 * 禁止：按名称猜测级别。级别只认步骤包里的 methodKey。
 */
public final class EmergencyCommandSteps {

    private EmergencyCommandSteps() {
    }

    /**
     * 取出与研判级别相同的那一棵步骤，并把每一步标成未完成。
     *
     * @return 只含这一级的步骤包；该级别没有步骤时返回 null
     */
    public static JSONObject copyLevel(Object raw, String level) {
        JSONObject pack = asObject(raw);
        if (pack == null || level == null || level.isBlank()) {
            return null;
        }
        JSONArray methods = pack.getJSONArray("methods");
        if (methods == null) {
            return null;
        }
        for (int i = 0; i < methods.size(); i++) {
            JSONObject method = methods.getJSONObject(i);
            if (method == null || !level.equals(text(method.get("methodKey")))) {
                continue;
            }
            JSONArray tree = method.getJSONArray("stepTree");
            if (tree == null || tree.isEmpty()) {
                return null;
            }
            JSONArray copy = JSON.parseArray(JSON.toJSONString(tree));
            markOpen(copy);
            JSONObject kept = new JSONObject();
            kept.put("methodKey", level);
            if (method.get("label") != null) {
                kept.put("label", method.get("label"));
            }
            kept.put("stepTree", copy);
            JSONArray one = new JSONArray();
            one.add(kept);
            JSONObject out = new JSONObject();
            Integer version = pack.getInteger("version");
            out.put("version", version == null ? 1 : version);
            out.put("methods", one);
            return out;
        }
        return null;
    }

    /**
     * 预案里每一级的步骤，带子步骤的父级编号。
     * 已启动的那一级带上响应副本里的完成情况。其它级只给查看，完成情况为空。
     */
    public static List<LevelNodes> listLevels(Object planRaw, Object responseRaw, String startedLevel) {
        Map<String, NodeMark> startedStatus = statusByNode(responseRaw);
        JSONObject plan = asObject(planRaw);
        List<LevelNodes> levels = new ArrayList<>();
        if (plan != null) {
            appendLevels(plan.getJSONArray("methods"), startedLevel, startedStatus, levels);
        }
        if (levels.isEmpty()) {
            JSONObject response = asObject(responseRaw);
            if (response != null) {
                appendLevels(response.getJSONArray("methods"), startedLevel, startedStatus, levels);
            }
        }
        return levels;
    }

    /**
     * 预案上用户定义的阶段，顺序就是脉络从前往后的顺序。名称用用户写的，不在这里另起名字。
     */
    public static List<StageDef> listStages(Object raw) {
        JSONObject pack = asObject(raw);
        List<StageDef> stages = new ArrayList<>();
        if (pack == null) {
            return stages;
        }
        JSONArray rows = pack.getJSONArray("stages");
        if (rows == null) {
            return stages;
        }
        for (int i = 0; i < rows.size(); i++) {
            JSONObject row = rows.getJSONObject(i);
            if (row == null) {
                continue;
            }
            String stageKey = text(row.get(EmergencyCommandFields.STEP_STAGE_KEY));
            String name = text(row.get("name"));
            if (stageKey.isEmpty() || name.isEmpty()) {
                continue;
            }
            stages.add(new StageDef(stageKey, name));
        }
        return stages;
    }

    /**
     * 在响应副本里把指定步骤标为正在执行，并记下开始时间。
     *
     * @return 找不到该步时返回 null。已经完成的步骤保持已完成。
     */
    public static JSONObject begin(Object raw, String nodeKey) {
        JSONObject pack = asObject(raw);
        if (pack == null || nodeKey == null || nodeKey.isBlank()) {
            return null;
        }
        JSONObject copy = JSON.parseObject(JSON.toJSONString(pack));
        if (!markRunning(copy.getJSONArray("methods"), nodeKey)) {
            return null;
        }
        return copy;
    }

    /**
     * 在响应副本里把指定步骤标为已完成。
     *
     * @return 找不到该步时返回 null
     */
    public static JSONObject complete(Object raw, String nodeKey) {
        JSONObject pack = asObject(raw);
        if (pack == null || nodeKey == null || nodeKey.isBlank()) {
            return null;
        }
        JSONObject copy = JSON.parseObject(JSON.toJSONString(pack));
        if (!markDone(copy.getJSONArray("methods"), nodeKey)) {
            return null;
        }
        return copy;
    }

    private static boolean markDone(JSONArray methods, String nodeKey) {
        if (methods == null) {
            return false;
        }
        for (int i = 0; i < methods.size(); i++) {
            JSONObject method = methods.getJSONObject(i);
            if (method != null && markDoneNodes(method.getJSONArray("stepTree"), nodeKey)) {
                return true;
            }
        }
        return false;
    }

    private static boolean markRunning(JSONArray methods, String nodeKey) {
        if (methods == null) {
            return false;
        }
        for (int i = 0; i < methods.size(); i++) {
            JSONObject method = methods.getJSONObject(i);
            if (method != null && markRunningNodes(method.getJSONArray("stepTree"), nodeKey)) {
                return true;
            }
        }
        return false;
    }

    private static boolean markRunningNodes(JSONArray nodes, String nodeKey) {
        if (nodes == null) {
            return false;
        }
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            if (node == null) {
                continue;
            }
            if (nodeKey.equals(text(node.get("nodeKey")))) {
                if (EmergencyCommandFields.STEP_DONE.equals(text(node.get(EmergencyCommandFields.STEP_RUN_STATUS)))) {
                    return true;
                }
                node.put(EmergencyCommandFields.STEP_RUN_STATUS, EmergencyCommandFields.STEP_RUNNING);
                if (text(node.get(EmergencyCommandFields.STEP_STARTED_AT)).isEmpty()) {
                    node.put(EmergencyCommandFields.STEP_STARTED_AT,
                            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
                }
                return true;
            }
            if (markRunningNodes(node.getJSONArray("children"), nodeKey)) {
                return true;
            }
        }
        return false;
    }

    private static boolean markDoneNodes(JSONArray nodes, String nodeKey) {
        if (nodes == null) {
            return false;
        }
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            if (node == null) {
                continue;
            }
            if (nodeKey.equals(text(node.get("nodeKey")))) {
                node.put(EmergencyCommandFields.STEP_RUN_STATUS, EmergencyCommandFields.STEP_DONE);
                if (text(node.get(EmergencyCommandFields.STEP_COMPLETED_AT)).isEmpty()) {
                    node.put(EmergencyCommandFields.STEP_COMPLETED_AT,
                            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
                }
                return true;
            }
            if (markDoneNodes(node.getJSONArray("children"), nodeKey)) {
                return true;
            }
        }
        return false;
    }

    private static void markOpen(JSONArray nodes) {
        if (nodes == null) {
            return;
        }
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            if (node == null) {
                continue;
            }
            if (node.get("nodeKey") != null) {
                node.put(EmergencyCommandFields.STEP_RUN_STATUS, EmergencyCommandFields.STEP_OPEN);
            }
            markOpen(node.getJSONArray("children"));
        }
    }

    private static void appendLevels(
            JSONArray methods, String startedLevel, Map<String, NodeMark> startedStatus, List<LevelNodes> levels) {
        if (methods == null) {
            return;
        }
        for (int i = 0; i < methods.size(); i++) {
            JSONObject method = methods.getJSONObject(i);
            if (method == null) {
                continue;
            }
            String level = text(method.get("methodKey"));
            if (level.isEmpty()) {
                continue;
            }
            boolean started = level.equals(startedLevel);
            levels.add(new LevelNodes(
                    level,
                    flattenNodes(method.getJSONArray("stepTree"), null, started ? startedStatus : null)));
        }
    }

    private static List<StepNode> flattenNodes(JSONArray nodes, String parentKey, Map<String, NodeMark> status) {
        List<StepNode> out = new ArrayList<>();
        if (nodes == null) {
            return out;
        }
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            if (node == null || node.get("nodeKey") == null) {
                continue;
            }
            String nodeKey = text(node.get("nodeKey"));
            String ownParent = text(node.get("parentNodeKey"));
            String parent = ownParent.isEmpty() ? parentKey : ownParent;
            String description = text(node.get("stepDescription"));
            int order = node.getIntValue("order");
            if (order <= 0) {
                order = i + 1;
            }
            String runStatus = "";
            String completedAt = "";
            String startedAt = "";
            if (status != null) {
                NodeMark mark = status.get(nodeKey);
                runStatus = mark == null ? EmergencyCommandFields.STEP_OPEN : mark.runStatus();
                completedAt = mark == null ? "" : mark.completedAt();
                startedAt = mark == null ? "" : mark.startedAt();
            }
            out.add(new StepNode(
                    nodeKey,
                    parent == null || parent.isEmpty() ? null : parent,
                    description.isEmpty() ? nodeKey : description,
                    order,
                    runStatus,
                    completedAt,
                    startedAt,
                    text(node.get(EmergencyCommandFields.STEP_STAGE_KEY))));
            out.addAll(flattenNodes(node.getJSONArray("children"), nodeKey, status));
        }
        return out;
    }

    private static Map<String, NodeMark> statusByNode(Object raw) {
        Map<String, NodeMark> status = new LinkedHashMap<>();
        JSONObject pack = asObject(raw);
        if (pack == null) {
            return status;
        }
        JSONArray methods = pack.getJSONArray("methods");
        if (methods == null) {
            return status;
        }
        for (int i = 0; i < methods.size(); i++) {
            JSONObject method = methods.getJSONObject(i);
            if (method != null) {
                collectStatus(method.getJSONArray("stepTree"), status);
            }
        }
        return status;
    }

    private static void collectStatus(JSONArray nodes, Map<String, NodeMark> status) {
        if (nodes == null) {
            return;
        }
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            if (node == null || node.get("nodeKey") == null) {
                continue;
            }
            String runStatus = text(node.get(EmergencyCommandFields.STEP_RUN_STATUS));
            status.put(text(node.get("nodeKey")), new NodeMark(
                    runStatus.isEmpty() ? EmergencyCommandFields.STEP_OPEN : runStatus,
                    text(node.get(EmergencyCommandFields.STEP_COMPLETED_AT)),
                    text(node.get(EmergencyCommandFields.STEP_STARTED_AT))));
            collectStatus(node.getJSONArray("children"), status);
        }
    }

    private static JSONObject asObject(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof JSONObject object) {
            return object;
        }
        if (raw instanceof String text && !text.isBlank()) {
            return JSON.parseObject(text);
        }
        return JSON.parseObject(JSON.toJSONString(raw));
    }

    private static String text(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    /** 用户在预案上定义的阶段。名称原样显示。 */
    public record StageDef(String stageKey, String name) {
    }

    /** 某一级响应上的步骤。完成情况只在已经启动的那一级有值。 */
    public record LevelNodes(String level, List<StepNode> nodes) {
    }

    /** 步骤树上的一步。父级编号为空表示这一步在最外层。 */
    public record StepNode(
            String nodeKey,
            String parentNodeKey,
            String description,
            int order,
            String runStatus,
            String completedAt,
            String startedAt,
            String stageKey) {
    }

    private record NodeMark(String runStatus, String completedAt, String startedAt) {
    }
}
