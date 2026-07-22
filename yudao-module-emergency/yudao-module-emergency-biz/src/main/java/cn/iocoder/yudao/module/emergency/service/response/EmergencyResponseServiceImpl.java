package cn.iocoder.yudao.module.emergency.service.response;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.emergency.controller.admin.response.vo.*;
import cn.iocoder.yudao.module.emergency.convert.response.EmergencyResponseConvert;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.response.EmergencyResponseDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.response.EmergencyResponseHistoryDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskHistoryDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanStepMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.response.EmergencyResponseHistoryMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.response.EmergencyResponseMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.task.EmergencyTaskHistoryMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.task.EmergencyTaskMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.timeline.EmergencyTimelineMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.dispatch.ResourceDispatchMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.command.EmergencyCommandTemplateMapper;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandTemplateDO;
import cn.iocoder.yudao.module.emergency.service.task.EmergencyTaskService;
import cn.iocoder.yudao.module.emergency.service.dispatch.ResourceDispatchService;
import cn.iocoder.yudao.module.emergency.service.event.EventNotificationService;
import cn.iocoder.yudao.module.emergency.service.event.EventStateMachine;
import cn.iocoder.yudao.module.emergency.service.timeline.EmergencyProcessTimelineWriter;
import cn.iocoder.yudao.module.emergency.enums.EventStatus;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyStartResponseExpandRespDTO;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyStartResponseReqDTO;
import cn.cheers.x.module.platform.orchestration.api.OrchestrationRunApi;
import cn.cheers.x.module.platform.orchestration.api.dto.OrchestrationRunRequest;
import cn.cheers.x.module.platform.orchestration.api.dto.OrchestrationRunResponse;
import cn.cheers.x.module.platform.orchestration.enums.OrchestrationRefs;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventStatusHistoryDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventStatusHistoryMapper;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EmergencyResponseServiceImpl implements EmergencyResponseService {

    private static final Logger log = LoggerFactory.getLogger(EmergencyResponseServiceImpl.class);

    private final EmergencyEventMapper eventMapper;
    private final EmergencyResponseMapper responseMapper;
    private final EmergencyResponseHistoryMapper responseHistoryMapper;
    private final EmergencyPlanStepMapper planStepMapper;
    private final EmergencyTaskMapper taskMapper;
    private final EmergencyTaskHistoryMapper taskHistoryMapper;
    private final EmergencyTimelineMapper timelineMapper;
    private final ResourceDispatchMapper resourceDispatchMapper;
    private final EmergencyCommandTemplateMapper commandTemplateMapper;
    private final EmergencyTaskService taskService;
    private final ResourceDispatchService dispatchService;
    private final EventNotificationService notificationService;
    private final EventStateMachine stateMachine;
    private final EmergencyEventStatusHistoryMapper statusHistoryMapper;
    private final EmergencyProcessTimelineWriter processTimelineWriter;
    private final OrchestrationRunApi orchestrationRunApi;

    public EmergencyResponseServiceImpl(EmergencyEventMapper eventMapper,
                                        EmergencyResponseMapper responseMapper,
                                        EmergencyResponseHistoryMapper responseHistoryMapper,
                                        EmergencyPlanStepMapper planStepMapper,
                                        EmergencyTaskMapper taskMapper,
                                        EmergencyTaskHistoryMapper taskHistoryMapper,
                                        EmergencyTimelineMapper timelineMapper,
                                        ResourceDispatchMapper resourceDispatchMapper,
                                        EmergencyCommandTemplateMapper commandTemplateMapper,
                                        EmergencyTaskService taskService,
                                        ResourceDispatchService dispatchService,
                                        EventNotificationService notificationService,
                                        EventStateMachine stateMachine,
                                        EmergencyEventStatusHistoryMapper statusHistoryMapper,
                                        EmergencyProcessTimelineWriter processTimelineWriter,
                                        OrchestrationRunApi orchestrationRunApi) {
        this.eventMapper = eventMapper;
        this.responseMapper = responseMapper;
        this.responseHistoryMapper = responseHistoryMapper;
        this.planStepMapper = planStepMapper;
        this.taskMapper = taskMapper;
        this.taskHistoryMapper = taskHistoryMapper;
        this.timelineMapper = timelineMapper;
        this.resourceDispatchMapper = resourceDispatchMapper;
        this.commandTemplateMapper = commandTemplateMapper;
        this.taskService = taskService;
        this.dispatchService = dispatchService;
        this.notificationService = notificationService;
        this.stateMachine = stateMachine;
        this.statusHistoryMapper = statusHistoryMapper;
        this.processTimelineWriter = processTimelineWriter;
        this.orchestrationRunApi = orchestrationRunApi;
    }

    @Override
    public ResponseRespVO start(ResponseStartReqVO reqVO) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventId", reqVO.getEventId());
        payload.put("planId", reqVO.getPlanId());
        payload.put("responseLevel", reqVO.getResponseLevel());
        if (reqVO.getCommandOrg() != null) {
            payload.put("commandOrg", reqVO.getCommandOrg());
        }
        if (reqVO.getReason() != null) {
            payload.put("reason", reqVO.getReason());
        }

        OrchestrationRunResponse runResp = orchestrationRunApi.run(OrchestrationRunRequest.builder()
                        .orchestrationRef(OrchestrationRefs.EMERGENCY_START_RESPONSE_V1)
                        .scope("emergency")
                        .payload(payload)
                        .build())
                .getCheckedData();

        String responseNo = runResp != null && runResp.getResult() != null
                ? String.valueOf(runResp.getResult().get("responseNo"))
                : null;
        if (responseNo == null || "null".equals(responseNo) || responseNo.isBlank()) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.RESPONSE_NOT_EXISTS);
        }
        EmergencyResponseDO response = responseMapper.selectByResponseNo(responseNo);
        if (response == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.RESPONSE_NOT_EXISTS);
        }
        return EmergencyResponseConvert.INSTANCE.convert(response);
    }

    @Override
    public void validateStartForOrchestration(EmergencyStartResponseReqDTO req) {
        if (req == null || req.getEventId() == null || req.getPlanId() == null
                || req.getResponseLevel() == null || req.getResponseLevel().isBlank()) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_NOT_EXISTS);
        }
        var event = eventMapper.selectById(req.getEventId());
        if (event == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_NOT_EXISTS);
        }
        String commandOrg = req.getCommandOrg();
        if (commandOrg == null || commandOrg.isEmpty()) {
            commandOrg = event.getCommandOrg();
        }
        if (commandOrg == null || commandOrg.isEmpty()) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_RESPONDING_REQUIRES_LEADER);
        }
        // 提交兜底：同一事件禁止再开一条进行中响应（引擎路径 + HTTP 共用）
        if (responseMapper.selectLatestActiveByEventId(req.getEventId()) != null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.RESPONSE_ALREADY_ACTIVE);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public EmergencyStartResponseExpandRespDTO expandStartForOrchestration(EmergencyStartResponseReqDTO req) {
        Long tenantId = TenantContextHolder.getTenantId();
        Boolean ignore = TenantContextHolder.isIgnore();
        log.info("Orchestration expand start response eventId={}, tenantId={}, ignore={}",
                req.getEventId(), tenantId, ignore);

        var event = eventMapper.selectById(req.getEventId());
        if (event == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_NOT_EXISTS);
        }

        String commandOrg = req.getCommandOrg();
        if (commandOrg == null || commandOrg.isEmpty()) {
            commandOrg = event.getCommandOrg();
        }
        if (commandOrg == null || commandOrg.isEmpty()) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_RESPONDING_REQUIRES_LEADER);
        }

        if (responseMapper.selectLatestActiveByEventId(req.getEventId()) != null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.RESPONSE_ALREADY_ACTIVE);
        }

        if (req.getCommandOrg() != null && !req.getCommandOrg().isEmpty()
                && (event.getCommandOrg() == null || event.getCommandOrg().isEmpty()
                || !event.getCommandOrg().equals(req.getCommandOrg()))) {
            event.setCommandOrg(req.getCommandOrg());
            eventMapper.updateById(event);
        }

        EmergencyResponseDO response = new EmergencyResponseDO();
        response.setEventId(req.getEventId());
        response.setResponseLevel(req.getResponseLevel());
        response.setPlanId(req.getPlanId());
        response.setStatus("executing");
        response.setResponseNo("RESP-" + System.currentTimeMillis() + "-" + (System.nanoTime() % 10000));
        Map<String, Object> startInfo = new HashMap<>();
        if (req.getReason() != null && !req.getReason().isEmpty()) {
            startInfo.put("reason", req.getReason());
        }
        startInfo.put("responseLevel", req.getResponseLevel());
        startInfo.put("planId", req.getPlanId());
        startInfo.put("startTime", LocalDateTime.now().toString());
        if (req.getOrchestrationRef() != null) {
            startInfo.put("orchestrationRef", req.getOrchestrationRef());
        }
        response.setStartInfo(startInfo);
        responseMapper.insert(response);

        EmergencyResponseHistoryDO history = EmergencyResponseHistoryDO.builder()
                .responseId(response.getId())
                .type("start")
                .fromLevel(null)
                .toLevel(req.getResponseLevel())
                .reason(req.getReason())
                .build();
        responseHistoryMapper.insert(history);

        clonePlanStepsToTasks(
                req.getEventId(),
                response.getId(),
                req.getPlanId(),
                req.getResponseLevel());

        String currentEventStatus = event.getStatus();
        String targetStatus = EventStatus.RESPONDING.getCode();
        if (!targetStatus.equals(currentEventStatus)) {
            if ("assessing".equals(currentEventStatus)) {
                event.setStatus(targetStatus);
                eventMapper.updateById(event);
                recordEventStatusHistory(event.getId(), currentEventStatus, targetStatus, "启动应急响应");
                notificationService.sendEventStatusChangedNotification(event, currentEventStatus, targetStatus);
            } else {
                stateMachine.validateTransition(currentEventStatus, targetStatus);
                stateMachine.validateBusinessRules(event, targetStatus);
                event.setStatus(targetStatus);
                eventMapper.updateById(event);
                recordEventStatusHistory(event.getId(), currentEventStatus, targetStatus, "启动应急响应");
                notificationService.sendEventStatusChangedNotification(event, currentEventStatus, targetStatus);
            }
        }

        notificationService.sendResponseStartedNotification(event, response.getId());

        return EmergencyStartResponseExpandRespDTO.builder()
                .eventId(req.getEventId())
                .responseId(response.getId())
                .responseNo(response.getResponseNo())
                .responseLevel(req.getResponseLevel())
                .planId(req.getPlanId())
                .orchestrationRef(req.getOrchestrationRef())
                .build();
    }

    @Override
    public void persistStartForOrchestration(EmergencyStartResponseExpandRespDTO expandResult) {
        if (expandResult == null || expandResult.getEventId() == null) {
            return;
        }
        processTimelineWriter.appendEventAction(
                expandResult.getEventId(),
                "response.start",
                "启动响应 responseNo=" + expandResult.getResponseNo()
                        + ", level=" + expandResult.getResponseLevel()
                        + ", planId=" + expandResult.getPlanId()
                        + (expandResult.getOrchestrationRef() != null
                        ? ", orch=" + expandResult.getOrchestrationRef() : ""));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public void upgrade(String responseNo, ResponseUpgradeReqVO reqVO) {
        EmergencyResponseDO response = responseMapper.selectByResponseNo(responseNo);
        if (response == null) {
            // 业务异常（响应不存在）不应该导致事务回滚
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.RESPONSE_NOT_EXISTS);
        }
        String fromLevel = response.getResponseLevel();
        response.setResponseLevel(reqVO.getNewResponseLevel());
        if (reqVO.getNewPlanId() != null) {
            response.setPlanId(reqVO.getNewPlanId());
        }
        responseMapper.updateById(response);

        EmergencyResponseHistoryDO history = EmergencyResponseHistoryDO.builder()
                .responseId(response.getId())
                .type("upgrade")
                .fromLevel(fromLevel)
                .toLevel(reqVO.getNewResponseLevel())
                .reason(reqVO.getReason())
                .build();
        responseHistoryMapper.insert(history);

        // 将当前响应中处理中和未处理的任务标记为删除
        markIncompleteTasksAsDeleted(response.getId());

        // 终止旧任务并生成新任务
        terminateTasks(response.getId(), "upgrade");
        
        // 确定使用的预案ID：如果提供了新预案ID，使用新预案ID；否则使用当前响应的预案ID
        Long planIdToUse = reqVO.getNewPlanId() != null ? reqVO.getNewPlanId() : response.getPlanId();
        
        // 无论是否更换预案，只要调整了响应级别，都应该根据新的响应级别重新生成任务
        if (planIdToUse != null) {
            // 如果预案有步骤，自动生成新任务树
            // 如果预案没有步骤，允许手动创建任务
            boolean hasSteps = clonePlanStepsToTasks(
                response.getEventId(),
                response.getId(),
                planIdToUse,
                reqVO.getNewResponseLevel()
            );
            
            // 如果没有步骤，记录日志提示，允许手动创建任务
            if (!hasSteps) {
                // 预案没有步骤，允许手动创建任务
                // 注意：这是正常情况，某些预案可能没有预定义步骤，需要根据实际情况手动创建任务
                // 可以通过任务管理接口手动创建任务
            }
        }
        
        // 发送响应升级通知
        EmergencyEventDO event = eventMapper.selectById(response.getEventId());
        if (event != null) {
            notificationService.sendResponseUpgradedNotification(event, response.getId(), fromLevel, reqVO.getNewResponseLevel());
        }

        processTimelineWriter.appendEventAction(
                response.getEventId(),
                "response.upgrade",
                "升级响应 " + fromLevel + " → " + reqVO.getNewResponseLevel()
                        + (reqVO.getReason() != null && !reqVO.getReason().isBlank()
                        ? ": " + reqVO.getReason() : ""));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public void cancel(String responseNo, ResponseCancelReqVO reqVO) {
        EmergencyResponseDO response = responseMapper.selectByResponseNo(responseNo);
        if (response == null) {
            // 业务异常（响应不存在）不应该导致事务回滚
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.RESPONSE_NOT_EXISTS);
        }
        response.setStatus("cancelled");
        // 将 reqVO 转换为 Map 存储到 cancelInfo
        java.util.Map<String, Object> cancelInfo = new java.util.HashMap<>();
        cancelInfo.put("reason", reqVO.getReason());
        response.setCancelInfo(cancelInfo);
        // 使用 updateById 更新，MyBatis Plus 会自动使用实体类上定义的 TypeHandler
        responseMapper.updateById(response);

        EmergencyResponseHistoryDO history = EmergencyResponseHistoryDO.builder()
                .responseId(response.getId())
                .type("cancel")
                .fromLevel(response.getResponseLevel())
                .toLevel(response.getResponseLevel())
                .reason(reqVO.getReason())
                .build();
        responseHistoryMapper.insert(history);

        // 终止任务
        terminateTasks(response.getId(), "cancel");
        recoverDispatch(response.getId());
        
        // 发送响应取消通知
        EmergencyEventDO event = eventMapper.selectById(response.getEventId());
        if (event != null) {
            notificationService.sendResponseCancelledNotification(event, response.getId());
        }

        processTimelineWriter.appendEventAction(
                response.getEventId(),
                "response.cancel",
                "取消响应"
                        + (reqVO.getReason() != null && !reqVO.getReason().isBlank()
                        ? ": " + reqVO.getReason() : ""));
    }

    @Override
    public PageResult<TimelineItemRespVO> getTimeline(String responseNo, TimelinePageReqVO reqVO) {
        EmergencyResponseDO response = responseMapper.selectById(Long.parseLong(responseNo));
        if (response == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.RESPONSE_NOT_EXISTS);
        }
        int limit = reqVO.getPageSize();
        int offset = (reqVO.getPageNo() - 1) * reqVO.getPageSize();
        // 确保排序参数有效，默认为desc
        String orderBy = reqVO.getOrderBy();
        if (orderBy == null || (!orderBy.equalsIgnoreCase("asc") && !orderBy.equalsIgnoreCase("desc"))) {
            orderBy = "desc";
        }
        List<TimelineItemRespVO> list = timelineMapper.selectTimelinePage(response.getId(), response.getEventId(), limit, offset, orderBy)
                .stream()
                .map(raw -> {
                    TimelineItemRespVO vo = new TimelineItemRespVO();
                    vo.setType(raw.getType());
                    // 数据库时间已统一为 UTC，将 LocalDateTime 转换为 UTC OffsetDateTime
                    vo.setTimestamp(raw.getCreateTime().atOffset(java.time.ZoneOffset.UTC));
                    // 解析JSON字符串为Map
                    Map<String, Object> dataMap = Collections.emptyMap();
                    if (raw.getData() != null && !raw.getData().trim().isEmpty()) {
                        try {
                            dataMap = JsonUtils.parseObject(raw.getData(), new TypeReference<Map<String, Object>>() {});
                            if (dataMap == null) {
                                dataMap = Collections.emptyMap();
                            }
                            
                            // 如果是任务完成记录（task_status_change），检查 reason 字段是否是 JSON 格式
                            if (raw.getType() != null && raw.getType().contains("task_") && dataMap.containsKey("reason")) {
                                Object reasonObj = dataMap.get("reason");
                                if (reasonObj instanceof String) {
                                    String reasonStr = (String) reasonObj;
                                    // 检查是否是 JSON 格式
                                    if (reasonStr != null && reasonStr.trim().startsWith("{")) {
                                        try {
                                            Map<String, Object> reasonJson = JsonUtils.parseObject(reasonStr, new TypeReference<Map<String, Object>>() {});
                                            if (reasonJson != null) {
                                                // 提取 comment、attachments、timelyReport
                                                if (reasonJson.containsKey("comment")) {
                                                    dataMap.put("comment", reasonJson.get("comment"));
                                                }
                                                if (reasonJson.containsKey("attachments")) {
                                                    dataMap.put("attachments", reasonJson.get("attachments"));
                                                }
                                                if (reasonJson.containsKey("timelyReport")) {
                                                    dataMap.put("timelyReport", reasonJson.get("timelyReport"));
                                                }
                                            }
                                        } catch (Exception e) {
                                            // reason 不是有效的 JSON，保持原样
                                            log.debug("reason 字段不是有效的 JSON: {}", reasonStr);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log.warn("解析时间轴数据JSON失败: {}", raw.getData(), e);
                            dataMap = Collections.emptyMap();
                        }
                    }
                    vo.setData(dataMap);
                    return vo;
                })
                .toList();
        long total = timelineMapper.countTimeline(response.getId(), response.getEventId());
        return new PageResult<>(list, total);
    }

    @Override
    public ResponseRespVO getResponse(Long id) {
        EmergencyResponseDO response = responseMapper.selectById(id);
        if (response == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.RESPONSE_NOT_EXISTS);
        }
        return EmergencyResponseConvert.INSTANCE.convert(response);
    }

    @Override
    public EmergencyResponseDO getResponseByNo(String responseNo) {
        return responseMapper.selectByResponseNo(responseNo);
    }

    @Override
    public ResponseRespVO getResponseByEventId(Long eventId) {
        EmergencyResponseDO response = responseMapper.selectByEventId(eventId);
        if (response == null) {
            return null; // 如果事件没有响应，返回null
        }
        return EmergencyResponseConvert.INSTANCE.convert(response);
    }

    @Override
    public PageResult<ResponseRespVO> getResponsePage(PageParam pageReqVO) {
        PageResult<EmergencyResponseDO> pageResult = responseMapper.selectPage(pageReqVO, new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
        return EmergencyResponseConvert.INSTANCE.convertPage(pageResult);
    }

    private TimelineItemRespVO toTimeline(String type, LocalDateTime time, Map<String, Object> data) {
        TimelineItemRespVO vo = new TimelineItemRespVO();
        vo.setType(type);
        // 数据库时间已统一为 UTC，将 LocalDateTime 转换为 UTC OffsetDateTime
        vo.setTimestamp(time.atOffset(java.time.ZoneOffset.UTC));
        vo.setData(data == null ? Collections.emptyMap() : data);
        return vo;
    }

    /**
     * 克隆预案步骤到任务
     * 如果预案没有步骤，返回false，允许手动创建任务
     * 如果预案有步骤，自动生成任务树，返回true
     *
     * @param eventId 事件ID
     * @param responseId 响应ID
     * @param planId 预案ID
     * @param responseLevel 响应级别（可选，用于筛选对应级别的步骤）
     * @return true表示已生成任务，false表示预案没有步骤
     */
    private boolean clonePlanStepsToTasks(Long eventId, Long responseId, Long planId, String responseLevel) {
        // 查询预案步骤（如果指定了响应级别，先尝试匹配该级别的步骤）
        var steps = planStepMapper.selectListByPlanIdAndLevel(planId, responseLevel);
        
        // 如果指定了响应级别但查询不到步骤，则查询该预案的所有步骤（包括plan_level为null的步骤）
        // 这样可以处理步骤没有指定级别的情况
        if ((steps == null || steps.isEmpty()) && responseLevel != null) {
            log.info("按响应级别 {} 未找到步骤，尝试查询预案 {} 的所有步骤", responseLevel, planId);
            steps = planStepMapper.selectListByPlanId(planId);
        }
        
        if (steps == null || steps.isEmpty()) {
            // 预案没有步骤，返回false，允许手动创建任务
            log.info("预案 {} 没有步骤，允许手动创建任务", planId);
            return false;
        }
        
        // 建立预案步骤ID到任务ID的映射关系，用于后续设置parentId
        java.util.Map<Long, Long> planStepIdToTaskIdMap = new java.util.HashMap<>();
        
        // 第一遍：创建所有任务，建立映射关系
        for (var step : steps) {
            // 计算 startTime（根据规范，使用start_time字段）
            java.time.LocalDateTime startTime = null;
            if (step.getScheduledStartTime() != null) {
                startTime = LocalDateTime.now().plusMinutes(step.getScheduledStartTime());
            }
            
            // 从预案步骤传递执行时限（timeLimit）
            Integer timeLimit = step.getTimeLimit();
            
            // 计算 dueTime（根据预案步骤的执行时限）
            // 如果预案步骤设置了timeLimit，则根据startTime + timeLimit计算dueTime
            // 如果startTime为空，则根据当前时间 + timeLimit计算dueTime
            java.time.LocalDateTime dueTime = null;
            if (timeLimit != null && timeLimit > 0) {
                java.time.LocalDateTime baseTime = startTime != null ? startTime : LocalDateTime.now();
                dueTime = baseTime.plusMinutes(timeLimit);
            }
            
            // 生成任务编号（格式：TASK-YYYYMMDD-XXXXXX）
            String taskCode = "TASK-" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + 
                             String.format("%06d", System.currentTimeMillis() % 1000000);
            
            // 根据阶段设置responseId：预警阶段任务responseId为null，响应阶段任务responseId不为null
            // step.getStepStage()可能的值：
            //   字符串："WARNING"（预警阶段）、"RESPONSE"（响应阶段）、"响应阶段"、"预警阶段"
            //   数字："1"（预警阶段）、"2"（响应阶段）
            Long taskResponseId = null;
            String stepStage = step.getStepStage();
            if (stepStage != null) {
                // 判断是否为响应阶段：支持多种格式
                boolean isResponseStage = stepStage.equals("RESPONSE") 
                    || stepStage.equals("响应阶段")
                    || stepStage.equals("2"); // 数字格式：2表示响应阶段
                if (isResponseStage) {
                    // 响应阶段的任务，设置responseId
                    taskResponseId = responseId;
                }
            }
            // 预警阶段的任务，responseId保持为null
            
            EmergencyTaskDO task = EmergencyTaskDO.builder()
                    .eventId(eventId)
                    .responseId(taskResponseId) // 预警阶段为null，响应阶段为responseId
                    .planStepId(step.getId())
                    .taskCode(taskCode)
                    .title(step.getStepTitle() != null ? step.getStepTitle() : "未命名任务")
                    .content(step.getStepDescription())
                    .taskType(step.getStepStage())
                    .stage(step.getStepStage()) // 使用预案步骤的stage字段
                    .status("pending")
                    .priority("NORMAL")
                    .isKey(false)
                    .assignee(null) // 可以从responsiblePostId或responsibleUserId获取
                    .startTime(startTime)
                    .dueTime(dueTime) // 从预案步骤的timeLimit计算
                    .timeLimit(timeLimit) // 从预案步骤传递执行时限（分钟）
                    .parentId(null) // 先设置为null，后续统一设置
                    .build();
            // 设置创建人（继承自TenantBaseDO）
            task.setCreator("系统");
            taskMapper.insert(task);
            
            // 建立映射关系：预案步骤ID -> 任务ID
            planStepIdToTaskIdMap.put(step.getId(), task.getId());
            
            taskHistoryMapper.insert(EmergencyTaskHistoryDO.builder()
                    .responseId(responseId)
                    .taskId(task.getId())
                    .type("create")
                    .toStatus("pending")
                    .build());
        }
        
        // 第二遍：根据预案步骤的parentId设置任务的parentId
        for (var step : steps) {
            Long taskId = planStepIdToTaskIdMap.get(step.getId());
            if (taskId == null) {
                continue; // 跳过未创建任务的情况（理论上不应该发生）
            }
            
            // 如果预案步骤有parentId，查找对应的任务ID并设置
            Long stepParentId = step.getParentId();
            if (stepParentId != null && stepParentId > 0) {
                Long parentTaskId = planStepIdToTaskIdMap.get(stepParentId);
                if (parentTaskId != null) {
                    // 更新任务的parentId
                    EmergencyTaskDO task = taskMapper.selectById(taskId);
                    if (task != null) {
                        task.setParentId(parentTaskId);
                        taskMapper.updateById(task);
                        log.debug("设置任务 parentId: taskId={}, parentTaskId={}, stepId={}, stepParentId={}", 
                                taskId, parentTaskId, step.getId(), stepParentId);
                    }
                } else {
                    log.warn("预案步骤 {} 的父步骤 {} 对应的任务不存在，无法设置parentId", step.getId(), stepParentId);
                }
            }
            
            // 如果预案步骤关联了指令，为每个指令直接创建一个子任务
            if (step.getCommandIdList() != null && !step.getCommandIdList().isEmpty()) {
                Long taskResponseId = null;
                String stepStage = step.getStepStage();
                if (stepStage != null) {
                    // 判断是否为响应阶段：支持多种格式
                    boolean isResponseStage = stepStage.equals("RESPONSE") 
                        || stepStage.equals("响应阶段")
                        || stepStage.equals("2"); // 数字格式：2表示响应阶段
                    if (isResponseStage) {
                        taskResponseId = responseId;
                    }
                }
                // 查询父任务，获取父任务的stage
                EmergencyTaskDO parentTask = taskMapper.selectById(taskId);
                String parentStage = parentTask != null ? parentTask.getStage() : null;
                
                // 为每个指令创建一个子任务
                for (Long commandId : step.getCommandIdList()) {
                    // 查询指令模板，获取指令名称
                    EmergencyCommandTemplateDO commandTemplate = commandTemplateMapper.selectById(commandId);
                    String commandName = commandTemplate != null && commandTemplate.getName() != null 
                            ? commandTemplate.getName() : "指令任务";
                    
                    // 生成子任务编号
                    String subTaskCode = "TASK-" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + 
                                       String.format("%06d", System.currentTimeMillis() % 1000000);
                    
                    EmergencyTaskDO subTask = EmergencyTaskDO.builder()
                            .eventId(eventId)
                            .responseId(taskResponseId)
                            .planStepId(step.getId()) // 步骤的ID
                            .parentId(taskId) // 设置父任务ID（步骤对应的任务ID）
                            .commandId(commandId) // 保存指令ID
                            .taskCode(subTaskCode)
                            .title(commandName) // 使用指令模板名称
                            .content(null)
                            .taskType("COMMAND_STEP") // 标记为指令任务
                            .stage(parentStage) // 子任务的stage继承自父任务
                            .status("pending")
                            .priority("NORMAL")
                            .isKey(false)
                            .assignee(null)
                            .startTime(null)
                            .dueTime(null)
                            .timeLimit(null)
                            .build();
                    subTask.setCreator("系统");
                    taskMapper.insert(subTask);
                    
                    // 记录子任务创建历史
                    taskHistoryMapper.insert(EmergencyTaskHistoryDO.builder()
                            .responseId(responseId)
                            .taskId(subTask.getId())
                            .type("create")
                            .toStatus("pending")
                            .build());
                }
            }
        }
        
        return true;
    }
    
    /**
     * 克隆预案步骤到任务（兼容旧方法，不指定响应级别）
     */
    private void clonePlanStepsToTasks(Long eventId, Long responseId, Long planId) {
        clonePlanStepsToTasks(eventId, responseId, planId, null);
    }

    /**
     * 将当前响应中处理中和未处理的任务标记为删除
     * 这样前端页面就只显示已完成的任务和响应等级调整后新生成的任务
     *
     * @param responseId 响应ID
     */
    private void markIncompleteTasksAsDeleted(Long responseId) {
        // 查询属于该响应的所有任务
        var tasks = taskMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyTaskDO>()
                .eq(EmergencyTaskDO::getResponseId, responseId)
        );
        
        // 筛选出处理中和未处理的任务
        // 处理中：processing, in_progress
        // 未处理：pending, UNASSIGNED
        for (var task : tasks) {
            String status = task.getStatus();
            if (status != null && (
                status.equals("processing") || 
                status.equals("in_progress") || 
                status.equals("pending") || 
                status.equals("UNASSIGNED")
            )) {
                // 将任务标记为删除（软删除）
                task.setDeleted(true);
                taskMapper.updateById(task);
                log.info("标记任务为删除状态: taskId={}, status={}, responseId={}", 
                        task.getId(), status, responseId);
            }
        }
    }

    private void terminateTasks(Long responseId, String reason) {
        var tasks = taskMapper.selectList("response_id", responseId);
        for (var task : tasks) {
            taskService.terminateTask(task.getId(), reason);
        }
    }

    private void recoverDispatch(Long responseId) {
        // 使用专门的 selectByResponseId 方法，更语义化
        var dispatches = resourceDispatchMapper.selectByResponseId(responseId);
        for (var d : dispatches) {
            dispatchService.recover(d.getId(), "response_cancel_or_upgrade");
        }
    }

    /**
     * 记录事件状态变更历史
     *
     * @param eventId 事件ID
     * @param fromStatus 原状态
     * @param toStatus 目标状态
     * @param reason 变更原因
     */
    private void recordEventStatusHistory(Long eventId, String fromStatus, String toStatus, String reason) {
        EmergencyEventStatusHistoryDO history = EmergencyEventStatusHistoryDO.builder()
                .eventId(eventId)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .reason(reason)
                .operatorId(0L) // 系统操作
                .operatorName("系统")
                .operateTime(java.time.LocalDateTime.now())
                .build();
        statusHistoryMapper.insert(history);
    }
}
