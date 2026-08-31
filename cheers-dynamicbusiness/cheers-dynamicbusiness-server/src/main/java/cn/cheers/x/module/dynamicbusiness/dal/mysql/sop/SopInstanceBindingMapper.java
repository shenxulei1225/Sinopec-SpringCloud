package cn.cheers.x.module.dynamicbusiness.dal.mysql.sop;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.SopInstanceBindingDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SopInstanceBindingMapper extends BaseMapperX<SopInstanceBindingDO> {

    default SopInstanceBindingDO selectByIdentity(
            String hostType,
            Long hostId,
            String subjectType,
            Long subjectId,
            String dimensionKey,
            String dimensionValue) {
        return selectOne(new LambdaQueryWrapperX<SopInstanceBindingDO>()
                .eq(SopInstanceBindingDO::getHostType, hostType)
                .eq(SopInstanceBindingDO::getHostId, hostId)
                .eq(SopInstanceBindingDO::getSubjectType, subjectType)
                .eq(SopInstanceBindingDO::getSubjectId, subjectId)
                .eq(SopInstanceBindingDO::getDimensionKey, dimensionKey)
                .eq(SopInstanceBindingDO::getDimensionValue, dimensionValue));
    }

    default List<SopInstanceBindingDO> selectByHostAndSubject(
            String hostType, Long hostId, String subjectType, Long subjectId) {
        return selectList(new LambdaQueryWrapperX<SopInstanceBindingDO>()
                .eq(SopInstanceBindingDO::getHostType, hostType)
                .eq(SopInstanceBindingDO::getHostId, hostId)
                .eq(SopInstanceBindingDO::getSubjectType, subjectType)
                .eq(SopInstanceBindingDO::getSubjectId, subjectId)
                .orderByAsc(SopInstanceBindingDO::getDimensionKey)
                .orderByAsc(SopInstanceBindingDO::getDimensionValue));
    }

    default List<SopInstanceBindingDO> selectBySopInstanceId(Long sopInstanceId) {
        return selectList(new LambdaQueryWrapperX<SopInstanceBindingDO>()
                .eq(SopInstanceBindingDO::getSopInstanceId, sopInstanceId));
    }
}
