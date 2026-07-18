package cn.cheers.x.inspection.task.service.query.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.inspection.inspection_content.service.enhance.ContentEnhancementService;
import cn.cheers.x.inspection.task.controller.admin.vo.task.*;
import cn.cheers.x.inspection.task.dal.dataobject.schedule.InspectionTaskSchedulePlanDO;
import cn.cheers.x.inspection.task.dal.dataobject.task.InspectionTaskDO;
import cn.cheers.x.inspection.task.dal.mysql.schedule.InspectionTaskSchedulePlanMapper;
import cn.cheers.x.inspection.task.dal.mysql.task.InspectionTaskMapper;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.query.InspectionTaskQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

/**
 * 巡检任务查询服务实现类。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionTaskQueryServiceImpl implements InspectionTaskQueryService {

    private final InspectionTaskMapper inspectionTaskMapper;
    private final InspectionTaskSchedulePlanMapper schedulePlanMapper;
    private final ContentEnhancementService contentEnhancementService;

    // ==================== 分页查询 ====================

    @Override
    public PageResult<InspectionTaskSimpleRespVO> getTaskPage(InspectionTaskPageReqVO pageReqVO) {
        // 1. 查询任务分页
        PageResult<InspectionTaskDO> pageResult = inspectionTaskMapper.selectPage(pageReqVO);
        if (pageResult.getList().isEmpty()) {
            return new PageResult<>(Collections.emptyList(), pageResult.getTotal());
        }

        List<Long> taskIds = pageResult.getList().stream()
                .map(InspectionTaskDO::getId)
                .collect(Collectors.toList());

        // 2. 批量查询激活的排期计划
        List<InspectionTaskSchedulePlanDO> planList = schedulePlanMapper.selectActivatedPlansByTaskIds(taskIds);
        Map<Long, InspectionTaskSchedulePlanDO> planMap = planList.stream()
                .collect(Collectors.toMap(InspectionTaskSchedulePlanDO::getTaskId, p -> p, (v1, v2) -> v1));

        // 3. 批量查询子任务数量
        Map<Long, Long> subTaskCountMap = inspectionTaskMapper.countSubTasksByParentIds(taskIds);

        // 4. 转换并填充信息
        List<InspectionTaskSimpleRespVO> voList = pageResult.getList().stream()
                .map(task -> {
                    InspectionTaskSimpleRespVO vo = BeanUtils.toBean(task, InspectionTaskSimpleRespVO.class);
                    // 填充子任务数量
                    Long subTaskCount = subTaskCountMap.get(task.getId());
                    vo.setSubTaskCount(subTaskCount != null ? subTaskCount.intValue() : 0);
                    // 填充排期结果信息
                    InspectionTaskSchedulePlanDO plan = planMap.get(task.getId());
                    if (plan != null) {
                        vo.setActivePlanId(plan.getId());
                        vo.setActivePlanCode(plan.getPlanCode());
                        vo.setActivePlanStatus(plan.getPlanStatus());
                        vo.setHorizonStartDate(plan.getHorizonStartDate());
                        vo.setHorizonEndDate(plan.getHorizonEndDate());
                        vo.setScheduleCount(plan.getScheduleCount());
                    }
                    return vo;
                })
                .collect(Collectors.toList());

        return new PageResult<>(voList, pageResult.getTotal());
    }

    // ==================== 详情查询 ====================

    @Override
    public InspectionTaskRespVO getTaskDetail(Long id) {
        // 1. 查询任务基础信息
        InspectionTaskDO taskDO = inspectionTaskMapper.selectById(id);
        if (taskDO == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "任务不存在");
        }

        // 2. 转换为 RespVO
        InspectionTaskRespVO respVO = BeanUtils.toBean(taskDO, InspectionTaskRespVO.class);

        // 3. 处理 inspectionContent
        InspectionContent content = taskDO.getInspectionContent();
        if (content != null) {
            content = contentEnhancementService.enhanceContent(content, true);
            respVO.setInspectionContent(content);
        }

        // 4. 查询直接子任务（轻量级）
        List<InspectionTaskDO> subTaskDOs = inspectionTaskMapper.selectByParentId(id);
        if (subTaskDOs != null && !subTaskDOs.isEmpty()) {
            List<InspectionTaskSubTaskVO> subTasks = BeanUtils.toBean(subTaskDOs, InspectionTaskSubTaskVO.class);
            respVO.setSubTasks(subTasks);
        } else {
            respVO.setSubTasks(Collections.emptyList());
        }

        return respVO;
    }
}
