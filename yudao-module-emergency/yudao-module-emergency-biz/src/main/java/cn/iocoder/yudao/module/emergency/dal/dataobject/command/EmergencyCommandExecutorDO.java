package cn.iocoder.yudao.module.emergency.dal.dataobject.command;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 应急指令执行人
 */
@TableName("emergency_command_executor")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyCommandExecutorDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 指令ID
     */
    private Long commandId;

    /**
     * 执行人ID
     */
    private Long userId;

    /**
     * 执行人姓名（快照）
     */
    private String userName;

    /**
     * 执行角色
     */
    private String role;

    /**
     * 是否主要执行人
     */
    private Boolean isPrimary;

    /**
     * 接收时间
     */
    private LocalDateTime receiveTime;

    /**
     * 执行状态
     */
    private String status;
}
