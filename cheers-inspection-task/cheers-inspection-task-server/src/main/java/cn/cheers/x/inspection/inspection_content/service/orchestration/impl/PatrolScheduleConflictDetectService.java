package cn.cheers.x.inspection.inspection_content.service.orchestration.impl;

import cn.cheers.x.inspection.inspection_content.service.orchestration.PatrolScheduleMapService;
import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapRespDTO;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleConflictReportDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.runtime.api.RuntimeQueryApi;
import cn.cheers.x.module.platform.scheduling.conflict.ScheduleConflictReporter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

/**
 * 冲突检测：按已保存排期模板展开计划点，查指定设备已有占窗是否重叠，只出报告。
 * <p>「已有」只认别人的占窗。本任务历次试排/生成留下的窗口（工作项号或作业号对得上）必须排除。
 * <p>不管：挪已有、换设备、写试排快照、生成步骤图、释放残留占窗。设备只认执行设备绑定。
 */
@Service
@RequiredArgsConstructor
public class PatrolScheduleConflictDetectService {

    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Shanghai");

    private final PatrolScheduleMapService patrolScheduleMapService;
    private final PatrolTaskEntityStore patrolTaskEntityStore;
    private final RuntimeQueryApi runtimeQueryApi;

    public DetectedPlan detect(Long taskId, SchedulingSpecDTO incoming) {
        PatrolTaskDraft draft = patrolTaskEntityStore.require(taskId);
        requireSpecifiedDevice(draft);
        PatrolScheduleMapRespDTO expanded = patrolScheduleMapService.expandPatrolWorkItems(
                PatrolScheduleMapReqDTO.builder()
                        .taskId(taskId)
                        .fromSavedRouteSnapshot(true)
                        .expandWorkItemsFromScheduleTemplate(true)
                        .build());
        List<WorkItemDTO> workItems = expanded == null ? List.of() : expanded.getWorkItems();
        if (CollectionUtils.isEmpty(workItems)) {
            throw invalidParamException("排期模板没有展开出计划时刻");
        }
        PatrolTaskEntityStore.ArrangePolicy policy = patrolTaskEntityStore.readArrangePolicy(taskId);
        int gap = policy.taskGapMinutes() == null ? 0 : Math.max(0, policy.taskGapMinutes());
        List<ResourceReservationDTO> occupied = loadOccupied(incoming, draft);
        ScheduleConflictReportDTO report = ScheduleConflictReporter.report(workItems, occupied, gap);
        return new DetectedPlan(draft, workItems, report);
    }

    private static Long requireEquipmentId(PatrolTaskDraft draft) {
        ExecutionDeviceBinding binding = draft.executionDeviceBinding();
        if (binding == null || binding.getEquipmentId() == null) {
            throw invalidParamException("请先指定执行设备，再做冲突检测");
        }
        return binding.getEquipmentId();
    }

    private static void requireSpecifiedDevice(PatrolTaskDraft draft) {
        requireEquipmentId(draft);
    }

    private List<ResourceReservationDTO> loadOccupied(SchedulingSpecDTO incoming, PatrolTaskDraft draft) {
        OffsetDateTime from = horizonStart(incoming);
        OffsetDateTime to = horizonEnd(incoming);
        String resourceId = String.valueOf(requireEquipmentId(draft));
        List<ScheduleSlotDTO> slots = runtimeQueryApi.listSlots(
                from, to, resourceId, null, draft.facilityId(),
                List.of(SlotStatus.PLANNED, SlotStatus.IN_PROGRESS)).getCheckedData();
        if (CollectionUtils.isEmpty(slots)) {
            return List.of();
        }
        String ownJobId = draft.runtimeJobId();
        List<ResourceReservationDTO> occupied = new ArrayList<>(slots.size());
        for (ScheduleSlotDTO slot : slots) {
            if (slot == null) {
                continue;
            }
            // 本草稿自己写下的占窗不是「已有任务」，作业号被清掉时仍按工作项号认。
            if (PatrolTaskOwnOccupancy.isOwnSlot(draft.id(), ownJobId, slot)) {
                continue;
            }
            occupied.add(slot.toReservation());
        }
        return occupied;
    }

    private static OffsetDateTime horizonStart(SchedulingSpecDTO spec) {
        LocalDate start = parseHorizonDate(spec != null ? spec.getHorizonStart() : null);
        if (start == null) {
            start = LocalDate.now(DEFAULT_ZONE);
        }
        return start.atStartOfDay(DEFAULT_ZONE).toOffsetDateTime();
    }

    private static OffsetDateTime horizonEnd(SchedulingSpecDTO spec) {
        LocalDate end = parseHorizonDate(spec != null ? spec.getHorizonEnd() : null);
        if (end == null) {
            LocalDate start = parseHorizonDate(spec != null ? spec.getHorizonStart() : null);
            end = start != null ? start.plusWeeks(4) : LocalDate.now(DEFAULT_ZONE).plusWeeks(4);
        }
        return end.plusDays(1).atStartOfDay(DEFAULT_ZONE).toOffsetDateTime();
    }

    private static LocalDate parseHorizonDate(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        return LocalDate.parse(text.substring(0, Math.min(text.length(), 10)));
    }

    public record DetectedPlan(PatrolTaskDraft draft, List<WorkItemDTO> workItems, ScheduleConflictReportDTO report) {
    }
}
