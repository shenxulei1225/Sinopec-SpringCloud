package cn.iocoder.yudao.module.emergency.service.command;

import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandTemplateDO;

import java.util.List;

/**
 * 应急预案指令集成 Service 接口
 */
public interface EmergencyPlanCommandIntegrationService {

    /**
     * 获取预案步骤关联的指令模板
     *
     * @param planId 预案ID
     * @param stepId 步骤ID
     * @return 指令模板列表
     */
    List<EmergencyCommandTemplateDO> getStepCommandTemplates(Long planId, Long stepId);

    /**
     * 为预案步骤关联指令模板
     *
     * @param planId 预案ID
     * @param stepId 步骤ID
     * @param templateId 指令模板ID
     * @param triggerCondition 触发条件
     * @param isAutoTrigger 是否自动触发
     * @param triggerOrder 触发顺序
     */
    void associateStepCommand(Long planId, Long stepId, Long templateId,
                            String triggerCondition, Boolean isAutoTrigger, Integer triggerOrder);

    /**
     * 移除预案步骤的指令关联
     *
     * @param planId 预案ID
     * @param stepId 步骤ID
     * @param templateId 指令模板ID
     */
    void removeStepCommandAssociation(Long planId, Long stepId, Long templateId);

    /**
     * 预案执行时自动触发指令
     *
     * @param planId 预案ID
     * @param stepId 步骤ID
     * @param eventId 事件ID
     * @param responseId 响应ID
     * @return 自动创建的指令列表
     */
    List<EmergencyCommandDO> autoTriggerStepCommands(Long planId, Long stepId, Long eventId, Long responseId);

    /**
     * 检查预案步骤是否满足指令触发条件
     *
     * @param planId 预案ID
     * @param stepId 步骤ID
     * @param context 执行上下文
     * @return 是否满足触发条件
     */
    boolean checkTriggerCondition(Long planId, Long stepId, Object context);

    /**
     * 获取预案的指令执行概况
     *
     * @param planId 预案ID
     * @param responseId 响应ID
     * @return 指令执行统计信息
     */
    Object getPlanCommandExecutionSummary(Long planId, Long responseId);

    /**
     * 同步指令执行状态到预案进度
     *
     * @param commandId 指令ID
     * @param status 新状态
     */
    void syncCommandStatusToPlan(Long commandId, String status);
}
