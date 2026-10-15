package cn.cheers.x.module.dynamicbusiness.dal.mysql.model;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelModelRelationDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模型—模型关联。物理表经租户路由为 *_t{tenantId}。
 */
@Mapper
public interface ModelModelRelationMapper extends BaseMapperX<ModelModelRelationDO> {

    default ModelModelRelationDO selectActive(Long sourceModelId, Long targetModelId,
                                              String sourceEntityTypeCode, String targetEntityTypeCode) {
        return selectOne(new LambdaQueryWrapperX<ModelModelRelationDO>()
                .eq(ModelModelRelationDO::getSourceModelId, sourceModelId)
                .eq(ModelModelRelationDO::getTargetModelId, targetModelId)
                .eq(ModelModelRelationDO::getSourceEntityTypeCode, sourceEntityTypeCode)
                .eq(ModelModelRelationDO::getTargetEntityTypeCode, targetEntityTypeCode)
                .eq(ModelModelRelationDO::getDeleted, false));
    }

    default List<ModelModelRelationDO> selectBySource(Long sourceModelId,
                                                      String sourceEntityTypeCode,
                                                      String targetEntityTypeCode) {
        return selectList(new LambdaQueryWrapperX<ModelModelRelationDO>()
                .eq(ModelModelRelationDO::getSourceModelId, sourceModelId)
                .eq(ModelModelRelationDO::getSourceEntityTypeCode, sourceEntityTypeCode)
                .eq(ModelModelRelationDO::getTargetEntityTypeCode, targetEntityTypeCode)
                .eq(ModelModelRelationDO::getDeleted, false)
                .orderByAsc(ModelModelRelationDO::getSort)
                .orderByAsc(ModelModelRelationDO::getId));
    }

    default List<ModelModelRelationDO> selectByTarget(Long targetModelId,
                                                      String targetEntityTypeCode,
                                                      String sourceEntityTypeCode) {
        return selectList(new LambdaQueryWrapperX<ModelModelRelationDO>()
                .eq(ModelModelRelationDO::getTargetModelId, targetModelId)
                .eq(ModelModelRelationDO::getTargetEntityTypeCode, targetEntityTypeCode)
                .eq(ModelModelRelationDO::getSourceEntityTypeCode, sourceEntityTypeCode)
                .eq(ModelModelRelationDO::getDeleted, false)
                .orderByAsc(ModelModelRelationDO::getSort)
                .orderByAsc(ModelModelRelationDO::getId));
    }

    /**
     * 物理删除已挂上的型号—型号关联。没有这一行时删除 0 行。
     */
    @Delete("""
            DELETE FROM dynamic_model_model_relation
            WHERE source_model_id = #{sourceModelId}
              AND target_model_id = #{targetModelId}
              AND source_entity_type_code = #{sourceEntityTypeCode}
              AND target_entity_type_code = #{targetEntityTypeCode}
            """)
    int deleteLink(@Param("sourceModelId") Long sourceModelId,
                   @Param("targetModelId") Long targetModelId,
                   @Param("sourceEntityTypeCode") String sourceEntityTypeCode,
                   @Param("targetEntityTypeCode") String targetEntityTypeCode);

    default Integer maxSort(Long sourceModelId, String sourceEntityTypeCode, String targetEntityTypeCode) {
        List<ModelModelRelationDO> rows = selectList(new LambdaQueryWrapperX<ModelModelRelationDO>()
                .eq(ModelModelRelationDO::getSourceModelId, sourceModelId)
                .eq(ModelModelRelationDO::getSourceEntityTypeCode, sourceEntityTypeCode)
                .eq(ModelModelRelationDO::getTargetEntityTypeCode, targetEntityTypeCode)
                .eq(ModelModelRelationDO::getDeleted, false)
                .orderByDesc(ModelModelRelationDO::getSort)
                .last("LIMIT 1"));
        if (rows == null || rows.isEmpty() || rows.get(0).getSort() == null) {
            return 0;
        }
        return rows.get(0).getSort();
    }
}
