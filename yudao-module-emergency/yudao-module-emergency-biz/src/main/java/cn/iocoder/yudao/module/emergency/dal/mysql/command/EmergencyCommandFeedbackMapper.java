package cn.iocoder.yudao.module.emergency.dal.mysql.command;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandFeedbackDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应急指令反馈 Mapper
 */
@Mapper
public interface EmergencyCommandFeedbackMapper extends BaseMapperX<EmergencyCommandFeedbackDO> {

    default PageResult<EmergencyCommandFeedbackDO> selectPage(Long commandId, Long executorId, String status,
                                                             Integer pageNo, Integer pageSize) {
        cn.cheers.x.framework.common.pojo.PageParam pageParam = new cn.cheers.x.framework.common.pojo.PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        return selectPage(pageParam,
                new LambdaQueryWrapperX<EmergencyCommandFeedbackDO>()
                .eqIfPresent(EmergencyCommandFeedbackDO::getCommandId, commandId)
                .eqIfPresent(EmergencyCommandFeedbackDO::getExecutorId, executorId)
                .eqIfPresent(EmergencyCommandFeedbackDO::getStatus, status)
                .orderByDesc(EmergencyCommandFeedbackDO::getCreateTime));
    }
}
