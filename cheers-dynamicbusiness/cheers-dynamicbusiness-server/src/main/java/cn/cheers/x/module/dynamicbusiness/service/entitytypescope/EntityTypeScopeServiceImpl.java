package cn.cheers.x.module.dynamicbusiness.service.entitytypescope;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytypescope.EntityTypeScopeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytypescope.EntityTypeScopeMapper;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.EntityTypeEntryKindEnum;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeContext;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.SCOPE_ENTITY_IDS_EMPTY;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.SCOPE_ENTITY_NOT_IN_BASE;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.SCOPE_ENTITY_TYPE_NOT_EXISTS;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.SCOPE_ENTITY_TYPE_NOT_SCOPE;

@Service
@Validated
public class EntityTypeScopeServiceImpl implements EntityTypeScopeService {

    @Resource
    private EntityTypeScopeMapper entityTypeScopeMapper;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private EntityCoreService entityCoreService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void join(String entityTypeCode, List<Long> entityIds) {
        EntityTypeDO scopeType = requireScopeType(entityTypeCode);
        List<Long> normalized = normalizeEntityIds(entityIds);
        String storageCode = resolveStorageCode(scopeType);
        assertEntitiesInBaseStorage(storageCode, normalized);

        Set<Long> existing = entityTypeScopeMapper.selectByCodeAndEntityIds(scopeType.getCode(), normalized).stream()
                .map(EntityTypeScopeDO::getEntityId)
                .collect(Collectors.toCollection(HashSet::new));

        for (Long entityId : normalized) {
            if (existing.contains(entityId)) {
                continue;
            }
            EntityTypeScopeDO row = EntityTypeScopeDO.builder()
                    .entityTypeCode(scopeType.getCode())
                    .entityId(entityId)
                    .build();
            entityTypeScopeMapper.insert(row);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void leave(String entityTypeCode, List<Long> entityIds) {
        EntityTypeDO scopeType = requireScopeType(entityTypeCode);
        List<Long> normalized = normalizeEntityIds(entityIds);
        List<EntityTypeScopeDO> rows =
                entityTypeScopeMapper.selectByCodeAndEntityIds(scopeType.getCode(), normalized);
        for (EntityTypeScopeDO row : rows) {
            entityTypeScopeMapper.deleteById(row.getId());
        }
    }

    private EntityTypeDO requireScopeType(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            throw exception(SCOPE_ENTITY_TYPE_NOT_EXISTS);
        }
        EntityTypeDO type = entityTypeMapper.selectByCode(entityTypeCode.trim());
        if (type == null) {
            throw exception(SCOPE_ENTITY_TYPE_NOT_EXISTS);
        }
        EntityTypeEntryKindEnum kind = EntityTypeEntryKindEnum.fromCode(type.getEntryKind());
        if (!kind.isScopeEntry()) {
            throw exception(SCOPE_ENTITY_TYPE_NOT_SCOPE);
        }
        return type;
    }

    private String resolveStorageCode(EntityTypeDO scopeType) {
        EntityTypeScopeContext scope = EntityTypeScopeContext.from(scopeType);
        String storage = scope != null ? scope.getStorageEntityTypeCode() : null;
        if (!StringUtils.hasText(storage)) {
            throw exception(SCOPE_ENTITY_TYPE_NOT_EXISTS);
        }
        return storage.trim();
    }

    private void assertEntitiesInBaseStorage(String storageCode, List<Long> entityIds) {
        List<EntityDO> found = entityCoreService.listByIds(entityIds, storageCode);
        Set<Long> foundIds = found == null
                ? Set.of()
                : found.stream()
                        .map(EntityDO::getId)
                        .filter(id -> id != null)
                        .collect(Collectors.toCollection(HashSet::new));
        List<Long> missing = entityIds.stream().filter(id -> !foundIds.contains(id)).toList();
        if (!missing.isEmpty()) {
            throw exception(SCOPE_ENTITY_NOT_IN_BASE, missing.get(0));
        }
    }

    private List<Long> normalizeEntityIds(List<Long> entityIds) {
        if (entityIds == null || entityIds.isEmpty()) {
            throw exception(SCOPE_ENTITY_IDS_EMPTY);
        }
        LinkedHashSet<Long> set = new LinkedHashSet<>();
        for (Long id : entityIds) {
            if (id != null && id > 0) {
                set.add(id);
            }
        }
        if (set.isEmpty()) {
            throw exception(SCOPE_ENTITY_IDS_EMPTY);
        }
        return new ArrayList<>(set);
    }
}
