package cn.cheers.x.inspection.task.service.query.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.inspection.task.dal.dataobject.schedule.InspectionTaskScheduleDO;
import cn.cheers.x.inspection.task.dal.dataobject.schedule.InspectionTaskSchedulePlanDO;
import cn.cheers.x.inspection.task.dal.mysql.schedule.InspectionTaskScheduleMapper;
import cn.cheers.x.inspection.task.dal.mysql.schedule.InspectionTaskSchedulePlanMapper;
import cn.cheers.x.inspection.task.service.query.InspectionTaskScheduleQueryService;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.inspection.task.service.query.model.InspectionTaskSchedulePlanView;
import cn.cheers.x.inspection.task.service.query.model.InspectionTaskScheduleView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class InspectionTaskScheduleQueryServiceImpl implements InspectionTaskScheduleQueryService {

    private final InspectionTaskSchedulePlanMapper inspectionTaskSchedulePlanMapper;
    private final InspectionTaskScheduleMapper inspectionTaskScheduleMapper;
    private final PatrolTaskEntityStore patrolTaskEntityStore;

    @Override
    public InspectionTaskSchedulePlanView getSchedulePlanView(Long planId) {
        InspectionTaskSchedulePlanDO planDO = validateSchedulePlanExists(planId);
        return BeanUtils.toBean(planDO, InspectionTaskSchedulePlanView.class);
    }

    @Override
    public InspectionTaskSchedulePlanView getSchedulePlanViewByCode(String planCode) {
        InspectionTaskSchedulePlanDO planDO = inspectionTaskSchedulePlanMapper.selectByPlanCode(planCode);
        if (planDO == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "调度计划批次不存在");
        }
        return BeanUtils.toBean(planDO, InspectionTaskSchedulePlanView.class);
    }

    @Override
    public List<InspectionTaskSchedulePlanView> getSchedulePlanViewListByHorizon(LocalDateTime startTime, LocalDateTime endTime) {
        return inspectionTaskSchedulePlanMapper.selectListByHorizon(startTime.toLocalDate(), endTime.toLocalDate()).stream()
                .map(plan -> BeanUtils.toBean(plan, InspectionTaskSchedulePlanView.class))
                .toList();
    }

    @Override
    public List<InspectionTaskScheduleView> getScheduleViewListByPlanId(Long planId) {
        validateSchedulePlanExists(planId);
        return buildScheduleViews(inspectionTaskScheduleMapper.selectByPlanId(planId));
    }

    @Override
    public List<InspectionTaskScheduleView> getScheduleViewListByTaskId(Long taskId) {
        return buildScheduleViews(inspectionTaskScheduleMapper.selectByTaskId(taskId));
    }

    @Override
    public List<InspectionTaskScheduleView> getScheduleViewListByPlannedTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return buildScheduleViews(inspectionTaskScheduleMapper.selectByTimeRange(startTime, endTime));
    }

    @Override
    public List<InspectionTaskScheduleView> getPendingSchedules(LocalDateTime beforeTime) {
        return buildScheduleViews(inspectionTaskScheduleMapper.selectPendingSchedules(beforeTime));
    }

    private InspectionTaskSchedulePlanDO validateSchedulePlanExists(Long planId) {
        InspectionTaskSchedulePlanDO planDO = inspectionTaskSchedulePlanMapper.selectById(planId);
        if (planDO == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "调度计划批次不存在");
        }
        return planDO;
    }

    private List<InspectionTaskScheduleView> buildScheduleViews(List<InspectionTaskScheduleDO> schedules) {
        if (schedules == null || schedules.isEmpty()) {
            return List.of();
        }
        Set<Long> planIds = schedules.stream().map(InspectionTaskScheduleDO::getPlanId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> taskIds = schedules.stream().map(InspectionTaskScheduleDO::getTaskId).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, InspectionTaskSchedulePlanDO> planMap = planIds.isEmpty() ? Map.of()
                : inspectionTaskSchedulePlanMapper.selectBatchIds(planIds.stream().toList()).stream()
                .collect(Collectors.toMap(InspectionTaskSchedulePlanDO::getId, Function.identity()));
        Map<Long, PatrolTaskDraft> taskMap = taskIds.isEmpty() ? Map.of()
                : patrolTaskEntityStore.listAll().stream()
                .filter(draft -> taskIds.contains(draft.id()))
                .collect(Collectors.toMap(PatrolTaskDraft::id, Function.identity(), (left, right) -> left));

        return schedules.stream().map(schedule -> {
            InspectionTaskScheduleView view = BeanUtils.toBean(schedule, InspectionTaskScheduleView.class);
            InspectionTaskSchedulePlanDO planDO = planMap.get(schedule.getPlanId());
            if (planDO != null) {
                view.setPlanCode(planDO.getPlanCode());
            }
            PatrolTaskDraft task = taskMap.get(schedule.getTaskId());
            if (task != null) {
                view.setTaskName(task.name());
            }
            return view;
        }).toList();
    }
}
