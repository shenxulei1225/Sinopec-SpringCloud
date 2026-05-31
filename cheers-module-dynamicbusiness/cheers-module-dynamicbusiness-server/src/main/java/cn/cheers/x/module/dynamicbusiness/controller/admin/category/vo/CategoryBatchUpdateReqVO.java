package cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "管理后台 - 分类批量更新 Request VO")
@Data
public class CategoryBatchUpdateReqVO {

    @Schema(description = "分类列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "分类列表不能为空")
    @Size(max = 100, message = "批量更新数量不能超过100")
    @Valid
    private List<CategoryUpdateReqVO> categories;
}












































