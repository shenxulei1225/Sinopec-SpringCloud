package cn.iocoder.yudao.module.system.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 分类类型更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryTypeUpdateReqVO extends CategoryTypeBaseVO {

    @Schema(description = "分类类型编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "分类类型编号不能为空")
    private Long id;
}
