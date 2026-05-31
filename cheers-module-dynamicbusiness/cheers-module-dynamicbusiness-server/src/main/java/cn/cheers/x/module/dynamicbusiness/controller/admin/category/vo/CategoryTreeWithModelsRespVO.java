package cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo;

import cn.cheers.x.module.dynamicbusiness.framework.category.core.CategoryVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema(description = "管理后台 - 分类树（含模型）响应 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryTreeWithModelsRespVO extends CategoryRespVO
        implements CategoryVO<Long, CategoryTreeWithModelsRespVO> {

    @Schema(description = "子节点")
    @Getter
    @Setter(lombok.AccessLevel.NONE)
    private List<CategoryTreeWithModelsRespVO> children;

    @Override
    public void setChildren(List<CategoryTreeWithModelsRespVO> children) {
        this.children = children;
    }

    @Schema(description = "当前分类下的模型列表（按关联 sort 排序）")
    private List<ModelRespVO> models;
}
