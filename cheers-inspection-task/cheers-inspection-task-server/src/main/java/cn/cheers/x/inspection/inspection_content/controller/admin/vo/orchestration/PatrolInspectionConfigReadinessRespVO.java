package cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "巡检执行配置是否可生成步骤图")
public class PatrolInspectionConfigReadinessRespVO {

    @Schema(description = "无缺口时为 true")
    private boolean ready;

    @Schema(description = "缺口列表；ready 为 false 时非空")
    private List<PatrolInspectionConfigGapVO> gaps;
}
