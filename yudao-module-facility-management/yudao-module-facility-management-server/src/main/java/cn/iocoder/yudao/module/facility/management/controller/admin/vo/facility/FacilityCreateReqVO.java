package cn.iocoder.yudao.module.facility.management.controller.admin.vo.facility;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 设施创建请求 VO")
@Data
public class FacilityCreateReqVO {

    @Schema(description = "设施编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "FAC001")
    @NotBlank(message = "设施编码不能为空")
    private String facilityCode;

    @Schema(description = "设施名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "1号储罐")
    @NotBlank(message = "设施名称不能为空")
    private String facilityName;

    @Schema(description = "设施类型编码", example = "TANK")
    private String facilityTypeCode;

    @Schema(description = "设施类型名称", example = "储罐")
    private String facilityTypeName;

    @Schema(description = "所属区域ID", example = "1")
    @NotNull(message = "所属区域ID不能为空")
    private Long siteId;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

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

    @Schema(description = "备注", example = "这是一个重要的储罐")
    private String remark;

}
