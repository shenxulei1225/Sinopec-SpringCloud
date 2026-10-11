package cn.cheers.x.module.dynamicbusiness.api.execution.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 一次写下多条待执行账。步骤清单全批共用，每条账只带自己的计划点、计划执行时间和名称。
 */
@Data
public class TaskExecutionStartBatchReqDTO {

    @NotNull
    private Long taskDefinitionId;

    /** 执行记录入口类型；默认 task_excution_record */
    private String entityTypeCode;

    /** 执行记录型号 code（如 exec_patrol_record）；与 modelId 二选一 */
    private String modelCode;

    private Long modelId;

    /**
     * 这批执行账的所属场站。
     * 站场级执行账必须带；巡检从总任务已写入的场站带过来，不从请求头猜。
     */
    private Long facilityId;

    @NotNull
    private Object standardSnapshot;

    private Object parameterSnapshot;

    private List<String> gapCodes;

    @NotEmpty
    @Valid
    private List<TaskExecutionStartReqDTO.StepDraft> steps;

    @NotEmpty
    @Valid
    private List<Occurrence> occurrences;

    @Data
    public static class Occurrence {
        @NotNull
        private String pendingRef;
        /** 给用户看的名称：任务名 + 计划执行时刻，不写计划点编号。 */
        private String name;
        /** 该计划点的计划执行时间（ISO-8601）。写入执行账 custom_fields.planned_start。 */
        private String plannedStart;
    }
}
