package cn.iocoder.yudao.module.inspection.inspection_content.dal.mysql.spec;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.inspection.inspection_content.dal.dataobject.spec.InspectionSpecRefDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InspectionSpecRefMapper extends BaseMapperX<InspectionSpecRefDO> {

    default List<InspectionSpecRefDO> selectListBySpecId(Long specId) {
        return selectList(InspectionSpecRefDO::getSpecId, specId);
    }

    default List<InspectionSpecRefDO> selectListBySpecIdAndEnabled(Long specId, Boolean enabled) {
        return selectList(new LambdaQueryWrapperX<InspectionSpecRefDO>()
                .eqIfPresent(InspectionSpecRefDO::getSpecId, specId)
                .eqIfPresent(InspectionSpecRefDO::getEnabled, enabled));
    }
}
