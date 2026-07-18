package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 模型-分类批量关联操作响应 VO。
 */
@Schema(description = "管理后台 - 模型-分类批量关联操作响应")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchModelCategoryAssociationRespVO {

    @Schema(description = "操作类型（BATCH_ASSOCIATE/BATCH_DISASSOCIATE/BATCH_UPDATE）", example = "BATCH_UPDATE")
    private String operationType;

    @Schema(description = "总模型数量", example = "5")
    private Integer totalModelCount;

    @Schema(description = "总分类数量", example = "3")
    private Integer totalCategoryCount;

    @Schema(description = "成功模型数量", example = "4")
    private Integer successModelCount;

    @Schema(description = "失败模型数量", example = "1")
    private Integer failModelCount;

    @Schema(description = "未找到的模型ID列表")
    private List<Long> notFoundModelIds;

    @Schema(description = "未找到的分类ID列表")
    private List<Long> notFoundCategoryIds;

    @Schema(description = "模型结果明细")
    private List<ModelResult> modelResults;

    @Schema(description = "执行耗时（毫秒）", example = "120")
    private Long executionTime;

    @Schema(description = "模型结果项")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelResult {

        @Schema(description = "模型ID", example = "1001")
        private Long modelId;

        @Schema(description = "模型是否存在", example = "true")
        private Boolean modelExists;

        @Schema(description = "成功数量", example = "3")
        private Integer successCount;

        @Schema(description = "失败数量", example = "1")
        private Integer failCount;

        @Schema(description = "错误信息", example = "模型不存在")
        private String errorMessage;
    }
}
