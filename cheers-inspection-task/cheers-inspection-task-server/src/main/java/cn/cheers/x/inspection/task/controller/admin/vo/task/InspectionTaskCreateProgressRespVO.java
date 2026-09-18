package cn.cheers.x.inspection.task.controller.admin.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 建任务向导进度。
 *
 * <p>unlockedStep 是已经放行到哪一步，用户可以回头看 0 到这一步；
 * 还没放到的步骤不能点进去。</p>
 */
@Data
@Schema(description = "建任务向导进度")
public class InspectionTaskCreateProgressRespVO {

    @Schema(description = "已放行到哪一步：0 选对象 / 1 路线 / 2 排期与资源")
    private Integer unlockedStep;

    @Schema(description = "最后一步下标，当前是 2")
    private Integer lastStep;
}
