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

    @Schema(description = "变更前的业务域；空表示未划域")
    private String sourceDomain;

    @Schema(description = "变更后的业务域，取自目标模型；空表示未划域")
    private String targetDomain;

    @Schema(description = "是否跨业务域变更")
    private boolean domainChanged;

    @Schema(description = "同步迁移业务域的分类关联数")
    private int syncedRelationCount;

    @Schema(description = "为目标型号新补建的型号–分类关联数（实体已挂分类、型号列可见）")
    private int linkedModelCategoryCount;
}
