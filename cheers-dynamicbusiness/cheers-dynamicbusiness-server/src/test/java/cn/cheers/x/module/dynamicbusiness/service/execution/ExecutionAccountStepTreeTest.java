package cn.cheers.x.module.dynamicbusiness.service.execution;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStepSnapshotDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapReqVO;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExecutionAccountStepTreeTest {

    @Test
    void freezeKeepsParentCodeAndSourceOnTheAccount() {
        List<Map<String, Object>> tree = ExecutionAccountStepTree.freeze(List.of(
                draft("检查项", "item", 1, "inspection_item", null),
                draft("拍照", "shoot", 2, "action", "item")));
        assertEquals("item", tree.get(1).get("parentStepCode"));
        assertEquals("head", ((Map<?, ?>) tree.get(0).get("source")).get("nodeKey"));

        Map<String, Object> custom = new LinkedHashMap<>();
        ExecutionAccountStepTree.putFrozen(custom, tree);
        Map<String, Object> results = ExecutionAccountStepTree.mergeResult(
                custom, "shoot", "completed", Map.of("summary", "无泄漏"));
        custom.put(ExecutionAccountStepTree.FIELD_STEP_RESULTS, results);

        List<TaskExecutionStepSnapshotDTO> steps = ExecutionAccountStepTree.project(custom);
        assertEquals("pending", steps.get(0).getStepStatus());
        assertNull(steps.get(0).getResultPayload());
        assertEquals(1L, steps.get(1).getParentId());
        assertEquals("completed", steps.get(1).getStepStatus());
        assertEquals("无泄漏", steps.get(1).getResultPayload().get("summary"));
        assertEquals("检查项", ExecutionAccountStepTree.inspectionItemTitle(custom, "shoot"));
    }

    @Test
    void missingParentFails() {
        ServiceException error = assertThrows(ServiceException.class, () -> ExecutionAccountStepTree.freeze(List.of(
                draft("拍照", "shoot", 1, "action", "missing"))));
        assertEquals(400, error.getCode());
    }

    private static TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO draft(
            String name, String code, int order, String type, String parent) {
        TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO draft =
                new TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO();
        draft.setName(name);
        draft.setStepCode(code);
        draft.setStepOrder(order);
        draft.setStepType(type);
        draft.setParentStepCode(parent);
        draft.setSource(Map.of("nodeKey", "head"));
        return draft;
    }
}
