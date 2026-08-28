package cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Schema(description = "SOP 实例升格为新模板 Request")
@Data
public class SopPromoteReqVO {

    @Schema(description = "新模板名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "名称不能为空")
    private String name;

    @Schema(description = "挂接的 SOP 分类 id 列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "至少指定一个分类")
    private List<Long> categoryIds;
}
