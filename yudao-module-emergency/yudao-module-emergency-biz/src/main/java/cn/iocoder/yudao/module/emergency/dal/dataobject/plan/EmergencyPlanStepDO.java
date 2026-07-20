package cn.iocoder.yudao.module.emergency.dal.dataobject.plan;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.*;

import java.util.List;

/**
 * 应急预案步骤 DO
 *
 * @author 芋道源码
 */
@TableName(value = "emergency_plan_step", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyPlanStepDO extends TenantBaseDO {

    /**
     * 编号
     * 步骤的唯一标识符，自增主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 关联预案ID
     * 指向所属应急预案的ID，用于建立步骤与预案的关联关系
     */
    private Long planId;

    /**
     * 预案级别
     * 标识步骤所属的应急预案级别，如一级响应、二级响应等
     *
     * 枚举 {@link cn.iocoder.yudao.module.emergency.enums.dict.EmergencyDictTypeConstants#EMERGENCY_PLAN_LEVEL}
     * 明确标识步骤所属的预案级别
     */
    private String planLevel;

    /**
     * 父步骤ID
     * 用于构建步骤的层级结构，支持多级步骤嵌套
     * 若为顶级步骤，则该字段为null
     */
    private Long parentId;

    /**
     * 步骤序号
     * 在同级步骤中的执行顺序编号，从1开始递增
     * 用于确定步骤的执行先后顺序
     */
    private Integer stepOrder;

    /**
     * 步骤标题
     * 步骤的简短标题名称，便于快速识别步骤内容
     */
    private String stepTitle;

    /**
     * 步骤阶段
     * 标识步骤所属的应急处置阶段，如：预警阶段、响应阶段、恢复阶段等
     */
    private String stepStage;

    /**
     * 步骤描述
     * 对步骤的详细说明，包括具体的执行内容、方法和要求
     */
    private String stepDescription;

    /**
     * 计划启动时间（分钟）
     * 步骤计划从应急事件发生后多少分钟开始执行
     * 用于时间节点的控制和调度
     */
    private Integer scheduledStartTime;

    /**
     * 负责人岗位ID
     * 执行该步骤负责人的岗位标识
     * 与组织架构中的岗位信息关联
     */
    private Long responsiblePostId;

    /**
     * 负责人部门ID
     * 执行该步骤负责人的部门标识
     * 与组织架构中的部门信息关联
     */
    private Long responsibleDeptId;

    /**
     * 负责人用户ID
     * 执行该步骤的具体负责人用户标识
     * 指向系统用户表，用于精确指定执行人员
     */
    private Long responsibleUserId;

    /**
     * 兼容新任务克隆字段：名称/责任角色/资源/联系人/计划开始时间
     * 以下字段用于兼容新版本的任务克隆功能，提供更灵活的任务定义
     */

    /**
     * 任务名称
     * 步骤的别名或更详细的名称标识
     * 用于任务克隆时的显示和识别
     */
    private String name;

    /**
     * 责任角色
     * 执行该步骤所需承担的具体角色描述
     * 补充岗位信息，提供更详细的角色定义
     */
    private String responsibleRole;

    /**
     * 资源
     * 执行该步骤所需的资源清单
     * 包括人力、物资、设备等各类资源需求
     */
    private String resources;

    /**
     * 联系人
     * 步骤执行过程中的关键联系人信息
     * 包括电话、邮箱等联系方式
     */
    private String contacts;

    /**
     * 计划开始时间
     * 步骤的精确计划开始时间点
     * 提供比scheduledStartTime更精确的时间控制
     */
    private java.time.LocalDateTime plannedStartTime;

    /**
     * 关联指令ID列表（可选，JSONB类型）
     * 关联到应急指令模板列表，用于在预案步骤中指定可执行的指令（支持单个或多个）
     * 当设置时必须验证所有指令ID的有效性（存在且未删除）
     * 预案步骤可以关联多个应急指令，执行人员可以快速找到对应的指令内容和执行要求
     * 单个指令时，列表只包含一个元素；多个指令时，列表包含多个元素
     */
    @TableField(value = "command", typeHandler = cn.iocoder.yudao.module.emergency.framework.mybatis.LongListJsonbTypeHandler.class)
    private List<Long> commandIdList;

    /**
     * 执行时限（分钟，可选）
     * 建议的执行时限，范围1-1440分钟（24小时）
     * 作为强制要求传递给基于该步骤创建的任务，任务必须在此时限内完成
     */
    private Integer timeLimit;

    /**
     * 版本号（乐观锁字段，用于并发控制）
     * 每次更新时自动递增，用于检测并发修改冲突
     * 如果更新时版本号不匹配，说明记录已被其他用户修改，需要提示用户刷新后重试
     */
    @Version
    private Integer version;
}

