package cn.iocoder.yudao.module.emergency.dal.mysql.audit;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emergency.controller.admin.audit.vo.OperationAuditLogPageReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.audit.OperationAuditLogDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户操作审计日志 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface OperationAuditLogMapper extends BaseMapperX<OperationAuditLogDO> {

    default PageResult<OperationAuditLogDO> selectPage(OperationAuditLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OperationAuditLogDO>()
                .eqIfPresent(OperationAuditLogDO::getUserId, reqVO.getUserId())
                .eqIfPresent(OperationAuditLogDO::getOperationType, reqVO.getOperationType())
                .eqIfPresent(OperationAuditLogDO::getBusinessModule, reqVO.getBusinessModule())
                .eqIfPresent(OperationAuditLogDO::getBusinessId, reqVO.getBusinessId())
                .eqIfPresent(OperationAuditLogDO::getOperationResult, reqVO.getOperationResult())
                .betweenIfPresent(OperationAuditLogDO::getOperationTime, reqVO.getOperationTime())
                .orderByDesc(OperationAuditLogDO::getOperationTime));
    }
}








