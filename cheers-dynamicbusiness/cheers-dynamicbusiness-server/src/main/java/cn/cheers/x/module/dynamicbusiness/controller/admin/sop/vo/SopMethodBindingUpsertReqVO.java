package cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "SOP 方法选用 upsert")
@Data
public class SopMethodBindingUpsertReqVO {

    @NotBlank
    @Schema(description = "对象实体类型码（调用方传入，服务端不写死业务值）")
    private String subjectType;

    @NotNull
    private Long subjectId;

    @NotBlank
    @Schema(description = "维度键，如执行手段字段名")
    private String dimensionKey;

    @NotBlank
    @Schema(description = "维度取值")
    private String dimensionValue;

    @NotNull
    @Schema(description = "SOP 模板实体 id")
    private Long sopTemplateId;
}
