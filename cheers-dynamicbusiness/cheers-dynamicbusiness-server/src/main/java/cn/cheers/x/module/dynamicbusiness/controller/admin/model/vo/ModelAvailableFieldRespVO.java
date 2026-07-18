package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 模型可用字段")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelAvailableFieldRespVO {

    @Schema(description = "字段编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "name")
    private String fieldCode;

    @Schema(description = "字段名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "设备名称")
    private String fieldName;

    @Schema(description = "字段类型", example = "TEXT")
    private String fieldType;

    @Schema(description = "字段来源：BASE=固定列字段，CUSTOM=扩展字段", example = "BASE")
    private String fieldSource;
}

