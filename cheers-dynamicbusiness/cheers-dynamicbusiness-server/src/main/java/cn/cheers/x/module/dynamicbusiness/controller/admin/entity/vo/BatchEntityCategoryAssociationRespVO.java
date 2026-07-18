package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量实体-分类关联操作响应 VO
 * 
 * <p>返回批量关联操作的执行结果，包含每个实体的处理详情。</p>
 * 
 * @author 基础服务模块
 */
@Schema(description = "管理后台 - 批量实体-分类关联操作响应")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchEntityCategoryAssociationRespVO {

    @Schema(description = "操作类型（BATCH_ASSOCIATE/BATCH_DISASSOCIATE/BATCH_REPLACE）", example = "BATCH_ASSOCIATE")
    private String operationType;

    @Schema(description = "总实体数量", example = "10")
    private Integer totalEntityCount;

    @Schema(description = "总分类数量", example = "3")
    private Integer totalCategoryCount;

    @Schema(description = "成功的实体数量", example = "8")
    private Integer successEntityCount;

    @Schema(description = "失败的实体数量", example = "2")
    private Integer failEntityCount;

    @Schema(description = "不存在的实体ID列表")
    private List<Long> notFoundEntityIds;

    @Schema(description = "不存在的分类ID列表")
    private List<Long> notFoundCategoryIds;

    @Schema(description = "每个实体的处理结果")
    private List<EntityResult> entityResults;

    @Schema(description = "执行耗时（毫秒）", example = "150")
    private Long executionTime;

    /**
     * 单个实体的处理结果
     */
    @Schema(description = "单个实体的处理结果")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityResult {

        @Schema(description = "实体ID", example = "1")
        private Long entityId;

        @Schema(description = "实体是否存在", example = "true")
        private Boolean entityExists;

        @Schema(description = "成功关联/取消的分类数量", example = "3")
        private Integer successCount;

        @Schema(description = "失败的分类数量", example = "0")
        private Integer failCount;

        @Schema(description = "错误信息（如果实体不存在）", example = "实体不存在")
        private String errorMessage;
    }
}
