package cn.iocoder.yudao.module.system.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 分类批量删除 Request VO")
@Data
public class CategoryBatchDeleteReqVO {

    @Schema(description = "分类编号列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "分类编号列表不能为空")
    private List<Long> ids;

    @Schema(description = "分类类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "分类类型编码不能为空")
    private String categoryTypeCode;

    @Schema(description = "是否级联删除子分类", example = "false")
    private Boolean cascade = false;
}
