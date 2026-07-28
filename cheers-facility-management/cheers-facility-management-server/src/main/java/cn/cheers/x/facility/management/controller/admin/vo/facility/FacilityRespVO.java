package cn.cheers.x.facility.management.controller.admin.vo.facility;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 设施管理 - 设施详情 Response VO
 */
@Schema(description = "管理后台 - 设施详情 Response VO")
@Data
public class FacilityRespVO {

    @Schema(description = "设施ID", example = "1")
    private Long id;

    @Schema(description = "设施编码", example = "FAC001")
    private String facilityCode;

    @Schema(description = "设施名称", example = "1号储罐")
    private String facilityName;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "分类名称", example = "储罐类")
    private String categoryName;

    @Schema(description = "所属站场ID", example = "1")
    private Long stationId;

    @Schema(description = "所属站场名称", example = "罐区A")
    private String stationName;

    @Schema(description = "排序号", example = "1")
    private Integer sortNo;

    @Schema(description = "状态：0-正常，1-停用", example = "0")
    private Integer status;

    @Schema(description = "状态描述", example = "正常")
    private String statusDesc;

    @Schema(description = "生产厂家", example = "某某厂家")
    private String manufacturer;

    @Schema(description = "规格型号", example = "型号A")
    private String model;

    @Schema(description = "安装日期", example = "2024-01-01")
    private String installDate;

    @Schema(description = "安装位置", example = "罐区A-1号位")
    private String location;

    @Schema(description = "备注", example = "备注信息")
    private String remark;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "更新时间")
    private String updateTime;
}
