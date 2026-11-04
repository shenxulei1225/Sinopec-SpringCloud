package cn.cheers.x.module.dynamicbusiness.service.event;

import cn.cheers.x.framework.mybatis.core.type.JsonbMapTypeHandler;
import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 与业务状态同事务写入的待投递平台事件。
 */
@TableName(value = "platform_event_outbox", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class PlatformEventOutboxDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String eventId;
    private Long eventTopicId;
    private String eventCode;
    private String eventKind;
    private Long facilityId;
    private String sourceEntityTypeCode;
    private Long sourceEntityId;
    private String phase;
    private LocalDateTime occurredAt;
    @TableField(typeHandler = JsonbMapTypeHandler.class)
    private Map<String, Object> payload;
    private String status;
    private Integer attempts;
    private LocalDateTime availableAt;
    private LocalDateTime processedAt;
    private String lastError;
}
