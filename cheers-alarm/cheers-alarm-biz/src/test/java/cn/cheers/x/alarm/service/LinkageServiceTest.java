package cn.cheers.x.alarm.service;

import cn.cheers.x.framework.test.core.ut.BaseDbUnitTest;
import cn.cheers.x.alarm.config.AlarmTestConfiguration;
import cn.cheers.x.alarm.config.AlarmTestDataBuilder;
import cn.cheers.x.alarm.controller.admin.vo.linkage.*;
import cn.cheers.x.alarm.dal.dataobject.AlarmDO;
import cn.cheers.x.alarm.dal.dataobject.LinkageExecutionDO;
import cn.cheers.x.alarm.dal.dataobject.LinkageRuleDO;
import cn.cheers.x.alarm.dal.mysql.AlarmMapper;
import cn.cheers.x.alarm.dal.mysql.LinkageExecutionMapper;
import cn.cheers.x.alarm.dal.mysql.LinkageRuleMapper;
import cn.cheers.x.alarm.enums.*;
import cn.cheers.x.alarm.service.audit.AlarmAuditService;
import cn.cheers.x.alarm.service.linkage.LinkageServiceImpl;
import cn.cheers.x.alarm.service.linkage.executor.LinkageActionExecutor;
import cn.cheers.x.alarm.service.websocket.AlarmWebSocketService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LinkageService 单元测试
 * 
 * <p>测试联动执行、重试机制等核心逻辑</p>
 * 
 * <p>覆盖需求：FR-011（联动控制）</p>
 * <p>覆盖业务规则：BR-BIZ-004（消防优先）、BR-BIZ-005（重试机制）、BR-BIZ-006（人工介入）</p>
 *
 * @author 告警管理模块
 */
@Import({AlarmTestConfiguration.class, LinkageServiceImpl.class})
@DisplayName("LinkageService 单元测试")
public class LinkageServiceTest extends BaseDbUnitTest {

    @Resource
    private LinkageServiceImpl linkageService;

    @Resource
    private LinkageRuleMapper linkageRuleMapper;

    @Resource
    private LinkageExecutionMapper linkageExecutionMapper;

    @Resource
    private AlarmMapper alarmMapper;

    @MockitoBean
    private AlarmAuditService alarmAuditService;

    @MockitoBean
    private AlarmWebSocketService alarmWebSocketService;

    @MockitoBean
    private List<LinkageActionExecutor> executors;

    // ========== 联动规则 CRUD 测试 ==========

    @Test
    @DisplayName("测试创建联动规则")
    void testCreateLinkageRule() {
        // 准备测试数据
        LinkageRuleCreateReqVO reqVO = AlarmTestDataBuilder.buildLinkageRuleCreateReqVO();
        
        // 执行
        Long ruleId = linkageService.createLinkageRule(reqVO);
        
        // 验证
        assertNotNull(ruleId);
        LinkageRuleDO savedRule = linkageRuleMapper.selectById(ruleId);
        assertNotNull(savedRule);
        assertEquals(reqVO.getRuleName(), savedRule.getRuleName());
        assertEquals(reqVO.getRuleCode(), savedRule.getRuleCode());
        assertTrue(savedRule.getEnabled());
    }

    @Test
    @DisplayName("测试更新联动规则状态")
    void testToggleLinkageRuleStatus() {
        // 准备：创建规则
        LinkageRuleDO rule = AlarmTestDataBuilder.createDefaultLinkageRuleDO();
        rule.setEnabled(true);
        linkageRuleMapper.insert(rule);
        
        // 执行：禁用规则
        linkageService.toggleLinkageRuleStatus(rule.getId(), false);
        
        // 验证
        LinkageRuleDO updatedRule = linkageRuleMapper.selectById(rule.getId());
        assertFalse(updatedRule.getEnabled());
    }

    @Test
    @DisplayName("测试删除联动规则")
    void testDeleteLinkageRule() {
        // 准备：创建规则
        LinkageRuleDO rule = AlarmTestDataBuilder.createDefaultLinkageRuleDO();
        linkageRuleMapper.insert(rule);
        
        // 执行
        linkageService.deleteLinkageRule(rule.getId());
        
        // 验证
        LinkageRuleDO deletedRule = linkageRuleMapper.selectById(rule.getId());
        assertNull(deletedRule);
    }

    // ========== 联动规则匹配测试 ==========

    @Test
    @DisplayName("测试联动规则匹配 - 按告警类型匹配")
    void testMatchLinkageRules_ByAlarmType() {
        // 准备：创建联动规则
        LinkageRuleDO rule = AlarmTestDataBuilder.buildLinkageRuleDO()
                .alarmTypeId(1L)
                .alarmCategoryId(null)
                .alarmLevel(null)
                .enabled(true)
                .build();
        linkageRuleMapper.insert(rule);
        
        // 准备告警
        AlarmDO alarm = AlarmTestDataBuilder.buildAlarmDO()
                .alarmTypeId(1L)
                .build();
        
        // 执行
        List<LinkageRuleDO> matchedRules = linkageService.matchLinkageRules(alarm);
        
        // 验证
        assertFalse(matchedRules.isEmpty());
    }

    @Test
    @DisplayName("测试联动规则匹配 - 按告警级别匹配")
    void testMatchLinkageRules_ByAlarmLevel() {
        // 准备：创建联动规则
        LinkageRuleDO rule = AlarmTestDataBuilder.buildLinkageRuleDO()
                .alarmTypeId(null)
                .alarmCategoryId(null)
                .alarmLevel(AlarmLevelEnum.CRITICAL.getLevel())
                .enabled(true)
                .build();
        linkageRuleMapper.insert(rule);
        
        // 准备告警
        AlarmDO alarm = AlarmTestDataBuilder.buildAlarmDO()
                .alarmLevel(AlarmLevelEnum.CRITICAL.getLevel())
                .build();
        
        // 执行
        List<LinkageRuleDO> matchedRules = linkageService.matchLinkageRules(alarm);
        
        // 验证
        assertFalse(matchedRules.isEmpty());
    }

    @Test
    @DisplayName("测试联动规则匹配 - 禁用规则不匹配")
    void testMatchLinkageRules_DisabledRule() {
        // 准备：创建禁用的联动规则
        LinkageRuleDO rule = AlarmTestDataBuilder.buildLinkageRuleDO()
                .alarmTypeId(1L)
                .enabled(false)
                .build();
        linkageRuleMapper.insert(rule);
        
        // 准备告警
        AlarmDO alarm = AlarmTestDataBuilder.buildAlarmDO()
                .alarmTypeId(1L)
                .build();
        
        // 执行
        List<LinkageRuleDO> matchedRules = linkageService.matchLinkageRules(alarm);
        
        // 验证（禁用规则不匹配）
        assertTrue(matchedRules.isEmpty());
    }

    // ========== 联动执行记录查询测试 ==========

    @Test
    @DisplayName("测试查询告警的联动执行记录")
    void testGetLinkageExecutionsByAlarmId() {
        // 准备：创建告警
        AlarmDO alarm = AlarmTestDataBuilder.createPendingAlarmDO();
        alarmMapper.insert(alarm);
        
        // 准备：创建联动执行记录
        LinkageExecutionDO execution1 = AlarmTestDataBuilder.buildLinkageExecutionDO()
                .alarmId(alarm.getId())
                .actionType(LinkageActionTypeEnum.NOTIFICATION.getType())
                .executionStatus(LinkageExecutionStatusEnum.SUCCESS.getStatus())
                .build();
        linkageExecutionMapper.insert(execution1);
        
        LinkageExecutionDO execution2 = AlarmTestDataBuilder.buildLinkageExecutionDO()
                .alarmId(alarm.getId())
                .actionType(LinkageActionTypeEnum.VIDEO_LINKAGE.getType())
                .executionStatus(LinkageExecutionStatusEnum.SUCCESS.getStatus())
                .build();
        linkageExecutionMapper.insert(execution2);
        
        // 执行
        List<LinkageExecutionDO> executions = linkageService.getLinkageExecutionsByAlarmId(alarm.getId());
        
        // 验证
        assertEquals(2, executions.size());
    }

    @Test
    @DisplayName("测试查询需要人工介入的执行记录")
    void testGetManualInterventionExecutions() {
        // 准备：创建需要人工介入的执行记录
        LinkageExecutionDO execution = AlarmTestDataBuilder.buildLinkageExecutionDO()
                .executionStatus(LinkageExecutionStatusEnum.FAILED.getStatus())
                .retryCount(3)
                .manualIntervention(true)
                .build();
        linkageExecutionMapper.insert(execution);
        
        // 执行
        List<LinkageExecutionDO> executions = linkageService.getManualInterventionExecutions();
        
        // 验证
        assertFalse(executions.isEmpty());
        assertTrue(executions.get(0).getManualIntervention());
    }

    // ========== 联动执行重试测试 ==========

    @Test
    @DisplayName("测试联动执行重试 - 达到最大重试次数")
    void testRetryLinkageExecution_MaxRetryReached() {
        // 准备：创建告警
        AlarmDO alarm = AlarmTestDataBuilder.createPendingAlarmDO();
        alarmMapper.insert(alarm);
        
        // 准备：创建已达到最大重试次数的执行记录
        LinkageExecutionDO execution = AlarmTestDataBuilder.buildLinkageExecutionDO()
                .alarmId(alarm.getId())
                .executionStatus(LinkageExecutionStatusEnum.FAILED.getStatus())
                .retryCount(3) // 已达到最大重试次数
                .manualIntervention(false)
                .build();
        linkageExecutionMapper.insert(execution);
        
        // 执行并验证异常
        assertThrows(Exception.class, () -> linkageService.retryLinkageExecution(execution.getId()));
    }

    @Test
    @DisplayName("测试联动执行重试 - 状态不允许重试")
    void testRetryLinkageExecution_InvalidStatus() {
        // 准备：创建告警
        AlarmDO alarm = AlarmTestDataBuilder.createPendingAlarmDO();
        alarmMapper.insert(alarm);
        
        // 准备：创建成功状态的执行记录（不能重试）
        LinkageExecutionDO execution = AlarmTestDataBuilder.buildLinkageExecutionDO()
                .alarmId(alarm.getId())
                .executionStatus(LinkageExecutionStatusEnum.SUCCESS.getStatus())
                .retryCount(0)
                .build();
        linkageExecutionMapper.insert(execution);
        
        // 执行并验证异常
        assertThrows(Exception.class, () -> linkageService.retryLinkageExecution(execution.getId()));
    }

    // ========== 联动规则测试功能测试 ==========

    @Test
    @DisplayName("测试联动规则测试 - 空动作配置")
    void testTestLinkageRule_EmptyActions() {
        // 准备测试请求
        LinkageRuleTestReqVO reqVO = new LinkageRuleTestReqVO();
        reqVO.setActions("[]");
        reqVO.setDryRun(true);
        
        // 执行
        LinkageRuleTestRespVO result = linkageService.testLinkageRule(reqVO);
        
        // 验证
        assertFalse(result.getSuccess());
        assertNotNull(result.getErrorMessage());
    }

    // ========== 联动执行状态测试 ==========

    @Test
    @DisplayName("测试联动执行状态 - 成功状态")
    void testLinkageExecutionStatus_Success() {
        // 准备：创建成功的执行记录
        LinkageExecutionDO execution = AlarmTestDataBuilder.createSuccessLinkageExecutionDO();
        linkageExecutionMapper.insert(execution);
        
        // 验证
        LinkageExecutionDO savedExecution = linkageExecutionMapper.selectById(execution.getId());
        assertEquals(LinkageExecutionStatusEnum.SUCCESS.getStatus(), savedExecution.getExecutionStatus());
        assertFalse(savedExecution.getManualIntervention());
    }

    @Test
    @DisplayName("测试联动执行状态 - 失败需要人工介入")
    void testLinkageExecutionStatus_FailedManualIntervention() {
        // 准备：创建失败需要人工介入的执行记录
        LinkageExecutionDO execution = AlarmTestDataBuilder.createFailedLinkageExecutionDO();
        linkageExecutionMapper.insert(execution);
        
        // 验证
        LinkageExecutionDO savedExecution = linkageExecutionMapper.selectById(execution.getId());
        assertEquals(LinkageExecutionStatusEnum.FAILED.getStatus(), savedExecution.getExecutionStatus());
        assertTrue(savedExecution.getManualIntervention());
        assertEquals(3, savedExecution.getRetryCount());
    }

    // ========== 联动规则获取测试 ==========

    @Test
    @DisplayName("测试获取联动规则")
    void testGetLinkageRule() {
        // 准备：创建规则
        LinkageRuleDO rule = AlarmTestDataBuilder.createDefaultLinkageRuleDO();
        linkageRuleMapper.insert(rule);
        
        // 执行
        LinkageRuleDO result = linkageService.getLinkageRule(rule.getId());
        
        // 验证
        assertNotNull(result);
        assertEquals(rule.getId(), result.getId());
        assertEquals(rule.getRuleName(), result.getRuleName());
    }

    @Test
    @DisplayName("测试获取启用的联动规则列表")
    void testGetEnabledLinkageRules() {
        // 准备：创建启用和禁用的规则
        LinkageRuleDO enabledRule = AlarmTestDataBuilder.buildLinkageRuleDO()
                .enabled(true)
                .build();
        linkageRuleMapper.insert(enabledRule);
        
        LinkageRuleDO disabledRule = AlarmTestDataBuilder.buildLinkageRuleDO()
                .enabled(false)
                .build();
        linkageRuleMapper.insert(disabledRule);
        
        // 执行
        List<LinkageRuleDO> enabledRules = linkageService.getEnabledLinkageRules();
        
        // 验证（只返回启用的规则）
        assertFalse(enabledRules.isEmpty());
        assertTrue(enabledRules.stream().allMatch(LinkageRuleDO::getEnabled));
    }

    // ========== 联动执行记录获取测试 ==========

    @Test
    @DisplayName("测试获取联动执行记录")
    void testGetLinkageExecution() {
        // 准备：创建执行记录
        LinkageExecutionDO execution = AlarmTestDataBuilder.createDefaultLinkageExecutionDO();
        linkageExecutionMapper.insert(execution);
        
        // 执行
        LinkageExecutionDO result = linkageService.getLinkageExecution(execution.getId());
        
        // 验证
        assertNotNull(result);
        assertEquals(execution.getId(), result.getId());
    }

}
