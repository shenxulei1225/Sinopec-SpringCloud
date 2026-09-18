package cn.cheers.x.module.dynamicbusiness.api.execution.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 往这次执行的账里记一条过程。
 * <p>必填：账本编号、何时收到、哪类报文、合不合格。没有账本编号不得猜设备。
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
}
