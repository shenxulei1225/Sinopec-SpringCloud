package cn.cheers.x.module.dynamicbusiness.service.entity.refdisplay;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 列表读路径：REF 只存 id，展示名在此批量补齐（不写库）。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EntityRefDisplayEnrichServiceImpl implements EntityRefDisplayEnrichService {

    private final EntityCoreService entityCoreService;

    @Override
    public void enrich(List<EntityRespVO> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        Map<String, Set<Long>> idsByType = new HashMap<>();
        collectFromEntities(entities, idsByType);
        if (idsByType.isEmpty()) {
            return;
        }
        Map<String, Map<Long, String>> nameByTypeAndId = loadNames(idsByType);
        if (nameByTypeAndId.isEmpty()) {
            return;
        }
        applyToEntities(entities, nameByTypeAndId);
    }

    private void collectFromEntities(List<EntityRespVO> entities, Map<String, Set<Long>> idsByType) {
        for (EntityRespVO entity : entities) {
            if (entity == null) {
                continue;
            }
            collectFromBag(entity.getBaseFields(), idsByType);
            collectFromBag(entity.getCustomFields(), idsByType);
            if (!CollectionUtils.isEmpty(entity.getChildren())) {
                collectFromEntities(entity.getChildren(), idsByType);
            }
        }
    }

    private void collectFromBag(Map<String, Object> bag, Map<String, Set<Long>> idsByType) {
        if (bag == null || bag.isEmpty()) {
            return;
        }
        for (Object value : bag.values()) {
            collectValue(value, idsByType);
        }
    }

    private void collectValue(Object value, Map<String, Set<Long>> idsByType) {
        if (value == null) {
            return;
        }
        if (value instanceof List<?> list) {
            for (Object item : list) {
                collectValue(item, idsByType);
            }
            return;
        }
        RefKey key = parseRefKey(value);
        if (key == null) {
            return;
        }
        idsByType.computeIfAbsent(key.entityTypeCode(), k -> new HashSet<>()).add(key.id());
    }

    private Map<String, Map<Long, String>> loadNames(Map<String, Set<Long>> idsByType) {
        Map<String, Map<Long, String>> out = new HashMap<>();
        for (Map.Entry<String, Set<Long>> entry : idsByType.entrySet()) {
            String typeCode = entry.getKey();
            List<Long> ids = new ArrayList<>(entry.getValue());
            if (ids.isEmpty()) {
                continue;
            }
            try {
                List<EntityDO> rows = entityCoreService.listByIds(ids, typeCode);
                if (CollectionUtils.isEmpty(rows)) {
                    continue;
                }
                Map<Long, String> nameById = out.computeIfAbsent(typeCode, k -> new HashMap<>());
                for (EntityDO row : rows) {
                    if (row == null || row.getId() == null || !StringUtils.hasText(row.getName())) {
                        continue;
                    }
                    nameById.put(row.getId(), row.getName().trim());
                }
            } catch (Exception ex) {
                log.warn("[REF name enrich] 批量查名称失败 type={}, size={}, err={}",
                        typeCode, ids.size(), ex.getMessage());
            }
        }
        return out;
    }

    private void applyToEntities(List<EntityRespVO> entities,
                                 Map<String, Map<Long, String>> nameByTypeAndId) {
        for (EntityRespVO entity : entities) {
            if (entity == null) {
                continue;
            }
            entity.setBaseFields(enrichBag(entity.getBaseFields(), nameByTypeAndId));
            entity.setCustomFields(enrichBag(entity.getCustomFields(), nameByTypeAndId));
            if (!CollectionUtils.isEmpty(entity.getChildren())) {
                applyToEntities(entity.getChildren(), nameByTypeAndId);
            }
        }
    }

    private Map<String, Object> enrichBag(Map<String, Object> bag,
                                            Map<String, Map<Long, String>> nameByTypeAndId) {
        if (bag == null || bag.isEmpty()) {
            return bag;
        }
        Map<String, Object> next = new LinkedHashMap<>(bag.size());
        for (Map.Entry<String, Object> entry : bag.entrySet()) {
            next.put(entry.getKey(), enrichValue(entry.getValue(), nameByTypeAndId));
        }
        return next;
    }

    private Object enrichValue(Object value, Map<String, Map<Long, String>> nameByTypeAndId) {
        if (value == null) {
            return null;
        }
        if (value instanceof List<?> list) {
            List<Object> out = new ArrayList<>(list.size());
            for (Object item : list) {
                out.add(enrichValue(item, nameByTypeAndId));
            }
            return out;
        }
        if (!(value instanceof Map<?, ?>)) {
            return value;
        }
        RefKey key = parseRefKey(value);
        if (key == null) {
            return value;
        }
        String name = nameByTypeAndId
                .getOrDefault(key.entityTypeCode(), Map.of())
                .get(key.id());
        if (!StringUtils.hasText(name)) {
            return value;
        }
        Map<String, Object> copy = new LinkedHashMap<>();
        for (Map.Entry<?, ?> e : ((Map<?, ?>) value).entrySet()) {
            if (e.getKey() != null) {
                copy.put(String.valueOf(e.getKey()), e.getValue());
            }
        }
        copy.put("name", name);
        return copy;
    }

    private static RefKey parseRefKey(Object value) {
        if (!(value instanceof Map<?, ?> map)) {
            return null;
        }
        Object idObj = map.get("id");
        if (idObj == null) {
            idObj = map.get("entityId");
        }
        Long id = toLong(idObj);
        if (id == null) {
            return null;
        }
        Object typeObj = map.get("entityTypeCode");
        if (typeObj == null) {
            typeObj = map.get("bizCode");
        }
        if (typeObj == null || !StringUtils.hasText(String.valueOf(typeObj))) {
            return null;
        }
        // 必须像 REF 契约：有 id + 类型；避免把普通 Map 误判
        if (!map.containsKey("id") && !map.containsKey("entityId")) {
            return null;
        }
        return new RefKey(String.valueOf(typeObj).trim(), id);
    }

    private static Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            long id = number.longValue();
            return id > 0 ? id : null;
        }
        try {
            long id = Long.parseLong(String.valueOf(value).trim());
            return id > 0 ? id : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private record RefKey(String entityTypeCode, Long id) {
    }
}
