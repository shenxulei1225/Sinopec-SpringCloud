package cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 关联字段库（带状态）响应 VO
 * 
 * 扩展 RelationFieldLibraryRespVO，增加关联目标的状态信息
 */
@Schema(description = "管理后台 - 关联字段库（带状态）Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class RelationFieldLibraryWithStatusVO extends RelationFieldLibraryRespVO {

    /**
     * 字段状态
     * - AVAILABLE: 可用（关联目标存在）
     * - PENDING: 待建（关联目标不存在）
     */
    @Schema(description = "字段状态：AVAILABLE-可用，PENDING-待建", requiredMode = Schema.RequiredMode.REQUIRED, example = "AVAILABLE")
    private String status;

    /**
     * 关联业务类型名称（如果存在）
     */
    @Schema(description = "关联业务类型名称", example = "人员管理")
    private String targetEntityTypeName;

    /**
     * 目标 Model 名称（如果存在）
     * 
     * 业务级引用默认不返回模型名称，兼容旧字段保留。
     */
    @Schema(description = "目标 Model 名称", example = "员工")
    private String targetModelName;

    /**
     * 关联目标是否存在
     */
    @Schema(description = "关联目标是否存在", example = "true")
    private Boolean targetExists;

    /**
     * 状态常量
     */
    public static final String STATUS_AVAILABLE = "AVAILABLE";
    public static final String STATUS_PENDING = "PENDING";

    /**
     * 判断是否可用
     */
    public boolean isAvailable() {
        return STATUS_AVAILABLE.equals(this.status);
    }

    /**
     * 判断是否待建
     */
    public boolean isPending() {
        return STATUS_PENDING.equals(this.status);
    }
}
