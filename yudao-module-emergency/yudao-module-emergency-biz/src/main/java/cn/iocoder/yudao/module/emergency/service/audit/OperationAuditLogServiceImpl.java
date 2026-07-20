package cn.iocoder.yudao.module.emergency.service.audit;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.emergency.controller.admin.audit.vo.OperationAuditLogPageReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.audit.vo.OperationAuditLogRespVO;
import cn.iocoder.yudao.module.emergency.convert.audit.OperationAuditLogConvert;
import cn.iocoder.yudao.module.emergency.dal.dataobject.audit.OperationAuditLogDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.audit.OperationAuditLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 用户操作审计日志 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class OperationAuditLogServiceImpl implements OperationAuditLogService {

    @Resource
    private OperationAuditLogMapper auditLogMapper;

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void recordAuditLog(String operationType, String businessModule, Long businessId, String businessName,
                              String operationContent, String beforeData, String afterData, String operationResult,
                              String errorMessage, String requestPath, String requestMethod) {
        try {
            // 获取当前用户信息
            Long userId = null;
            String userName = null;
            String operationIp = null;
            
            try {
                if (SecurityFrameworkUtils.getLoginUserId() != null) {
                    userId = SecurityFrameworkUtils.getLoginUserId();
                    cn.cheers.x.framework.security.core.LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
                    if (loginUser != null && loginUser.getInfo() != null) {
                        // 从info Map中获取nickname
                        userName = loginUser.getInfo().get(cn.cheers.x.framework.security.core.LoginUser.INFO_KEY_NICKNAME);
                        if (userName == null) {
                            userName = String.valueOf(userId);
                        }
                    } else {
                        userName = String.valueOf(userId);
                    }
                }
            } catch (Exception e) {
                log.warn("获取用户信息失败", e);
                userName = userId != null ? String.valueOf(userId) : null;
            }

            // 创建审计日志
            OperationAuditLogDO auditLog = OperationAuditLogDO.builder()
                    .userId(userId)
                    .userName(userName)
                    .operationType(operationType)
                    .businessModule(businessModule)
                    .businessId(businessId)
                    .businessName(businessName)
                    .operationContent(operationContent)
                    .beforeData(beforeData)
                    .afterData(afterData)
                    .operationIp(operationIp)
                    .operationTime(LocalDateTime.now())
                    .operationResult(operationResult)
                    .errorMessage(errorMessage)
                    .requestPath(requestPath)
                    .requestMethod(requestMethod)
                    .build();

            auditLogMapper.insert(auditLog);
        } catch (Exception e) {
            // 审计日志记录失败不应该影响主业务流程
            log.error("记录审计日志失败", e);
        }
    }

    @Override
    public OperationAuditLogRespVO getOperationAuditLog(Long id) {
        OperationAuditLogDO auditLog = auditLogMapper.selectById(id);
        return OperationAuditLogConvert.INSTANCE.convert(auditLog);
    }

    @Override
    public PageResult<OperationAuditLogRespVO> getOperationAuditLogPage(OperationAuditLogPageReqVO pageReqVO) {
        PageResult<OperationAuditLogDO> pageResult = auditLogMapper.selectPage(pageReqVO);
        return OperationAuditLogConvert.INSTANCE.convertPage(pageResult);
    }
}

