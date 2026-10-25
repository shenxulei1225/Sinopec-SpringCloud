package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.execution.steptree.ActionGroupMemberParser;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GeneratedCheckInputReviewReadSupportTest {

    @Test
    void load_batchesCurrentPageEntitiesAndRelations() {
        EntityRpcApi api = mock(EntityRpcApi.class);
        ActionGroupMemberParser groupParser = mock(ActionGroupMemberParser.class);
        when(groupParser.parse(any())).thenReturn(List.of());
        when(api.listEntitiesByIds(any(), eq("equipment")))
                .thenReturn(CommonResult.success(List.of(entity(8001L, Map.of("modelId", 1772)))));
        when(api.listEntitiesByIds(any(), eq("inspection_item")))
                .thenReturn(CommonResult.success(List.of(item())));
        when(api.listEntitiesByCodes(List.of("takePhoto"), "action"))
                .thenReturn(CommonResult.success(List.of(action())));
        when(api.listCounterpartEntityIdsGrouped(any(), eq("equipment"), eq("inspection_item")))
                .thenReturn(CommonResult.success(Map.of(900105L, List.of(2354L))));
        GeneratedCheckInputReviewReadSupport support =
                new GeneratedCheckInputReviewReadSupport(api, new ObjectMapper(), groupParser);

        GeneratedCheckInputReviewReadContext context = support.load(List.of(draft()));

        assertEquals(1772L, context.equipmentModelId(8001L));
        assertEquals(2354L, context.item(2354L).getId());
        assertEquals(1, context.applicableItemIds(900105L).size());
        verify(api).listEntitiesByIds(any(), eq("equipment"));
        verify(api).listEntitiesByIds(any(), eq("inspection_item"));
        verify(api).listEntitiesByCodes(List.of("takePhoto"), "action");
        verify(api).listCounterpartEntityIdsGrouped(any(), eq("equipment"), eq("inspection_item"));
        verify(api, never()).getEntity(any(), any());
        verify(api, never()).getEntityByCode(any(), any());
        verify(api, never()).listCounterpartEntityIds(any(), any(), any());
    }

    private static PatrolTaskDraft draft() {
        InspectionContent.ItemContent item = new InspectionContent.ItemContent();
        item.setItemId(2354L);
        InspectionContent.ObjectContent object = new InspectionContent.ObjectContent();
        object.setObjectId(900105L);
        object.setItems(List.of(item));
        InspectionContent content = new InspectionContent();
        content.setCustomObjects(List.of(object));
        ExecutionDeviceBinding binding = new ExecutionDeviceBinding();
        binding.setEquipmentId(8001L);
        return new PatrolTaskDraft(
                9L, "任务", "巡检", 1L, "ROBOT", content, binding,
                null, Map.of(), null, "enabled", null, null, null, "job-1", true,
                null, null, null, null, null, null);
    }

    private static EntityRespDTO item() {
        return entity(2354L, Map.of(
                "step_tree_json", Map.of(
                        "methods", List.of(Map.of(
                                "methodKey", "ROBOT",
                                "stepTree", List.of(Map.of(
                                        "nodeKey", "n1",
                                        "nodeType", "STEP",
                                        "actionId", "takePhoto")))))));
    }

    private static EntityRespDTO action() {
        return entity(31L, Map.of("code", "takePhoto"));
    }

    private static EntityRespDTO entity(Long id, Map<String, Object> fields) {
        EntityRespDTO entity = new EntityRespDTO();
        entity.setId(id);
        entity.setBaseFields(fields);
        return entity;
    }
}
