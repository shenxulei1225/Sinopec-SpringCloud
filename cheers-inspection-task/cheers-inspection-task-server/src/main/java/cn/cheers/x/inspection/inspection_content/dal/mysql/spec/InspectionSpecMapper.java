package cn.cheers.x.inspection.inspection_content.dal.mysql.spec;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.spec.InspectionSpecDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InspectionSpecMapper extends BaseMapperX<InspectionSpecDO> {

    default InspectionSpecDO selectBySpecCodeAndVersion(String specCode, String specVersion) {
        return selectOne(new LambdaQueryWrapperX<InspectionSpecDO>()
                .eqIfPresent(InspectionSpecDO::getSpecCode, specCode)
                .eqIfPresent(InspectionSpecDO::getSpecVersion, specVersion));
    }

    default List<InspectionSpecDO> selectListByStatus(String status) {
        return selectList(new LambdaQueryWrapperX<InspectionSpecDO>()
                .eqIfPresent(InspectionSpecDO::getStatus, status));
    }
}
