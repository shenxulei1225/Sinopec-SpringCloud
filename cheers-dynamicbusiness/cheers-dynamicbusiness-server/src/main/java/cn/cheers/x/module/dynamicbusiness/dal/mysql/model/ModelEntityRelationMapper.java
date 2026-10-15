package cn.cheers.x.module.dynamicbusiness.dal.mysql.model;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelEntityRelationDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 型号—实体多对多关联 Mapper（物理表经 Tenant 路由为 *_t{tenantId}）。
 */
@Mapper
public interface ModelEntityRelationMapper extends BaseMapperX<ModelEntityRelationDO> {

    default ModelEntityRelationDO selectActive(Long modelId, Long entityId,
                                               String modelEntityTypeCode, String entityTypeCode) {
        return selectOne(new LambdaQueryWrapperX<ModelEntityRelationDO>()
                .eq(ModelEntityRelationDO::getModelId, modelId)
                .eq(ModelEntityRelationDO::getEntityId, entityId)
                .eq(ModelEntityRelationDO::getModelEntityTypeCode, modelEntityTypeCode)
                .eq(ModelEntityRelationDO::getEntityTypeCode, entityTypeCode)
                .eq(ModelEntityRelationDO::getDeleted, false));
    }

    default List<ModelEntityRelationDO> selectByModel(Long modelId, String modelEntityTypeCode,
                                                      String entityTypeCode) {
        return selectList(new LambdaQueryWrapperX<ModelEntityRelationDO>()
                .eq(ModelEntityRelationDO::getModelId, modelId)
                .eq(ModelEntityRelationDO::getModelEntityTypeCode, modelEntityTypeCode)
                .eq(ModelEntityRelationDO::getEntityTypeCode, entityTypeCode)
                .eq(ModelEntityRelationDO::getDeleted, false)
                .orderByAsc(ModelEntityRelationDO::getSort)
                .orderByAsc(ModelEntityRelationDO::getId));
    }

    default List<ModelEntityRelationDO> selectByModels(Collection<Long> modelIds, String modelEntityTypeCode,
                                                       String entityTypeCode) {
        return selectList(new LambdaQueryWrapperX<ModelEntityRelationDO>()
                .in(ModelEntityRelationDO::getModelId, modelIds)
                .eq(ModelEntityRelationDO::getModelEntityTypeCode, modelEntityTypeCode)
                .eq(ModelEntityRelationDO::getEntityTypeCode, entityTypeCode)
                .eq(ModelEntityRelationDO::getDeleted, false)
                .orderByAsc(ModelEntityRelationDO::getModelId)
                .orderByAsc(ModelEntityRelationDO::getSort)
                .orderByAsc(ModelEntityRelationDO::getId));
    }

    default List<ModelEntityRelationDO> selectByEntity(Long entityId, String entityTypeCode,
                                                       String modelEntityTypeCode) {
        LambdaQueryWrapperX<ModelEntityRelationDO> q = new LambdaQueryWrapperX<ModelEntityRelationDO>()
                .eq(ModelEntityRelationDO::getEntityId, entityId)
                .eq(ModelEntityRelationDO::getEntityTypeCode, entityTypeCode)
                .eq(ModelEntityRelationDO::getDeleted, false)
                .orderByAsc(ModelEntityRelationDO::getSort)
                .orderByAsc(ModelEntityRelationDO::getId);
        if (modelEntityTypeCode != null && !modelEntityTypeCode.isBlank()) {
            q.eq(ModelEntityRelationDO::getModelEntityTypeCode, modelEntityTypeCode);
        }
        return selectList(q);
    }

    /**
     * 物理删除已挂上的型号—实体关联。没有这一行时删除 0 行，不另插。
     */
    @Delete("""
            DELETE FROM dynamic_model_entity_relation
            WHERE model_id = #{modelId}
              AND entity_id = #{entityId}
              AND model_entity_type_code = #{modelEntityTypeCode}
              AND entity_type_code = #{entityTypeCode}
            """)
    int deleteLink(@Param("modelId") Long modelId,
                   @Param("entityId") Long entityId,
                   @Param("modelEntityTypeCode") String modelEntityTypeCode,
                   @Param("entityTypeCode") String entityTypeCode);

    default Integer maxSort(Long modelId, String modelEntityTypeCode, String entityTypeCode) {
        List<ModelEntityRelationDO> rows = selectList(new LambdaQueryWrapperX<ModelEntityRelationDO>()
                .eq(ModelEntityRelationDO::getModelId, modelId)
                .eq(ModelEntityRelationDO::getModelEntityTypeCode, modelEntityTypeCode)
                .eq(ModelEntityRelationDO::getEntityTypeCode, entityTypeCode)
                .eq(ModelEntityRelationDO::getDeleted, false)
                .orderByDesc(ModelEntityRelationDO::getSort)
                .last("LIMIT 1"));
        if (rows == null || rows.isEmpty() || rows.get(0).getSort() == null) {
            return 0;
        }
        return rows.get(0).getSort();
    }
}
