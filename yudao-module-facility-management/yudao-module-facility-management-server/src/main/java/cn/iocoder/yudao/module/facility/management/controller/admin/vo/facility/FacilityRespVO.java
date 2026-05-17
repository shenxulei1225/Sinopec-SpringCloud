package cn.iocoder.yudao.module.facility.management.controller.admin.vo.facility;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 设施响应 VO")
@Data
public class FacilityRespVO {

    @Schema(description = "设施ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "设施编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "FAC001")
    private String facilityCode;

    @Schema(description = "设施名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "1号储罐")
    private String facilityName;

    @Schema(description = "设施类型编码", example = "TANK")
    private String facilityTypeCode;

    @Schema(description = "设施类型名称", example = "储罐")
    private String facilityTypeName;

    @Schema(description = "所属区域ID", example = "1")
    private Long siteId;

    @Schema(description = "所属区域名称", example = "罐区A")
    private String siteName;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "分类名称", example = "储罐类")
    private String categoryName;

    @Schema(description = "设施型号/规格", example = "C-1000")
    private String model;

    @Schema(description = "设施位置", example = "罐区A-1号位")
    private String location;

    @Schema(description = "负责人", example = "张三")
    private String manager;

    @Schema(description = "负责人电话", example = "13800138000")
    private String managerPhone;

    @Schema(description = "设施状态", example = "NORMAL")
    private String status;

    @Schema(description = "排序号", example = "1")
    private Integer sortNo;

}
