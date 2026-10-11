package cn.cheers.x.inspection.inspection_content.service.orchestration.impl;

import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapReqDTO;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.task.PatrolItemActionDurationCalculator;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

/**
 * 建任务向导这一页叠到内存里的任务内容上，用来生成计划点和步骤图。
 * <p>不管：不写已发布字段，不写草稿。请求里没有这一页的内容时，原样返回库里那份。
 */
final class PatrolWizardPageDraft {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private PatrolWizardPageDraft() {
    }

    static boolean hasPage(PatrolScheduleMapReqDTO request) {
        if (request == null) {
            return false;
        }
        return request.getPagePlannedRoute() != null
                || request.getPageScheduleConfig() != null
                || request.getPageInspectionContent() != null
                || request.getPageExecutionDeviceBinding() != null
                || StringUtils.hasText(request.getPagePatrolExecutionMode());
    }

    static PatrolTaskDraft overlay(
            PatrolTaskDraft stored,
            PatrolScheduleMapReqDTO request,
            PatrolItemActionDurationCalculator calculator) {
        if (stored == null || !hasPage(request)) {
            return stored;
        }
        String mode = StringUtils.hasText(request.getPagePatrolExecutionMode())
                ? request.getPagePatrolExecutionMode().trim()
                : stored.patrolExecutionMode();
        InspectionContent content = request.getPageInspectionContent() != null
                ? MAPPER.convertValue(request.getPageInspectionContent(), InspectionContent.class)
                : stored.inspectionContent();
        ExecutionDeviceBinding binding = request.getPageExecutionDeviceBinding() != null
                ? MAPPER.convertValue(request.getPageExecutionDeviceBinding(), ExecutionDeviceBinding.class)
                : stored.executionDeviceBinding();
        // 选了执行设备才按这一型号加总。还没选时，按型号分开的动作不算秒数。旧合计不拿来占这次的窗口。
        if (content != null && calculator != null) {
            Long executionEquipmentId = binding == null ? null : binding.getEquipmentId();
            calculator.computeLiveSecondsForEquipment(content, mode, executionEquipmentId)
                    .ifPresent(content::setItemActionDurationMinutes);
        }
        Object route = request.getPagePlannedRoute() != null ? request.getPagePlannedRoute() : stored.plannedRoute();
        Object schedule = request.getPageScheduleConfig() != null
                ? request.getPageScheduleConfig()
                : stored.scheduleConfig();
        String start = StringUtils.hasText(request.getPageStartStopId())
                ? request.getPageStartStopId()
                : stored.startStopId();
        String end = StringUtils.hasText(request.getPageEndStopId())
                ? request.getPageEndStopId()
                : stored.endStopId();
        return new PatrolTaskDraft(
                stored.id(),
                stored.name(),
                stored.domain(),
                stored.facilityId(),
                mode,
                content,
                binding,
                stored.resourcePolicy(),
                stored.stepTree(),
                route,
                stored.manageStatus(),
                stored.createUnlockedStep(),
                start,
                end,
                stored.runtimeJobId(),
                stored.orchestrationCommitted(),
                schedule,
                stored.schedulePolicyId(),
                stored.orchestrationPreviewSlots(),
                stored.orchestrationPreviewPlainSummary(), stored.updateHint());
    }
}
