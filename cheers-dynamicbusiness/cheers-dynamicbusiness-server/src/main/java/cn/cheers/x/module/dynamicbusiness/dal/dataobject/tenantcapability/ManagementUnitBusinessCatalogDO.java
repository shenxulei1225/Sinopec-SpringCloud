package cn.cheers.x.module.dynamicbusiness.dal.dataobject.tenantcapability;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 管理点上勾选的一条业务（数据目录注册编码）。 */
@TableName("management_unit_business_catalog")
@KeySequence("management_unit_business_catalog_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class ManagementUnitBusinessCatalogDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String managementUnitRegistryCode;

    private Long managementUnitId;

    private String catalogRegistryCode;
}