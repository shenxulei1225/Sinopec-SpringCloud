package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.RelatedEntityRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelRelationService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityDoVoHelper;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 关联 Entity 查询服务实现
 * 
 * <p>实现反向查询能力，通过分析 Model 关联关系和 Entity 的 customFields，
 * 查询所有关联到指定 Entity 的其他 Entity。</p>
 * 
 * <h3>实现原理</h3>
 * <ol>
 *   <li>获取目标 Entity 的信息（通过 EntityService 支持多存储策略）</li>
 *   <li>查询所有以该 Model 为目标的关联关系（反向关联）</li>
 *   <li>对于每个关联关系，通过 EntityService 查询源 Model 下所有 Entity</li>
 *   <li>过滤出 customFields 中关联字段值等于目标 Entity ID 的 Entity</li>
 * </ol>
 * 
 * <h3>存储策略支持</h3>
 * <p>通过 EntityService 进行查询，自动路由到正确的存储策略：
 * <ul>
 *   <li>GENERIC 类型：查询 dynamic_entity 表</li>
 *   <li>DEDICATED 类型：查询动态创建的 ent_xxx 表</li>
 * </ul>
 * </p>
 * 
 * <h3>需求引用</h3>
 * <ul>
 *   <li>FR-078: 系统必须支持反向查询</li>
 *   <li>FR-086: 系统必须提供反向查询 API</li>
 *   <li>BR-REL-005: 反向查询通过 GET /api/entity/{id}/related API 提供</li>
 * </ul>
 * 
 * @author yudao
 */
@Service
@Validated
@Slf4j
public class RelatedEntityQueryServiceImpl implements RelatedEntityQueryService {

    @Resource
    private EntityCoreService entityCoreService;

    @Resource
    private CustomFieldValidationService customFieldValidationService;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private ModelRelationService modelRelationService;

    @Override
    public List<RelatedEntityRespVO> getRelatedEntities(Long entityId, String modelCode) {
        return getRelatedEntities(entityId, modelCode, null);
    }

    @Override
    public List<RelatedEntityRespVO> getRelatedEntities(Long entityId, String modelCode, String targetEntityTypeCode) {
        // 1. 获取目标 Entity（通过 EntityService 支持多存储策略）
        // 必须提供 entityTypeCode 或 modelCode 来确定查询哪个存储
        EntityRespVO targetEntity = null;
        if (targetEntityTypeCode != null && !targetEntityTypeCode.isEmpty()) {
            // 如果指定了业务类型，直接路由到对应存储策略
            EntityDO targetEntityDO = entityCoreService.get(entityId, targetEntityTypeCode);
            targetEntity = targetEntityDO != null ? EntityDoVoHelper.toRespVO(targetEntityDO, customFieldValidationService) : null;
        } else if (modelCode != null && !modelCode.isEmpty()) {
            // 否则从 modelCode 推断 entityTypeCode
            log.debug("未指定 entityTypeCode，从 modelCode 推断: entityId={}, modelCode={}", entityId, modelCode);
            ModelDO model = modelMapper.selectByCode(modelCode);
            if (model != null) {
                EntityDO targetEntityDO = entityCoreService.get(entityId, model.getEntityTypeCode());
                targetEntity = targetEntityDO != null ? EntityDoVoHelper.toRespVO(targetEntityDO, customFieldValidationService) : null;
            }
        } else {
            // 两者都没有，无法确定存储位置
            log.warn("未指定 entityTypeCode 和 modelCode，无法查询实体: entityId={}", entityId);
        }
        
        if (targetEntity == null) {
            log.warn("目标 Entity 不存在: entityId={}, entityTypeCode={}", entityId, targetEntityTypeCode);
            return new ArrayList<>();
        }

        // 2. 获取目标 Entity 的 Model
        ModelDO targetModel = modelMapper.selectById(targetEntity.getModelId());
        if (targetModel == null) {
            log.warn("目标 Entity 的 Model 不存在: modelId={}", targetEntity.getModelId());
            return new ArrayList<>();
        }

        // 3. 查询所有以该 Model 为目标的关联关系（反向关联）
        List<ModelRelationDO> reverseRelations = modelRelationService.getRelationsByTargetModelCode(targetModel.getCode());
        if (reverseRelations.isEmpty()) {
            log.debug("没有找到反向关联: targetModelCode={}", targetModel.getCode());
            return new ArrayList<>();
        }

        // 4. 如果指定了 modelCode，过滤关联关系
        if (modelCode != null && !modelCode.isEmpty()) {
            reverseRelations = reverseRelations.stream()
                    .filter(r -> modelCode.equals(r.getSourceModelCode()))
                    .collect(Collectors.toList());
        }

        // 5. 对于每个关联关系，查询关联的 Entity
        List<RelatedEntityRespVO> result = new ArrayList<>();
        
        // 预加载所有源 Model 信息
        Map<String, ModelDO> modelMap = reverseRelations.stream()
                .map(ModelRelationDO::getSourceModelCode)
                .distinct()
                .map(code -> modelMapper.selectByCode(code))
                .filter(m -> m != null)
                .collect(Collectors.toMap(ModelDO::getCode, m -> m));

        for (ModelRelationDO relation : reverseRelations) {
            ModelDO sourceModel = modelMap.get(relation.getSourceModelCode());
            if (sourceModel == null) {
                continue;
            }

            // 如果指定了 entityTypeCode，且与源 Model 的业务类型不一致，跳过该关联关系
            // 这允许前端通过传入 entityTypeCode 来过滤只返回特定业务类型的关联实体
            if (targetEntityTypeCode != null && !targetEntityTypeCode.isEmpty()) {
                if (!targetEntityTypeCode.equals(sourceModel.getEntityTypeCode())) {
                    log.debug("跳过业务类型不匹配的关联关系: sourceModelCode={}, expectedEntityType={}, actualEntityType={}", 
                            relation.getSourceModelCode(), targetEntityTypeCode, sourceModel.getEntityTypeCode());
                    continue;
                }
            }

            // 通过 EntityService 查询源 Model 下的所有 Entity（支持多存储策略）
            // 使用 entityTypeCode 进行路由，确保能查询到动态表中的数据
            // 查询源 Model 下的所有 Entity（本体数据通过 CoreService 获取）
            List<EntityDO> sourceEntityDOs = entityCoreService.listEntities(sourceModel.getEntityTypeCode(), sourceModel.getId(), null);
            List<EntityRespVO> sourceEntities = EntityDoVoHelper.toRespVOList(sourceEntityDOs, customFieldValidationService);
            
            // 过滤出关联字段值等于目标 Entity ID 的 Entity
            String fieldCode = relation.getFieldCode();
            for (EntityRespVO entity : sourceEntities) {
                if (isRelatedToTarget(entity, fieldCode, entityId)) {
                    RelatedEntityRespVO respVO = convertToRespVO(entity, sourceModel, relation);
                    result.add(respVO);
                }
            }
        }

        log.debug("反向查询完成: entityId={}, modelCode={}, entityTypeCode={}, resultCount={}", 
                entityId, modelCode, targetEntityTypeCode, result.size());
        return result;
    }

    @Override
    public Long countRelatedEntities(Long entityId, String modelCode) {
        return (long) getRelatedEntities(entityId, modelCode).size();
    }

    @Override
    public Long countRelatedEntities(Long entityId, String modelCode, String entityTypeCode) {
        return (long) getRelatedEntities(entityId, modelCode, entityTypeCode).size();
    }

    // ========== 私有方法 ==========

    /**
     * 检查 Entity 是否通过指定字段关联到目标 Entity
     * 
     * 支持 ENTITY_REF（单选）和 ENTITY_REF_MULTI（多选List）两种类型
     * 
     * @param entity 源 Entity（EntityRespVO）
     * @param fieldCode 关联字段编码
     * @param targetEntityId 目标 Entity ID
     * @return 是否关联
     */
    private boolean isRelatedToTarget(EntityRespVO entity, String fieldCode, Long targetEntityId) {
        Map<String, Object> customFields = entity.getCustomFields();
        if (customFields == null || customFields.isEmpty()) {
            return false;
        }

        Object fieldValue = customFields.get(fieldCode);
        if (fieldValue == null) {
            return false;
        }

        if (fieldValue instanceof List<?> list) {
            for (Object item : list) {
                Long id = parseEntityId(item);
                if (id != null && id.equals(targetEntityId)) {
                    return true;
                }
            }
            return false;
        }

        if (fieldValue.getClass().isArray()) {
            Object[] array = (Object[]) fieldValue;
            for (Object item : array) {
                Long id = parseEntityId(item);
                if (id != null && id.equals(targetEntityId)) {
                    return true;
                }
            }
            return false;
        }

        Long id = parseEntityId(fieldValue);
        return id != null && id.equals(targetEntityId);
    }

    /**
     * 解析Entity ID（支持多种格式）
     * 
     * @param value 字段值（可能是Number、String、Long等）
     * @return Entity ID，解析失败返回null
     */
    private Long parseEntityId(Object value) {
        if (value == null) {
            return null;
        }
        
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }

    /**
     * 转换为响应 VO
     */
    private RelatedEntityRespVO convertToRespVO(EntityRespVO entity, ModelDO model, ModelRelationDO relation) {
        RelatedEntityRespVO respVO = new RelatedEntityRespVO();
        respVO.setId(entity.getId());
        respVO.setName(entity.getName());
        respVO.setModelCode(model.getCode());
        respVO.setModelName(model.getName());
        respVO.setEntityTypeCode(entity.getEntityTypeCode());
        respVO.setRelationFieldCode(relation.getFieldCode());
        respVO.setRelationName(relation.getRelationName());
        respVO.setStatus(entity.getStatus());
        respVO.setCreateTime(entity.getCreateTime());
        respVO.setUpdateTime(entity.getUpdateTime());

        respVO.setCustomFields(entity.getCustomFields());
        return respVO;
    }
}
