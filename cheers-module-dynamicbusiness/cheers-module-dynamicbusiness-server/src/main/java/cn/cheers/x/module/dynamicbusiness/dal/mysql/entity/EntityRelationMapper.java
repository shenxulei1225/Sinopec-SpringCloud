package cn.cheers.x.module.dynamicbusiness.dal.mysql.entity;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityRelationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务实体关联关系 Mapper
 *
 * V1.0.33 扩展：新增分组统计和按字段查询方法
 * - 分组统计方法：支持按源 Model、字段编码分组统计反向关联
 * - 按字段查询方法：支持按字段编码查询和删除关联
 */
@Mapper
public interface EntityRelationMapper extends BaseMapperX<EntityRelationDO> {

    // =====================================================
    // V1.0.33 新增：基础查询方法
    // 需求：FR-BDA-073, FR-BDA-090
    // =====================================================

        /**
         * 根据源实体ID查询关联关系列表
         */
        default List<EntityRelationDO> selectBySourceEntityId(Long sourceEntityId) {
        return selectList(new LambdaQueryWrapperX<EntityRelationDO>()
                .eq(EntityRelationDO::getSourceEntityId, sourceEntityId)
                .eq(EntityRelationDO::getDeleted, false));
        }

        /**
         * 根据目标实体ID查询关联关系列表
         */
        default List<EntityRelationDO> selectByTargetEntityId(Long targetEntityId) {
                return selectList(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getTargetEntityId, targetEntityId)
                        .eq(EntityRelationDO::getDeleted, false));
        }

        /**
         * 根据实体ID查询所有关联关系（作为源或目标）
         */
        default List<EntityRelationDO> selectByEntityId(Long entityId) {
                return selectList(new LambdaQueryWrapperX<EntityRelationDO>()
                        .and(wrapper -> wrapper
                                .eq(EntityRelationDO::getSourceEntityId, entityId)
                                .or()
                                .eq(EntityRelationDO::getTargetEntityId, entityId))
                        .eq(EntityRelationDO::getDeleted, false));
        }

        /**
         * 根据源实体ID和目标实体ID查询关联关系
         */
        default EntityRelationDO selectBySourceAndTarget(Long sourceEntityId, Long targetEntityId) {
                return selectOne(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getSourceEntityId, sourceEntityId)
                        .eq(EntityRelationDO::getTargetEntityId, targetEntityId)
                        .eq(EntityRelationDO::getDeleted, false));
        }

        /**
         * 根据源实体ID和关联类型查询关联关系列表
         */
        default List<EntityRelationDO> selectBySourceAndType(Long sourceEntityId, String relationType) {
                return selectList(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getSourceEntityId, sourceEntityId)
                        .eqIfPresent(EntityRelationDO::getRelationType, relationType)
                        .eq(EntityRelationDO::getDeleted, false));
        }

        /**
         * 根据目标实体ID和关联类型查询关联关系列表
         */
        default List<EntityRelationDO> selectByTargetEntityIdAndType(Long targetEntityId, String relationType) {
                return selectList(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getTargetEntityId, targetEntityId)
                        .eqIfPresent(EntityRelationDO::getRelationType, relationType)
                        .eq(EntityRelationDO::getDeleted, false));
        }

        /**
         * 根据源实体ID和目标业务类型编码查询关联关系列表
         */
        default List<EntityRelationDO> selectBySourceEntityIdAndTargetEntityTypeCode(Long sourceEntityId, String targetEntityTypeCode) {
                return selectList(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getSourceEntityId, sourceEntityId)
                        .eq(EntityRelationDO::getTargetEntityTypeCode, targetEntityTypeCode)
                        .eq(EntityRelationDO::getDeleted, false));
        }

        /**
         * 根据源实体ID、目标业务类型编码和关联类型查询关联关系列表
         */
        default List<EntityRelationDO> selectBySourceEntityIdAndTargetEntityTypeCodeAndType(Long sourceEntityId, String targetEntityTypeCode, String relationType) {
                return selectList(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getSourceEntityId, sourceEntityId)
                        .eq(EntityRelationDO::getTargetEntityTypeCode, targetEntityTypeCode)
                        .eqIfPresent(EntityRelationDO::getRelationType, relationType)
                        .eq(EntityRelationDO::getDeleted, false));
        }

        /**
         * 根据目标实体ID和源业务类型编码查询关联关系列表
         */
        default List<EntityRelationDO> selectByTargetEntityIdAndSourceEntityTypeCode(Long targetEntityId, String sourceEntityTypeCode) {
                return selectList(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getTargetEntityId, targetEntityId)
                        .eq(EntityRelationDO::getSourceEntityTypeCode, sourceEntityTypeCode)
                        .eq(EntityRelationDO::getDeleted, false));
        }

        /**
         * 根据目标实体ID、源业务类型编码和关联类型查询关联关系列表
         */
        default List<EntityRelationDO> selectByTargetEntityIdAndSourceEntityTypeCodeAndType(Long targetEntityId, String sourceEntityTypeCode, String relationType) {
                return selectList(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getTargetEntityId, targetEntityId)
                        .eq(EntityRelationDO::getSourceEntityTypeCode, sourceEntityTypeCode)
                        .eqIfPresent(EntityRelationDO::getRelationType, relationType)
                        .eq(EntityRelationDO::getDeleted, false));
        }

        /**
         * 删除源实体的所有关联关系
         */
        default int deleteBySourceEntityId(Long sourceEntityId) {
                return delete(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getSourceEntityId, sourceEntityId));
        }

        /**
         * 删除目标实体的所有关联关系
         */
        default int deleteByTargetEntityId(Long targetEntityId) {
                return delete(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getTargetEntityId, targetEntityId));
        }

        /**
         * 删除实体的所有关联关系（作为源或目标）
         */
        default int deleteByEntityId(Long entityId) {
                return delete(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getSourceEntityId, entityId)
                        .or()
                        .eq(EntityRelationDO::getTargetEntityId, entityId));
        }

        /**
         * 删除指定的关联关系
         */
        default int deleteBySourceAndTarget(Long sourceEntityId, Long targetEntityId) {
                return delete(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getSourceEntityId, sourceEntityId)
                        .eq(EntityRelationDO::getTargetEntityId, targetEntityId));
        }

        /**
         * 统计实体的关联关系数量
         */
        default Long countByEntityId(Long entityId) {
                return selectCount(new LambdaQueryWrapperX<EntityRelationDO>()
                        .and(wrapper -> wrapper
                                .eq(EntityRelationDO::getSourceEntityId, entityId)
                                .or()
                                .eq(EntityRelationDO::getTargetEntityId, entityId))
                        .eq(EntityRelationDO::getDeleted, false));
        }

        // =====================================================
        // V1.0.33 新增：分组统计方法
        // 需求：FR-BDA-075, FR-BDA-092
        // =====================================================

        /**
         * 按源 Model 分组统计反向关联数量
         * 用于统计有多少不同类型的实体引用了目标实体
         *
         * @param targetEntityId 目标实体ID
         * @return 按源 Model 分组的统计结果，包含 source_model_code 和 count
         */
        default List<Map<String, Object>> countByTargetEntityIdGroupBySourceModel(Long targetEntityId) {
                List<EntityRelationDO> relations = selectList(new LambdaQueryWrapperX<EntityRelationDO>()
                        .select(EntityRelationDO::getSourceModelCode)
                        .eq(EntityRelationDO::getTargetEntityId, targetEntityId)
                        .isNotNull(EntityRelationDO::getSourceModelCode)
                        .eq(EntityRelationDO::getDeleted, false));
                Map<String, Long> grouped = relations.stream()
                        .collect(java.util.stream.Collectors.groupingBy(
                                EntityRelationDO::getSourceModelCode,
                                java.util.stream.Collectors.counting()));
                List<Map<String, Object>> result = new java.util.ArrayList<>();
                grouped.forEach((k, v) -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("source_model_code", k);
                        item.put("count", v);
                        result.add(item);
                });
                return result;
        }

        /**
         * 按字段编码分组统计反向关联数量
         * 用于统计不同字段产生的关联数量
         *
         * @param targetEntityId 目标实体ID
         * @return 按字段编码分组的统计结果，包含 field_code 和 count
         */
        default List<Map<String, Object>> countByTargetEntityIdGroupByFieldCode(Long targetEntityId) {
                List<EntityRelationDO> relations = selectList(new LambdaQueryWrapperX<EntityRelationDO>()
                        .select(EntityRelationDO::getFieldCode)
                        .eq(EntityRelationDO::getTargetEntityId, targetEntityId)
                        .isNotNull(EntityRelationDO::getFieldCode)
                        .eq(EntityRelationDO::getDeleted, false));
                Map<String, Long> grouped = relations.stream()
                        .collect(java.util.stream.Collectors.groupingBy(
                                EntityRelationDO::getFieldCode,
                                java.util.stream.Collectors.counting()));
                List<Map<String, Object>> result = new java.util.ArrayList<>();
                grouped.forEach((k, v) -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("field_code", k);
                        item.put("count", v);
                        result.add(item);
                });
                return result;
        }

        /**
         * 统计特定 Model 的反向关联数量
         * 用于统计某个特定类型的实体引用目标实体的数量
         *
         * @param targetEntityId 目标实体ID
         * @param sourceModelCode 源 Model 编码
         * @return 关联数量
         */
        default Long countByTargetEntityIdAndSourceModelCode(Long targetEntityId, String sourceModelCode) {
                return selectCount(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getTargetEntityId, targetEntityId)
                        .eq(EntityRelationDO::getSourceModelCode, sourceModelCode)
                        .eq(EntityRelationDO::getDeleted, false));
        }

        /**
         * 精确统计：按目标实体、源 Model 和字段编码统计
         * 用于精确统计某个特定字段产生的关联数量
         *
         * @param targetEntityId 目标实体ID
         * @param sourceModelCode 源 Model 编码
         * @param fieldCode 字段编码
         * @return 关联数量
         */
        default Long countByTargetEntityIdAndSourceModelCodeAndFieldCode(Long targetEntityId,
                                                                                String sourceModelCode,
                                                                                String fieldCode) {
                return selectCount(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getTargetEntityId, targetEntityId)
                        .eq(EntityRelationDO::getSourceModelCode, sourceModelCode)
                        .eq(EntityRelationDO::getFieldCode, fieldCode)
                        .eq(EntityRelationDO::getDeleted, false));
        }

        /**
         * 按目标业务类型分组统计反向关联数量
         * 用于跨业务类型统计
         *
         * @param targetEntityId 目标实体ID
         * @return 按目标业务类型分组的统计结果
         */
        default List<Map<String, Object>> countByTargetEntityIdGroupBySourceEntityType(Long targetEntityId) {
                List<EntityRelationDO> relations = selectList(new LambdaQueryWrapperX<EntityRelationDO>()
                        .select(EntityRelationDO::getSourceEntityTypeCode)
                        .eq(EntityRelationDO::getTargetEntityId, targetEntityId)
                        .isNotNull(EntityRelationDO::getSourceEntityTypeCode)
                        .eq(EntityRelationDO::getDeleted, false));
                Map<String, Long> grouped = relations.stream()
                        .collect(java.util.stream.Collectors.groupingBy(
                                EntityRelationDO::getSourceEntityTypeCode,
                                java.util.stream.Collectors.counting()));
                List<Map<String, Object>> result = new java.util.ArrayList<>();
                grouped.forEach((k, v) -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("source_entity_type_code", k);
                        item.put("count", v);
                        result.add(item);
                });
                return result;
        }

        // =====================================================
        // V1.0.33 新增：按字段查询方法
        // 需求：FR-BDA-074, FR-BDA-091
        // =====================================================

        /**
         * 按目标实体和字段编码查询关联关系
         * 用于查询引用目标实体的特定字段的所有关联
         *
         * @param targetEntityId 目标实体ID
         * @param fieldCode 字段编码
         * @return 关联关系列表
         */
        default List<EntityRelationDO> selectByTargetEntityIdAndFieldCode(Long targetEntityId, String fieldCode) {
                return selectList(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getTargetEntityId, targetEntityId)
                        .eq(EntityRelationDO::getFieldCode, fieldCode)
                        .eq(EntityRelationDO::getDeleted, false));
        }

        /**
         * 按源实体和字段编码查询关联关系
         * 用于查询源实体通过特定字段建立的所有关联
         *
         * @param sourceEntityId 源实体ID
         * @param fieldCode 字段编码
         * @return 关联关系列表
         */
        default List<EntityRelationDO> selectBySourceEntityIdAndFieldCode(Long sourceEntityId, String fieldCode) {
                return selectList(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getSourceEntityId, sourceEntityId)
                        .eq(EntityRelationDO::getFieldCode, fieldCode)
                        .eq(EntityRelationDO::getDeleted, false));
        }

        /**
         * 根据字段编码与目标实体ID集合，查询命中的源实体ID集合。
         */
        default List<Long> selectSourceEntityIdsByFieldCodeAndTargetIds(String fieldCode, List<Long> targetEntityIds) {
                if (targetEntityIds == null || targetEntityIds.isEmpty()) {
                        return java.util.Collections.emptyList();
                }
                List<EntityRelationDO> relations = selectList(new LambdaQueryWrapperX<EntityRelationDO>()
                        .select(EntityRelationDO::getSourceEntityId)
                        .eq(EntityRelationDO::getFieldCode, fieldCode)
                        .in(EntityRelationDO::getTargetEntityId, targetEntityIds)
                        .eq(EntityRelationDO::getDeleted, false));
                return relations.stream()
                        .map(EntityRelationDO::getSourceEntityId)
                        .filter(java.util.Objects::nonNull)
                        .distinct()
                        .toList();
        }

        /**
         * 按源实体和字段编码删除关联关系
         * 用于在更新实体时清除旧的关联关系
         *
         * @param sourceEntityId 源实体ID
         * @param fieldCode 字段编码
         * @return 删除的记录数
         */
        default int deleteBySourceEntityIdAndFieldCode(Long sourceEntityId, String fieldCode) {
                return delete(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getSourceEntityId, sourceEntityId)
                        .eq(EntityRelationDO::getFieldCode, fieldCode));
        }

        /**
         * 按源实体、字段编码和目标实体删除关联关系
         * 用于多选关联字段的部分删除（删除特定目标的关联）
         *
         * @param sourceEntityId 源实体ID
         * @param fieldCode 字段编码
         * @param targetEntityId 目标实体ID
         * @return 删除的记录数
         */
        default int deleteBySourceEntityIdAndFieldCodeAndTargetEntityId(Long sourceEntityId, String fieldCode, Long targetEntityId) {
                return delete(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getSourceEntityId, sourceEntityId)
                        .eq(EntityRelationDO::getFieldCode, fieldCode)
                        .eq(EntityRelationDO::getTargetEntityId, targetEntityId));
        }

        /**
         * 按目标实体和源 Model 编码查询关联关系
         * 用于查询特定类型实体对目标实体的引用
         *
         * @param targetEntityId 目标实体ID
         * @param sourceModelCode 源 Model 编码
         * @return 关联关系列表
         */
        default List<EntityRelationDO> selectByTargetEntityIdAndSourceModelCode(Long targetEntityId, String sourceModelCode) {
                return selectList(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getTargetEntityId, targetEntityId)
                        .eq(EntityRelationDO::getSourceModelCode, sourceModelCode)
                        .eq(EntityRelationDO::getDeleted, false));
        }

        /**
         * 按目标实体、源 Model 编码和字段编码查询关联关系
         * 用于精确查询特定类型实体通过特定字段对目标实体的引用
         *
         * @param targetEntityId 目标实体ID
         * @param sourceModelCode 源 Model 编码
         * @param fieldCode 字段编码
         * @return 关联关系列表
         */
        default List<EntityRelationDO> selectByTargetEntityIdAndSourceModelCodeAndFieldCode(
                Long targetEntityId, String sourceModelCode, String fieldCode) {
                return selectList(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getTargetEntityId, targetEntityId)
                        .eq(EntityRelationDO::getSourceModelCode, sourceModelCode)
                        .eq(EntityRelationDO::getFieldCode, fieldCode)
                        .eq(EntityRelationDO::getDeleted, false));
        }

        /**
         * 按目标业务类型编码查询关联关系
         * 用于跨业务类型查询
         *
         * @param targetEntityTypeCode 目标业务类型编码
         * @return 关联关系列表
         */
        default List<EntityRelationDO> selectByTargetEntityTypeCode(String targetEntityTypeCode) {
                return selectList(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getTargetEntityTypeCode, targetEntityTypeCode)
                        .eq(EntityRelationDO::getDeleted, false));
        }

        /**
         * 按源业务类型编码查询关联关系
         * 用于跨业务类型查询
         *
         * @param sourceEntityTypeCode 源业务类型编码
         * @return 关联关系列表
         */
        default List<EntityRelationDO> selectBySourceEntityTypeCode(String sourceEntityTypeCode) {
                return selectList(new LambdaQueryWrapperX<EntityRelationDO>()
                        .eq(EntityRelationDO::getSourceEntityTypeCode, sourceEntityTypeCode)
                        .eq(EntityRelationDO::getDeleted, false));
        }
}
