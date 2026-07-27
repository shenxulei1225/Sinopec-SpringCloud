package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "实体变更模型 - 预览 Response VO")
@Data
public class EntityChangeModelPreviewRespVO {

    @Schema(description = "实体 ID")
    private Long entityId;

    @Schema(description = "源模型")
    private EntityChangeModelModelSummaryVO sourceModel;

    @Schema(description = "目标模型")
    private EntityChangeModelModelSummaryVO targetModel;

    @Schema(description = "按 fieldCode 交集保留的字段")
    private List<EntityChangeModelFieldItemVO> keptFields = new ArrayList<>();

    @Schema(description = "目标模型必填但源无值，需用户补填")
    private List<EntityChangeModelFieldItemVO> missingRequired = new ArrayList<>();

    @Schema(description = "源有、目标无，将归档的字段")
    private List<EntityChangeModelFieldItemVO> archivedFields = new ArrayList<>();

    @Schema(description = "变更前的业务域；空表示未划域")
    private String sourceDomain;

    @Schema(description = "变更后的业务域，取自目标模型；空表示未划域")
    private String targetDomain;

    @Schema(description = "是否跨业务域变更")
    private boolean domainChanged;

    @Schema(description = "该实体的有效分类关联数；跨业务域时这些关联的业务域会同步迁移")
    private int relationCount;

    @Schema(description = "提示信息")
    private List<String> warnings = new ArrayList<>();
}
