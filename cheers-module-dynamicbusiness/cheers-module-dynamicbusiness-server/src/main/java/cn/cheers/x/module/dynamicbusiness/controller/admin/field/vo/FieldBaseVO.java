package cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class FieldBaseVO {

    @Schema(description = "字段名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "长度")
    @NotBlank(message = "字段名称不能为空")
    @Size(max = 100, message = "字段名称长度不能超过100")
    private String name;

    @Schema(description = "字段类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "NUMBER")
    @NotBlank(message = "字段类型不能为空")
    @JsonAlias({"fieldType"}) 
    private String type;

    @Schema(description = "单位（数值类必填）", example = "米")
    private String unit;

    @Schema(description = "描述", example = "长度（米）")
    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

    @Schema(description = "来源", example = "USER")
    @NotBlank(message = "来源不能为空")
    private String source;

    @Schema(description = "状态", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    // ========== 扩展字段查询相关 ==========

    @Schema(description = "是否可查询", example = "true")
    private Boolean isSearchable;

    @Schema(description = "是否可排序", example = "true")
    private Boolean isSortable;

    @Schema(description = "索引策略", example = "GIN")
    private String indexStrategy;

    @Schema(description = "最大关联数量（ENTITY_REF_MULTI 类型专用，null 或 0 表示不限制）", example = "5")
    private Integer maxRelations;

    @Schema(description = "选项列表（ENUM 类型专用，JSON 数组格式）", example = "[{\"label\":\"待处理\",\"value\":\"pending\"},{\"label\":\"已完成\",\"value\":\"completed\"}]")
    private String options;

    // ========== 关联字段专用属性 ==========

    @Schema(description = "关联目标业务类型编码（关联字段使用）", example = "personnel")
    private String targetEntityType;

    @Schema(description = "关联目标业务类型名称（关联字段使用）", example = "人员管理")
    private String targetEntityTypeName;

    @Schema(description = "关联目标 Model 编码（关联字段使用）", example = "employee")
    private String targetModelCode;

    @Schema(description = "关联目标 Model 名称（关联字段使用）", example = "员工")
    private String targetModelName;

    @Schema(description = "引用 Provider 编码（REFERENCE/ENTITY_REF 可选）", example = "dynamic_USER")
    private String providerCode;

    @Schema(description = "引用语义类型（USER/DEPT/ROLE/MATERIAL...）", example = "USER")
    private String semanticType;
}
