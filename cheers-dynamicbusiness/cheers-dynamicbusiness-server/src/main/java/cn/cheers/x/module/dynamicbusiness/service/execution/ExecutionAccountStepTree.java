package cn.cheers.x.module.dynamicbusiness.service.execution;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStepSnapshotDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapReqVO;
import com.alibaba.fastjson2.JSON;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 一笔执行账上的步骤图和逐步结果。
 * <p>管什么：生成时把步骤图冻进这笔账；回写时按步骤编号改这一步的状态和结果。
 * <p>不管什么：不新建步骤行，不改总任务上的步骤图。
 * <p>缺步骤图、步骤编号对不上、父步骤还没出现，直接报错。
 */
public final class ExecutionAccountStepTree {

    /** 生成时写下的步骤图。形状是步骤列表，父步骤用步骤编号指向。 */
    public static final String FIELD_STEP_TREE = "step_tree_json";
    /** 按步骤编号记下的状态和结果。没回报的步骤没有这一格。 */
    public static final String FIELD_STEP_RESULTS = "step_results";

    private ExecutionAccountStepTree() {
    }

    /**
     * 把生成时的步骤清单冻成可放进执行账的列表。
     * 父步骤必须排在子步骤前面。编号重复、缺编号、找不到父步骤都报错。
     */
    public static List<Map<String, Object>> freeze(
            List<TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO> steps) {
        if (steps == null || steps.isEmpty()) {
            throw new ServiceException(400, "步骤清单为空");
        }
        List<Map<String, Object>> tree = new ArrayList<>(steps.size());
        Map<String, Integer> seen = new LinkedHashMap<>();
        for (TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO draft : steps) {
            if (draft == null || !hasText(draft.getStepCode())) {
                throw new ServiceException(400, "步骤 draft 缺少 stepCode");
            }
            String stepCode = draft.getStepCode().trim();
            if (seen.containsKey(stepCode)) {
                throw new ServiceException(400, "步骤 stepCode 重复：" + stepCode);
            }
            if (hasText(draft.getParentStepCode()) && !seen.containsKey(draft.getParentStepCode().trim())) {
                throw new ServiceException(400,
                        "步骤 parentStepCode 未找到或顺序错误（父须在子之前）：" + draft.getParentStepCode());
            }
            seen.put(stepCode, tree.size());
            tree.add(nodeOf(draft, stepCode));
        }
        return tree;
    }

    /** 把已经冻好的步骤图放上这笔账。已有结果时不覆盖。 */
    public static void putFrozen(Map<String, Object> custom, List<Map<String, Object>> tree) {
        custom.put(FIELD_STEP_TREE, new ArrayList<>(tree));
        custom.putIfAbsent(FIELD_STEP_RESULTS, new LinkedHashMap<String, Object>());
    }

    /**
     * 按步骤编号写入这一步的状态和结果。
     * 步骤图里没有这个编号就报错，不新造一步。
     * 返回合并后的结果表，调用方写回这笔账。
     */
    public static Map<String, Object> mergeResult(
            Map<String, Object> custom,
            String stepCode,
            String status,
            Map<String, Object> resultPayload) {
        if (!hasText(stepCode)) {
            throw new ServiceException(400, "回写须带步骤编号");
        }
        String code = stepCode.trim();
        requireNode(custom, code);
        Map<String, Object> results = copyMap(asMap(custom.get(FIELD_STEP_RESULTS)));
        Map<String, Object> one = new LinkedHashMap<>();
        one.put("status", status.trim());
        if (resultPayload != null) {
            one.put("resultPayload", resultPayload);
        }
        results.put(code, one);
        return results;
    }

    /**
     * 打开这笔账时用的步骤列表。编号是这份清单里的顺序，只用于画出上下级。
     * 没有步骤图时返回空列表。
     */
    public static List<TaskExecutionStepSnapshotDTO> project(Map<String, Object> custom) {
        List<Map<String, Object>> tree = asNodeList(custom == null ? null : custom.get(FIELD_STEP_TREE));
        if (tree.isEmpty()) {
            return List.of();
        }
        Map<String, Object> results = asMap(custom.get(FIELD_STEP_RESULTS));
        Map<String, Long> idByCode = new LinkedHashMap<>();
        for (int i = 0; i < tree.size(); i++) {
            idByCode.put(text(tree.get(i).get("stepCode")), (long) i + 1);
        }
        List<TaskExecutionStepSnapshotDTO> steps = new ArrayList<>(tree.size());
        for (int i = 0; i < tree.size(); i++) {
            Map<String, Object> node = tree.get(i);
            String stepCode = text(node.get("stepCode"));
            TaskExecutionStepSnapshotDTO snapshot = new TaskExecutionStepSnapshotDTO();
            snapshot.setId((long) i + 1);
            snapshot.setStepCode(stepCode);
            snapshot.setStepTitle(firstText(node.get("stepTitle"), node.get("name"), stepCode));
            String parentCode = text(node.get("parentStepCode"));
            snapshot.setParentId(parentCode.isEmpty() ? 0L : idByCode.getOrDefault(parentCode, 0L));
            snapshot.setSource(asMap(node.get("source")));
            Map<String, Object> result = asMap(results.get(stepCode));
            String status = text(result.get("status"));
            snapshot.setStepStatus(status.isEmpty() ? "pending" : status);
            snapshot.setResultPayload(result.get("resultPayload") instanceof Map<?, ?> payload
                    ? copyLoose(payload)
                    : null);
            steps.add(snapshot);
        }
        return steps;
    }

    /**
     * 告警名称要用检查项标题。从这一步往上找种类为检查项的节点。
     * 找不到就返回空，调用方只用告警原文。
     */
    public static String inspectionItemTitle(Map<String, Object> custom, String stepCode) {
        List<Map<String, Object>> tree = asNodeList(custom == null ? null : custom.get(FIELD_STEP_TREE));
        Map<String, Map<String, Object>> byCode = new LinkedHashMap<>();
        for (Map<String, Object> node : tree) {
            byCode.put(text(node.get("stepCode")), node);
        }
        Map<String, Object> current = byCode.get(stepCode == null ? "" : stepCode.trim());
        int guard = 0;
        while (current != null && guard++ < 12) {
            String parentCode = text(current.get("parentStepCode"));
            if (parentCode.isEmpty()) {
                return "";
            }
            Map<String, Object> parent = byCode.get(parentCode);
            if (parent == null) {
                return "";
            }
            if ("inspection_item".equals(text(parent.get("stepType")))) {
                return firstText(parent.get("stepTitle"), parent.get("name"), "");
            }
            current = parent;
        }
        return "";
    }

    private static void requireNode(Map<String, Object> custom, String stepCode) {
        if (custom == null || asNodeList(custom.get(FIELD_STEP_TREE)).isEmpty()) {
            throw new ServiceException(400, "这笔执行账没有步骤图");
        }
        for (Map<String, Object> node : asNodeList(custom.get(FIELD_STEP_TREE))) {
            if (stepCode.equals(text(node.get("stepCode")))) {
                return;
            }
        }
        throw new ServiceException(404, "这笔执行账的步骤图里没有这一步：" + stepCode);
    }

    private static Map<String, Object> nodeOf(
            TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO draft, String stepCode) {
        Map<String, Object> node = new LinkedHashMap<>();
        node.put("stepCode", stepCode);
        node.put("name", draft.getName());
        node.put("stepOrder", draft.getStepOrder() == null ? 0 : draft.getStepOrder());
        node.put("stepTitle", hasText(draft.getStepTitle()) ? draft.getStepTitle().trim() : draft.getName());
        node.put("stepType", hasText(draft.getStepType()) ? draft.getStepType().trim() : "sop_step");
        node.put("stepRequired", draft.getStepRequired() == null || draft.getStepRequired());
        if (hasText(draft.getParentStepCode())) {
            node.put("parentStepCode", draft.getParentStepCode().trim());
        }
        if (draft.getSource() != null && !draft.getSource().isEmpty()) {
            node.put("source", new LinkedHashMap<>(draft.getSource()));
        }
        return node;
    }

    private static List<Map<String, Object>> asNodeList(Object raw) {
        Object value = raw instanceof String text && hasText(text) ? JSON.parse(text) : raw;
        if (!(value instanceof List<?> list)) {
            return List.of();
        }
        List<Map<String, Object>> nodes = new ArrayList<>();
        for (Object item : list) {
            Map<String, Object> node = asMap(item);
            if (!node.isEmpty()) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    private static Map<String, Object> asMap(Object raw) {
        Object value = raw instanceof String text && hasText(text) ? JSON.parse(text) : raw;
        if (!(value instanceof Map<?, ?> map)) {
            return new LinkedHashMap<>();
        }
        return copyLoose(map);
    }

    private static Map<String, Object> copyLoose(Map<?, ?> source) {
        Map<String, Object> copy = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            if (entry.getKey() != null) {
                copy.put(String.valueOf(entry.getKey()), entry.getValue());
            }
        }
        return copy;
    }

    private static Map<String, Object> copyMap(Map<String, Object> source) {
        return new LinkedHashMap<>(source);
    }

    private static String text(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    private static String firstText(Object... values) {
        for (Object value : values) {
            String text = text(value);
            if (!text.isEmpty()) {
                return text;
            }
        }
        return "";
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
