package cn.iocoder.yudao.module.facility.management.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 站场类型 DO
 *
 * <p>用于定义站场的类型，如：加油站、炼油厂、化工厂、管道站等。</p>
 */
@TableName("fac_site_type")
@KeySequence("fac_site_type_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class SiteTypeDO extends TenantBaseDO {

    /**
     * 类型ID
     */
    @TableId
    private Long id;

    /**
     * 类型编码
     */
    private String typeCode;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 类型描述
     */
    private String description;

    /**
     * 排序号
     */
    private Integer sortNo;

    /**
     * 状态：0-正常，1-停用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}
