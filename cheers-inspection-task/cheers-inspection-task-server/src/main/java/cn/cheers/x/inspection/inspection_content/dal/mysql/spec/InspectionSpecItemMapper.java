package cn.cheers.x.inspection.inspection_content.dal.mysql.spec;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.spec.InspectionSpecItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InspectionSpecItemMapper extends BaseMapperX<InspectionSpecItemDO> {

    default List<InspectionSpecItemDO> selectListBySpecId(Long specId) {
        return selectList(InspectionSpecItemDO::getSpecId, specId);
    }

    default List<InspectionSpecItemDO> selectListBySpecIdAndEnabled(Long specId, Boolean enabled) {
        return selectList(new LambdaQueryWrapperX<InspectionSpecItemDO>()
                .eqIfPresent(InspectionSpecItemDO::getSpecId, specId)
                .eqIfPresent(InspectionSpecItemDO::getEnabled, enabled));
    }
}
