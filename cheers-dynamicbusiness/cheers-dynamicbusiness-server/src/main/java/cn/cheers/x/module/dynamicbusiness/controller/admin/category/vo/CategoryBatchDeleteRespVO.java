package cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 分类批量删除 Response VO")
@Data
public class CategoryBatchDeleteRespVO {

    @Schema(description = "成功删除的数量", example = "5")
    private Integer successCount;

    @Schema(description = "失败的数量", example = "1")
    private Integer failCount;

    @Schema(description = "失败详情列表")
    private List<CategoryBatchDeleteFailItem> failItems;

    @Schema(description = "管理后台 - 分类批量删除失败项")
    @Data
    public static class CategoryBatchDeleteFailItem {

        @Schema(description = "分类ID", example = "10")
        private Long id;

        @Schema(description = "失败原因", example = "存在子分类，无法删除")
        private String reason;
    }
}












































