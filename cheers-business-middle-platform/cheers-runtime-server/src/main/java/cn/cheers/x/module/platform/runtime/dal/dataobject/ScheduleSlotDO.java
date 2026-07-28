package cn.cheers.x.module.platform.runtime.dal.dataobject;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import cn.cheers.x.framework.mybatis.core.type.JsonbStringTypeHandler;
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
 * 计划点 DO（L4）。
 */
@TableName(value = "platform_schedule_slot", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSlotDO extends TenantBaseDO {

    @TableId(type = IdType.INPUT)
    private String id;

    private String runtimeJobId;

    private String workId;

    private String entityTypeCode;

    private OffsetDateTime plannedStart;

    private OffsetDateTime plannedEnd;

    private OffsetDateTime actualStart;

    private OffsetDateTime actualEnd;

    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String assignedResources;

    private String lockState;

    private String slotStatus;

    private String policySnapshotId;

    private String decisionTraceId;

    private Long facilityId;
}
