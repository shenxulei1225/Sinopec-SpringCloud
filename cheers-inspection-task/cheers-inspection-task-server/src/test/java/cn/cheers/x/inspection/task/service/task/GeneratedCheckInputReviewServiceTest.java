package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GeneratedCheckInputReviewServiceTest {

    @Test
    void review_modelScopedPhotoSlotsMatchSavedStepTree() {
        PatrolTaskEntityStore store = mock(PatrolTaskEntityStore.class);
        PatrolItemActionDurationCalculator duration = mock(PatrolItemActionDurationCalculator.class);
        when(duration.computeLiveSeconds(any(), any(), any(), any())).thenReturn(Optional.empty());

        GeneratedCheckInputReviewService service = new GeneratedCheckInputReviewService(
                store,
                duration,
                mock(GeneratedCheckInputReviewReadSupport.class),
                new ObjectMapper());

        assertTrue(service.review(draft(), context()).getChanges().isEmpty());
    }

    @Test
    void review_reportsChangedActionEvenWhenModelMatches() {
        PatrolTaskEntityStore store = mock(PatrolTaskEntityStore.class);
        PatrolItemActionDurationCalculator duration = mock(PatrolItemActionDurationCalculator.class);
        when(duration.computeLiveSeconds(any(), any(), any(), any())).thenReturn(Optional.empty());

        GeneratedCheckInputReviewService service = new GeneratedCheckInputReviewService(
                store,
                duration,
                mock(GeneratedCheckInputReviewReadSupport.class),
                new ObjectMapper());

        var changes = service.review(draftWithAction("other-action"), context()).getChanges();
        assertEquals(1, changes.size());
        assertEquals("步骤", changes.get(0).getAspect());
    }

    private static PatrolTaskDraft draft() {
        return draftWithAction("takePhoto");
    }

    private static PatrolTaskDraft draftWithAction(String actionCode) {
        InspectionContent.ItemContent item = new InspectionContent.ItemContent();
        item.setItemId(2354L);
        item.setItemName("罐体无渗漏变形");
        InspectionContent.ObjectContent object = new InspectionContent.ObjectContent();
        object.setObjectId(900105L);
        object.setObjectName("北罐组 2#储罐");
        object.setItems(List.of(item));
        InspectionContent.ObjectGroup group = new InspectionContent.ObjectGroup();
        group.setObjects(List.of(object));
        InspectionContent content = new InspectionContent();
        content.setGroups(List.of(group));
        ExecutionDeviceBinding binding = new ExecutionDeviceBinding();
        binding.setEquipmentId(8001L);
        return new PatrolTaskDraft(
                9L, "机器人储罐巡检", "巡检", 1L, "ROBOT", content, binding,
                null, stepTree(actionCode), null, null, null, null, null, null, true,
                null, null, null, null, null, null);
    }

    private static Map<String, Object> stepTree(String actionCode) {
        return Map.of(
                "version", 1,
                "nodes", List.of(
                        Map.of("nodeKey", "eq", "hangTypeCode", "equipment", "refId", 900105),
                        Map.of(
                                "nodeKey", "item",
                                "hangTypeCode", "inspection_item",
                                "refId", 2354,
                                "parentNodeKey", "eq"),
                        Map.of(
                                "nodeKey", "act",
                                "hangTypeCode", "action",
                                "parentNodeKey", "item",
                                "refCode", actionCode,
                                "sourceNodeKey", "n3",
                                "order", 1,
                                "declaredSlots", List.of("yaw", "action_duration"),
                                "params", Map.of("yaw", "30", "action_duration", 12))));
    }

    private static EntityRespDTO equipment() {
        EntityRespDTO entity = new EntityRespDTO();
        entity.setId(8001L);
        entity.setBaseFields(Map.of("modelId", 1772));
        return entity;
    }

    private static EntityRespDTO item() {
        EntityRespDTO entity = new EntityRespDTO();
        entity.setId(2354L);
        entity.setBaseFields(Map.of(
                "step_tree_json", Map.of(
                        "version", 1,
                        "methods", List.of(Map.of(
                                "methodKey", "ROBOT",
                                "stepTree", List.of(Map.of(
                                        "order", 1,
                                        "nodeKey", "n3",
                                        "nodeType", "STEP",
                                        "actionId", "takePhoto")))))));
        return entity;
    }

    private static EntityRespDTO photoAction() {
        EntityRespDTO entity = new EntityRespDTO();
        entity.setBaseFields(Map.of(
                "code", "takePhoto",
                "param_slots_json", Map.of(
                        "version", 2,
                        "methods", List.of(
                                Map.of(
                                        "executionMeans", "ROBOT",
                                        "paramsPerEquipmentModel", true,
                                        "fields", List.of()),
                                Map.of(
                                        "executionMeans", "ROBOT",
                                        "equipmentModelId", 1772,
                                        "fields", List.of(
                                                Map.of("fieldCode", "yaw", "defaultValue", "30"),
                                                Map.of("fieldCode", "action_duration", "defaultValue", 12)))))));
        return entity;
    }

    private static GeneratedCheckInputReviewReadContext context() {
        return new GeneratedCheckInputReviewReadContext(
                Map.of(8001L, equipment()),
                Map.of(2354L, item()),
                Map.of("takePhoto", photoAction()),
                Map.of(900105L, Set.of(2354L)),
                new ObjectMapper());
    }
}
