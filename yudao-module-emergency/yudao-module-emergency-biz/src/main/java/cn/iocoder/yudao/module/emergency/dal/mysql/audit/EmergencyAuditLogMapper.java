package cn.iocoder.yudao.module.emergency.dal.mysql.audit;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.emergency.dal.dataobject.audit.EmergencyAuditLogDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应急管理系统审计日志 Mapper
 */
@Mapper
public interface EmergencyAuditLogMapper extends BaseMapperX<EmergencyAuditLogDO> {
}



