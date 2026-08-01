package cn.cheers.x.module.dynamicbusiness.dal.mysql.model;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelEntityRelationDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
     * 含软删行：用于恢复，避免撞唯一键。须原生 SQL（TableLogic 会挡 deleted=true）。
     */
    @Select("""
            SELECT *
            FROM dynamic_model_entity_relation
            WHERE model_id = #{modelId}
              AND entity_id = #{entityId}
              AND model_entity_type_code = #{modelEntityTypeCode}
              AND entity_type_code = #{entityTypeCode}
            LIMIT 1
            """)
    ModelEntityRelationDO selectIncludingDeleted(@Param("modelId") Long modelId,
                                                 @Param("entityId") Long entityId,
                                                 @Param("modelEntityTypeCode") String modelEntityTypeCode,
                                                 @Param("entityTypeCode") String entityTypeCode);

    @Update("""
            UPDATE dynamic_model_entity_relation
            SET deleted = FALSE,
                domain = #{domain},
                sort = #{sort},
                update_time = CURRENT_TIMESTAMP
            WHERE model_id = #{modelId}
              AND entity_id = #{entityId}
              AND model_entity_type_code = #{modelEntityTypeCode}
              AND entity_type_code = #{entityTypeCode}
              AND deleted = TRUE
            """)
    int restoreDeleted(@Param("modelId") Long modelId,
                       @Param("entityId") Long entityId,
                       @Param("modelEntityTypeCode") String modelEntityTypeCode,
                       @Param("entityTypeCode") String entityTypeCode,
                       @Param("domain") String domain,
                       @Param("sort") Integer sort);

    default int softDelete(Long modelId, Long entityId, String modelEntityTypeCode, String entityTypeCode) {
        return update(null, new LambdaUpdateWrapper<ModelEntityRelationDO>()
                .eq(ModelEntityRelationDO::getModelId, modelId)
                .eq(ModelEntityRelationDO::getEntityId, entityId)
                .eq(ModelEntityRelationDO::getModelEntityTypeCode, modelEntityTypeCode)
                .eq(ModelEntityRelationDO::getEntityTypeCode, entityTypeCode)
                .eq(ModelEntityRelationDO::getDeleted, false)
                .set(ModelEntityRelationDO::getDeleted, true));
    }

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
