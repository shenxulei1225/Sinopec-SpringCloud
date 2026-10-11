package cn.cheers.x.module.dynamicbusiness.service.analysis.result;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.analysis.AnalysisJobDO;
import cn.cheers.x.module.dynamicbusiness.service.execution.TaskExecutionSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 把标准分析结果写回任务步骤。
 *
 * <p>businessRefId 固定为“执行记录 id:步骤编码”。本适配不读取检查项或动作标题。</p>
 */
@Component
@RequiredArgsConstructor
public class TaskStepAnalysisResultSink implements AnalysisJobResultSink {

    public static final String BUSINESS_REF_TYPE = "TASK_STEP";

    private final TaskExecutionSessionService taskExecutionSessionService;

    @Override
    public boolean supports(String businessRefType) {
        return BUSINESS_REF_TYPE.equals(businessRefType);
    }

    @Override
    public void deliver(AnalysisJobDO job, Map<String, Object> normalizedResult) {
        String reference = job.getBusinessRefId();
        int split = reference == null ? -1 : reference.indexOf(':');
        if (split <= 0 || split >= reference.length() - 1) {
            throw new ServiceException(400, "分析任务的任务步骤引用无效");
        }
        long executionRecordId;
        try {
            executionRecordId = Long.parseLong(reference.substring(0, split));
        } catch (NumberFormatException ex) {
            throw new ServiceException(400, "分析任务的执行记录编号无效");
        }
        String stepCode = reference.substring(split + 1).trim();
        if (!StringUtils.hasText(stepCode)) {
            throw new ServiceException(400, "分析任务的步骤编码为空");
        }

        String verdict = String.valueOf(normalizedResult.getOrDefault("verdict", "FAILED"));
        Map<String, Object> payload = new LinkedHashMap<>(normalizedResult);
        payload.put("analysisJobNo", job.getJobNo());
        payload.put("analysisCapabilityCode", job.getCapabilityCodeSnapshot());
        payload.put("analysisCapabilityVersion", job.getCapabilityVersionSnapshot());
        payload.put("analysisResult", switch (verdict) {
            case "NORMAL" -> "正常";
            case "ALARM" -> "告警";
            default -> "失败";
        });

        TaskExecutionWritebackReqDTO request = new TaskExecutionWritebackReqDTO();
        request.setExecutionRecordId(executionRecordId);
        request.setEntityTypeCode("task_record_patrol");
        TaskExecutionWritebackReqDTO.StepUpdate update = new TaskExecutionWritebackReqDTO.StepUpdate();
        update.setStepCode(stepCode);
        update.setStatus("FAILED".equals(verdict) ? "failed" : "completed");
        update.setResultPayload(payload);
        request.setStepUpdates(List.of(update));
        taskExecutionSessionService.writeback(request);
    }
}
