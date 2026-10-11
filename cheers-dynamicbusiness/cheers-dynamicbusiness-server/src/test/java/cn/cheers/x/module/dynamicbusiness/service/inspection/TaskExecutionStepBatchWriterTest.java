package cn.cheers.x.module.dynamicbusiness.service.inspection;

import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapReqVO;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TaskExecutionStepBatchWriterTest {

    @Test
    void stepColumnsIncludeRequiredStepCode() {
        TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO draft =
                new TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO();
        draft.setName("任务准备");
        draft.setStepCode("prepare");
        draft.setStepOrder(0);
        draft.setStepType("prepare");
        draft.setSource(Map.of("nodeKey", "head"));

        Map<String, Object> columns = TaskExecutionStepBatchWriter.stepPhysicalColumns(
                draft, "prepare", 44L, 900L);

        assertEquals("prepare", columns.get("step_code"));
        assertEquals(0, columns.get("step_order"));
        assertEquals("pending", columns.get("step_status"));
        assertEquals(true, columns.get("step_required"));
        assertEquals(44L, columns.get("facility_id"));
        assertEquals(900L, columns.get("execution_record_id"));
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) columns.get("result_payload");
        assertEquals(Map.of("nodeKey", "head"), payload.get("source"));
    }

    @Test
    void missingSourceStillKeepsResultColumn() {
        TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO draft =
                new TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO();
        draft.setName("拍照");
        draft.setStepCode("shoot");
        draft.setStepOrder(2);

        Map<String, Object> columns = TaskExecutionStepBatchWriter.stepPhysicalColumns(
                draft, "shoot", 44L, 900L);

        assertEquals("shoot", columns.get("step_code"));
        assertNull(columns.get("result_payload"));
    }
}
