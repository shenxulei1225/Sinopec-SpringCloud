package cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 字段创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class FieldCreateReqVO extends FieldBaseVO {
}

