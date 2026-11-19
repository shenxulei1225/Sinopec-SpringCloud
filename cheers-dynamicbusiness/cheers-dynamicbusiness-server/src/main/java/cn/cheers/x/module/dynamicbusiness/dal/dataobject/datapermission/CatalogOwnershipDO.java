package cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 归属声明：一本数据目录的记录用哪个引用字段，往上一跳归到哪一本目录。
 * 例如设备用「所属场站」归到场站，场站用「所属管线段」归到管线。
 * 只描述一跳。逐跳往上到达权限维度由数据范围计算完成。
 * 不表示分类挂接或筛选视角。
 */
@TableName("dynamic_catalog_ownership")
@KeySequence("dynamic_catalog_ownership_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class CatalogOwnershipDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 声明归属的数据目录注册编码 */
    private String registryCode;

    /** 归属字段编码，必须是这本目录上的引用字段 */
    private String fieldCode;

    /** 引用字段指向的数据目录注册编码 */
    private String targetRegistryCode;

    /** 保存记录时这个字段必须有值 */
    private Boolean required;

    /** 一条记录可以同时归到多个上级；以实体关系表为准 */
    private Boolean multiValued;
}
