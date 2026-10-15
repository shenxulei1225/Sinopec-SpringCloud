package cn.cheers.x.module.dynamicbusiness.service.model.relation;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelModelAssociationRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelModelRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelModelRelationMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeResolver;
import cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantAssociationTableService;
import cn.cheers.x.module.dynamicbusiness.service.model.core.ModelCoreService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 模型—模型关联。
 *
 * <p>同类型、异类型都写同一张表，用来源类型和目标类型区分。
 * 解除时物理删除这一行。再挂同一对时插入新行。缺型号、自己指向自己、类型为空都拒绝。</p>
 */
@Service
@Validated
public class ModelModelRelationServiceImpl implements ModelModelRelationService {

    private static final int SORT_STEP = 1024;

    @Resource
    private ModelModelRelationMapper relationMapper;

    @Resource
    private ModelCoreService modelCoreService;

    @Resource
    private EntityTypeScopeResolver entityTypeScopeResolver;

    @Resource
    private TenantAssociationTableService tenantAssociationTableService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelModelAssociationRespVO associate(Long sourceModelId, Long targetModelId,
                                                 String sourceEntityTypeCode, String targetEntityTypeCode) {
        tenantAssociationTableService.ensureCurrentTenantAssociationTables();
        String sourceType = resolveStorage(sourceEntityTypeCode, "sourceEntityTypeCode");
        String targetType = resolveStorage(targetEntityTypeCode, "targetEntityTypeCode");
        requirePair(sourceModelId, targetModelId, sourceType, targetType);
        write(sourceModelId, targetModelId, sourceType, targetType);
        return result("ASSOCIATE", sourceModelId, targetModelId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelModelAssociationRespVO disassociate(Long sourceModelId, Long targetModelId,
                                                    String sourceEntityTypeCode, String targetEntityTypeCode) {
        tenantAssociationTableService.ensureCurrentTenantAssociationTables();
        String sourceType = resolveStorage(sourceEntityTypeCode, "sourceEntityTypeCode");
        String targetType = resolveStorage(targetEntityTypeCode, "targetEntityTypeCode");
        requireIds(sourceModelId, targetModelId);
        relationMapper.deleteLink(sourceModelId, targetModelId, sourceType, targetType);
        return result("DISASSOCIATE", sourceModelId, targetModelId);
    }

    @Override
    public List<Long> listTargetModelIds(Long sourceModelId, String sourceEntityTypeCode, String targetEntityTypeCode) {
        if (sourceModelId == null) {
            throw new ServiceException(400, "来源型号不能为空");
        }
        tenantAssociationTableService.ensureCurrentTenantAssociationTables();
        String sourceType = resolveStorage(sourceEntityTypeCode, "sourceEntityTypeCode");
        String targetType = resolveStorage(targetEntityTypeCode, "targetEntityTypeCode");
        return relationMapper.selectBySource(sourceModelId, sourceType, targetType).stream()
                .map(ModelModelRelationDO::getTargetModelId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    @Override
    public List<Long> listSourceModelIds(Long targetModelId, String targetEntityTypeCode, String sourceEntityTypeCode) {
        if (targetModelId == null) {
            throw new ServiceException(400, "目标型号不能为空");
        }
        tenantAssociationTableService.ensureCurrentTenantAssociationTables();
        String targetType = resolveStorage(targetEntityTypeCode, "targetEntityTypeCode");
        String sourceType = resolveStorage(sourceEntityTypeCode, "sourceEntityTypeCode");
        return relationMapper.selectByTarget(targetModelId, targetType, sourceType).stream()
                .map(ModelModelRelationDO::getSourceModelId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceTargets(String entityTypeCode, Long sourceModelId, List<Long> targetModelIds) {
        String type = storageType(entityTypeCode);
        if (sourceModelId == null) {
            throw new ServiceException(400, "来源型号不能为空");
        }
        List<Long> next = distinctIds(targetModelIds);
        if (next.contains(sourceModelId)) {
            throw new ServiceException(400, "型号不能关联自己");
        }
        List<Long> current = listTargetModelIds(sourceModelId, type, type);
        for (Long targetModelId : current) {
            if (!next.contains(targetModelId)) {
                disassociate(sourceModelId, targetModelId, type, type);
            }
        }
        for (Long targetModelId : next) {
            if (!current.contains(targetModelId)) {
                associate(sourceModelId, targetModelId, type, type);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceSources(String entityTypeCode, Long targetModelId, List<Long> sourceModelIds) {
        String type = storageType(entityTypeCode);
        if (targetModelId == null) {
            throw new ServiceException(400, "目标型号不能为空");
        }
        List<Long> next = distinctIds(sourceModelIds);
        if (next.contains(targetModelId)) {
            throw new ServiceException(400, "型号不能关联自己");
        }
        List<Long> current = listSourceModelIds(targetModelId, type, type);
        for (Long sourceModelId : current) {
            if (!next.contains(sourceModelId)) {
                disassociate(sourceModelId, targetModelId, type, type);
            }
        }
        for (Long sourceModelId : next) {
            if (!current.contains(sourceModelId)) {
                associate(sourceModelId, targetModelId, type, type);
            }
        }
    }

    private String storageType(String entityTypeCode) {
        tenantAssociationTableService.ensureCurrentTenantAssociationTables();
        return resolveStorage(entityTypeCode, "entityTypeCode");
    }

    private static List<Long> distinctIds(List<Long> modelIds) {
        if (modelIds == null || modelIds.isEmpty()) {
            return List.of();
        }
        LinkedHashSet<Long> ids = new LinkedHashSet<>();
        for (Long modelId : modelIds) {
            if (modelId == null || modelId <= 0) {
                throw new ServiceException(400, "型号编号不正确");
            }
            ids.add(modelId);
        }
        return new ArrayList<>(ids);
    }

    private void write(Long sourceModelId, Long targetModelId, String sourceType, String targetType) {
        if (relationMapper.selectActive(sourceModelId, targetModelId, sourceType, targetType) != null) {
            return;
        }
        int sort = nextSort(relationMapper.maxSort(sourceModelId, sourceType, targetType));
        relationMapper.insert(ModelModelRelationDO.builder()
                .sourceEntityTypeCode(sourceType)
                .sourceModelId(sourceModelId)
                .targetEntityTypeCode(targetType)
                .targetModelId(targetModelId)
                .sort(sort)
                .build());
    }

    private void requirePair(Long sourceModelId, Long targetModelId, String sourceType, String targetType) {
        requireIds(sourceModelId, targetModelId);
        if (sourceModelId.equals(targetModelId)) {
            throw new ServiceException(400, "型号不能关联自己");
        }
        requireModel(sourceModelId, sourceType);
        requireModel(targetModelId, targetType);
    }

    private void requireIds(Long sourceModelId, Long targetModelId) {
        if (sourceModelId == null || targetModelId == null) {
            throw new ServiceException(400, "来源型号和目标型号都不能为空");
        }
    }

    private void requireModel(Long modelId, String entityTypeCode) {
        Set<Long> existing = modelCoreService.filterExistingModelIds(List.of(modelId), entityTypeCode);
        if (existing == null || !existing.contains(modelId)) {
            throw new ServiceException(404, "型号不存在或不属于类型 " + entityTypeCode);
        }
    }

    private String resolveStorage(String entityTypeCode, String label) {
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new ServiceException(400, label + " 不能为空");
        }
        String storage = entityTypeScopeResolver.resolveStorageEntityTypeCode(entityTypeCode.trim());
        if (!StringUtils.hasText(storage)) {
            throw new ServiceException(400, label + " 不能为空");
        }
        return storage.trim();
    }

    private static int nextSort(Integer max) {
        return (max == null ? 0 : max) + SORT_STEP;
    }

    private static ModelModelAssociationRespVO result(String operation, Long sourceModelId, Long targetModelId) {
        return ModelModelAssociationRespVO.builder()
                .operationType(operation)
                .sourceModelId(sourceModelId)
                .targetModelId(targetModelId)
                .successCount(1)
                .build();
    }
}
