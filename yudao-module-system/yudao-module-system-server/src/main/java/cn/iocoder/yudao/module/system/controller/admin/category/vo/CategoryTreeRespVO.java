package cn.iocoder.yudao.module.system.controller.admin.category.vo;

import cn.iocoder.yudao.framework.category.core.CategoryVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Schema(description = "管理后台 - 分类树响应 VO")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CategoryTreeRespVO extends CategoryRespVO implements CategoryVO<Long, CategoryTreeRespVO> {

    @Schema(description = "子节点")
    private List<CategoryTreeRespVO> children;
}
