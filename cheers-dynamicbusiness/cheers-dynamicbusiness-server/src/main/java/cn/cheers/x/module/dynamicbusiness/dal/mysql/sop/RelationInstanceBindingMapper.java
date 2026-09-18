package cn.cheers.x.module.dynamicbusiness.dal.mysql.sop;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.RelationInstanceBindingDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RelationInstanceBindingMapper extends BaseMapperX<RelationInstanceBindingDO> {

    default RelationInstanceBindingDO selectByIdentity(
            String hostType,
            Long hostId,
            String subjectType,
            Long subjectId,
            String dimensionKey,
            String dimensionValue) {
        return selectOne(new LambdaQueryWrapperX<RelationInstanceBindingDO>()
                .eq(RelationInstanceBindingDO::getHostType, hostType)
                .eq(RelationInstanceBindingDO::getHostId, hostId)
                .eq(RelationInstanceBindingDO::getSubjectType, subjectType)
                .eq(RelationInstanceBindingDO::getSubjectId, subjectId)
                .eq(RelationInstanceBindingDO::getDimensionKey, dimensionKey)
                .eq(RelationInstanceBindingDO::getDimensionValue, dimensionValue));
    }

    default List<RelationInstanceBindingDO> selectByHostAndSubject(
            String hostType, Long hostId, String subjectType, Long subjectId) {
        return selectList(new LambdaQueryWrapperX<RelationInstanceBindingDO>()
                .eq(RelationInstanceBindingDO::getHostType, hostType)
                .eq(RelationInstanceBindingDO::getHostId, hostId)
                .eq(RelationInstanceBindingDO::getSubjectType, subjectType)
                .eq(RelationInstanceBindingDO::getSubjectId, subjectId)
                .orderByAsc(RelationInstanceBindingDO::getDimensionKey)
                .orderByAsc(RelationInstanceBindingDO::getDimensionValue));
    }

    default List<RelationInstanceBindingDO> selectByTarget(String targetType, Long targetId) {
        return selectList(new LambdaQueryWrapperX<RelationInstanceBindingDO>()
                .eq(RelationInstanceBindingDO::getTargetType, targetType)
                .eq(RelationInstanceBindingDO::getTargetId, targetId));
    }
}
