package cn.cheers.x.module.dynamicbusiness.api.execution.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 把已经记下的一条下发事件改成设备已接收或设备已响应。
 * <p>按消息编号找到这一条。找不到就不新造一行。
 * 同号答卷带上业务字段时，写回这一行，页面悬浮才能看到电量、位置、故障说明。
 * 回执不带字段，原来的字段留着。
 */
@Data
public class TaskExecutionMarkProcessReqDTO {

    @NotNull
    private Long executionRecordId;

    private String entityTypeCode;

    @NotBlank
    private String msgId;

    /** 设备已接收，或设备已响应。 */
    @NotBlank
    private String eventStatus;

    /** 设备已响应时的正常或异常。设备已接收不填。 */
    private String outcome;

    /**
     * 同号答卷的业务字段。有内容时覆盖这一行原来的字段。
     * 回执留空。指令阶段不落在这一行上。
     */
    private Map<String, Object> fields;
}
