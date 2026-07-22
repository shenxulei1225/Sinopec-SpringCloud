package cn.iocoder.yudao.module.emergency.service.command;

import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandTemplateDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 应急指令阶段管理 Service 实现类
 */
@Service
@Slf4j
public class EmergencyCommandStageServiceImpl implements EmergencyCommandStageService {

    @Override
    public List<EmergencyCommandTemplateDO> getTemplatesByStage(String stage) {
        // 这里应该从数据库查询，暂时返回模拟数据
        // 实际实现中应该调用 commandTemplateService.getCommandTemplateListByCategory(null, stage)
        log.info("获取{}阶段的指令模板", stage);
        return new ArrayList<>();
    }

    @Override
    public List<EmergencyCommandTemplateDO> getRecommendedTemplates(String stage, String eventType) {
        log.info("获取{}阶段{}事件的推荐指令模板", stage, eventType);

        // 根据阶段和事件类型返回推荐模板
        // 这里应该基于业务规则进行智能推荐
        List<EmergencyCommandTemplateDO> recommendations = new ArrayList<>();

        switch (stage) {
            case "warning":
                // 预警阶段推荐疏散、监测等指令
                if ("fire".equals(eventType) || "explosion".equals(eventType)) {
                    // 添加消防预警指令
                }
                break;
            case "response_start":
                // 响应启动阶段推荐资源调配、专家支援等指令
                // 添加物资调配、专家支援指令
                break;
            case "disposal":
                // 处置阶段推荐现场指挥、技术处置等指令
                if ("fire".equals(eventType)) {
                    // 添加消防处置指令
                }
                break;
            case "recovery":
                // 恢复阶段推荐善后处理、评估总结等指令
                break;
        }

        return recommendations;
    }

    @Override
    public boolean validateStageCommand(String stage, Long eventId) {
        // 验证指令是否可以在当前阶段发起
        // 这里应该检查事件的当前状态和阶段匹配性
        log.info("验证事件{}是否可以在{}阶段下发指令", eventId, stage);

        // 基本验证逻辑：确保阶段有效
        List<String> validStages = Arrays.asList("warning", "response_start", "disposal", "recovery");
        return validStages.contains(stage);
    }

    @Override
    public List<EmergencyCommandTemplateDO> getNextStageTemplates(String currentStage, Long eventId) {
        log.info("获取事件{}从{}阶段转换时的下一阶段指令模板", eventId, currentStage);

        String nextStage = getNextStage(currentStage);
        if (nextStage != null) {
            return getTemplatesByStage(nextStage);
        }

        return new ArrayList<>();
    }

    @Override
    public List<EmergencyCommandTemplateDO> getStageTransitionReminders(String fromStage, String toStage, Long eventId) {
        log.info("获取从{}阶段转换到{}阶段时的事件{}指令提醒", fromStage, toStage, eventId);

        // 根据阶段转换提供相应的指令提醒
        List<EmergencyCommandTemplateDO> reminders = new ArrayList<>();

        if ("warning".equals(fromStage) && "response_start".equals(toStage)) {
            // 从预警到响应启动：提醒启动正式处置指令
            // 添加处置类指令模板
        } else if ("response_start".equals(fromStage) && "disposal".equals(toStage)) {
            // 从响应启动到处置：提醒现场指挥指令
            // 添加现场处置指令模板
        } else if ("disposal".equals(fromStage) && "recovery".equals(toStage)) {
            // 从处置到恢复：提醒善后处理指令
            // 添加恢复阶段指令模板
        }

        return reminders;
    }

    /**
     * 获取下一阶段
     */
    private String getNextStage(String currentStage) {
        switch (currentStage) {
            case "warning":
                return "response_start";
            case "response_start":
                return "disposal";
            case "disposal":
                return "recovery";
            default:
                return null;
        }
    }
}
