package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.framework.common.exception.ServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskStepTreeAssemblerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void assemble_skipsHumanAndIdentify_usesInspectedItemPack() {
        List<TaskStepNode> nodes = List.of(
                action("n-move", 1, null, 11L, "act-arrive", "到达指定位置", Map.of("location_ref", "SHOULD_NOT_USE")),
                item("n-item", 2, null, 7L, "检查阀"),
                action("n-photo", 3, "n-item", 22L, "act-photo", "拍照", Map.of()),
                action("n-human", 4, "n-item", 33L, "act-human", "人工确认", Map.of()),
                action("n-ai", 5, "n-item", 44L, "act-ai", "智能识别", Map.of())
        );
        HostSopParamPack pack = HostSopParamPack.parse(Map.of(
                "version", 1,
                "entries", List.of(Map.of(
                        "subjectId", 7,
                        "paramsByNode", Map.of("n-photo", Map.of("shot_count", 3))
                ))
        ), mapper);
        Function<Long, HostSopParamPack> packs = itemId ->
                Long.valueOf(7L).equals(itemId) ? pack : HostSopParamPack.empty();

        TaskStepTreeAssembler.AssembledOpenRun result = TaskStepTreeAssembler.assemble(
                nodes, packs, code -> Optional.empty());

        assertEquals(2, result.dispatchActions().size());
        assertEquals(11L, result.dispatchActions().get(0).actionId());
        assertTrue(result.dispatchActions().get(0).params().isEmpty());
        assertEquals(22L, result.dispatchActions().get(1).actionId());
        assertEquals(3, result.dispatchActions().get(1).params().get("shot_count"));
        assertEquals("seq-0", result.bindings().get(0).stepCode());
        assertEquals("seq-1", result.bindings().get(1).stepCode());
        assertEquals(7L, result.bindings().get(1).inspectionItemId());
        assertEquals("任务准备", result.sessionSteps().get(0).getName());
        assertEquals("检查阀", result.sessionSteps().get(1).getName());
        assertFalse((Boolean) result.sessionSteps().get(4).getSource().get("dispatched"));
        assertTrue((Boolean) result.sessionSteps().get(2).getSource().get("dispatched"));
    }

    @Test
    void assemble_resolvesActionCode_whenRefIdMissing() {
        List<TaskStepNode> nodes = List.of(
                action("n-move", 1, null, null, "act-arrive", "到达指定位置", Map.of())
        );

        TaskStepTreeAssembler.AssembledOpenRun result = TaskStepTreeAssembler.assemble(
                nodes, itemId -> HostSopParamPack.empty(), code -> Optional.of(99L));

        assertEquals(99L, result.dispatchActions().get(0).actionId());
    }

    @Test
    void assemble_emptyTree_throws() {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> TaskStepTreeAssembler.assemble(List.of(), itemId -> HostSopParamPack.empty(), code -> Optional.empty()));
        assertTrue(ex.getMessage().contains("没有执行步骤图"));
    }

    @Test
    void assemble_onlyHuman_throws() {
        List<TaskStepNode> nodes = List.of(
                action("n-human", 1, null, 33L, "act-human", "人工确认", Map.of())
        );
        ServiceException ex = assertThrows(ServiceException.class,
                () -> TaskStepTreeAssembler.assemble(nodes, itemId -> HostSopParamPack.empty(), code -> Optional.empty()));
        assertTrue(ex.getMessage().contains("没有可下发的动作"));
    }

    @Test
    void assemble_unresolvedAction_throws() {
        List<TaskStepNode> nodes = List.of(
                action("n-move", 1, null, null, "act-missing", "到达指定位置", Map.of())
        );
        ServiceException ex = assertThrows(ServiceException.class,
                () -> TaskStepTreeAssembler.assemble(nodes, itemId -> HostSopParamPack.empty(), code -> Optional.empty()));
        assertTrue(ex.getMessage().contains("对不上动作库"));
    }

    private static TaskStepNode action(
            String key, int order, String parent, Long refId, String refCode, String title, Map<String, Object> params
    ) {
        return new TaskStepNode(key, order, parent, TaskStepNode.HANG_ACTION, refCode, refId, title, params);
    }

    private static TaskStepNode item(String key, int order, String parent, Long refId, String title) {
        return new TaskStepNode(key, order, parent, TaskStepNode.HANG_INSPECTION_ITEM, null, refId, title, Map.of());
    }
}
