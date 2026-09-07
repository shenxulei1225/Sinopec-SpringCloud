package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.CategoryIdGroupReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Schema(description = "按分类—实体求交结果派生型号列表请求")
@Data
public class ModelFromEntityCategoryGroupsReqVO {

    @Schema(description = "实体存储类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "entityTypeCode 不能为空")
    private String entityTypeCode;

    @Schema(description = "单维时的分类体系编码；多组时各组可自带 categoryTypeCode", example = "equipment")
    private String categoryTypeCode;

    @Schema(description = "多独立栏分类求交组；与 categoryIds 二选一，优先 groups")
    private List<CategoryIdGroupReqVO> categoryIdGroups;

    @Schema(description = "单维分类节点（兼容）；有 categoryIdGroups 时忽略", example = "[100]")
    private List<Long> categoryIds;

    @Schema(description = "业务域（可选）", example = "巡检")
    private String domain;

    @Schema(description = "关键词（可选，匹配模型名称/描述）", example = "阀门")
    private String keyword;

    @Schema(description = "是否按分类树展开子孙后再求交（默认 true；false 时仅用传入的分类 id）", example = "true")
    private Boolean includeDescendants;
}
