package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "实体变更模型 - 批量预览 Response VO")
@Data
public class EntityChangeModelBatchPreviewRespVO {

    @Schema(description = "目标模型")
    private EntityChangeModelModelSummaryVO targetModel;

    @Schema(description = "可直接迁移")
    private List<EntityChangeModelBatchItemVO> ready = new ArrayList<>();

    @Schema(description = "需补填必填字段后才能迁")
    private List<EntityChangeModelBatchItemVO> needsPatch = new ArrayList<>();

    @Schema(description = "不可迁移")
    private List<EntityChangeModelBatchItemVO> blocked = new ArrayList<>();

    @Schema(description = "可迁数量")
    private int readyCount;

    @Schema(description = "需补填数量")
    private int needsPatchCount;

    @Schema(description = "不可迁数量")
    private int blockedCount;
}
