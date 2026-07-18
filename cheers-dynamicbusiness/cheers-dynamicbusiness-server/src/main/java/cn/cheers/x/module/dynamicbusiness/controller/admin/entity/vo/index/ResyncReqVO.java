package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.index;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 补同步请求 VO
 * 
 * @author 扩展字段查询服务
 */
@Schema(description = "管理后台 - 补同步请求")
public class ResyncReqVO {

    /**
     * 补同步类型
     * 
     * <p>可选值：</p>
     * <ul>
     *   <li>BY_IDS: 按 Entity ID 列表补同步</li>
     *   <li>BY_TIME_RANGE: 按时间范围补同步</li>
     *   <li>FAILED_ONLY: 仅补同步失败的记录</li>
     * </ul>
     */
    @Schema(description = "补同步类型", example = "BY_IDS")
    private ResyncType type;

    /**
     * Entity ID 列表（BY_IDS 类型时必填）
     */
    @Schema(description = "Entity ID 列表")
    private List<Long> entityIds;

    /**
     * 业务类型编码（BY_IDS 类型时必填，用于路由到正确的存储策略）
     */
    @Schema(description = "业务类型编码", example = "equipment")
    private String entityTypeCode;

    /**
     * Model ID（BY_TIME_RANGE 和 FAILED_ONLY 类型时可选）
     */
    @Schema(description = "Model ID", example = "1")
    private Long modelId;

    /**
     * 开始时间（BY_TIME_RANGE 类型时必填）
     */
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    /**
     * 结束时间（BY_TIME_RANGE 类型时必填）
     */
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    /**
     * 补同步类型枚举
     */
    public enum ResyncType {
        /**
         * 按 Entity ID 列表补同步
         */
        BY_IDS,
        
        /**
         * 按时间范围补同步
         */
        BY_TIME_RANGE,
        
        /**
         * 仅补同步失败的记录
         */
        FAILED_ONLY
    }

    // ==================== Getter/Setter ====================

    public ResyncType getType() {
        return type;
    }

    public void setType(ResyncType type) {
        this.type = type;
    }

    public List<Long> getEntityIds() {
        return entityIds;
    }

    public void setEntityIds(List<Long> entityIds) {
        this.entityIds = entityIds;
    }

    public String getEntityTypeCode() {
        return entityTypeCode;
    }

    public void setEntityTypeCode(String entityTypeCode) {
        this.entityTypeCode = entityTypeCode;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
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
}
