package cn.cheers.x.facility.management.controller.admin.vo.facility;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 设施分类树响应 VO
 */
@Schema(description = "管理后台 - 设施分类树响应 VO")
@Data
public class FacilityCategoryTreeResponse {

    @Schema(description = "设施类型列表")
    private List<FacilityTypeVO> facilitiesType;

    @Schema(description = "分类树")
    private List<FacilityTreeNodeVO> tree;

    @Data
    public static class FacilityTypeVO {
        @Schema(description = "类型编码")
        private String code;
        @Schema(description = "类型描述")
        private String desc;
    }
}
