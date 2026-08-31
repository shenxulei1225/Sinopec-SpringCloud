package cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "开跑：锁定 merge 快照并批量创建步骤实例")
@Data
public class TaskExecutionBootstrapReqVO {

    @NotNull
    @Schema(description = "任务执行记录 id（task_excution_record）")
    private Long executionRecordId;

    @NotNull
    @Schema(description = "锁定的 standard_snapshot JSON 对象")
    private Object standardSnapshot;

    @Schema(description = "参数快照；可与 standardSnapshot 内 params 合并存档")
    private Object parameterSnapshot;

    @Schema(description = "解析阶段已收集的 gapCodes；无缺口传空")
    private List<String> gapCodes;

    @NotEmpty
    @Valid
    private List<TaskExecutionStepDraftVO> steps;

    @Data
    public static class TaskExecutionStepDraftVO {
        @NotNull
        private String name;
        @NotNull
        private String stepCode;
        @NotNull
        private Integer stepOrder;
        private String stepTitle;
        private String stepType;
        private Boolean stepRequired;
        /**
         * 父步骤的 stepCode（可选）。列表须父在前、子在后；
         * bootstrap 按此映射写入实体 parent_id。
         */
        private String parentStepCode;
        private Map<String, Object> source;
    }
}
