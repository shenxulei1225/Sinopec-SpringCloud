package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.index;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 同步失败日志 VO
 * 
 * @author 扩展字段查询服务
 */
@Schema(description = "管理后台 - 同步失败日志")
public class SyncFailLogVO {

    /**
     * 日志 ID
     */
    @Schema(description = "日志 ID", example = "1")
    private Long id;

    /**
     * Entity ID
     */
    @Schema(description = "Entity ID", example = "1001")
    private Long entityId;

    /**
     * Entity 名称
     */
    @Schema(description = "Entity 名称", example = "空调设备A")
    private String entityName;

    /**
     * Model ID
     */
    @Schema(description = "Model ID", example = "1")
    private Long modelId;

    /**
     * 业务类型编码
     */
    @Schema(description = "业务类型编码", example = "equipment")
    private String entityTypeCode;

    /**
     * Model 名称
     */
    @Schema(description = "Model 名称", example = "中央空调")
    private String modelName;

    /**
     * 引擎类型
     */
    @Schema(description = "引擎类型", example = "postgresql")
    private String engineType;

    /**
     * 失败原因
     */
    @Schema(description = "失败原因", example = "连接超时")
    private String failReason;

    /**
     * 重试次数
     */
    @Schema(description = "重试次数", example = "3")
    private Integer retryCount;

    /**
     * 最后重试时间
     */
    @Schema(description = "最后重试时间")
    private LocalDateTime lastRetryAt;

    /**
     * 状态
     * 
     * <p>可选值：PENDING、RETRYING、FAILED、SUCCESS</p>
     */
    @Schema(description = "状态", example = "FAILED")
    private String status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    // ==================== Getter/Setter ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getEntityTypeCode() {
        return entityTypeCode;
    }

    public void setEntityTypeCode(String entityTypeCode) {
        this.entityTypeCode = entityTypeCode;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public LocalDateTime getLastRetryAt() {
        return lastRetryAt;
    }

    public void setLastRetryAt(LocalDateTime lastRetryAt) {
        this.lastRetryAt = lastRetryAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
