package cn.cheers.x.inspection.task.api.dto;

import lombok.Data;

/**
 * 这次往来有没有记进执行记录。
 * <p>没记上时 reason 说明原因。调用方不得改成已记上。
 */
@Data
public class TaskExchangeTranscriptRespDTO {

    private boolean recorded;

    private String reason;
}
