package cn.cheers.x.workorder.dal.mysql;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.workorder.controller.admin.vo.order.WorkOrderPageReqVO;
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

    /**
     * 查询指定工单编号前缀下的最大编号（用于日序号）
     *
     * @param woNoPrefix 前缀，如 WO-20260719-
     * @return 最大工单编号，或 null
     */
    default WorkOrderDO selectLatestByWoNoPrefix(String woNoPrefix) {
        return selectOne(new LambdaQueryWrapperX<WorkOrderDO>()
                .likeRight(WorkOrderDO::getWoNo, woNoPrefix)
                .orderByDesc(WorkOrderDO::getWoNo)
                .last("LIMIT 1"));
    }

    /**
     * 工单分页
     *
     * @param reqVO 查询条件
     * @return 分页结果
     */
    default PageResult<WorkOrderDO> selectPage(WorkOrderPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<WorkOrderDO>()
                .eqIfPresent(WorkOrderDO::getScope, reqVO.getScope())
                .eqIfPresent(WorkOrderDO::getStatus, reqVO.getStatus())
                .eqIfPresent(WorkOrderDO::getWoNo, reqVO.getWoNo())
                .likeIfPresent(WorkOrderDO::getTitle, reqVO.getTitle())
                .orderByDesc(WorkOrderDO::getId));
    }

}
