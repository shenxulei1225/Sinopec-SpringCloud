package cn.iocoder.yudao.module.emergency.service.audit;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.audit.vo.AuditLogPageReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.audit.EmergencyAuditLogDO;

/**
 * 应急管理系统审计日志服务接口
 * 
 * 负责记录和查询系统关键操作的审计日志，包括：
 * - 事件创建、更新、确认、关闭等操作
 * - 响应启动、升级、取消等操作
 * - 任务创建、分配、完成等操作
 * - 资源分配、回收等操作
 */
public interface EmergencyAuditLogService {

    /**
     * 记录审计日志
     * 
     * @param operationType 操作类型
     * @param operationModule 操作模块
     * @param operationContent 操作内容
     * @param eventId 事件ID（可选）
     * @param responseId 响应ID（可选）
     * @param taskId 任务ID（可选）
     * @param operationResult 操作结果
     * @param errorMessage 错误信息（可选）
     */
    void log(String operationType, String operationModule, String operationContent,
             Long eventId, Long responseId, Long taskId,
             String operationResult, String errorMessage);

    /**
     * 记录审计日志（成功操作）
     * 
     * @param operationType 操作类型
     * @param operationModule 操作模块
     * @param operationContent 操作内容
     * @param eventId 事件ID（可选）
     * @param responseId 响应ID（可选）
     * @param taskId 任务ID（可选）
     */
    void logSuccess(String operationType, String operationModule, String operationContent,
                    Long eventId, Long responseId, Long taskId);

    /**
     * 记录审计日志（失败操作）
     * 
     * @param operationType 操作类型
     * @param operationModule 操作模块
     * @param operationContent 操作内容
     * @param errorMessage 错误信息
     * @param eventId 事件ID（可选）
     * @param responseId 响应ID（可选）
     * @param taskId 任务ID（可选）
     */
    void logFailure(String operationType, String operationModule, String operationContent,
                    String errorMessage, Long eventId, Long responseId, Long taskId);

    /**
     * 分页查询审计日志
     * 
     * @param pageReqVO 查询条件
     * @return 审计日志分页结果
     */
    PageResult<EmergencyAuditLogDO> getAuditLogPage(AuditLogPageReqVO pageReqVO);

    /**
     * 根据事件ID查询审计日志
     * 
     * @param eventId 事件ID
     * @return 审计日志列表
     */
    java.util.List<EmergencyAuditLogDO> getAuditLogsByEventId(Long eventId);

    /**
     * 根据响应ID查询审计日志
     * 
     * @param responseId 响应ID
     * @return 审计日志列表
     */
    java.util.List<EmergencyAuditLogDO> getAuditLogsByResponseId(Long responseId);
}



