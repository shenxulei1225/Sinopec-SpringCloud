package cn.cheers.x.module.dynamicbusiness.dal.mysql.entity;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.service.entity.dto.EntityAggregationCountDTO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Entity 聚合统计 Mapper（PostgreSQL）
 *
 * <p>仅用于聚合统计（facet）。表名固定写 dynamic_entity，由 MyBatis-Plus DynamicTableNameInnerInterceptor
 * 结合 ThreadLocal 的 businessTypeCode 在运行时替换为 biz_xxx。</p>
 */
@Mapper
public interface EntityAggregationMapper extends BaseMapperX<EntityDO> {

        /**
         * 安全调用封装：统一处理 null/空数组与 keyword 规范化，避免调用方手动维护 entityIdsSize。
         */
        default List<EntityAggregationCountDTO<Integer>> selectStatusCountSafe(Long modelId,
                                                                                Integer status,
                                                                                String keyword,
                                                                                Long[] entityIds) {
                Long[] safeEntityIds = entityIds == null ? new Long[0] : entityIds;
                String safeKeyword = keyword == null ? null : keyword.trim();
                return selectStatusCount(modelId, status, safeKeyword, safeEntityIds, safeEntityIds.length);
        }

        /**
         * 安全调用封装：统一处理 null/空数组与 keyword 规范化，避免调用方手动维护 entityIdsSize。
         */
        default List<EntityAggregationCountDTO<Long>> selectModelCountSafe(Long modelId,
                                                                                Integer status,
                                                                                String keyword,
                                                                                Long[] entityIds) {
                Long[] safeEntityIds = entityIds == null ? new Long[0] : entityIds;
                String safeKeyword = keyword == null ? null : keyword.trim();
                return selectModelCount(modelId, status, safeKeyword, safeEntityIds, safeEntityIds.length);
        }

        /**
         * 兼容旧签名：保留 entityIdsSize 入参，内部不依赖该参数，统一按 entityIds 实际长度处理。
         */
        default List<EntityAggregationCountDTO<Integer>> selectStatusCount(Long modelId,
                                                                                Integer status,
                                                                                String keyword,
                                                                                Long[] entityIds,
                                                                                int entityIdsSize) {
                List<Long> ids = toIdList(entityIds);
                QueryWrapper<EntityDO> query = new QueryWrapper<>();
                query.select("status AS key", "COUNT(1) AS cnt");
                query.eq("deleted", false);
                if (modelId != null) {
                        query.eq("model_id", modelId);
                }
                if (status != null) {
                        query.eq("status", status);
                }
                if (keyword != null && !keyword.isBlank()) {
                        query.like("name", keyword.trim());
                }
                if (!ids.isEmpty()) {
                        query.in("id", ids);
                }
                query.groupBy("status");

                List<Map<String, Object>> rows = selectMaps(query);
                if (rows == null || rows.isEmpty()) {
                        return Collections.emptyList();
                }
                return rows.stream()
                        .map(EntityAggregationMapper::toStatusCountDTO)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
        }

        /**
         * 兼容旧签名：保留 entityIdsSize 入参，内部不依赖该参数，统一按 entityIds 实际长度处理。
         */
        default List<EntityAggregationCountDTO<Long>> selectModelCount(Long modelId,
                                                                        Integer status,
                                                                        String keyword,
                                                                        Long[] entityIds,
                                                                        int entityIdsSize) {
                List<Long> ids = toIdList(entityIds);
                QueryWrapper<EntityDO> query = new QueryWrapper<>();
                query.select("model_id AS key", "COUNT(1) AS cnt");
                query.eq("deleted", false);
                if (modelId != null) {
                        query.eq("model_id", modelId);
                }
                if (status != null) {
                        query.eq("status", status);
                }
                if (keyword != null && !keyword.isBlank()) {
                        query.like("name", keyword.trim());
                }
                if (!ids.isEmpty()) {
                        query.in("id", ids);
                }
                query.groupBy("model_id");

                List<Map<String, Object>> rows = selectMaps(query);
                if (rows == null || rows.isEmpty()) {
                        return Collections.emptyList();
                }
                return rows.stream()
                        .map(EntityAggregationMapper::toModelCountDTO)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
        }

        static List<Long> toIdList(Long[] entityIds) {
                if (entityIds == null || entityIds.length == 0) {
                return Collections.emptyList();
                }
                return java.util.Arrays.stream(entityIds)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList());
        }

        static EntityAggregationCountDTO<Integer> toStatusCountDTO(Map<String, Object> row) {
                Integer key = toInteger(getIgnoreCase(row, "key"));
                Long cnt = toLong(getIgnoreCase(row, "cnt"));
                if (key == null || cnt == null) {
                return null;
                }
                EntityAggregationCountDTO<Integer> dto = new EntityAggregationCountDTO<>();
                dto.setKey(key);
                dto.setCnt(cnt);
                return dto;
        }

        static EntityAggregationCountDTO<Long> toModelCountDTO(Map<String, Object> row) {
                Long key = toLong(getIgnoreCase(row, "key"));
                Long cnt = toLong(getIgnoreCase(row, "cnt"));
                if (key == null || cnt == null) {
                return null;
                }
                EntityAggregationCountDTO<Long> dto = new EntityAggregationCountDTO<>();
                dto.setKey(key);
                dto.setCnt(cnt);
                return dto;
        }

        static Object getIgnoreCase(Map<String, Object> row, String key) {
                if (row == null || row.isEmpty() || key == null) {
                return null;
                }
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                if (entry.getKey() != null && key.equalsIgnoreCase(entry.getKey())) {
                        return entry.getValue();
                }
                }
                return null;
        }

        static Long toLong(Object value) {
                if (value == null) {
                return null;
                }
                if (value instanceof Number number) {
                return number.longValue();
                }
                try {
                return Long.parseLong(String.valueOf(value));
                } catch (Exception ignored) {
                return null;
                }
        }

        static Integer toInteger(Object value) {
                if (value == null) {
                return null;
                }
                if (value instanceof Number number) {
                return number.intValue();
                }
                try {
                return Integer.parseInt(String.valueOf(value));
                } catch (Exception ignored) {
                return null;
                }
        }
}
