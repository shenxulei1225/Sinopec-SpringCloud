package cn.cheers.x.intelligentanalysis.service.result;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.intelligentanalysis.dal.dataobject.AnalysisJobDO;
import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 把标准分析结果写回任务步骤。
 *
 * <p>businessRefId 固定为“执行记录 id:步骤编码”。结果通过任务执行会话的远程接口交回。
 * 本适配不读取检查项或动作标题，也不直接写动态业务的表。</p>
 */
@Component
@RequiredArgsConstructor
public class TaskStepAnalysisResultSink implements AnalysisJobResultSink {

    public static final String BUSINESS_REF_TYPE = "TASK_STEP";

    private final TaskExecutionSessionApi taskExecutionSessionApi;

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
        CommonResult<Boolean> written = taskExecutionSessionApi.writeback(request);
        if (written == null || !written.isSuccess()) {
            throw new ServiceException(500, written != null && StringUtils.hasText(written.getMsg())
                    ? written.getMsg()
                    : "分析结果没有写回任务步骤");
        }
    }
}
