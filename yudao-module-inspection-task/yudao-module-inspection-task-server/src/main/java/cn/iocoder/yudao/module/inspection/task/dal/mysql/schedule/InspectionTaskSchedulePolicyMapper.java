package cn.iocoder.yudao.module.inspection.task.dal.mysql.schedule;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule.InspectionTaskSchedulePolicyDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 巡检任务排期策略 Mapper。
 */
@Mapper
public interface InspectionTaskSchedulePolicyMapper extends BaseMapperX<InspectionTaskSchedulePolicyDO> {

    default InspectionTaskSchedulePolicyDO selectByPolicyCode(@Param("policyCode") String policyCode) {
        return selectOne(new LambdaQueryWrapperX<InspectionTaskSchedulePolicyDO>()
                .eq(InspectionTaskSchedulePolicyDO::getPolicyCode, policyCode));
    }

    default List<InspectionTaskSchedulePolicyDO> selectByEnabled(Boolean enabled) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskSchedulePolicyDO>()
                .eq(InspectionTaskSchedulePolicyDO::getEnabled, enabled)
                .orderByDesc(InspectionTaskSchedulePolicyDO::getCreateTime));
    }
}
