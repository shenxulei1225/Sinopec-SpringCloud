package cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.bidirectional;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 关联统计 VO
 * 
 * <p>用于展示实体的关联统计信息。</p>
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 关联统计 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationStatisticsVO {

    @Schema(description = "实体 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    private Long entityId;

    @Schema(description = "总关联实体数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "15")
    private Long totalRelatedEntities;

    @Schema(description = "按 Model 分组的统计（Model 编码 -> 数量）", example = "{\"production_task\": 8, \"maintenance_task\": 5}")
    private Map<String, Long> byModel;

    @Schema(description = "按 Model 名称分组的统计（Model 名称 -> 数量）", example = "{\"生产任务\": 8, \"维修任务\": 5}")
    private Map<String, Long> byModelName;

    @Schema(description = "按状态分组的统计（状态 -> 数量）", example = "{\"completed\": 10, \"in_progress\": 3}")
    private Map<String, Long> byStatus;

    @Schema(description = "正向关联数量（当前实体引用的其他实体）", example = "3")
    private Long forwardRelationCount;

    @Schema(description = "反向关联数量（引用当前实体的其他实体）", example = "12")
    private Long reverseRelationCount;
}
