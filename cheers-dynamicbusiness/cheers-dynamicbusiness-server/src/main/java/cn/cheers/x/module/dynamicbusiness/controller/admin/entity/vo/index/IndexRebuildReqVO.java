package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.index;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

/**
 * 索引重建请求 VO
 * 
 * @author 扩展字段查询服务
 */
@Schema(description = "管理后台 - 索引重建请求")
public class IndexRebuildReqVO {

    /**
     * Model ID（必填）
     */
    @Schema(description = "Model ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "Model ID 不能为空")
    private Long modelId;

    /**
     * 是否异步执行
     * 
     * <p>默认为 true（异步执行）。
     * 异步执行时返回任务 ID，可通过任务 ID 查询执行状态。</p>
     */
    @Schema(description = "是否异步执行", example = "true")
    private Boolean async = true;

    // ==================== Getter/Setter ====================

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }
}
