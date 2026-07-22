package cn.iocoder.yudao.module.emergency.service.audit;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.audit.vo.OperationAuditLogPageReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.audit.vo.OperationAuditLogRespVO;

/**
 * 用户操作审计日志 Service 接口
 *
 * @author 芋道源码
 */
public interface OperationAuditLogService {

    /**
     * 记录操作审计日志
     *
     * @param operationType 操作类型
     * @param businessModule 业务模块
     * @param businessId 业务对象ID
     * @param businessName 业务对象名称
     * @param operationContent 操作内容
     * @param beforeData 操作前数据
     * @param afterData 操作后数据
     * @param operationResult 操作结果
     * @param errorMessage 错误信息
     * @param requestPath 请求路径
     * @param requestMethod 请求方法
     */
    void recordAuditLog(String operationType, String businessModule, Long businessId, String businessName,
                       String operationContent, String beforeData, String afterData, String operationResult,
                       String errorMessage, String requestPath, String requestMethod);

    /**
     * 获得操作审计日志
     *
     * @param id 编号
     * @return 操作审计日志
     */
    OperationAuditLogRespVO getOperationAuditLog(Long id);

    /**
     * 获得操作审计日志分页
     *
     * @param pageReqVO 分页查询
     * @return 操作审计日志分页
     */
    PageResult<OperationAuditLogRespVO> getOperationAuditLogPage(OperationAuditLogPageReqVO pageReqVO);
}








