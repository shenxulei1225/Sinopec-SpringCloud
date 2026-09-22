package cn.cheers.x.inspection.task.controller.admin.vo.task;

import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.ResourcePolicy;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理后台 - 巡检任务更新 Request VO
 *
 * <p>设计原则：所有字段均可选，只有 id 必填。</p>
 *
 * <p>不允许修改的字段：</p>
 * <ul>
 *     <li>taskCode - 任务编码不允许修改</li>
 *     <li>status - 状态由启用/禁用接口维护</li>
 *     <li>activePlanId, planIds - 编排结果由编排服务维护</li>
 * </ul>
 */
@Schema(description = "管理后台 - 巡检任务更新 Request VO")
@Data
public class InspectionTaskUpdateReqVO {

    // ==================== 唯一必填 ====================
    @Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "任务ID不能为空")
    private Long id;

    // ==================== 可选字段 ====================
    @Schema(description = "任务名称")
    @Size(max = 128, message = "任务名称长度不能超过128位")
    private String taskName;

    @Schema(description = "任务分类 ID")
    private Long categoryId;

    @Schema(description = "父任务 ID（用于重新挂载任务树）")
    private Long parentId;

    @Schema(description = "是否继承父任务排期规则")
    private Boolean inheritParentSchedule;

    @Schema(description = "是否继承父任务资源策略")
    private Boolean inheritParentResourcePolicy;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过500位")
    private String remark;

    @Schema(description = "业务域（任务分池；与编排 taskDomain 对齐）", example = "巡检")
    @Size(max = 64, message = "业务域长度不能超过64位")
    private String domain;

    // ==================== 任务内容 ====================
    @Schema(description = "巡检内容")
    private InspectionContent inspectionContent;

    // ==================== 排期与资源 ====================
    @Schema(description = "排期策略 ID")
    private Long schedulePolicyId;

    @Schema(description = "排期策略·冲突处理：defer_slot / reject_batch / priority_preempt")
    private String conflictStrategy;

    @Schema(description = "排期策略·任务间隔（分钟）")
    private Integer taskGapMinutes;

    @Schema(description = "空闲不够时是否允许挪动已有任务排期")
    private Boolean allowShiftExisting;

    @Schema(description = "已有任务单侧最多挪动分钟数")
    private Integer maxShiftMinutes;

    @Schema(description = "排期模板配置（与前端 ScheduleConfig JSON 对齐，写入 FLD-TSK-018）")
    private Object scheduleConfig;

    @Schema(description = "资源策略")
    private ResourcePolicy resourcePolicy;

    @Schema(description = "执行设备绑定（绑设备时写入；开跑只读）")
    private ExecutionDeviceBinding executionDeviceBinding;

    @Schema(description = "所属设施 id（总任务必填）")
    private Long facilityId;

    @Schema(description = "巡检方式：MANUAL / UAV / ROBOT / FIXED_CAMERA")
    private String patrolExecutionMode;

    @Schema(description = "任务创建选定的起点（点位路网节点 id）；空串表示清掉")
    private String startStopId;

    @Schema(description = "任务创建选定的终点（点位路网节点 id）；空串表示清掉")
    private String endStopId;
}
