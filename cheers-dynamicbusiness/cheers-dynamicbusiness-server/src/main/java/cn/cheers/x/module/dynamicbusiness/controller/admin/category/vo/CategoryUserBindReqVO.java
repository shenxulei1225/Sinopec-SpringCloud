package cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 分类绑定/解绑系统用户 Request VO")
@Data
public class CategoryUserBindReqVO {

    @Schema(description = "分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "分类编号不能为空")
    private Long categoryId;

    @Schema(description = "系统用户编号列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "用户编号列表不能为空")
    private List<Long> userIds;
}
