package cn.cheers.x.workorder.dal.dataobject;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.cheers.x.framework.mybatis.core.type.JsonbStringTypeHandler;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 工单 DO
 *
 * @author 工单标准服务
 */
@TableName(value = "wo_work_order", autoResultMap = true)
@KeySequence("wo_work_order_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 工单编号
     */
    private String woNo;

    /**
     * 业务域范围（scope）
     */
    private String scope;

    /**
     * 工单状态：DRAFT / DISPATCHED / IN_PROGRESS / COMPLETED / CANCELLED
     */
    private String status;

    /**
     * 关联资产 ID
     */
    private Long assetId;

    /**
     * 资产类型编码
     */
    private String assetTypeCode;

    /**
     * 频率编码
     */
    private String frequencyCode;

    /**
     * 现场作业标准 ID
     */
    private Long standardId;

    /**
     * 现场作业标准版本号
     */
    private Integer standardVersionNo;

    /**
     * 现场作业标准快照（JSON）
     */
    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String standardSnapshotJson;

    /**
     * 运行时作业 ID
     */
    private String runtimeJobId;

    /**
     * 排程槽位 ID
     */
    private String scheduleSlotId;

    /**
     * 业务键
     */
    private String businessKey;

    /**
     * 派单班组 ID（人员编组分类 id；必选路由单位）
     */
    private Long assigneeTeamId;

    /**
     * 执行人用户 ID（可空 = 班组待领）
     */
    private Long assigneeUserId;

    private Long correctiveCaseId;

    /**
     * 处理时刻。报修派工写入，告诉提出方何时去处理。空着表示这张单不约定到场处理的时刻。
     */
    private java.time.LocalDateTime handlingTime;

    /**
     * 计划开始。只与计划结束同时有值，表示这张工单占维修班的一段档期。
     */
    private java.time.LocalDateTime plannedStartTime;

    /**
     * 计划结束。只与计划开始同时有值。
     */
    private java.time.LocalDateTime plannedFinishTime;

    /**
     * 工单标题
     */
    private String title;

    /**
     * 工作内容。接单人凭这一段知道要处理什么。报修派工从报修内容写入，手工建单由建单人填写。
     */
    private String workContent;

    /**
     * 租户 ID
     */
    private Long tenantId;

}
