package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.CategoryIdGroupReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Schema(description = "模型统一列表查询请求")
@Data
public class ModelListBySceneReqVO {

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "entityTypeCode 不能为空")
    private String entityTypeCode;

    @Schema(description = "分类体系编码（可选，未传时默认使用 entityTypeCode）", example = "equipment")
    private String categoryTypeCode;

    @Schema(description = "单维分类节点（可选）", example = "[100]")
    private List<Long> categoryIds;

    @Schema(description = "多独立栏分类求交组（可选）")
    @Valid
    private List<CategoryIdGroupReqVO> categoryIdGroups;

    @Schema(description = "分类来源（MODEL_CATEGORY / ENTITY_CATEGORY）", example = "MODEL_CATEGORY")
    private String categoryFilterSource;

    @Schema(description = "分类过滤模式（NODE / CATEGORIZED / UNCATEGORIZED / NONE）", example = "NODE")
    private String filterMode;

    @Schema(description = "是否按分类树展开子孙后过滤（默认 true）", example = "true")
    private Boolean includeDescendants;

    @Schema(description = "业务域（可选）", example = "巡检")
    private String domain;

    @Schema(description = "关键词（可选，匹配模型名称/描述）", example = "阀门")
    private String keyword;

    @Schema(description = "字段筛选条件（可选）")
    @Valid
    private List<ModelFieldFilterReqVO> fieldFilters;
}
