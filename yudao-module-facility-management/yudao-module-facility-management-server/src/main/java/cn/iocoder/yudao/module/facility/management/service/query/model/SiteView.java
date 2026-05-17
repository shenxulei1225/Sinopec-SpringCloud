package cn.iocoder.yudao.module.facility.management.service.query.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 站场视图对象（读模型）
 */
@Data
public class SiteView {

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
     * 父级节点名称
     */
    private String parentName;

    /**
     * 排序号
     */
    private Integer sortNo;

    /**
     * 节点类型：1-分组，2-站场
     */
    private Integer nodeType;

    /**
     * 节点类型描述
     */
    private String nodeTypeDesc;

    /**
     * 站场类型ID
     */
    private Long siteTypeId;

    /**
     * 站场类型名称
     */
    private String siteTypeName;

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
     * 路由URL
     */
    private String routingUrl;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 负责人用户ID
     */
    private Integer ownerUserId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 子节点列表
     */
    private List<SiteView> children;

}
