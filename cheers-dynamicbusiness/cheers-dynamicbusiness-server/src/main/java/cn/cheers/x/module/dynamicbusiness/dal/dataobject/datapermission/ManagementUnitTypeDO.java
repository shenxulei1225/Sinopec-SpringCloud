package cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 已登记的管理单元类型（型号）。只说明哪些型号算管理单元，不承载可管目录。
 */
@TableName("dynamic_management_unit_type")
@KeySequence("dynamic_management_unit_type_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class ManagementUnitTypeDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 管理单元目录里的型号编号 */
    private Long modelId;
}