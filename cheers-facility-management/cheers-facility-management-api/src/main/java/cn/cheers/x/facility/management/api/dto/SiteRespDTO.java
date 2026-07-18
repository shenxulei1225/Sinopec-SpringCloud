package cn.cheers.x.facility.management.api.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 站场响应DTO
 */
@Data
public class SiteRespDTO {

    /**
     * 站场ID
     */
    private Long siteId;

    /**
     * 站场编码
     */
    private String siteCode;

    /**
     * 站场名称
     */
    private String siteName;

    /**
     * 父级节点ID
     */
    private Long parentId;

    /**
     * 排序号
     */
    private Integer sortNo;

    /**
     * 节点类型：1-分组，2-站场
     */
    private Integer nodeType;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 路径
     */
    private String path;

    /**
     * 状态：0-正常，1-停用
     */
    private Integer status;

    /**
     * 省份编码
     */
    private Integer provinceCode;

    /**
     * 城市编码
     */
    private Integer cityCode;

    /**
     * 区县编码
     */
    private Integer areaCode;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 负责人
     */
    private String director;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 备注
     */
    private String remark;

}
