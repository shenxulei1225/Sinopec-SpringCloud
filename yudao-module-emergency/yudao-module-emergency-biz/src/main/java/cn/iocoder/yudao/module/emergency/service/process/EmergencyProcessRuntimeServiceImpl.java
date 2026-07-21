package cn.iocoder.yudao.module.emergency.service.process;

import cn.cheers.x.bpm.api.task.BpmProcessInstanceApi;
import cn.cheers.x.bpm.api.task.dto.BpmActivityNodeRespDTO;
import cn.cheers.x.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.cheers.x.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanRespVO;
import cn.iocoder.yudao.module.emergency.controller.admin.response.vo.ResponseStartReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventAssessDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventAssessMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventMapper;
import cn.iocoder.yudao.module.emergency.enums.EventStatus;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import cn.iocoder.yudao.module.emergency.service.plan.EmergencyPlanService;
import cn.iocoder.yudao.module.emergency.service.response.EmergencyResponseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应急指挥流程运行时（嵌入式 Flowable / cheers-bpm）——薄适配器。
 */
@Service
@Slf4j
public class EmergencyProcessRuntimeServiceImpl implements EmergencyProcessRuntimeService {

    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Resource
    private EmergencyEventMapper emergencyEventMapper;
    @Resource
    private EmergencyEventAssessMapper emergencyEventAssessMapper;
    @Resource
    private EmergencyPlanService planService;
    @Resource
    private EmergencyResponseService responseService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String startOnEventCreated(Long eventId, Long userId) {
        EmergencyEventDO event = requireEvent(eventId);
        if (StringUtils.hasText(event.getProcessInstanceId())) {
            return event.getProcessInstanceId();
        }

        BpmProcessInstanceCreateReqDTO req = new BpmProcessInstanceCreateReqDTO();
        req.setProcessDefinitionKey(EmergencyProcessKeys.DEFINITION_KEY);
        req.setBusinessKey(EmergencyProcessKeys.businessKey(eventId));
        Map<String, Object> variables = new HashMap<>();
        variables.put("eventId", eventId);
        variables.put("scope", "emergency");
        variables.put("initiator", String.valueOf(userId));
        req.setVariables(variables);

        String processInstanceId;
        try {
            processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, req).getCheckedData();
        } catch (Exception ex) {
            log.error("[startOnEventCreated] create process failed, eventId={}", eventId, ex);
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_PROCESS_DEFINITION_MISSING);
        }
        if (!StringUtils.hasText(processInstanceId)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_PROCESS_DEFINITION_MISSING);
        }

        EmergencyEventDO update = new EmergencyEventDO();
        update.setId(eventId);
        update.setProcessInstanceId(processInstanceId);
        emergencyEventMapper.updateById(update);
        return processInstanceId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeUserTask(Long eventId, Long userId, String taskDefinitionKey, Map<String, Object> vars) {
        requireStarted(eventId);

        List<BpmActivityNodeRespDTO> nodes = bpmProcessInstanceApi
                .getRunningTasksByBusinessKey(EmergencyProcessKeys.businessKey(eventId))
                .getCheckedData();
        BpmActivityNodeRespDTO matched = null;
        if (nodes != null) {
            for (BpmActivityNodeRespDTO node : nodes) {
                if (taskDefinitionKey.equals(node.getTaskDefinitionKey())) {
                    matched = node;
                    break;
                }
            }
        }
        if (matched == null || !StringUtils.hasText(matched.getTaskId())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_PROCESS_TASK_NOT_FOUND, taskDefinitionKey);
        }

        BpmTaskApproveReqDTO approveReq = new BpmTaskApproveReqDTO();
        approveReq.setId(matched.getTaskId());
        approveReq.setReason("emergency:" + taskDefinitionKey);
        approveReq.setVariables(vars);
        bpmProcessInstanceApi.approveTask(userId, approveReq).getCheckedData();
        projectLedgerStatus(eventId);
    }

    @Override
    public List<BpmActivityNodeRespDTO> listCurrentNodes(Long eventId) {
        requireStarted(eventId);
        return bpmProcessInstanceApi
                .getRunningTasksByBusinessKey(EmergencyProcessKeys.businessKey(eventId))
                .getCheckedData();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onServiceTaskStartResponse(Long eventId) {
        EmergencyEventDO event = requireStarted(eventId);

        EmergencyEventAssessDO latestAssess = emergencyEventAssessMapper.selectOne(
                new LambdaQueryWrapper<EmergencyEventAssessDO>()
                        .eq(EmergencyEventAssessDO::getEmergencyEventId, eventId)
                        .orderByDesc(EmergencyEventAssessDO::getId)
                        .last("LIMIT 1"));
        String responseLevel = latestAssess != null ? latestAssess.getNewLevel() : null;
        if (!StringUtils.hasText(responseLevel)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_PROCESS_TASK_NOT_FOUND,
                    "responseLevel(from assess)");
        }

        Integer planType = event.getEventType() != null ? event.getEventType().intValue() : null;
        List<EmergencyPlanRespVO> plans = planService.recommendPlans(responseLevel, planType);
        if (CollectionUtils.isEmpty(plans)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.RESPONSE_PLAN_MATCH_FAILED);
        }

        ResponseStartReqVO startReq = new ResponseStartReqVO();
        startReq.setEventId(eventId);
        startReq.setResponseLevel(responseLevel);
        startReq.setPlanId(plans.get(0).getId());
        startReq.setReason("流程服务任务触发启动响应（引擎路径）");
        responseService.start(startReq);

        EmergencyEventDO statusUpdate = new EmergencyEventDO();
        statusUpdate.setId(eventId);
        statusUpdate.setStatus(EventStatus.RESPONDING.getCode());
        emergencyEventMapper.updateById(statusUpdate);
        log.info("[onServiceTaskStartResponse] orch start done, eventId={}, planId={}",
                eventId, plans.get(0).getId());
    }

    @Override
    public void projectLedgerStatus(Long eventId) {
        EmergencyEventDO event = requireStarted(eventId);
        // 终态台账不被流程投影覆盖（误报取消 / 关闭）
        if (EventStatus.CANCELLED.getCode().equals(event.getStatus())
                || EventStatus.CLOSED.getCode().equals(event.getStatus())) {
            return;
        }
        List<BpmActivityNodeRespDTO> nodes;
        try {
            nodes = listCurrentNodes(eventId);
        } catch (Exception ex) {
            log.warn("[projectLedgerStatus] skip, eventId={}", eventId, ex);
            return;
        }
        String projected = null;
        if (nodes != null) {
            for (BpmActivityNodeRespDTO node : nodes) {
                if (EmergencyProcessKeys.TASK_CONFIRM.equals(node.getTaskDefinitionKey())) {
                    projected = EventStatus.PENDING.getCode();
                    break;
                }
                if (EmergencyProcessKeys.TASK_ASSESS.equals(node.getTaskDefinitionKey())) {
                    projected = EventStatus.WARNING.getCode();
                    break;
                }
            }
        }
        if (projected == null) {
            // 无用户任务：可能在服务任务或已结束 → 响应中投影（启动响应侧会再写）
            projected = EventStatus.RESPONDING.getCode();
        }
        if (!projected.equals(event.getStatus())) {
            EmergencyEventDO update = new EmergencyEventDO();
            update.setId(eventId);
            update.setStatus(projected);
            emergencyEventMapper.updateById(update);
        }
    }

    private EmergencyEventDO requireEvent(Long eventId) {
        EmergencyEventDO event = emergencyEventMapper.selectById(eventId);
        if (event == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_NOT_EXISTS);
        }
        return event;
    }

    private EmergencyEventDO requireStarted(Long eventId) {
        EmergencyEventDO event = requireEvent(eventId);
        if (!StringUtils.hasText(event.getProcessInstanceId())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_PROCESS_NOT_STARTED);
        }
        return event;
    }

}
