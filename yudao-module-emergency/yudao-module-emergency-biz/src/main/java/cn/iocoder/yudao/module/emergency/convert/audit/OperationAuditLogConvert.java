package cn.iocoder.yudao.module.emergency.convert.audit;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.audit.vo.OperationAuditLogRespVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.audit.OperationAuditLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 用户操作审计日志 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface OperationAuditLogConvert {

    OperationAuditLogConvert INSTANCE = Mappers.getMapper(OperationAuditLogConvert.class);

    OperationAuditLogRespVO convert(OperationAuditLogDO bean);

    PageResult<OperationAuditLogRespVO> convertPage(PageResult<OperationAuditLogDO> page);
}





