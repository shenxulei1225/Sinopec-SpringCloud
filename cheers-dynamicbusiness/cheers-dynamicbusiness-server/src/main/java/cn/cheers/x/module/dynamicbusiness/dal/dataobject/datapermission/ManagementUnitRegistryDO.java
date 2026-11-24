package cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 已添加为管理单元的一本数据目录。
 * 只说明第二步要为这本目录单独开一个业务配置标签。
 * 不表示这本目录挂在另一本目录下面。
 */
@TableName("dynamic_management_unit_registry")
@KeySequence("dynamic_management_unit_registry_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class ManagementUnitRegistryDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 数据目录注册编码 */
    private String registryCode;

    /** 标签从左到右的顺序 */
    private Integer sortOrder;
}
