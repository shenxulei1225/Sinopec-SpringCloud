package cn.cheers.x.module.dynamicbusiness.service.entity.validation;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation.RelationFieldLibraryDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelRelationMapper;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityDoVoHelper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.relation.RelationFieldLibraryMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;
import cn.cheers.x.module.dynamicbusiness.service.entity.validation.exception.CircularReferenceException;
import cn.cheers.x.module.dynamicbusiness.service.entity.validation.exception.InvalidEntityRefException;
import cn.cheers.x.module.dynamicbusiness.service.entity.validation.exception.InvalidParentRefException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class EntityValidationServiceImpl implements EntityValidationService {

    private static final String FIELD_TYPE_ENTITY_REF = "ENTITY_REF";

    @Resource
    private EntityCoreService entityCoreService;

    @Resource
    private CustomFieldValidationService customFieldValidationService;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private FieldMapper fieldMapper;

    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;

    @Resource
    private RelationFieldLibraryMapper relationFieldLibraryMapper;

    @Resource
    private ModelRelationMapper modelRelationMapper;

    @Override
    public void validateEntity(EntityDO entity, ModelDO model) {
        Map<String, Object> customFields = entity.getCustomFields() != null
                ? entity.getCustomFields() : Collections.emptyMap();
        validateEntity(entity, model, customFields, model.getEntityTypeCode());
    }

    @Override
    public void validateEntity(EntityDO entity, ModelDO model, Map<String, Object> customFields) {
        validateEntity(entity, model, customFields, model.getEntityTypeCode());
    }

    /**
     * 验证 Entity 的所有引用字段（带 entityTypeCode）
     *
     * @param entity 待验证的 Entity
     * @param model Entity 所属的 Model
     * @param customFields 解析后的扩展字段数据
     * @param entityTypeCode 业务类型编码（用于存储路由）
     */
    public void validateEntity(EntityDO entity, ModelDO model, Map<String, Object> customFields, String entityTypeCode) {
        log.debug("开始验证 Entity: id={}, modelId={}, entityTypeCode={}", entity.getId(), model.getId(), entityTypeCode);

        if (entity.getParentId() != null && entity.getParentId() > 0) {
            validateParentRef(entity.getParentId(), entity.getId(), model.getId(), entityTypeCode);
        }

        if (customFields != null && !customFields.isEmpty()) {
            validateEntityRefFields(entity, model, customFields);
        }

        log.debug("Entity 验证通过: id={}, modelId={}", entity.getId(), model.getId());
    }

    @Override
    public void validateParentRef(Long parentId, Long entityId, Long modelId) {
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            throw InvalidParentRefException.notExists(entityId, modelId, parentId);
        }
        validateParentRef(parentId, entityId, modelId, model.getEntityTypeCode());
    }

    /**
     * 验证 parent_id 引用（带 entityTypeCode）
     *
     * @param parentId 父节点 ID
     * @param entityId 当前 Entity ID
     * @param modelId Model ID
     * @param entityTypeCode 业务类型编码（parent 与当前实体同 entityTypeCode）
     */
    public void validateParentRef(Long parentId, Long entityId, Long modelId, String entityTypeCode) {
        log.debug("验证 parent_id: parentId={}, entityId={}, modelId={}, entityTypeCode={}",
                parentId, entityId, modelId, entityTypeCode);

        EntityRespVO parentEntity;
        try {
            EntityDO parentEntityDO = entityCoreService.get(parentId, entityTypeCode);
            parentEntity = parentEntityDO != null ? EntityDoVoHelper.toRespVO(parentEntityDO, customFieldValidationService) : null;
        } catch (Exception e) {
            log.warn("parent_id 引用无效：目标 Entity 不存在, parentId={}, entityTypeCode={}, error={}",
                    parentId, entityTypeCode, e.getMessage());
            throw InvalidParentRefException.notExists(entityId, modelId, parentId);
        }
        if (parentEntity == null) {
            log.warn("parent_id 引用无效：目标 Entity 不存在, parentId={}", parentId);
            throw InvalidParentRefException.notExists(entityId, modelId, parentId);
        }

        if (!modelId.equals(parentEntity.getModelId())) {
            log.debug("跨Model的parent_id关联: currentModelId={}, parentModelId={}, parentId={}",
                    modelId, parentEntity.getModelId(), parentId);
        }

        if (entityId != null) {
            List<Long> cyclePath = detectCircularReference(parentId, entityId, modelId, entityTypeCode);
            if (cyclePath != null) {
                log.warn("检测到循环引用: entityId={}, cyclePath={}", entityId, cyclePath);
                throw new CircularReferenceException(entityId, modelId, cyclePath);
            }
        }

        log.debug("parent_id 验证通过: parentId={}", parentId);
    }

    @Override
    public void validateEntityRef(Long refEntityId, FieldDO field, Long entityId, Long modelId) {
        log.debug("验证 ENTITY_REF 字段: fieldCode={}, refEntityId={}, entityId={}",
                field.getCode(), refEntityId, entityId);

        String refEntityTypeCode = resolveRefEntityTypeCode(field, refEntityId, modelId);

        EntityRespVO refEntity;
        try {
            EntityDO refEntityDO = entityCoreService.get(refEntityId, refEntityTypeCode);
            refEntity = refEntityDO != null ? EntityDoVoHelper.toRespVO(refEntityDO, customFieldValidationService) : null;
        } catch (Exception e) {
            log.warn("ENTITY_REF 引用无效：目标 Entity 不存在, fieldCode={}, refEntityId={}, entityTypeCode={}, error={}",
                    field.getCode(), refEntityId, refEntityTypeCode, e.getMessage());
            throw InvalidEntityRefException.notExists(entityId, modelId, field.getCode(),
                    field.getName(), refEntityId);
        }
        if (refEntity == null) {
            log.warn("ENTITY_REF 引用无效：目标 Entity 不存在, fieldCode={}, refEntityId={}",
                    field.getCode(), refEntityId);
            throw InvalidEntityRefException.notExists(entityId, modelId, field.getCode(),
                    field.getName(), refEntityId);
        }

        ModelDO refModel = modelMapper.selectById(refEntity.getModelId());
        if (refModel == null) {
            log.warn("ENTITY_REF 引用无效：目标 Entity 的 Model 不存在, fieldCode={}, refEntityId={}",
                    field.getCode(), refEntityId);
            throw InvalidEntityRefException.notExists(entityId, modelId, field.getCode(),
                    field.getName(), refEntityId);
        }

        log.debug("ENTITY_REF 字段验证通过: fieldCode={}, refEntityId={}", field.getCode(), refEntityId);
    }

    /**
     * 根据模型字段分配（关联字段库 / 模型关系 / 兜底 targetEntityType）解析引用实体所在业务类型。
     */
    private String resolveRefEntityTypeCode(FieldDO field, Long refEntityId, Long modelId) {
        ModelFieldAssignmentDO assignment = modelFieldAssignmentMapper.selectByModelIdAndFieldId(modelId, field.getId());
        if (assignment == null) {
            return null;
        }
        if (assignment.getRefLibraryId() != null) {
            RelationFieldLibraryDO lib = relationFieldLibraryMapper.selectById(assignment.getRefLibraryId());
            if (lib != null && lib.getRefEntityType() != null && !lib.getRefEntityType().isEmpty()) {
                String bt = lib.getRefEntityType();
                if (entityCoreService.get(refEntityId, bt) != null) {
                    return bt;
                }
            }
        }
        if (assignment.getModelRelationId() != null) {
            ModelRelationDO rel = modelRelationMapper.selectById(assignment.getModelRelationId());
            if (rel != null && rel.getTargetModelCode() != null) {
                ModelDO tm = modelMapper.selectByCode(rel.getTargetModelCode());
                if (tm != null) {
                    String bt = tm.getEntityTypeCode();
                    if (entityCoreService.get(refEntityId, bt) != null) {
                        return bt;
                    }
                }
            }
        }
        if (assignment.getTargetEntityType() != null && !assignment.getTargetEntityType().isEmpty()) {
            String bt = assignment.getTargetEntityType();
            if (entityCoreService.get(refEntityId, bt) != null) {
                return bt;
            }
        }
        return null;
    }

    @Override
    public List<Long> detectCircularReference(Long parentId, Long entityId, Long modelId) {
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            return null;
        }
        return detectCircularReference(parentId, entityId, modelId, model.getEntityTypeCode());
    }

    public List<Long> detectCircularReference(Long parentId, Long entityId, Long modelId, String entityTypeCode) {
        Set<Long> visited = new HashSet<>();
        List<Long> path = new ArrayList<>();

        visited.add(entityId);
        path.add(entityId);

        Long currentParentId = parentId;
        while (currentParentId != null && currentParentId > 0) {
            if (visited.contains(currentParentId)) {
                path.add(currentParentId);
                return path;
            }

            visited.add(currentParentId);
            path.add(currentParentId);

            EntityRespVO current;
            try {
                EntityDO currentDO = entityCoreService.get(currentParentId, entityTypeCode);
                current = currentDO != null ? EntityDoVoHelper.toRespVO(currentDO, customFieldValidationService) : null;
            } catch (Exception e) {
                log.debug("获取父节点失败: parentId={}, entityTypeCode={}", currentParentId, entityTypeCode);
                break;
            }
            if (current == null) {
                break;
            }
            currentParentId = current.getParentId();
        }

        return null;
    }

    private void validateEntityRefFields(EntityDO entity, ModelDO model, Map<String, Object> customFields) {
        List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelId(model.getId());
        if (assignments == null || assignments.isEmpty()) {
            return;
        }

        for (ModelFieldAssignmentDO assignment : assignments) {
            FieldDO field = fieldMapper.selectById(assignment.getFieldId());
            if (field == null) {
                continue;
            }

            if (!FIELD_TYPE_ENTITY_REF.equals(field.getType())) {
                continue;
            }

            Object fieldValue = customFields.get(field.getCode());
            if (fieldValue == null) {
                continue;
            }

            Long refEntityId = parseLongValue(fieldValue);
            if (refEntityId == null || refEntityId <= 0) {
                continue;
            }

            validateEntityRef(refEntityId, field, entity.getId(), model.getId());
        }
    }

    private Long parseLongValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
