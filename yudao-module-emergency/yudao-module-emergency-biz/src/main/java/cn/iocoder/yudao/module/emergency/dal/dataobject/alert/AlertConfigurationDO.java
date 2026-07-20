package cn.iocoder.yudao.module.emergency.dal.dataobject.alert;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import cn.iocoder.yudao.module.emergency.framework.mybatis.PostgreSQLJsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * 告警配置
 * 
 * 超时告警的配置管理
 */
@TableName("alert_configuration")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertConfigurationDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 告警类型：command_step_timeout
     */
    private String alertType;

    /**
     * 告警级别：yellow/orange/red
     */
    private String alertLevel;

    /**
     * 触发条件（JSONB格式）
     * 例如：{"beforeMinutes": 15} 表示提前15分钟
     */
    @TableField(typeHandler = PostgreSQLJsonbTypeHandler.class)
    private Map<String, Object> triggerCondition;

    /**
     * 接收对象（JSONB数组）
     * 例如：[{"type": "user", "id": 1}, {"type": "role", "id": 2}]
     */
    @TableField(typeHandler = PostgreSQLJsonbTypeHandler.class)
    private List<Map<String, Object>> receivers;

    /**
     * 通知渠道（JSONB数组）
     * 例如：["sms", "email", "system"]
     */
    @TableField(typeHandler = PostgreSQLJsonbTypeHandler.class)
    private List<String> notificationChannels;

    /**
     * 启用状态
     */
    private Boolean enabled;
}

