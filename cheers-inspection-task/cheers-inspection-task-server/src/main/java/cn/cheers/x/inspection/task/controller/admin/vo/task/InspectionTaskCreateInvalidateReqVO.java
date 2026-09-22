package cn.cheers.x.inspection.task.controller.admin.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 前面步骤改了，后面已放行的步骤作废。
 *
 * <p>优先传 reason（作废原因），由服务端按「方法和步骤」对照表决定保留到哪一步。
 * 未传 reason 时才用 keepThroughStep，兼容旧调用。</p>
 */
@Data
@Schema(description = "建任务后面步骤作废")
public class InspectionTaskCreateInvalidateReqVO {

    @Schema(description = "作废原因：EXECUTION_MEANS_CHANGED / OBJECTS_OR_ITEMS_CHANGED / ROUTE_SAVED / PATH_INPUTS_CHANGED / ITEM_ACTION_DURATION_CHANGED")
    private String reason;

    @Schema(description = "未传 reason 时：最多还保留到哪一步，更后面的作废")
    private Integer keepThroughStep;

    @Schema(description = "未传 reason 时：是否作废已保存路线")
    private Boolean clearPlannedRoute;
}
