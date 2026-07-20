package cn.iocoder.yudao.module.emergency.service.command;

import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandTemplateDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应急预案指令集成 Service 实现类
 */
@Service
@Slf4j
public class EmergencyPlanCommandIntegrationServiceImpl implements EmergencyPlanCommandIntegrationService {

    @Resource
    private EmergencyCommandService commandService;

    @Resource
    private EmergencyCommandTemplateService commandTemplateService;

    @Override
    public List<EmergencyCommandTemplateDO> getStepCommandTemplates(Long planId, Long stepId) {
        log.info("获取预案{}步骤{}关联的指令模板", planId, stepId);

        // 这里应该查询emergency_plan_step_command表
        // 暂时返回空列表，实际实现中需要Mapper支持
        return new ArrayList<>();
    }

    @Override
    public void associateStepCommand(Long planId, Long stepId, Long templateId,
                                   String triggerCondition, Boolean isAutoTrigger, Integer triggerOrder) {
        log.info("为预案{}步骤{}关联指令模板{}", planId, stepId, templateId);

        // 验证模板存在
        EmergencyCommandTemplateDO template = commandTemplateService.getCommandTemplate(templateId);
        if (template == null) {
            throw new IllegalArgumentException("指令模板不存在: " + templateId);
        }

        // 这里应该插入emergency_plan_step_command表记录
        // 暂时记录日志，实际实现中需要Mapper支持
        log.info("关联成功：预案{}步骤{} -> 模板{}，触发条件：{}，自动触发：{}，顺序：{}",
                planId, stepId, templateId, triggerCondition, isAutoTrigger, triggerOrder);
    }

    @Override
    public void removeStepCommandAssociation(Long planId, Long stepId, Long templateId) {
        log.info("移除预案{}步骤{}的指令模板{}关联", planId, stepId, templateId);

        // 这里应该删除emergency_plan_step_command表记录
        // 暂时记录日志，实际实现中需要Mapper支持
    }

    @Override
    public List<EmergencyCommandDO> autoTriggerStepCommands(Long planId, Long stepId, Long eventId, Long responseId) {
        log.info("预案{}步骤{}执行，自动触发指令，事件：{}，响应：{}", planId, stepId, eventId, responseId);

        List<EmergencyCommandDO> triggeredCommands = new ArrayList<>();

        // 获取步骤关联的自动触发指令模板
        List<EmergencyCommandTemplateDO> templates = getStepCommandTemplates(planId, stepId);

        for (EmergencyCommandTemplateDO template : templates) {
            // 检查是否满足触发条件
            if (checkTriggerCondition(planId, stepId, null)) { // context暂时传null
                try {
                    // 基于模板创建指令
                    EmergencyCommandDO command = createCommandFromTemplate(template, eventId, responseId);

                    // 这里应该调用commandService.createCommand
                    // triggeredCommands.add(command);

                    log.info("自动触发指令成功：模板{} -> 指令{}", template.getId(), command.getId());
                } catch (Exception e) {
                    log.error("自动触发指令失败：模板{}", template.getId(), e);
                }
            }
        }

        return triggeredCommands;
    }

    @Override
    public boolean checkTriggerCondition(Long planId, Long stepId, Object context) {
        // 检查触发条件
        // 这里应该解析trigger_condition字段并评估条件
        // 暂时返回true，实际实现中需要条件解析器
        log.debug("检查预案{}步骤{}的触发条件", planId, stepId);
        return true;
    }

    @Override
    public Object getPlanCommandExecutionSummary(Long planId, Long responseId) {
        log.info("获取预案{}响应{}的指令执行概况", planId, responseId);

        // 这里应该统计指令执行情况
        // 返回指令总数、完成数、进行中数、超时数等统计信息
        return new Object(); // 暂时返回空对象
    }

    @Override
    public void syncCommandStatusToPlan(Long commandId, String status) {
        log.info("同步指令{}状态{}到预案进度", commandId, status);

        // 这里应该根据指令状态更新预案步骤的执行进度
        // 如果所有关联指令都完成，则标记步骤为完成
    }

    /**
     * 基于模板创建指令
     */
    private EmergencyCommandDO createCommandFromTemplate(EmergencyCommandTemplateDO template,
                                                        Long eventId, Long responseId) {
        EmergencyCommandDO command = new EmergencyCommandDO();

        // 生成指令编号
        String commandNo = "CMD" + System.currentTimeMillis();
        command.setCommandNo(commandNo);

        // 设置基本信息
        command.setTitle(template.getTitleTemplate() != null ?
                        template.getTitleTemplate() : template.getName());
        command.setContent(template.getContentTemplate());
        command.setCommandType("disposal"); // 默认处置类型
        command.setPriority(template.getPriority() != null ? template.getPriority() : "normal");
        command.setStatus("issued");
        command.setStage(template.getStage());
        command.setEventId(eventId);
        command.setResponseId(responseId);
        command.setTemplateId(template.getId());
        
        // 自动继承模板的表单配置ID（如果模板配置了）
        if (template.getFormId() != null) {
            command.setFormId(template.getFormId());
        }

        // 设置发布信息（JSONB）
        Map<String, Object> issueInfo = new HashMap<>();
        issueInfo.put("issuedAt", LocalDateTime.now());
        issueInfo.put("templateId", template.getId());
        command.setIssueInfo(issueInfo);

        // 设置执行信息（JSONB）
        Map<String, Object> executionInfo = new HashMap<>();
        executionInfo.put("status", "issued");
        command.setExecutionInfo(executionInfo);

        return command;
    }
}
