package cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeRelationDO;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EntityTypeRelationMapper extends BaseMapperX<EntityTypeRelationDO> {

    default List<EntityTypeRelationDO> selectBySourceEntityTypeCode(String sourceEntityTypeCode) {
        return selectList(new LambdaQueryWrapperX<EntityTypeRelationDO>()
                .eq(EntityTypeRelationDO::getSourceEntityTypeCode, sourceEntityTypeCode));
    }

    default List<EntityTypeRelationDO> selectByTargetEntityTypeCode(String targetEntityTypeCode) {
        return selectList(new LambdaQueryWrapperX<EntityTypeRelationDO>()
                .eq(EntityTypeRelationDO::getTargetEntityTypeCode, targetEntityTypeCode));
    }

    default boolean existsBySourceAndTarget(String sourceEntityTypeCode, String targetEntityTypeCode) {
        return selectCount(new LambdaQueryWrapperX<EntityTypeRelationDO>()
                .eq(EntityTypeRelationDO::getSourceEntityTypeCode, sourceEntityTypeCode)
                .eq(EntityTypeRelationDO::getTargetEntityTypeCode, targetEntityTypeCode)) > 0;
    }
}
