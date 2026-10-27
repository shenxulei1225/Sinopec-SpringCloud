package cn.cheers.x.inspection.task.service.execution;

import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.service.execution.impl.InspectionDeviceUplinkServiceImpl;
import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionMarkProcessReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionMarkProcessRespDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 把回执记进这次执行的过程账。
 * <p>负责：回执只改已下发那一行的状态。
 * <p>不负责：把这一行发给页面、填下发报文、判断步骤完成、从设备编号猜执行记录。
 * <p>禁止：账写上之后再查主题来推送；把报文原文写进过程；回执另造一行；没有执行记录编号还记一条。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutionEventLedger {

    static final String RECEIVED = "设备已接收";

    private final TaskExecutionSessionApi taskExecutionSessionApi;

    /**
     * 指令监控读的是报文原文那几行。标准事件不在这里再记一行，避免同一条下发或上报出现两次。
     * 步骤完成、整次状态仍由调用方交给策略。
     */
    public void record(CollectionSample sample) {
        if (sample == null) {
            return;
        }
        log.debug("[patrol-event] 标准事件不另记过程 messageKind={} executionRecordId={}",
                sample.messageKind(), sample.executionRecordId());
    }

    /**
     * 设备回执只改已下发那一行。没有这条下发事件就记错误，不新造一行。
     */
    public void markReceived(Long executionRecordId, String msgId) {
        if (executionRecordId == null || executionRecordId <= 0 || !StringUtils.hasText(msgId)) {
            log.error("[patrol-event] 回执没有执行记录编号或消息编号，不能标成设备已接收");
            return;
        }
        if (!mark(executionRecordId, msgId.trim(), RECEIVED, null, null)) {
            log.error("[patrol-event] 回执对不上已下发事件 executionRecordId={} msgId={}",
                    executionRecordId, msgId);
        }
    }

    /**
     * 回执不带字段。同号答卷带上业务字段，写回已有的那一行。
     */
    private boolean mark(
            Long executionRecordId,
            String msgId,
            String status,
            String outcome,
            Map<String, Object> fields
    ) {
        TaskExecutionMarkProcessReqDTO req = new TaskExecutionMarkProcessReqDTO();
        req.setExecutionRecordId(executionRecordId);
        req.setEntityTypeCode(InspectionDeviceUplinkServiceImpl.PATROL_RECORD_TYPE);
        req.setMsgId(msgId);
        req.setEventStatus(status);
        req.setOutcome(outcome);
        req.setFields(fields);
        CommonResult<TaskExecutionMarkProcessRespDTO> result = taskExecutionSessionApi.markProcessEvent(req);
        TaskExecutionMarkProcessRespDTO row = result == null ? null : result.getData();
        return result != null && result.isSuccess() && row != null && Boolean.TRUE.equals(row.getUpdated());
    }
}
