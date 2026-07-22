package cn.iocoder.yudao.module.emergency.controller.admin.plan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


/**
 * 应急预案步骤基础 VO
 * <p>
 * 包含预案步骤的核心字段定义，被其他步骤VO类继承。
 * 定义了步骤的基本属性、层级关系、执行信息和负责人信息。
 */
@Schema(description = "应急预案步骤基础 VO - 包含步骤的核心字段定义")
@Data
public class EmergencyPlanStepBaseVO {

    @Schema(description = "步骤编号 - 步骤的唯一标识符，更新时需要传递", example = "1001")
    private Long id;

    @Schema(description = "父步骤ID - 用于构建步骤的层级结构，不填表示根步骤", example = "1000")
    private Long parentId;

    @Schema(description = "步骤序号 - 在同级步骤中的执行顺序编号，从1开始递增", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer stepOrder;

    @Schema(description = "步骤标题 - 步骤的简短标题名称，便于快速识别步骤内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "响应启动")
    private String stepTitle;

    @Schema(description = "步骤阶段 - 标识步骤所属的应急处置阶段，如预警、响应、恢复阶段", example = "response_phase")
    private String stepStage;

    @Schema(description = "预案级别 - 标识步骤所属的应急预案级别，如一级响应、二级响应等（响应阶段步骤需要此字段）", example = "I")
    private String planLevel;

    @Schema(description = "步骤描述 - 对步骤的详细说明，包括具体的执行内容、方法和要求", example = "启动应急响应流程")
    private String stepDescription;

    @Schema(description = "计划启动时间（分钟）- 步骤计划从应急事件发生后多少分钟开始执行", example = "0")
    private Integer scheduledStartTime;

    @Schema(description = "负责人岗位ID - 执行该步骤负责人的岗位标识，与组织架构关联", example = "501")
    private Long responsiblePostId;

    @Schema(description = "负责人部门ID - 执行该步骤负责人的部门标识，与组织架构关联", example = "101")
    private Long responsibleDeptId;

    @Schema(description = "负责人用户ID - 执行该步骤的具体负责人用户标识，精确指定执行人员", example = "1")
    private Long responsibleUserId;

    @Schema(description = "关联指令ID列表（可选）- 关联到应急指令模板列表，用于在预案步骤中指定可执行的指令（支持单个或多个）。预案步骤可以关联多个应急指令，执行人员可以快速找到对应的指令内容和执行要求", example = "[1024, 1025, 1026]")
    private List<Long> commandIdList;

    @Schema(description = "执行时限（分钟，可选）- 建议的执行时限，范围1-1440分钟（24小时）。作为强制要求传递给基于该步骤创建的任务，任务必须在此时限内完成", example = "60")
    private Integer timeLimit;

}

















