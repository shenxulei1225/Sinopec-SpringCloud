package cn.cheers.x.module.platform.policy.dal.dataobject;

import cn.cheers.x.module.platform.policy.framework.mybatis.JsonbStringTypeHandler;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
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

@TableName(value = "platform_policy_snapshot", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicySnapshotDO extends TenantBaseDO {

    @TableId(type = IdType.INPUT)
    private String id;

    private String policySetId;

    private Integer policySetVersion;

    private String entityTypeCode;

    private String platformLawVersion;

    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String resolvedSpec;

    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String provenanceIndex;

    private Long siteId;
}
