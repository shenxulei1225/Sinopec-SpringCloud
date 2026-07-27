package cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytypescope;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytypescope.EntityTypeScopeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mapper
public interface EntityTypeScopeMapper extends BaseMapperX<EntityTypeScopeDO> {

    default EntityTypeScopeDO selectByCodeAndEntityId(String entityTypeCode, Long entityId) {
        return selectOne(new LambdaQueryWrapperX<EntityTypeScopeDO>()
                .eq(EntityTypeScopeDO::getEntityTypeCode, entityTypeCode)
                .eq(EntityTypeScopeDO::getEntityId, entityId)
                .eq(EntityTypeScopeDO::getDeleted, false));
    }

    default List<EntityTypeScopeDO> selectByCode(String entityTypeCode) {
        return selectList(new LambdaQueryWrapperX<EntityTypeScopeDO>()
                .eq(EntityTypeScopeDO::getEntityTypeCode, entityTypeCode)
                .eq(EntityTypeScopeDO::getDeleted, false)
                .orderByAsc(EntityTypeScopeDO::getId));
    }

    default List<Long> selectEntityIdsByCode(String entityTypeCode) {
        return selectByCode(entityTypeCode).stream()
                .map(EntityTypeScopeDO::getEntityId)
                .toList();
    }

    default List<EntityTypeScopeDO> selectByCodeAndEntityIds(
            String entityTypeCode, Collection<Long> entityIds) {
        if (entityIds == null || entityIds.isEmpty()) {
            return new ArrayList<>();
        }
        return selectList(new LambdaQueryWrapperX<EntityTypeScopeDO>()
                .eq(EntityTypeScopeDO::getEntityTypeCode, entityTypeCode)
                .in(EntityTypeScopeDO::getEntityId, entityIds)
                .eq(EntityTypeScopeDO::getDeleted, false));
    }

    /** 实体删除时级联：按实体 id 软删所有划分行。 */
    default void deleteByEntityId(Long entityId) {
        if (entityId == null) {
            return;
        }
        delete(new LambdaQueryWrapperX<EntityTypeScopeDO>()
                .eq(EntityTypeScopeDO::getEntityId, entityId));
    }

    /** 仅删除指定划分入口下的实体成员行。 */
    default void deleteByEntityIdAndCodes(Long entityId, Collection<String> entityTypeCodes) {
        if (entityId == null || entityTypeCodes == null || entityTypeCodes.isEmpty()) {
            return;
        }
        delete(new LambdaQueryWrapperX<EntityTypeScopeDO>()
                .eq(EntityTypeScopeDO::getEntityId, entityId)
                .in(EntityTypeScopeDO::getEntityTypeCode, entityTypeCodes));
    }
}
