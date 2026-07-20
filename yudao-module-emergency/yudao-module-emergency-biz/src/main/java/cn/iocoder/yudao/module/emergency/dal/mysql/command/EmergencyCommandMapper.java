package cn.iocoder.yudao.module.emergency.dal.mysql.command;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

/**
 * 应急指令 Mapper
 */
@Mapper
public interface EmergencyCommandMapper extends BaseMapperX<EmergencyCommandDO> {

    default PageResult<EmergencyCommandDO> selectPage(String commandNo, String title, String commandType,
                                                     String priority, String status, String stage,
                                                     Long eventId, Long responseId, Long templateId,
                                                     LocalDateTime deadlineStart, LocalDateTime deadlineEnd,
                                                     Integer pageNo, Integer pageSize) {
        cn.cheers.x.framework.common.pojo.PageParam pageParam = new cn.cheers.x.framework.common.pojo.PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        return selectPage(pageParam,
                new LambdaQueryWrapperX<EmergencyCommandDO>()
                .likeIfPresent(EmergencyCommandDO::getCommandNo, commandNo)
                .likeIfPresent(EmergencyCommandDO::getTitle, title)
                .eqIfPresent(EmergencyCommandDO::getCommandType, commandType)
                .eqIfPresent(EmergencyCommandDO::getPriority, priority)
                .eqIfPresent(EmergencyCommandDO::getStatus, status)
                .eqIfPresent(EmergencyCommandDO::getStage, stage)
                .eqIfPresent(EmergencyCommandDO::getEventId, eventId)
                .eqIfPresent(EmergencyCommandDO::getResponseId, responseId)
                .eqIfPresent(EmergencyCommandDO::getTemplateId, templateId)
                .geIfPresent(EmergencyCommandDO::getDeadline, deadlineStart)
                .leIfPresent(EmergencyCommandDO::getDeadline, deadlineEnd)
                .orderByDesc(EmergencyCommandDO::getPriority)
                .orderByDesc(EmergencyCommandDO::getCreateTime));
    }
}
