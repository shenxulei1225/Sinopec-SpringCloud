package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "实体变更模型 - 批量预览单条")
@Data
public class EntityChangeModelBatchItemVO {

    public static final String STATUS_READY = "READY";
    public static final String STATUS_NEEDS_PATCH = "NEEDS_PATCH";
    public static final String STATUS_BLOCKED = "BLOCKED";

    @Schema(description = "实体 ID")
    private Long entityId;

    @Schema(description = "实体名称")
    private String entityName;

    @Schema(description = "READY / NEEDS_PATCH / BLOCKED")
    private String status;

    @Schema(description = "不可迁或需补填时的说明")
    private String reason;

    @Schema(description = "源模型名称")
    private String sourceModelName;

    @Schema(description = "保留字段数")
    private Integer keptFieldCount;

    @Schema(description = "将归档字段数")
    private Integer archivedFieldCount;

    @Schema(description = "需补填的必填字段")
    private List<EntityChangeModelFieldItemVO> missingRequired = new ArrayList<>();

    @Schema(description = "是否跨业务域")
    private Boolean domainChanged;
}
