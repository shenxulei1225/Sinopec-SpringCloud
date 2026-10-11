package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.inspection.task.api.TaskUpdateHintMarkReqDTO;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.execution.steptree.HostSopParamPack;
import cn.cheers.x.inspection.task.service.execution.steptree.InspectionMethodSnapshotParser;
import cn.cheers.x.inspection.task.service.execution.steptree.PatrolTaskStepTreeReader;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepTreeGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 动作库或检查项保存时，给用到这次改动的已生成任务记下「需要更新」。
 * <p>列表只读任务上的这段说明。点更新任务成功后由草稿写入清掉。
 * <p>没勾这项、步骤里没有这个动作的任务不记。
 * 动作耗时、参数增减、检查步骤变化会记。只改其它参数的默认值时，设备上这一格已经填过数的不记。
 */
@Service
@RequiredArgsConstructor
public class GeneratedTaskUpdateHintService {

    static final String TYPE_ACTION = "action";
    static final String TYPE_ITEM = "inspection_item";

    private final PatrolTaskEntityStore patrolTaskEntityStore;
    private final PatrolTaskStepTreeReader stepTreeReader;
    private final ObjectMapper objectMapper;

    public void mark(TaskUpdateHintMarkReqDTO req) {
        if (req == null || req.getEntityId() == null || !StringUtils.hasText(req.getEntityTypeCode())) {
            return;
        }
        if (Objects.equals(normalizeJson(req.getBeforeJson()), normalizeJson(req.getAfterJson()))) {
            return;
        }
        String type = req.getEntityTypeCode().trim();
        if (TYPE_ACTION.equalsIgnoreCase(type)) {
            markAction(req);
            return;
        }
        if (TYPE_ITEM.equalsIgnoreCase(type)) {
            markItem(req);
        }
    }

    private void markAction(TaskUpdateHintMarkReqDTO req) {
        Object before = parseJson(req.getBeforeJson());
        Object after = parseJson(req.getAfterJson());
        String label = labelOf(req.getName(), req.getCode(), "动作");
        Set<String> meansKeys = meansKeysOf(before, after);
        boolean allMeans = meansKeys.isEmpty();
        if (allMeans) {
            meansKeys = Set.of("");
        }
        List<PatrolTaskDraft> tasks = generatedTasks();
        for (String means : meansKeys) {
            boolean durationChanged = durationChanged(before, after, means);
            boolean slotsChanged = !fieldCodes(before, means).equals(fieldCodes(after, means));
            Set<String> defaultChanged = changedDefaults(before, after, means);
            if (!durationChanged && !slotsChanged && defaultChanged.isEmpty()) {
                continue;
            }
            for (PatrolTaskDraft task : tasks) {
                if (!allMeans && !means.equalsIgnoreCase(text(task.patrolExecutionMode()))) {
                    continue;
                }
                if (!taskUsesAction(task, req.getCode(), req.getEntityId())) {
                    continue;
                }
                List<String> lines = new ArrayList<>();
                if (durationChanged) {
                    lines.add("动作「" + label + "」的动作耗时已变化");
                }
                if (slotsChanged) {
                    lines.add("动作「" + label + "」的参数有增减");
                }
                if (!defaultChanged.isEmpty() && usesLibraryDefault(task, req.getCode(), req.getEntityId(), means, defaultChanged)) {
                    lines.add("动作「" + label + "」的参数默认值已变化，有的步骤当时用的是默认值");
                }
                appendLines(task, lines);
            }
        }
    }

    private void markItem(TaskUpdateHintMarkReqDTO req) {
        Object before = parseJson(req.getBeforeJson());
        Object after = parseJson(req.getAfterJson());
        String label = labelOf(req.getName(), req.getCode(), "检查项");
        Set<String> meansKeys = new LinkedHashSet<>();
        meansKeys.addAll(methodKeys(before));
        meansKeys.addAll(methodKeys(after));
        if (meansKeys.isEmpty()) {
            return;
        }
        List<PatrolTaskDraft> tasks = generatedTasks();
        for (String means : meansKeys) {
            if (stepCodes(before, means).equals(stepCodes(after, means))) {
                continue;
            }
            for (PatrolTaskDraft task : tasks) {
                if (!means.equalsIgnoreCase(text(task.patrolExecutionMode()))) {
                    continue;
                }
                if (!taskChecksItem(task, req.getEntityId())) {
                    continue;
                }
                appendLines(task, List.of("检查项「" + label + "」的检查步骤已变化"));
            }
        }
    }

    private void appendLines(PatrolTaskDraft task, List<String> incoming) {
        if (task.id() == null || incoming == null || incoming.isEmpty()) {
            return;
        }
        List<String> lines = new ArrayList<>();
        if (StringUtils.hasText(task.updateHint())) {
            for (String row : task.updateHint().split("\n")) {
                if (StringUtils.hasText(row)) {
                    lines.add(row.trim());
                }
            }
        }
        boolean added = false;
        for (String line : incoming) {
            if (!StringUtils.hasText(line) || lines.contains(line.trim())) {
                continue;
            }
            lines.add(line.trim());
            added = true;
        }
        if (!added) {
            return;
        }
        patrolTaskEntityStore.mergeDraftFields(task.id(), Map.of(
                PatrolTaskEntityStore.DRAFT_KEY_UPDATE_HINT, String.join("\n", lines)));
    }

    private List<PatrolTaskDraft> generatedTasks() {
        List<PatrolTaskDraft> all = patrolTaskEntityStore.listAll();
        List<PatrolTaskDraft> generated = new ArrayList<>();
        for (PatrolTaskDraft task : all) {
            if (task != null && Boolean.TRUE.equals(task.orchestrationCommitted())) {
                generated.add(task);
            }
        }
        return generated;
    }

    private boolean taskUsesAction(PatrolTaskDraft task, String actionCode, Long actionId) {
        for (Map<String, Object> node : actionNodes(task.stepTree())) {
            if (sameAction(node, actionCode, actionId)) {
                return true;
            }
        }
        return false;
    }

    private boolean usesLibraryDefault(
            PatrolTaskDraft task, String actionCode, Long actionId, String means, Set<String> slots
    ) {
        Map<String, Map<String, Object>> byKey = nodesByKey(task.stepTree());
        Map<Long, HostSopParamPack> packs = new LinkedHashMap<>();
        for (Map<String, Object> node : actionNodes(task.stepTree())) {
            if (!sameAction(node, actionCode, actionId)) {
                continue;
            }
            Long equipmentId = ancestorRefId(node, byKey, "equipment");
            Long itemId = ancestorRefId(node, byKey, "inspection_item");
            HostSopParamPack pack = equipmentId == null
                    ? HostSopParamPack.empty()
                    : packs.computeIfAbsent(equipmentId, stepTreeReader::loadHostPackOrEmpty);
            Map<String, Object> filled = pack.paramsFor(
                    itemId, text(node.get("sourceNodeKey")), text(node.get("refCode")), means);
            for (String slot : slots) {
                if (!isFilled(filled.get(slot))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean taskChecksItem(PatrolTaskDraft task, Long itemId) {
        if (itemId == null || task.inspectionContent() == null) {
            return false;
        }
        InspectionContent content = task.inspectionContent();
        if (content.getCustomObjects() != null) {
            for (InspectionContent.ObjectContent object : content.getCustomObjects()) {
                if (objectHasItem(object, itemId)) {
                    return true;
                }
            }
        }
        if (content.getGroups() != null) {
            for (InspectionContent.ObjectGroup group : content.getGroups()) {
                if (group.getObjects() == null) {
                    continue;
                }
                for (InspectionContent.ObjectContent object : group.getObjects()) {
                    if (objectHasItem(object, itemId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean objectHasItem(InspectionContent.ObjectContent object, Long itemId) {
        if (object == null || object.getItems() == null) {
            return false;
        }
        for (InspectionContent.ItemContent item : object.getItems()) {
            if (item != null && itemId.equals(item.getItemId())) {
                return true;
            }
        }
        return false;
    }

    private boolean durationChanged(Object before, Object after, String means) {
        ActionDurationParamSupport.SlotDuration left = ActionDurationParamSupport
                .readSlot(before, means, objectMapper).orElse(null);
        ActionDurationParamSupport.SlotDuration right = ActionDurationParamSupport
                .readSlot(after, means, objectMapper).orElse(null);
        if (left == null && right == null) {
            return false;
        }
        if (left == null || right == null) {
            return true;
        }
        if (left.seconds().compareTo(right.seconds()) != 0) {
            return true;
        }
        return !text(left.displayUnit()).equalsIgnoreCase(text(right.displayUnit()));
    }

    private Set<String> changedDefaults(Object before, Object after, String means) {
        Map<String, Object> left = ActionParamSlotsSupport.defaultValues(before, means, objectMapper);
        Map<String, Object> right = ActionParamSlotsSupport.defaultValues(after, means, objectMapper);
        Set<String> codes = new LinkedHashSet<>();
        codes.addAll(left.keySet());
        codes.addAll(right.keySet());
        codes.remove(ActionDurationParamSupport.FIELD_CODE);
        Set<String> changed = new LinkedHashSet<>();
        for (String code : codes) {
            if (!sameValue(left.get(code), right.get(code))) {
                changed.add(code);
            }
        }
        return changed;
    }

    private Set<String> fieldCodes(Object raw, String means) {
        return new LinkedHashSet<>(ActionParamSlotsSupport.fieldCodes(raw, means, objectMapper));
    }

    private List<String> stepCodes(Object raw, String means) {
        List<String> codes = new ArrayList<>();
        for (TaskStepTreeGenerator.MethodAction action : InspectionMethodSnapshotParser.actionsForMeans(
                raw, means, objectMapper)) {
            codes.add(text(action.refCode()));
        }
        return codes;
    }

    private Set<String> meansKeysOf(Object before, Object after) {
        Set<String> keys = new LinkedHashSet<>();
        keys.addAll(methodKeys(before));
        keys.addAll(methodKeys(after));
        return keys;
    }

    private static Set<String> methodKeys(Object raw) {
        Set<String> keys = new LinkedHashSet<>();
        if (!(raw instanceof Map<?, ?> root)) {
            return keys;
        }
        Object methods = root.get("methods");
        if (!(methods instanceof List<?> list)) {
            return keys;
        }
        for (Object row : list) {
            if (!(row instanceof Map<?, ?> method)) {
                continue;
            }
            String means = text(first(method, "methodKey", "executionMeans"));
            if (StringUtils.hasText(means)) {
                keys.add(means.toUpperCase());
            }
        }
        return keys;
    }

    private List<Map<String, Object>> actionNodes(Object tree) {
        List<Map<String, Object>> actions = new ArrayList<>();
        for (Map<String, Object> node : nodesByKey(tree).values()) {
            if ("action".equals(text(node.get("hangTypeCode")))) {
                actions.add(node);
            }
        }
        return actions;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Map<String, Object>> nodesByKey(Object tree) {
        Map<String, Map<String, Object>> byKey = new LinkedHashMap<>();
        if (!(tree instanceof Map<?, ?> root)) {
            return byKey;
        }
        Object nodes = root.get("nodes");
        if (!(nodes instanceof List<?> list)) {
            return byKey;
        }
        for (Object raw : list) {
            if (!(raw instanceof Map<?, ?> row)) {
                continue;
            }
            Map<String, Object> node = (Map<String, Object>) row;
            String key = text(node.get("nodeKey"));
            if (StringUtils.hasText(key)) {
                byKey.put(key, node);
            }
        }
        return byKey;
    }

    private static Long ancestorRefId(
            Map<String, Object> node, Map<String, Map<String, Object>> byKey, String hangType
    ) {
        String parent = text(node.get("parentNodeKey"));
        int guard = 0;
        while (StringUtils.hasText(parent) && guard++ < byKey.size()) {
            Map<String, Object> current = byKey.get(parent);
            if (current == null) {
                return null;
            }
            if (hangType.equals(text(current.get("hangTypeCode")))) {
                return longOf(current.get("refId"));
            }
            parent = text(current.get("parentNodeKey"));
        }
        return null;
    }

    private static boolean sameAction(Map<String, Object> node, String actionCode, Long actionId) {
        if (StringUtils.hasText(actionCode) && actionCode.trim().equals(text(node.get("refCode")))) {
            return true;
        }
        Long refId = longOf(node.get("refId"));
        return actionId != null && actionId.equals(refId);
    }

    private static boolean isFilled(Object raw) {
        if (raw == null) {
            return false;
        }
        if (raw instanceof String text) {
            return StringUtils.hasText(text);
        }
        return true;
    }

    private static boolean sameValue(Object left, Object right) {
        if (left == null && right == null) {
            return true;
        }
        if (left instanceof Number && right instanceof Number) {
            return new BigDecimal(left.toString()).compareTo(new BigDecimal(right.toString())) == 0;
        }
        return text(left).equals(text(right));
    }

    private Object parseJson(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        try {
            return objectMapper.readValue(raw, Object.class);
        } catch (Exception ex) {
            return raw;
        }
    }

    private static String normalizeJson(String raw) {
        return raw == null ? "" : raw.trim();
    }

    private static String labelOf(String name, String code, String fallback) {
        if (StringUtils.hasText(name)) {
            return name.trim();
        }
        if (StringUtils.hasText(code)) {
            return code.trim();
        }
        return fallback;
    }

    private static String text(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    private static Object first(Map<?, ?> row, String... keys) {
        for (String key : keys) {
            if (row.containsKey(key) && row.get(key) != null) {
                return row.get(key);
            }
        }
        return null;
    }

    private static Long longOf(Object raw) {
        if (raw instanceof Number number && number.longValue() > 0) {
            return number.longValue();
        }
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                long value = Long.parseLong(text.trim());
                return value > 0 ? value : null;
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}
