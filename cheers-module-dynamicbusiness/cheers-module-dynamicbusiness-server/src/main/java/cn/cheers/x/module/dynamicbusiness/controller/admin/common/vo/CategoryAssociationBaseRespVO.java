package cn.cheers.x.module.dynamicbusiness.controller.admin.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 分类关联操作响应基类。
 */
@Schema(description = "管理后台 - 分类关联操作响应基类")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryAssociationBaseRespVO {

    @Schema(description = "操作类型（ASSOCIATE/DISASSOCIATE/UPDATE/BATCH_UPDATE）", example = "ASSOCIATE")
    private String operationType;

    @Schema(description = "总分类数量", example = "5")
    private Integer totalCount;

    @Schema(description = "成功数量", example = "4")
    private Integer successCount;

    @Schema(description = "失败数量", example = "1")
    private Integer failCount;

    @Schema(description = "成功的分类ID列表")
    private List<Long> successCategoryIds;

    @Schema(description = "失败详情列表")
    private List<FailItem> failItems;

    @Schema(description = "执行耗时（毫秒）", example = "150")
    private Long executionTime;

    /**
     * 失败项详情。
     */
    @Schema(description = "失败项详情")
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailItem {

        @Schema(description = "分类ID", example = "64")
        private Long categoryId;

        @Schema(description = "失败原因", example = "分类不存在")
        private String reason;

        @Schema(description = "错误码", example = "404")
        private Integer errorCode;
    }
}
