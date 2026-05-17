package cn.iocoder.yudao.module.facility.management.service.query.model;

import lombok.Data;

/**
 * 设施视图对象（读模型）
 */
@Data
public class FacilityView {

    /**
     * 主键ID
     */
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
