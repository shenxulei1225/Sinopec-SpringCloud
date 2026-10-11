package cn.cheers.x.inspection.task.service.execution.analysis;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.intelligentanalysis.api.IntelligentAnalysisApi;
import cn.cheers.x.intelligentanalysis.api.dto.AnalysisJobRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStepSnapshotDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyTriggerEventDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PatrolPhotoAnalysisServiceTest {

    private TaskExecutionSessionApi sessionApi;
    private IntelligentAnalysisApi intelligentAnalysisApi;
    private PatrolPhotoAnalysisService service;

    @BeforeEach
    void setUp() {
        sessionApi = mock(TaskExecutionSessionApi.class);
        intelligentAnalysisApi = mock(IntelligentAnalysisApi.class);
        service = new PatrolPhotoAnalysisService(sessionApi, intelligentAnalysisApi);
    }

    @Test
    void deviceAction_isNotSentToAnalysis() {
        when(sessionApi.listSteps(9L)).thenReturn(CommonResult.success(List.of(
                step(1L, "item-1", "外观检查", null, "pending"),
                step(2L, "seq-0", "拍照", 1L, "completed"),
                step(3L, "device-step", "设备动作", 1L, "pending"))));

        service.submitAfterReported(photoEvent(9L, "seq-0", "https://cdn.example/a.jpg"));

        verify(intelligentAnalysisApi, never()).submitJob(any());
    }

    @Test
    void analysisAction_sendsPhotoAndMarksAnalysisRunning() {
        when(sessionApi.listSteps(9L)).thenReturn(CommonResult.success(List.of(
                step(1L, "item-1", "跑冒滴漏检查", null, "pending"),
                step(2L, "seq-0", "拍照", 1L, "completed"),
                analysisStep(3L, "analysis-step", "任意标题", 1L, "pending"))));
        AnalysisJobRespDTO submitted = new AnalysisJobRespDTO();
        submitted.setJobNo("analysis-9");
        submitted.setStatus("SUBMITTED");
        when(intelligentAnalysisApi.submitJob(any())).thenReturn(CommonResult.success(submitted));
        when(sessionApi.writeback(any())).thenReturn(CommonResult.success(true));

        service.submitAfterReported(photoEvent(9L, "seq-0", "https://cdn.example/a.jpg"));

        TaskExecutionWritebackReqDTO written = captureWriteback();
        assertEquals("analysis-step", written.getStepUpdates().get(0).getStepCode());
        assertEquals("in_progress", written.getStepUpdates().get(0).getStatus());
        assertEquals("分析中", written.getStepUpdates().get(0).getResultPayload().get("analysisResult"));
    }

    @Test
    void missingImage_marksNotExecuted() {
        when(sessionApi.listSteps(9L)).thenReturn(CommonResult.success(List.of(
                step(1L, "item-1", "跑冒滴漏检查", null, "pending"),
                step(2L, "seq-0", "拍照", 1L, "completed"),
                analysisStep(3L, "analysis-step", "任意标题", 1L, "pending"))));
        when(sessionApi.writeback(any())).thenReturn(CommonResult.success(true));

        service.submitAfterReported(photoEvent(9L, "seq-0", ""));

        TaskExecutionWritebackReqDTO written = captureWriteback();
        assertEquals("skipped", written.getStepUpdates().get(0).getStatus());
        assertEquals("暂不执行", written.getStepUpdates().get(0).getResultPayload().get("analysisResult"));
        verify(intelligentAnalysisApi, never()).submitJob(any());
    }

    @Test
    void onePhoto_submitsEveryAnalysisActionUnderTheItem() {
        when(sessionApi.listSteps(9L)).thenReturn(CommonResult.success(List.of(
                step(1L, "item-1", "设备检查", null, "pending"),
                step(2L, "seq-0", "拍照", 1L, "completed"),
                analysisStep(3L, "analysis-a", "分析 A", 1L, "pending"),
                analysisStep(4L, "analysis-b", "分析 B", 1L, "pending"))));
        AnalysisJobRespDTO submitted = new AnalysisJobRespDTO();
        submitted.setJobNo("analysis-job");
        submitted.setStatus("RUNNING");
        when(intelligentAnalysisApi.submitJob(any())).thenReturn(CommonResult.success(submitted));
        when(sessionApi.writeback(any())).thenReturn(CommonResult.success(true));

        service.submitAfterReported(photoEvent(9L, "seq-0", "https://cdn.example/a.jpg"));

        verify(intelligentAnalysisApi, times(2)).submitJob(any());
        ArgumentCaptor<TaskExecutionWritebackReqDTO> captor =
                ArgumentCaptor.forClass(TaskExecutionWritebackReqDTO.class);
        verify(sessionApi, times(2)).writeback(captor.capture());
        assertEquals(
                List.of("analysis-a", "analysis-b"),
                captor.getAllValues().stream()
                        .map(value -> value.getStepUpdates().get(0).getStepCode())
                        .toList());
    }

    @Test
    void planner_ignoresSettledAnalysis() {
        TaskExecutionStepSnapshotDTO analysis =
                analysisStep(3L, "analysis-step", "任意标题", 1L, "completed");
        analysis.setResultPayload(Map.of("analysisResult", "正常"));
        List<PatrolPhotoAnalysisPlanner.Decision> decisions = PatrolPhotoAnalysisPlanner.decideAll(
                List.of(
                        step(1L, "item-1", "跑冒滴漏检查", null, "pending"),
                        step(2L, "seq-0", "拍照", 1L, "completed"),
                        analysis),
                "seq-0",
                Map.of("result", Map.of("url", "https://cdn.example/a.jpg")));
        assertTrue(decisions.isEmpty());
        assertTrue(PatrolPhotoAnalysisPlanner.isPhotoReport(Map.of(
                "status", "completed",
                "resultPayload", Map.of("opcode", 200301))));
    }

    private TaskExecutionWritebackReqDTO captureWriteback() {
        ArgumentCaptor<TaskExecutionWritebackReqDTO> captor =
                ArgumentCaptor.forClass(TaskExecutionWritebackReqDTO.class);
        verify(sessionApi).writeback(captor.capture());
        return captor.getValue();
    }

    private static StrategyTriggerEventDTO photoEvent(long recordId, String stepCode, String imageUrl) {
        StrategyTriggerEventDTO event = new StrategyTriggerEventDTO();
        event.setExecutionRecordId(recordId);
        Map<String, Object> result = imageUrl == null || imageUrl.isBlank()
                ? Map.of()
                : Map.of("url", imageUrl);
        event.setStepUpdates(List.of(Map.of(
                "stepCode", stepCode,
                "status", "completed",
                "resultPayload", Map.of("opcode", 200301, "result", result))));
        return event;
    }

    private static TaskExecutionStepSnapshotDTO step(
            long id, String code, String title, Long parentId, String status) {
        TaskExecutionStepSnapshotDTO step = new TaskExecutionStepSnapshotDTO();
        step.setId(id);
        step.setStepCode(code);
        step.setStepTitle(title);
        step.setParentId(parentId);
        step.setStepStatus(status);
        step.setResultPayload(Map.of());
        return step;
    }

    private static TaskExecutionStepSnapshotDTO analysisStep(
            long id, String code, String title, Long parentId, String status) {
        TaskExecutionStepSnapshotDTO step = step(id, code, title, parentId, status);
        step.setSource(Map.of(
                "actionDomain", "INTELLIGENT_ANALYSIS",
                "analysisCapabilityId", 41L));
        return step;
    }
}
