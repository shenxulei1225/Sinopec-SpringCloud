package cn.cheers.x.workorder.dal.mysql;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.workorder.dal.dataobject.WorkOrderDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 工单 Mapper
 *
 * @author 工单标准服务
 */
@Mapper
public interface WorkOrderMapper extends BaseMapperX<WorkOrderDO> {

    /**
     * 按工单编号查询
     *
     * @param woNo 工单编号
     * @return 工单
     */
    default WorkOrderDO selectByWoNo(String woNo) {
        return selectOne(WorkOrderDO::getWoNo, woNo);
    }

    /**
     * 按业务域范围与状态查询工单列表
     *
     * @param scope  业务域范围
     * @param status 工单状态
     * @return 工单列表
     */
    default List<WorkOrderDO> selectListByScopeAndStatus(String scope, String status) {
        return selectList(new LambdaQueryWrapperX<WorkOrderDO>()
                .eq(WorkOrderDO::getScope, scope)
                .eq(WorkOrderDO::getStatus, status)
                .orderByDesc(WorkOrderDO::getCreateTime));
    }

}
