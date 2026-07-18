package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 实体更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class EntityUpdateReqVO extends EntityBaseVO {

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    @NotNull(message = "实体ID不能为空")
    private Long id;
}

