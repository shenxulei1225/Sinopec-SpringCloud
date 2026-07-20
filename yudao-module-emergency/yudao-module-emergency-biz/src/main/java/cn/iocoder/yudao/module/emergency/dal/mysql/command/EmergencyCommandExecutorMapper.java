package cn.iocoder.yudao.module.emergency.dal.mysql.command;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandExecutorDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 应急指令执行人 Mapper
 */
@Mapper
public interface EmergencyCommandExecutorMapper extends BaseMapperX<EmergencyCommandExecutorDO> {

    default List<EmergencyCommandExecutorDO> selectByCommandId(Long commandId) {
        return selectList(EmergencyCommandExecutorDO::getCommandId, commandId);
    }

    default List<EmergencyCommandExecutorDO> selectByUserId(Long userId) {
        return selectList(EmergencyCommandExecutorDO::getUserId, userId);
    }
}
