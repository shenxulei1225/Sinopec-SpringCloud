package cn.iocoder.yudao.module.facility.management.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 站场 DO
 *
 * <p>站场是指实际的生产运营场所，如加油站、化工站场、炼油厂等。</p>
 */
@TableName("fac_site")
@KeySequence("fac_site_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class SiteDO extends TenantBaseDO {

    /**
     * 站场ID
     */
    @TableId
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
     * 站场类型ID
     */
    private Long siteTypeId;

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
     * 路由地址
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
     * 备注
     */
    private String remark;

    /**
     * 负责人用户ID
     */
    private Integer ownerUserId;

}
