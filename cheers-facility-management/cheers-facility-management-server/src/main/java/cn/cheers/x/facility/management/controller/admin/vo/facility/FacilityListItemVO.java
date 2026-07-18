package cn.cheers.x.facility.management.controller.admin.vo.facility;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 设施列表项 VO（legacy接口使用）
 */
@Schema(description = "管理后台 - 设施列表项 VO")
@Data
public class FacilityListItemVO {

    @Schema(description = "设施ID", example = "1")
    private Long facilityId;

    @Schema(description = "设施编码", example = "FAC001")
    private String facilityCode;

    @Schema(description = "设施名称", example = "1号储罐")
    private String facilityName;

    @Schema(description = "设施类型描述", example = "储罐")
    @JsonProperty("facilityTypeDesc")
    private String facilityTypeDesc;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "分类名称", example = "储罐类")
    private String categoryName;

    @Schema(description = "所属站点ID", example = "1")
    private Long siteId;

    @Schema(description = "所属站点名称", example = "罐区A")
    private String siteName;

    @Schema(description = "设备类型", example = "TANK")
    private String equipmentType;

    @Schema(description = "地理位置名称", example = "罐区A-1号位")
    private String geographicalName;

    @Schema(description = "使用状态描述", example = "正常使用")
    @JsonProperty("usageStateDesc")
    private String usageStateDesc;

    @Schema(description = "设施状态", example = "1")
    private Integer status;

    @Schema(description = "经度")
    private String longitude;

    @Schema(description = "纬度")
    private String latitude;

    @Schema(description = "海拔")
    private String altitude;

    @Schema(description = "模型URL")
    private String modelUrl;

    @Schema(description = "渲染类型")
    private String renderType;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "修改时间")
    private String modifyTime;

    @Schema(description = "描述")
    private String description;
}
