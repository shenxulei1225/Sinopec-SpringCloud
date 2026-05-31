package cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 字段更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class FieldUpdateReqVO extends FieldBaseVO {

    @Schema(description = "字段ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "字段ID不能为空")
    private Long id;
}

