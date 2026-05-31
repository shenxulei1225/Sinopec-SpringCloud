package cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 分类批量删除 Request VO")
@Data
public class CategoryBatchDeleteReqVO {

    @Schema(description = "分类编号列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1,2,3]")
    @NotEmpty(message = "分类编号列表不能为空")
    private List<Long> ids;

    @Schema(description = "分类类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "region")
    @NotNull(message = "分类类型编码不能为空")
    private String categoryTypeCode;
}   