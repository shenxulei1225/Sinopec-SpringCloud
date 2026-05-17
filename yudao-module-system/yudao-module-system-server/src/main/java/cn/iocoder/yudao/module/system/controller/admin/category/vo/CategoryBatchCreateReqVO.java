package cn.iocoder.yudao.module.system.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 分类批量创建 Request VO")
@Data
public class CategoryBatchCreateReqVO {

    @Schema(description = "分类创建列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    @NotEmpty(message = "分类创建列表不能为空")
    private List<CategoryCreateReqVO> categories;
}
