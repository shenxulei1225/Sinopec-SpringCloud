package cn.iocoder.yudao.module.alarm.service;

import cn.cheers.x.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.alarm.config.AlarmTestConfiguration;
import cn.iocoder.yudao.module.alarm.config.AlarmTestDataBuilder;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.rule.*;
import cn.iocoder.yudao.module.alarm.dal.dataobject.AlarmRuleDO;
import cn.iocoder.yudao.module.alarm.dal.mysql.AlarmRuleMapper;
import cn.iocoder.yudao.module.alarm.enums.AlarmRuleTypeEnum;
import cn.iocoder.yudao.module.alarm.service.rule.AlarmRuleServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AlarmRuleService 单元测试
 * 
 * <p>测试规则匹配、条件表达式解析等核心逻辑</p>
 * 
 * <p>覆盖需求：FR-008（告警规则配置）</p>
 *
 * @author 告警管理模块
 */
@Import({AlarmTestConfiguration.class, AlarmRuleServiceImpl.class})
@DisplayName("AlarmRuleService 单元测试")
public class AlarmRuleServiceTest extends BaseDbUnitTest {

    @Resource
    private AlarmRuleServiceImpl alarmRuleService;

    @Resource
    private AlarmRuleMapper alarmRuleMapper;

    // ========== 规则匹配测试 ==========

    @Test
    @DisplayName("测试阈值规则匹配 - 大于阈值")
    void testMatchRules_Threshold_GreaterThan() {
        // 准备：创建阈值规则
        AlarmRuleDO rule = AlarmTestDataBuilder.buildAlarmRuleDO()
                .ruleType(AlarmRuleTypeEnum.THRESHOLD.getType())
                .deviceType("WATER_LEVEL_SENSOR")
                .conditionExpression("{\"field\":\"waterLevel\",\"operator\":\">\",\"value\":50}")
                .enabled(true)
                .build();
        alarmRuleMapper.insert(rule);
        
        // 准备设备数据（超过阈值）
        Map<String, Object> deviceData = new HashMap<>();
        deviceData.put("waterLevel", 55);
        
        // 执行
        List<AlarmRuleDO> matchedRules = alarmRuleService.matchRules("WATER_LEVEL_SENSOR", deviceData);
        
        // 验证
        assertFalse(matchedRules.isEmpty());
        assertEquals(rule.getId(), matchedRules.get(0).getId());
    }

    @Test
    @DisplayName("测试阈值规则匹配 - 未超过阈值")
    void testMatchRules_Threshold_NotExceed() {
        // 准备：创建阈值规则
        AlarmRuleDO rule = AlarmTestDataBuilder.buildAlarmRuleDO()
                .ruleType(AlarmRuleTypeEnum.THRESHOLD.getType())
                .deviceType("WATER_LEVEL_SENSOR")
                .conditionExpression("{\"field\":\"waterLevel\",\"operator\":\">\",\"value\":50}")
                .enabled(true)
                .build();
        alarmRuleMapper.insert(rule);
        
        // 准备设备数据（未超过阈值）
        Map<String, Object> deviceData = new HashMap<>();
        deviceData.put("waterLevel", 45);
        
        // 执行
        List<AlarmRuleDO> matchedRules = alarmRuleService.matchRules("WATER_LEVEL_SENSOR", deviceData);
        
        // 验证
        assertTrue(matchedRules.isEmpty());
    }

    @Test
    @DisplayName("测试阈值规则匹配 - 小于等于阈值")
    void testMatchRules_Threshold_LessThanOrEqual() {
        // 准备：创建阈值规则
        AlarmRuleDO rule = AlarmTestDataBuilder.buildAlarmRuleDO()
                .ruleType(AlarmRuleTypeEnum.THRESHOLD.getType())
                .deviceType("TEMPERATURE_SENSOR")
                .conditionExpression("{\"field\":\"temperature\",\"operator\":\"<=\",\"value\":0}")
                .enabled(true)
                .build();
        alarmRuleMapper.insert(rule);
        
        // 准备设备数据
        Map<String, Object> deviceData = new HashMap<>();
        deviceData.put("temperature", -5);
        
        // 执行
        List<AlarmRuleDO> matchedRules = alarmRuleService.matchRules("TEMPERATURE_SENSOR", deviceData);
        
        // 验证
        assertFalse(matchedRules.isEmpty());
    }

    @Test
    @DisplayName("测试变化率规则匹配")
    void testMatchRules_Rate() {
        // 准备：创建变化率规则
        AlarmRuleDO rule = AlarmTestDataBuilder.buildAlarmRuleDO()
                .ruleType(AlarmRuleTypeEnum.RATE.getType())
                .deviceType("TEMPERATURE_SENSOR")
                .conditionExpression("{\"field\":\"temperature\",\"operator\":\">\",\"value\":5}")
                .enabled(true)
                .build();
        alarmRuleMapper.insert(rule);
        
        // 准备设备数据和历史数据
        Map<String, Object> deviceData = new HashMap<>();
        deviceData.put("temperature", 35);
        
        Map<String, Object> historicalData = new HashMap<>();
        historicalData.put("temperature", 25);
        
        // 执行
        List<AlarmRuleDO> matchedRules = alarmRuleService.matchRulesWithHistory(
                "TEMPERATURE_SENSOR", deviceData, historicalData);
        
        // 验证（变化率 = |35-25| = 10 > 5）
        assertFalse(matchedRules.isEmpty());
    }

    @Test
    @DisplayName("测试模式规则匹配")
    void testMatchRules_Pattern() {
        // 准备：创建模式规则
        AlarmRuleDO rule = AlarmTestDataBuilder.buildAlarmRuleDO()
                .ruleType(AlarmRuleTypeEnum.PATTERN.getType())
                .deviceType("STATUS_MONITOR")
                .conditionExpression("{\"field\":\"status\",\"pattern\":\"ERROR|FAULT|ALARM\"}")
                .enabled(true)
                .build();
        alarmRuleMapper.insert(rule);
        
        // 准备设备数据
        Map<String, Object> deviceData = new HashMap<>();
        deviceData.put("status", "ERROR");
        
        // 执行
        List<AlarmRuleDO> matchedRules = alarmRuleService.matchRules("STATUS_MONITOR", deviceData);
        
        // 验证
        assertFalse(matchedRules.isEmpty());
    }

    @Test
    @DisplayName("测试组合条件规则匹配 - AND 逻辑")
    void testMatchRules_Combination_And() {
        // 准备：创建组合条件规则
        String conditionJson = "{\"logic\":\"AND\",\"conditions\":[" +
                "{\"field\":\"temperature\",\"operator\":\">\",\"value\":35}," +
                "{\"field\":\"humidity\",\"operator\":\">\",\"value\":80}" +
                "]}";
        
        AlarmRuleDO rule = AlarmTestDataBuilder.buildAlarmRuleDO()
                .ruleType(AlarmRuleTypeEnum.COMBINATION.getType())
                .deviceType("ENV_SENSOR")
                .conditionExpression(conditionJson)
                .enabled(true)
                .build();
        alarmRuleMapper.insert(rule);
        
        // 准备设备数据（两个条件都满足）
        Map<String, Object> deviceData = new HashMap<>();
        deviceData.put("temperature", 40);
        deviceData.put("humidity", 85);
        
        // 执行
        List<AlarmRuleDO> matchedRules = alarmRuleService.matchRules("ENV_SENSOR", deviceData);
        
        // 验证
        assertFalse(matchedRules.isEmpty());
    }

    @Test
    @DisplayName("测试组合条件规则匹配 - AND 逻辑部分不满足")
    void testMatchRules_Combination_And_PartialMatch() {
        // 准备：创建组合条件规则
        String conditionJson = "{\"logic\":\"AND\",\"conditions\":[" +
                "{\"field\":\"temperature\",\"operator\":\">\",\"value\":35}," +
                "{\"field\":\"humidity\",\"operator\":\">\",\"value\":80}" +
                "]}";
        
        AlarmRuleDO rule = AlarmTestDataBuilder.buildAlarmRuleDO()
                .ruleType(AlarmRuleTypeEnum.COMBINATION.getType())
                .deviceType("ENV_SENSOR")
                .conditionExpression(conditionJson)
                .enabled(true)
                .build();
        alarmRuleMapper.insert(rule);
        
        // 准备设备数据（只有一个条件满足）
        Map<String, Object> deviceData = new HashMap<>();
        deviceData.put("temperature", 40);
        deviceData.put("humidity", 70); // 不满足 > 80
        
        // 执行
        List<AlarmRuleDO> matchedRules = alarmRuleService.matchRules("ENV_SENSOR", deviceData);
        
        // 验证（AND 逻辑，部分不满足则不匹配）
        assertTrue(matchedRules.isEmpty());
    }

    @Test
    @DisplayName("测试组合条件规则匹配 - OR 逻辑")
    void testMatchRules_Combination_Or() {
        // 准备：创建组合条件规则
        String conditionJson = "{\"logic\":\"OR\",\"conditions\":[" +
                "{\"field\":\"temperature\",\"operator\":\">\",\"value\":35}," +
                "{\"field\":\"humidity\",\"operator\":\">\",\"value\":80}" +
                "]}";
        
        AlarmRuleDO rule = AlarmTestDataBuilder.buildAlarmRuleDO()
                .ruleType(AlarmRuleTypeEnum.COMBINATION.getType())
                .deviceType("ENV_SENSOR")
                .conditionExpression(conditionJson)
                .enabled(true)
                .build();
        alarmRuleMapper.insert(rule);
        
        // 准备设备数据（只有一个条件满足）
        Map<String, Object> deviceData = new HashMap<>();
        deviceData.put("temperature", 40);
        deviceData.put("humidity", 70);
        
        // 执行
        List<AlarmRuleDO> matchedRules = alarmRuleService.matchRules("ENV_SENSOR", deviceData);
        
        // 验证（OR 逻辑，任一满足即匹配）
        assertFalse(matchedRules.isEmpty());
    }

    @Test
    @DisplayName("测试禁用规则不匹配")
    void testMatchRules_DisabledRule() {
        // 准备：创建禁用的规则
        AlarmRuleDO rule = AlarmTestDataBuilder.buildAlarmRuleDO()
                .ruleType(AlarmRuleTypeEnum.THRESHOLD.getType())
                .deviceType("WATER_LEVEL_SENSOR")
                .conditionExpression("{\"field\":\"waterLevel\",\"operator\":\">\",\"value\":50}")
                .enabled(false) // 禁用
                .build();
        alarmRuleMapper.insert(rule);
        
        // 准备设备数据（超过阈值）
        Map<String, Object> deviceData = new HashMap<>();
        deviceData.put("waterLevel", 55);
        
        // 执行
        List<AlarmRuleDO> matchedRules = alarmRuleService.matchRules("WATER_LEVEL_SENSOR", deviceData);
        
        // 验证（禁用规则不匹配）
        assertTrue(matchedRules.isEmpty());
    }

    // ========== 条件表达式校验测试 ==========

    @Test
    @DisplayName("测试条件表达式校验 - 有效的阈值条件")
    void testValidateConditionExpression_ValidThreshold() {
        // 执行（不抛异常即为成功）
        assertDoesNotThrow(() -> alarmRuleService.validateConditionExpression(
                AlarmRuleTypeEnum.THRESHOLD.getType(),
                "{\"field\":\"waterLevel\",\"operator\":\">\",\"value\":50}"
        ));
    }

    @Test
    @DisplayName("测试条件表达式校验 - 缺少必要字段")
    void testValidateConditionExpression_MissingField() {
        // 执行并验证异常
        assertThrows(Exception.class, () -> alarmRuleService.validateConditionExpression(
                AlarmRuleTypeEnum.THRESHOLD.getType(),
                "{\"operator\":\">\",\"value\":50}" // 缺少 field
        ));
    }

    @Test
    @DisplayName("测试条件表达式校验 - 无效的操作符")
    void testValidateConditionExpression_InvalidOperator() {
        // 执行并验证异常
        assertThrows(Exception.class, () -> alarmRuleService.validateConditionExpression(
                AlarmRuleTypeEnum.THRESHOLD.getType(),
                "{\"field\":\"waterLevel\",\"operator\":\"INVALID\",\"value\":50}"
        ));
    }

    @Test
    @DisplayName("测试条件表达式校验 - 无效的 JSON 格式")
    void testValidateConditionExpression_InvalidJson() {
        // 执行并验证异常
        assertThrows(Exception.class, () -> alarmRuleService.validateConditionExpression(
                AlarmRuleTypeEnum.THRESHOLD.getType(),
                "invalid json"
        ));
    }

    @Test
    @DisplayName("测试条件表达式校验 - 有效的组合条件")
    void testValidateConditionExpression_ValidCombination() {
        String conditionJson = "{\"logic\":\"AND\",\"conditions\":[" +
                "{\"field\":\"temperature\",\"operator\":\">\",\"value\":35}," +
                "{\"field\":\"humidity\",\"operator\":\">\",\"value\":80}" +
                "]}";
        
        // 执行（不抛异常即为成功）
        assertDoesNotThrow(() -> alarmRuleService.validateConditionExpression(
                AlarmRuleTypeEnum.COMBINATION.getType(),
                conditionJson
        ));
    }

    @Test
    @DisplayName("测试条件表达式校验 - 组合条件缺少 logic")
    void testValidateConditionExpression_CombinationMissingLogic() {
        String conditionJson = "{\"conditions\":[" +
                "{\"field\":\"temperature\",\"operator\":\">\",\"value\":35}" +
                "]}";
        
        // 执行并验证异常
        assertThrows(Exception.class, () -> alarmRuleService.validateConditionExpression(
                AlarmRuleTypeEnum.COMBINATION.getType(),
                conditionJson
        ));
    }

    // ========== 规则 CRUD 测试 ==========

    @Test
    @DisplayName("测试创建告警规则")
    void testCreateAlarmRule() {
        // 准备测试数据
        AlarmRuleCreateReqVO reqVO = AlarmTestDataBuilder.buildAlarmRuleCreateReqVO();
        
        // 执行
        Long ruleId = alarmRuleService.createAlarmRule(reqVO);
        
        // 验证
        assertNotNull(ruleId);
        AlarmRuleDO savedRule = alarmRuleMapper.selectById(ruleId);
        assertNotNull(savedRule);
        assertEquals(reqVO.getRuleName(), savedRule.getRuleName());
        assertEquals(reqVO.getRuleCode(), savedRule.getRuleCode());
        assertTrue(savedRule.getEnabled());
    }

    @Test
    @DisplayName("测试更新告警规则状态")
    void testUpdateAlarmRuleStatus() {
        // 准备：创建规则
        AlarmRuleDO rule = AlarmTestDataBuilder.createDefaultAlarmRuleDO();
        rule.setEnabled(true);
        alarmRuleMapper.insert(rule);
        
        // 执行：禁用规则
        alarmRuleService.updateAlarmRuleStatus(rule.getId(), false);
        
        // 验证
        AlarmRuleDO updatedRule = alarmRuleMapper.selectById(rule.getId());
        assertFalse(updatedRule.getEnabled());
    }

    @Test
    @DisplayName("测试删除告警规则")
    void testDeleteAlarmRule() {
        // 准备：创建规则
        AlarmRuleDO rule = AlarmTestDataBuilder.createDefaultAlarmRuleDO();
        alarmRuleMapper.insert(rule);
        
        // 执行
        alarmRuleService.deleteAlarmRule(rule.getId());
        
        // 验证
        AlarmRuleDO deletedRule = alarmRuleMapper.selectById(rule.getId());
        assertNull(deletedRule);
    }

    // ========== 告警内容生成测试 ==========

    @Test
    @DisplayName("测试告警内容生成")
    void testGenerateAlarmContent() {
        // 准备规则
        AlarmRuleDO rule = AlarmTestDataBuilder.buildAlarmRuleDO()
                .alarmContentTemplate("水位超过阈值，当前值：${value}cm，阈值：${threshold}cm")
                .build();
        
        // 准备设备数据
        Map<String, Object> deviceData = new HashMap<>();
        deviceData.put("value", 55);
        deviceData.put("threshold", 50);
        
        // 执行
        String content = alarmRuleService.generateAlarmContent(rule, deviceData);
        
        // 验证
        assertEquals("水位超过阈值，当前值：55cm，阈值：50cm", content);
    }

}
