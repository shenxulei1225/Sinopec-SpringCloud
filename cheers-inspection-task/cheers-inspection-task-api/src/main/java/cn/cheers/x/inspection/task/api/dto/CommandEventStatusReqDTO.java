package cn.cheers.x.inspection.task.api.dto;

import lombok.Data;

/**
 * 设备回执对上的那一次下发。
 * <p>只带执行记录编号和消息编号。不带报文原文。
 */
@Data
public class CommandEventStatusReqDTO {

    private Long executionRecordId;

    private String msgId;
}
