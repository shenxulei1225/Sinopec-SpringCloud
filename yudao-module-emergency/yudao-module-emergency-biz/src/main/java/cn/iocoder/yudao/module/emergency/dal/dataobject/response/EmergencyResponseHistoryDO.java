package cn.iocoder.yudao.module.emergency.dal.dataobject.response;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 响应历史（启动/升级/取消等）
 */
@TableName("emergency_response_history")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyResponseHistoryDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
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










