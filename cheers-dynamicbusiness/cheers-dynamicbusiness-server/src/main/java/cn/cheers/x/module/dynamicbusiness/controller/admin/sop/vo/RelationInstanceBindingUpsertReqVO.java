package cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "通用宿主-对象-维度-目标绑定 upsert")
@Data
public class RelationInstanceBindingUpsertReqVO {

    @NotBlank
    private String hostType;

    @NotNull
    private Long hostId;

    @NotBlank
    private String subjectType;

    @NotNull
    private Long subjectId;

    @NotBlank
    private String dimensionKey;

    @NotBlank
    private String dimensionValue;

    @NotBlank
    @Schema(description = "绑定目标实体类型码（调用方传入）")
    private String targetType;

    @NotNull
    @Schema(description = "绑定目标实体 id")
    private Long targetId;
}
