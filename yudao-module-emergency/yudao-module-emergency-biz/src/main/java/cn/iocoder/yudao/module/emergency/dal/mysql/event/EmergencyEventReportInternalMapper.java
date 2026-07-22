package cn.iocoder.yudao.module.emergency.dal.mysql.event;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventReportInternalDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 事件内部上报 Mapper
 * 
 * 按照数据模型.md中的定义实现
 */
@Mapper
public interface EmergencyEventReportInternalMapper extends BaseMapperX<EmergencyEventReportInternalDO> {
}









