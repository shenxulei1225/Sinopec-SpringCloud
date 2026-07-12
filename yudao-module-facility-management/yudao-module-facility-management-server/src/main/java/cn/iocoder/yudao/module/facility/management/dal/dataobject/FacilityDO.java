package cn.iocoder.yudao.module.facility.management.dal.dataobject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 设施台账 DO
 *
 * <p>用于记录设施的基本信息，包括设备、仪表、建筑物等。</p>
 * <p><strong>已废弃</strong>：主数据已迁移至动态业务 {@code ent_facility}；本 DO 仅保留给 legacy 写路径，勿扩展。</p>
 */
@TableName("fac_facility")
@KeySequence("fac_facility_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class FacilityDO extends TenantBaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 设施编码
     */
    private String facilityCode;

    /**
     * 设施名称
     */
    private String facilityName;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 所属站场ID
     */
    private Long siteId;

    /**
     * 所属站场名称
     */
    private String siteName;

    /**
     * 排序号
     */
    private Integer sortNo;

    /**
     * 状态：0-正常，1-停用
     */
    private Integer status;

    /**
     * 生产厂家
     */
    private String manufacturer;

    /**
     * 规格型号
     */
    private String model;

    /**
     * 安装日期
     */
    private String installDate;

    /**
     * 安装位置
     */
    private String location;

    /**
     * 备注
     */
    private String remark;

}
