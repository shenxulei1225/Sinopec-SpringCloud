package cn.cheers.x.module.dynamicbusiness.dal.mysql.model;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelCategoryRelationDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 模型分类关联 Mapper
 *
 * @author yudao
 */
@Mapper
public interface ModelCategoryRelationMapper extends BaseMapperX<ModelCategoryRelationDO> {

    /**
     * 根据模型ID查询分类关联列表
     */
    default List<ModelCategoryRelationDO> selectByModelId(Long modelId) {
        return selectList(new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                .eq(ModelCategoryRelationDO::getModelId, modelId)
                .eq(ModelCategoryRelationDO::getDeleted, false));
    }

    /**
     * 根据模型ID + 业务类型查询有效分类关联列表。
     */
    default List<ModelCategoryRelationDO> selectByModelIdAndEntityType(Long modelId, String entityTypeCode) {
        return selectList(new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                .eq(ModelCategoryRelationDO::getModelId, modelId)
                .eq(ModelCategoryRelationDO::getEntityTypeCode, entityTypeCode)
                .eq(ModelCategoryRelationDO::getDeleted, false));
    }

    /**
     * 根据分类ID查询模型关联列表
     */
    default List<ModelCategoryRelationDO> selectByCategoryId(Long categoryId) {
        return selectList(new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                .eq(ModelCategoryRelationDO::getCategoryId, categoryId)
                .eq(ModelCategoryRelationDO::getDeleted, false)
                .orderByAsc(ModelCategoryRelationDO::getSort)
                .orderByAsc(ModelCategoryRelationDO::getId));
    }

    /**
     * 根据分类ID + 业务类型查询模型关联列表（分类内按 sort,id 排序）。
     */
    default List<ModelCategoryRelationDO> selectByCategoryIdAndEntityType(Long categoryId, String entityTypeCode) {
        return selectList(new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                .eq(ModelCategoryRelationDO::getCategoryId, categoryId)
                .eq(ModelCategoryRelationDO::getEntityTypeCode, entityTypeCode)
                .eq(ModelCategoryRelationDO::getDeleted, false)
                .orderByAsc(ModelCategoryRelationDO::getSort)
                .orderByAsc(ModelCategoryRelationDO::getId));
    }

    /**
     * 根据模型ID和分类ID查询关联关系
     */
    default ModelCategoryRelationDO selectByModelIdAndCategoryId(Long modelId, Long categoryId, String entityTypeCode) {
        return selectOne(new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                .eq(ModelCategoryRelationDO::getModelId, modelId)
                .eq(ModelCategoryRelationDO::getCategoryId, categoryId)
                .eq(ModelCategoryRelationDO::getEntityTypeCode, entityTypeCode)
                .eq(ModelCategoryRelationDO::getDeleted, false));
    }

    /**
     * 兼容方法：根据模型ID和分类ID查询关联关系（命名与 Entity 侧一致）。
     */
    default ModelCategoryRelationDO selectByModelAndCategory(Long modelId, Long categoryId, String entityTypeCode) {
        return selectByModelIdAndCategoryId(modelId, categoryId, entityTypeCode);
    }

    /**
     * 查询模型在指定分类集合上的关联（包含 deleted=true 记录）。
     */
    @Select("""
            <script>
            SELECT *
            FROM dynamic_model_category_relation
            WHERE model_id = #{modelId}
                AND entity_type_code = #{entityTypeCode}
                AND category_id IN
                <foreach collection='categoryIds' item='id' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
            </script>
            """)
    List<ModelCategoryRelationDO> selectByModelAndCategoryIdsIncludingDeleted(@Param("modelId") Long modelId,
                                                                                @Param("categoryIds") List<Long> categoryIds,
                                                                                @Param("entityTypeCode") String entityTypeCode);

    /**
     * 根据模型ID删除所有分类关联
     * @param modelId 模型ID
     */
    default void deleteByModelId(Long modelId) {
        delete(new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                .eq(ModelCategoryRelationDO::getModelId, modelId));
    }

    /**
     * 根据模型ID + 业务类型删除分类关联。
     */
    default void deleteByModelId(Long modelId, String entityTypeCode) {
        delete(new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                .eq(ModelCategoryRelationDO::getModelId, modelId)
                .eq(ModelCategoryRelationDO::getEntityTypeCode, entityTypeCode));
    }

    /**
     * 根据分类ID删除所有模型关联（全业务）。
     * @param categoryId 分类ID
     */
    default void deleteByCategoryId(Long categoryId) {
        delete(new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                .eq(ModelCategoryRelationDO::getCategoryId, categoryId));
    }

    /**
     * 根据分类ID删除所有模型关联（按业务类型隔离）。
     * @param categoryId 分类ID
     */
    default void deleteByCategoryId(Long categoryId, String entityTypeCode) {
        delete(new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                .eq(ModelCategoryRelationDO::getCategoryId, categoryId)
                .eq(ModelCategoryRelationDO::getEntityTypeCode, entityTypeCode));
    }

    /**
     * 批量删除多个分类的模型关联（全业务）。
     */
    default void deleteByCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        delete(new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                .in(ModelCategoryRelationDO::getCategoryId, categoryIds));
    }

    /**
     * 批量删除多个分类的模型关联（按业务类型隔离）。
     */
    default void deleteByCategoryIds(List<Long> categoryIds, String entityTypeCode) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        delete(new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                .in(ModelCategoryRelationDO::getCategoryId, categoryIds)
                .eq(ModelCategoryRelationDO::getEntityTypeCode, entityTypeCode));
    }

    /**
     * 批量删除特定的模型-分类绑定关系
     *
     * 用于解除某个模型与多个分类的特定绑定，不影响该模型与其他分类的绑定
     *
     * @param modelId 模型ID
     * @param categoryIds 分类ID列表
     */
    default void deleteByModelIdAndCategoryIds(Long modelId, List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        delete(new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                .eq(ModelCategoryRelationDO::getModelId, modelId)
                .in(ModelCategoryRelationDO::getCategoryId, categoryIds));
    }

    /**
     * 按模型ID + 分类列表 + 业务类型删除特定绑定关系。
     */
    default void deleteByModelIdAndCategoryIds(Long modelId, List<Long> categoryIds, String entityTypeCode) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        delete(new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                .eq(ModelCategoryRelationDO::getModelId, modelId)
                .in(ModelCategoryRelationDO::getCategoryId, categoryIds)
                .eq(ModelCategoryRelationDO::getEntityTypeCode, entityTypeCode));
    }

    /**
     * 删除特定的模型-分类绑定关系
     *
     * 用于解除某个模型与某个分类的特定绑定，不影响该模型与其他分类的绑定
     *
     * <p>内部实现：调用批量删除方法，传入单元素列表</p>
     *
     * @param modelId 模型ID
     * @param categoryId 分类ID
     */
    default void deleteByModelIdAndCategoryId(Long modelId, Long categoryId, String entityTypeCode) {
        delete(new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                .eq(ModelCategoryRelationDO::getModelId, modelId)
                .eq(ModelCategoryRelationDO::getCategoryId, categoryId)
                .eq(ModelCategoryRelationDO::getEntityTypeCode, entityTypeCode));
    }

    /**
     * 兼容方法：删除模型与分类关联（命名与 Entity 侧一致，不区分业务类型）。
     */
    default void deleteByModelAndCategory(Long modelId, Long categoryId) {
        delete(new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                .eq(ModelCategoryRelationDO::getModelId, modelId)
                .eq(ModelCategoryRelationDO::getCategoryId, categoryId));
    }

    /**
     * 兼容方法：删除模型与分类关联（命名与 Entity 侧一致，按业务类型过滤）。
     */
    default void deleteByModelAndCategory(Long modelId, Long categoryId, String entityTypeCode) {
        deleteByModelIdAndCategoryId(modelId, categoryId, entityTypeCode);
    }

    /**
     * 兼容方法：按模型ID + 分类ID列表删除关联（命名与 Entity 侧一致）。
     */
    default void deleteByModelAndCategoryIds(Long modelId, List<Long> categoryIds) {
        deleteByModelIdAndCategoryIds(modelId, categoryIds);
    }

    /**
     * 批量删除指定模型的所有分类关联（全业务）。
     */
    default void deleteByModelIds(List<Long> modelIds) {
        if (modelIds == null || modelIds.isEmpty()) {
            return;
        }
        delete(new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                .in(ModelCategoryRelationDO::getModelId, modelIds));
    }

    /**
     * 批量查询模型-分类关联（用于优化批量关联时的 N+1 查询问题）。
     */
    default List<ModelCategoryRelationDO> selectByModelIdsAndCategoryIds(List<Long> modelIds, List<Long> categoryIds) {
        if (modelIds == null || modelIds.isEmpty() || categoryIds == null || categoryIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                .in(ModelCategoryRelationDO::getModelId, modelIds)
                .in(ModelCategoryRelationDO::getCategoryId, categoryIds)
                .eq(ModelCategoryRelationDO::getDeleted, false));
    }

    /**
     * 恢复软删除的模型-分类关联（deleted 从 true 改为 false）。
     */
    @Update("""
            WITH target AS (
                SELECT id
                FROM dynamic_model_category_relation
                WHERE model_id = #{modelId}
                  AND category_id = #{categoryId}
                  AND entity_type_code = #{entityTypeCode}
                  AND deleted = TRUE
                ORDER BY id DESC
                LIMIT 1
            )
            UPDATE dynamic_model_category_relation r
            SET deleted = FALSE,
                sort = #{sort}
            FROM target
            WHERE r.id = target.id
            """)
    int restoreDeletedRelation(@Param("modelId") Long modelId,
                                @Param("categoryId") Long categoryId,
                                @Param("entityTypeCode") String entityTypeCode,
                                @Param("sort") Integer sort);

    /**
     * 批量恢复软删除的模型-分类关联（deleted 从 true 改为 false）。
     */
    @Update("""
            <script>
            WITH candidates AS (
                SELECT id,
                       ROW_NUMBER() OVER (PARTITION BY category_id ORDER BY id DESC) AS rn
                FROM dynamic_model_category_relation
                WHERE model_id = #{modelId}
                  AND entity_type_code = #{entityTypeCode}
                  AND category_id IN
                  <foreach collection='categoryIds' item='id' open='(' separator=',' close=')'>
                      #{id}
                  </foreach>
                  AND deleted = TRUE
            )
            UPDATE dynamic_model_category_relation r
            SET deleted = FALSE
            FROM candidates c
            WHERE r.id = c.id
              AND c.rn = 1
            </script>
            """)
    int restoreDeletedRelationsBatch(@Param("modelId") Long modelId,
                                        @Param("categoryIds") List<Long> categoryIds,
                                        @Param("entityTypeCode") String entityTypeCode);

    /**
     * 更新模型-分类关联的排序值。
     */
    default int updateSortByModelAndCategory(Long modelId, Long categoryId, Integer sort, String entityTypeCode) {
        return update(new LambdaUpdateWrapper<ModelCategoryRelationDO>()
                .set(ModelCategoryRelationDO::getSort, sort)
                .eq(ModelCategoryRelationDO::getModelId, modelId)
                .eq(ModelCategoryRelationDO::getCategoryId, categoryId)
                .eq(ModelCategoryRelationDO::getEntityTypeCode, entityTypeCode)
                .eq(ModelCategoryRelationDO::getDeleted, false));
    }

    /**
     * 批量创建模型-分类关联。
     */
    default void insertBatchRelations(List<ModelCategoryRelationDO> relations) {
        if (relations == null || relations.isEmpty()) {
            return;
        }
        insertBatch(relations);
    }

    /**
     * 批量查询分类当前 max(sort)。
     */
    @Select("""
            <script>
            SELECT category_id AS categoryId, COALESCE(MAX(sort), 0) AS maxSort
            FROM dynamic_model_category_relation
            WHERE deleted = FALSE
                AND entity_type_code = #{entityTypeCode}
                AND category_id IN
                <foreach collection='categoryIds' item='id' open='(' separator=',' close=')'>
                        #{id}
                </foreach>
            GROUP BY category_id
            </script>
            """)
    List<Map<String, Object>> selectMaxSortByCategoryIds(@Param("categoryIds") List<Long> categoryIds,
                                                            @Param("entityTypeCode") String entityTypeCode);

    /**
     * 兼容方法：不显式传 entityTypeCode，按分类聚合 max(sort)。
     */
    default List<Map<String, Object>> selectMaxSortByCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        return selectMaps(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ModelCategoryRelationDO>()
                .select("category_id AS categoryId", "COALESCE(MAX(sort), 0) AS maxSort")
                .in("category_id", categoryIds)
                .eq("deleted", false)
                .groupBy("category_id"));
    }

    /**
     * 查询多分类原始关系记录（用于 Service 层按 categoryIds 顺序二次编排）。
     */
    default List<ModelCategoryRelationDO> selectRelationsByCategoryIdsForOrdering(List<Long> categoryIds, String entityTypeCode) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapperX<ModelCategoryRelationDO> query = new LambdaQueryWrapperX<>();
        query.in(ModelCategoryRelationDO::getCategoryId, categoryIds);
        query.eq(ModelCategoryRelationDO::getDeleted, false);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            query.eq(ModelCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        query.orderByAsc(ModelCategoryRelationDO::getSort);
        query.orderByAsc(ModelCategoryRelationDO::getId);
        return selectList(query);
    }

    /**
     * DB 前置分页（单分类）：按分类内 sort,id 排序后返回当前页 modelId。
     */
    @Select("""
            <script>
            WITH dedup AS (
                SELECT r.model_id,
                        row_number() OVER (
                            PARTITION BY r.model_id
                            ORDER BY r.sort ASC NULLS LAST, r.id ASC
                        ) AS rn,
                        MIN(r.sort) OVER (PARTITION BY r.model_id) AS min_sort,
                        MIN(r.id) OVER (PARTITION BY r.model_id) AS min_id
                FROM dynamic_model_category_relation r
                WHERE r.deleted = FALSE
                    AND r.category_id = #{categoryId}
                <if test='entityTypeCode != null and entityTypeCode != ""'>
                    AND r.entity_type_code = #{entityTypeCode}
                </if>
            )
            SELECT model_id
            FROM dedup
            WHERE rn = 1
            ORDER BY min_sort ASC NULLS LAST, min_id ASC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<Long> selectModelIdsBySingleCategoryPaged(@Param("categoryId") Long categoryId,
                                                    @Param("entityTypeCode") String entityTypeCode,
                                                    @Param("offset") int offset,
                                                    @Param("limit") int limit);

    /**
     * DB 前置分页（单分类）：统计去重后的总模型数量。
     */
    @Select("""
            <script>
            SELECT COUNT(DISTINCT r.model_id)
            FROM dynamic_model_category_relation r
            WHERE r.deleted = FALSE
                AND r.category_id = #{categoryId}
            <if test='entityTypeCode != null and entityTypeCode != ""'>
                AND r.entity_type_code = #{entityTypeCode}
            </if>
            </script>
            """)
    long countModelIdsBySingleCategory(@Param("categoryId") Long categoryId,
                                       @Param("entityTypeCode") String entityTypeCode);

    /**
     * DB 前置分页（多分类）：按 categoryIds 输入顺序(rank) + 分类内 sort,id 生成稳定顺序，去重后返回当前页 modelId。
     */
    @Select("""
            <script>
            WITH input_categories AS (
                SELECT cid AS category_id, ordinality AS rank
                FROM unnest(
                    <foreach collection='categoryIds' item='cid' open='ARRAY[' separator=',' close=']::bigint[]'>
                        #{cid}
                    </foreach>
                ) WITH ORDINALITY AS t(cid, ordinality)
            ),
            relations AS (
                SELECT r.model_id, r.category_id, r.sort, r.id, ic.rank
                FROM dynamic_model_category_relation r
                JOIN input_categories ic ON ic.category_id = r.category_id
                WHERE r.deleted = FALSE
                <if test='entityTypeCode != null and entityTypeCode != ""'>
                    AND r.entity_type_code = #{entityTypeCode}
                </if>
            ),
            dedup AS (
                SELECT model_id, rank, sort, id,
                        row_number() OVER (
                            PARTITION BY model_id
                            ORDER BY rank ASC, sort ASC NULLS LAST, id ASC
                        ) AS rn
                FROM relations
            )
            SELECT model_id
            FROM dedup
            WHERE rn = 1
            ORDER BY rank ASC, sort ASC NULLS LAST, id ASC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<Long> selectModelIdsByCategoryIdsRankPaged(@Param("categoryIds") List<Long> categoryIds,
                                                     @Param("entityTypeCode") String entityTypeCode,
                                                     @Param("offset") int offset,
                                                     @Param("limit") int limit);

    /**
     * DB 前置分页：统计去重后的总模型数量（与 rank 排序规则一致）。
     */
    @Select("""
            <script>
            WITH input_categories AS (
                SELECT cid AS category_id
                FROM unnest(
                    <foreach collection='categoryIds' item='cid' open='ARRAY[' separator=',' close=']::bigint[]'>
                        #{cid}
                    </foreach>
                ) AS t(cid)
            )
            SELECT COUNT(DISTINCT r.model_id)
            FROM dynamic_model_category_relation r
            JOIN input_categories ic ON ic.category_id = r.category_id
            WHERE r.deleted = FALSE
            <if test='entityTypeCode != null and entityTypeCode != ""'>
                AND r.entity_type_code = #{entityTypeCode}
            </if>
            </script>
            """)
    long countModelIdsByCategoryIdsRank(@Param("categoryIds") List<Long> categoryIds,
                                         @Param("entityTypeCode") String entityTypeCode);

    /**
     * 按分类范围和业务类型分页查询 modelId（兼容保留）。
     */
    @Deprecated
    default List<Long> selectModelIdsByCategoryIdsPaged(List<Long> categoryIds, String entityTypeCode,
                                                         int offset, int limit) {
        if (categoryIds == null || categoryIds.isEmpty() || limit <= 0) {
            return new ArrayList<>();
        }
        int current = offset / limit + 1;
        Page<ModelCategoryRelationDO> page = new Page<>(current, limit, false);
        LambdaQueryWrapperX<ModelCategoryRelationDO> query = new LambdaQueryWrapperX<>();
        query.select(ModelCategoryRelationDO::getModelId);
        query.in(ModelCategoryRelationDO::getCategoryId, categoryIds);
        query.eq(ModelCategoryRelationDO::getDeleted, false);
        query.orderByAsc(ModelCategoryRelationDO::getId);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            query.eq(ModelCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        Page<ModelCategoryRelationDO> result = selectPage(page, query);
        return result.getRecords().stream()
                .map(ModelCategoryRelationDO::getModelId)
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}













































