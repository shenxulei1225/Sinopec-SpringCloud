package cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 字段创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class FieldCreateReqVO extends FieldBaseVO {

    @Schema(description = "新字段要挂载的型号 ID；传入本地型号时，字段随本地包写为 LOCAL", example = "1024")
    private Long modelId;
}

