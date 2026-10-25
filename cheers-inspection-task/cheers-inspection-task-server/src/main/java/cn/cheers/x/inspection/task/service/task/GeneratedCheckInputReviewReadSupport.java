package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.execution.steptree.ActionGroupMemberParser;
import cn.cheers.x.inspection.task.service.execution.steptree.InspectionMethodSnapshotParser;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepTreeGenerator;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRpcDtoSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 为当前任务页批量装入实时核对所需数据。
 *
 * <p>同一页的设备、检查项、动作和适用关系各按集合查询，不在逐条核对中访问远程接口。</p>
 * <p>不负责缓存跨请求数据。标准库发生变化后，下一次列表请求会重新读取。</p>
 */
@Component
@RequiredArgsConstructor
public class GeneratedCheckInputReviewReadSupport {

    private static final String EQUIPMENT_TYPE = "equipment";
    private static final String ITEM_TYPE = "inspection_item";
    private static final String ACTION_TYPE = "action";
    private static final String FIELD_STEP_TREE = "step_tree_json";
    private static final String FIELD_ACTION_TREE = "action_tree_json";

    private final EntityRpcApi entityRpcApi;
    private final ObjectMapper objectMapper;
    private final ActionGroupMemberParser actionGroupMemberParser;

    public GeneratedCheckInputReviewReadContext load(List<PatrolTaskDraft> drafts) {
        Set<Long> equipmentIds = new LinkedHashSet<>();
        Set<Long> itemIds = new LinkedHashSet<>();
        for (PatrolTaskDraft draft : drafts) {
            collectTaskIds(draft, equipmentIds, itemIds);
        }

        Map<Long, EntityRespDTO> equipmentById = indexById(
                listByIds(equipmentIds, EQUIPMENT_TYPE, "读不到任务页设备"));
        Set<Long> mountedIds = new LinkedHashSet<>();
        for (EntityRespDTO equipment : equipmentById.values()) {
            mountedIds.addAll(GeneratedCheckInputReviewReadContext.mountedEquipmentIds(equipment, objectMapper));
        }
        mountedIds.removeAll(equipmentById.keySet());
        equipmentById.putAll(indexById(listByIds(mountedIds, EQUIPMENT_TYPE, "读不到挂载设备")));

        Map<Long, EntityRespDTO> itemById = indexById(
                listByIds(itemIds, ITEM_TYPE, "读不到任务页检查项"));
        Map<String, EntityRespDTO> actionByCode = loadActionClosure(drafts, itemById);
        Map<Long, Set<Long>> applicable = loadApplicable(equipmentIds);
        return new GeneratedCheckInputReviewReadContext(
                equipmentById, itemById, actionByCode, applicable, objectMapper);
    }

    private void collectTaskIds(PatrolTaskDraft draft, Set<Long> equipmentIds, Set<Long> itemIds) {
        if (draft == null) {
            return;
        }
        if (draft.executionDeviceBinding() != null
                && draft.executionDeviceBinding().getEquipmentId() != null) {
            equipmentIds.add(draft.executionDeviceBinding().getEquipmentId());
        }
        for (InspectionContent.ObjectContent object : objectsOf(draft.inspectionContent())) {
            if (object.getObjectId() != null) {
                equipmentIds.add(object.getObjectId());
            }
            if (object.getItems() == null) {
                continue;
            }
            for (InspectionContent.ItemContent item : object.getItems()) {
                if (item != null && item.getItemId() != null) {
                    itemIds.add(item.getItemId());
                }
            }
        }
    }

    private Map<String, EntityRespDTO> loadActionClosure(
            List<PatrolTaskDraft> drafts,
            Map<Long, EntityRespDTO> itemById) {
        Set<String> pending = new LinkedHashSet<>();
        for (PatrolTaskDraft draft : drafts) {
            if (draft == null || !StringUtils.hasText(draft.patrolExecutionMode())) {
                continue;
            }
            for (EntityRespDTO item : itemById.values()) {
                Object raw = methodSnapshotOf(GeneratedCheckInputReviewReadContext.bagOf(item));
                for (TaskStepTreeGenerator.MethodAction action :
                        InspectionMethodSnapshotParser.actionsForMeans(
                                raw, draft.patrolExecutionMode().trim(), objectMapper)) {
                    if (StringUtils.hasText(action.refCode())) {
                        pending.add(action.refCode().trim());
                    }
                }
            }
        }
        Map<String, EntityRespDTO> loaded = new LinkedHashMap<>();
        Set<String> requested = new LinkedHashSet<>();
        while (!pending.isEmpty()) {
            List<String> batch = pending.stream().filter(requested::add).toList();
            pending.clear();
            if (batch.isEmpty()) {
                break;
            }
            for (EntityRespDTO action : listByCodes(batch, ACTION_TYPE, "读不到任务页动作")) {
                String code = EntityRpcDtoSupport.readCode(action);
                if (!StringUtils.hasText(code)) {
                    continue;
                }
                loaded.put(code.trim(), action);
                for (ActionGroupMemberParser.Member member : actionGroupMemberParser.parse(
                        GeneratedCheckInputReviewReadContext.bagOf(action).get(FIELD_ACTION_TREE))) {
                    if (StringUtils.hasText(member.actionCode()) && !requested.contains(member.actionCode().trim())) {
                        pending.add(member.actionCode().trim());
                    }
                }
            }
        }
        return loaded;
    }

    private Map<Long, Set<Long>> loadApplicable(Set<Long> equipmentIds) {
        if (equipmentIds.isEmpty()) {
            return Map.of();
        }
        CommonResult<Map<Long, List<Long>>> result = entityRpcApi.listCounterpartEntityIdsGrouped(
                new ArrayList<>(equipmentIds), EQUIPMENT_TYPE, ITEM_TYPE);
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException("读不到任务页设备适用检查项");
        }
        Map<Long, Set<Long>> grouped = new LinkedHashMap<>();
        equipmentIds.forEach(id -> grouped.put(id, new LinkedHashSet<>()));
        result.getData().forEach((id, values) ->
                grouped.put(id, values == null ? Set.of() : new LinkedHashSet<>(values)));
        return grouped;
    }

    private List<EntityRespDTO> listByIds(Set<Long> ids, String type, String error) {
        if (ids.isEmpty()) {
            return List.of();
        }
        CommonResult<List<EntityRespDTO>> result =
                entityRpcApi.listEntitiesByIds(new ArrayList<>(ids), type);
        return checked(result, error);
    }

    private List<EntityRespDTO> listByCodes(List<String> codes, String type, String error) {
        if (codes.isEmpty()) {
            return List.of();
        }
        return checked(entityRpcApi.listEntitiesByCodes(codes, type), error);
    }

    private static List<EntityRespDTO> checked(
            CommonResult<List<EntityRespDTO>> result,
            String error) {
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException(error);
        }
        return result.getData();
    }

    private static Map<Long, EntityRespDTO> indexById(List<EntityRespDTO> entities) {
        Map<Long, EntityRespDTO> indexed = new LinkedHashMap<>();
        for (EntityRespDTO entity : entities) {
            if (entity != null && entity.getId() != null) {
                indexed.put(entity.getId(), entity);
            }
        }
        return indexed;
    }

    private static Object methodSnapshotOf(Map<String, Object> bag) {
        Object stepTree = bag.get(FIELD_STEP_TREE);
        if (stepTree instanceof String text ? StringUtils.hasText(text) : stepTree != null) {
            return stepTree;
        }
        return bag.get(FIELD_ACTION_TREE);
    }

    private static List<InspectionContent.ObjectContent> objectsOf(InspectionContent content) {
        if (content == null) {
            return List.of();
        }
        List<InspectionContent.ObjectContent> objects = new ArrayList<>();
        if (content.getGroups() != null) {
            for (InspectionContent.ObjectGroup group : content.getGroups()) {
                if (group != null && group.getObjects() != null) {
                    objects.addAll(group.getObjects());
                }
            }
        }
        if (content.getCustomObjects() != null) {
            objects.addAll(content.getCustomObjects());
        }
        return objects;
    }
}
