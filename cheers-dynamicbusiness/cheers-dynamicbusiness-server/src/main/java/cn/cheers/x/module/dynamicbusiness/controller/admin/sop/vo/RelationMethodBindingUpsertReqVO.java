package cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "通用对象-维度-目标绑定 upsert")
@Data
public class RelationMethodBindingUpsertReqVO {

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

    @NotBlank
    @Schema(description = "绑定目标实体类型码（调用方传入）")
    private String targetType;

    @NotNull
    @Schema(description = "绑定目标实体 id")
    private Long targetId;
}
