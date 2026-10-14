package cn.cheers.x.inspection.task.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 一次设备往来的原文。
 * <p>设备编号是连接上已经绑定的那一个。报文正文里没有设备编号时也用它。
 * <p>不负责：在这里决定记进哪一条执行记录。
 */
@Data
public class TaskExchangeTranscriptReqDTO {

    /** 平台发出。 */
    public static final String OUTBOUND = "outbound";

    /** 设备发来。 */
    public static final String INBOUND = "inbound";

    @NotBlank
    private String deviceId;

    /** {@link #INBOUND} 或 {@link #OUTBOUND}。 */
    @NotBlank
    private String direction;

    @NotBlank
    private String payloadJson;

    /** 空着时由记入的一侧填当前时刻。 */
    private Long receivedAtEpochMs;

    /**
     * 发送侧已经用事务编号对上这次执行时才填。
     * 报文里的任务 id 或飞行编号仍优先。没有这两处时才用它。
     */
    private Long executionRecordId;
}
