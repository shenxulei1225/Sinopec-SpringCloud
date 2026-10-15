package cn.cheers.x.module.dynamicbusiness.dal.dataobject.cataloggrant;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色在某个数据目录上、某一类对象的一项操作。
 * 目录身份是目录注册编码。对象是分类、模型、实体或详情。
 * 超级管理员不靠本表放行。租户管理员和自定义角色都读本表。
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

    /** category 分类 / model 模型 / entity 实体 / detail 详情 */
    private String subject;

    /** query 查看；create、update、delete 合成角色页上的一项「维护」 */
    private String operation;
}
