package cn.iocoder.yudao.module.emergency.service.command;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.emergency.controller.admin.command.vo.CommandStepCreateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.command.vo.CommandStepRespVO;
import cn.iocoder.yudao.module.emergency.controller.admin.command.vo.CommandStepUpdateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.command.vo.CommandStepCompleteReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandStepDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.command.EmergencyCommandStepMapper;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import cn.cheers.x.system.api.user.AdminUserApi;
import cn.cheers.x.system.api.user.dto.AdminUserRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 指令步骤 Service 实现类
 */
@Service
@Slf4j
public class EmergencyCommandStepServiceImpl implements EmergencyCommandStepService {

    @Resource
    private EmergencyCommandStepMapper commandStepMapper;

    @Resource
    private AdminUserApi adminUserApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCommandStep(CommandStepCreateReqVO createReqVO) {
        log.info("创建指令步骤: commandId={}, stepContent={}, timeLimitMinutes={}",
                createReqVO.getCommandId(), createReqVO.getStepContent(), createReqVO.getTimeLimitMinutes());

        // 校验执行时限：必须大于0且不超过1440分钟（24小时）
        Integer timeLimit = createReqVO.getTimeLimitMinutes();
        if (timeLimit == null || timeLimit <= 0 || timeLimit > 1440) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.COMMAND_STEP_TIME_LIMIT_INVALID);
        }

        // 构建指令步骤DO
        EmergencyCommandStepDO step = new EmergencyCommandStepDO();
        step.setCommandId(createReqVO.getCommandId());
        step.setStepContent(createReqVO.getStepContent());
        step.setTimeLimitMinutes(timeLimit);
        step.setStatus("pending");
        step.setTimeoutFlag(false);
        step.setPlanStepId(createReqVO.getPlanStepId());

        // 插入指令步骤
        commandStepMapper.insert(step);

        log.info("指令步骤创建成功: id={}, commandId={}", step.getId(), step.getCommandId());
        return step.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCommandStep(CommandStepUpdateReqVO updateReqVO) {
        log.info("更新指令步骤: id={}", updateReqVO.getId());

        // 校验存在
        EmergencyCommandStepDO step = validateStepExists(updateReqVO.getId());

        // 如果步骤已完成，不允许修改
        if ("completed".equals(step.getStatus())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.COMMAND_STEP_ALREADY_COMPLETED);
        }

        // 如果步骤已超时，不允许修改
        if (Boolean.TRUE.equals(step.getTimeoutFlag())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.COMMAND_STEP_ALREADY_TIMEOUT);
        }

        // 校验执行时限
        if (updateReqVO.getTimeLimitMinutes() != null) {
            Integer timeLimit = updateReqVO.getTimeLimitMinutes();
            if (timeLimit <= 0 || timeLimit > 1440) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.COMMAND_STEP_TIME_LIMIT_INVALID);
            }
        }

        // 构建更新DO
        EmergencyCommandStepDO updateObj = new EmergencyCommandStepDO();
        updateObj.setId(updateReqVO.getId());
        if (updateReqVO.getStepContent() != null) {
            updateObj.setStepContent(updateReqVO.getStepContent());
        }
        if (updateReqVO.getTimeLimitMinutes() != null) {
            updateObj.setTimeLimitMinutes(updateReqVO.getTimeLimitMinutes());
        }
        if (updateReqVO.getPlanStepId() != null) {
            updateObj.setPlanStepId(updateReqVO.getPlanStepId());
        }

        commandStepMapper.updateById(updateObj);

        log.info("指令步骤更新成功: id={}", updateReqVO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCommandStep(Long id) {
        log.info("删除指令步骤: id={}", id);

        // 校验存在
        EmergencyCommandStepDO step = validateStepExists(id);

        // 如果步骤已开始执行，不允许删除
        if (!"pending".equals(step.getStatus())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.COMMAND_STEP_CANNOT_DELETE);
        }

        // 删除指令步骤
        commandStepMapper.deleteById(id);

        log.info("指令步骤删除成功: id={}", id);
    }

    @Override
    public CommandStepRespVO getCommandStep(Long id) {
        EmergencyCommandStepDO step = validateStepExists(id);
        return convert(step);
    }

    @Override
    public List<CommandStepRespVO> getCommandStepList(Long commandId) {
        List<EmergencyCommandStepDO> steps = commandStepMapper.selectListByCommandId(commandId);
        return steps.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startCommandStep(Long id) {
        log.info("启动指令步骤: id={}", id);

        EmergencyCommandStepDO step = validateStepExists(id);

        // 校验状态
        if (!"pending".equals(step.getStatus())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.COMMAND_STEP_STATUS_INVALID);
        }

        // 更新状态和开始时间
        EmergencyCommandStepDO updateObj = new EmergencyCommandStepDO();
        updateObj.setId(id);
        updateObj.setStatus("in_progress");
        updateObj.setStartTime(LocalDateTime.now());

        // 设置执行人
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (currentUserId != null) {
            updateObj.setExecutorId(currentUserId);
            AdminUserRespDTO user = adminUserApi.getUser(currentUserId).getData();
            if (user != null && user.getNickname() != null) {
                updateObj.setExecutorName(user.getNickname());
            }
        }

        commandStepMapper.updateById(updateObj);

        log.info("指令步骤启动成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeCommandStep(Long id, CommandStepCompleteReqVO completeReqVO) {
        log.info("完成指令步骤: id={}", id);

        EmergencyCommandStepDO step = validateStepExists(id);

        // 校验状态
        if (!"in_progress".equals(step.getStatus()) && !"timeout".equals(step.getStatus())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.COMMAND_STEP_STATUS_INVALID);
        }

        // 如果已超时，必须提供超时原因和处理措施
        if (Boolean.TRUE.equals(step.getTimeoutFlag()) || "timeout".equals(step.getStatus())) {
            if (completeReqVO.getTimeoutReason() == null || completeReqVO.getTimeoutReason().isEmpty()) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.COMMAND_STEP_TIMEOUT_REASON_REQUIRED);
            }
            if (completeReqVO.getHandlingMeasures() == null || completeReqVO.getHandlingMeasures().isEmpty()) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.COMMAND_STEP_HANDLING_MEASURES_REQUIRED);
            }
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = step.getStartTime();

        // 计算实际执行时长
        Integer actualDuration = null;
        if (startTime != null) {
            actualDuration = (int) ChronoUnit.MINUTES.between(startTime, now);
        }

        // 更新状态和完成时间
        EmergencyCommandStepDO updateObj = new EmergencyCommandStepDO();
        updateObj.setId(id);
        updateObj.setStatus("completed");
        updateObj.setCompleteTime(now);
        updateObj.setActualDurationMinutes(actualDuration);

        // 如果超时，记录超时原因和处理措施
        if (Boolean.TRUE.equals(step.getTimeoutFlag()) || "timeout".equals(step.getStatus())) {
            updateObj.setTimeoutReason(completeReqVO.getTimeoutReason());
            updateObj.setHandlingMeasures(completeReqVO.getHandlingMeasures());
        }

        commandStepMapper.updateById(updateObj);

        log.info("指令步骤完成成功: id={}, actualDuration={}分钟", id, actualDuration);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelCommandStep(Long id, String reason) {
        log.info("取消指令步骤: id={}, reason={}", id, reason);

        EmergencyCommandStepDO step = validateStepExists(id);

        // 校验状态
        if ("completed".equals(step.getStatus())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.COMMAND_STEP_ALREADY_COMPLETED);
        }

        // 更新状态
        EmergencyCommandStepDO updateObj = new EmergencyCommandStepDO();
        updateObj.setId(id);
        updateObj.setStatus("cancelled");

        commandStepMapper.updateById(updateObj);

        log.info("指令步骤取消成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markTimeout(Long id, String timeoutReason, String handlingMeasures) {
        log.info("标记指令步骤超时: id={}", id);

        EmergencyCommandStepDO step = validateStepExists(id);

        // 更新超时标记和状态
        EmergencyCommandStepDO updateObj = new EmergencyCommandStepDO();
        updateObj.setId(id);
        updateObj.setTimeoutFlag(true);
        updateObj.setStatus("timeout");
        if (timeoutReason != null) {
            updateObj.setTimeoutReason(timeoutReason);
        }
        if (handlingMeasures != null) {
            updateObj.setHandlingMeasures(handlingMeasures);
        }

        commandStepMapper.updateById(updateObj);

        log.info("指令步骤超时标记成功: id={}", id);
    }

    private EmergencyCommandStepDO validateStepExists(Long id) {
        EmergencyCommandStepDO step = commandStepMapper.selectById(id);
        if (step == null) {
            throw exception(ErrorCodeConstants.COMMAND_STEP_NOT_EXISTS);
        }
        return step;
    }

    private CommandStepRespVO convert(EmergencyCommandStepDO step) {
        CommandStepRespVO respVO = new CommandStepRespVO();
        respVO.setId(step.getId());
        respVO.setCommandId(step.getCommandId());
        respVO.setStepContent(step.getStepContent());
        respVO.setTimeLimitMinutes(step.getTimeLimitMinutes());
        respVO.setStartTime(step.getStartTime());
        respVO.setCompleteTime(step.getCompleteTime());
        respVO.setStatus(step.getStatus());
        respVO.setTimeoutFlag(step.getTimeoutFlag());
        respVO.setActualDurationMinutes(step.getActualDurationMinutes());
        respVO.setPlanStepId(step.getPlanStepId());
        respVO.setExecutorId(step.getExecutorId());
        respVO.setExecutorName(step.getExecutorName());
        respVO.setTimeoutReason(step.getTimeoutReason());
        respVO.setHandlingMeasures(step.getHandlingMeasures());
        respVO.setCreateTime(step.getCreateTime());
        return respVO;
    }
}

