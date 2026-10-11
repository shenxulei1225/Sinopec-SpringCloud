package cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration;

import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 冲突检测、排期生成计划点、智能编排、生成任务共用请求。
 * <p>建任务向导带上这一页的勾选、路线、排期和执行设备。检测和生成计划点只拿这份计算，不写任务。
 * 生成任务再把当前方式写入已发布字段，并占用请求里已经算出的计划点。
 * 列表里启用已经生成的任务不带这一页，仍读已发布字段。
 */
@Data
public class PatrolOrchestrationRunReqVO {

    @NotNull(message = "taskId 不能为空")
    private Long taskId;

    @NotNull(message = "schedulingSpec 不能为空")
    private SchedulingSpecDTO schedulingSpec;

    /** 向导这一页。不带时读已发布字段。 */
    private WizardPage page;

    /** 生成任务时，页面上已经给用户看过的计划点。不在生成时重算。 */
    private List<ScheduleSlotDTO> slots;

    /** 生成任务时，页面上已经给出的已确认任务新时间。按原窗口编号更新，不重算。 */
    private List<ScheduleSlotDTO> shiftedSlots;

    /** 生成任务时，页面上已经给用户看过的步骤图。 */
    private Map<String, Object> executionStepTree;

    /**
     * 更新任务时，这一页要写入已保存草稿的内容。
     * 含当前方式，以及这一页上其他方式。第一次生成任务不带，服务也不写草稿。
     */
    private Map<String, Object> savedDraft;

    @Data
    public static class WizardPage {
        private String taskName;
        /** 这一页选的所属设施。生成任务要写进任务，缺了必填校验会拦住保存。 */
        private Long facilityId;
        private String patrolExecutionMode;
        private InspectionContent inspectionContent;
        private ExecutionDeviceBinding executionDeviceBinding;
        private Object scheduleConfig;
        private Map<String, Object> plannedRoute;
        private String startStopId;
        private String endStopId;
        /** 这一页上「智能编排」填的策略。没点开时为空，不从已发布字段补。 */
        private String conflictStrategy;
        private Integer taskGapMinutes;
        private Boolean allowShiftExisting;
        private Integer maxShiftMinutes;
    }
}
