package cn.cheers.x.mes.dal.mysql.wm.stocktaking.task;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.mes.controller.admin.wm.stocktaking.task.vo.MesWmStockTakingTaskPageReqVO;
import cn.cheers.x.mes.dal.dataobject.wm.stocktaking.task.MesWmStockTakingTaskDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 盘点任务 Mapper
 *
 * 
 */
@Mapper
public interface MesWmStockTakingTaskMapper extends BaseMapperX<MesWmStockTakingTaskDO> {

    default MesWmStockTakingTaskDO selectByCode(String code) {
        return selectOne(MesWmStockTakingTaskDO::getCode, code);
    }

    default PageResult<MesWmStockTakingTaskDO> selectPage(MesWmStockTakingTaskPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmStockTakingTaskDO>()
                .likeIfPresent(MesWmStockTakingTaskDO::getCode, reqVO.getCode())
                .likeIfPresent(MesWmStockTakingTaskDO::getName, reqVO.getName())
                .eqIfPresent(MesWmStockTakingTaskDO::getType, reqVO.getType())
                .eqIfPresent(MesWmStockTakingTaskDO::getStatus, reqVO.getStatus())
                .eqIfPresent(MesWmStockTakingTaskDO::getUserId, reqVO.getUserId())
                .eqIfPresent(MesWmStockTakingTaskDO::getPlanId, reqVO.getPlanId())
                .betweenIfPresent(MesWmStockTakingTaskDO::getTakingDate, reqVO.getTakingDate())
                .orderByDesc(MesWmStockTakingTaskDO::getId));
    }

}
