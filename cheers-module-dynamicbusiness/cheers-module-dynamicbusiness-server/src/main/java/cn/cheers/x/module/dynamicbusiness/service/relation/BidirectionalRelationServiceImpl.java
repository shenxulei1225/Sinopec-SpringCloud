package cn.cheers.x.module.dynamicbusiness.service.relation;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.bidirectional.*;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityFieldIndexDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation.RelationFieldLibraryDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityFieldIndexMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.relation.RelationFieldLibraryMapper;
import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityDoVoHelper;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Lazy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BidirectionalRelationServiceImpl implements BidirectionalRelationService {

    @Resource
    private EntityCoreService entityCoreService;

    @Resource
    @Lazy
    private EntityService entityService;

    @Resource
    private CustomFieldValidationService customFieldValidationService;
    @Resource
    private EntityFieldIndexMapper entityFieldIndexMapper;
    @Resource
    private EntityRelationMapper entityRelationMapper;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    @Resource
    private FieldMapper fieldMapper;

    @Resource
    private RelationFieldLibraryMapper relationFieldLibraryMapper;

    @Resource
    private ModelRelationMapper modelRelationMapper;

    private final Cache<String, List<RelationDiscoveryVO>> discoveryCache = Caffeine.newBuilder()
            .maximumSize(1000).expireAfterWrite(5, TimeUnit.MINUTES).build();
    
    /**
     * 统计结果缓存
     * Key: "stats:{entityId}" 或 "count:{targetEntityId}:{sourceModelCode}:{fieldCode}"
     * Value: 统计结果
     * 
     * 缓存失效机制：Entity 变更时通过 clearStatisticsCache() 清除
     */
    private final Cache<String, Object> statisticsCache = Caffeine.newBuilder()
            .maximumSize(5000).expireAfterWrite(5, TimeUnit.MINUTES).build();

    @Override
    @Deprecated
    public List<RelatedEntityVO> getForwardRelations(Long entityId) {
        // 已废弃：无法确定 entityTypeCode，无法正确路由到动态表
        // 尝试从通用表查询，如果失败则返回空列表
        log.warn("[getForwardRelations][使用了已废弃的方法，entityId={}，建议使用 getForwardRelations(entityId, entityTypeCode)]", entityId);
        try {
            // 尝试使用默认的 entityTypeCode（通用表）
            EntityDO entityDO = entityCoreService.get(entityId, null);
            EntityRespVO entity = entityDO != null ? EntityDoVoHelper.toRespVO(entityDO, customFieldValidationService) : null;
            if (entity == null) return Collections.emptyList();
            ModelDO model = modelMapper.selectById(entity.getModelId());
            if (model == null) return Collections.emptyList();
            return getForwardRelationsInternal(entityId, entity, model);
        } catch (Exception e) {
            log.warn("[getForwardRelations][查询失败，entityId={}，可能是动态表实体，请使用带 entityTypeCode 的方法]", entityId);
            return Collections.emptyList();
        }
    }

    @Override
    public List<RelatedEntityVO> getForwardRelations(Long entityId, String entityTypeCode) {
        EntityDO entityDO = entityCoreService.get(entityId, entityTypeCode);
        EntityRespVO entity = entityDO != null ? EntityDoVoHelper.toRespVO(entityDO, customFieldValidationService) : null;
        if (entity == null) return Collections.emptyList();
        ModelDO model = modelMapper.selectById(entity.getModelId());
        if (model == null) return Collections.emptyList();
        return getForwardRelationsInternal(entityId, entity, model);
    }

    /**
     * 获取正向关联的内部实现
     */
    private List<RelatedEntityVO> getForwardRelationsInternal(Long entityId, EntityRespVO entity, ModelDO model) {
        List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelId(model.getId());
        List<RelatedEntityVO> result = new ArrayList<>();
        for (ModelFieldAssignmentDO assignment : assignments) {
            FieldDO field = fieldMapper.selectById(assignment.getFieldId());
            if (field == null || !FieldTypeEnum.ENTITY_REF.getCode().equals(field.getType())) continue;
            EntityFieldIndexDO indexRecord = entityFieldIndexMapper.selectByEntityIdAndFieldCode(entityId, field.getCode());
            if (indexRecord == null || StrUtil.isBlank(indexRecord.getValueString())) continue;
            Long refEntityId = parseEntityRefValue(indexRecord.getValueString());
            if (refEntityId == null) continue;
            String refEntityTypeCode = resolveRefEntityTypeCode(field, refEntityId, assignment);
            if (refEntityTypeCode == null) {
                log.warn("[getForwardRelationsInternal][无法确定引用实体的 entityTypeCode，fieldCode={}, refEntityId={}]", field.getCode(), refEntityId);
                continue;
            }
            EntityDO refEntityDO = entityCoreService.get(refEntityId, refEntityTypeCode);
            EntityRespVO refEntity = refEntityDO != null ? EntityDoVoHelper.toRespVO(refEntityDO, customFieldValidationService) : null;
            if (refEntity == null) continue;
            ModelDO refModel = modelMapper.selectById(refEntity.getModelId());
            if (refModel == null) continue;
            result.add(RelatedEntityVO.builder().modelCode(refModel.getCode()).modelName(refModel.getName())
                    .fieldCode(field.getCode()).fieldName(field.getName()).count(1L)
                    .entityTypeCode(refModel.getEntityTypeCode()).autoGenerated(false)
                    .entities(Collections.singletonList(buildEntitySimpleVO(refEntity, refModel))).build());
        }
        return result;
    }

    @Override
    @Deprecated
    public List<RelatedEntityVO> getReverseRelations(Long entityId) {
        // 已废弃：无法确定 entityTypeCode，无法正确路由到动态表
        log.warn("[getReverseRelations][使用了已废弃的方法，entityId={}，建议使用 getReverseRelations(entityId, entityTypeCode)]", entityId);
        try {
            // 尝试使用默认的 entityTypeCode（通用表）
            EntityDO entityDO = entityCoreService.get(entityId, null);
            EntityRespVO entity = entityDO != null ? EntityDoVoHelper.toRespVO(entityDO, customFieldValidationService) : null;
            if (entity == null) return Collections.emptyList();
            ModelDO model = modelMapper.selectById(entity.getModelId());
            if (model == null) return Collections.emptyList();
            return getReverseRelationsInternal(entityId, model);
        } catch (Exception e) {
            log.warn("[getReverseRelations][查询失败，entityId={}，可能是动态表实体，请使用带 entityTypeCode 的方法]", entityId);
            return Collections.emptyList();
        }
    }

    @Override
    public List<RelatedEntityVO> getReverseRelations(Long entityId, String entityTypeCode) {
        EntityDO entityDO = entityCoreService.get(entityId, entityTypeCode);
        EntityRespVO entity = entityDO != null ? EntityDoVoHelper.toRespVO(entityDO, customFieldValidationService) : null;
        if (entity == null) return Collections.emptyList();
        ModelDO model = modelMapper.selectById(entity.getModelId());
        if (model == null) return Collections.emptyList();
        return getReverseRelationsInternal(entityId, model);
    }

    /**
     * 获取反向关联的内部实现
     * 
     * 优化：优先使用 EntityRelationMapper 查询，移除 EntityFieldIndex 扫描逻辑
     * 需求：FR-BDA-074, FR-BDA-091
     */
    private List<RelatedEntityVO> getReverseRelationsInternal(Long entityId, ModelDO model) {
        // 优化路径：优先从 EntityRelationDO 表查询
        List<EntityRelationDO> relations = entityRelationMapper.selectByTargetEntityId(entityId);
        
        if (CollUtil.isNotEmpty(relations)) {
            // 使用 EntityRelationDO 表数据（优化路径）
            // 按 sourceModelCode + fieldCode 分组统计
            Map<String, List<EntityRelationDO>> groupedRelations = relations.stream()
                    .filter(r -> StrUtil.isNotBlank(r.getSourceModelCode()) && StrUtil.isNotBlank(r.getFieldCode()))
                    .collect(Collectors.groupingBy(r -> r.getSourceModelCode() + ":" + r.getFieldCode()));
            
            List<RelatedEntityVO> result = new ArrayList<>();
            for (Map.Entry<String, List<EntityRelationDO>> entry : groupedRelations.entrySet()) {
                List<EntityRelationDO> groupRelations = entry.getValue();
                EntityRelationDO firstRelation = groupRelations.get(0);
                
                // 获取 Model 信息
                ModelDO sourceModel = modelMapper.selectByCode(firstRelation.getSourceModelCode());
                String modelName = sourceModel != null ? sourceModel.getName() : firstRelation.getSourceModelCode();
                String entityTypeCode = firstRelation.getSourceEntityTypeCode();
                if (entityTypeCode == null && sourceModel != null) {
                    entityTypeCode = sourceModel.getEntityTypeCode();
                }
                
                // 获取字段信息
                FieldDO field = fieldMapper.selectByCode(firstRelation.getFieldCode());
                String fieldName = field != null ? field.getName() : firstRelation.getFieldCode();
                
                result.add(RelatedEntityVO.builder()
                        .modelCode(firstRelation.getSourceModelCode())
                        .modelName(modelName)
                        .fieldCode(firstRelation.getFieldCode())
                        .fieldName(fieldName)
                        .count((long) groupRelations.size())
                        .entityTypeCode(entityTypeCode)
                        .autoGenerated(false)
                        .build());
            }
            
            if (!result.isEmpty()) {
                return result;
            }
        }
        
        // 回退到旧逻辑：使用关联发现机制（兼容未同步的历史数据）
        List<RelationDiscoveryVO> discoveries = discoverRelations(model.getCode(), true);
        if (CollUtil.isEmpty(discoveries)) return Collections.emptyList();
        List<RelatedEntityVO> result = new ArrayList<>();
        for (RelationDiscoveryVO discovery : discoveries) {
            Long count = countReverseRelations(entityId, discovery.getSourceModelCode(), discovery.getFieldCode());
            if (count == 0) continue;
            result.add(RelatedEntityVO.builder().modelCode(discovery.getSourceModelCode())
                    .modelName(discovery.getSourceModelName()).fieldCode(discovery.getFieldCode())
                    .fieldName(discovery.getFieldName()).count(count)
                    .entityTypeCode(discovery.getSourceEntityTypeCode())
                    .entityTypeName(discovery.getSourceEntityTypeName())
                    .autoGenerated(discovery.getAutoGenerated()).build());
        }
        return result;
    }

    @Override
    @Deprecated
    public PageResult<EntitySimpleVO> getReverseRelationsByModel(Long entityId, String modelCode, PageParam pageParam) {
        // 已废弃：无法确定 entityTypeCode，无法正确路由到动态表
        log.warn("[getReverseRelationsByModel][使用了已废弃的方法，entityId={}，建议使用 getReverseRelationsByModel(entityId, entityTypeCode, modelCode, pageParam)]", entityId);
        try {
            // 尝试使用默认的 entityTypeCode（通用表）
            EntityDO entityDO = entityCoreService.get(entityId, null);
            EntityRespVO entity = entityDO != null ? EntityDoVoHelper.toRespVO(entityDO, customFieldValidationService) : null;
            if (entity == null) return new PageResult<>(Collections.emptyList(), 0L);
            ModelDO targetModel = modelMapper.selectById(entity.getModelId());
            if (targetModel == null) return new PageResult<>(Collections.emptyList(), 0L);
            return getReverseRelationsByModelInternal(entityId, targetModel, modelCode, pageParam);
        } catch (Exception e) {
            log.warn("[getReverseRelationsByModel][查询失败，entityId={}，可能是动态表实体，请使用带 entityTypeCode 的方法]", entityId);
            return new PageResult<>(Collections.emptyList(), 0L);
        }
    }

    @Override
    public PageResult<EntitySimpleVO> getReverseRelationsByModel(Long entityId, String entityTypeCode, String modelCode, PageParam pageParam) {
        EntityDO entityDO = entityCoreService.get(entityId, entityTypeCode);
        EntityRespVO entity = entityDO != null ? EntityDoVoHelper.toRespVO(entityDO, customFieldValidationService) : null;
        if (entity == null) return new PageResult<>(Collections.emptyList(), 0L);
        ModelDO targetModel = modelMapper.selectById(entity.getModelId());
        if (targetModel == null) return new PageResult<>(Collections.emptyList(), 0L);
        return getReverseRelationsByModelInternal(entityId, targetModel, modelCode, pageParam);
    }

    /**
     * 按 Model 获取反向关联的内部实现
     * 
     * 优化：优先使用 EntityRelationMapper 查询，移除 EntityFieldIndex 扫描逻辑
     * 需求：FR-BDA-074, FR-BDA-091
     */
    private PageResult<EntitySimpleVO> getReverseRelationsByModelInternal(Long entityId, ModelDO targetModel, String modelCode, PageParam pageParam) {
        ModelDO sourceModel = modelMapper.selectByCode(modelCode);
        if (sourceModel == null) return new PageResult<>(Collections.emptyList(), 0L);
        
        // 获取 sourceModel 的 entityTypeCode
        String sourceEntityTypeCode = sourceModel.getEntityTypeCode();
        if (sourceEntityTypeCode == null) {
            log.warn("[getReverseRelationsByModelInternal][sourceModel 没有配置 entityTypeCode，modelCode={}]", sourceModel.getCode());
        }
        
        // 优化路径：优先从 EntityRelationDO 表查询
        List<EntityRelationDO> relations = entityRelationMapper.selectByTargetEntityIdAndSourceModelCode(entityId, modelCode);
        
        if (CollUtil.isNotEmpty(relations)) {
            // 使用 EntityRelationDO 表数据（优化路径）
            int total = relations.size();
            int start = (pageParam.getPageNo() - 1) * pageParam.getPageSize();
            int end = Math.min(start + pageParam.getPageSize(), total);
            if (start >= total) return new PageResult<>(Collections.emptyList(), (long) total);
            
            List<EntityRelationDO> pagedRelations = relations.subList(start, end);
            List<Long> entityIds = pagedRelations.stream()
                    .map(EntityRelationDO::getSourceEntityId)
                    .distinct()
                    .collect(Collectors.toList());
            
            List<EntitySimpleVO> entities = new ArrayList<>();
            for (Long eid : entityIds) {
                try {
                    EntityDO eDO = entityCoreService.get(eid, sourceEntityTypeCode);
                    if (eDO != null) {
                        EntityRespVO e = EntityDoVoHelper.toRespVO(eDO, customFieldValidationService);
                        entities.add(buildEntitySimpleVO(e, sourceModel));
                    }
                } catch (Exception ex) {
                    log.warn("[getReverseRelationsByModelInternal][查询实体失败，entityId={}, entityTypeCode={}]", eid, sourceEntityTypeCode);
                }
            }
            return new PageResult<>(entities, (long) total);
        }
        
        // 回退到旧逻辑：使用关联发现机制（兼容未同步的历史数据）
        List<RelationDiscoveryVO> discoveries = discoverRelations(targetModel.getCode(), true);
        RelationDiscoveryVO discovery = discoveries.stream().filter(d -> d.getSourceModelCode().equals(modelCode)).findFirst().orElse(null);
        if (discovery == null) return new PageResult<>(Collections.emptyList(), 0L);
        List<EntityFieldIndexDO> indexRecords = findReverseRelationIndexRecords(entityId, sourceModel.getId(), discovery.getFieldCode());
        int total = indexRecords.size();
        int start = (pageParam.getPageNo() - 1) * pageParam.getPageSize();
        int end = Math.min(start + pageParam.getPageSize(), total);
        if (start >= total) return new PageResult<>(Collections.emptyList(), (long) total);
        List<EntityFieldIndexDO> pagedRecords = indexRecords.subList(start, end);
        List<Long> entityIds = pagedRecords.stream().map(EntityFieldIndexDO::getEntityId).distinct().collect(Collectors.toList());
        List<EntitySimpleVO> entities = new ArrayList<>();
        for (Long eid : entityIds) {
            try {
                EntityDO eDO = entityCoreService.get(eid, sourceEntityTypeCode);
                if (eDO != null) {
                    EntityRespVO e = EntityDoVoHelper.toRespVO(eDO, customFieldValidationService);
                    entities.add(buildEntitySimpleVO(e, sourceModel));
                }
            } catch (Exception ex) {
                log.warn("[getReverseRelationsByModelInternal][查询实体失败，entityId={}, entityTypeCode={}]", eid, sourceEntityTypeCode);
            }
        }
        return new PageResult<>(entities, (long) total);
    }

    @Override
    @Deprecated
    public RelationStatisticsVO getRelationStatistics(Long entityId) {
        // 已废弃：无法确定 entityTypeCode，无法正确路由到动态表
        log.warn("[getRelationStatistics][使用了已废弃的方法，entityId={}，建议使用 getRelationStatistics(entityId, entityTypeCode)]", entityId);
        try {
            // 尝试使用默认的 entityTypeCode（通用表）
            EntityDO entityDO = entityCoreService.get(entityId, null);
            EntityRespVO entity = entityDO != null ? EntityDoVoHelper.toRespVO(entityDO, customFieldValidationService) : null;
            if (entity == null) return buildEmptyStatistics(entityId);
            return getRelationStatisticsInternal(entityId, entity);
        } catch (Exception e) {
            log.warn("[getRelationStatistics][查询失败，entityId={}，可能是动态表实体，请使用带 entityTypeCode 的方法]", entityId);
            return buildEmptyStatistics(entityId);
        }
    }

    @Override
    public RelationStatisticsVO getRelationStatistics(Long entityId, String entityTypeCode) {
        EntityDO entityDO = entityCoreService.get(entityId, entityTypeCode);
        EntityRespVO entity = entityDO != null ? EntityDoVoHelper.toRespVO(entityDO, customFieldValidationService) : null;
        if (entity == null) return buildEmptyStatistics(entityId);
        return getRelationStatisticsInternal(entityId, entity);
    }

    /**
     * 构建空的统计结果
     */
    private RelationStatisticsVO buildEmptyStatistics(Long entityId) {
        return RelationStatisticsVO.builder().entityId(entityId).totalRelatedEntities(0L)
                .forwardRelationCount(0L).reverseRelationCount(0L).byModel(Collections.emptyMap()).byModelName(Collections.emptyMap()).build();
    }

    /**
     * 获取关联统计的内部实现
     * 
     * 优化：使用 EntityRelationMapper.countByTargetEntityIdGroupBySourceModel() 替代循环查询
     * 优化为单次 GROUP BY 查询
     * 需求：FR-BDA-075, FR-BDA-092
     */
    private RelationStatisticsVO getRelationStatisticsInternal(Long entityId, EntityRespVO entity) {
        // 检查缓存
        String cacheKey = "stats:" + entityId;
        RelationStatisticsVO cachedStats = (RelationStatisticsVO) statisticsCache.getIfPresent(cacheKey);
        if (cachedStats != null) {
            return cachedStats;
        }
        
        // 获取 Model 以确定 entityTypeCode
        ModelDO model = modelMapper.selectById(entity.getModelId());
        String entityTypeCode = model != null ? model.getEntityTypeCode() : null;
        
        // 获取正向关联统计
        List<RelatedEntityVO> forwardRelations = entityTypeCode != null 
            ? getForwardRelations(entityId, entityTypeCode) 
            : getForwardRelations(entityId);
        long forwardCount = forwardRelations.stream().mapToLong(RelatedEntityVO::getCount).sum();
        
        // 优化：使用单次 GROUP BY 查询获取反向关联统计
        Map<String, Long> byModel = new HashMap<>();
        Map<String, Long> byModelName = new HashMap<>();
        long reverseCount = 0L;
        
        // 尝试从 EntityRelationDO 表获取分组统计（优化路径）
        List<Map<String, Object>> groupedStats = entityRelationMapper.countByTargetEntityIdGroupBySourceModel(entityId);
        
        if (CollUtil.isNotEmpty(groupedStats)) {
            // 使用优化的 GROUP BY 查询结果
            for (Map<String, Object> stat : groupedStats) {
                String sourceModelCode = (String) stat.get("source_model_code");
                Long count = ((Number) stat.get("count")).longValue();
                
                if (sourceModelCode != null && count > 0) {
                    byModel.put(sourceModelCode, count);
                    reverseCount += count;
                    
                    // 获取 Model 名称
                    ModelDO sourceModel = modelMapper.selectByCode(sourceModelCode);
                    if (sourceModel != null) {
                        byModelName.put(sourceModel.getName(), count);
                    }
                }
            }
        } else {
            // 回退到旧逻辑：遍历关联发现结果（兼容未同步的历史数据）
            List<RelatedEntityVO> reverseRelations = entityTypeCode != null 
                ? getReverseRelations(entityId, entityTypeCode) 
                : getReverseRelations(entityId);
            reverseCount = reverseRelations.stream().mapToLong(RelatedEntityVO::getCount).sum();
            
            for (RelatedEntityVO relation : reverseRelations) {
                byModel.put(relation.getModelCode(), relation.getCount());
                byModelName.put(relation.getModelName(), relation.getCount());
            }
        }
        
        RelationStatisticsVO result = RelationStatisticsVO.builder()
                .entityId(entityId)
                .totalRelatedEntities(forwardCount + reverseCount)
                .forwardRelationCount(forwardCount)
                .reverseRelationCount(reverseCount)
                .byModel(byModel)
                .byModelName(byModelName)
                .build();
        
        // 缓存结果
        statisticsCache.put(cacheKey, result);
        
        return result;
    }

    @Override
    public List<AggregateResultVO> aggregateByRelation(AggregateQueryReqVO reqVO) {
        ModelDO model = modelMapper.selectByCode(reqVO.getModelCode());
        if (model == null) return Collections.emptyList();
        FieldDO groupByField = fieldMapper.selectByCode(reqVO.getGroupByFieldCode());
        if (groupByField == null || !FieldTypeEnum.ENTITY_REF.getCode().equals(groupByField.getType())) {
            log.warn("[aggregateByRelation][分组字段不存在或不是 ENTITY_REF 类型: {}]", reqVO.getGroupByFieldCode());
            return Collections.emptyList();
        }
        List<EntityFieldIndexDO> indexRecords = entityFieldIndexMapper.selectByModelIdAndFieldCode(model.getId(), reqVO.getGroupByFieldCode());
        if (CollUtil.isEmpty(indexRecords)) return Collections.emptyList();
        Map<Long, List<EntityFieldIndexDO>> groupedRecords = indexRecords.stream()
                .filter(r -> StrUtil.isNotBlank(r.getValueString())).filter(r -> parseEntityRefValue(r.getValueString()) != null)
                .collect(Collectors.groupingBy(r -> parseEntityRefValue(r.getValueString())));
        List<AggregateResultVO> result = new ArrayList<>();
        for (Map.Entry<Long, List<EntityFieldIndexDO>> entry : groupedRecords.entrySet()) {
            Long groupKey = entry.getKey();
            List<EntityFieldIndexDO> records = entry.getValue();
            ModelFieldAssignmentDO groupAssignment = modelFieldAssignmentMapper.selectByModelIdAndFieldId(
                    model.getId(), groupByField.getId());
            String refEntityTypeCode = resolveRefEntityTypeCode(groupByField, groupKey, groupAssignment);
            EntityRespVO relatedEntity = null;
            if (refEntityTypeCode != null) {
                try {
                    relatedEntity = entityService.get(groupKey, refEntityTypeCode);
                } catch (Exception ex) {
                    log.warn("[aggregateByRelation][查询引用实体失败，groupKey={}, entityTypeCode={}]", groupKey, refEntityTypeCode);
                }
            } else {
                log.warn("[aggregateByRelation][无法确定引用实体的 entityTypeCode，groupKey={}]", groupKey);
            }
            String groupKeyDisplay = relatedEntity != null ? relatedEntity.getName() : String.valueOf(groupKey);
            ModelDO relatedModel = relatedEntity != null ? modelMapper.selectById(relatedEntity.getModelId()) : null;
            BigDecimal aggregateValue = calculateAggregateValue(reqVO.getAggregateType(), records, reqVO.getTargetFieldCode());
            result.add(AggregateResultVO.builder().groupKey(groupKey).groupKeyDisplay(groupKeyDisplay).aggregateValue(aggregateValue)
                    .aggregateType(reqVO.getAggregateType()).recordCount((long) records.size())
                    .relatedModelCode(relatedModel != null ? relatedModel.getCode() : null)
                    .relatedModelName(relatedModel != null ? relatedModel.getName() : null).build());
        }
        if (reqVO.getLimit() != null && reqVO.getLimit() > 0 && result.size() > reqVO.getLimit()) result = result.subList(0, reqVO.getLimit());
        return result;
    }

    @Override
    public List<RelationDiscoveryVO> discoverRelations(String modelCode) { return discoverRelations(modelCode, false); }

    @Override
    public List<RelationDiscoveryVO> discoverRelations(String modelCode, boolean useCache) {
        if (useCache) {
            List<RelationDiscoveryVO> cached = discoveryCache.getIfPresent(modelCode);
            if (cached != null) return cached;
        }
        ModelDO targetModel = modelMapper.selectByCode(modelCode);
        if (targetModel == null) return Collections.emptyList();
        List<RelationDiscoveryVO> result = new ArrayList<>();
        List<ModelDO> allModels = modelMapper.selectList();
        for (ModelDO sourceModel : allModels) {
            if (sourceModel.getCode().equals(modelCode)) continue;
            List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelId(sourceModel.getId());
            for (ModelFieldAssignmentDO assignment : assignments) {
                FieldDO field = fieldMapper.selectById(assignment.getFieldId());
                if (field == null || !FieldTypeEnum.ENTITY_REF.getCode().equals(field.getType())) continue;
                if (!isAssignmentReferencingTargetModel(assignment, targetModel)) continue;
                result.add(RelationDiscoveryVO.builder().sourceModelId(sourceModel.getId()).sourceModelCode(sourceModel.getCode())
                        .sourceModelName(sourceModel.getName()).sourceEntityTypeCode(sourceModel.getEntityTypeCode())
                        .fieldId(field.getId()).fieldCode(field.getCode()).fieldName(field.getName())
                        .targetModelCode(modelCode).targetModelName(targetModel.getName()).autoGenerated(false)
                        .refLibraryId(assignment.getRefLibraryId()).build());
            }
        }
        if (useCache) discoveryCache.put(modelCode, result);
        return result;
    }

    @Override
    public void clearDiscoveryCache(String modelCode) {
        if (modelCode == null) { discoveryCache.invalidateAll(); log.info("[clearDiscoveryCache][清除所有关联发现缓存]"); }
        else { discoveryCache.invalidate(modelCode); log.info("[clearDiscoveryCache][清除关联发现缓存，modelCode={}]", modelCode); }
    }

    @Override
    public void clearStatisticsCache(Long entityId) {
        if (entityId == null) {
            statisticsCache.invalidateAll();
            log.info("[clearStatisticsCache][清除所有统计缓存]");
        } else {
            // 清除与该实体相关的所有缓存
            // 1. 清除该实体的统计缓存
            statisticsCache.invalidate("stats:" + entityId);
            
            // 2. 清除所有以该实体为目标的计数缓存
            // 由于 Caffeine 不支持前缀删除，我们需要遍历所有 key
            // 为了性能，这里只清除统计缓存，计数缓存会在过期后自动失效
            statisticsCache.asMap().keySet().removeIf(key -> 
                key.startsWith("count:" + entityId + ":") || 
                key.startsWith("stats:" + entityId));
            
            log.info("[clearStatisticsCache][清除实体统计缓存，entityId={}]", entityId);
        }
    }

    @Override
    @Deprecated
    public EntityRelationInfoVO getEntityRelationInfo(Long entityId) {
        // 已废弃：无法确定 entityTypeCode，无法正确路由到动态表
        log.warn("[getEntityRelationInfo][使用了已废弃的方法，entityId={}，建议使用 getEntityRelationInfo(entityId, entityTypeCode)]", entityId);
        try {
            // 尝试使用默认的 entityTypeCode（通用表）
            EntityRespVO entity = entityService.get(entityId, null);
            if (entity == null) return null;
            ModelDO model = modelMapper.selectById(entity.getModelId());
            if (model == null) return null;
            return getEntityRelationInfoInternal(entityId, entity, model);
        } catch (Exception e) {
            log.warn("[getEntityRelationInfo][查询失败，entityId={}，可能是动态表实体，请使用带 entityTypeCode 的方法]", entityId);
            return null;
        }
    }

    @Override
    public EntityRelationInfoVO getEntityRelationInfo(Long entityId, String entityTypeCode) {
        EntityRespVO entity = entityService.get(entityId, entityTypeCode);
        if (entity == null) return null;
        ModelDO model = modelMapper.selectById(entity.getModelId());
        if (model == null) return null;
        return getEntityRelationInfoInternal(entityId, entity, model);
    }

    /**
     * 获取实体关联信息的内部实现
     */
    private EntityRelationInfoVO getEntityRelationInfoInternal(Long entityId, EntityRespVO entity, ModelDO model) {
        String entityTypeCode = model.getEntityTypeCode();
        return EntityRelationInfoVO.builder().entityId(entityId).entityName(entity.getName()).modelCode(model.getCode())
                .modelName(model.getName())
                .forwardRelations(entityTypeCode != null ? getForwardRelations(entityId, entityTypeCode) : getForwardRelations(entityId))
                .reverseRelations(entityTypeCode != null ? getReverseRelations(entityId, entityTypeCode) : getReverseRelations(entityId))
                .statistics(entityTypeCode != null ? getRelationStatistics(entityId, entityTypeCode) : getRelationStatistics(entityId)).build();
    }

    @Override
    @Deprecated
    public Map<Long, Long> batchGetReverseRelationCounts(List<Long> entityIds) {
        // 已废弃：无法确定 entityTypeCode，无法正确路由到动态表
        log.warn("[batchGetReverseRelationCounts][使用了已废弃的方法，建议使用 batchGetReverseRelationCounts(entityTypeCode, entityIds)]");
        if (CollUtil.isEmpty(entityIds)) return Collections.emptyMap();
        Map<Long, Long> result = new HashMap<>();
        for (Long entityId : entityIds) {
            // 使用已废弃的方法，会记录警告
            List<RelatedEntityVO> reverseRelations = getReverseRelations(entityId);
            result.put(entityId, reverseRelations.stream().mapToLong(RelatedEntityVO::getCount).sum());
        }
        return result;
    }

    @Override
    public Map<Long, Long> batchGetReverseRelationCounts(String entityTypeCode, List<Long> entityIds) {
        if (CollUtil.isEmpty(entityIds)) return Collections.emptyMap();
        Map<Long, Long> result = new HashMap<>();
        for (Long entityId : entityIds) {
            List<RelatedEntityVO> reverseRelations = getReverseRelations(entityId, entityTypeCode);
            result.put(entityId, reverseRelations.stream().mapToLong(RelatedEntityVO::getCount).sum());
        }
        return result;
    }

    private Long parseEntityRefValue(String value) {
        if (StrUtil.isBlank(value)) return null;
        try { return Long.parseLong(value); } catch (NumberFormatException e) { log.warn("[parseEntityRefValue][无法解析: {}]", value); return null; }
    }

    /**
     * 根据模型字段分配解析引用实体所在 entityTypeCode。
     */
    private String resolveRefEntityTypeCode(FieldDO field, Long refEntityId, ModelFieldAssignmentDO assignment) {
        if (assignment == null) {
            return null;
        }
        if (assignment.getRefLibraryId() != null) {
            RelationFieldLibraryDO lib = relationFieldLibraryMapper.selectById(assignment.getRefLibraryId());
            if (lib != null && StrUtil.isNotBlank(lib.getRefEntityType())) {
                String bt = lib.getRefEntityType();
                if (entityCoreService.get(refEntityId, bt) != null) {
                    return bt;
                }
            }
        }
        if (assignment.getModelRelationId() != null) {
            ModelRelationDO rel = modelRelationMapper.selectById(assignment.getModelRelationId());
            if (rel != null && StrUtil.isNotBlank(rel.getTargetModelCode())) {
                ModelDO tm = modelMapper.selectByCode(rel.getTargetModelCode());
                if (tm != null) {
                    String bt = tm.getEntityTypeCode();
                    if (entityCoreService.get(refEntityId, bt) != null) {
                        return bt;
                    }
                }
            }
        }
        if (StrUtil.isNotBlank(assignment.getTargetEntityType())) {
            String bt = assignment.getTargetEntityType();
            if (entityCoreService.get(refEntityId, bt) != null) {
                return bt;
            }
        }
        return null;
    }

    /**
     * 判断字段分配是否指向目标模型（关联字段库按业务类型、模型关系按目标 model code）。
     */
    private boolean isAssignmentReferencingTargetModel(ModelFieldAssignmentDO assignment, ModelDO targetModel) {
        if (assignment == null || targetModel == null) {
            return false;
        }
        if (assignment.getModelRelationId() != null) {
            ModelRelationDO rel = modelRelationMapper.selectById(assignment.getModelRelationId());
            return rel != null && targetModel.getCode().equals(rel.getTargetModelCode());
        }
        if (assignment.getRefLibraryId() != null) {
            RelationFieldLibraryDO lib = relationFieldLibraryMapper.selectById(assignment.getRefLibraryId());
            return lib != null && StrUtil.isNotBlank(lib.getRefEntityType())
                    && lib.getRefEntityType().equals(targetModel.getEntityTypeCode());
        }
        return StrUtil.isNotBlank(assignment.getTargetEntityType())
                && assignment.getTargetEntityType().equals(targetModel.getEntityTypeCode());
    }

    private EntitySimpleVO buildEntitySimpleVO(EntityRespVO entity, ModelDO model) {
        return EntitySimpleVO.builder().id(entity.getId()).name(entity.getName()).modelId(entity.getModelId())
                .modelCode(model != null ? model.getCode() : null).modelName(model != null ? model.getName() : null)
                .status(entity.getStatus()).createTime(entity.getCreateTime()).displayValue(entity.getName()).build();
    }

    private Long countReverseRelations(Long targetEntityId, String sourceModelCode, String fieldCode) {
        // 优化：优先使用 EntityRelationMapper 的索引查询
        // 需求：FR-BDA-075, FR-BDA-092
        String cacheKey = "count:" + targetEntityId + ":" + sourceModelCode + ":" + fieldCode;
        Long cachedCount = (Long) statisticsCache.getIfPresent(cacheKey);
        if (cachedCount != null) {
            return cachedCount;
        }
        
        // 尝试从 EntityRelationDO 表查询（优化路径）
        Long count = entityRelationMapper.countByTargetEntityIdAndSourceModelCodeAndFieldCode(
                targetEntityId, sourceModelCode, fieldCode);
        
        // 如果 EntityRelationDO 表有数据，直接返回
        if (count != null && count > 0) {
            statisticsCache.put(cacheKey, count);
            return count;
        }
        
        // 回退到旧逻辑：从 EntityFieldIndex 表查询（兼容未同步的历史数据）
        ModelDO sourceModel = modelMapper.selectByCode(sourceModelCode);
        if (sourceModel == null) {
            statisticsCache.put(cacheKey, 0L);
            return 0L;
        }
        List<EntityFieldIndexDO> indexRecords = entityFieldIndexMapper.selectByModelIdAndFieldCode(sourceModel.getId(), fieldCode);
        count = indexRecords.stream()
                .filter(r -> {
                    Long refId = parseEntityRefValue(r.getValueString());
                    return refId != null && refId.equals(targetEntityId);
                })
                .count();
        
        statisticsCache.put(cacheKey, count);
        return count;
    }

    private List<EntityFieldIndexDO> findReverseRelationIndexRecords(Long targetEntityId, Long sourceModelId, String fieldCode) {
        List<EntityFieldIndexDO> indexRecords = entityFieldIndexMapper.selectByModelIdAndFieldCode(sourceModelId, fieldCode);
        return indexRecords.stream().filter(r -> { Long refId = parseEntityRefValue(r.getValueString()); return refId != null && refId.equals(targetEntityId); }).collect(Collectors.toList());
    }

    private BigDecimal calculateAggregateValue(String aggregateType, List<EntityFieldIndexDO> records, String targetFieldCode) {
        if (CollUtil.isEmpty(records)) return BigDecimal.ZERO;
        switch (aggregateType.toUpperCase()) {
            case "COUNT": return BigDecimal.valueOf(records.size());
            case "SUM": if (StrUtil.isBlank(targetFieldCode)) return BigDecimal.ZERO;
                return records.stream().map(r -> r.getValueNumber()).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
            case "AVG": if (StrUtil.isBlank(targetFieldCode)) return BigDecimal.ZERO;
                List<BigDecimal> values = records.stream().map(r -> r.getValueNumber()).filter(Objects::nonNull).collect(Collectors.toList());
                if (values.isEmpty()) return BigDecimal.ZERO;
                return values.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_UP);
            case "MAX": if (StrUtil.isBlank(targetFieldCode)) return BigDecimal.ZERO;
                return records.stream().map(r -> r.getValueNumber()).filter(Objects::nonNull).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            case "MIN": if (StrUtil.isBlank(targetFieldCode)) return BigDecimal.ZERO;
                return records.stream().map(r -> r.getValueNumber()).filter(Objects::nonNull).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            default: log.warn("[calculateAggregateValue][不支持的聚合类型: {}]", aggregateType); return BigDecimal.ZERO;
        }
    }
}
