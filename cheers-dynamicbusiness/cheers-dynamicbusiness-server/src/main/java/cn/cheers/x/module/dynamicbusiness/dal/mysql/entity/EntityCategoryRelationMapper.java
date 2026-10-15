package cn.cheers.x.module.dynamicbusiness.dal.mysql.entity;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityCategoryRelationDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Delete;
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
        return selectByCategoryIdAndEntityType(categoryId, entityTypeCode, null);
    }

    /**
     * 根据分类ID + 实际存储类型 + 业务域查询所有关联（分类内按 sort,id 排序）。
     *
     * <p>{@code domain} 为空表示不限业务域（总账视角）。</p>
     */
    default List<EntityCategoryRelationDO> selectByCategoryIdAndEntityType(Long categoryId, String entityTypeCode,
                                                                          String domain) {
        LambdaQueryWrapperX<EntityCategoryRelationDO> query = new LambdaQueryWrapperX<EntityCategoryRelationDO>();
        query.eq(EntityCategoryRelationDO::getCategoryId, categoryId);
        query.eq(EntityCategoryRelationDO::getDeleted, false);
        query.orderByAsc(EntityCategoryRelationDO::getSort);
        query.orderByAsc(EntityCategoryRelationDO::getId);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            query.eq(EntityCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        if (domain != null && !domain.isBlank()) {
            query.eq(EntityCategoryRelationDO::getDomain, domain);
        }
        return selectList(query);
    }

    /**
     * 物理删除实体与分类的关联（不区分业务类型）。
     * 框架的 delete 只会把行标成不挂，这里用 DELETE 把行去掉。
     */
    default void deleteByEntityAndCategory(Long entityId, Long categoryId) {
        physicalDeleteByEntityAndCategory(entityId, categoryId);
    }

    /**
     * 物理删除实体与分类的关联（按业务类型过滤）。
     */
    default void deleteByEntityAndCategory(Long entityId, Long categoryId, String entityTypeCode) {
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            physicalDeleteByEntityAndCategoryAndType(entityId, categoryId, entityTypeCode);
            return;
        }
        physicalDeleteByEntityAndCategory(entityId, categoryId);
    }

    /**
     * 按分类 + 多实体物理删除已挂关联。没有挂上的实体不写行。
     */
    default void deleteByCategoryIdAndEntityIds(Long categoryId, List<Long> entityIds, String entityTypeCode) {
        if (categoryId == null || entityIds == null || entityIds.isEmpty()) {
            return;
        }
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            physicalDeleteByCategoryAndEntityIdsAndType(categoryId, entityIds, entityTypeCode);
            return;
        }
        physicalDeleteByCategoryAndEntityIds(categoryId, entityIds);
    }

    /**
     * 物理删除实体的所有分类关联。
     */
    default void deleteByEntityId(Long entityId) {
        physicalDeleteByEntityId(entityId);
    }

    /** 按实体 + 业务类型物理删除分类关联。 */
    default void deleteByEntityId(Long entityId, String entityTypeCode) {
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            physicalDeleteByEntityIdAndType(entityId, entityTypeCode);
            return;
        }
        physicalDeleteByEntityId(entityId);
    }

    /**
     * 按实体 + 分类列表物理删除关联。
     */
    default void deleteByEntityAndCategoryIds(Long entityId, List<Long> categoryIds) {
        if (entityId == null || categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        physicalDeleteByEntityAndCategoryIds(entityId, categoryIds);
    }

    /**
     * 物理删除分类的所有实体关联（全业务）。
     */
    default void deleteByCategoryId(Long categoryId) {
        physicalDeleteByCategoryId(categoryId);
    }

    /**
     * 按业务类型物理删除分类的所有实体关联。
     */
    default void deleteByCategoryId(Long categoryId, String entityTypeCode) {
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            physicalDeleteByCategoryIdAndType(categoryId, entityTypeCode);
            return;
        }
        physicalDeleteByCategoryId(categoryId);
    }

    @Delete("""
            DELETE FROM dynamic_entity_category_relation
            WHERE entity_id = #{entityId} AND category_id = #{categoryId}
            """)
    int physicalDeleteByEntityAndCategory(@Param("entityId") Long entityId, @Param("categoryId") Long categoryId);

    @Delete("""
            DELETE FROM dynamic_entity_category_relation
            WHERE entity_id = #{entityId}
                AND category_id = #{categoryId}
                AND entity_type_code = #{entityTypeCode}
            """)
    int physicalDeleteByEntityAndCategoryAndType(@Param("entityId") Long entityId,
                                                 @Param("categoryId") Long categoryId,
                                                 @Param("entityTypeCode") String entityTypeCode);

    @Delete("""
            <script>
            DELETE FROM dynamic_entity_category_relation
            WHERE category_id = #{categoryId}
                AND entity_id IN
                <foreach collection='entityIds' item='id' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
            </script>
            """)
    int physicalDeleteByCategoryAndEntityIds(@Param("categoryId") Long categoryId,
                                             @Param("entityIds") List<Long> entityIds);

    @Delete("""
            <script>
            DELETE FROM dynamic_entity_category_relation
            WHERE category_id = #{categoryId}
                AND entity_type_code = #{entityTypeCode}
                AND entity_id IN
                <foreach collection='entityIds' item='id' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
            </script>
            """)
    int physicalDeleteByCategoryAndEntityIdsAndType(@Param("categoryId") Long categoryId,
                                                    @Param("entityIds") List<Long> entityIds,
                                                    @Param("entityTypeCode") String entityTypeCode);

    @Delete("""
            DELETE FROM dynamic_entity_category_relation
            WHERE entity_id = #{entityId}
            """)
    int physicalDeleteByEntityId(@Param("entityId") Long entityId);

    @Delete("""
            DELETE FROM dynamic_entity_category_relation
            WHERE entity_id = #{entityId} AND entity_type_code = #{entityTypeCode}
            """)
    int physicalDeleteByEntityIdAndType(@Param("entityId") Long entityId,
                                        @Param("entityTypeCode") String entityTypeCode);

    @Delete("""
            <script>
            DELETE FROM dynamic_entity_category_relation
            WHERE entity_id = #{entityId}
                AND category_id IN
                <foreach collection='categoryIds' item='id' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
            </script>
            """)
    int physicalDeleteByEntityAndCategoryIds(@Param("entityId") Long entityId,
                                             @Param("categoryIds") List<Long> categoryIds);

    @Delete("""
            DELETE FROM dynamic_entity_category_relation
            WHERE category_id = #{categoryId}
            """)
    int physicalDeleteByCategoryId(@Param("categoryId") Long categoryId);

    @Delete("""
            DELETE FROM dynamic_entity_category_relation
            WHERE category_id = #{categoryId} AND entity_type_code = #{entityTypeCode}
            """)
    int physicalDeleteByCategoryIdAndType(@Param("categoryId") Long categoryId,
                                          @Param("entityTypeCode") String entityTypeCode);

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
     * 统计这些实体在指定存储类型下的有效分类关联数（业务域迁移的影响预览用）。
     */
    default long countByEntityIds(List<Long> entityIds, String entityTypeCode) {
        if (entityIds == null || entityIds.isEmpty()) {
            return 0L;
        }
        LambdaQueryWrapperX<EntityCategoryRelationDO> query = new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .in(EntityCategoryRelationDO::getEntityId, entityIds)
                .eq(EntityCategoryRelationDO::getDeleted, false);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            query.eq(EntityCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        return selectCount(query);
    }

    /**
     * 批量物理删除指定实体的所有分类关联。
     */
    default void deleteByEntityIds(List<Long> entityIds) {
        if (entityIds == null || entityIds.isEmpty()) {
            return;
        }
        physicalDeleteByEntityIds(entityIds);
    }

    @Delete("""
            <script>
            DELETE FROM dynamic_entity_category_relation
            WHERE entity_id IN
            <foreach collection='entityIds' item='id' open='(' separator=',' close=')'>
                #{id}
            </foreach>
            </script>
            """)
    int physicalDeleteByEntityIds(@Param("entityIds") List<Long> entityIds);

    /**
     * 按多个分类 ID 查询有效关联（删分类级联清 REF 前回写用）。
     */
    default List<EntityCategoryRelationDO> selectByCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .in(EntityCategoryRelationDO::getCategoryId, categoryIds)
                .eq(EntityCategoryRelationDO::getDeleted, false));
    }

    /**
     * 按多个分类 ID + 存储类型查询有效关联。
     */
    default List<EntityCategoryRelationDO> selectByCategoryIds(List<Long> categoryIds, String entityTypeCode) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapperX<EntityCategoryRelationDO> query = new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .in(EntityCategoryRelationDO::getCategoryId, categoryIds)
                .eq(EntityCategoryRelationDO::getDeleted, false);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            query.eq(EntityCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        return selectList(query);
    }

    /**
     * 批量物理删除指定分类的所有实体关联（全业务）。
     */
    default void deleteByCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        physicalDeleteByCategoryIds(categoryIds);
    }

    /**
     * 批量物理删除指定分类的所有实体关联（按业务类型隔离）。
     */
    default void deleteByCategoryIds(List<Long> categoryIds, String entityTypeCode) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            physicalDeleteByCategoryIdsAndType(categoryIds, entityTypeCode);
            return;
        }
        physicalDeleteByCategoryIds(categoryIds);
    }

    @Delete("""
            <script>
            DELETE FROM dynamic_entity_category_relation
            WHERE category_id IN
            <foreach collection='categoryIds' item='id' open='(' separator=',' close=')'>
                #{id}
            </foreach>
            </script>
            """)
    int physicalDeleteByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    @Delete("""
            <script>
            DELETE FROM dynamic_entity_category_relation
            WHERE entity_type_code = #{entityTypeCode}
                AND category_id IN
                <foreach collection='categoryIds' item='id' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
            </script>
            """)
    int physicalDeleteByCategoryIdsAndType(@Param("categoryIds") List<Long> categoryIds,
                                           @Param("entityTypeCode") String entityTypeCode);

    /**
     * 把实体当前业务域抄到仍挂着的分类关联上。
     * 只更新还挂着的行。解除后的行已经物理删除，不再跟随。
     */
    @Update("""
            <script>
            UPDATE dynamic_entity_category_relation
            SET domain = #{domain}
            WHERE entity_type_code = #{entityTypeCode}
                AND deleted = FALSE
                AND entity_id IN
                <foreach collection='entityIds' item='eid' open='(' separator=',' close=')'>
                    #{eid}
                </foreach>
                AND domain IS DISTINCT FROM #{domain}
            </script>
            """)
    int updateDomainByEntityIds(@Param("entityIds") List<Long> entityIds,
                                @Param("entityTypeCode") String entityTypeCode,
                                @Param("domain") String domain);

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
        return selectByEntityIdsAndCategoryIds(entityIds, categoryIds, null);
    }

    /**
     * 批量查询实体-分类关联，并按实际存储类型收窄，避免跨表同 ID 串行。
     *
     * @param entityTypeCode 实际存储类型；为空表示不限类型
     */
    default List<EntityCategoryRelationDO> selectByEntityIdsAndCategoryIds(List<Long> entityIds, List<Long> categoryIds,
                                                                           String entityTypeCode) {
        if (entityIds == null || entityIds.isEmpty() || categoryIds == null || categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapperX<EntityCategoryRelationDO> query = new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .in(EntityCategoryRelationDO::getEntityId, entityIds)
                .in(EntityCategoryRelationDO::getCategoryId, categoryIds)
                .eq(EntityCategoryRelationDO::getDeleted, false);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            query.eq(EntityCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        return selectList(query);
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
        return selectRelationsByCategoryIdsForOrdering(categoryIds, entityTypeCode, null);
    }

    /**
     * 查询多分类原始关系记录（用于 Service 层按 categoryIds 顺序二次编排）。
     *
     * <p>{@code domain} 为空表示不限业务域（总账视角）。</p>
     */
    default List<EntityCategoryRelationDO> selectRelationsByCategoryIdsForOrdering(List<Long> categoryIds,
                                                                                   String entityTypeCode,
                                                                                   String domain) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapperX<EntityCategoryRelationDO> query = new LambdaQueryWrapperX<>();
        query.in(EntityCategoryRelationDO::getCategoryId, categoryIds);
        query.eq(EntityCategoryRelationDO::getDeleted, false);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            query.eq(EntityCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        if (domain != null && !domain.isBlank()) {
            query.eq(EntityCategoryRelationDO::getDomain, domain);
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

    /**
     * 多组分类求交：每组内 category_id IN (...)，组与组 INTERSECT。
     * 只返回 entity_id；调用方再叠 domain/scope/排序。domain 为空则不限业务域。
     */
    @Select("""
            <script>
            <foreach collection='groups' item='group' separator=' INTERSECT '>
              SELECT DISTINCT entity_id
              FROM dynamic_entity_category_relation
              WHERE deleted = false
                AND entity_type_code = #{entityTypeCode}
                <if test='domain != null and domain != \"\"'>
                  AND domain = #{domain}
                </if>
                AND category_id IN
                <foreach collection='group' item='cid' open='(' separator=',' close=')'>
                  #{cid}
                </foreach>
            </foreach>
            </script>
            """)
    List<Long> selectEntityIdsIntersectingCategoryGroups(@Param("entityTypeCode") String entityTypeCode,
                                                         @Param("domain") String domain,
                                                         @Param("groups") List<List<Long>> groups);

    /**
     * 当前分类种类下已挂接任一节点的实体 id（排除保留「未分类」桶节点）。
     */
    @Select("""
            SELECT DISTINCT ecr.entity_id
            FROM dynamic_entity_category_relation ecr
            INNER JOIN dynamic_category c ON c.id = ecr.category_id AND c.deleted = FALSE
            WHERE ecr.deleted = FALSE
              AND ecr.entity_type_code = #{entityTypeCode}
              AND c.category_type_code = #{categoryTypeCode}
              AND UPPER(c.code) NOT LIKE '%UNCATEGORIZED%'
            ORDER BY ecr.entity_id ASC
            """)
    List<Long> selectDistinctEntityIdsByCategoryTypeCode(@Param("categoryTypeCode") String categoryTypeCode,
                                                         @Param("entityTypeCode") String entityTypeCode);
}
