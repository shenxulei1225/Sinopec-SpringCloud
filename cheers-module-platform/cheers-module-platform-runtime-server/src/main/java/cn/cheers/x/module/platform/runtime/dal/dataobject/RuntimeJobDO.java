package cn.cheers.x.module.platform.runtime.dal.dataobject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.cheers.x.module.platform.runtime.framework.mybatis.JsonbStringTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 运行作业 DO（L4 根实例）。
 */
@TableName(value = "platform_runtime_job", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuntimeJobDO extends TenantBaseDO {

    @TableId(type = IdType.INPUT)
    private String id;

    private String entityTypeCode;

    private String triggerAction;

    private String status;

    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String sourceWorkIds;

    private String policySnapshotId;

    private String orchestrationRef;

    private Long siteId;
}
