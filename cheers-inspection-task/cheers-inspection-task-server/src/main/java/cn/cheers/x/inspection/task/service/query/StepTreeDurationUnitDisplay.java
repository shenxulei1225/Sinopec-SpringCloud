package cn.cheers.x.inspection.task.service.query;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.service.execution.steptree.HostSopParamPack;
import cn.cheers.x.inspection.task.service.execution.steptree.PatrolTaskStepTreeReader;
import cn.cheers.x.inspection.task.service.execution.steptree.RoutePointCode;
import cn.cheers.x.inspection.task.service.execution.steptree.StepParamModelScope;
import cn.cheers.x.inspection.task.service.task.ActionDurationParamSupport;
import cn.cheers.x.inspection.task.service.task.ActionParamSlotsSupport;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 打开任务时，在返回的步骤副本上补展示字段，不写回任务。
 * 动作上少了这一档已经声明的参数时，按生成步骤同一规则列上：设备填过的用设备上的，没填的用动作库默认，默认也空仍列出。
 * 动作耗时跟生成步骤同一档型号：执行设备对得上用这一档，对不上用挂载里唯一一个适用型号。没有或不止一个就不填。不从设备参数包取。步骤上已经写下的值保留。
 * 动作上没写下秒 / 分 / 小时的，按动作库这一档补单位。
 * 到达位置、选择位置若只写下了点位编号，把这些编号收成一份，一次批量读出名称放到返回的这一格。读不到的仍是编号。
 * 起点、终点按点位业务编码读到那一条，副本上带上点位编号和名称，标题写成「起点 · 名称」。
 * 不负责：改任务里存的步骤、占窗、重新生成步骤树、扫点位列表。
 */
@Component
@RequiredArgsConstructor
public class StepTreeDurationUnitDisplay {

    static final String ACTION_TYPE = "action";
    static final String POINT_TYPE = "point";
    /** 到达位置、选择位置。格子里的数字是点位编号。 */
    private static final List<String> LOCATION_FIELDS = List.of(
            "location_ref", "F-e49f76bdca3e4e1393e8e2f1cc0e7867");

    private final EntityRpcApi entityRpcApi;
    private final ObjectMapper objectMapper;
    private final PatrolTaskStepTreeReader stepTreeReader;

    /**
     * @param facilityId 任务所属设施。用来按业务编码读起点、终点对应的点位；没有则不补名称。
     */
    @SuppressWarnings("unchecked")
    public Object withDisplayUnits(Object stepTree, String means, Long facilityId) {
        return withDisplayUnits(stepTree, means, facilityId, null);
    }

    /**
     * @param executionEquipmentId 这次任务选中的执行设备。用来对上按型号分开的动作耗时。没有则不拿挂载型号来填。
     */
    public Object withDisplayUnits(Object stepTree, String means, Long facilityId, Long executionEquipmentId) {
        if (!(stepTree instanceof Map<?, ?> tree)) {
            return stepTree;
        }
        Object nodesRaw = tree.get("nodes");
        if (!(nodesRaw instanceof List<?> nodes)) {
            return stepTree;
        }
        boolean hasMeans = StringUtils.hasText(means);
        boolean hasFacility = facilityId != null && facilityId > 0;
        List<Object> copiedNodes = new ArrayList<>();
        Map<String, Map<String, Object>> byKey = new LinkedHashMap<>();
        for (Object raw : nodes) {
            if (!(raw instanceof Map<?, ?> row)) {
                copiedNodes.add(raw);
                continue;
            }
            Map<String, Object> node = new LinkedHashMap<>((Map<String, Object>) row);
            copiedNodes.add(node);
            String key = text(node.get("nodeKey"));
            if (StringUtils.hasText(key)) {
                byKey.put(key, node);
            }
        }
        boolean changed = false;
        if (hasMeans) {
            Map<String, EntityRespDTO> actions = new HashMap<>();
            Map<Long, HostSopParamPack> packs = new HashMap<>();
            Long executorModelId = modelIdOf(executionEquipmentId);
            List<Long> mountedModelIds = mountedModelIds(executionEquipmentId);
            for (Object raw : copiedNodes) {
                if (raw instanceof Map<?, ?> row
                        && fillDeclaredParams(
                                (Map<String, Object>) row, means.trim(), byKey, actions, packs,
                                executorModelId, mountedModelIds)) {
                    changed = true;
                }
            }
        }
        Map<Long, EntityRespDTO> points = pointsById(collectPointIds(copiedNodes));
        if (!changed && !hasFacility && points.isEmpty()) {
            return stepTree;
        }
        for (Object raw : copiedNodes) {
            if (!(raw instanceof Map<?, ?> row)) {
                continue;
            }
            Map<String, Object> node = (Map<String, Object>) row;
            if (hasMeans && fillUnit(node, means.trim(), executionEquipmentId)) {
                changed = true;
            }
            if (hasFacility && fillPointName(node, facilityId)) {
                changed = true;
            }
            if (fillLocationNames(node, points)) {
                changed = true;
            }
        }
        if (!changed) {
            return stepTree;
        }
        Map<String, Object> copy = new LinkedHashMap<>((Map<String, Object>) tree);
        copy.put("nodes", copiedNodes);
        return copy;
    }

    /**
     * 这一步少了动作库当前巡检方式已经声明的参数时，补到返回的副本上。
     * 设备这一格填过就用设备上的，没填就用动作库默认；默认也空仍列出。步骤上已经有的值留下。
     * 动作耗时只读动作库，不从设备参数包取。不改任务里存的步骤。
     */
    private boolean fillDeclaredParams(
            Map<String, Object> node,
            String means,
            Map<String, Map<String, Object>> byKey,
            Map<String, EntityRespDTO> actions,
            Map<Long, HostSopParamPack> packs,
            Long executorModelId,
            List<Long> mountedModelIds
    ) {
        if (!"action".equals(text(node.get("hangTypeCode")))) {
            return false;
        }
        String code = text(node.get("refCode"));
        EntityRespDTO action = actionOf(actions, code);
        if (action == null) {
            return false;
        }
        Object slots = bagOf(action).get("param_slots_json");
        Long durationModelId = durationModelId(slots, means, executorModelId, mountedModelIds);
        List<String> declared = ActionParamSlotsSupport.fieldCodes(slots, means, durationModelId, objectMapper);
        if (declared.isEmpty()) {
            return false;
        }
        Map<String, Object> defaults = ActionParamSlotsSupport.defaultValues(
                slots, means, durationModelId, objectMapper);
        Map<String, Object> equipmentValues = equipmentValues(node, code, means, byKey, packs);
        Map<String, Object> merged = paramsCopy(node.get("params"));
        boolean changed = false;
        for (String slot : declared) {
            if (isFilled(merged.get(slot))) {
                continue;
            }
            Object value = "";
            if (!ActionDurationParamSupport.FIELD_CODE.equals(slot)) {
                Object equipment = equipmentValues.get(slot);
                if (isFilled(equipment)) {
                    value = equipment;
                } else if (isFilled(defaults.get(slot))) {
                    value = defaults.get(slot);
                }
            }
            if (!merged.containsKey(slot) || !Objects.equals(merged.get(slot), value)) {
                merged.put(slot, value);
                changed = true;
            }
        }
        Optional<ActionDurationParamSupport.SlotDuration> duration =
                ActionDurationParamSupport.readSlot(slots, means, durationModelId, objectMapper);
        if (duration.isPresent() && !isFilled(merged.get(ActionDurationParamSupport.FIELD_CODE))) {
            merged.put(ActionDurationParamSupport.FIELD_CODE, duration.get().seconds());
            changed = true;
        }
        if (changed) {
            node.put("params", merged);
        }
        return changed;
    }

    private Map<String, Object> equipmentValues(
            Map<String, Object> node,
            String actionCode,
            String means,
            Map<String, Map<String, Object>> byKey,
            Map<Long, HostSopParamPack> packs
    ) {
        Long itemId = ancestorRefId(node, byKey, "inspection_item");
        Long equipmentId = ancestorRefId(node, byKey, "equipment");
        if (itemId == null || equipmentId == null) {
            return Map.of();
        }
        HostSopParamPack pack = packs.computeIfAbsent(equipmentId, stepTreeReader::loadHostPackOrEmpty);
        if (pack == null) {
            return Map.of();
        }
        return pack.paramsFor(itemId, text(node.get("sourceNodeKey")), actionCode, means);
    }

    private EntityRespDTO actionOf(Map<String, EntityRespDTO> actions, String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        if (actions.containsKey(code)) {
            return actions.get(code);
        }
        EntityRespDTO action = load(code);
        actions.put(code, action);
        return action;
    }

    private static Long ancestorRefId(
            Map<String, Object> node,
            Map<String, Map<String, Object>> byKey,
            String hangType
    ) {
        String parentKey = text(node.get("parentNodeKey"));
        int guard = 0;
        while (StringUtils.hasText(parentKey) && guard++ < byKey.size() + 1) {
            Map<String, Object> parent = byKey.get(parentKey);
            if (parent == null) {
                return null;
            }
            if (hangType.equals(text(parent.get("hangTypeCode")))) {
                return longOrNull(parent.get("refId"));
            }
            parentKey = text(parent.get("parentNodeKey"));
        }
        return null;
    }

    private static Map<String, Object> paramsCopy(Object raw) {
        Map<String, Object> copy = new LinkedHashMap<>();
        if (raw instanceof Map<?, ?> params) {
            for (Map.Entry<?, ?> entry : params.entrySet()) {
                if (entry.getKey() != null) {
                    copy.put(String.valueOf(entry.getKey()), entry.getValue());
                }
            }
        }
        return copy;
    }

    private static boolean isFilled(Object raw) {
        if (raw == null) {
            return false;
        }
        if (raw instanceof String text) {
            return !text.isBlank();
        }
        return true;
    }

    private static Map<String, Object> bagOf(EntityRespDTO entity) {
        Map<String, Object> bag = new LinkedHashMap<>();
        if (entity.getBaseFields() != null) {
            bag.putAll(entity.getBaseFields());
        }
        if (entity.getCustomFields() != null) {
            bag.putAll(entity.getCustomFields());
        }
        return bag;
    }

    /**
     * 到达位置、选择位置只写下点位编号时，用这一次批量读到的点位，把名称放到返回的这一格。
     * 不改任务上存的编号。这批里没有该点位时，留下编号。
     */
    @SuppressWarnings("unchecked")
    private boolean fillLocationNames(Map<String, Object> node, Map<Long, EntityRespDTO> points) {
        if (points.isEmpty() || !"action".equals(text(node.get("hangTypeCode")))) {
            return false;
        }
        Object paramsRaw = node.get("params");
        if (!(paramsRaw instanceof Map<?, ?> params)) {
            return false;
        }
        Map<String, Object> copied = null;
        for (String field : LOCATION_FIELDS) {
            if (!params.containsKey(field)) {
                continue;
            }
            Object raw = copied == null ? params.get(field) : copied.get(field);
            if (StringUtils.hasText(nameOn(raw))) {
                continue;
            }
            Long id = pointIdOf(raw);
            EntityRespDTO point = id == null ? null : points.get(id);
            String name = nameOf(point);
            if (!StringUtils.hasText(name)) {
                continue;
            }
            if (copied == null) {
                copied = new LinkedHashMap<>((Map<String, Object>) paramsRaw);
            }
            Map<String, Object> cell = new LinkedHashMap<>();
            cell.put("id", id);
            String code = codeOf(point);
            if (StringUtils.hasText(code)) {
                cell.put("code", code);
            }
            cell.put("name", name);
            copied.put(field, cell);
        }
        if (copied == null) {
            return false;
        }
        node.put("params", copied);
        return true;
    }

    private Set<Long> collectPointIds(List<?> nodes) {
        Set<Long> ids = new LinkedHashSet<>();
        for (Object raw : nodes) {
            if (!(raw instanceof Map<?, ?> row) || !"action".equals(text(row.get("hangTypeCode")))) {
                continue;
            }
            Object paramsRaw = row.get("params");
            if (!(paramsRaw instanceof Map<?, ?> params)) {
                continue;
            }
            for (String field : LOCATION_FIELDS) {
                Object value = params.get(field);
                if (StringUtils.hasText(nameOn(value))) {
                    continue;
                }
                Long id = pointIdOf(value);
                if (id != null) {
                    ids.add(id);
                }
            }
        }
        return ids;
    }

    /** 一次按点位编号批量读取。没有编号就不查。 */
    private Map<Long, EntityRespDTO> pointsById(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        List<EntityRespDTO> rows = dataList(entityRpcApi.listEntitiesByIds(new ArrayList<>(ids), POINT_TYPE));
        Map<Long, EntityRespDTO> byId = new LinkedHashMap<>();
        for (EntityRespDTO row : rows) {
            if (row != null && row.getId() != null) {
                byId.put(row.getId(), row);
            }
        }
        return byId;
    }

    /**
     * 按型号分开时，先对执行设备，对不上再用挂载里唯一一个适用型号。没选设备时不改用别的型号。
     */
    private Long durationModelId(
            Object slots, String means, Long executorModelId, List<Long> mountedModelIds) {
        Long scoped = StepParamModelScope.resolve(slots, means, executorModelId, mountedModelIds, objectMapper);
        return scoped != null ? scoped : executorModelId;
    }

    private Long modelIdOf(Long equipmentId) {
        EntityRespDTO equipment = equipmentOf(equipmentId);
        if (equipment == null) {
            return null;
        }
        return longOrNull(bagOf(equipment).get("modelId"));
    }

    /**
     * 执行设备挂载上的设备型号。读不到挂载就空列表，不编一个型号。
     */
    private List<Long> mountedModelIds(Long executionEquipmentId) {
        EntityRespDTO host = equipmentOf(executionEquipmentId);
        if (host == null) {
            return List.of();
        }
        Object raw = bagOf(host).get("payload_mounts");
        Object value = raw;
        if (raw instanceof String text && !text.isBlank()) {
            try {
                value = objectMapper.readValue(text, new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {
                });
            } catch (Exception ex) {
                return List.of();
            }
        }
        if (!(value instanceof List<?> rows)) {
            return List.of();
        }
        List<Long> modelIds = new ArrayList<>();
        for (Object row : rows) {
            if (!(row instanceof Map<?, ?> map)) {
                continue;
            }
            Long childId = longOrNull(map.get("equipmentId"));
            EntityRespDTO child = equipmentOf(childId);
            if (child == null) {
                continue;
            }
            Long modelId = longOrNull(bagOf(child).get("modelId"));
            if (modelId != null && !modelIds.contains(modelId)) {
                modelIds.add(modelId);
            }
        }
        return modelIds;
    }

    private EntityRespDTO equipmentOf(Long equipmentId) {
        if (equipmentId == null || equipmentId <= 0) {
            return null;
        }
        return data(entityRpcApi.getEntity(equipmentId, "equipment"));
    }

    private boolean fillUnit(Map<String, Object> node, String means, Long executionEquipmentId) {
        if (!"action".equals(text(node.get("hangTypeCode")))) {
            return false;
        }
        Object paramsRaw = node.get("params");
        if (!(paramsRaw instanceof Map<?, ?> params) || params.containsKey("durationUnit")) {
            return false;
        }
        if (!params.containsKey(ActionDurationParamSupport.FIELD_CODE)) {
            return false;
        }
        String unit = unitOf(node, means, executionEquipmentId);
        if (!StringUtils.hasText(unit)) {
            return false;
        }
        Map<String, Object> copied = new LinkedHashMap<>((Map<String, Object>) paramsRaw);
        copied.put("durationUnit", unit);
        node.put("params", copied);
        return true;
    }

    private String unitOf(Map<String, Object> node, String means, Long executionEquipmentId) {
        EntityRespDTO action = load(text(node.get("refCode")));
        if (action == null) {
            return null;
        }
        Map<String, Object> bag = new LinkedHashMap<>();
        if (action.getBaseFields() != null) {
            bag.putAll(action.getBaseFields());
        }
        if (action.getCustomFields() != null) {
            bag.putAll(action.getCustomFields());
        }
        Object slots = bag.get("param_slots_json");
        Long durationModelId = durationModelId(
                slots, means, modelIdOf(executionEquipmentId), mountedModelIds(executionEquipmentId));
        return ActionDurationParamSupport.readSlot(slots, means, durationModelId, objectMapper)
                .map(ActionDurationParamSupport.SlotDuration::displayUnit)
                .filter(StringUtils::hasText)
                .orElse(null);
    }

    /** 巡检读动作只认业务编码。返回和打开这条动作相同的档案，含参数清单。 */
    private EntityRespDTO load(String actionCode) {
        if (!StringUtils.hasText(actionCode)) {
            return null;
        }
        return data(entityRpcApi.getEntityByCode(actionCode.trim(), ACTION_TYPE));
    }

    private static List<EntityRespDTO> dataList(CommonResult<List<EntityRespDTO>> result) {
        if (result == null || !result.isSuccess() || result.getData() == null) {
            return List.of();
        }
        return result.getData();
    }

    private static EntityRespDTO data(CommonResult<EntityRespDTO> result) {
        if (result == null || !result.isSuccess()) {
            return null;
        }
        return result.getData();
    }

    private static String text(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    /**
     * 起点、终点的 refCode 是路网节点编号。按同步后的业务编码读点位，把编号和名称写到返回副本上。
     */
    private boolean fillPointName(Map<String, Object> node, long facilityId) {
        if (!"point".equals(text(node.get("hangTypeCode")))) {
            return false;
        }
        String nodeId = text(node.get("refCode"));
        if (!StringUtils.hasText(nodeId)) {
            return false;
        }
        EntityRespDTO point = data(entityRpcApi.getEntityByCode(RoutePointCode.of(facilityId, nodeId), POINT_TYPE));
        if (point == null) {
            return false;
        }
        boolean changed = false;
        if (point.getId() != null && !point.getId().equals(longOrNull(node.get("refId")))) {
            node.put("refId", point.getId());
            changed = true;
        }
        String name = nameOf(point);
        if (!StringUtils.hasText(name)) {
            return changed;
        }
        if (!name.equals(text(node.get("name")))) {
            node.put("name", name);
            changed = true;
        }
        String title = text(node.get("title"));
        String head = title.startsWith("起点") ? "起点" : title.startsWith("终点") ? "终点" : "";
        if (StringUtils.hasText(head)) {
            String next = head + " · " + name;
            if (!next.equals(title)) {
                node.put("title", next);
                changed = true;
            }
        }
        return changed;
    }

    private static Long pointIdOf(Object raw) {
        if (raw instanceof Map<?, ?> obj) {
            return longOrNull(obj.get("id"));
        }
        return longOrNull(raw);
    }

    private static String nameOn(Object raw) {
        if (!(raw instanceof Map<?, ?> obj) || obj.get("name") == null) {
            return "";
        }
        return String.valueOf(obj.get("name")).trim();
    }

    private static String codeOf(EntityRespDTO point) {
        if (point == null || point.getBaseFields() == null || point.getBaseFields().get("code") == null) {
            return "";
        }
        return String.valueOf(point.getBaseFields().get("code")).trim();
    }

    private static Long longOrNull(Object raw) {
        if (raw instanceof Number number && number.longValue() > 0) {
            return number.longValue();
        }
        if (raw instanceof String text && !text.isBlank()) {
            try {
                long value = Long.parseLong(text.trim());
                return value > 0 ? value : null;
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static String nameOf(EntityRespDTO point) {
        if (point == null) {
            return "";
        }
        if (point.getBaseFields() != null && point.getBaseFields().get("name") != null) {
            String name = String.valueOf(point.getBaseFields().get("name")).trim();
            if (StringUtils.hasText(name)) {
                return name;
            }
        }
        if (point.getCustomFields() != null && point.getCustomFields().get("name") != null) {
            String name = String.valueOf(point.getCustomFields().get("name")).trim();
            if (StringUtils.hasText(name)) {
                return name;
            }
        }
        return "";
    }
}
