package cn.cheers.x.inspection.task.controller.admin.vo.task;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理后台 - 巡检任务分页 Request VO
 *
 * <p>用于列表分页查询，不是树形结构。</p>
 * <p>查看子任务请使用：getSubTasks(parentId)</p>
 */
@Schema(description = "管理后台 - 巡检任务分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskPageReqVO extends PageParam {

    @Schema(description = "任务分类 ID")
    private Long categoryId;

    @Schema(description = "任务名称")
    private String taskName;

    @Schema(description = "任务编码")
    private String taskCode;

    @Schema(description = "任务状态")
    private Integer status;

    @Schema(description = "是否启用")
    private Boolean enabled;
}
