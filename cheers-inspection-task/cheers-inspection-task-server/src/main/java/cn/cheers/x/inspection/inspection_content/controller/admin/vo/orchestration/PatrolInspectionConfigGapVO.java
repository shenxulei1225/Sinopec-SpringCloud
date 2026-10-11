package cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "巡检执行配置缺口（第 3 步生成步骤图前）")
public class PatrolInspectionConfigGapVO {

    @Schema(description = "缺口编码", example = "EMPTY_INSPECTION_METHOD")
    private String code;

    @Schema(description = "给人看的说明")
    private String message;

    @Schema(description = "关联检查项 id，可空")
    private Long itemId;
}
