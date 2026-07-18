package cn.iocoder.yudao.module.alarm.service.linkage;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.linkage.*;
import cn.iocoder.yudao.module.alarm.dal.dataobject.AlarmDO;
import cn.iocoder.yudao.module.alarm.dal.dataobject.LinkageExecutionDO;
import cn.iocoder.yudao.module.alarm.dal.dataobject.LinkageRuleDO;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 联动服务接口
 * 
 * <p>负责联动规则的配置管理和联动动作的执行</p>
 * 
 * <p>核心功能：</p>
 * <ul>
 *   <li>联动规则 CRUD 操作</li>
 *   <li>联动动作自动执行</li>
 *   <li>联动执行重试机制</li>
 *   <li>联动执行记录查询</li>
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
public interface LinkageService {

    // ========== 联动规则 CRUD ==========

    /**
     * 创建联动规则
     *
     * @param createReqVO 创建请求
     * @return 联动规则ID
     */
    Long createLinkageRule(@Valid LinkageRuleCreateReqVO createReqVO);

    /**
     * 更新联动规则
     *
     * @param updateReqVO 更新请求
     */
    void updateLinkageRule(@Valid LinkageRuleUpdateReqVO updateReqVO);

    /**
     * 删除联动规则
     *
     * @param id 联动规则ID
     */
    void deleteLinkageRule(Long id);

    /**
     * 获取联动规则
     *
     * @param id 联动规则ID
     * @return 联动规则
     */
    LinkageRuleDO getLinkageRule(Long id);

    /**
     * 获取联动规则分页
     *
     * @param pageReqVO 分页查询条件
     * @return 联动规则分页结果
     */
    PageResult<LinkageRuleDO> getLinkageRulePage(LinkageRulePageReqVO pageReqVO);

    /**
     * 获取所有启用的联动规则
     *
     * @return 启用的联动规则列表
     */
    List<LinkageRuleDO> getEnabledLinkageRules();

    /**
     * 启用/禁用联动规则
     *
     * @param id      联动规则ID
     * @param enabled 是否启用
     */
    void toggleLinkageRuleStatus(Long id, Boolean enabled);

    // ========== 联动规则匹配 ==========

    /**
     * 根据告警匹配联动规则
     * 
     * <p>匹配逻辑：</p>
     * <ul>
     *   <li>告警规则ID匹配（为空表示适用所有）</li>
     *   <li>告警类型ID匹配（为空表示适用所有）</li>
     *   <li>告警分类ID匹配（为空表示适用所有）</li>
     *   <li>告警级别匹配（为空表示适用所有）</li>
     * </ul>
     *
     * @param alarm 告警信息
     * @return 匹配的联动规则列表（按优先级排序）
     */
    List<LinkageRuleDO> matchLinkageRules(AlarmDO alarm);

    // ========== 联动执行 ==========

    /**
     * 执行告警联动
     * 
     * <p>根据告警信息匹配联动规则并执行联动动作</p>
     * 
     * <p>执行流程：</p>
     * <ol>
     *   <li>匹配联动规则</li>
     *   <li>解析联动动作配置</li>
     *   <li>按优先级排序（消防控制优先）</li>
     *   <li>根据执行模式（串行/并行）执行动作</li>
     *   <li>记录执行结果</li>
     *   <li>失败时触发重试机制</li>
     * </ol>
     *
     * @param alarm 告警信息
     * @return 联动执行记录列表
     */
    List<LinkageExecutionDO> executeLinkage(AlarmDO alarm);

    /**
     * 执行指定联动规则
     *
     * @param alarmId       告警ID
     * @param linkageRuleId 联动规则ID
     * @return 联动执行记录列表
     */
    List<LinkageExecutionDO> executeLinkageRule(Long alarmId, Long linkageRuleId);

    /**
     * 手动执行联动动作
     * 
     * <p>用于值班员手动触发联动控制</p>
     *
     * @param alarmId      告警ID
     * @param actionType   动作类型
     * @param actionConfig 动作配置（JSON格式）
     * @return 联动执行记录
     */
    LinkageExecutionDO manualExecuteLinkage(Long alarmId, String actionType, String actionConfig);

    /**
     * 重试失败的联动执行
     * 
     * <p>业务规则：</p>
     * <ul>
     *   <li>最多重试3次</li>
     *   <li>每次重试间隔5秒</li>
     *   <li>3次重试后仍失败，标记需要人工介入</li>
     * </ul>
     *
     * @param executionId 联动执行记录ID
     * @return 重试后的执行记录
     */
    LinkageExecutionDO retryLinkageExecution(Long executionId);

    // ========== 联动执行记录查询 ==========

    /**
     * 获取联动执行记录
     *
     * @param id 执行记录ID
     * @return 联动执行记录
     */
    LinkageExecutionDO getLinkageExecution(Long id);

    /**
     * 获取联动执行记录分页
     *
     * @param pageReqVO 分页查询条件
     * @return 联动执行记录分页结果
     */
    PageResult<LinkageExecutionDO> getLinkageExecutionPage(LinkageExecutionPageReqVO pageReqVO);

    /**
     * 根据告警ID获取联动执行记录列表
     *
     * @param alarmId 告警ID
     * @return 联动执行记录列表
     */
    List<LinkageExecutionDO> getLinkageExecutionsByAlarmId(Long alarmId);

    /**
     * 获取需要人工介入的联动执行记录
     *
     * @return 需要人工介入的执行记录列表
     */
    List<LinkageExecutionDO> getManualInterventionExecutions();

    // ========== 联动规则测试 ==========

    /**
     * 测试联动规则
     * 
     * <p>用于验证联动规则配置的有效性</p>
     *
     * @param testReqVO 测试请求
     * @return 测试结果
     */
    LinkageRuleTestRespVO testLinkageRule(@Valid LinkageRuleTestReqVO testReqVO);

}
