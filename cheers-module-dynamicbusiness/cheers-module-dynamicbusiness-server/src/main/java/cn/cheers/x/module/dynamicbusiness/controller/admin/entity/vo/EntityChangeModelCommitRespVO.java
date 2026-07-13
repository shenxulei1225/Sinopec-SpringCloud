package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "实体变更模型 - 提交 Response VO")
@Data
public class EntityChangeModelCommitRespVO {

    @Schema(description = "实体 ID")
    private Long entityId;

    @Schema(description = "变更后的模型 ID")
    private Long modelId;

    @Schema(description = "保留字段数")
    private int keptFieldCount;

    @Schema(description = "归档字段数")
    private int archivedFieldCount;
}
