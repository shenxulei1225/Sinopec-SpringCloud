package cn.cheers.x.module.dynamicbusiness.dal.mysql.entity;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityFieldIndexDO;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 扩展字段索引 Mapper
 *
 * @author yudao
 */
@Mapper
public interface EntityFieldIndexMapper extends BaseMapperX<EntityFieldIndexDO> {

    /**
     * 根据实体ID查询所有索引记录
     */
    default List<EntityFieldIndexDO> selectByEntityId(Long entityId) {
        return selectList(new LambdaQueryWrapperX<EntityFieldIndexDO>()
                .eq(EntityFieldIndexDO::getEntityId, entityId));
    }

    /**
     * 根据实体ID和字段编码查询索引记录
     */
    default EntityFieldIndexDO selectByEntityIdAndFieldCode(Long entityId, String fieldCode) {
        return selectOne(new LambdaQueryWrapperX<EntityFieldIndexDO>()
                .eq(EntityFieldIndexDO::getEntityId, entityId)
                .eq(EntityFieldIndexDO::getFieldCode, fieldCode));
    }

    /**
     * 根据模型ID和字段编码查询索引记录列表
     */
    default List<EntityFieldIndexDO> selectByModelIdAndFieldCode(Long modelId, String fieldCode) {
        return selectList(new LambdaQueryWrapperX<EntityFieldIndexDO>()
                .eq(EntityFieldIndexDO::getModelId, modelId)
                .eq(EntityFieldIndexDO::getFieldCode, fieldCode));
    }

    /**
     * 根据字段编码和实体ID列表查询索引记录（用于多选 token 精确匹配）。
     */
    default List<EntityFieldIndexDO> selectByFieldCodeAndEntityIds(String fieldCode, Collection<Long> entityIds) {
        if (fieldCode == null || fieldCode.isBlank() || entityIds == null || entityIds.isEmpty()) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<EntityFieldIndexDO>()
                .eq(EntityFieldIndexDO::getFieldCode, fieldCode)
                .in(EntityFieldIndexDO::getEntityId, entityIds));
    }

    default List<EntityFieldIndexDO> selectByFieldCode(String fieldCode) {
        if (fieldCode == null || fieldCode.isBlank()) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<EntityFieldIndexDO>()
                .eq(EntityFieldIndexDO::getFieldCode, fieldCode));
    }

    /**
     * 按关键词在索引表中查询命中的实体ID（仅字符串索引列）。
     *
     * <p>说明：
     * - 仅匹配 value_string（对应 STRING/SELECT/ENTITY_REF 等）；
     * - 不按 model 预过滤，避免多模型场景漏检；
     * - 返回去重后的 entityId 列表。</p>
     */
    default List<Long> selectEntityIdsByKeyword(String keywordLower) {
        return selectRowsByKeyword(keywordLower).stream()
                .map(EntityFieldIndexDO::getEntityId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    /**
     * 按关键词命中的索引行（含 modelId/fieldCode，供上层校验可搜索）。
     */
    default List<EntityFieldIndexDO> selectRowsByKeyword(String keywordLower) {
        if (keywordLower == null || keywordLower.isBlank()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapperX<EntityFieldIndexDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.isNotNull(EntityFieldIndexDO::getEntityId);
        wrapper.isNotNull(EntityFieldIndexDO::getValueString);
        wrapper.like(EntityFieldIndexDO::getValueString, keywordLower);
        return selectList(wrapper);
    }

    /**
     * 按字段 + 数值范围筛选命中的实体ID。
     */
    default List<Long> selectEntityIdsByNumberRange(String fieldCode, BigDecimal min, BigDecimal max) {
        if (fieldCode == null || fieldCode.isBlank()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapperX<EntityFieldIndexDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.select(EntityFieldIndexDO::getEntityId);
        wrapper.eq(EntityFieldIndexDO::getFieldCode, fieldCode);
        if (min != null) {
            wrapper.ge(EntityFieldIndexDO::getValueNumber, min);
        }
        if (max != null) {
            wrapper.le(EntityFieldIndexDO::getValueNumber, max);
        }
        wrapper.groupBy(EntityFieldIndexDO::getEntityId);
        return selectList(wrapper).stream()
                .map(EntityFieldIndexDO::getEntityId)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 按字段 + 日期范围筛选命中的实体ID。
     */
    default List<Long> selectEntityIdsByDateRange(String fieldCode, LocalDate min, LocalDate max) {
        if (fieldCode == null || fieldCode.isBlank()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapperX<EntityFieldIndexDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.select(EntityFieldIndexDO::getEntityId);
        wrapper.eq(EntityFieldIndexDO::getFieldCode, fieldCode);
        if (min != null) {
            wrapper.ge(EntityFieldIndexDO::getValueDate, min);
        }
        if (max != null) {
            wrapper.le(EntityFieldIndexDO::getValueDate, max);
        }
        wrapper.groupBy(EntityFieldIndexDO::getEntityId);
        return selectList(wrapper).stream()
                .map(EntityFieldIndexDO::getEntityId)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 按字段 + 日期时间范围筛选命中的实体ID。
     */
    default List<Long> selectEntityIdsByDateTimeRange(String fieldCode, LocalDateTime min, LocalDateTime max) {
        if (fieldCode == null || fieldCode.isBlank()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapperX<EntityFieldIndexDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.select(EntityFieldIndexDO::getEntityId);
        wrapper.eq(EntityFieldIndexDO::getFieldCode, fieldCode);
        if (min != null) {
            wrapper.ge(EntityFieldIndexDO::getValueDatetime, min);
        }
        if (max != null) {
            wrapper.le(EntityFieldIndexDO::getValueDatetime, max);
        }
        wrapper.groupBy(EntityFieldIndexDO::getEntityId);
        return selectList(wrapper).stream()
                .map(EntityFieldIndexDO::getEntityId)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 按字段 + 字符串等值/包含筛选命中的实体ID。
     */
    default List<Long> selectEntityIdsByString(String fieldCode, String value, boolean contains) {
        if (fieldCode == null || fieldCode.isBlank() || value == null || value.isBlank()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapperX<EntityFieldIndexDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.select(EntityFieldIndexDO::getEntityId);
        wrapper.eq(EntityFieldIndexDO::getFieldCode, fieldCode);
        if (contains) {
            wrapper.like(EntityFieldIndexDO::getValueString, value);
        } else {
            wrapper.eq(EntityFieldIndexDO::getValueString, value);
        }
        wrapper.groupBy(EntityFieldIndexDO::getEntityId);
        return selectList(wrapper).stream()
                .map(EntityFieldIndexDO::getEntityId)
                .filter(Objects::nonNull)
                .toList();
    }


    /**
     * 按字段 + 字符串等值筛选命中的实体ID。
     */
    default List<Long> selectEntityIdsByStringEquals(String fieldCode, String value) {
        if (fieldCode == null || fieldCode.isBlank() || value == null) {
            return Collections.emptyList();
        }
        LambdaQueryWrapperX<EntityFieldIndexDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.select(EntityFieldIndexDO::getEntityId);
        wrapper.eq(EntityFieldIndexDO::getFieldCode, fieldCode);
        wrapper.eq(EntityFieldIndexDO::getValueString, value);
        wrapper.groupBy(EntityFieldIndexDO::getEntityId);
        return selectList(wrapper).stream()
                .map(EntityFieldIndexDO::getEntityId)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 按字段 + 字符串 IN 筛选命中的实体ID。
     */
    default List<Long> selectEntityIdsByStringIn(String fieldCode, Collection<String> values) {
        if (fieldCode == null || fieldCode.isBlank() || values == null || values.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapperX<EntityFieldIndexDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.select(EntityFieldIndexDO::getEntityId);
        wrapper.eq(EntityFieldIndexDO::getFieldCode, fieldCode);
        wrapper.in(EntityFieldIndexDO::getValueString, values);
        wrapper.groupBy(EntityFieldIndexDO::getEntityId);
        return selectList(wrapper).stream()
                .map(EntityFieldIndexDO::getEntityId)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 按字段 + 字符串包含筛选命中的实体ID。
     */
    default List<Long> selectEntityIdsByStringContains(String fieldCode, String keyword) {
        if (fieldCode == null || fieldCode.isBlank() || keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapperX<EntityFieldIndexDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.select(EntityFieldIndexDO::getEntityId);
        wrapper.eq(EntityFieldIndexDO::getFieldCode, fieldCode);
        wrapper.like(EntityFieldIndexDO::getValueString, keyword);
        wrapper.groupBy(EntityFieldIndexDO::getEntityId);
        return selectList(wrapper).stream()
                .map(EntityFieldIndexDO::getEntityId)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 按字段 + 布尔值筛选命中的实体ID。
     */
    default List<Long> selectEntityIdsByBooleanEquals(String fieldCode, Boolean value) {
        if (fieldCode == null || fieldCode.isBlank() || value == null) {
            return Collections.emptyList();
        }
        LambdaQueryWrapperX<EntityFieldIndexDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.select(EntityFieldIndexDO::getEntityId);
        wrapper.eq(EntityFieldIndexDO::getFieldCode, fieldCode);
        wrapper.eq(EntityFieldIndexDO::getValueBoolean, value);
        wrapper.groupBy(EntityFieldIndexDO::getEntityId);
        return selectList(wrapper).stream()
                .map(EntityFieldIndexDO::getEntityId)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 根据实体ID删除所有索引记录
     */
    default int deleteByEntityId(Long entityId) {
        return delete(new LambdaQueryWrapperX<EntityFieldIndexDO>()
                .eq(EntityFieldIndexDO::getEntityId, entityId));
    }

    /**
     * 根据实体ID和字段编码删除索引记录
     */
    default int deleteByEntityIdAndFieldCode(Long entityId, String fieldCode) {
        return delete(new LambdaQueryWrapperX<EntityFieldIndexDO>()
                .eq(EntityFieldIndexDO::getEntityId, entityId)
                .eq(EntityFieldIndexDO::getFieldCode, fieldCode));
    }

    /**
     * 根据模型ID删除所有索引记录（用于索引重建）
     */
    default int deleteByModelId(Long modelId) {
        return delete(new LambdaQueryWrapperX<EntityFieldIndexDO>()
                .eq(EntityFieldIndexDO::getModelId, modelId));
    }

    /**
     * 根据模型ID和字段编码删除索引记录（用于字段取消可查询）
     */
    default int deleteByModelIdAndFieldCode(Long modelId, String fieldCode) {
        return delete(new LambdaQueryWrapperX<EntityFieldIndexDO>()
                .eq(EntityFieldIndexDO::getModelId, modelId)
                .eq(EntityFieldIndexDO::getFieldCode, fieldCode));
    }

    /**
     * 统计模型下的索引记录数
     */
    default Long countByModelId(Long modelId) {
        return selectCount(new LambdaQueryWrapperX<EntityFieldIndexDO>()
                .eq(EntityFieldIndexDO::getModelId, modelId));
    }
}
