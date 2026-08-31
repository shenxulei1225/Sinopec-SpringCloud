package cn.cheers.x.module.dynamicbusiness.dal.mysql.sop;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.SopMethodBindingDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SopMethodBindingMapper extends BaseMapperX<SopMethodBindingDO> {

    default List<SopMethodBindingDO> selectBySubject(String subjectType, Long subjectId) {
        return selectList(new LambdaQueryWrapperX<SopMethodBindingDO>()
                .eq(SopMethodBindingDO::getSubjectType, subjectType)
                .eq(SopMethodBindingDO::getSubjectId, subjectId)
                .orderByAsc(SopMethodBindingDO::getDimensionKey)
                .orderByAsc(SopMethodBindingDO::getDimensionValue)
                .orderByAsc(SopMethodBindingDO::getId));
    }

    default SopMethodBindingDO selectByIdentity(
            String subjectType, Long subjectId, String dimensionKey, String dimensionValue) {
        return selectOne(new LambdaQueryWrapperX<SopMethodBindingDO>()
                .eq(SopMethodBindingDO::getSubjectType, subjectType)
                .eq(SopMethodBindingDO::getSubjectId, subjectId)
                .eq(SopMethodBindingDO::getDimensionKey, dimensionKey)
                .eq(SopMethodBindingDO::getDimensionValue, dimensionValue));
    }
}
