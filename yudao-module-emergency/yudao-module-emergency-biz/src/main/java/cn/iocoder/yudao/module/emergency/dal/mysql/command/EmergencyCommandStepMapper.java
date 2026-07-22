package cn.iocoder.yudao.module.emergency.dal.mysql.command;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandStepDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 指令步骤 Mapper
 */
@Mapper
public interface EmergencyCommandStepMapper extends BaseMapperX<EmergencyCommandStepDO> {

    default List<EmergencyCommandStepDO> selectListByCommandId(Long commandId) {
        return selectList(new LambdaQueryWrapperX<EmergencyCommandStepDO>()
                .eq(EmergencyCommandStepDO::getCommandId, commandId)
                .orderByAsc(EmergencyCommandStepDO::getCreateTime));
    }

    default List<EmergencyCommandStepDO> selectTimeoutSteps(LocalDateTime now) {
        return selectList(new LambdaQueryWrapperX<EmergencyCommandStepDO>()
                .eq(EmergencyCommandStepDO::getStatus, "in_progress")
                .eq(EmergencyCommandStepDO::getTimeoutFlag, false)
                .isNotNull(EmergencyCommandStepDO::getStartTime)
                .isNotNull(EmergencyCommandStepDO::getTimeLimitMinutes));
    }

    default List<EmergencyCommandStepDO> selectStepsNearTimeout(LocalDateTime warningTime) {
        return selectList(new LambdaQueryWrapperX<EmergencyCommandStepDO>()
                .eq(EmergencyCommandStepDO::getStatus, "in_progress")
                .eq(EmergencyCommandStepDO::getTimeoutFlag, false)
                .isNotNull(EmergencyCommandStepDO::getStartTime)
                .isNotNull(EmergencyCommandStepDO::getTimeLimitMinutes)
                .le(EmergencyCommandStepDO::getStartTime, warningTime));
    }
}

