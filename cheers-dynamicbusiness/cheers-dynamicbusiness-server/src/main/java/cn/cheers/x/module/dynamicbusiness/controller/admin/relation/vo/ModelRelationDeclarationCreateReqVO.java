package cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Model 关联声明 创建请求 VO
 */
@Schema(description = "管理后台 - Model 关联声明创建 Request VO")
@Data
public class ModelRelationDeclarationCreateReqVO {

    @Schema(description = "Model ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "Model ID 不能为空")
    private Long modelId;

    @Schema(description = "可关联的业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "personnel")
    @NotBlank(message = "目标业务类型不能为空")
    @Size(max = 64, message = "目标业务类型编码长度不能超过64个字符")
    private String targetEntityType;
}
