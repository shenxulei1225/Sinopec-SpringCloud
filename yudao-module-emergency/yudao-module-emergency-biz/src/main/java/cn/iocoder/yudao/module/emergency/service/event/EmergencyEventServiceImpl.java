package cn.iocoder.yudao.module.emergency.service.event;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.pojo.PageParam;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.emergency.controller.admin.event.vo.*;
import cn.iocoder.yudao.module.emergency.convert.event.EmergencyEventConvert;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventAssessDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventReportInternalDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventReportExternalDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventStatusHistoryDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventAssessMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventReportInternalMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventReportExternalMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventStatusHistoryMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanStepMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.task.EmergencyTaskHistoryMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.task.EmergencyTaskMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.timeline.EmergencyTimelineMapper;
import cn.iocoder.yudao.module.emergency.enums.EventStatus;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanStepDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskHistoryDO;
import cn.iocoder.yudao.module.emergency.service.audit.EmergencyAuditLogService;
import cn.iocoder.yudao.module.emergency.service.report.InformationReportFlowService;
import cn.iocoder.yudao.module.emergency.service.event.EventCategoryValidationService;
import cn.iocoder.yudao.module.emergency.service.event.EventDeduplicationService;
import cn.iocoder.yudao.module.emergency.service.event.EventNotificationService;
import cn.iocoder.yudao.module.emergency.service.plan.EmergencyPlanService;
import cn.iocoder.yudao.module.emergency.service.response.EmergencyResponseService;
import cn.iocoder.yudao.module.emergency.service.timeline.TimelineCacheService;
import cn.iocoder.yudao.module.emergency.service.timeline.EmergencyProcessTimelineWriter;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanRespVO;
import cn.iocoder.yudao.module.emergency.controller.admin.response.vo.ResponseStartReqVO;
import cn.cheers.x.system.api.user.AdminUserApi;
import cn.cheers.x.framework.common.util.json.JsonUtils;
import cn.cheers.x.module.platform.runtime.api.ProcessTimelineApi;
import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionRespDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import cn.cheers.x.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class EmergencyEventServiceImpl implements EmergencyEventService {

    private final EmergencyEventMapper eventMapper;
    private final EmergencyEventReportInternalMapper reportInternalMapper;
    private final EmergencyEventReportExternalMapper reportExternalMapper;
    private final EmergencyEventAssessMapper assessMapper;
    private final EmergencyEventStatusHistoryMapper statusHistoryMapper;
    private final EmergencyTimelineMapper timelineMapper;
    private final AdminUserApi adminUserApi;
    private final EventStateMachine stateMachine;
    private final InformationReportFlowService reportFlowService;
    private final EmergencyAuditLogService auditLogService;
    private final EventCategoryValidationService categoryValidationService;
    private final EventDeduplicationService deduplicationService;
    private final EventNotificationService notificationService;
    private final EmergencyPlanService planService;
    private final EmergencyResponseService responseService;
    private final EmergencyPlanStepMapper planStepMapper;
    private final EmergencyTaskMapper taskMapper;
    private final EmergencyTaskHistoryMapper taskHistoryMapper;
    private final TimelineCacheService timelineCacheService;
    private final EmergencyProcessTimelineWriter processTimelineWriter;
    private final ProcessTimelineApi processTimelineApi;

    public EmergencyEventServiceImpl(EmergencyEventMapper eventMapper,
                                     EmergencyEventReportInternalMapper reportInternalMapper,
                                     EmergencyEventReportExternalMapper reportExternalMapper,
                                     EmergencyEventAssessMapper assessMapper,
                                     EmergencyEventStatusHistoryMapper statusHistoryMapper,
                                     EmergencyTimelineMapper timelineMapper,
                                     AdminUserApi adminUserApi,
                                     EventStateMachine stateMachine,
                                     InformationReportFlowService reportFlowService,
                                     EmergencyAuditLogService auditLogService,
                                     EventCategoryValidationService categoryValidationService,
                                     EventDeduplicationService deduplicationService,
                                     EventNotificationService notificationService,
                                     EmergencyPlanService planService,
                                     EmergencyResponseService responseService,
                                     EmergencyPlanStepMapper planStepMapper,
                                     EmergencyTaskMapper taskMapper,
                                     EmergencyTaskHistoryMapper taskHistoryMapper,
                                     TimelineCacheService timelineCacheService,
                                     EmergencyProcessTimelineWriter processTimelineWriter,
                                     ProcessTimelineApi processTimelineApi) {
        this.eventMapper = eventMapper;
        this.reportInternalMapper = reportInternalMapper;
        this.reportExternalMapper = reportExternalMapper;
        this.assessMapper = assessMapper;
        this.statusHistoryMapper = statusHistoryMapper;
        this.timelineMapper = timelineMapper;
        this.adminUserApi = adminUserApi;
        this.stateMachine = stateMachine;
        this.reportFlowService = reportFlowService;
        this.auditLogService = auditLogService;
        this.categoryValidationService = categoryValidationService;
        this.deduplicationService = deduplicationService;
        this.notificationService = notificationService;
        this.planService = planService;
        this.responseService = responseService;
        this.planStepMapper = planStepMapper;
        this.taskMapper = taskMapper;
        this.taskHistoryMapper = taskHistoryMapper;
        this.timelineCacheService = timelineCacheService;
        this.processTimelineWriter = processTimelineWriter;
        this.processTimelineApi = processTimelineApi;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventRespVO create(EventCreateReqVO reqVO) {
        // 兜底时间：发现时间必填，若未传发生时间则默认与发现时间一致，避免空值导致查询无返回
        if (reqVO.getDiscoveredAt() == null) {
            reqVO.setDiscoveredAt(java.time.LocalDateTime.now());
        }
        if (reqVO.getOccurredAt() == null) {
            reqVO.setOccurredAt(reqVO.getDiscoveredAt());
        }

        EmergencyEventDO event = EmergencyEventConvert.INSTANCE.convert(reqVO);
        // 手动设置impactSummary，因为MapStruct映射有歧义
        if (reqVO.getImpactSummary() != null) {
            event.setImpactSummary(reqVO.getImpactSummary());
        }
        // 创建时eventType（分类ID）为可选，如果提供了则验证有效性
        if (event.getEventType() != null) {
            categoryValidationService.validateCategoryIdForCreate(event.getEventType());
        }
        
        // 事件去重检测：如果提供了eventType、locationGis和discoveredAt，则进行去重检测
        if (event.getEventType() != null && event.getLocationGis() != null && event.getDiscoveredAt() != null) {
            List<EmergencyEventDO> duplicates = deduplicationService.checkDuplicates(event);
            if (!duplicates.isEmpty()) {
                // 如果检测到重复事件，抛出异常，提示用户
                StringBuilder duplicateInfo = new StringBuilder("检测到重复事件：");
                for (EmergencyEventDO duplicate : duplicates) {
                    duplicateInfo.append(duplicate.getEventCode()).append(" ");
                }
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_DUPLICATE_DETECTED,
                        duplicateInfo.toString());
            }
        }
        
        eventMapper.insert(event);
        
        // FR-024：记录审计日志
        try {
            auditLogService.logSuccess("CREATE", "event", 
                    "创建应急事件：" + event.getEventName(), 
                    event.getId(), null, null);
        } catch (Exception e) {
            // 审计日志记录失败不应该影响主业务流程
            log.warn("记录事件创建审计日志失败：eventId={}", event.getId(), e);
        }
        
        // 发送事件创建通知
        notificationService.sendEventCreatedNotification(event);
        
        // 首次上报写入历史（如有描述或附件）
        Map<String, Object> normalizedAttachments = EmergencyEventConvert.INSTANCE.normalizeAttachmentsForEvent(reqVO.getAttachments());
        if ((reqVO.getDescription() != null && !reqVO.getDescription().isEmpty())
                || (normalizedAttachments != null && !normalizedAttachments.isEmpty())) {
            EventReportReqVO firstReport = new EventReportReqVO();
            firstReport.setContent(reqVO.getDescription());
            // 将 normalizedAttachments 中的 photos 列表直接传给 firstReport（支持字符串或对象数组）
            if (normalizedAttachments != null && normalizedAttachments.containsKey("photos")) {
                firstReport.setAttachments(normalizedAttachments.get("photos"));
            }
            addReport(event.getId(), firstReport);
        }
        
        // 记录审计日志
        auditLogService.logSuccess("create", "event", 
                "创建应急事件：" + event.getEventCode(), event.getId(), null, null);
        
        return EmergencyEventConvert.INSTANCE.convert(event);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public void addReport(Long eventId, EventReportReqVO reqVO) {
        EmergencyEventDO event = eventMapper.selectById(eventId);
        if (event == null) {
            // 业务异常（事件不存在）不应该导致事务回滚
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_NOT_EXISTS);
        }
        // 使用新的内部上报DO和表结构
        EmergencyEventReportInternalDO report = EmergencyEventConvert.INSTANCE.convertInternalReport(eventId, reqVO);
        // 从当前用户上下文获取reporterId和reporterName
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (currentUserId != null) {
            report.setReporterId(currentUserId);
            // 从用户服务获取用户名
            AdminUserRespDTO user = adminUserApi.getUser(currentUserId).getData();
            if (user != null && user.getNickname() != null) {
                report.setReporterName(user.getNickname());
            } else {
                // 如果无法获取用户信息，使用用户ID作为默认值
                report.setReporterName("用户" + currentUserId);
            }
        } else {
            // 测试环境或系统调用场景：设置默认值，避免违反数据库NOT NULL约束
            report.setReporterId(0L);
            report.setReporterName("系统");
        }
        report.setReportTime(LocalDateTime.now());
        reportInternalMapper.insert(report);
        
        // 记录接报时间到报送流程服务
        reportFlowService.recordReceiveTime(eventId, report.getReportTime());
        
        // 校验报送时限规则
        if (!reportFlowService.validateTimeLimit(event, "初报", report.getReportTime())) {
            // 如果超出时限，记录警告日志，但不阻止上报
            // 实际业务中可能需要更严格的处理
        }
        
        // 记录审计日志
        auditLogService.logSuccess("add_report", "event", 
                "追加事件上报：事件ID=" + eventId, eventId, null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public EventExternalReportRespVO addExternalReport(Long eventId, EventExternalReportReqVO reqVO) {
        EmergencyEventDO event = eventMapper.selectById(eventId);
        if (event == null) {
            // 业务异常（事件不存在）不应该导致事务回滚
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_NOT_EXISTS);
        }
        
        // 使用外部上报DO和表结构
        EmergencyEventReportExternalDO report = EmergencyEventConvert.INSTANCE.convertExternalReport(eventId, reqVO);
        // 设置上报人信息：reporterId 为 NOT NULL 字段，这里与内部上报保持一致，从当前登录用户获取
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (currentUserId != null) {
            report.setReporterId(currentUserId);
            // 如果前端没有传 reporterName，则从用户服务补全姓名
            if (report.getReporterName() == null || report.getReporterName().isEmpty()) {
                AdminUserRespDTO user = adminUserApi.getUser(currentUserId).getData();
                if (user != null && user.getNickname() != null) {
                    report.setReporterName(user.getNickname());
                } else {
                    report.setReporterName("用户" + currentUserId);
                }
            }
            // 默认由当前登录用户签发，避免 signer 相关字段违反 NOT NULL 约束
            report.setSignerId(currentUserId);
            if (report.getSignerName() == null || report.getSignerName().isEmpty()) {
                report.setSignerName(report.getReporterName());
            }
        } else {
            // 测试环境或系统调用场景：兜底，避免违反 NOT NULL 约束
            report.setReporterId(0L);
            if (report.getReporterName() == null || report.getReporterName().isEmpty()) {
                report.setReporterName("系统");
            }
            report.setSignerId(0L);
            if (report.getSignerName() == null || report.getSignerName().isEmpty()) {
                report.setSignerName("系统");
            }
        }
        // receive_org_names 为 NOT NULL JSONB，若未提供则设置为空列表，避免违反约束
        if (report.getReceiveOrgNames() == null) {
            report.setReceiveOrgNames(Collections.emptyList());
        }
        // 确保 report_org_name 和 reporter_name 不为空（NOT NULL 字段）
        if (report.getReportOrgName() == null || report.getReportOrgName().trim().isEmpty()) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_EXTERNAL_REPORT_ORG_NAME_REQUIRED);
        }
        if (report.getReporterName() == null || report.getReporterName().trim().isEmpty()) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_EXTERNAL_REPORTER_NAME_REQUIRED);
        }
        report.setReportTime(LocalDateTime.now());
        reportExternalMapper.insert(report);
        
        // 记录接报时间到报送流程服务（外部上报也记录接报时间）
        reportFlowService.recordReceiveTime(eventId, report.getReportTime());
        
        // 校验报送时限规则
        if (!reportFlowService.validateTimeLimit(event, "初报", report.getReportTime())) {
            // 如果超出时限，记录警告日志，但不阻止上报
            // 实际业务中可能需要更严格的处理
        }
        
        // 记录审计日志
        auditLogService.logSuccess("add_external_report", "event", 
                "追加外部事件上报：事件ID=" + eventId + ", 上报机构=" + reqVO.getReportOrgName(), 
                eventId, null, null);
        
        return EmergencyEventConvert.INSTANCE.convertExternalReport(report);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public void confirm(Long eventId, EventConfirmReqVO reqVO) {
        EmergencyEventDO event = eventMapper.selectById(eventId);
        if (event == null) {
            // 业务异常（事件不存在）不应该导致事务回滚
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_NOT_EXISTS);
        }

        String currentStatus = event.getStatus();
        String targetStatus;

        // 根据确认结果确定目标状态
        // result可能的值：real/false_alarm/ignore
        if ("real".equals(reqVO.getResult())) {
            // 真实事件：根据事件级别判断进入预警还是响应中
            // BR-004：事件分级为Ⅰ级（特别重大）或Ⅱ级（重大）时，必须自动启动相应级别的应急预案
            // 根据事件级别判断：
            // - Ⅰ级（特别重大）或Ⅱ级（重大）：直接进入响应中状态
            // - 其他级别：进入预警状态
            String eventLevel = event.getEventLevel();
            if (eventLevel != null && ("特别重大".equals(eventLevel) || "重大".equals(eventLevel) || "Ⅰ级".equals(eventLevel) || "Ⅱ级".equals(eventLevel))) {
                // 重大事件：直接进入响应中状态，后续会自动启动响应
                targetStatus = EventStatus.RESPONDING.getCode();
            } else {
                // 一般事件：进入预警阶段
                targetStatus = EventStatus.WARNING.getCode();
            }
        } else if ("false_alarm".equals(reqVO.getResult())) {
            targetStatus = EventStatus.CANCELLED.getCode();
        } else {
            // ignore：保持待响应状态
            targetStatus = currentStatus;
        }

        // 使用状态机更新状态
        if (!currentStatus.equals(targetStatus)) {
            updateStatus(event, targetStatus, reqVO.getComment());
            
            // BR-004和BR-012：如果事件进入响应中状态且是重大事件，自动启动响应
            // 根据事件级别自动确定响应级别和启动响应
            if (EventStatus.RESPONDING.getCode().equals(targetStatus) && "real".equals(reqVO.getResult())) {
                autoStartResponse(event);
            } else if (EventStatus.WARNING.getCode().equals(targetStatus) && "real".equals(reqVO.getResult())) {
                // 一般真实事件：进入预警阶段时，如果前端传入了预案ID，则根据预案的“预警阶段”步骤生成预警任务
                if (reqVO.getPlanId() != null) {
                    try {
                        cloneWarningStepsToTasks(event.getId(), reqVO.getPlanId());
                    } catch (Exception e) {
                        // 预警任务生成失败不影响事件确认主流程，只记录错误日志
                        log.error("事件确认后生成预警阶段任务失败：eventId={}, planId={}", event.getId(), reqVO.getPlanId(), e);
                    }
                }
            }
        }
        
        // 发送事件确认通知
        notificationService.sendEventConfirmedNotification(event, reqVO.getResult());

        String how = "确认结果=" + reqVO.getResult()
                + (reqVO.getPlanId() != null ? ", planId=" + reqVO.getPlanId() : "")
                + (reqVO.getComment() != null && !reqVO.getComment().isBlank() ? ", " + reqVO.getComment() : "");
        processTimelineWriter.appendEventAction(eventId, "event.confirm", how);
    }

    /**
     * 自动启动响应
     * BR-004：事件分级为Ⅰ级（特别重大）或Ⅱ级（重大）时，必须自动启动相应级别的应急预案
     * BR-012：系统必须根据事件分级自动确定应急响应级别
     *
     * @param event 事件
     */
    private void autoStartResponse(EmergencyEventDO event) {
        try {
            String eventLevel = event.getEventLevel();
            if (eventLevel == null) {
                log.warn("事件级别为空，无法自动启动响应：eventId={}", event.getId());
                return;
            }

            // 根据事件级别确定响应级别
            // 事件级别：特别重大/重大/较大/一般
            // 响应级别：I/II/III/IV/V
            String responseLevel = determineResponseLevel(eventLevel);
            if (responseLevel == null) {
                log.warn("无法确定响应级别，跳过自动启动响应：eventId={}, eventLevel={}", event.getId(), eventLevel);
                return;
            }

            // 查找推荐的预案：根据响应级别和事件类型查找合适的预案
            // 1. 根据响应级别推荐预案（planService.recommendPlans会根据plan_levels字段匹配）
            // 2. 如果事件有类型，可以进一步筛选（planType参数）
            Integer planType = event.getEventType() != null ? event.getEventType().intValue() : null;
            List<EmergencyPlanRespVO> recommendedPlans = planService.recommendPlans(responseLevel, planType);
            
            if (recommendedPlans == null || recommendedPlans.isEmpty()) {
                log.warn("未找到推荐的预案，无法自动启动响应：eventId={}, eventLevel={}, responseLevel={}, planType={}", 
                        event.getId(), eventLevel, responseLevel, planType);
                // 未找到预案时，记录日志但不影响事件确认流程
                // 用户需要手动启动响应并选择预案
                return;
            }
            
            // 选择第一个推荐的预案（如果有多个，可以选择优先级最高的）
            EmergencyPlanRespVO selectedPlan = recommendedPlans.get(0);
            Long planId = selectedPlan.getId();
            
            log.info("找到推荐的预案，自动启动响应：eventId={}, eventLevel={}, responseLevel={}, planId={}, planName={}", 
                    event.getId(), eventLevel, responseLevel, planId, selectedPlan.getPlanName());
            
            // 自动启动响应
            try {
                ResponseStartReqVO startReq = new ResponseStartReqVO();
                startReq.setEventId(event.getId());
                startReq.setResponseLevel(responseLevel);
                startReq.setPlanId(planId);
                startReq.setReason("事件确认后自动启动响应（事件级别：" + eventLevel + "）");
                
                responseService.start(startReq);
                log.info("自动启动响应成功：eventId={}, responseLevel={}, planId={}", 
                        event.getId(), responseLevel, planId);
            } catch (Exception e) {
                log.error("自动启动响应失败：eventId={}, responseLevel={}, planId={}", 
                        event.getId(), responseLevel, planId, e);
                // 自动启动失败不影响事件确认流程，只记录日志
                // 用户需要手动启动响应
            }
        } catch (Exception e) {
            log.error("自动启动响应失败：eventId={}", event.getId(), e);
            // 自动启动失败不影响事件确认流程，只记录日志
        }
    }

    /**
     * 根据事件级别确定响应级别
     *
     * @param eventLevel 事件级别（特别重大/重大/较大/一般 或 Ⅰ级/Ⅱ级/Ⅲ级/Ⅳ级）
     * @return 响应级别（I/II/III/IV/V）
     */
    private String determineResponseLevel(String eventLevel) {
        if (eventLevel == null) {
            return null;
        }
        
        // 支持两种格式：中文描述和罗马数字
        if ("特别重大".equals(eventLevel) || "Ⅰ级".equals(eventLevel) || "I级".equals(eventLevel)) {
            return "I";
        } else if ("重大".equals(eventLevel) || "Ⅱ级".equals(eventLevel) || "II级".equals(eventLevel)) {
            return "II";
        } else if ("较大".equals(eventLevel) || "Ⅲ级".equals(eventLevel) || "III级".equals(eventLevel)) {
            return "III";
        } else if ("一般".equals(eventLevel) || "Ⅳ级".equals(eventLevel) || "IV级".equals(eventLevel)) {
            return "IV";
        }
        
        return null;
    }

    /**
     * 将预案中的“预警阶段”步骤克隆为任务，用于事件确认为真实且进入预警阶段时
     *
     * 复用响应服务中的任务克隆规范：
     * - 预警阶段任务的 responseId 为 null
     * - stage 和 taskType 均使用预案步骤的 stepStage
     * - timeLimit、scheduledStartTime 等字段行为与响应阶段一致
     *
     * @param eventId 事件ID
     * @param planId  预案ID
     */
    private void cloneWarningStepsToTasks(Long eventId, Long planId) {
        // 只克隆“预警阶段”的步骤：stepStage=WARNING/预警阶段/1
        java.util.List<EmergencyPlanStepDO> steps = planStepMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyPlanStepDO>()
                        .eq(EmergencyPlanStepDO::getPlanId, planId)
                        .and(wrapper -> wrapper
                                .eq(EmergencyPlanStepDO::getStepStage, "WARNING")
                                .or()
                                .eq(EmergencyPlanStepDO::getStepStage, "预警阶段")
                                .or()
                                .eq(EmergencyPlanStepDO::getStepStage, "1")
                        )
                        .orderByAsc(EmergencyPlanStepDO::getStepOrder)
        );

        if (steps == null || steps.isEmpty()) {
            log.info("预案 {} 未配置预警阶段步骤，跳过预警任务自动生成：eventId={}", planId, eventId);
            return;
        }

        java.util.Map<Long, Long> planStepIdToTaskIdMap = new java.util.HashMap<>();

        // 第一遍：创建所有预警阶段任务
        for (EmergencyPlanStepDO step : steps) {
            java.time.LocalDateTime startTime = null;
            if (step.getScheduledStartTime() != null) {
                startTime = LocalDateTime.now().plusMinutes(step.getScheduledStartTime());
            }

            Integer timeLimit = step.getTimeLimit();
            java.time.LocalDateTime dueTime = null;
            if (timeLimit != null && timeLimit > 0) {
                java.time.LocalDateTime baseTime = startTime != null ? startTime : LocalDateTime.now();
                dueTime = baseTime.plusMinutes(timeLimit);
            }

            String taskCode = "TASK-" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" +
                    String.format("%06d", System.currentTimeMillis() % 1000000);

            EmergencyTaskDO task = EmergencyTaskDO.builder()
                    .eventId(eventId)
                    .responseId(null) // 预警阶段任务不关联响应
                    .planStepId(step.getId())
                    .taskCode(taskCode)
                    .title(step.getStepTitle() != null ? step.getStepTitle() : "未命名任务")
                    .content(step.getStepDescription())
                    .taskType(step.getStepStage())
                    .stage(step.getStepStage())
                    .status("pending")
                    .priority("NORMAL")
                    .isKey(false)
                    .assignee(null)
                    .startTime(startTime)
                    .dueTime(dueTime)
                    .timeLimit(timeLimit)
                    .parentId(null)
                    .build();
            task.setCreator("系统");
            taskMapper.insert(task);

            planStepIdToTaskIdMap.put(step.getId(), task.getId());

            // 说明：
            // - emergency_task_history.response_id 列现在为 NOT NULL，且业务约束要求
            //   “所有任务历史记录都必须关联响应 ID”（见 EmergencyTaskServiceImpl#getResponseIdOrThrow）。
            // - 预警阶段任务在事件确认后、响应创建之前生成，此时还不存在响应，无法为历史记录
            //   正确设置 responseId。
            // - 为避免违反非空约束，这里不再为预警阶段任务写入历史记录；后续在响应阶段
            //   对任务的实际操作（开始、完成、终止等）都会通过 TaskService 正常记录历史。
        }

        // 第二遍：根据预案步骤的 parentId 设置任务的 parentId
        for (EmergencyPlanStepDO step : steps) {
            Long taskId = planStepIdToTaskIdMap.get(step.getId());
            if (taskId == null) {
                continue;
            }
            Long stepParentId = step.getParentId();
            if (stepParentId != null && stepParentId > 0) {
                Long parentTaskId = planStepIdToTaskIdMap.get(stepParentId);
                if (parentTaskId != null) {
                    EmergencyTaskDO task = taskMapper.selectById(taskId);
                    if (task != null) {
                        task.setParentId(parentTaskId);
                        taskMapper.updateById(task);
                        log.debug("设置预警任务 parentId: taskId={}, parentTaskId={}, stepId={}, stepParentId={}",
                                taskId, parentTaskId, step.getId(), stepParentId);
                    }
                } else {
                    log.warn("预案步骤 {} 的父步骤 {} 对应的预警任务不存在，无法设置parentId", step.getId(), stepParentId);
                }
            }
        }

        log.info("根据预案 {} 的预警阶段步骤为事件 {} 生成预警任务 {} 个", planId, eventId, planStepIdToTaskIdMap.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public EventAssessRespVO assess(Long eventId, EventAssessReqVO reqVO) {
        EmergencyEventDO event = eventMapper.selectById(eventId);
        if (event == null) {
            // 业务异常（事件不存在）不应该导致事务回滚
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_NOT_EXISTS);
        }
        // 更新事件状态和级别（当前状态）
        event.setStatus("assessing");
        // 注意：根据数据模型，eventLevel是事件级别（特别重大/重大/较大/一般），
        // 而reqVO.getResponseLevel()是响应级别（I/II/III/IV/V），两者不同
        // 这里暂时使用响应级别，后续需要根据业务逻辑调整
        // event.setEventLevel(reqVO.getResponseLevel());
        
        // 研判信息存储在历史表中，事件表只存储当前状态
        eventMapper.updateById(event);

        // 保存研判记录
        EmergencyEventAssessDO assess = EmergencyEventConvert.INSTANCE.convertAssess(eventId, reqVO);
        assessMapper.insert(assess);

        EventAssessRespVO resp = new EventAssessRespVO();
        resp.setResponseLevel(reqVO.getResponseLevel());
        resp.setRecommendedPlanLevel(reqVO.getRecommendedPlanLevel());

        String how = "研判"
                + (reqVO.getResponseLevel() != null ? " 建议响应级别=" + reqVO.getResponseLevel() : "")
                + (reqVO.getComment() != null && !reqVO.getComment().isBlank() ? ": " + reqVO.getComment() : "");
        processTimelineWriter.appendEventAction(eventId, "event.assess", how.isBlank() ? "事件研判" : how);

        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void close(Long eventId, String reason) {
        EmergencyEventDO event = eventMapper.selectById(eventId);
        if (event == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_NOT_EXISTS);
        }
        // 使用状态机更新状态为已关闭，状态机会自动校验关键任务是否已完成
        updateStatus(event, EventStatus.CLOSED.getCode(), reason != null ? reason : "事件关闭");
    }

    @Override
    public EventRespVO getEvent(Long id) {
        EmergencyEventDO event = eventMapper.selectById(id);
        if (event == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_NOT_EXISTS);
        }
        
        // 调试日志：检查从数据库读取的 attachments 值（使用 INFO 级别以便查看）
        Object attachmentsObj = event.getAttachments();
        log.info("【调试】从数据库读取的 attachments: eventId={}, attachments={}, attachments类型={}, attachments是否为null={}", 
                id, attachmentsObj, 
                attachmentsObj != null ? attachmentsObj.getClass().getName() : "null",
                attachmentsObj == null);
        
        // 如果 attachments 为 null，尝试使用自定义查询方法重新获取
        Map<String, Object> attachments = null;
        if (attachmentsObj == null) {
            try {
                Long tenantId = cn.cheers.x.framework.tenant.core.context.TenantContextHolder.getTenantId();
                if (tenantId == null) {
                    tenantId = 1L; // 兜底：默认租户
                }
                String attachmentsText = eventMapper.selectAttachmentsTextById(id, tenantId);
                log.info("【调试】通过自定义查询方法获取 attachmentsText: {}", attachmentsText);
                if (attachmentsText != null && !attachmentsText.trim().isEmpty()) {
                    attachments = JsonUtils.parseObject(attachmentsText,
                            new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                }
            } catch (Exception e) {
                log.warn("【警告】通过自定义查询方法获取 attachments 失败: eventId={}", id, e);
            }
        } else {
            // 如果 attachments 是 Object 类型但实际是 Map，需要转换
            if (attachmentsObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) attachmentsObj;
                attachments = map;
            } else {
                // 如果不是 Map，尝试通过 JSON 转换
                try {
                    String jsonStr = JsonUtils.toJsonString(attachmentsObj);
                    attachments = JsonUtils.parseObject(jsonStr, 
                        new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                    log.info("【调试】通过 JSON 转换 attachments: {}", attachments);
                } catch (Exception e) {
                    log.error("【错误】转换 attachments 失败: eventId={}, attachmentsObj={}", id, attachmentsObj, e);
                }
            }
        }
        
        EventRespVO respVO = EmergencyEventConvert.INSTANCE.convert(event);
        
        // 手动设置 attachments 字段
        if (attachments != null) {
            respVO.setAttachments(attachments);
            log.info("【调试】设置 attachments 到 respVO: {}", attachments);
        } else {
            log.warn("【警告】attachments 为 null，无法设置到 respVO: eventId={}", id);
        }
        
        // 手动处理 impactSummary 字段
        Object impactSummaryObj = event.getImpactSummary();
        if (impactSummaryObj != null) {
            if (impactSummaryObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> impactSummaryMap = (Map<String, Object>) impactSummaryObj;
                respVO.setImpactSummary(impactSummaryMap);
            } else {
                try {
                    String jsonStr = JsonUtils.toJsonString(impactSummaryObj);
                    Map<String, Object> impactSummaryMap = JsonUtils.parseObject(jsonStr,
                        new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                    respVO.setImpactSummary(impactSummaryMap);
                } catch (Exception e) {
                    log.warn("转换 impactSummary 字段失败：eventId={}", id, e);
                }
            }
        }
        
        log.info("【调试】最终返回的 respVO.attachments: {}", respVO.getAttachments());
        return respVO;
    }

    @Override
    public PageResult<EventRespVO> getEventPage(PageParam pageReqVO) {
        // 按创建时间倒序，确保列表按最新创建的事件优先返回
        PageResult<EmergencyEventDO> pageResult = eventMapper.selectPage(
                pageReqVO,
                new LambdaQueryWrapper<EmergencyEventDO>().orderByDesc(EmergencyEventDO::getCreateTime)
        );
        return EmergencyEventConvert.INSTANCE.convertPage(pageResult);
    }

    /**
     * 更新事件状态（使用状态机校验）
     *
     * @param eventId 事件ID
     * @param targetStatus 目标状态
     * @param reason 操作原因
     */
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public void updateStatus(Long eventId, String targetStatus, String reason) {
        EmergencyEventDO event = eventMapper.selectById(eventId);
        if (event == null) {
            // 业务异常（事件不存在）不应该导致事务回滚
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_NOT_EXISTS);
        }
        updateStatus(event, targetStatus, reason);
    }

    /**
     * 更新事件状态（使用状态机校验）
     *
     * @param event 事件
     * @param targetStatus 目标状态
     * @param reason 操作原因
     */
    private void updateStatus(EmergencyEventDO event, String targetStatus, String reason) {
        String currentStatus = event.getStatus();

        // 校验状态转换是否合法
        stateMachine.validateTransition(currentStatus, targetStatus);

        // 校验业务规则
        stateMachine.validateBusinessRules(event, targetStatus);

        // 记录状态变更历史
        recordStatusHistory(event.getId(), currentStatus, targetStatus, reason);

        // 更新事件状态
        event.setStatus(targetStatus);
        eventMapper.updateById(event);
        
        // 发送事件状态变更通知
        notificationService.sendEventStatusChangedNotification(event, currentStatus, targetStatus);
    }

    /**
     * 记录状态变更历史
     *
     * @param eventId 事件ID
     * @param fromStatus 原状态
     * @param toStatus 新状态
     * @param reason 操作原因
     */
    private void recordStatusHistory(Long eventId, String fromStatus, String toStatus, String reason) {
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        String operatorName = null;
        if (currentUserId != null) {
            AdminUserRespDTO user = adminUserApi.getUser(currentUserId).getData();
            if (user != null && user.getNickname() != null) {
                operatorName = user.getNickname();
            }
        }

        EmergencyEventStatusHistoryDO history = EmergencyEventStatusHistoryDO.builder()
                .eventId(eventId)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .reason(reason)
                .operatorId(currentUserId != null ? currentUserId : 0L) // 测试环境或系统调用场景：设置默认值
                .operatorName(operatorName != null ? operatorName : "系统")
                .operateTime(java.time.LocalDateTime.now())
                .build();

        statusHistoryMapper.insert(history);
    }

    @Override
    public PageResult<cn.iocoder.yudao.module.emergency.controller.admin.response.vo.TimelineItemRespVO> getTimeline(
            Long eventId, 
            cn.iocoder.yudao.module.emergency.controller.admin.response.vo.TimelinePageReqVO pageReqVO) {
        // 验证事件存在
        EmergencyEventDO event = eventMapper.selectById(eventId);
        if (event == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_NOT_EXISTS);
        }

        // 优先读中台过程时间线；有数据则作为真源（不写回伪造中台行）
        try {
            PageResult<ProcessTimelineActionRespDTO> platformPage = processTimelineApi
                    .pageByTarget(
                            EmergencyProcessTimelineWriter.TARGET_TYPE_EMERGENCY_EVENT,
                            String.valueOf(eventId),
                            pageReqVO.getPageNo(),
                            pageReqVO.getPageSize())
                    .getCheckedData();
            if (platformPage != null
                    && !CollectionUtils.isEmpty(platformPage.getList())
                    && platformPage.getTotal() != null
                    && platformPage.getTotal() > 0) {
                List<cn.iocoder.yudao.module.emergency.controller.admin.response.vo.TimelineItemRespVO> mapped =
                        platformPage.getList().stream().map(this::mapPlatformTimelineItem).toList();
                log.debug("事件时间线使用中台过程时间线: eventId={}, total={}", eventId, platformPage.getTotal());
                return new PageResult<>(mapped, platformPage.getTotal());
            }
        } catch (Exception e) {
            log.warn("读取中台过程时间线失败，回退旧聚合: eventId={}", eventId, e);
        }
        
        // 检查缓存（仅第一页且无时间范围过滤时使用缓存）
        // 注意：TimelinePageReqVO可能没有startTime和endTime字段，暂时移除时间范围检查
        if (pageReqVO.getPageNo() == 1) {
            List<cn.iocoder.yudao.module.emergency.controller.admin.response.vo.TimelineItemRespVO> cached = 
                timelineCacheService.getTimelineFromCache(eventId);
            if (cached != null) {
                long total = timelineMapper.countTimeline(null, eventId);
                return new PageResult<>(cached, total);
            }
        }
        
        int limit = pageReqVO.getPageSize();
        int offset = (pageReqVO.getPageNo() - 1) * pageReqVO.getPageSize();
        
        // 确保排序参数有效，默认为desc
        String orderBy = pageReqVO.getOrderBy();
        if (orderBy == null || (!orderBy.equalsIgnoreCase("asc") && !orderBy.equalsIgnoreCase("desc"))) {
            orderBy = "desc";
        }
        
        // 通过聚合查询多个业务表生成时间线（responseId为null，仅基于eventId查询）
        List<cn.iocoder.yudao.module.emergency.controller.admin.response.vo.TimelineItemRespVO> list = 
            timelineMapper.selectTimelinePage(null, eventId, limit, offset, orderBy)
                .stream()
                .map(raw -> {
                    cn.iocoder.yudao.module.emergency.controller.admin.response.vo.TimelineItemRespVO vo = 
                        new cn.iocoder.yudao.module.emergency.controller.admin.response.vo.TimelineItemRespVO();
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
                        } catch (Exception e) {
                            log.warn("解析时间轴数据JSON失败: {}", raw.getData(), e);
                            dataMap = Collections.emptyMap();
                        }
                    }
                    Map<String, Object> withSource = new HashMap<>(dataMap);
                    withSource.put("source", "legacy_aggregate");
                    vo.setData(withSource);
                    return vo;
                })
                .toList();
        
        // 缓存结果（仅第一页）
        if (pageReqVO.getPageNo() == 1) {
            timelineCacheService.setTimelineCache(eventId, list);
        }
        
        long total = timelineMapper.countTimeline(null, eventId);
        return new PageResult<>(list, total);
    }

    private cn.iocoder.yudao.module.emergency.controller.admin.response.vo.TimelineItemRespVO mapPlatformTimelineItem(
            ProcessTimelineActionRespDTO action) {
        cn.iocoder.yudao.module.emergency.controller.admin.response.vo.TimelineItemRespVO vo =
                new cn.iocoder.yudao.module.emergency.controller.admin.response.vo.TimelineItemRespVO();
        vo.setType(action.getActionCode());
        OffsetDateTime ts = action.getOccurredAt();
        vo.setTimestamp(ts != null ? ts : OffsetDateTime.now());
        Map<String, Object> data = new HashMap<>();
        data.put("source", "platform");
        data.put("howSummary", action.getHowSummary());
        data.put("actorName", action.getActorName());
        data.put("actorId", action.getActorId());
        data.put("actionCode", action.getActionCode());
        if (action.getPayloadJson() != null && !action.getPayloadJson().isBlank()) {
            data.put("payloadJson", action.getPayloadJson());
        }
        vo.setData(data);
        return vo;
    }

    @Override
    public PageResult<EventStatusHistoryRespVO> getStatusHistory(Long eventId, PageParam pageReqVO) {
        // 验证事件存在
        EmergencyEventDO event = eventMapper.selectById(eventId);
        if (event == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_NOT_EXISTS);
        }

        // 查询状态变更历史
        PageResult<EmergencyEventStatusHistoryDO> pageResult = statusHistoryMapper.selectPage(
                pageReqVO,
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyEventStatusHistoryDO>()
                        .eq(EmergencyEventStatusHistoryDO::getEventId, eventId)
                        .orderByDesc(EmergencyEventStatusHistoryDO::getOperateTime)
        );

        // 转换为响应VO
        return EmergencyEventConvert.INSTANCE.convertStatusHistoryPage(pageResult);
    }

    @Override
    public List<EventAssessListRespVO> getAssessments(Long eventId) {
        // 验证事件存在
        EmergencyEventDO event = eventMapper.selectById(eventId);
        if (event == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_NOT_EXISTS);
        }

        // 查询研判记录
        List<EmergencyEventAssessDO> assessList = assessMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyEventAssessDO>()
                        .eq(EmergencyEventAssessDO::getEmergencyEventId, eventId)
                        .orderByDesc(EmergencyEventAssessDO::getAssessTime)
        );

        // 转换为响应VO
        List<EventAssessListRespVO> result = EmergencyEventConvert.INSTANCE.convertAssessList(assessList);

        // 补充创建人姓名
        if (result != null && !result.isEmpty()) {
            // 收集所有创建人ID
            List<Long> creatorIds = result.stream()
                    .map(EventAssessListRespVO::getCreator)
                    .filter(creator -> creator != null && !creator.isEmpty())
                    .map(Long::valueOf)
                    .distinct()
                    .toList();

            // 批量查询用户信息
            if (!creatorIds.isEmpty()) {
                Map<Long, String> userMap = new HashMap<>();
                try {
                    List<AdminUserRespDTO> users = adminUserApi.getUserList(creatorIds).getCheckedData();
                    if (users != null) {
                        users.forEach(user -> userMap.put(user.getId(), user.getNickname()));
                    }
                } catch (Exception e) {
                    log.warn("获取用户信息失败", e);
                }

                // 设置创建人姓名
                final Map<Long, String> finalUserMap = userMap;
                result.forEach(vo -> {
                    if (vo.getCreator() != null && !vo.getCreator().isEmpty()) {
                        try {
                            Long userId = Long.valueOf(vo.getCreator());
                            String userName = finalUserMap.get(userId);
                            if (userName != null) {
                                vo.setCreatorName(userName);
                            }
                        } catch (NumberFormatException e) {
                            // 忽略转换异常
                        }
                    }
                });
            }
        }

        return result;
    }
}
