package cn.cheers.x.module.dynamicbusiness.dal.mysql.inspection;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.inspection.InspectionItemSopMethodDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InspectionItemSopMethodMapper extends BaseMapperX<InspectionItemSopMethodDO> {

    default List<InspectionItemSopMethodDO> selectByInspectionItemId(Long inspectionItemId) {
        return selectList(new LambdaQueryWrapperX<InspectionItemSopMethodDO>()
                .eq(InspectionItemSopMethodDO::getInspectionItemId, inspectionItemId)
                .orderByAsc(InspectionItemSopMethodDO::getSort)
                .orderByAsc(InspectionItemSopMethodDO::getId));
    }

    default InspectionItemSopMethodDO selectByItemAndMeans(Long inspectionItemId, String executionMeans) {
        return selectOne(new LambdaQueryWrapperX<InspectionItemSopMethodDO>()
                .eq(InspectionItemSopMethodDO::getInspectionItemId, inspectionItemId)
                .eq(InspectionItemSopMethodDO::getExecutionMeans, executionMeans));
    }
}
