package cn.iocoder.yudao.module.emergency.controller.admin.plan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预案步骤创建 Request VO
 * <p>
 * 用于创建新的预案步骤，支持创建根步骤或子步骤。
 * 包含了步骤的基本信息、负责人信息以及兼容性字段。
 *
 * @author 芋道源码
 */
@Schema(description = "管理后台 - 预案步骤创建 Request VO")
@Data
public class PlanStepCreateReqVO {

    @Schema(description = "预案ID - 步骤所属的应急预案标识，必填。注意：如果ID是很大的整数（超过2^53），前端应传递字符串类型以避免JavaScript精度丢失", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "预案ID不能为空")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongDeserializer.class)
    private Long planId;

    @Schema(description = "预案级别 - 标识步骤所属的应急预案级别，如I/II/III/IV/V，可选", example = "I")
    private String planLevel;

    @Schema(description = "父步骤ID - 用于构建步骤的层级结构，不填则创建根步骤。注意：如果ID是很大的整数（超过2^53），前端应传递字符串类型以避免JavaScript精度丢失", example = "1024")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongDeserializer.class)
    private Long parentId;

    @Schema(description = "步骤序号 - 在同级步骤中的执行顺序，不填则自动计算", example = "1")
    private Integer stepOrder;

    @Schema(description = "步骤标题 - 步骤的简短标题名称，必填", requiredMode = Schema.RequiredMode.REQUIRED, example = "启动应急响应")
    @NotBlank(message = "步骤标题不能为空")
    private String stepTitle;

    @Schema(description = "步骤阶段 - 标识步骤所属的应急处置阶段，如预警阶段、响应阶段、恢复阶段", example = "响应阶段")
    private String stepStage;

    @Schema(description = "步骤描述 - 对步骤的详细说明，包括具体的执行内容、方法和要求", example = "立即启动应急响应程序")
    private String stepDescription;

    @Schema(description = "计划启动时间（分钟）- 步骤计划从应急事件发生后多少分钟开始执行", example = "0")
    private Integer scheduledStartTime;

    @Schema(description = "负责人岗位ID - 执行该步骤负责人的岗位标识，与组织架构关联。注意：如果ID是很大的整数（超过2^53），前端应传递字符串类型以避免JavaScript精度丢失", example = "1024")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongDeserializer.class)
    private Long responsiblePostId;

    @Schema(description = "负责人部门ID - 执行该步骤负责人的部门标识，与组织架构关联。注意：如果ID是很大的整数（超过2^53），前端应传递字符串类型以避免JavaScript精度丢失", example = "1024")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongDeserializer.class)
    private Long responsibleDeptId;

    @Schema(description = "负责人用户ID - 执行该步骤的具体负责人用户标识，精确指定执行人员。注意：如果ID是很大的整数（超过2^53），前端应传递字符串类型以避免JavaScript精度丢失", example = "1024")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongDeserializer.class)
    private Long responsibleUserId;

    // ==================== 兼容字段 ====================

    @Schema(description = "任务名称 - 步骤的别名或更详细的名称标识，用于任务克隆功能", example = "启动应急响应")
    private String name;

    @Schema(description = "计划开始时间 - 步骤的精确计划开始时间点，提供比scheduledStartTime更精确的时间控制")
    private LocalDateTime plannedStartTime;

    @Schema(description = "责任角色 - 执行该步骤所需承担的具体角色描述，补充岗位信息", example = "应急指挥")
    private String responsibleRole;

    @Schema(description = "资源清单 - 执行该步骤所需的资源清单，JSON格式，包括人力、物资、设备等", example = "[]")
    private String resources;

    @Schema(description = "联系人信息 - 步骤执行过程中的关键联系人信息，JSON格式，包含电话、邮箱等", example = "[]")
    private String contacts;

    @Schema(description = "关联指令ID列表（可选）- 关联到应急指令模板列表，用于在预案步骤中指定可执行的指令（支持单个或多个）。当设置时必须验证所有指令ID的有效性（存在且未删除）。预案步骤可以关联多个应急指令，执行人员可以快速找到对应的指令内容和执行要求。单个指令时，列表只包含一个元素；多个指令时，列表包含多个元素。注意：大整数ID应使用字符串格式传递，避免JavaScript精度丢失", example = "[\"1024\", \"1025\", \"1026\"]")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongListDeserializer.class)
    private List<Long> commandIdList;

    @Schema(description = "执行时限（分钟，可选）- 建议的执行时限，范围1-1440分钟（24小时）。填写时必须验证，不符合范围要求时系统必须拒绝保存并提示用户", example = "60")
    private Integer timeLimit;
}


