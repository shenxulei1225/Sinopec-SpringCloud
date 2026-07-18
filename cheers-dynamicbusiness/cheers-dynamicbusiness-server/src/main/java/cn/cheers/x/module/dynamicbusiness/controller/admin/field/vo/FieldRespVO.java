package cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 字段响应 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class FieldRespVO extends FieldBaseVO {

    @Schema(description = "字段ID", example = "1024")
    private Long id;

    @Schema(description = "字段编码", example = "F-abc123")
    private String code;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

