package cn.cheers.x.module.dynamicbusiness.dal.dataobject.cataloggrant;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义角色在某个数据目录上的一项操作。
 * 目录身份是目录注册编码。超级管理员和租户管理员不靠本表放行。
 */
@TableName("dynamic_catalog_role_grant")
@KeySequence("dynamic_catalog_role_grant_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class CatalogRoleGrantDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色编号 */
    private Long roleId;

    /** 数据目录注册编码 */
    private String registryCode;

    /** query / create / update / delete */
    private String operation;
}
