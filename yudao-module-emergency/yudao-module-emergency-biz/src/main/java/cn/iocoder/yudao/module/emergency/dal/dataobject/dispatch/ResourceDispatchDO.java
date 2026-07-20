package cn.iocoder.yudao.module.emergency.dal.dataobject.dispatch;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import cn.iocoder.yudao.module.emergency.framework.mybatis.PostgreSQLJsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 资源调度
 */
@TableName("resource_dispatch")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDispatchDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 调度编号（唯一）
     */
    @TableField("dispatch_no")
    private String dispatchNo;

    @TableField("event_id")
    private Long eventId;

    /**
     * 响应ID（预警阶段时为NULL）
     * 注意：数据库需要添加此字段
     */
    @TableField("response_id")
    private Long responseId;

    @TableField("resource_id")
    private Long resourceId;

    /**
     * 调度阶段：预警阶段、响应阶段等
     * 注意：数据库需要添加此字段
     */
    private String stage;

    /**
     * 调度状态：pending/dispatched/in_use/recovered
     */
    private String status;

    /**
     * 派发信息（JSONB）
     */
    @TableField(value = "dispatch_info", typeHandler = PostgreSQLJsonbTypeHandler.class)
    private Map<String, Object> dispatchInfo;

    /**
     * 分配信息（JSONB）
     */
    @TableField(value = "allocation_info", typeHandler = PostgreSQLJsonbTypeHandler.class)
    private Map<String, Object> allocationInfo;

    /**
     * 使用信息（JSONB）
     */
    @TableField(value = "usage_info", typeHandler = PostgreSQLJsonbTypeHandler.class)
    private Map<String, Object> usageInfo;

    /**
     * 回收信息（JSONB）
     */
    @TableField(value = "recovery_info", typeHandler = PostgreSQLJsonbTypeHandler.class)
    private Map<String, Object> recoveryInfo;

    /**
     * 派发时间
     */
    @TableField("dispatch_time")
    private LocalDateTime dispatchTime;

    /**
     * 到达/预计到达分钟
     */
    @TableField("eta_minutes")
    private Integer etaMinutes;

    private String comment;
}

