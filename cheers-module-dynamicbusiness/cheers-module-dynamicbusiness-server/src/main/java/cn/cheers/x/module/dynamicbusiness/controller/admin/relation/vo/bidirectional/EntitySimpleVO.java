package cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.bidirectional;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 实体简要信息 VO
 * 
 * <p>用于关联查询结果中展示实体的基本信息。</p>
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 实体简要信息 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntitySimpleVO {

    @Schema(description = "实体 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "实体名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "生产任务-001")
    private String name;

    @Schema(description = "实体编码", example = "TASK-001")
    private String code;

    @Schema(description = "Model ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long modelId;

    @Schema(description = "Model 编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "production_task")
    private String modelCode;

    @Schema(description = "Model 名称", example = "生产任务")
    private String modelName;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "显示字段值（使用名称字段配置）", example = "张三")
    private String displayValue;
}
