package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "实体变更模型 - 批量提交 Response VO")
@Data
public class EntityChangeModelBatchCommitRespVO {

    @Schema(description = "成功条数")
    private int successCount;

    @Schema(description = "失败条数")
    private int failureCount;

    @Schema(description = "成功明细")
    private List<EntityChangeModelCommitRespVO> successes = new ArrayList<>();

    @Schema(description = "失败明细")
    private List<EntityChangeModelBatchItemVO> failures = new ArrayList<>();
}
