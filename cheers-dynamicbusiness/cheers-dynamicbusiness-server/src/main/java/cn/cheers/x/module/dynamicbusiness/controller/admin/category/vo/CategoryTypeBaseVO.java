package cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * 分类类型 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class CategoryTypeBaseVO {

    @Schema(description = "分类类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "project_phase")
    @NotEmpty(message = "分类类型编码不能为空")
    private String categoryTypeCode;

    @Schema(description = "分类类型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "项目阶段")
    @NotEmpty(message = "分类类型名称不能为空")
    private String name;

    @Schema(description = "分类类型描述", example = "用于管理项目执行的不同阶段")
    private String description;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(
        description = "分类建立方式。ADVANCED（高级分类）≡ 既有分类即实体管线；SIMPLE=简单分类。勿另开实现。",
        example = "SIMPLE",
        allowableValues = {"SIMPLE", "ADVANCED"}
    )
    private String categoryMode;

    @Schema(
        description = "实体与分类挂靠：SINGLE=同一实体在本种类树上只能挂一个节点；MULTI=可挂多个。默认 MULTI。",
        example = "MULTI",
        allowableValues = {"SINGLE", "MULTI"}
    )
    private String entityAssociationMode;

}