package cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

@Data
@Accessors(chain = false)
public class CategoryBaseVO {

    @Schema(description = "父分类ID", example = "0")
    private Long parentId;

    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "生产设备")
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100")
    private String name;

    @Schema(description = "分类类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "分类类型编码不能为空")
    @Size(max = 50, message = "分类类型编码长度不能超过50")
    private String categoryTypeCode;

    @Schema(description = "分类编码（GUID），全局唯一，用于与第三方对接", example = "A9418D4E4F3C606785C54BA8CBDF1797")
    @Size(max = 100, message = "分类编码长度不能超过100")
    private String code;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "状态", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "描述", example = "设备分类")
    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

    // ========== 模式C：Category 即 Entity 支持 ==========

    @Schema(description = "是否作为实体（模式C）", example = "false")
    private Boolean isEntity;

    @Schema(description = "关联的 Model ID，定义该分类节点的字段结构（模式C）", example = "1")
    private Long entityModelId;

    @Schema(description = "自定义字段（模式C），当 isEntity=true 时使用，格式：{\"field_code\":\"value\"}")
    private Map<String, Object> customFields;
}

