package cn.cheers.x.module.dynamicbusiness.api.execution.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 开始一次执行会话：创建执行记录并 bootstrap 步骤。
 */
@Data
public class TaskExecutionStartReqDTO {

    @NotNull
    private Long taskDefinitionId;

    /** 执行记录入口类型；默认 task_excution_record */
    private String entityTypeCode;

    /** 执行记录型号 code（如 exec_patrol_round）；与 modelId 二选一 */
    private String modelCode;

    private Long modelId;

    /** 待执行壳 / 计划点引用（选项 A：排期产物） */
    private String pendingRef;

    private String name;

    @NotNull
    private Object standardSnapshot;

    private Object parameterSnapshot;

    private List<String> gapCodes;

    @NotEmpty
    @Valid
    private List<StepDraft> steps;

    @Data
    public static class StepDraft {
        @NotNull
        private String name;
        @NotNull
        private String stepCode;
        @NotNull
        private Integer stepOrder;
        private String stepTitle;
        private String stepType;
        private Boolean stepRequired;
        private String parentStepCode;
        private Map<String, Object> source;
    }
}
