package cn.cheers.x.facility.management.controller.admin.vo.facility;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 设施管理 - 创建设施请求 VO
 */
@Schema(description = "管理后台 - 设施创建 Request VO")
@Data
public class FacilityCreateReqVO {

    @Schema(description = "设施编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "FAC001")
    @NotBlank(message = "设施编码不能为空")
    @Size(max = 64, message = "设施编码长度不能超过64个字符")
    private String facilityCode;

    @Schema(description = "设施名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "1号储罐")
    @NotBlank(message = "设施名称不能为空")
    @Size(max = 128, message = "设施名称长度不能超过128个字符")
    private String facilityName;

    @Schema(description = "分类ID", example = "1")
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @Schema(description = "分类名称", example = "储罐类")
    private String categoryName;

    @Schema(description = "所属站场ID", example = "1")
    @NotNull(message = "站场ID不能为空")
    private Long siteId;

    @Schema(description = "所属站场名称", example = "罐区A")
    private String siteName;

    @Schema(description = "排序号", example = "1")
    private Integer sortNo;

    @Schema(description = "状态：0-正常，1-停用", example = "0")
    private Integer status;

    @Schema(description = "生产厂家", example = "某某厂家")
    @Size(max = 128, message = "生产厂家长度不能超过128个字符")
    private String manufacturer;

    @Schema(description = "规格型号", example = "型号A")
    @Size(max = 64, message = "规格型号长度不能超过64个字符")
    private String model;

    @Schema(description = "安装日期", example = "2024-01-01")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate installDate;

    @Schema(description = "安装位置", example = "罐区A-1号位")
    @Size(max = 256, message = "安装位置长度不能超过256个字符")
    private String location;

    @Schema(description = "备注", example = "备注信息")
    @Size(max = 512, message = "备注长度不能超过512个字符")
    private String remark;
}
