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
 * 列表和详情读路径：引用只存编号，名称在这里补上（不写库）。
 *
 * <p>上级分区在库里是纯数字。补完后同一格变成「类型 + 编号 + 名称」，编号仍在 {@code id}。</p>
 * <p>不负责：保存时改上级，或让页面自己拿编号再查名称。</p>
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
            collectBareParentId(entity.getBaseFields(), idsByType);
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
            if (isParentIdKey(entry.getKey())) {
                next.put(entry.getKey(), enrichBareParentId(bag, entry.getValue(), nameByTypeAndId));
                continue;
            }
            next.put(entry.getKey(), enrichValue(entry.getValue(), nameByTypeAndId));
        }
        return next;
    }

    /**
     * 上级分区在详情里要显示名称，编号不能丢掉。
     * 已经是带编号的对象时，走普通引用补名称。
     * 纯数字且查到了上级名称 → {@code {entityTypeCode, id, name}}，{@code id} 仍是原来的编号。
     * 没有类型或没有名称 → 仍返回原来的数字，页面上缺口可见。
     */
    private Object enrichBareParentId(Map<String, Object> bag,
                                      Object value,
                                      Map<String, Map<Long, String>> nameByTypeAndId) {
        if (value instanceof Map<?, ?> || value instanceof List<?>) {
            return enrichValue(value, nameByTypeAndId);
        }
        Long id = toLong(value);
        String typeCode = readEntityTypeCode(bag);
        if (id == null || !StringUtils.hasText(typeCode)) {
            return value;
        }
        String name = nameByTypeAndId.getOrDefault(typeCode, Map.of()).get(id);
        if (!StringUtils.hasText(name)) {
            return value;
        }
        Map<String, Object> ref = new LinkedHashMap<>();
        ref.put("entityTypeCode", typeCode);
        ref.put("id", id);
        ref.put("name", name);
        return ref;
    }

    /** 上级分区的纯编号，按本记录自己的类型去查上级名称。已是对象的交给普通引用收集。 */
    private void collectBareParentId(Map<String, Object> bag, Map<String, Set<Long>> idsByType) {
        if (bag == null || bag.isEmpty()) {
            return;
        }
        Object raw = firstParentId(bag);
        if (raw == null || raw instanceof Map<?, ?> || raw instanceof List<?>) {
            return;
        }
        Long id = toLong(raw);
        String typeCode = readEntityTypeCode(bag);
        if (id == null || !StringUtils.hasText(typeCode)) {
            return;
        }
        idsByType.computeIfAbsent(typeCode, k -> new HashSet<>()).add(id);
    }

    private static Object firstParentId(Map<String, Object> bag) {
        if (bag.containsKey("parentId")) {
            return bag.get("parentId");
        }
        return bag.get("parent_id");
    }

    private static boolean isParentIdKey(String key) {
        return "parentId".equals(key) || "parent_id".equals(key);
    }

    private static String readEntityTypeCode(Map<String, Object> bag) {
        if (bag == null) {
            return null;
        }
        Object typeObj = bag.get("entityTypeCode");
        if (typeObj == null) {
            typeObj = bag.get("bizCode");
        }
        if (typeObj == null || !StringUtils.hasText(String.valueOf(typeObj))) {
            return null;
        }
        return String.valueOf(typeObj).trim();
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
