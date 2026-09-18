package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskStepTreeGenerateServiceTest {

    @Mock
    private PatrolTaskEntityStore patrolTaskEntityStore;
    @Mock
    private EntityRpcTaskStepTreeCatalog catalog;
    @Mock
    private EntityRpcApi entityRpcApi;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();
    @InjectMocks
    private TaskStepTreeGenerateService service;

    @Test
    void generate_putsLoadedRobotMethodsIntoTree() {
        InspectionContent.ItemContent item = new InspectionContent.ItemContent();
        item.setItemId(2354L);
        item.setItemName("罐体无渗漏变形（原油储罐）");
        InspectionContent.ObjectContent object = new InspectionContent.ObjectContent();
        object.setObjectId(10L);
        object.setObjectName("北罐区1#储罐");
        object.setItems(List.of(item));
        InspectionContent content = new InspectionContent();
        content.setCustomObjects(List.of(object));

        when(patrolTaskEntityStore.require(1L)).thenReturn(new PatrolTaskDraft(
                1L, "巡检", "patrol", 44L, "ROBOT", content,
                null, null, null,
                Map.of("startStopId", "pad-a", "endStopId", "pad-b"),
                "draft", 1, "pad-a", "pad-b"));

        EntityRespDTO itemEntity = new EntityRespDTO();
        itemEntity.setId(2354L);
        itemEntity.setBaseFields(Map.of(
                "name", "罐体无渗漏变形（原油储罐）",
                "step_tree_json", Map.of(
                        "version", 1,
                        "methods", List.of(Map.of(
                                "executionMeans", "ROBOT",
                                "actionTree", List.of(Map.of(
                                        "nodeKey", "n1",
                                        "actionId", "act-robot-shoot",
                                        "stepTitle", "拍照",
                                        "nodeType", "STEP")))))));
        when(entityRpcApi.getEntity(2354L, "inspection_item"))
                .thenReturn(CommonResult.success(itemEntity));
        when(catalog.resolveActionId("act-robot-shoot")).thenReturn(Optional.of(22L));
        when(catalog.resolveActionId("act-robot-head")).thenReturn(Optional.of(1L));
        when(catalog.resolveActionId("act-robot-tail")).thenReturn(Optional.of(2L));

        service.generate(1L, List.of(10L), "pad-a", "pad-b");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> tree = ArgumentCaptor.forClass(Map.class);
        verify(patrolTaskEntityStore).writeStepTree(eq(1L), tree.capture());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) tree.getValue().get("nodes");
        assertFalse(nodes.isEmpty());
        assertEquals(2354L, nodes.stream()
                .filter(node -> "item-2354-10".equals(node.get("nodeKey")))
                .findFirst()
                .orElseThrow()
                .get("refId"));
    }
}
