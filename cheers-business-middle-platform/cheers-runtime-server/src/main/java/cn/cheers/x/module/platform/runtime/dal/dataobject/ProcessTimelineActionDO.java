package cn.cheers.x.module.platform.runtime.dal.dataobject;

import cn.cheers.x.framework.mybatis.core.type.JsonbStringTypeHandler;
import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;

/**
 * 过程时间线动作（五问动作台账）。
 */
@TableName(value = "platform_process_timeline_action", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessTimelineActionDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long facilityId;

    private String targetType;

    private String targetId;

    private OffsetDateTime occurredAt;

    private String actorId;

    private String actorName;

    private String actionCode;

    private String howSummary;

    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String payloadJson;
}
