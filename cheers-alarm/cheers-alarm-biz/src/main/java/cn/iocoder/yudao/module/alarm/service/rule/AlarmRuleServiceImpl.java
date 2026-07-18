package cn.iocoder.yudao.module.alarm.service.rule;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.rule.*;
import cn.iocoder.yudao.module.alarm.convert.AlarmRuleConvert;
import cn.iocoder.yudao.module.alarm.dal.dataobject.AlarmRuleDO;
import cn.iocoder.yudao.module.alarm.dal.mysql.AlarmRuleMapper;
import cn.iocoder.yudao.module.alarm.enums.AlarmRuleTypeEnum;
import cn.iocoder.yudao.module.alarm.framework.cache.AlarmQueryCacheService;
import cn.iocoder.yudao.module.alarm.framework.cache.DeviceDataCacheService;
import cn.iocoder.yudao.module.alarm.service.rule.pattern.DataPoint;
import cn.iocoder.yudao.module.alarm.service.rule.pattern.PatternDetectionResult;
import cn.iocoder.yudao.module.alarm.service.rule.pattern.PatternDetectorManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.alarm.enums.ErrorCodeConstants.*;

/**
 * 告警规则 Service 实现类
 *
 * <p>实现告警规则的 CRUD 操作、规则匹配和规则测试功能</p>
 * <p>支持四种规则类型：阈值告警、变化率告警、异常模式告警、组合条件告警</p>
 *
 * @author 告警管理模块
 */
@Service
@Validated
@Slf4j
public class AlarmRuleServiceImpl implements AlarmRuleService {

    @Resource
    private AlarmRuleMapper alarmRuleMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private AlarmQueryCacheService alarmQueryCacheService;

    @Resource
    private DeviceDataCacheService deviceDataCacheService;

    @Resource
    private PatternDetectorManager patternDetectorManager;

    /**
     * 模板变量匹配模式：${variableName}
     */
    private static final Pattern TEMPLATE_VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    // ========== 规则 CRUD 操作 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAlarmRule(AlarmRuleCreateReqVO createReqVO) {
        // 1. 校验规则编码唯一性
        validateRuleCodeUnique(null, createReqVO.getRuleCode());
        
        // 2. 校验条件表达式有效性
        validateConditionExpression(createReqVO.getRuleType(), createReqVO.getConditionExpression());
        
        // 3. 转换并插入数据
        AlarmRuleDO alarmRule = AlarmRuleConvert.INSTANCE.convert(createReqVO);
        // 设置默认值
        if (alarmRule.getEnabled() == null) {
            alarmRule.setEnabled(true);
        }
        if (alarmRule.getPriority() == null) {
            alarmRule.setPriority(0);
        }
        alarmRuleMapper.insert(alarmRule);
        
        // 清除规则缓存（性能优化）
        alarmQueryCacheService.clearAlarmRulesCache();
        
        log.info("[createAlarmRule][创建告警规则成功，规则ID: {}, 规则编码: {}]", 
                alarmRule.getId(), alarmRule.getRuleCode());
        return alarmRule.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAlarmRule(AlarmRuleUpdateReqVO updateReqVO) {
        // 1. 校验规则存在
        AlarmRuleDO existingRule = validateAlarmRuleExists(updateReqVO.getId());
        
        // 2. 校验条件表达式有效性
        validateConditionExpression(updateReqVO.getRuleType(), updateReqVO.getConditionExpression());
        
        // 3. 更新数据
        AlarmRuleDO updateObj = AlarmRuleConvert.INSTANCE.convert(updateReqVO);
        // 保留原有的规则编码（不允许修改）
        updateObj.setRuleCode(existingRule.getRuleCode());
        alarmRuleMapper.updateById(updateObj);
        
        // 清除规则缓存（性能优化）
        alarmQueryCacheService.clearAlarmRulesCache();
        
        log.info("[updateAlarmRule][更新告警规则成功，规则ID: {}]", updateReqVO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAlarmRule(Long id) {
        // 1. 校验规则存在
        validateAlarmRuleExists(id);
        
        // 2. 删除规则
        alarmRuleMapper.deleteById(id);
        
        // 清除规则缓存（性能优化）
        alarmQueryCacheService.clearAlarmRulesCache();
        
        log.info("[deleteAlarmRule][删除告警规则成功，规则ID: {}]", id);
    }

    @Override
    public AlarmRuleDO getAlarmRule(Long id) {
        return alarmRuleMapper.selectById(id);
    }

    @Override
    public PageResult<AlarmRuleDO> getAlarmRulePage(AlarmRulePageReqVO pageReqVO) {
        return alarmRuleMapper.selectPage(pageReqVO);
    }

    @Override
    public List<AlarmRuleDO> getEnabledAlarmRuleList() {
        // 尝试从缓存获取（性能优化）
        List<AlarmRuleDO> cachedRules = alarmQueryCacheService.getEnabledAlarmRulesCache();
        if (cachedRules != null) {
            log.debug("[getEnabledAlarmRuleList][从缓存获取启用的告警规则] count={}", cachedRules.size());
            return cachedRules;
        }
        
        // 从数据库查询
        List<AlarmRuleDO> rules = alarmRuleMapper.selectEnabledRules();
        
        // 设置缓存
        alarmQueryCacheService.setEnabledAlarmRulesCache(rules);
        
        return rules;
    }

    @Override
    public List<AlarmRuleDO> getEnabledAlarmRuleListByDeviceType(String deviceType) {
        return alarmRuleMapper.selectEnabledRulesByDeviceType(deviceType);
    }

    @Override
    public List<AlarmRuleDO> getEnabledAlarmRuleListByAlarmTypeId(Long alarmTypeId) {
        return alarmRuleMapper.selectEnabledRulesByAlarmTypeId(alarmTypeId);
    }

    // ========== 规则状态管理 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAlarmRuleStatus(Long id, Boolean enabled) {
        // 1. 校验规则存在
        validateAlarmRuleExists(id);
        
        // 2. 更新状态
        AlarmRuleDO updateObj = new AlarmRuleDO();
        updateObj.setId(id);
        updateObj.setEnabled(enabled);
        alarmRuleMapper.updateById(updateObj);
        
        // 清除规则缓存（性能优化）
        alarmQueryCacheService.clearAlarmRulesCache();
        
        log.info("[updateAlarmRuleStatus][更新告警规则状态，规则ID: {}, 启用状态: {}]", id, enabled);
    }

    // ========== 规则匹配 ==========

    @Override
    public List<AlarmRuleDO> matchRules(String deviceType, Map<String, Object> deviceData) {
        return matchRulesWithHistory(deviceType, deviceData, null);
    }

    @Override
    public List<AlarmRuleDO> matchRulesWithHistory(String deviceType, Map<String, Object> deviceData,
                                                    Map<String, Object> historicalData) {
        if (deviceData == null || deviceData.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. 获取适用的规则列表
        List<AlarmRuleDO> rules = getEnabledAlarmRuleListByDeviceType(deviceType);
        if (CollUtil.isEmpty(rules)) {
            return Collections.emptyList();
        }

        // 2. 逐个评估规则
        List<AlarmRuleDO> matchedRules = new ArrayList<>();
        for (AlarmRuleDO rule : rules) {
            try {
                boolean matched = evaluateRuleWithHistory(rule, deviceData, historicalData);
                if (matched) {
                    matchedRules.add(rule);
                    log.debug("[matchRulesWithHistory][规则匹配成功，规则ID: {}, 规则名称: {}]", 
                            rule.getId(), rule.getRuleName());
                }
            } catch (Exception e) {
                log.warn("[matchRulesWithHistory][规则评估异常，规则ID: {}, 错误: {}]", 
                        rule.getId(), e.getMessage());
            }
        }

        return matchedRules;
    }

    @Override
    public boolean evaluateRule(AlarmRuleDO rule, Map<String, Object> deviceData) {
        return evaluateRuleWithHistory(rule, deviceData, null);
    }

    @Override
    public boolean evaluateRuleWithHistory(AlarmRuleDO rule, Map<String, Object> deviceData,
                                           Map<String, Object> historicalData) {
        if (rule == null || !Boolean.TRUE.equals(rule.getEnabled())) {
            return false;
        }

        String ruleType = rule.getRuleType();
        String conditionExpression = rule.getConditionExpression();

        if (StrUtil.isBlank(conditionExpression)) {
            return false;
        }

        try {
            Map<String, Object> condition = parseConditionExpression(conditionExpression);
            
            // 根据规则类型进行评估
            AlarmRuleTypeEnum ruleTypeEnum = AlarmRuleTypeEnum.getByType(ruleType);
            if (ruleTypeEnum == null) {
                log.warn("[evaluateRuleWithHistory][未知的规则类型: {}]", ruleType);
                return false;
            }

            return switch (ruleTypeEnum) {
                case THRESHOLD -> evaluateThresholdCondition(condition, deviceData);
                case RATE -> evaluateRateCondition(condition, deviceData, historicalData);
                case PATTERN -> evaluatePatternCondition(condition, deviceData);
                case COMBINATION -> evaluateCombinationCondition(condition, deviceData, historicalData);
            };
        } catch (Exception e) {
            log.warn("[evaluateRuleWithHistory][规则评估异常，规则ID: {}, 错误: {}]", 
                    rule.getId(), e.getMessage());
            return false;
        }
    }


    // ========== 规则测试 ==========

    @Override
    public AlarmRuleTestRespVO testAlarmRule(AlarmRuleTestReqVO testReqVO) {
        long startTime = System.currentTimeMillis();
        AlarmRuleTestRespVO result = new AlarmRuleTestRespVO();

        try {
            // 1. 解析测试数据
            Map<String, Object> testData = parseTestData(testReqVO.getTestData());
            
            // 2. 校验条件表达式
            validateConditionExpression(testReqVO.getRuleType(), testReqVO.getConditionExpression());
            
            // 3. 构建临时规则进行评估
            AlarmRuleDO tempRule = new AlarmRuleDO();
            tempRule.setRuleType(testReqVO.getRuleType());
            tempRule.setConditionExpression(testReqVO.getConditionExpression());
            tempRule.setEnabled(true);
            
            // 如果指定了规则ID，获取规则的告警内容模板
            if (testReqVO.getRuleId() != null) {
                AlarmRuleDO existingRule = getAlarmRule(testReqVO.getRuleId());
                if (existingRule != null) {
                    tempRule.setAlarmContentTemplate(existingRule.getAlarmContentTemplate());
                    tempRule.setAlarmLevel(existingRule.getAlarmLevel());
                }
            }
            
            // 4. 评估规则
            boolean matched = evaluateRule(tempRule, testData);
            result.setMatched(matched);
            
            // 5. 生成匹配结果说明
            String matchResult = generateMatchResultDescription(testReqVO.getRuleType(), 
                    testReqVO.getConditionExpression(), testData, matched);
            result.setMatchResult(matchResult);
            
            // 6. 如果匹配成功，生成告警内容
            if (matched && StrUtil.isNotBlank(tempRule.getAlarmContentTemplate())) {
                String alarmContent = generateAlarmContent(tempRule, testData);
                result.setGeneratedAlarmContent(alarmContent);
            }
            
            result.setAlarmLevel(tempRule.getAlarmLevel());
            
        } catch (Exception e) {
            result.setMatched(false);
            result.setErrorMessage(e.getMessage());
            log.warn("[testAlarmRule][规则测试失败: {}]", e.getMessage());
        }

        result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        return result;
    }

    // ========== 告警内容生成 ==========

    @Override
    public String generateAlarmContent(AlarmRuleDO rule, Map<String, Object> deviceData) {
        String template = rule.getAlarmContentTemplate();
        if (StrUtil.isBlank(template)) {
            return null;
        }

        // 替换模板中的变量
        StringBuffer result = new StringBuffer();
        Matcher matcher = TEMPLATE_VARIABLE_PATTERN.matcher(template);
        
        while (matcher.find()) {
            String variableName = matcher.group(1);
            Object value = deviceData.get(variableName);
            String replacement = value != null ? String.valueOf(value) : "";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    // ========== 校验方法 ==========

    @Override
    public AlarmRuleDO validateAlarmRuleExists(Long id) {
        AlarmRuleDO rule = alarmRuleMapper.selectById(id);
        if (rule == null) {
            throw exception(ALARM_RULE_NOT_EXISTS);
        }
        return rule;
    }

    @Override
    public void validateConditionExpression(String ruleType, String conditionExpression) {
        if (StrUtil.isBlank(conditionExpression)) {
            throw exception(ALARM_RULE_CONDITION_INVALID, "条件表达式不能为空");
        }

        try {
            Map<String, Object> condition = parseConditionExpression(conditionExpression);
            
            AlarmRuleTypeEnum ruleTypeEnum = AlarmRuleTypeEnum.getByType(ruleType);
            if (ruleTypeEnum == null) {
                throw exception(ALARM_RULE_CONDITION_INVALID, "未知的规则类型: " + ruleType);
            }

            // 根据规则类型校验必要字段
            switch (ruleTypeEnum) {
                case THRESHOLD -> validateThresholdCondition(condition);
                case RATE -> validateRateCondition(condition);
                case PATTERN -> validatePatternCondition(condition);
                case COMBINATION -> validateCombinationCondition(condition);
            }
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("告警规则条件表达式无效")) {
                throw e;
            }
            throw exception(ALARM_RULE_CONDITION_INVALID, e.getMessage());
        }
    }

    // ========== 私有方法：条件解析 ==========

    /**
     * 解析条件表达式JSON
     */
    private Map<String, Object> parseConditionExpression(String conditionExpression) {
        try {
            return objectMapper.readValue(conditionExpression, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw exception(ALARM_RULE_CONDITION_INVALID, "JSON格式错误: " + e.getMessage());
        }
    }

    /**
     * 解析测试数据JSON
     */
    private Map<String, Object> parseTestData(String testData) {
        try {
            return objectMapper.readValue(testData, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw exception(ALARM_RULE_CONDITION_INVALID, "测试数据JSON格式错误: " + e.getMessage());
        }
    }

    // ========== 私有方法：条件校验 ==========

    /**
     * 校验阈值条件
     */
    private void validateThresholdCondition(Map<String, Object> condition) {
        if (!condition.containsKey("field")) {
            throw exception(ALARM_RULE_CONDITION_INVALID, "阈值条件缺少field字段");
        }
        if (!condition.containsKey("operator")) {
            throw exception(ALARM_RULE_CONDITION_INVALID, "阈值条件缺少operator字段");
        }
        if (!condition.containsKey("value")) {
            throw exception(ALARM_RULE_CONDITION_INVALID, "阈值条件缺少value字段");
        }
        
        String operator = String.valueOf(condition.get("operator"));
        if (!isValidOperator(operator)) {
            throw exception(ALARM_RULE_CONDITION_INVALID, "无效的操作符: " + operator);
        }
    }

    /**
     * 校验变化率条件
     */
    private void validateRateCondition(Map<String, Object> condition) {
        if (!condition.containsKey("field")) {
            throw exception(ALARM_RULE_CONDITION_INVALID, "变化率条件缺少field字段");
        }
        if (!condition.containsKey("operator")) {
            throw exception(ALARM_RULE_CONDITION_INVALID, "变化率条件缺少operator字段");
        }
        if (!condition.containsKey("value")) {
            throw exception(ALARM_RULE_CONDITION_INVALID, "变化率条件缺少value字段");
        }
        // timeWindow 是可选的，默认为300秒（5分钟）
    }

    /**
     * 校验模式条件
     * 
     * <p>支持两种模式：
     * <ul>
     *   <li>正则匹配：需要 field 和 pattern 字段</li>
     *   <li>高级模式检测：需要 field 和 patternType 字段</li>
     * </ul>
     * </p>
     */
    private void validatePatternCondition(Map<String, Object> condition) {
        if (!condition.containsKey("field")) {
            throw exception(ALARM_RULE_CONDITION_INVALID, "模式条件缺少field字段");
        }
        
        // 检查是否是高级模式检测
        if (condition.containsKey("patternType")) {
            String patternType = String.valueOf(condition.get("patternType"));
            // 验证模式类型是否支持
            if (!patternDetectorManager.isSupported(patternType)) {
                throw exception(ALARM_RULE_CONDITION_INVALID, 
                        "不支持的模式类型: " + patternType + "，支持的类型: " + 
                        patternDetectorManager.getSupportedPatternTypes());
            }
            return;
        }
        
        // 传统正则匹配模式
        if (!condition.containsKey("pattern")) {
            throw exception(ALARM_RULE_CONDITION_INVALID, "模式条件缺少pattern字段或patternType字段");
        }
    }

    /**
     * 校验组合条件
     */
    private void validateCombinationCondition(Map<String, Object> condition) {
        if (!condition.containsKey("logic")) {
            throw exception(ALARM_RULE_CONDITION_INVALID, "组合条件缺少logic字段");
        }
        if (!condition.containsKey("conditions")) {
            throw exception(ALARM_RULE_CONDITION_INVALID, "组合条件缺少conditions字段");
        }
        
        String logic = String.valueOf(condition.get("logic"));
        if (!isValidLogicOperator(logic)) {
            throw exception(ALARM_RULE_CONDITION_INVALID, "无效的逻辑操作符: " + logic);
        }
        
        Object conditions = condition.get("conditions");
        if (!(conditions instanceof List) || ((List<?>) conditions).isEmpty()) {
            throw exception(ALARM_RULE_CONDITION_INVALID, "组合条件的conditions必须是非空数组");
        }
    }


    // ========== 私有方法：条件评估 ==========

    /**
     * 评估阈值条件
     * 
     * 条件格式：{"field":"waterLevel","operator":">","value":50}
     */
    private boolean evaluateThresholdCondition(Map<String, Object> condition, Map<String, Object> deviceData) {
        String field = String.valueOf(condition.get("field"));
        String operator = String.valueOf(condition.get("operator"));
        Object thresholdValue = condition.get("value");

        Object actualValue = deviceData.get(field);
        if (actualValue == null) {
            return false;
        }

        return compareValues(actualValue, operator, thresholdValue);
    }

    /**
     * 评估变化率条件
     * 
     * 条件格式：{"field":"temperature","operator":">","value":5,"timeWindow":300}
     */
    private boolean evaluateRateCondition(Map<String, Object> condition, Map<String, Object> deviceData,
                                          Map<String, Object> historicalData) {
        if (historicalData == null || historicalData.isEmpty()) {
            // 没有历史数据，无法计算变化率
            return false;
        }

        String field = String.valueOf(condition.get("field"));
        String operator = String.valueOf(condition.get("operator"));
        Object rateThreshold = condition.get("value");

        Object currentValue = deviceData.get(field);
        Object previousValue = historicalData.get(field);

        if (currentValue == null || previousValue == null) {
            return false;
        }

        // 计算变化率
        BigDecimal current = toBigDecimal(currentValue);
        BigDecimal previous = toBigDecimal(previousValue);
        
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            // 避免除零错误
            return current.compareTo(BigDecimal.ZERO) != 0;
        }

        BigDecimal rate = current.subtract(previous).abs();
        
        return compareValues(rate, operator, rateThreshold);
    }

    /**
     * 评估模式条件
     * 
     * <p>支持两种模式：
     * <ul>
     *   <li>正则匹配：{"field":"status","pattern":"ERROR|FAULT|ALARM"}</li>
     *   <li>高级模式检测：{"field":"temperature","patternType":"SUDDEN_CHANGE","count":5,"timeWindow":300}</li>
     * </ul>
     * </p>
     * 
     * <p>支持的高级模式类型（patternType）：
     * <ul>
     *   <li>CONSECUTIVE_SAME - 连续相同值检测</li>
     *   <li>SUDDEN_CHANGE - 突变检测（3σ原则）</li>
     *   <li>TREND - 趋势检测（线性回归）</li>
     *   <li>PERIODIC_FLUCTUATION - 周期性波动检测</li>
     * </ul>
     * </p>
     */
    private boolean evaluatePatternCondition(Map<String, Object> condition, Map<String, Object> deviceData) {
        String field = String.valueOf(condition.get("field"));
        
        // 检查是否是高级模式检测
        if (condition.containsKey("patternType")) {
            return evaluateAdvancedPatternCondition(condition, deviceData);
        }
        
        // 传统正则匹配模式
        String pattern = String.valueOf(condition.get("pattern"));
        Object actualValue = deviceData.get(field);
        if (actualValue == null) {
            return false;
        }

        String valueStr = String.valueOf(actualValue);
        try {
            return valueStr.matches(pattern);
        } catch (Exception e) {
            log.warn("[evaluatePatternCondition][正则表达式匹配失败，pattern: {}, value: {}]", pattern, valueStr);
            return false;
        }
    }

    /**
     * 评估高级模式条件
     * 
     * <p>使用模式检测器进行时序数据分析</p>
     */
    private boolean evaluateAdvancedPatternCondition(Map<String, Object> condition, Map<String, Object> deviceData) {
        String field = String.valueOf(condition.get("field"));
        String patternType = String.valueOf(condition.get("patternType"));
        
        // 获取设备 ID（从 deviceData 中获取，或使用默认值）
        String deviceId = deviceData.containsKey("deviceId") 
                ? String.valueOf(deviceData.get("deviceId")) 
                : "unknown";
        
        // 获取时间窗口（秒），默认 5 分钟
        int timeWindow = getIntParam(condition, "timeWindow", 300);
        
        // 获取检测次数，默认 5 次
        int count = getIntParam(condition, "count", 5);
        
        // 从缓存获取历史数据
        List<DataPoint> dataPoints = deviceDataCacheService.getDataPointsInWindow(deviceId, field, timeWindow);
        
        // 如果历史数据不足，添加当前数据点
        Object currentValue = deviceData.get(field);
        if (currentValue != null) {
            try {
                BigDecimal value = toBigDecimal(currentValue);
                // 添加当前数据点到缓存
                deviceDataCacheService.addDataPoint(deviceId, field, value);
                // 添加到检测数据中
                dataPoints.add(new DataPoint(java.time.LocalDateTime.now(), value));
            } catch (Exception e) {
                log.warn("[evaluateAdvancedPatternCondition][转换当前值失败: {}]", e.getMessage());
            }
        }
        
        // 数据点不足时无法进行模式检测
        if (dataPoints.size() < count) {
            log.debug("[evaluateAdvancedPatternCondition][数据点不足，需要 {} 个，当前 {} 个]", 
                    count, dataPoints.size());
            return false;
        }
        
        // 构建检测参数
        Map<String, Object> params = new HashMap<>(condition);
        params.put("count", count);
        
        // 执行模式检测
        PatternDetectionResult result = patternDetectorManager.detect(patternType, dataPoints, params);
        
        if (result.isDetected()) {
            log.info("[evaluateAdvancedPatternCondition][检测到异常模式: patternType={}, field={}, deviceId={}, description={}]",
                    patternType, field, deviceId, result.getDescription());
        }
        
        return result.isDetected();
    }

    /**
     * 获取整数参数
     */
    private int getIntParam(Map<String, Object> params, String key, int defaultValue) {
        if (params == null || !params.containsKey(key)) {
            return defaultValue;
        }
        Object value = params.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 评估组合条件
     * 
     * 条件格式：{"logic":"AND","conditions":[{"field":"temperature","operator":">","value":35},{"field":"humidity","operator":">","value":80}]}
     */
    @SuppressWarnings("unchecked")
    private boolean evaluateCombinationCondition(Map<String, Object> condition, Map<String, Object> deviceData,
                                                  Map<String, Object> historicalData) {
        String logic = String.valueOf(condition.get("logic")).toUpperCase();
        List<Map<String, Object>> conditions = (List<Map<String, Object>>) condition.get("conditions");

        if (CollUtil.isEmpty(conditions)) {
            return false;
        }

        List<Boolean> results = new ArrayList<>();
        for (Map<String, Object> subCondition : conditions) {
            // 获取子条件的类型，默认为阈值条件
            String subType = subCondition.containsKey("type") 
                    ? String.valueOf(subCondition.get("type")) 
                    : AlarmRuleTypeEnum.THRESHOLD.getType();
            
            boolean subResult = evaluateSubCondition(subType, subCondition, deviceData, historicalData);
            results.add(subResult);
        }

        return switch (logic) {
            case "AND" -> results.stream().allMatch(Boolean::booleanValue);
            case "OR" -> results.stream().anyMatch(Boolean::booleanValue);
            case "NOT" -> !results.isEmpty() && !results.get(0);
            default -> false;
        };
    }

    /**
     * 评估子条件
     */
    private boolean evaluateSubCondition(String type, Map<String, Object> condition, 
                                         Map<String, Object> deviceData, Map<String, Object> historicalData) {
        AlarmRuleTypeEnum ruleType = AlarmRuleTypeEnum.getByType(type);
        if (ruleType == null) {
            ruleType = AlarmRuleTypeEnum.THRESHOLD;
        }

        return switch (ruleType) {
            case THRESHOLD -> evaluateThresholdCondition(condition, deviceData);
            case RATE -> evaluateRateCondition(condition, deviceData, historicalData);
            case PATTERN -> evaluatePatternCondition(condition, deviceData);
            case COMBINATION -> evaluateCombinationCondition(condition, deviceData, historicalData);
        };
    }

    // ========== 私有方法：工具方法 ==========

    /**
     * 比较两个值
     */
    private boolean compareValues(Object actualValue, String operator, Object thresholdValue) {
        BigDecimal actual = toBigDecimal(actualValue);
        BigDecimal threshold = toBigDecimal(thresholdValue);

        return switch (operator) {
            case ">" -> actual.compareTo(threshold) > 0;
            case ">=" -> actual.compareTo(threshold) >= 0;
            case "<" -> actual.compareTo(threshold) < 0;
            case "<=" -> actual.compareTo(threshold) <= 0;
            case "=", "==" -> actual.compareTo(threshold) == 0;
            case "!=", "<>" -> actual.compareTo(threshold) != 0;
            default -> false;
        };
    }

    /**
     * 转换为BigDecimal
     */
    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        try {
            return new BigDecimal(String.valueOf(value));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * 校验操作符是否有效
     */
    private boolean isValidOperator(String operator) {
        return Set.of(">", ">=", "<", "<=", "=", "==", "!=", "<>").contains(operator);
    }

    /**
     * 校验逻辑操作符是否有效
     */
    private boolean isValidLogicOperator(String logic) {
        return Set.of("AND", "OR", "NOT").contains(logic.toUpperCase());
    }

    /**
     * 校验规则编码唯一性
     */
    private void validateRuleCodeUnique(Long excludeId, String ruleCode) {
        if (alarmRuleMapper.existsByRuleCode(ruleCode, excludeId)) {
            throw exception(ALARM_RULE_CODE_DUPLICATE);
        }
    }

    /**
     * 生成匹配结果描述
     */
    private String generateMatchResultDescription(String ruleType, String conditionExpression,
                                                   Map<String, Object> testData, boolean matched) {
        try {
            Map<String, Object> condition = parseConditionExpression(conditionExpression);
            StringBuilder sb = new StringBuilder();
            
            if (matched) {
                sb.append("条件匹配成功：");
            } else {
                sb.append("条件未匹配：");
            }

            AlarmRuleTypeEnum ruleTypeEnum = AlarmRuleTypeEnum.getByType(ruleType);
            if (ruleTypeEnum == null) {
                return sb.append("未知规则类型").toString();
            }

            switch (ruleTypeEnum) {
                case THRESHOLD -> {
                    String field = String.valueOf(condition.get("field"));
                    String operator = String.valueOf(condition.get("operator"));
                    Object threshold = condition.get("value");
                    Object actual = testData.get(field);
                    sb.append(String.format("%s(%s) %s %s", field, actual, operator, threshold));
                }
                case RATE -> {
                    String field = String.valueOf(condition.get("field"));
                    sb.append(String.format("字段 %s 的变化率检查", field));
                }
                case PATTERN -> {
                    String field = String.valueOf(condition.get("field"));
                    String pattern = String.valueOf(condition.get("pattern"));
                    Object actual = testData.get(field);
                    sb.append(String.format("%s(%s) 匹配模式 %s", field, actual, pattern));
                }
                case COMBINATION -> {
                    String logic = String.valueOf(condition.get("logic"));
                    sb.append(String.format("组合条件(%s)检查", logic));
                }
            }

            return sb.toString();
        } catch (Exception e) {
            return matched ? "条件匹配成功" : "条件未匹配";
        }
    }

}
