package cn.cheers.x.module.dynamicbusiness.service.relation;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityDoVoHelper;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 关联展示服务实现类
 * 
 * 需求：FR-BDA-040~042
 * 
 * @author yudao
 */
@Service
@Validated
@Slf4j
public class RelationDisplayServiceImpl implements RelationDisplayService {

    @Resource
    private ModelService modelService;

    @Resource
    private EntityCoreService entityCoreService;

    @Resource
    private CustomFieldValidationService customFieldValidationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getDisplayValue(String targetEntityType, String targetModelCode,
                                   Long entityId, String displayFieldCode) {
        if (entityId == null) {
            return null;
        }

        // 获取实际使用的展示字段编码
        String effectiveFieldCode = getEffectiveDisplayFieldCode(
                targetEntityType, targetModelCode, displayFieldCode);

        // 查询实体（通过 EntityService 支持多存储策略）
        EntityRespVO entity;
        try {
            EntityDO entityDO;
            if (targetEntityType != null && !targetEntityType.isEmpty()) {
                entityDO = entityCoreService.get(entityId, targetEntityType);
            } else {
                // 没有 entityTypeCode 时，尝试使用 null（通用表）
                log.warn("[getDisplayValue][未指定 entityTypeCode，entityId={}，尝试从通用表查询]", entityId);
                entityDO = entityCoreService.get(entityId, null);
            }
            entity = entityDO != null ? EntityDoVoHelper.toRespVO(entityDO, customFieldValidationService) : null;
        } catch (Exception e) {
            log.warn("[getDisplayValue][查询实体失败，entityId={}, businessType={}]", 
                    entityId, targetEntityType, e);
            return String.valueOf(entityId);
        }
        
        if (entity == null) {
            return String.valueOf(entityId);
        }

        // 从实体中获取展示值
        return extractDisplayValue(entity, effectiveFieldCode);
    }

    @Override
    public Map<Long, String> getDisplayValues(String targetEntityType, String targetModelCode,
                                               List<Long> entityIds, String displayFieldCode) {
        Map<Long, String> result = new HashMap<>();
        if (entityIds == null || entityIds.isEmpty()) {
            return result;
        }

        // 获取实际使用的展示字段编码
        String effectiveFieldCode = getEffectiveDisplayFieldCode(
                targetEntityType, targetModelCode, displayFieldCode);

        // 批量查询实体（通过 EntityService 支持多存储策略）
        List<EntityRespVO> entities = new ArrayList<>();
        // Instead of N+1 queries, do a single batch query if possible.
        // For now, we replicate the logic with the new service.
        for (Long entityId : entityIds) {
            try {
                EntityDO entityDO;
                if (targetEntityType != null && !targetEntityType.isEmpty()) {
                    entityDO = entityCoreService.get(entityId, targetEntityType);
                } else {
                    entityDO = entityCoreService.get(entityId, null);
                }
                if (entityDO != null) {
                    entities.add(EntityDoVoHelper.toRespVO(entityDO, customFieldValidationService));
                }
            } catch (Exception e) {
                log.debug("[getDisplayValues][查询实体失败，entityId={}, businessType={}]", 
                        entityId, targetEntityType);
            }
        }

        // 提取展示值
        for (EntityRespVO entity : entities) {
            String displayValue = extractDisplayValue(entity, effectiveFieldCode);
            result.put(entity.getId(), displayValue);
        }

        // 对于未找到的实体，使用 ID 作为展示值
        for (Long entityId : entityIds) {
            if (!result.containsKey(entityId)) {
                result.put(entityId, String.valueOf(entityId));
            }
        }

        return result;
    }

    @Override
    public String getEffectiveDisplayFieldCode(String targetEntityType, String targetModelCode,
                                                String displayFieldCode) {
        // 1. 如果指定了 displayFieldCode，优先使用
        if (displayFieldCode != null && !displayFieldCode.isEmpty()) {
            return displayFieldCode;
        }

        // 2. 否则返回 null（后续会使用默认逻辑，如使用实体的 name 字段）
        return null;
    }

    @Override
    public RelationDisplayInfo getDisplayInfo(String targetEntityType, String targetModelCode,
                                               Long entityId, String displayFieldCode) {
        RelationDisplayInfo info = new RelationDisplayInfo();
        info.setEntityId(entityId);
        info.setTargetEntityType(targetEntityType);
        info.setTargetModelCode(targetModelCode);

        // 获取实际使用的展示字段编码
        String effectiveFieldCode = getEffectiveDisplayFieldCode(
                targetEntityType, targetModelCode, displayFieldCode);
        info.setDisplayFieldCode(effectiveFieldCode);

        // 获取展示值
        String displayValue = getDisplayValue(targetEntityType, targetModelCode,
                entityId, displayFieldCode);
        info.setDisplayValue(displayValue);

        return info;
    }

    /**
     * 从实体中提取展示值
     * 
     * @param entity 实体
     * @param fieldCode 字段编码
     * @return 展示值
     */
    private String extractDisplayValue(EntityRespVO entity, String fieldCode) {
        if (entity == null) {
            return null;
        }

        // 1. 如果指定了字段编码，尝试从 customFields 中获取
        if (fieldCode != null && !fieldCode.isEmpty()) {
            String customFieldValue = getCustomFieldValue(entity, fieldCode);
            if (customFieldValue != null) {
                return customFieldValue;
            }
        }

        // 2. 尝试使用实体的 name 字段
        if (entity.getName() != null && !entity.getName().isEmpty()) {
            return entity.getName();
        }

        // 3. 尝试从 customFields 中获取 name 字段
        String nameFromCustom = getCustomFieldValue(entity, "name");
        if (nameFromCustom != null) {
            return nameFromCustom;
        }

        // 4. 最后使用实体 ID
        return String.valueOf(entity.getId());
    }

    /**
     * 从实体的 customFields 中获取指定字段的值
     * 
     * @param entity 实体
     * @param fieldCode 字段编码
     * @return 字段值
     */
    private String getCustomFieldValue(EntityRespVO entity, String fieldCode) {
        Map<String, Object> customFields = entity.getCustomFields();
        if (customFields == null || customFields.isEmpty()) {
            return null;
        }

        Object value = customFields.get(fieldCode);
        return value != null ? String.valueOf(value) : null;
    }
}
