package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模型—模型挂上或卸下的结果。
 */
@Schema(description = "管理后台 - 模型—模型关联操作响应")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelModelAssociationRespVO {

    @Schema(description = "操作类型 ASSOCIATE / DISASSOCIATE")
    private String operationType;

    @Schema(description = "来源模型")
    private Long sourceModelId;

    @Schema(description = "目标模型")
    private Long targetModelId;

    @Schema(description = "成功数")
    private Integer successCount;
}
