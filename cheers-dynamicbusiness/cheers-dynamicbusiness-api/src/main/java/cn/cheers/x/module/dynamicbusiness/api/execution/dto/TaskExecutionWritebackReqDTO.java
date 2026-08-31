package cn.cheers.x.module.dynamicbusiness.api.execution.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 按执行记录 id 回写状态与步骤（人工 / 外部适配器共用）。
 */
@Data
public class TaskExecutionWritebackReqDTO {

    @NotNull
    private Long executionRecordId;

    /** 执行记录入口类型；默认 task_excution_record */
    private String entityTypeCode;

    /** pending / in_progress / completed / fault */
    private String executionStatus;

    @Valid
    private List<StepUpdate> stepUpdates;

    @Data
    public static class StepUpdate {
        /** 与 stepId 二选一 */
        private String stepCode;
        private Long stepId;
        /** pending / in_progress / completed / skipped / failed */
        @NotNull
        private String status;
        private Map<String, Object> resultPayload;
    }
}
