package cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模型 CRUD 表单定义响应。
 */
@Schema(description = "管理后台 - 模型 CRUD 表单定义响应")
@Data
public class ModelCrudFormDefinitionRespVO {

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    private String entityTypeCode;

    @Schema(description = "模型编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    private Long modelId;

    @Schema(description = "CRUD 表单字段定义 JSON 字符串", requiredMode = Schema.RequiredMode.REQUIRED)
    private String crudFormFields;

    @Schema(description = "定义版本号", example = "8")
    private Long version;
}
