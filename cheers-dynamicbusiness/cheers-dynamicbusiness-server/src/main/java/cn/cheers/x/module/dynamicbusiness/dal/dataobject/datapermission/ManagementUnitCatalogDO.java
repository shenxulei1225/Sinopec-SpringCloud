package cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 可管目录：某个管理点能管哪一本数据目录。在「管理单元与业务启用」里按树上的点维护。
 * 不说明看哪几条记录：看哪几条由任职所在的单元和归属声明决定。
 */
@TableName("dynamic_management_unit_catalog")
@KeySequence("dynamic_management_unit_catalog_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class ManagementUnitCatalogDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 管理单元目录注册编码 */
    private String managementUnitRegistryCode;

    /** 管理单元目录里的记录编号 */
    private Long managementUnitId;

    /** 能管的数据目录注册编码 */
    private String registryCode;
}
