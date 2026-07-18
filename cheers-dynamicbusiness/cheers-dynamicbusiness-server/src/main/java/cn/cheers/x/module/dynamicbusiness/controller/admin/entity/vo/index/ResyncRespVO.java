package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.index;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 补同步响应 VO
 * 
 * @author 扩展字段查询服务
 */
@Schema(description = "管理后台 - 补同步响应")
public class ResyncRespVO {

    /**
     * 总记录数
     */
    @Schema(description = "总记录数", example = "100")
    private Long totalCount;

    /**
     * 成功记录数
     */
    @Schema(description = "成功记录数", example = "98")
    private Long successCount;

    /**
     * 失败记录数
     */
    @Schema(description = "失败记录数", example = "2")
    private Long failedCount;

    /**
     * 跳过记录数（已是最新状态）
     */
    @Schema(description = "跳过记录数", example = "0")
    private Long skippedCount;

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
     * 耗时（毫秒）
     */
    @Schema(description = "耗时（毫秒）", example = "1500")
    private Long durationMs;

    // ==================== Getter/Setter ====================

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Long successCount) {
        this.successCount = successCount;
    }

    public Long getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(Long failedCount) {
        this.failedCount = failedCount;
    }

    public Long getSkippedCount() {
        return skippedCount;
    }

    public void setSkippedCount(Long skippedCount) {
        this.skippedCount = skippedCount;
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

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }
}
