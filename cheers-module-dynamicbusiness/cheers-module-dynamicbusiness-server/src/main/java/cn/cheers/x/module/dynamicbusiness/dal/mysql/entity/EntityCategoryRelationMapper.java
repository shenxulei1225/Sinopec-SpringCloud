package cn.cheers.x.module.dynamicbusiness.dal.mysql.entity;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityCategoryRelationDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
 * 业务实体与分类关联 Mapper
 */
@Mapper
public interface EntityCategoryRelationMapper extends BaseMapperX<EntityCategoryRelationDO> {

    /**
     * 根据实体ID和分类ID查询关联
     */
    default EntityCategoryRelationDO selectByEntityAndCategory(Long entityId, Long categoryId) {
        return selectOne(new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .eq(EntityCategoryRelationDO::getEntityId, entityId)
                .eq(EntityCategoryRelationDO::getCategoryId, categoryId)
                .eq(EntityCategoryRelationDO::getDeleted, false));
    }

    /**
     * 根据实体ID和分类ID查询关联（按业务类型过滤）。
     */
    default EntityCategoryRelationDO selectByEntityAndCategory(Long entityId, Long categoryId, String entityTypeCode) {
        LambdaQueryWrapperX<EntityCategoryRelationDO> query = new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .eq(EntityCategoryRelationDO::getEntityId, entityId)
                .eq(EntityCategoryRelationDO::getCategoryId, categoryId)
                .eq(EntityCategoryRelationDO::getDeleted, false);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            query.eq(EntityCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        return selectOne(query);
    }

    /**
     * 根据实体ID查询所有关联
     */
    default List<EntityCategoryRelationDO> selectByEntityId(Long entityId) {
        return selectList(new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .eq(EntityCategoryRelationDO::getEntityId, entityId)
                .eq(EntityCategoryRelationDO::getDeleted, false));
    }

    /**
     * 查询实体在指定分类集合上的关联（包含 deleted=true 记录）。
     *
     * <p>须用原生 SQL：{@code @TableLogic} 会拦截 Wrapper 对 deleted=true 的查询。</p>
     */
    @Select("""
            <script>
            SELECT *
            FROM dynamic_entity_category_relation
            WHERE entity_id = #{entityId}
                AND entity_type_code = #{entityTypeCode}
                AND category_id IN
                <foreach collection='categoryIds' item='id' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
                AND deleted = TRUE
            </script>
            """)
    List<EntityCategoryRelationDO> selectByEntityAndCategoryIdsIncludingDeleted(@Param("entityId") Long entityId,
                                                                                @Param("categoryIds") List<Long> categoryIds,
                                                                                @Param("entityTypeCode") String entityTypeCode);

    /**
     * 批量查询「已解除关联」记录（deleted=true），用于模型挂分类路径下的显式排除。
     *
     * <p>须用原生 SQL：{@code @TableLogic} 会拦截 Wrapper 对 deleted=true 的查询。</p>
     */
    @Select("""
            <script>
            SELECT entity_id, category_id
            FROM dynamic_entity_category_relation
            WHERE entity_type_code = #{entityTypeCode}
                AND entity_id IN
                <foreach collection='entityIds' item='eid' open='(' separator=',' close=')'>
                    #{eid}
                </foreach>
                AND category_id IN
                <foreach collection='categoryIds' item='cid' open='(' separator=',' close=')'>
                    #{cid}
                </foreach>
                AND deleted = TRUE
            </script>
            """)
    List<EntityCategoryRelationDO> selectExcludedPairsByEntityIdsAndCategoryIds(@Param("entityIds") List<Long> entityIds,
                                                                                @Param("categoryIds") List<Long> categoryIds,
                                                                                @Param("entityTypeCode") String entityTypeCode);

    /**
     * 根据分类ID查询所有关联（分类内按 sort,id 排序）。
     */
    default List<EntityCategoryRelationDO> selectByCategoryId(Long categoryId) {
        return selectList(new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .eq(EntityCategoryRelationDO::getCategoryId, categoryId)
                .eq(EntityCategoryRelationDO::getDeleted, false)
                // 分类上下文实体列表：优先按 relation.sort，其次按 relation.id 稳定兜底
                .orderByAsc(EntityCategoryRelationDO::getSort)
                .orderByAsc(EntityCategoryRelationDO::getId));
    }

    /**
     * 根据分类ID + 业务类型查询所有关联（分类内按 sort,id 排序）。
     *
     * <p>适用于“单分类”场景，保证分类内排序语义，同时按业务类型过滤。</p>
     */
    default List<EntityCategoryRelationDO> selectByCategoryIdAndEntityType(Long categoryId, String entityTypeCode) {
        LambdaQueryWrapperX<EntityCategoryRelationDO> query = new LambdaQueryWrapperX<EntityCategoryRelationDO>();
        query.eq(EntityCategoryRelationDO::getCategoryId, categoryId);
        query.eq(EntityCategoryRelationDO::getDeleted, false);
        query.orderByAsc(EntityCategoryRelationDO::getSort);
        query.orderByAsc(EntityCategoryRelationDO::getId);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            query.eq(EntityCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        return selectList(query);
    }

    /**
     * 删除实体与分类的关联（不区分业务类型）。
     */
    default void deleteByEntityAndCategory(Long entityId, Long categoryId) {
        delete(new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .eq(EntityCategoryRelationDO::getEntityId, entityId)
                .eq(EntityCategoryRelationDO::getCategoryId, categoryId));
    }

    /**
     * 删除实体与分类的关联（按业务类型过滤）。
     */
    default void deleteByEntityAndCategory(Long entityId, Long categoryId, String entityTypeCode) {
        LambdaQueryWrapperX<EntityCategoryRelationDO> query = new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .eq(EntityCategoryRelationDO::getEntityId, entityId)
                .eq(EntityCategoryRelationDO::getCategoryId, categoryId);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            query.eq(EntityCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        delete(query);
    }

    /**
     * 删除实体的所有关联
     */
    default void deleteByEntityId(Long entityId) {
        delete(new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .eq(EntityCategoryRelationDO::getEntityId, entityId));
    }

    /**
     * 按实体ID + 分类ID列表删除关联。
     */
    default void deleteByEntityAndCategoryIds(Long entityId, List<Long> categoryIds) {
        if (entityId == null || categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        delete(new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .eq(EntityCategoryRelationDO::getEntityId, entityId)
                .in(EntityCategoryRelationDO::getCategoryId, categoryIds));
    }

    /**
     * 删除分类的所有关联（全业务）。
     */
    default void deleteByCategoryId(Long categoryId) {
        delete(new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .eq(EntityCategoryRelationDO::getCategoryId, categoryId));
    }

    /**
     * 按业务类型删除分类的所有关联（按业务类型隔离）。
     */
    default void deleteByCategoryId(Long categoryId, String entityTypeCode) {
        LambdaQueryWrapperX<EntityCategoryRelationDO> query = new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .eq(EntityCategoryRelationDO::getCategoryId, categoryId);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            query.eq(EntityCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        delete(query);
    }

    /**
     * 批量创建实体-分类关联
     */
    default void insertBatchRelations(List<EntityCategoryRelationDO> relations) {
        if (relations == null || relations.isEmpty()) {
            return;
        }
        insertBatch(relations);
    }

    /**
     * 批量删除指定实体的所有分类关联
     */
    default void deleteByEntityIds(List<Long> entityIds) {
        if (entityIds == null || entityIds.isEmpty()) {
            return;
        }
        delete(new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .in(EntityCategoryRelationDO::getEntityId, entityIds));
    }

    /**
     * 批量删除指定分类的所有实体关联（全业务）。
     */
    default void deleteByCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        delete(new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .in(EntityCategoryRelationDO::getCategoryId, categoryIds));
    }

    /**
     * 批量删除指定分类的所有实体关联（按业务类型隔离）。
     */
    default void deleteByCategoryIds(List<Long> categoryIds, String entityTypeCode) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        LambdaQueryWrapperX<EntityCategoryRelationDO> query = new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .in(EntityCategoryRelationDO::getCategoryId, categoryIds);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            query.eq(EntityCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        delete(query);
    }

    /**
     * 恢复软删除的实体-分类关联（deleted=true -> false）。
     *
     * <p>须用原生 SQL：{@code @TableLogic} 会拦截 Wrapper 对 deleted=true 记录的更新。</p>
     */
    @Update("""
            UPDATE dynamic_entity_category_relation
            SET deleted = FALSE,
                sort = #{sort},
                entity_type_code = #{entityTypeCode}
            WHERE entity_id = #{entityId}
                AND category_id = #{categoryId}
                AND deleted = TRUE
            """)
    int restoreDeletedRelation(@Param("entityId") Long entityId,
                               @Param("categoryId") Long categoryId,
                               @Param("entityTypeCode") String entityTypeCode,
                               @Param("sort") Integer sort);

    /**
     * 批量恢复软删除的实体-分类关联（deleted=true -> false）。
     *
     * <p>须用原生 SQL：{@code @TableLogic} 会拦截 Wrapper 对 deleted=true 记录的更新。</p>
     */
    @Update("""
            <script>
            UPDATE dynamic_entity_category_relation
            SET deleted = FALSE,
                entity_type_code = #{entityTypeCode}
            WHERE entity_id = #{entityId}
                AND entity_type_code = #{entityTypeCode}
                AND category_id IN
                <foreach collection='categoryIds' item='id' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
                AND deleted = TRUE
            </script>
            """)
    int restoreDeletedRelationsBatch(@Param("entityId") Long entityId,
                                     @Param("categoryIds") List<Long> categoryIds,
                                     @Param("entityTypeCode") String entityTypeCode);

    /**
     * 更新实体-分类关联的排序值。
     */
    default int updateSortByEntityAndCategory(Long entityId, Long categoryId, Integer sort, String entityTypeCode) {
        LambdaUpdateWrapper<EntityCategoryRelationDO> update = new LambdaUpdateWrapper<EntityCategoryRelationDO>()
                .set(EntityCategoryRelationDO::getSort, sort)
                .eq(EntityCategoryRelationDO::getEntityId, entityId)
                .eq(EntityCategoryRelationDO::getCategoryId, categoryId)
                .eq(EntityCategoryRelationDO::getDeleted, false);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            update.eq(EntityCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        return update(update);
    }

    /**
     * 批量查询实体-分类关联（用于优化批量关联时的 N+1 查询问题）。
     *
     * <p>说明：本方法用于“关系存在性校验”场景，不用于排序输出场景。</p>
     *
     * @param entityIds 实体ID列表
     * @param categoryIds 分类ID列表
     * @return 关联列表
     */
    default List<EntityCategoryRelationDO> selectByEntityIdsAndCategoryIds(List<Long> entityIds, List<Long> categoryIds) {
        if (entityIds == null || entityIds.isEmpty() || categoryIds == null || categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        return selectList(new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .in(EntityCategoryRelationDO::getEntityId, entityIds)
                .in(EntityCategoryRelationDO::getCategoryId, categoryIds)
                .eq(EntityCategoryRelationDO::getDeleted, false));
    }

    /**
     * 根据分类ID列表查询实体ID列表（去重）。
     *
     * <p>注意：本方法不保证按 categoryIds 输入顺序排序，不能用于多分类有序输出场景。</p>
     */
    @Deprecated
    default List<Long> selectEntityIdsByCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<EntityCategoryRelationDO> relations = selectList(new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .select(EntityCategoryRelationDO::getEntityId)
                .in(EntityCategoryRelationDO::getCategoryId, categoryIds)
                .eq(EntityCategoryRelationDO::getDeleted, false));
        return relations.stream()
                .map(EntityCategoryRelationDO::getEntityId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();
    }

    /**
     * 批量查询分类的 max(sort)（用于批量分配 sort）。
     */
    default List<Map<String, Object>> selectMaxSortByCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        return selectMaps(new QueryWrapper<EntityCategoryRelationDO>()
                .select("category_id AS categoryId", "COALESCE(MAX(sort), 0) AS maxSort")
                .in("category_id", categoryIds)
                .eq("deleted", false)
                .groupBy("category_id"));
    }

    /**
     * 根据分类ID列表查询关联（包含排序字段）。
     * 废弃原因：不能保证“先 categoryIds 输入顺序，再分类内 sort”语义。
     * <p>注意：当前仅保证“全局 sort,id”排序，不能保证“先 categoryIds 输入顺序，再分类内 sort”语义。</p>
     * <p>多分类稳定有序输出应在 Service 层按 categoryIds 顺序二次编排。</p>
     */
    @Deprecated
    default List<EntityCategoryRelationDO> selectRelationsByCategoryIds(List<Long> categoryIds, String entityTypeCode) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapperX<EntityCategoryRelationDO> query = new LambdaQueryWrapperX<>();
        query.in(EntityCategoryRelationDO::getCategoryId, categoryIds);
        query.eq(EntityCategoryRelationDO::getDeleted, false);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            query.eq(EntityCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        // 分类上下文排序优先，其次按ID兜底
        query.orderByAsc(EntityCategoryRelationDO::getSort);
        query.orderByAsc(EntityCategoryRelationDO::getId);
        return selectList(query);
    }

    /**
     * 查询多分类原始关系记录（用于 Service 层按 categoryIds 顺序二次编排）。
     */
    default List<EntityCategoryRelationDO> selectRelationsByCategoryIdsForOrdering(List<Long> categoryIds, String entityTypeCode) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapperX<EntityCategoryRelationDO> query = new LambdaQueryWrapperX<>();
        query.in(EntityCategoryRelationDO::getCategoryId, categoryIds);
        query.eq(EntityCategoryRelationDO::getDeleted, false);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            query.eq(EntityCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        // 这里仅提供稳定基础顺序，最终顺序由 Service 层按输入 categoryIds 决定
        query.orderByAsc(EntityCategoryRelationDO::getSort);
        query.orderByAsc(EntityCategoryRelationDO::getId);
        return selectList(query);
    }

    /**
     * 按分类范围和业务类型分页查询实体ID（MyBatis-Plus Wrapper 版）。
     *
     * <p>注意：本方法分页结果不保证按 categoryIds 输入顺序排序，
     * 仅用于非稳定排序场景。</p>
     */
    @Deprecated
    default List<Long> selectEntityIdsByCategoryIdsPaged(List<Long> categoryIds, String entityTypeCode,
                                                        int offset, int limit) {
        if (categoryIds == null || categoryIds.isEmpty() || limit <= 0) {
            return new ArrayList<>();
        }
        int current = offset / limit + 1;
        Page<EntityCategoryRelationDO> page = new Page<>(current, limit, false);
        LambdaQueryWrapperX<EntityCategoryRelationDO> query = new LambdaQueryWrapperX<>();
        query.select(EntityCategoryRelationDO::getEntityId);
        query.in(EntityCategoryRelationDO::getCategoryId, categoryIds);
        query.eq(EntityCategoryRelationDO::getDeleted, false);
        query.orderByAsc(EntityCategoryRelationDO::getId);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            query.eq(EntityCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        Page<EntityCategoryRelationDO> result = selectPage(page, query);
        return result.getRecords().stream()
                .map(EntityCategoryRelationDO::getEntityId)
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    /**
     * 按分类范围和业务类型统计实体关联数量（MyBatis-Plus Wrapper 版）
     */
    default long countEntityIdsByCategoryIds(List<Long> categoryIds, String entityTypeCode) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return 0L;
        }
        LambdaQueryWrapperX<EntityCategoryRelationDO> query = new LambdaQueryWrapperX<>();
        query.in(EntityCategoryRelationDO::getCategoryId, categoryIds);
        query.eq(EntityCategoryRelationDO::getDeleted, false);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            query.eq(EntityCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        return selectCount(query);
    }
}
