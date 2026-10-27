package cn.cheers.x.module.dynamicbusiness.api.execution.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 往这次执行的账里记一条过程。
 * <p>必填：账本编号、何时收到、哪类报文、合不合格。调用方必须已经选定账本编号。
 * <p>摘要不放报文原文。要回放的原文放在 payloadJson，方向放在 direction。
 */
@Data
public class TaskExecutionAppendProcessReqDTO {

    @NotNull
    private Long executionRecordId;

    /** 账记在哪个目录；巡检传 task_record_patrol */
    private String entityTypeCode;

    @NotNull
    private Long receivedAtEpochMs;

    @NotBlank
    private String messageKind;

    @NotBlank
    private String protocolQualify;

    private List<String> qualifyErrors = new ArrayList<>();

    /** 过程摘要；不要塞原始报文 */
    private String summary;

    /**
     * 这次是平台发出还是设备发来。取值 inbound 或 outbound。
     * 旧的步骤过程可以不填。
     */
    private String direction;

    /** 这一次往来的报文原文，供任务回放。步骤过程可以不填。标准事件不写原文。 */
    private String payloadJson;

    /** 已下发、设备已接收、设备已响应、上报。标准事件才有。 */
    private String eventStatus;

    /** 这条事件对应的动作编号。包级事件可以空着。 */
    private Long actionId;

    /** 和这次下发对上的消息编号。回执用它把状态改到同一条事件上。 */
    private String msgId;

    /** 这条事件属于哪台设备。主题里要填设备编号时用它。没有就不填。 */
    private String deviceId;

    /** 设备已响应时的正常或异常。没有返回码就空着。 */
    private String outcome;

    /** 标准事件的转发字段。不放报文原文。 */
    private Map<String, Object> fields;

    /**
     * 这包上报里已经带上的可播放文件地址。有则写到这次执行的录像地址上。
     * 没有就空着，不编一个地址。
     */
    private String mediaFileUrl;
}
