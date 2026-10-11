package cn.cheers.x.inspection.task.service.execution.analysis;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.service.execution.impl.InspectionDeviceUplinkServiceImpl;
import cn.cheers.x.intelligentanalysis.api.IntelligentAnalysisApi;
import cn.cheers.x.intelligentanalysis.api.dto.AnalysisJobRespDTO;
import cn.cheers.x.intelligentanalysis.api.dto.AnalysisJobSubmitReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStepSnapshotDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyTriggerEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 拍照上报后，把图片提交给同一检查项下的全部智能分析动作。
 *
 * <p>动作快照中的能力编号是唯一调用依据。每个分析动作创建独立任务，结果只写回对应步骤。</p>
 * <p>不负责选择算法、读取动作标题或维护设备协议支持关系。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatrolPhotoAnalysisService {

    private final TaskExecutionSessionApi sessionApi;
    private final IntelligentAnalysisApi intelligentAnalysisApi;

    public void submitAfterReported(StrategyTriggerEventDTO event) {
        if (event == null || event.getExecutionRecordId() == null || event.getStepUpdates() == null) {
            return;
        }
        for (Map<String, Object> update : event.getStepUpdates()) {
            if (PatrolPhotoAnalysisPlanner.isPhotoReport(update)) {
                submitAll(event.getExecutionRecordId(), String.valueOf(update.get("stepCode")), update);
            }
        }
    }

    private void submitAll(Long recordId, String photoStepCode, Map<String, Object> update) {
        List<TaskExecutionStepSnapshotDTO> steps = loadSteps(recordId);
        Object rawPayload = update.get("resultPayload");
        Map<String, Object> payload = rawPayload instanceof Map<?, ?> map ? copyMap(map) : Map.of();
        for (PatrolPhotoAnalysisPlanner.Decision decision
                : PatrolPhotoAnalysisPlanner.decideAll(steps, photoStepCode, payload)) {
            if (decision instanceof PatrolPhotoAnalysisPlanner.Decision.Hold hold) {
                writeHold(recordId, hold.stepCode(), hold.reason());
            } else {
                submitJob(recordId, (PatrolPhotoAnalysisPlanner.Decision.Send) decision);
            }
        }
    }

    private void submitJob(Long recordId, PatrolPhotoAnalysisPlanner.Decision.Send send) {
        AnalysisJobSubmitReqDTO request = new AnalysisJobSubmitReqDTO();
        request.setAnalysisCapabilityId(send.analysisCapabilityId());
        request.setInputType("IMAGE");
        request.setInputRef(send.imageUrl());
        request.setBusinessRefType("TASK_STEP");
        request.setBusinessRefId(recordId + ":" + send.stepCode());
        request.setParameters(Map.of("pointId", String.valueOf(send.stepId())));
        CommonResult<AnalysisJobRespDTO> submitted = intelligentAnalysisApi.submitJob(request);
        if (submitted == null || !submitted.isSuccess() || submitted.getData() == null
                || "FAILED".equals(submitted.getData().getStatus())) {
            Map<String, Object> failed = new LinkedHashMap<>();
            failed.put("analysisResult", "失败");
            failed.put("display", submitted != null && submitted.getData() != null
                    ? textOr(submitted.getData().getFailureMessage(), "智能分析服务未受理")
                    : "智能分析服务未受理");
            failed.put("imageUrl", send.imageUrl());
            writeStep(recordId, send.stepCode(), "failed", failed);
            return;
        }
        if (alreadyVerdict(recordId, send.stepCode())) {
            return;
        }
        Map<String, Object> running = new LinkedHashMap<>();
        running.put("analysisResult", "分析中");
        running.put("imageUrl", send.imageUrl());
        running.put("analysisJobNo", submitted.getData().getJobNo());
        writeStep(recordId, send.stepCode(), "in_progress", running);
    }

    private boolean alreadyVerdict(Long recordId, String stepCode) {
        TaskExecutionStepSnapshotDTO step = findStep(recordId, stepCode);
        return step != null && PatrolPhotoAnalysisPlanner.verdictOf(step.getResultPayload())
                .filter(verdict -> "正常".equals(verdict) || "告警".equals(verdict))
                .isPresent();
    }

    private void writeHold(Long recordId, String stepCode, String reason) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("analysisResult", "暂不执行");
        payload.put("display", reason);
        writeStep(recordId, stepCode, "skipped", payload);
    }

    private TaskExecutionStepSnapshotDTO findStep(Long recordId, String stepCode) {
        for (TaskExecutionStepSnapshotDTO step : loadSteps(recordId)) {
            if (step != null && stepCode.equals(step.getStepCode())) {
                return step;
            }
        }
        return null;
    }

    private List<TaskExecutionStepSnapshotDTO> loadSteps(Long recordId) {
        CommonResult<List<TaskExecutionStepSnapshotDTO>> listed = sessionApi.listSteps(recordId);
        if (listed == null || !listed.isSuccess() || listed.getData() == null) {
            log.error("[intelligent-analysis] 读取执行步骤失败 recordId={}", recordId);
            return List.of();
        }
        return listed.getData();
    }

    private void writeStep(Long recordId, String stepCode, String status, Map<String, Object> payload) {
        TaskExecutionWritebackReqDTO request = new TaskExecutionWritebackReqDTO();
        request.setExecutionRecordId(recordId);
        request.setEntityTypeCode(InspectionDeviceUplinkServiceImpl.PATROL_RECORD_TYPE);
        TaskExecutionWritebackReqDTO.StepUpdate update = new TaskExecutionWritebackReqDTO.StepUpdate();
        update.setStepCode(stepCode);
        update.setStatus(status);
        update.setResultPayload(payload);
        request.setStepUpdates(List.of(update));
        CommonResult<Boolean> written = sessionApi.writeback(request);
        if (written == null || !written.isSuccess()) {
            log.error("[intelligent-analysis] 写回分析步骤失败 recordId={} stepCode={} status={}",
                    recordId, stepCode, status);
        }
    }

    private static Map<String, Object> copyMap(Map<?, ?> source) {
        Map<String, Object> copy = new LinkedHashMap<>();
        source.forEach((key, value) -> {
            if (key != null) {
                copy.put(String.valueOf(key), value);
            }
        });
        return copy;
    }

    private static String textOr(String value, String fallback) {
        return StringUtils.hasText(value) ? value.trim() : fallback;
    }
}
