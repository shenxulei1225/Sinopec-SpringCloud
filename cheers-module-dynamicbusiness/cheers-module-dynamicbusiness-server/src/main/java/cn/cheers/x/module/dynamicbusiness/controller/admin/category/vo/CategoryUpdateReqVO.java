package cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 分类更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryUpdateReqVO extends CategoryBaseVO {

    @Schema(description = "分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "分类编号不能为空")
    private Long id;
}

