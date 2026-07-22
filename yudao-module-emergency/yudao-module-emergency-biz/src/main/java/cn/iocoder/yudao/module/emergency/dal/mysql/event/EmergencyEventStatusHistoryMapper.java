package cn.iocoder.yudao.module.emergency.dal.mysql.event;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventStatusHistoryDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 事件状态变更历史 Mapper
 */
@Mapper
public interface EmergencyEventStatusHistoryMapper extends BaseMapperX<EmergencyEventStatusHistoryDO> {
}

