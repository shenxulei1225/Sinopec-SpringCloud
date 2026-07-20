package cn.iocoder.yudao.module.emergency.service.audit;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.emergency.controller.admin.audit.vo.AuditLogPageReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.audit.EmergencyAuditLogDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.audit.EmergencyAuditLogMapper;
import cn.cheers.x.system.api.user.AdminUserApi;
import cn.cheers.x.system.api.user.dto.AdminUserRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 应急管理系统审计日志服务实现
 */
@Slf4j
@Service
public class EmergencyAuditLogServiceImpl implements EmergencyAuditLogService {

    private final EmergencyAuditLogMapper auditLogMapper;
    private final AdminUserApi adminUserApi;

    public EmergencyAuditLogServiceImpl(EmergencyAuditLogMapper auditLogMapper,
                                         AdminUserApi adminUserApi) {
        this.auditLogMapper = auditLogMapper;
        this.adminUserApi = adminUserApi;
    }

    @Override
    public void log(String operationType, String operationModule, String operationContent,
                    Long eventId, Long responseId, Long taskId,
                    String operationResult, String errorMessage) {
        EmergencyAuditLogDO auditLog = EmergencyAuditLogDO.builder()
                .eventId(eventId)
                .responseId(responseId)
                .taskId(taskId)
                .operationType(operationType)
                .operationModule(operationModule)
                .operationContent(operationContent)
                .operationTime(LocalDateTime.now())
                .operationResult(operationResult)
                .errorMessage(errorMessage)
                .build();

        // 从当前用户上下文获取操作人信息
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (currentUserId != null) {
            auditLog.setOperatorId(currentUserId);
            // 从用户服务获取用户名
            AdminUserRespDTO user = adminUserApi.getUser(currentUserId).getData();
            if (user != null && user.getNickname() != null) {
                auditLog.setOperatorName(user.getNickname());
            } else {
                auditLog.setOperatorName("用户" + currentUserId);
            }
        } else {
            auditLog.setOperatorId(0L);
            auditLog.setOperatorName("系统");
        }

        try {
            auditLogMapper.insert(auditLog);
        } catch (Exception e) {
            // 审计日志记录失败不应该影响业务流程，只记录错误日志
            log.error("记录审计日志失败", e);
        }
    }

    @Override
    public void logSuccess(String operationType, String operationModule, String operationContent,
                           Long eventId, Long responseId, Long taskId) {
        log(operationType, operationModule, operationContent, eventId, responseId, taskId, "success", null);
    }

    @Override
    public void logFailure(String operationType, String operationModule, String operationContent,
                           String errorMessage, Long eventId, Long responseId, Long taskId) {
        log(operationType, operationModule, operationContent, eventId, responseId, taskId, "failure", errorMessage);
    }

    @Override
    public PageResult<EmergencyAuditLogDO> getAuditLogPage(AuditLogPageReqVO pageReqVO) {
        return auditLogMapper.selectPage(pageReqVO, new LambdaQueryWrapperX<EmergencyAuditLogDO>()
                .eqIfPresent(EmergencyAuditLogDO::getEventId, pageReqVO.getEventId())
                .eqIfPresent(EmergencyAuditLogDO::getResponseId, pageReqVO.getResponseId())
                .eqIfPresent(EmergencyAuditLogDO::getTaskId, pageReqVO.getTaskId())
                .eqIfPresent(EmergencyAuditLogDO::getOperationType, pageReqVO.getOperationType())
                .eqIfPresent(EmergencyAuditLogDO::getOperationModule, pageReqVO.getOperationModule())
                .eqIfPresent(EmergencyAuditLogDO::getOperatorId, pageReqVO.getOperatorId())
                .betweenIfPresent(EmergencyAuditLogDO::getOperationTime, pageReqVO.getStartTime(), pageReqVO.getEndTime())
                .orderByDesc(EmergencyAuditLogDO::getOperationTime));
    }

    @Override
    public List<EmergencyAuditLogDO> getAuditLogsByEventId(Long eventId) {
        return auditLogMapper.selectList(
                new LambdaQueryWrapperX<EmergencyAuditLogDO>()
                        .eq(EmergencyAuditLogDO::getEventId, eventId)
                        .orderByDesc(EmergencyAuditLogDO::getOperationTime));
    }

    @Override
    public List<EmergencyAuditLogDO> getAuditLogsByResponseId(Long responseId) {
        return auditLogMapper.selectList(
                new LambdaQueryWrapperX<EmergencyAuditLogDO>()
                        .eq(EmergencyAuditLogDO::getResponseId, responseId)
                        .orderByDesc(EmergencyAuditLogDO::getOperationTime));
    }
}



