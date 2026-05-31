package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.index;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 索引重建状态 VO
 * 
 * @author 扩展字段查询服务
 */
@Schema(description = "管理后台 - 索引重建状态")
public class IndexRebuildStatusVO {

    /**
     * 任务 ID
     */
    @Schema(description = "任务 ID", example = "task-123")
    private String taskId;

    /**
     * Model ID
     */
    @Schema(description = "Model ID", example = "1")
    private Long modelId;

    /**
     * 执行状态
     * 
     * <p>可选值：PENDING、RUNNING、COMPLETED、FAILED</p>
     */
    @Schema(description = "执行状态", example = "RUNNING")
    private String status;

    /**
     * 总记录数
     */
    @Schema(description = "总记录数", example = "1000")
    private Long totalCount;

    /**
     * 已处理记录数
     */
    @Schema(description = "已处理记录数", example = "500")
    private Long processedCount;

    /**
     * 失败记录数
     */
    @Schema(description = "失败记录数", example = "0")
    private Long failedCount;

    /**
     * 进度百分比
     */
    @Schema(description = "进度百分比", example = "50")
    private Integer progressPercent;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    /**
     * 预计剩余时间（秒）
     */
    @Schema(description = "预计剩余时间（秒）", example = "60")
    private Long estimatedRemainingSeconds;

    /**
     * 错误信息（失败时）
     */
    @Schema(description = "错误信息")
    private String errorMessage;

    // ==================== Getter/Setter ====================

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getProcessedCount() {
        return processedCount;
    }

    public void setProcessedCount(Long processedCount) {
        this.processedCount = processedCount;
    }

    public Long getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(Long failedCount) {
        this.failedCount = failedCount;
    }

    public Integer getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(Integer progressPercent) {
        this.progressPercent = progressPercent;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Long getEstimatedRemainingSeconds() {
        return estimatedRemainingSeconds;
    }

    public void setEstimatedRemainingSeconds(Long estimatedRemainingSeconds) {
        this.estimatedRemainingSeconds = estimatedRemainingSeconds;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
