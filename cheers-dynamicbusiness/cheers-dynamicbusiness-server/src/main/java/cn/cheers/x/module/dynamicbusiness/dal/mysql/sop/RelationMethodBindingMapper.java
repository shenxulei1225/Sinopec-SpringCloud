package cn.cheers.x.module.dynamicbusiness.dal.mysql.sop;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.RelationMethodBindingDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RelationMethodBindingMapper extends BaseMapperX<RelationMethodBindingDO> {

    default List<RelationMethodBindingDO> selectBySubject(String subjectType, Long subjectId) {
        return selectList(new LambdaQueryWrapperX<RelationMethodBindingDO>()
                .eq(RelationMethodBindingDO::getSubjectType, subjectType)
                .eq(RelationMethodBindingDO::getSubjectId, subjectId)
                .orderByAsc(RelationMethodBindingDO::getDimensionKey)
                .orderByAsc(RelationMethodBindingDO::getDimensionValue)
                .orderByAsc(RelationMethodBindingDO::getId));
    }

    default RelationMethodBindingDO selectByIdentity(
            String subjectType, Long subjectId, String dimensionKey, String dimensionValue) {
        return selectOne(new LambdaQueryWrapperX<RelationMethodBindingDO>()
                .eq(RelationMethodBindingDO::getSubjectType, subjectType)
                .eq(RelationMethodBindingDO::getSubjectId, subjectId)
                .eq(RelationMethodBindingDO::getDimensionKey, dimensionKey)
                .eq(RelationMethodBindingDO::getDimensionValue, dimensionValue));
    }
}
