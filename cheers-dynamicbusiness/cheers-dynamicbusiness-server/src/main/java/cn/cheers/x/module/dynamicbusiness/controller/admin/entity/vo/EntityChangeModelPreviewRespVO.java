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

    @Schema(description = "提示信息")
    private List<String> warnings = new ArrayList<>();
}
