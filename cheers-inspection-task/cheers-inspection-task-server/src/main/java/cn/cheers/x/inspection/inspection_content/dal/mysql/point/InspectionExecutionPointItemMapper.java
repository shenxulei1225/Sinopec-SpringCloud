package cn.cheers.x.inspection.inspection_content.dal.mysql.point;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.point.InspectionExecutionPointItemPageReqVO;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.point.InspectionExecutionPointItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InspectionExecutionPointItemMapper extends BaseMapperX<InspectionExecutionPointItemDO> {

    default List<InspectionExecutionPointItemDO> selectListByPointId(Long pointId) {
        return selectList(InspectionExecutionPointItemDO::getPointId, pointId);
    }

    default List<InspectionExecutionPointItemDO> selectListByPointIdAndEnabled(Long pointId, Boolean enabled) {
        return selectList(new LambdaQueryWrapperX<InspectionExecutionPointItemDO>()
                .eqIfPresent(InspectionExecutionPointItemDO::getPointId, pointId)
                .eqIfPresent(InspectionExecutionPointItemDO::getEnabled, enabled));
    }

    default List<InspectionExecutionPointItemDO> selectListByCondition(InspectionExecutionPointItemPageReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<InspectionExecutionPointItemDO>()
                .eqIfPresent(InspectionExecutionPointItemDO::getFacilityId, reqVO.getFacilityId())
                .eqIfPresent(InspectionExecutionPointItemDO::getPointId, reqVO.getPointId())
                .eqIfPresent(InspectionExecutionPointItemDO::getItemId, reqVO.getItemId())
                .eqIfPresent(InspectionExecutionPointItemDO::getEnabled, reqVO.getEnabled())
                .orderByAsc(InspectionExecutionPointItemDO::getSortNo)
                .orderByAsc(InspectionExecutionPointItemDO::getId));
    }
}
