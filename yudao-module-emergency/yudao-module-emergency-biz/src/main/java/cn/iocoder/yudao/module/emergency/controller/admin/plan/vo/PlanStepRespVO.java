package cn.iocoder.yudao.module.emergency.controller.admin.plan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预案步骤 Response VO
 * <p>
 * 该 VO 包含了预案步骤的完整信息，支持树形结构展示。
 * 所有字段都配备了详细的中文描述，方便前端开发和 API 文档阅读。
 *
 * @author 芋道源码
 */

/**
 * 预案步骤 Response VO
 *
 * @author 芋道源码
 */
@Schema(description = "管理后台 - 预案步骤 Response VO")
@Data
public class PlanStepRespVO {

    @Schema(description = "步骤编号 - 步骤的唯一标识符，自增主键", example = "1024")
    private Long id;

    @Schema(description = "关联预案ID - 指向所属应急预案的ID，用于建立步骤与预案的关联关系", example = "1024")
    private Long planId;

    @Schema(description = "预案级别 - 标识步骤所属的应急预案级别，如一级响应、二级响应等", example = "I")
    private String planLevel;

    @Schema(description = "父步骤ID - 用于构建步骤的层级结构，支持多级步骤嵌套。若为顶级步骤，则该字段为null", example = "1024")
    private Long parentId;

    @Schema(description = "步骤序号 - 在同级步骤中的执行顺序编号，从1开始递增，用于确定步骤的执行先后顺序", example = "1")
    private Integer stepOrder;

    @Schema(description = "步骤标题 - 步骤的简短标题名称，便于快速识别步骤内容", example = "启动应急响应")
    private String stepTitle;

    @Schema(description = "步骤阶段 - 标识步骤所属的应急处置阶段，如：预警阶段、响应阶段、恢复阶段等", example = "响应阶段")
    private String stepStage;

    @Schema(description = "步骤描述 - 对步骤的详细说明，包括具体的执行内容、方法和要求", example = "立即启动应急响应程序")
    private String stepDescription;

    @Schema(description = "计划启动时间（分钟）- 步骤计划从应急事件发生后多少分钟开始执行，用于时间节点的控制和调度", example = "0")
    private Integer scheduledStartTime;

    @Schema(description = "负责人岗位ID - 执行该步骤负责人的岗位标识，与组织架构中的岗位信息关联", example = "1024")
    private Long responsiblePostId;

    @Schema(description = "负责人部门ID - 执行该步骤负责人的部门标识，与组织架构中的部门信息关联", example = "1024")
    private Long responsibleDeptId;

    @Schema(description = "负责人用户ID - 执行该步骤的具体负责人用户标识，指向系统用户表，用于精确指定执行人员", example = "1024")
    private Long responsibleUserId;

    @Schema(description = "任务名称 - 步骤的别名或更详细的名称标识，用于任务克隆时的显示和识别", example = "启动应急响应")
    private String name;

    @Schema(description = "责任角色 - 执行该步骤所需承担的具体角色描述，补充岗位信息，提供更详细的角色定义", example = "应急指挥")
    private String responsibleRole;

    @Schema(description = "资源 - 执行该步骤所需的资源清单，包括人力、物资、设备等各类资源需求", example = "[]")
    private String resources;

    @Schema(description = "联系人 - 步骤执行过程中的关键联系人信息，包括电话、邮箱等联系方式", example = "[]")
    private String contacts;

    @Schema(description = "计划开始时间 - 步骤的精确计划开始时间点，提供比scheduledStartTime更精确的时间控制")
    private LocalDateTime plannedStartTime;

    @Schema(description = "子步骤列表 - 支持多级步骤嵌套的树形结构，包含所有子步骤的详细信息")
    private List<PlanStepRespVO> children;

    @Schema(description = "是否为叶子节点 - 用于前端树形组件展示，标识该步骤是否还有子步骤", example = "true")
    private Boolean leaf;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;

    @Schema(description = "关联指令ID列表（可选）- 关联到应急指令模板列表，用于在预案步骤中指定可执行的指令（支持单个或多个）。预案步骤可以关联多个应急指令，执行人员可以快速找到对应的指令内容和执行要求。单个指令时，列表只包含一个元素；多个指令时，列表包含多个元素", example = "[1024, 1025, 1026]")
    private List<Long> commandIdList;

    @Schema(description = "执行时限（分钟，可选）- 建议的执行时限，范围1-1440分钟（24小时）。作为强制要求传递给基于该步骤创建的任务", example = "60")
    private Integer timeLimit;
}
















