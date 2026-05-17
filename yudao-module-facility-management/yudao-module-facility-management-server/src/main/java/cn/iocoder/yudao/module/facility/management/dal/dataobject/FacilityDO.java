package cn.iocoder.yudao.module.facility.management.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * 设施台账 DO
 *
 * <p>用于记录设施的基本信息，包括设备、仪表、建筑物等。</p>
 */
@TableName("facility")
@KeySequence("facility_seq")
@Data
public class FacilityDO extends BaseDO {

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
