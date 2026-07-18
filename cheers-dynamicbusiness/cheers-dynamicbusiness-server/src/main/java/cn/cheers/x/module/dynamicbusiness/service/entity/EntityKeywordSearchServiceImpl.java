package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityFieldIndexMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 关键词搜索实现：索引表 + 实体名称双通道命中。
 */
@Service
public class EntityKeywordSearchServiceImpl implements EntityKeywordSearchService {

    @Resource
    private EntityFieldIndexMapper entityFieldIndexMapper;

    @Resource
    private EntityCoreService entityCoreService;

    @Override
    public Set<Long> searchMatchedEntityIds(String entityTypeCode, String keyword, List<Long> candidateEntityIds) {
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            return Collections.emptySet();
        }
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptySet();
        }
        if (candidateEntityIds == null || candidateEntityIds.isEmpty()) {
            return Collections.emptySet();
        }

        String k = keyword.trim().toLowerCase(Locale.ROOT);
        Set<Long> candidateSet = new HashSet<>(candidateEntityIds);
        Set<Long> matched = new HashSet<>();

        // 通道1：索引表（扩展字段 value_string）
        List<Long> matchedFromIndex = entityFieldIndexMapper.selectEntityIdsByKeyword(k);
        if (matchedFromIndex != null && !matchedFromIndex.isEmpty()) {
            for (Long id : matchedFromIndex) {
                if (id != null && candidateSet.contains(id)) {
                    matched.add(id);
                }
            }
        }

        // 通道2：实体名称（name）
        List<EntityDO> entities = entityCoreService.listByIds(candidateEntityIds, entityTypeCode);
        if (entities != null && !entities.isEmpty()) {
            Set<Long> matchedByName = entities.stream()
                    .filter(e -> e != null && e.getId() != null && e.getName() != null)
                    .filter(e -> e.getName().toLowerCase(Locale.ROOT).contains(k))
                    .map(EntityDO::getId)
                    .collect(Collectors.toSet());
            matched.addAll(matchedByName);
        }

        return matched;
    }
}
