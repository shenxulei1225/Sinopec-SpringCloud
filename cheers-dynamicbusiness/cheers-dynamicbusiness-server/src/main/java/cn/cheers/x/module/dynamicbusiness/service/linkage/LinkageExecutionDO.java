package cn.cheers.x.module.dynamicbusiness.service.linkage;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 一次事件命中一条联动的整体执行记录。
 */
@TableName("dynamic_linkage_execution")
@Data
@EqualsAndHashCode(callSuper = true)
public class LinkageExecutionDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String eventId;
    private Long eventTopicId;
    private Long linkageRuleId;
    private Long facilityId;
    private String sourceEntityTypeCode;
    private Long sourceEntityId;
    private String status;
    private Integer itemCount;
    private Integer successCount;
    private Integer failureCount;
    private String lastError;
}
