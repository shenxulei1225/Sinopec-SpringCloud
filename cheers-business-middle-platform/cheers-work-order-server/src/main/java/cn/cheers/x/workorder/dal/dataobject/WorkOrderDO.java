package cn.cheers.x.workorder.dal.dataobject;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 工单 DO
 *
 * @author 工单标准服务
 */
@TableName("wo_work_order")
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
     * 指派人用户 ID
     */
    private Long assigneeUserId;

    /**
     * 工单标题
     */
    private String title;

    /**
     * 租户 ID
     */
    private Long tenantId;

}
