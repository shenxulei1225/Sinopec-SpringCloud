package cn.cheers.x.module.dynamicbusiness.dal.dataobject.tenantcapability;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 某条管理单元（或场站、管线段等锚点）上已启用的业务能力包。
 * 租户须先在 {@code tenant_capability_pack} 开通该包；本表不代替租户开通。
 */
@TableName("management_unit_capability_pack")
@KeySequence("management_unit_capability_pack_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class ManagementUnitCapabilityPackDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 业务能力包编码 */
    private String packCode;

    /** 锚点所在数据目录注册编码 */
    private String managementUnitRegistryCode;

    /** 锚点记录 id */
    private Long managementUnitId;
}