package cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 可选的数据管理方式：数据目录能按哪本目录管理（按场站、按管线、按组织机构……）。
 * 在平台配置页「数据资源管理」第一个标签里登记，各数据目录只能从这里选。页面显示为「按目录名称管理」。
 * 不说明哪本目录用了哪种方式：那是归属声明（{@link CatalogOwnershipDO}）。
 */
@TableName("dynamic_management_method")
@KeySequence("dynamic_management_method_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class ManagementMethodDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用来管理数据的目录注册编码 */
    private String registryCode;

    /** 在「数据管理方式」表格里的列顺序，从小到大 */
    private Integer sort;
}
