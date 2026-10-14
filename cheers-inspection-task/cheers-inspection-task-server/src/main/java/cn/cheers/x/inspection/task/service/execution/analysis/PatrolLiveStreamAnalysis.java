package cn.cheers.x.inspection.task.service.execution.analysis;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.intelligentanalysis.api.IntelligentAnalysisApi;
import cn.cheers.x.intelligentanalysis.api.dto.AnalysisJobSubmitReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStepSnapshotDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 连续视频检查：把流里刚抽出的一张图片提交给整次任务上的分析步骤。
 * <p>只提交标明整次任务的分析步骤。检查点上的点拍摄分析不在这里送。
 * 媒体类型仍是图片。不解码视频，不按动作名称挑选算法。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatrolLiveStreamAnalysis {

    private final TaskExecutionSessionApi sessionApi;
    private final IntelligentAnalysisApi intelligentAnalysisApi;

    public static final String SPAN_TASK = "TASK";
    public static final String BUSINESS_REF = "TASK_STREAM";

    public boolean hasTaskSpan(Long recordId) {
        return !taskSpanSteps(recordId).isEmpty();
    }

    public void close(Long recordId) {
        for (TaskExecutionStepSnapshotDTO step : taskSpanSteps(recordId)) {
            if (step.getStepStatus() != null && "completed".equals(step.getStepStatus())) {
                continue;
            }
            Map<String, Object> payload = step.getResultPayload() == null
                    ? new java.util.LinkedHashMap<>()
                    : new java.util.LinkedHashMap<>(step.getResultPayload());
            if (!payload.containsKey("analysisResult")) {
                payload.put("analysisResult", "正常");
            }
            write(recordId, step.getStepCode(), "completed", payload);
        }
    }

    public void submitFrame(Long recordId, String imageUrl) {
        if (recordId == null || !StringUtils.hasText(imageUrl)) {
            return;
        }
        for (TaskExecutionStepSnapshotDTO step : taskSpanSteps(recordId)) {
            Long capabilityId = capabilityId(step);
            if (capabilityId == null) {
                continue;
            }
            submitOne(recordId, step, capabilityId, imageUrl);
        }
    }

    private List<TaskExecutionStepSnapshotDTO> taskSpanSteps(Long recordId) {
        CommonResult<List<TaskExecutionStepSnapshotDTO>> listed = sessionApi.listSteps(recordId);
        if (listed == null || !listed.isSuccess() || listed.getData() == null) {
            return List.of();
        }
        return taskSpanSteps(listed.getData());
    }

    private static List<TaskExecutionStepSnapshotDTO> taskSpanSteps(List<TaskExecutionStepSnapshotDTO> steps) {
        List<TaskExecutionStepSnapshotDTO> matched = new java.util.ArrayList<>();
        for (TaskExecutionStepSnapshotDTO step : steps) {
            if (capabilityId(step) != null && StringUtils.hasText(step.getStepCode())) {
                matched.add(step);
            }
        }
        return matched;
    }

    private void submitOne(
            Long recordId, TaskExecutionStepSnapshotDTO step, Long capabilityId, String imageUrl) {
        AnalysisJobSubmitReqDTO request = new AnalysisJobSubmitReqDTO();
        request.setAnalysisCapabilityId(capabilityId);
        request.setInputType("IMAGE");
        request.setInputRef(imageUrl);
        request.setBusinessRefType(BUSINESS_REF);
        request.setBusinessRefId(recordId + ":" + step.getStepCode());
        request.setParameters(Map.of("pointId", String.valueOf(step.getId())));
        try {
            intelligentAnalysisApi.submitJob(request);
        } catch (RuntimeException ex) {
            log.info("[live-frame] 这一帧没有提交到分析 recordId={} step={} reason={}",
                    recordId, step.getStepCode(), ex.getMessage());
        }
    }

    private void write(Long recordId, String stepCode, String status, Map<String, Object> payload) {
        cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO request =
                new cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO();
        request.setExecutionRecordId(recordId);
        request.setEntityTypeCode("task_record_patrol");
        cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO.StepUpdate update =
                new cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO.StepUpdate();
        update.setStepCode(stepCode);
        update.setStatus(status);
        update.setResultPayload(payload);
        request.setStepUpdates(List.of(update));
        sessionApi.writeback(request);
    }

    private static Long capabilityId(TaskExecutionStepSnapshotDTO step) {
        if (step == null || step.getSource() == null) {
            return null;
        }
        if (!"INTELLIGENT_ANALYSIS".equals(String.valueOf(step.getSource().get("actionDomain")))) {
            return null;
        }
        if (!SPAN_TASK.equals(String.valueOf(step.getSource().get("analysisSpan")))) {
            return null;
        }
        Object raw = step.getSource().get("analysisCapabilityId");
        if (raw instanceof Number number) {
            long value = number.longValue();
            return value > 0 ? value : null;
        }
        if (raw == null) {
            return null;
        }
        try {
            long value = Long.parseLong(String.valueOf(raw).trim());
            return value > 0 ? value : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
