package cn.iocoder.yudao.module.alarm.service.linkage;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.linkage.*;
import cn.iocoder.yudao.module.alarm.convert.LinkageRuleConvert;
import cn.iocoder.yudao.module.alarm.dal.dataobject.AlarmDO;
import cn.iocoder.yudao.module.alarm.dal.dataobject.LinkageExecutionDO;
import cn.iocoder.yudao.module.alarm.dal.dataobject.LinkageRuleDO;
import cn.iocoder.yudao.module.alarm.dal.mysql.AlarmMapper;
import cn.iocoder.yudao.module.alarm.dal.mysql.LinkageExecutionMapper;
import cn.iocoder.yudao.module.alarm.dal.mysql.LinkageRuleMapper;
import cn.iocoder.yudao.module.alarm.enums.LinkageActionTypeEnum;
import cn.iocoder.yudao.module.alarm.enums.LinkageExecutionModeEnum;
import cn.iocoder.yudao.module.alarm.enums.LinkageExecutionStatusEnum;
import cn.iocoder.yudao.module.alarm.framework.cache.AlarmQueryCacheService;
import cn.iocoder.yudao.module.alarm.service.audit.AlarmAuditService;
import cn.iocoder.yudao.module.alarm.service.linkage.executor.LinkageActionContext;
import cn.iocoder.yudao.module.alarm.service.linkage.executor.LinkageActionExecutor;
import cn.iocoder.yudao.module.alarm.service.linkage.executor.LinkageActionResult;
import cn.iocoder.yudao.module.alarm.service.websocket.AlarmWebSocketService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.alarm.enums.ErrorCodeConstants.*;

/**
 * 联动服务实现类
 * 
 * <p>负责联动规则的配置管理和联动动作的执行</p>
 * 
 * <p>核心功能：</p>
 * <ul>
 *   <li>联动规则 CRUD 操作</li>
 *   <li>联动动作自动执行</li>
 *   <li>联动执行重试机制（最多3次，间隔5秒）</li>
 *   <li>人工介入标记</li>
 * </ul>
 * 
 * <p>业务规则：</p>
 * <ul>
 *   <li>BR-BIZ-004：消防设施控制必须优先于其他联动动作执行</li>
 *   <li>BR-BIZ-005：联动动作执行失败时，系统应自动重试3次，每次间隔5秒</li>
 *   <li>BR-BIZ-006：联动重试3次后仍失败，系统必须发送通知给值班员，要求人工介入处理</li>
 *   <li>BR-BIZ-007：所有联动动作必须记录执行状态、执行时间、执行结果</li>
 * </ul>
 *
 * @author 告警管理模块
 */
@Slf4j
@Service
@Validated
public class LinkageServiceImpl implements LinkageService {

    /** 最大重试次数 */
    private static final int MAX_RETRY_COUNT = 3;

    /** 重试间隔（毫秒） */
    private static final long RETRY_INTERVAL_MS = 5000;

    /** 并行执行线程池 */
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Resource
    private LinkageRuleMapper linkageRuleMapper;

    @Resource
    private LinkageExecutionMapper linkageExecutionMapper;

    @Resource
    private AlarmMapper alarmMapper;

    @Resource
    private AlarmAuditService alarmAuditService;

    @Resource
    private AlarmWebSocketService alarmWebSocketService;

    @Resource
    private AlarmQueryCacheService alarmQueryCacheService;

    /** 联动动作执行器列表（通过 Spring 自动注入所有实现类） */
    @Resource
    private List<LinkageActionExecutor> executors;

    // ========== 联动规则 CRUD ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createLinkageRule(LinkageRuleCreateReqVO createReqVO) {
        // 1. 校验规则编码唯一性
        validateRuleCodeUnique(createReqVO.getRuleCode(), null);
        
        // 2. 校验动作配置有效性
        validateActionsConfig(createReqVO.getActions());
        
        // 3. 转换并保存
        LinkageRuleDO linkageRule = LinkageRuleConvert.INSTANCE.convert(createReqVO);
        // 设置默认值
        if (linkageRule.getEnabled() == null) {
            linkageRule.setEnabled(true);
        }
        if (linkageRule.getPriority() == null) {
            linkageRule.setPriority(0);
        }
        if (StrUtil.isBlank(linkageRule.getExecutionMode())) {
            linkageRule.setExecutionMode(LinkageExecutionModeEnum.SERIAL.getMode());
        }
        
        linkageRuleMapper.insert(linkageRule);
        
        // 清除联动规则缓存（性能优化）
        alarmQueryCacheService.clearLinkageRulesCache();
        
        log.info("[创建联动规则] 规则创建成功: id={}, ruleCode={}, ruleName={}", 
                linkageRule.getId(), linkageRule.getRuleCode(), linkageRule.getRuleName());
        
        return linkageRule.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLinkageRule(LinkageRuleUpdateReqVO updateReqVO) {
        // 1. 校验规则存在
        LinkageRuleDO existingRule = validateLinkageRuleExists(updateReqVO.getId());
        
        // 2. 校验动作配置有效性
        validateActionsConfig(updateReqVO.getActions());
        
        // 3. 更新规则
        LinkageRuleConvert.INSTANCE.update(updateReqVO, existingRule);
        linkageRuleMapper.updateById(existingRule);
        
        // 清除联动规则缓存（性能优化）
        alarmQueryCacheService.clearLinkageRulesCache();
        
        log.info("[更新联动规则] 规则更新成功: id={}, ruleName={}", 
                existingRule.getId(), existingRule.getRuleName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLinkageRule(Long id) {
        // 1. 校验规则存在
        validateLinkageRuleExists(id);
        
        // 2. 删除规则
        linkageRuleMapper.deleteById(id);
        
        // 清除联动规则缓存（性能优化）
        alarmQueryCacheService.clearLinkageRulesCache();
        
        log.info("[删除联动规则] 规则删除成功: id={}", id);
    }

    @Override
    public LinkageRuleDO getLinkageRule(Long id) {
        return linkageRuleMapper.selectById(id);
    }

    @Override
    public PageResult<LinkageRuleDO> getLinkageRulePage(LinkageRulePageReqVO pageReqVO) {
        return linkageRuleMapper.selectPage(pageReqVO);
    }

    @Override
    public List<LinkageRuleDO> getEnabledLinkageRules() {
        // 尝试从缓存获取（性能优化）
        List<LinkageRuleDO> cachedRules = alarmQueryCacheService.getEnabledLinkageRulesCache();
        if (cachedRules != null) {
            log.debug("[getEnabledLinkageRules][从缓存获取启用的联动规则] count={}", cachedRules.size());
            return cachedRules;
        }
        
        // 从数据库查询
        List<LinkageRuleDO> rules = linkageRuleMapper.selectEnabledRules();
        
        // 设置缓存
        alarmQueryCacheService.setEnabledLinkageRulesCache(rules);
        
        return rules;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleLinkageRuleStatus(Long id, Boolean enabled) {
        // 1. 校验规则存在
        LinkageRuleDO linkageRule = validateLinkageRuleExists(id);
        
        // 2. 更新状态
        linkageRule.setEnabled(enabled);
        linkageRuleMapper.updateById(linkageRule);
        
        // 清除联动规则缓存（性能优化）
        alarmQueryCacheService.clearLinkageRulesCache();
        
        log.info("[切换联动规则状态] id={}, enabled={}", id, enabled);
    }

    // ========== 联动规则匹配 ==========

    @Override
    public List<LinkageRuleDO> matchLinkageRules(AlarmDO alarm) {
        if (alarm == null) {
            return Collections.emptyList();
        }
        
        // 查询匹配的联动规则
        List<LinkageRuleDO> matchedRules = linkageRuleMapper.selectMatchingRules(
                alarm.getRuleId(),
                alarm.getAlarmTypeId(),
                alarm.getAlarmCategoryId(),
                alarm.getAlarmLevel()
        );
        
        log.debug("[匹配联动规则] alarmId={}, 匹配到{}条规则", alarm.getId(), matchedRules.size());
        
        return matchedRules;
    }

    // ========== 联动执行 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<LinkageExecutionDO> executeLinkage(AlarmDO alarm) {
        if (alarm == null) {
            log.warn("[执行联动] 告警信息为空，跳过联动执行");
            return Collections.emptyList();
        }
        
        // 1. 匹配联动规则
        List<LinkageRuleDO> matchedRules = matchLinkageRules(alarm);
        if (CollUtil.isEmpty(matchedRules)) {
            log.info("[执行联动] alarmId={}, 未匹配到联动规则", alarm.getId());
            return Collections.emptyList();
        }
        
        // 2. 执行所有匹配的联动规则
        List<LinkageExecutionDO> allExecutions = new ArrayList<>();
        for (LinkageRuleDO rule : matchedRules) {
            List<LinkageExecutionDO> executions = executeRuleActions(alarm, rule, false);
            allExecutions.addAll(executions);
        }
        
        log.info("[执行联动] alarmId={}, 执行了{}条联动规则，共{}个动作", 
                alarm.getId(), matchedRules.size(), allExecutions.size());
        
        return allExecutions;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<LinkageExecutionDO> executeLinkageRule(Long alarmId, Long linkageRuleId) {
        // 1. 校验告警存在
        AlarmDO alarm = alarmMapper.selectById(alarmId);
        if (alarm == null) {
            throw exception(ALARM_NOT_EXISTS);
        }
        
        // 2. 校验联动规则存在
        LinkageRuleDO linkageRule = validateLinkageRuleExists(linkageRuleId);
        
        // 3. 执行联动规则
        return executeRuleActions(alarm, linkageRule, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LinkageExecutionDO manualExecuteLinkage(Long alarmId, String actionType, String actionConfig) {
        // 1. 校验告警存在
        AlarmDO alarm = alarmMapper.selectById(alarmId);
        if (alarm == null) {
            throw exception(ALARM_NOT_EXISTS);
        }
        
        // 2. 校验动作类型
        LinkageActionTypeEnum actionTypeEnum = LinkageActionTypeEnum.getByType(actionType);
        if (actionTypeEnum == null) {
            throw exception(LINKAGE_RULE_ACTIONS_INVALID, "不支持的动作类型: " + actionType);
        }
        
        // 3. 获取执行器
        LinkageActionExecutor executor = getExecutor(actionType);
        if (executor == null) {
            throw exception(LINKAGE_RULE_ACTIONS_INVALID, "未找到动作执行器: " + actionType);
        }
        
        // 4. 构建执行上下文
        Map<String, Object> configMap = parseActionConfig(actionConfig);
        LinkageActionContext context = LinkageActionContext.builder()
                .alarm(alarm)
                .actionType(actionType)
                .actionConfig(actionConfig)
                .actionConfigMap(configMap)
                .targetDeviceId(getTargetDeviceId(configMap))
                .targetDeviceName(getTargetDeviceName(configMap))
                .dryRun(false)
                .retryCount(0)
                .build();
        
        // 5. 创建执行记录
        LinkageExecutionDO execution = createExecutionRecord(alarm.getId(), null, actionType, actionConfig, context);
        
        // 6. 执行动作
        executeActionWithRetry(execution, executor, context);
        
        return execution;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public LinkageExecutionDO retryLinkageExecution(Long executionId) {
        // 1. 校验执行记录存在
        LinkageExecutionDO execution = linkageExecutionMapper.selectById(executionId);
        if (execution == null) {
            throw exception(LINKAGE_EXECUTION_NOT_EXISTS);
        }
        
        // 2. 校验是否可以重试
        if (!LinkageExecutionStatusEnum.canRetry(execution.getExecutionStatus())) {
            throw exception(LINKAGE_EXECUTION_FAILED, "当前状态不允许重试");
        }
        
        if (execution.getRetryCount() >= MAX_RETRY_COUNT) {
            throw exception(LINKAGE_EXECUTION_FAILED, "已达到最大重试次数");
        }
        
        // 3. 获取告警信息
        AlarmDO alarm = alarmMapper.selectById(execution.getAlarmId());
        
        // 4. 获取执行器
        LinkageActionExecutor executor = getExecutor(execution.getActionType());
        if (executor == null) {
            throw exception(LINKAGE_RULE_ACTIONS_INVALID, "未找到动作执行器");
        }
        
        // 5. 构建执行上下文
        Map<String, Object> configMap = parseActionConfig(execution.getActionConfig());
        LinkageActionContext context = LinkageActionContext.builder()
                .alarm(alarm)
                .actionType(execution.getActionType())
                .actionConfig(execution.getActionConfig())
                .actionConfigMap(configMap)
                .targetDeviceId(execution.getTargetDeviceId())
                .targetDeviceName(execution.getTargetDeviceName())
                .dryRun(false)
                .retryCount(execution.getRetryCount())
                .executionId(executionId)
                .build();
        
        // 6. 执行重试
        executeActionWithRetry(execution, executor, context);
        
        return execution;
    }

    // ========== 联动执行记录查询 ==========

    @Override
    public LinkageExecutionDO getLinkageExecution(Long id) {
        return linkageExecutionMapper.selectById(id);
    }

    @Override
    public PageResult<LinkageExecutionDO> getLinkageExecutionPage(LinkageExecutionPageReqVO pageReqVO) {
        return linkageExecutionMapper.selectPage(pageReqVO);
    }

    @Override
    public List<LinkageExecutionDO> getLinkageExecutionsByAlarmId(Long alarmId) {
        return linkageExecutionMapper.selectListByAlarmId(alarmId);
    }

    @Override
    public List<LinkageExecutionDO> getManualInterventionExecutions() {
        return linkageExecutionMapper.selectListNeedManualIntervention();
    }

    // ========== 联动规则测试 ==========

    @Override
    public LinkageRuleTestRespVO testLinkageRule(LinkageRuleTestReqVO testReqVO) {
        LinkageRuleTestRespVO result = new LinkageRuleTestRespVO();
        List<LinkageRuleTestRespVO.ActionTestResult> actionResults = new ArrayList<>();
        long totalStartTime = System.currentTimeMillis();
        
        try {
            // 1. 解析动作配置
            JSONArray actionsArray = JSONUtil.parseArray(testReqVO.getActions());
            if (actionsArray.isEmpty()) {
                result.setSuccess(false);
                result.setErrorMessage("联动动作配置为空");
                return result;
            }
            
            // 2. 是否模拟执行
            boolean dryRun = Boolean.TRUE.equals(testReqVO.getDryRun());
            
            // 3. 执行每个动作
            for (int i = 0; i < actionsArray.size(); i++) {
                JSONObject actionObj = actionsArray.getJSONObject(i);
                String actionType = actionObj.getStr("type");
                
                LinkageRuleTestRespVO.ActionTestResult actionResult = new LinkageRuleTestRespVO.ActionTestResult();
                actionResult.setActionType(actionType);
                
                // 获取执行器
                LinkageActionExecutor executor = getExecutor(actionType);
                if (executor == null) {
                    actionResult.setSuccess(false);
                    actionResult.setErrorMessage("未找到动作执行器: " + actionType);
                    actionResults.add(actionResult);
                    continue;
                }
                
                // 构建上下文
                Map<String, Object> configMap = actionObj.toBean(Map.class);
                LinkageActionContext context = LinkageActionContext.builder()
                        .actionType(actionType)
                        .actionConfig(actionObj.toString())
                        .actionConfigMap(configMap)
                        .targetDeviceId(getTargetDeviceId(configMap))
                        .targetDeviceName(getTargetDeviceName(configMap))
                        .dryRun(dryRun)
                        .retryCount(0)
                        .build();
                
                actionResult.setTargetDeviceName(context.getTargetDeviceName());
                
                // 执行动作
                long actionStartTime = System.currentTimeMillis();
                LinkageActionResult execResult = executor.execute(context);
                actionResult.setExecutionTimeMs(System.currentTimeMillis() - actionStartTime);
                
                actionResult.setSuccess(execResult.isSuccess());
                actionResult.setResult(execResult.getResultMessage());
                actionResult.setErrorMessage(execResult.getErrorMessage());
                
                actionResults.add(actionResult);
            }
            
            // 4. 汇总结果
            long successCount = actionResults.stream().filter(LinkageRuleTestRespVO.ActionTestResult::getSuccess).count();
            result.setSuccess(successCount == actionResults.size());
            result.setActionResults(actionResults);
            result.setTotalExecutionTimeMs(System.currentTimeMillis() - totalStartTime);
            result.setResultMessage(String.format("联动规则测试完成，共%d个动作，成功%d个，失败%d个",
                    actionResults.size(), successCount, actionResults.size() - successCount));
            
        } catch (Exception e) {
            log.error("[测试联动规则] 测试异常", e);
            result.setSuccess(false);
            result.setErrorMessage("测试异常：" + e.getMessage());
            result.setTotalExecutionTimeMs(System.currentTimeMillis() - totalStartTime);
        }
        
        return result;
    }


    // ========== 私有方法：联动执行 ==========

    /**
     * 执行联动规则的所有动作
     *
     * @param alarm       告警信息
     * @param linkageRule 联动规则
     * @param dryRun      是否模拟执行
     * @return 执行记录列表
     */
    private List<LinkageExecutionDO> executeRuleActions(AlarmDO alarm, LinkageRuleDO linkageRule, boolean dryRun) {
        List<LinkageExecutionDO> executions = new ArrayList<>();
        
        try {
            // 1. 解析动作配置
            JSONArray actionsArray = JSONUtil.parseArray(linkageRule.getActions());
            if (actionsArray.isEmpty()) {
                log.warn("[执行联动规则] 规则动作配置为空: ruleId={}", linkageRule.getId());
                return executions;
            }
            
            // 2. 解析动作并按优先级排序（消防控制优先 - BR-BIZ-004）
            List<ActionItem> actionItems = new ArrayList<>();
            for (int i = 0; i < actionsArray.size(); i++) {
                JSONObject actionObj = actionsArray.getJSONObject(i);
                String actionType = actionObj.getStr("type");
                LinkageActionExecutor executor = getExecutor(actionType);
                
                if (executor != null) {
                    actionItems.add(new ActionItem(actionObj, executor));
                } else {
                    log.warn("[执行联动规则] 未找到动作执行器: actionType={}", actionType);
                }
            }
            
            // 按优先级排序（消防控制优先级最高）
            actionItems.sort((a, b) -> Integer.compare(b.executor.getPriority(), a.executor.getPriority()));
            
            // 3. 根据执行模式执行动作
            boolean isParallel = LinkageExecutionModeEnum.isParallel(linkageRule.getExecutionMode());
            
            if (isParallel) {
                // 并行执行
                executions = executeActionsParallel(alarm, linkageRule, actionItems, dryRun);
            } else {
                // 串行执行
                executions = executeActionsSerial(alarm, linkageRule, actionItems, dryRun);
            }
            
        } catch (Exception e) {
            log.error("[执行联动规则] 执行异常: alarmId={}, ruleId={}", alarm.getId(), linkageRule.getId(), e);
        }
        
        return executions;
    }

    /**
     * 串行执行动作
     */
    private List<LinkageExecutionDO> executeActionsSerial(AlarmDO alarm, LinkageRuleDO linkageRule,
                                                          List<ActionItem> actionItems, boolean dryRun) {
        List<LinkageExecutionDO> executions = new ArrayList<>();
        
        for (ActionItem item : actionItems) {
            LinkageExecutionDO execution = executeAction(alarm, linkageRule, item, dryRun);
            executions.add(execution);
        }
        
        return executions;
    }

    /**
     * 并行执行动作
     */
    private List<LinkageExecutionDO> executeActionsParallel(AlarmDO alarm, LinkageRuleDO linkageRule,
                                                            List<ActionItem> actionItems, boolean dryRun) {
        List<LinkageExecutionDO> executions = new ArrayList<>();
        List<Future<LinkageExecutionDO>> futures = new ArrayList<>();
        
        // 提交所有任务
        for (ActionItem item : actionItems) {
            Future<LinkageExecutionDO> future = executorService.submit(() -> 
                    executeAction(alarm, linkageRule, item, dryRun));
            futures.add(future);
        }
        
        // 等待所有任务完成
        for (Future<LinkageExecutionDO> future : futures) {
            try {
                LinkageExecutionDO execution = future.get(60, TimeUnit.SECONDS);
                executions.add(execution);
            } catch (Exception e) {
                log.error("[并行执行联动] 获取执行结果异常", e);
            }
        }
        
        return executions;
    }

    /**
     * 执行单个动作
     */
    private LinkageExecutionDO executeAction(AlarmDO alarm, LinkageRuleDO linkageRule,
                                             ActionItem item, boolean dryRun) {
        String actionType = item.actionObj.getStr("type");
        String actionConfig = item.actionObj.toString();
        Map<String, Object> configMap = item.actionObj.toBean(Map.class);
        
        // 1. 构建执行上下文
        LinkageActionContext context = LinkageActionContext.builder()
                .alarm(alarm)
                .linkageRule(linkageRule)
                .actionType(actionType)
                .actionConfig(actionConfig)
                .actionConfigMap(configMap)
                .targetDeviceId(getTargetDeviceId(configMap))
                .targetDeviceName(getTargetDeviceName(configMap))
                .dryRun(dryRun)
                .retryCount(0)
                .build();
        
        // 2. 创建执行记录
        LinkageExecutionDO execution = createExecutionRecord(
                alarm.getId(), linkageRule.getId(), actionType, actionConfig, context);
        
        // 3. 执行动作（带重试机制）
        executeActionWithRetry(execution, item.executor, context);
        
        return execution;
    }

    /**
     * 执行动作（带重试机制）
     * 
     * <p>业务规则：</p>
     * <ul>
     *   <li>BR-BIZ-005：联动动作执行失败时，系统应自动重试3次，每次间隔5秒</li>
     *   <li>BR-BIZ-006：联动重试3次后仍失败，系统必须标记需要人工介入</li>
     *   <li>BR-BIZ-009：所有联动控制动作必须记录审计日志</li>
     * </ul>
     */
    private void executeActionWithRetry(LinkageExecutionDO execution, 
                                        LinkageActionExecutor executor, 
                                        LinkageActionContext context) {
        int retryCount = context.getRetryCount() != null ? context.getRetryCount() : 0;
        
        // 获取告警信息用于审计日志
        AlarmDO alarm = context.getAlarm();
        if (alarm == null && execution.getAlarmId() != null) {
            alarm = alarmMapper.selectById(execution.getAlarmId());
        }
        
        while (retryCount <= MAX_RETRY_COUNT) {
            // 更新执行状态为执行中
            execution.setExecutionStatus(LinkageExecutionStatusEnum.EXECUTING.getStatus());
            execution.setStartTime(LocalDateTime.now());
            execution.setRetryCount(retryCount);
            linkageExecutionMapper.updateById(execution);
            
            // 执行动作
            LinkageActionResult result = executor.execute(context);
            
            // 更新执行结果
            execution.setEndTime(LocalDateTime.now());
            execution.setDurationMs(result.getExecutionTimeMs());
            execution.setExecutionResult(result.getResultMessage());
            execution.setErrorMessage(result.getErrorMessage());
            
            if (result.isSuccess()) {
                // 执行成功
                execution.setExecutionStatus(LinkageExecutionStatusEnum.SUCCESS.getStatus());
                execution.setManualIntervention(false);
                linkageExecutionMapper.updateById(execution);
                
                // 记录联动执行审计日志（BR-BIZ-009）
                alarmAuditService.logLinkageExecute(execution, alarm);
                
                // 推送联动执行结果（WebSocket）
                pushLinkageExecutionWebSocket(execution, alarm, context.getLinkageRule());
                
                log.info("[联动执行成功] executionId={}, actionType={}, retryCount={}", 
                        execution.getId(), execution.getActionType(), retryCount);
                return;
            }
            
            // 执行失败
            if (!result.isCanRetry()) {
                // 不可重试的错误（如配置错误）
                execution.setExecutionStatus(LinkageExecutionStatusEnum.FAILED.getStatus());
                execution.setManualIntervention(true);
                linkageExecutionMapper.updateById(execution);
                
                // 记录联动执行审计日志（BR-BIZ-009）
                alarmAuditService.logLinkageExecute(execution, alarm);
                
                log.warn("[联动执行失败-不可重试] executionId={}, actionType={}, error={}", 
                        execution.getId(), execution.getActionType(), result.getErrorMessage());
                
                // 发送人工介入通知
                sendManualInterventionNotification(execution);
                
                // 推送联动执行结果和人工介入消息（WebSocket）
                pushLinkageExecutionWebSocket(execution, alarm, context.getLinkageRule());
                pushLinkageManualInterventionWebSocket(execution, alarm, context.getLinkageRule());
                return;
            }
            
            retryCount++;
            
            if (retryCount > MAX_RETRY_COUNT) {
                // 达到最大重试次数，标记需要人工介入（BR-BIZ-006）
                execution.setExecutionStatus(LinkageExecutionStatusEnum.FAILED.getStatus());
                execution.setManualIntervention(true);
                linkageExecutionMapper.updateById(execution);
                
                // 记录联动执行审计日志（BR-BIZ-009）
                alarmAuditService.logLinkageExecute(execution, alarm);
                
                log.warn("[联动执行失败-达到最大重试次数] executionId={}, actionType={}, retryCount={}", 
                        execution.getId(), execution.getActionType(), retryCount - 1);
                
                // 发送人工介入通知
                sendManualInterventionNotification(execution);
                
                // 推送联动执行结果和人工介入消息（WebSocket）
                pushLinkageExecutionWebSocket(execution, alarm, context.getLinkageRule());
                pushLinkageManualInterventionWebSocket(execution, alarm, context.getLinkageRule());
                return;
            }
            
            // 更新状态为重试中
            execution.setExecutionStatus(LinkageExecutionStatusEnum.RETRY.getStatus());
            linkageExecutionMapper.updateById(execution);
            
            // 记录联动重试审计日志（BR-BIZ-009）
            alarmAuditService.logLinkageRetry(execution, alarm, retryCount);
            
            log.info("[联动执行重试] executionId={}, actionType={}, retryCount={}/{}", 
                    execution.getId(), execution.getActionType(), retryCount, MAX_RETRY_COUNT);
            
            // 等待重试间隔（BR-BIZ-005：每次间隔5秒）
            try {
                Thread.sleep(RETRY_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            
            // 更新上下文的重试次数
            context.setRetryCount(retryCount);
        }
    }


    /**
     * 创建执行记录
     */
    private LinkageExecutionDO createExecutionRecord(Long alarmId, Long linkageRuleId,
                                                     String actionType, String actionConfig,
                                                     LinkageActionContext context) {
        LinkageExecutionDO execution = LinkageExecutionDO.builder()
                .alarmId(alarmId)
                .linkageRuleId(linkageRuleId)
                .actionType(actionType)
                .actionConfig(actionConfig)
                .targetDeviceId(context.getTargetDeviceId())
                .targetDeviceName(context.getTargetDeviceName())
                .executionStatus(LinkageExecutionStatusEnum.PENDING.getStatus())
                .retryCount(0)
                .manualIntervention(false)
                .build();
        
        linkageExecutionMapper.insert(execution);
        return execution;
    }

    /**
     * 发送人工介入通知
     */
    private void sendManualInterventionNotification(LinkageExecutionDO execution) {
        // TODO: 调用通知服务发送人工介入通知
        log.warn("[人工介入通知] 联动执行失败需要人工介入: executionId={}, actionType={}, error={}", 
                execution.getId(), execution.getActionType(), execution.getErrorMessage());
    }

    /**
     * 推送联动执行结果（WebSocket）
     *
     * @param execution 联动执行记录
     * @param alarm 告警信息
     * @param linkageRule 联动规则
     */
    private void pushLinkageExecutionWebSocket(LinkageExecutionDO execution, AlarmDO alarm, LinkageRuleDO linkageRule) {
        try {
            String alarmCode = alarm != null ? alarm.getAlarmCode() : null;
            String linkageRuleName = linkageRule != null ? linkageRule.getRuleName() : null;
            alarmWebSocketService.pushLinkageExecutionResult(execution, alarmCode, linkageRuleName);
        } catch (Exception e) {
            log.error("[推送联动执行结果] 推送失败: executionId={}", execution.getId(), e);
        }
    }

    /**
     * 推送联动需要人工介入消息（WebSocket）
     *
     * @param execution 联动执行记录
     * @param alarm 告警信息
     * @param linkageRule 联动规则
     */
    private void pushLinkageManualInterventionWebSocket(LinkageExecutionDO execution, AlarmDO alarm, LinkageRuleDO linkageRule) {
        try {
            String alarmCode = alarm != null ? alarm.getAlarmCode() : null;
            String linkageRuleName = linkageRule != null ? linkageRule.getRuleName() : null;
            alarmWebSocketService.pushLinkageManualIntervention(execution, alarmCode, linkageRuleName);
        } catch (Exception e) {
            log.error("[推送人工介入消息] 推送失败: executionId={}", execution.getId(), e);
        }
    }

    // ========== 私有方法：工具方法 ==========

    /**
     * 获取执行器
     */
    private LinkageActionExecutor getExecutor(String actionType) {
        if (CollUtil.isEmpty(executors)) {
            return null;
        }
        return executors.stream()
                .filter(e -> e.getActionType().getType().equals(actionType))
                .findFirst()
                .orElse(null);
    }

    /**
     * 解析动作配置
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseActionConfig(String actionConfig) {
        if (StrUtil.isBlank(actionConfig)) {
            return new HashMap<>();
        }
        try {
            return JSONUtil.parseObj(actionConfig).toBean(Map.class);
        } catch (Exception e) {
            log.warn("[解析动作配置] 解析失败: {}", actionConfig);
            return new HashMap<>();
        }
    }

    /**
     * 获取目标设备ID
     */
    private Long getTargetDeviceId(Map<String, Object> configMap) {
        if (configMap == null) {
            return null;
        }
        Object deviceId = configMap.get("deviceId");
        if (deviceId == null) {
            return null;
        }
        if (deviceId instanceof Number) {
            return ((Number) deviceId).longValue();
        }
        try {
            return Long.parseLong(deviceId.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取目标设备名称
     */
    private String getTargetDeviceName(Map<String, Object> configMap) {
        if (configMap == null) {
            return null;
        }
        Object deviceName = configMap.get("deviceName");
        return deviceName != null ? deviceName.toString() : null;
    }

    // ========== 私有方法：校验方法 ==========

    /**
     * 校验联动规则存在
     */
    private LinkageRuleDO validateLinkageRuleExists(Long id) {
        LinkageRuleDO linkageRule = linkageRuleMapper.selectById(id);
        if (linkageRule == null) {
            throw exception(LINKAGE_RULE_NOT_EXISTS);
        }
        return linkageRule;
    }

    /**
     * 校验规则编码唯一性
     */
    private void validateRuleCodeUnique(String ruleCode, Long excludeId) {
        if (linkageRuleMapper.existsByRuleCode(ruleCode, excludeId)) {
            throw exception(LINKAGE_RULE_CODE_DUPLICATE);
        }
    }

    /**
     * 校验动作配置有效性
     */
    private void validateActionsConfig(String actions) {
        if (StrUtil.isBlank(actions)) {
            throw exception(LINKAGE_RULE_ACTIONS_INVALID, "动作配置不能为空");
        }
        
        try {
            JSONArray actionsArray = JSONUtil.parseArray(actions);
            if (actionsArray.isEmpty()) {
                throw exception(LINKAGE_RULE_ACTIONS_INVALID, "动作配置不能为空数组");
            }
            
            for (int i = 0; i < actionsArray.size(); i++) {
                JSONObject actionObj = actionsArray.getJSONObject(i);
                String actionType = actionObj.getStr("type");
                
                if (StrUtil.isBlank(actionType)) {
                    throw exception(LINKAGE_RULE_ACTIONS_INVALID, "动作类型不能为空");
                }
                
                LinkageActionTypeEnum typeEnum = LinkageActionTypeEnum.getByType(actionType);
                if (typeEnum == null) {
                    throw exception(LINKAGE_RULE_ACTIONS_INVALID, "不支持的动作类型: " + actionType);
                }
            }
        } catch (cn.hutool.json.JSONException e) {
            throw exception(LINKAGE_RULE_ACTIONS_INVALID, "动作配置JSON格式无效");
        }
    }

    // ========== 内部类 ==========

    /**
     * 动作项（用于排序）
     */
    private static class ActionItem {
        final JSONObject actionObj;
        final LinkageActionExecutor executor;
        
        ActionItem(JSONObject actionObj, LinkageActionExecutor executor) {
            this.actionObj = actionObj;
            this.executor = executor;
        }
    }

}
