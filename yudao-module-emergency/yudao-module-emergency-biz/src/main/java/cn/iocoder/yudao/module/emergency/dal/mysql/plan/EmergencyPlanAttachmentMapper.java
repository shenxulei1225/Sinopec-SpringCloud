package cn.iocoder.yudao.module.emergency.dal.mysql.plan;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanAttachmentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmergencyPlanAttachmentMapper extends BaseMapperX<EmergencyPlanAttachmentDO> {

    default List<EmergencyPlanAttachmentDO> selectListByPlanId(Long planId) {
        return selectList(EmergencyPlanAttachmentDO::getPlanId, planId);
    }

    default int deleteByPlanId(Long planId) {
        return delete(EmergencyPlanAttachmentDO::getPlanId, planId);
    }
}

