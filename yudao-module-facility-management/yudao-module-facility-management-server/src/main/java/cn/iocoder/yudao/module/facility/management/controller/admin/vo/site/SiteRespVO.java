package cn.iocoder.yudao.module.facility.management.controller.admin.vo.site;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "管理后台 - 站场响应 VO")
public class SiteRespVO {

    @Schema(description = "站场ID", example = "1")
    private Long siteId;

    @Schema(description = "站场编码", example = "SITE001")
    private String siteCode;

    @Schema(description = "站场名称", example = "北京站")
    private String siteName;

    @Schema(description = "父级节点ID", example = "0")
    private Long parentId;

    @Schema(description = "父级节点名称", example = "华北大区")
    private String parentName;

    @Schema(description = "排序号", example = "0")
    private Integer sortNo;

    @Schema(description = "节点类型：1-分组，2-站场", example = "2")
    private Integer nodeType;

    @Schema(description = "节点类型描述", example = "站场")
    private String nodeTypeDesc;

    @Schema(description = "站场类型ID", example = "1")
    private Long siteTypeId;

    @Schema(description = "站场类型名称", example = "加油站")
    private String siteTypeName;

    @Schema(description = "层级", example = "1")
    private Integer level;

    @Schema(description = "路径", example = "/ROOT/SITE001")
    private String path;

    @Schema(description = "状态：0-正常，1-停用", example = "0")
    private Integer status;

    @Schema(description = "省份编码", example = "110000")
    private Integer provinceCode;

    @Schema(description = "城市编码", example = "110100")
    private Integer cityCode;

    @Schema(description = "区县编码", example = "110101")
    private Integer areaCode;

    @Schema(description = "省份", example = "北京市")
    private String province;

    @Schema(description = "城市", example = "北京市")
    private String city;

    @Schema(description = "区县", example = "朝阳区")
    private String area;

    @Schema(description = "详细地址", example = "北京市朝阳区xxx")
    private String address;

    @Schema(description = "负责人", example = "张三")
    private String director;

    @Schema(description = "联系电话", example = "13800138000")
    private String phone;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "路由地址", example = "/dashboard")
    private String routingUrl;

    @Schema(description = "经度", example = "116.404")
    private BigDecimal longitude;

    @Schema(description = "纬度", example = "39.915")
    private BigDecimal latitude;

    @Schema(description = "备注", example = "这是一个重要的站场")
    private String remark;

    @Schema(description = "负责人用户ID", example = "1")
    private Integer ownerUserId;

    @Schema(description = "负责人用户名称", example = "张三")
    private String ownerUserName;

    @JsonProperty("createTime")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
