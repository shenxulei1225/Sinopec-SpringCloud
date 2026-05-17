package cn.iocoder.yudao.module.system.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 分类批量更新 Request VO")
@Data
public class CategoryBatchUpdateReqVO {

    @Schema(description = "分类更新列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    @NotEmpty(message = "分类更新列表不能为空")
    private List<CategoryUpdateReqVO> categories;
}
