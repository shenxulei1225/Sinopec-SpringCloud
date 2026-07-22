package cn.iocoder.yudao.module.emergency.service.command;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.emergency.controller.admin.command.vo.*;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandTemplateDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.command.EmergencyCommandMapper;
import cn.iocoder.yudao.module.emergency.event.CommandDeletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants.COMMAND_NOT_EXISTS;

/**
 * 应急指令 Service 实现类
 */
@Service
@Validated
@Slf4j
public class EmergencyCommandServiceImpl implements EmergencyCommandService {

    @Resource
    private EmergencyCommandMapper commandMapper;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Resource
    private EmergencyCommandTemplateService commandTemplateService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCommand(EmergencyCommandCreateReqVO createReqVO) {
        log.info("创建应急指令: commandNo={}, title={}, commandType={}",
                createReqVO.getCommandNo(), createReqVO.getTitle(), createReqVO.getCommandType());

        // 构建指令DO
        EmergencyCommandDO command = new EmergencyCommandDO();
        command.setCommandNo(createReqVO.getCommandNo());
        command.setTitle(createReqVO.getTitle());
        command.setContent(createReqVO.getContent());
        command.setCommandType(createReqVO.getCommandType());
        command.setPriority(createReqVO.getPriority() != null ? createReqVO.getPriority() : "normal");
        command.setDeadline(createReqVO.getDeadline());
        command.setStatus("draft");
        command.setStage(createReqVO.getStage());
        command.setEventId(createReqVO.getEventId());
        command.setResponseId(createReqVO.getResponseId());
        command.setTemplateId(createReqVO.getTemplateId());
        command.setAttachments(createReqVO.getAttachments());
        // issue_info字段在数据库中是NOT NULL，如果为null则初始化为空Map
        command.setIssueInfo(createReqVO.getIssueInfo() != null ? createReqVO.getIssueInfo() : new HashMap<>());
        command.setExecutionInfo(createReqVO.getExecutionInfo());
        
        // 表单配置ID处理：用户输入优先，模板作为默认值
        if (createReqVO.getFormId() != null) {
            // 用户指定了formId，使用用户输入
            command.setFormId(createReqVO.getFormId());
        } else if (createReqVO.getTemplateId() != null) {
            // 用户没有指定，但指定了模板ID，尝试从模板继承
            EmergencyCommandTemplateDO template = commandTemplateService.getCommandTemplate(createReqVO.getTemplateId());
            if (template != null && template.getFormId() != null) {
                command.setFormId(template.getFormId());
            }
        }

        // 验证formId（如果设置）
        if (command.getFormId() != null) {
            validateFormId(command.getFormId());
        }

        // 插入指令
        commandMapper.insert(command);

        log.info("应急指令创建成功: id={}, commandNo={}", command.getId(), command.getCommandNo());
        return command.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCommand(EmergencyCommandUpdateReqVO updateReqVO) {
        log.info("更新应急指令: id={}, title={}", updateReqVO.getId(), updateReqVO.getTitle());

        // 校验存在
        EmergencyCommandDO existingCommand = validateCommandExists(updateReqVO.getId());

        // 构建更新DO（formId允许更新，用户可以在指令中添加或修改表单配置）
        EmergencyCommandDO updateObj = new EmergencyCommandDO();
        updateObj.setId(updateReqVO.getId());
        updateObj.setTitle(updateReqVO.getTitle());
        updateObj.setContent(updateReqVO.getContent());
        updateObj.setCommandType(updateReqVO.getCommandType());
        updateObj.setPriority(updateReqVO.getPriority());
        updateObj.setDeadline(updateReqVO.getDeadline());
        updateObj.setStatus(updateReqVO.getStatus());
        updateObj.setStage(updateReqVO.getStage());
        updateObj.setEventId(updateReqVO.getEventId());
        updateObj.setResponseId(updateReqVO.getResponseId());
        updateObj.setTemplateId(updateReqVO.getTemplateId());
        updateObj.setAttachments(updateReqVO.getAttachments());
        updateObj.setIssueInfo(updateReqVO.getIssueInfo());
        updateObj.setExecutionInfo(updateReqVO.getExecutionInfo());
        updateObj.setFormId(updateReqVO.getFormId());
        
        // 验证formId（如果设置）
        if (updateObj.getFormId() != null) {
            validateFormId(updateObj.getFormId());
        }

        commandMapper.updateById(updateObj);

        log.info("应急指令更新成功: id={}", updateReqVO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCommand(Long id) {
        log.info("删除应急指令: id={}", id);

        // 校验存在
        EmergencyCommandDO command = validateCommandExists(id);

        // 获取操作人和租户ID（在删除前获取，因为删除后无法获取）
        // 注意：使用用户ID作为操作人标识，如果需要用户名可以后续扩展
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        String operator = currentUserId != null ? String.valueOf(currentUserId) : "system";
        Long tenantId = command.getTenantId();

        // 删除指令（软删除）
        commandMapper.deleteById(id);

        // 发布指令删除事件（用于级联处理：从预案步骤的commandIdList中移除该指令ID）
        eventPublisher.publishEvent(new CommandDeletedEvent(this, id, operator, tenantId));

        log.info("应急指令删除成功: id={}, operator={}, tenantId={}", id, operator, tenantId);
    }

    @Override
    public EmergencyCommandDO getCommand(Long id) {
        return commandMapper.selectById(id);
    }

    @Override
    public PageResult<EmergencyCommandDO> getCommandPage(EmergencyCommandPageReqVO pageReqVO) {
        return commandMapper.selectPage(
                pageReqVO.getCommandNo(),
                pageReqVO.getTitle(),
                pageReqVO.getCommandType(),
                pageReqVO.getPriority(),
                pageReqVO.getStatus(),
                pageReqVO.getStage(),
                pageReqVO.getEventId(),
                pageReqVO.getResponseId(),
                pageReqVO.getTemplateId(),
                pageReqVO.getDeadlineStart(),
                pageReqVO.getDeadlineEnd(),
                pageReqVO.getPageNo(),
                pageReqVO.getPageSize()
        );
    }

    @Override
    public void updateCommandStatus(Long id, String status) {
        log.info("更新指令状态: id={}, status={}", id, status);

        // 校验存在
        validateCommandExists(id);

        // 获取当前指令（获取完整对象，确保所有字段都存在）
        EmergencyCommandDO command = commandMapper.selectById(id);
        Map<String, Object> executionInfo = command.getExecutionInfo();
        if (executionInfo == null) {
            executionInfo = new HashMap<>();
        }
        executionInfo.put("status", status);
        
        // 直接更新完整对象，MyBatis Plus 会自动使用实体类上定义的 TypeHandler
        command.setExecutionInfo(executionInfo);
        commandMapper.updateById(command);
    }

    @Override
    public void terminateCommand(Long id, String reason) {
        log.info("终止指令: id={}, reason={}", id, reason);

        // 校验存在
        validateCommandExists(id);

        // 获取当前指令（获取完整对象，确保所有字段都存在）
        EmergencyCommandDO command = commandMapper.selectById(id);
        Map<String, Object> executionInfo = command.getExecutionInfo();
        if (executionInfo == null) {
            executionInfo = new HashMap<>();
        }
        executionInfo.put("status", "terminated");
        executionInfo.put("terminationReason", reason);
        executionInfo.put("terminatedAt", LocalDateTime.now().toString()); // 转换为字符串，避免JSONB序列化问题

        // 直接更新完整对象，MyBatis Plus 会自动使用实体类上定义的 TypeHandler
        command.setExecutionInfo(executionInfo);
        commandMapper.updateById(command);
    }

    private EmergencyCommandDO validateCommandExists(Long id) {
        EmergencyCommandDO command = commandMapper.selectById(id);
        if (command == null) {
            throw exception(COMMAND_NOT_EXISTS);
        }
        return command;
    }

    /**
     * 验证表单配置ID的有效性
     * @param formId 表单配置ID
     * @throws RuntimeException 如果表单配置不存在或已删除
     */
    private void validateFormId(Long formId) {
        if (formId == null) {
            return;
        }
        // TODO: 调用基础服务的表单配置API验证表单配置ID的有效性
        // 这里暂时只做非空验证，实际应该调用基础服务的API
        // 示例：FormConfigApi formConfigApi; formConfigApi.getFormConfig(formId);
        log.warn("表单配置ID验证待实现，formId: {}", formId);
    }
}