package cn.cheers.x.workorder.dal.dataobject;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 工单步骤执行结果 DO
 *
 * @author 工单标准服务
 */
@TableName("wo_work_order_step_result")
@KeySequence("wo_work_order_step_result_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderStepResultDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 工单 ID
     */
    private Long workOrderId;

    /**
     * 步骤编码
     */
    private String stepCode;

    /**
     * 步骤顺序
     */
    private Integer stepOrder;

    /**
     * 是否已完成
     */
    private Boolean completed;

    /**
     * 步骤结果（JSON）
     */
    private String resultJson;

    /**
     * 附件 ID 列表（JSON）
     */
    private String attachmentIds;

    /**
     * 租户 ID
     */
    private Long tenantId;

}
