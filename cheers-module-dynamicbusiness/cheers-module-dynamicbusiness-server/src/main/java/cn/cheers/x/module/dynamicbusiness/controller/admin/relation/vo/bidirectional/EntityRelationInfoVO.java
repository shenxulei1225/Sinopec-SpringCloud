package cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.bidirectional;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 实体关联信息 VO
 * 
 * <p>用于展示实体的完整关联信息，包括正向和反向关联。</p>
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 实体关联信息 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityRelationInfoVO {

    @Schema(description = "实体 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    private Long entityId;

    @Schema(description = "实体名称", example = "2024年1月生产计划")
    private String entityName;

    @Schema(description = "Model 编码", example = "production_plan")
    private String modelCode;

    @Schema(description = "Model 名称", example = "生产计划")
    private String modelName;

    @Schema(description = "正向关联列表（当前实体引用的其他实体）")
    private List<RelatedEntityVO> forwardRelations;

    @Schema(description = "反向关联列表（引用当前实体的其他实体）")
    private List<RelatedEntityVO> reverseRelations;

    @Schema(description = "关联统计信息")
    private RelationStatisticsVO statistics;
}
