package cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "SOP 实例绑定 upsert")
@Data
public class SopInstanceBindingUpsertReqVO {

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

    @NotNull
    @Schema(description = "SOP 实例 id")
    private Long sopInstanceId;
}
