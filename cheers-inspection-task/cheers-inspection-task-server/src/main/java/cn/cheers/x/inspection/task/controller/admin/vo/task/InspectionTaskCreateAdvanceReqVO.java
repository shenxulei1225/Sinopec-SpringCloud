package cn.cheers.x.inspection.task.controller.admin.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 建任务放行下一步。
 *
 * <p>只能放到「当前已放行 + 1」。已经放过的再请求同一格，原样返回。</p>
 */
@Data
@Schema(description = "建任务放行下一步")
public class InspectionTaskCreateAdvanceReqVO {

    @NotNull(message = "要放到哪一步不能为空")
    @Schema(description = "要放到哪一步：1 路线 / 2 资源 / 3 排期", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer toStep;
}
