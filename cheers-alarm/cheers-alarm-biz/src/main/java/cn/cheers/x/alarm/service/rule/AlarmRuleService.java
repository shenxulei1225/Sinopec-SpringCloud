package cn.cheers.x.alarm.service.rule;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.alarm.controller.admin.vo.rule.*;
import cn.cheers.x.alarm.dal.dataobject.AlarmRuleDO;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 告警规则 Service 接口
 *
 * <p>负责告警规则的 CRUD 操作、规则匹配和规则测试功能</p>
 * <p>支持四种规则类型：阈值告警、变化率告警、异常模式告警、组合条件告警</p>
 *
 * @author 告警管理模块
 */
public interface AlarmRuleService {

    // ========== 规则 CRUD 操作 ==========

    /**
     * 创建告警规则
     *
     * @param createReqVO 创建请求
     * @return 规则ID
     */
    Long createAlarmRule(@Valid AlarmRuleCreateReqVO createReqVO);

    /**
     * 更新告警规则
     *
     * @param updateReqVO 更新请求
     */
    void updateAlarmRule(@Valid AlarmRuleUpdateReqVO updateReqVO);

    /**
     * 删除告警规则
     *
     * @param id 规则ID
     */
    void deleteAlarmRule(Long id);

    /**
     * 获取告警规则
     *
     * @param id 规则ID
     * @return 告警规则
     */
    AlarmRuleDO getAlarmRule(Long id);

    /**
     * 获取告警规则分页列表
     *
     * @param pageReqVO 分页查询条件
     * @return 告警规则分页结果
     */
    PageResult<AlarmRuleDO> getAlarmRulePage(AlarmRulePageReqVO pageReqVO);

    /**
     * 获取所有启用的告警规则列表
     *
     * @return 启用的告警规则列表
     */
    List<AlarmRuleDO> getEnabledAlarmRuleList();

    /**
     * 根据设备类型获取启用的告警规则列表
     *
     * @param deviceType 设备类型
     * @return 告警规则列表
     */
    List<AlarmRuleDO> getEnabledAlarmRuleListByDeviceType(String deviceType);

    /**
     * 根据告警类型ID获取启用的告警规则列表
     *
     * @param alarmTypeId 告警类型ID
     * @return 告警规则列表
     */
    List<AlarmRuleDO> getEnabledAlarmRuleListByAlarmTypeId(Long alarmTypeId);

    // ========== 规则状态管理 ==========

    /**
     * 启用/禁用告警规则
     *
     * @param id 规则ID
     * @param enabled 是否启用
     */
    void updateAlarmRuleStatus(Long id, Boolean enabled);

    // ========== 规则匹配 ==========

    /**
     * 根据设备数据匹配告警规则
     *
     * <p>根据设备类型和数据内容，匹配所有符合条件的告警规则</p>
     * <p>支持四种规则类型的匹配：</p>
     * <ul>
     *   <li>THRESHOLD - 阈值告警：数据值超过预设阈值</li>
     *   <li>RATE - 变化率告警：数据变化率超过预设范围</li>
     *   <li>PATTERN - 异常模式告警：数据符合预设的异常模式</li>
     *   <li>COMBINATION - 组合条件告警：多个条件组合满足</li>
     * </ul>
     *
     * @param deviceType 设备类型
     * @param deviceData 设备数据（JSON格式的键值对）
     * @return 匹配的告警规则列表
     */
    List<AlarmRuleDO> matchRules(String deviceType, Map<String, Object> deviceData);

    /**
     * 根据设备数据匹配告警规则（带历史数据用于变化率计算）
     *
     * @param deviceType 设备类型
     * @param deviceData 当前设备数据
     * @param historicalData 历史设备数据（用于变化率计算）
     * @return 匹配的告警规则列表
     */
    List<AlarmRuleDO> matchRulesWithHistory(String deviceType, Map<String, Object> deviceData, 
                                             Map<String, Object> historicalData);

    /**
     * 评估单个规则是否匹配
     *
     * @param rule 告警规则
     * @param deviceData 设备数据
     * @return 是否匹配
     */
    boolean evaluateRule(AlarmRuleDO rule, Map<String, Object> deviceData);

    /**
     * 评估单个规则是否匹配（带历史数据）
     *
     * @param rule 告警规则
     * @param deviceData 当前设备数据
     * @param historicalData 历史设备数据
     * @return 是否匹配
     */
    boolean evaluateRuleWithHistory(AlarmRuleDO rule, Map<String, Object> deviceData, 
                                    Map<String, Object> historicalData);

    // ========== 规则测试 ==========

    /**
     * 测试告警规则
     *
     * <p>使用提供的测试数据验证规则的有效性和匹配结果</p>
     *
     * @param testReqVO 测试请求
     * @return 测试结果
     */
    AlarmRuleTestRespVO testAlarmRule(@Valid AlarmRuleTestReqVO testReqVO);

    // ========== 告警内容生成 ==========

    /**
     * 根据规则模板和设备数据生成告警内容
     *
     * @param rule 告警规则
     * @param deviceData 设备数据
     * @return 生成的告警内容
     */
    String generateAlarmContent(AlarmRuleDO rule, Map<String, Object> deviceData);

    // ========== 校验方法 ==========

    /**
     * 校验告警规则是否存在
     *
     * @param id 规则ID
     * @return 告警规则
     */
    AlarmRuleDO validateAlarmRuleExists(Long id);

    /**
     * 校验条件表达式是否有效
     *
     * @param ruleType 规则类型
     * @param conditionExpression 条件表达式
     */
    void validateConditionExpression(String ruleType, String conditionExpression);

}
