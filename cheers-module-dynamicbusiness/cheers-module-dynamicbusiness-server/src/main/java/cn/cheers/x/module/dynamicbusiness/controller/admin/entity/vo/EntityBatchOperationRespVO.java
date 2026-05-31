package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 业务实体批量操作响应 VO
 * 
 * 返回批量操作的执行结果，包括成功数量、失败数量和失败详情
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 业务实体批量操作响应")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityBatchOperationRespVO {

    @Schema(description = "操作类型（UPDATE/DELETE/MOVE/EXPORT）", example = "UPDATE")
    private String operationType;

    @Schema(description = "总数量", example = "100")
    private Integer totalCount;

    @Schema(description = "成功数量", example = "95")
    private Integer successCount;

    @Schema(description = "失败数量", example = "5")
    private Integer failCount;

    @Schema(description = "是否异步执行", example = "false")
    private Boolean async;

    @Schema(description = "异步任务ID（异步执行时返回）", example = "task-123456")
    private String taskId;

    @Schema(description = "失败详情列表")
    private List<FailItem> failItems;

    @Schema(description = "执行耗时（毫秒）", example = "1500")
    private Long executionTime;

    /**
     * 失败项详情
     */
    @Schema(description = "失败项详情")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailItem {

        @Schema(description = "实体ID", example = "1")
        private Long entityId;

        @Schema(description = "实体名称", example = "设备001")
        private String entityName;

        @Schema(description = "失败原因", example = "实体不存在")
        private String reason;

        @Schema(description = "错误码", example = "404")
        private Integer errorCode;
    }
}
