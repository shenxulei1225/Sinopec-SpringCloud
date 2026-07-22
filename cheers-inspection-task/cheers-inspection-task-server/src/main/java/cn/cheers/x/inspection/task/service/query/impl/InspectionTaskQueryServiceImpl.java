package cn.cheers.x.inspection.task.service.query.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.route.InspectionRoutePlanDO;
import cn.cheers.x.inspection.inspection_content.dal.mysql.route.InspectionRoutePlanMapper;
import cn.cheers.x.inspection.inspection_content.service.enhance.ContentEnhancementService;
import cn.cheers.x.inspection.task.controller.admin.vo.task.*;
import cn.cheers.x.inspection.task.dal.dataobject.schedule.InspectionTaskSchedulePlanDO;
import cn.cheers.x.inspection.task.dal.dataobject.task.InspectionTaskDO;
import cn.cheers.x.inspection.task.dal.mysql.schedule.InspectionTaskSchedulePlanMapper;
import cn.cheers.x.inspection.task.dal.mysql.task.InspectionTaskMapper;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.query.InspectionTaskQueryService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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
    private final InspectionRoutePlanMapper routePlanMapper;
    private final ObjectMapper objectMapper;

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

        // 4. 已保存路线读模型（confirm 快照）
        enrichSavedRoute(respVO, taskDO);

        // 5. 查询直接子任务（轻量级）
        List<InspectionTaskDO> subTaskDOs = inspectionTaskMapper.selectByParentId(id);
        if (subTaskDOs != null && !subTaskDOs.isEmpty()) {
            List<InspectionTaskSubTaskVO> subTasks = BeanUtils.toBean(subTaskDOs, InspectionTaskSubTaskVO.class);
            respVO.setSubTasks(subTasks);
        } else {
            respVO.setSubTasks(Collections.emptyList());
        }

        return respVO;
    }

    /**
     * 从任务 DO / 路线方案台账组装 stopSequence、startStopId、routePreview。
     * 无已确认路线时保持字段为空，不编造。
     */
    private void enrichSavedRoute(InspectionTaskRespVO respVO, InspectionTaskDO taskDO) {
        respVO.setRoutePlanId(taskDO.getRoutePlanId());
        respVO.setNetworkRef(taskDO.getNetworkRef());
        respVO.setInspectionType(taskDO.getInspectionType());
        respVO.setDurationEstimateMinutes(taskDO.getDurationEstimateMinutes());

        if (!StringUtils.hasText(taskDO.getPlannedRoute()) && taskDO.getRoutePlanId() == null) {
            respVO.setStopSequence(Collections.emptyList());
            return;
        }

        Map<String, Object> planned = parseJsonMap(taskDO.getPlannedRoute());
        List<String> stopSequence = resolveStopSequence(taskDO, planned);
        respVO.setStopSequence(stopSequence);

        String startStopId = asString(planned != null ? planned.get("startStopId") : null);
        respVO.setStartStopId(startStopId);

        if (!StringUtils.hasText(taskDO.getNetworkRef()) && CollectionUtils.isEmpty(stopSequence)
                && planned == null) {
            return;
        }

        TaskSavedRoutePreviewVO preview = new TaskSavedRoutePreviewVO();
        preview.setNetworkRef(taskDO.getNetworkRef());
        if (planned != null && StringUtils.hasText(asString(planned.get("networkRef")))) {
            preview.setNetworkRef(asString(planned.get("networkRef")));
        }
        preview.setStopIds(new ArrayList<>(stopSequence));
        preview.setTotalDistanceMeters(asLong(planned != null ? planned.get("totalDistanceMeters") : null));
        preview.setMobilityProfileId(asString(planned != null ? planned.get("mobilityProfileId") : null));
        preview.setStartStopId(startStopId);
        preview.setReturnToStart(asBoolean(planned != null ? planned.get("returnToStart") : null));
        preview.setDecisionTraceId(asString(planned != null ? planned.get("decisionTraceId") : null));
        respVO.setRoutePreview(preview);
    }

    private List<String> resolveStopSequence(InspectionTaskDO taskDO, Map<String, Object> planned) {
        if (taskDO.getRoutePlanId() != null) {
            InspectionRoutePlanDO plan = routePlanMapper.selectById(taskDO.getRoutePlanId());
            if (plan != null && StringUtils.hasText(plan.getStopIds())) {
                List<String> fromPlan = parseStringList(plan.getStopIds());
                if (!CollectionUtils.isEmpty(fromPlan)) {
                    return fromPlan;
                }
            }
        }
        if (planned != null) {
            List<String> fromPlanned = asStringList(planned.get("stopIds"));
            if (!CollectionUtils.isEmpty(fromPlanned)) {
                return fromPlanned;
            }
        }
        return Collections.emptyList();
    }

    private Map<String, Object> parseJsonMap(String json) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            log.warn("[getTaskDetail] plannedRoute JSON 无效, skip route enrich: {}", ex.getMessage());
            return null;
        }
    }

    private List<String> parseStringList(String json) {
        if (!StringUtils.hasText(json)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    private static List<String> asStringList(Object raw) {
        if (!(raw instanceof List<?> list)) {
            return Collections.emptyList();
        }
        List<String> out = new ArrayList<>(list.size());
        for (Object item : list) {
            if (item != null) {
                out.add(String.valueOf(item));
            }
        }
        return out;
    }

    private static String asString(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value);
        return StringUtils.hasText(text) ? text : null;
    }

    private static Long asLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static Boolean asBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }
}
