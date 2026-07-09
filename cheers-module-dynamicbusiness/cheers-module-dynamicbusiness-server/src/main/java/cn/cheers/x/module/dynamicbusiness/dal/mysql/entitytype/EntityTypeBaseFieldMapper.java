package cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EntityTypeBaseFieldMapper extends BaseMapperX<EntityTypeBaseFieldDO> {

    default List<EntityTypeBaseFieldDO> selectByEntityTypeCode(String entityTypeCode) {
        return selectList(new LambdaQueryWrapperX<EntityTypeBaseFieldDO>()
                .eq(EntityTypeBaseFieldDO::getEntityTypeCode, entityTypeCode)
                .eq(EntityTypeBaseFieldDO::getStatus, 1)
                .orderByAsc(EntityTypeBaseFieldDO::getSortOrder));
    }

    default List<EntityTypeBaseFieldDO> selectAllByEntityTypeCode(String entityTypeCode) {
        return selectList(new LambdaQueryWrapperX<EntityTypeBaseFieldDO>()
                .eq(EntityTypeBaseFieldDO::getEntityTypeCode, entityTypeCode)
                .orderByAsc(EntityTypeBaseFieldDO::getSortOrder));
    }

    default EntityTypeBaseFieldDO selectByEntityTypeCodeAndFieldCode(String entityTypeCode, String fieldCode) {
        return selectOne(new LambdaQueryWrapperX<EntityTypeBaseFieldDO>()
                .eq(EntityTypeBaseFieldDO::getEntityTypeCode, entityTypeCode)
                .eq(EntityTypeBaseFieldDO::getFieldCode, fieldCode));
    }

    default boolean existsByFieldCode(String entityTypeCode, String fieldCode, Long excludeId) {
        return selectCount(new LambdaQueryWrapperX<EntityTypeBaseFieldDO>()
                .eq(EntityTypeBaseFieldDO::getEntityTypeCode, entityTypeCode)
                .eq(EntityTypeBaseFieldDO::getFieldCode, fieldCode)
                .neIfPresent(EntityTypeBaseFieldDO::getId, excludeId)) > 0;
    }

    default Integer selectMaxSortOrder(String entityTypeCode) {
        EntityTypeBaseFieldDO field = selectOne(new LambdaQueryWrapperX<EntityTypeBaseFieldDO>()
                .eq(EntityTypeBaseFieldDO::getEntityTypeCode, entityTypeCode)
                .orderByDesc(EntityTypeBaseFieldDO::getSortOrder)
                .last("LIMIT 1"));
        return field == null ? 0 : field.getSortOrder();
    }

    default Long countByEntityTypeCode(String entityTypeCode) {
        return selectCount(new LambdaQueryWrapperX<EntityTypeBaseFieldDO>()
                .eq(EntityTypeBaseFieldDO::getEntityTypeCode, entityTypeCode)
                .eq(EntityTypeBaseFieldDO::getStatus, 1));
    }
}
