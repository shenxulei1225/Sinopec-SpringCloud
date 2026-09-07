package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.CategoryIdGroupReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Schema(description = "按多组分类—型号关联求交型号列表请求")
@Data
public class ModelFromModelCategoryGroupsReqVO {

    @Schema(description = "型号所属实体类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "entityTypeCode 不能为空")
    private String entityTypeCode;

    @Schema(description = "分类条件组；组内分类取并集，组间型号取交集", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "categoryIdGroups 不能为空")
    @Valid
    private List<CategoryIdGroupReqVO> categoryIdGroups;

    @Schema(description = "业务域（可选）", example = "巡检")
    private String domain;

    @Schema(description = "关键词（可选，匹配模型名称/描述）", example = "阀门")
    private String keyword;

    @Schema(description = "是否按分类树展开子孙后再求交（默认 true）", example = "true")
    private Boolean includeDescendants;
}
