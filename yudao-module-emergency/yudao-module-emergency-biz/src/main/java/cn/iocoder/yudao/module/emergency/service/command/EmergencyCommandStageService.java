package cn.iocoder.yudao.module.emergency.service.command;

import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandTemplateDO;

import java.util.List;

/**
 * 应急指令阶段管理 Service 接口
 */
public interface EmergencyCommandStageService {

    /**
     * 获取指定阶段的可用指令模板
     *
     * @param stage 阶段
     * @return 指令模板列表
     */
    List<EmergencyCommandTemplateDO> getTemplatesByStage(String stage);

    /**
     * 获取指定阶段的推荐指令模板
     *
     * @param stage 阶段
     * @param eventType 事件类型
     * @return 推荐的指令模板列表
     */
    List<EmergencyCommandTemplateDO> getRecommendedTemplates(String stage, String eventType);

    /**
     * 验证指令是否可以在当前阶段发起
     *
     * @param stage 阶段
     * @param eventId 事件ID
     * @return 是否允许
     */
    boolean validateStageCommand(String stage, Long eventId);

    /**
     * 获取下一阶段的推荐指令
     *
     * @param currentStage 当前阶段
     * @param eventId 事件ID
     * @return 下一阶段的指令模板列表
     */
    List<EmergencyCommandTemplateDO> getNextStageTemplates(String currentStage, Long eventId);

    /**
     * 阶段转换时自动提醒指令下发
     *
     * @param fromStage 原阶段
     * @param toStage 新阶段
     * @param eventId 事件ID
     * @return 需要提醒的指令模板列表
     */
    List<EmergencyCommandTemplateDO> getStageTransitionReminders(String fromStage, String toStage, Long eventId);
}
