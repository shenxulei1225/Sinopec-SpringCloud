package cn.cheers.x.inspection.task.controller.admin.vo.task;

import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.ResourcePolicy;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理后台 - 巡检任务创建 Request VO
 *
 * <p>设计原则：最小必填，快速创建。</p>
 * <ul>
 *     <li>taskName - 唯一必填，用户必须明确任务名称</li>
 *     <li>categoryId - 可选，默认归属"未分类"</li>
 *     <li>taskCode - 可选，系统自动生成</li>
 *     <li>inspectionContent - 可选，允许先创建空任务，后续再配置巡检对象</li>
 *     <li>排期/资源策略 - 可选，创建后可单独配置</li>
 * </ul>
 */
@Schema(description = "管理后台 - 巡检任务创建 Request VO")
@Data
public class InspectionTaskCreateReqVO {

    // ==================== 唯一必填 ====================
    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "罐区A日常巡检")
    @NotBlank(message = "任务名称不能为空")
    @Size(max = 128, message = "任务名称长度不能超过128位")
    private String taskName;

    // ==================== 组织信息（可选） ====================
    @Schema(description = "父任务ID（用于创建子任务）")
    private Long parentId;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "任务编码（不填则自动生成）")
    @Size(max = 64, message = "任务编码长度不能超过64位")
    private String taskCode;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过500位")
    private String remark;

    @Schema(description = "业务域（任务分池；巡检创建页写「巡检」。不填则保持空，禁止服务端猜默认）", example = "巡检")
    @Size(max = 64, message = "业务域长度不能超过64位")
    private String domain;

    // ==================== 巡检内容（可选，后续配置） ====================
    @Schema(description = "巡检内容")
    private InspectionContent inspectionContent;

    // ==================== 排期配置（可选，后续配置） ====================
    @Schema(description = "是否继承父任务排期（默认false）")
    private Boolean inheritParentSchedule;

    @Schema(description = "排期策略ID")
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

    // ==================== 资源策略（可选，后续配置） ====================
    @Schema(description = "是否继承父任务资源策略（默认false）")
    private Boolean inheritParentResourcePolicy;

    @Schema(description = "资源策略")
    private ResourcePolicy resourcePolicy;

    @Schema(description = "执行设备绑定（绑设备时写入；开跑只读）")
    private ExecutionDeviceBinding executionDeviceBinding;

    @Schema(description = "所属设施 id（总任务必填）")
    private Long facilityId;

    @Schema(description = "巡检方式：MANUAL / UAV / ROBOT / FIXED_CAMERA")
    private String patrolExecutionMode;

    @Schema(description = "任务创建选定的起点（点位路网节点 id）")
    private String startStopId;

    @Schema(description = "任务创建选定的终点（点位路网节点 id）")
    private String endStopId;
}
