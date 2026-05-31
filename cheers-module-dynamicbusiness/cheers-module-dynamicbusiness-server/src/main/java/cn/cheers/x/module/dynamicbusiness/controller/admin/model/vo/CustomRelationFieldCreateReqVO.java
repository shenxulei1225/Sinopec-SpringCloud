package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 自定义关联字段创建请求 VO
 * 
 * 用于在 Model 字段管理中新建自定义关联字段（FR-BDA-031, FR-BDA-033）
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 自定义关联字段创建请求")
@Data
public class CustomRelationFieldCreateReqVO {

    @Schema(description = "模型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模型ID不能为空")
    private Long modelId;

    @Schema(description = "字段名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "安全负责人")
    @NotBlank(message = "字段名称不能为空")
    @Size(max = 64, message = "字段名称长度不能超过64个字符")
    private String fieldName;

    @Schema(description = "字段编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "safety_manager")
    @NotBlank(message = "字段编码不能为空")
    @Size(max = 64, message = "字段编码长度不能超过64个字符")
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "字段编码格式不正确，必须以小写字母开头，只能包含小写字母、数字和下划线")
    private String fieldCode;

    @Schema(description = "字段说明", example = "负责安全的人员")
    @Size(max = 512, message = "字段说明长度不能超过512个字符")
    private String description;

    @Schema(description = "是否必填", example = "false")
    private Boolean required;

    @Schema(description = "排序值", example = "100")
    private Integer sort;

    @Schema(description = "已废弃：不再同步到关联字段库，保留字段仅为兼容旧请求", example = "false")
    private Boolean syncToLibrary;

    // ========== 业务关联流程优化：直接存储关联信息 ==========

    @Schema(description = "关联的 Model 关联 ID（系统自动创建时使用）", example = "1")
    private Long modelRelationId;
}
