package cn.iocoder.yudao.module.emergency.dal.dataobject.response;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 响应历史（启动/升级/取消等）
 */
@TableName("emergency_response_history")
@KeySequence("emergency_response_history_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyResponseHistoryDO extends TenantBaseDO {

    @TableId(type = IdType.INPUT)
    private Long id;

    private Long responseId;

    /**
     * 类型：start/upgrade/cancel
     */
    private String type;

    private String fromLevel;

    private String toLevel;

    private String reason;
}
