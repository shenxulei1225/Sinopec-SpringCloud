package cn.cheers.x.module.platform.runtime.dal.dataobject;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import cn.cheers.x.framework.mybatis.core.type.JsonbStringTypeHandler;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
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
 * 资源占窗 DO（L4 {@code platform_resource_reservation}）。
 */
@TableName(value = "platform_resource_reservation", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceReservationDO extends TenantBaseDO {

    @TableId(type = IdType.INPUT)
    private String id;

    private String runtimeJobId;

    private String candidateType;

    private String workId;

    private String entityTypeCode;

    private OffsetDateTime plannedStart;

    private OffsetDateTime plannedEnd;

    /** 生成任务写下计划中时必须清掉残留实测时刻。空值也要写入，不能留下错误年份。 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private OffsetDateTime actualStart;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private OffsetDateTime actualEnd;

    private OffsetDateTime candidateStart;

    private OffsetDateTime candidateEnd;

    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String assignedResources;

    private String lockState;

    private String candidateStatus;

    private String policySnapshotId;

    private String decisionTraceId;

    private Long facilityId;
}
