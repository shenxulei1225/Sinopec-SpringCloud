package cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Model 关联声明 响应 VO
 */
@Schema(description = "管理后台 - Model 关联声明 Response VO")
@Data
public class ModelRelationDeclarationRespVO {

    @Schema(description = "声明ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "Model ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long modelId;

    @Schema(description = "可关联的业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "personnel")
    private String targetBusinessType;

    @Schema(description = "目标业务类型名称", example = "人员管理")
    private String targetBusinessTypeName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;
}
