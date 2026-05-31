package cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo;

import cn.cheers.x.module.dynamicbusiness.framework.category.core.CategoryVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema(description = "管理后台 - 分类树响应 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryTreeRespVO extends CategoryRespVO
        implements CategoryVO<Long, CategoryTreeRespVO> {

    @Schema(description = "子节点")
    @Getter
    @Setter(lombok.AccessLevel.NONE)
    private List<CategoryTreeRespVO> children;

    @Override
    public void setChildren(List<CategoryTreeRespVO> children) {
        this.children = children;
    }
}
