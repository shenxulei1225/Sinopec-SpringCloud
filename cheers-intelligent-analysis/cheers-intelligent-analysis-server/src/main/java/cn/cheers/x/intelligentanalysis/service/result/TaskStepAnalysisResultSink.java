package cn.cheers.x.intelligentanalysis.service.result;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.intelligentanalysis.dal.dataobject.AnalysisCapabilityDO;
import cn.cheers.x.intelligentanalysis.dal.dataobject.AnalysisJobDO;
import cn.cheers.x.intelligentanalysis.dal.mysql.AnalysisCapabilityMapper;
import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStepSnapshotDTO;
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
 * 本适配不读取检查项或动作标题，也不直接写动态业务的表。
 * 告警交给巡检时带上分析能力名称，以及任务里已有照片的地址。不另存照片文件。</p>
 */
@Component
@RequiredArgsConstructor
public class TaskStepAnalysisResultSink implements AnalysisJobResultSink {

    public static final String BUSINESS_REF_TYPE = "TASK_STEP";
    /** 连续视频检查。一帧的结果不结束这一步，整次任务结束才关。 */
    public static final String TASK_STREAM_REF_TYPE = "TASK_STREAM";

    private final TaskExecutionSessionApi taskExecutionSessionApi;
    private final AnalysisCapabilityMapper capabilityMapper;

    /** 这一步已经告警过。后面的正常帧不把告警抹掉。 */
    private boolean alreadyAlarm(long executionRecordId, String stepCode) {
        CommonResult<List<TaskExecutionStepSnapshotDTO>> listed = taskExecutionSessionApi.listSteps(executionRecordId);
        if (listed == null || !listed.isSuccess() || listed.getData() == null) {
            return false;
        }
        for (TaskExecutionStepSnapshotDTO step : listed.getData()) {
            if (step == null || !stepCode.equals(step.getStepCode()) || step.getResultPayload() == null) {
                continue;
            }
            return "告警".equals(String.valueOf(step.getResultPayload().get("analysisResult")));
        }
        return false;
    }

    @Override
    public boolean supports(String businessRefType) {
        return BUSINESS_REF_TYPE.equals(businessRefType) || TASK_STREAM_REF_TYPE.equals(businessRefType);
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
        String analysisResult = switch (verdict) {
            case "NORMAL" -> "正常";
            case "ALARM" -> "告警";
            default -> "失败";
        };
        if ("正常".equals(analysisResult) && alreadyAlarm(executionRecordId, stepCode)) {
            analysisResult = "告警";
        }
        AnalysisCapabilityDO capability = capabilityMapper.selectById(job.getCapabilityId());
        if (capability == null || !StringUtils.hasText(capability.getName())) {
            throw new ServiceException(400, "分析能力没有名称，不能把结果交回巡检");
        }
        Map<String, Object> payload = new LinkedHashMap<>(normalizedResult);
        payload.put("analysisJobNo", job.getJobNo());
        payload.put("analysisCapabilityCode", job.getCapabilityCodeSnapshot());
        payload.put("analysisCapabilityVersion", job.getCapabilityVersionSnapshot());
        payload.put("analysisCapabilityName", capability.getName().trim());
        payload.put("photoUrl", job.getInputRef());
        payload.put("analysisResult", analysisResult);
        if ("告警".equals(analysisResult)) {
            verdict = "ALARM";
        }

        TaskExecutionWritebackReqDTO request = new TaskExecutionWritebackReqDTO();
        request.setExecutionRecordId(executionRecordId);
        request.setEntityTypeCode("task_record_patrol");
        TaskExecutionWritebackReqDTO.StepUpdate update = new TaskExecutionWritebackReqDTO.StepUpdate();
        update.setStepCode(stepCode);
        boolean taskStream = TASK_STREAM_REF_TYPE.equals(job.getBusinessRefType());
        update.setStatus(taskStream ? "in_progress" : ("FAILED".equals(verdict) ? "failed" : "completed"));
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
