package cn.iocoder.yudao.module.facility.management.controller.admin.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 站场更新请求 VO")
@Data
public class SiteUpdateReqVO {

    @Schema(description = "站场ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "站场ID不能为空")
    private Long siteId;

    @Schema(description = "站场编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "SITE001")
    @NotBlank(message = "站场编码不能为空")
    private String siteCode;

    @Schema(description = "站场名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "北京站")
    @NotBlank(message = "站场名称不能为空")
    private String siteName;

    @Schema(description = "父级节点ID", example = "0")
    @NotNull(message = "父级节点ID不能为空")
    private Long parentId;

    @Schema(description = "排序号", example = "0")
    private Integer sortNo;

    @Schema(description = "节点类型：1-分组，2-站场", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "节点类型不能为空")
    private Integer nodeType;

    @Schema(description = "站场类型ID", example = "1")
    private Long siteTypeId;

    @Schema(description = "状态：0-正常，1-停用", example = "0")
    private Integer status;

    @Schema(description = "省份编码", example = "110000")
    private Integer provinceCode;

    @Schema(description = "城市编码", example = "110100")
    private Integer cityCode;

    @Schema(description = "区县编码", example = "110101")
    private Integer areaCode;

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

}
