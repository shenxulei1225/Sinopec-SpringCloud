package cn.cheers.x.workorder.dal.mysql;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.workorder.dal.dataobject.WorkOrderStepResultDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 工单步骤执行结果 Mapper
 *
 * @author 工单标准服务
 */
@Mapper
public interface WorkOrderStepResultMapper extends BaseMapperX<WorkOrderStepResultDO> {

    /**
     * 按工单 ID 查询步骤结果列表
     *
     * @param workOrderId 工单 ID
     * @return 步骤结果列表
     */
    default List<WorkOrderStepResultDO> selectListByWorkOrderId(Long workOrderId) {
        return selectList(new LambdaQueryWrapperX<WorkOrderStepResultDO>()
                .eq(WorkOrderStepResultDO::getWorkOrderId, workOrderId)
                .orderByAsc(WorkOrderStepResultDO::getStepOrder));
    }

}
