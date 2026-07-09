package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityConvert;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityFieldMapsSupport;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.framework.mybatis.JsonbMapTypeHandler;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 实体写路径辅助：模型校验、请求→DO、customFields 加密等。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EntityBusinessHelper {

    private final ModelMapper modelMapper;
    private final CustomFieldValidationService customFieldValidationService;

    public ModelDO validateModelExists(Long modelId) {
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }
        return model;
    }

    public void validateModelEntityType(Long modelId, String entityTypeCode) {
        ModelDO model = modelMapper.selectById(modelId);
        if (model != null && !Objects.equals(model.getEntityTypeCode(), entityTypeCode)) {
            throw new ServiceException(400, "模型与业务类型不匹配");
        }
    }

    public void validateCustomFields(Long modelId, Map<String, Object> customFields) {
        if (customFields != null && !customFields.isEmpty()) {
            customFieldValidationService.validateCustomFields(modelId, customFields);
        }
    }

    public void validateBaseFields(String entityTypeCode, Map<String, Object> baseFields) {
        EntityFieldMapsSupport.getRequiredEntityTypeCode(baseFields);
        Map<String, Object> base = EntityFieldMapsSupport.normalizeMap(baseFields);
        Object entityName = base.get("name");
        if (entityName == null || String.valueOf(entityName).isBlank()) {
            throw new ServiceException(400, "baseFields.name 不能为空");
        }
        if (base.get("status") == null) {
            throw new ServiceException(400, "baseFields.status 不能为空");
        }
        EntityFieldMapsSupport.getRequiredModelId(baseFields);
        // TODO: 与 BaseFieldValidationService 对齐后在此实现业务基础列校验
    }

    public void validateEntityReferences(EntityDO entity, ModelDO model,
            Map<String, Object> customFields, String entityTypeCode) {
        // TODO: 与 EntityValidationService 对齐后在此实现引用校验
    }

    public EntityDO prepareCreateEntity(EntityCreateReqVO reqVO) {
        Map<String, Object> baseFields = EntityFieldMapsSupport.normalizeMap(reqVO.getBaseFields());
        Long modelId = EntityFieldMapsSupport.getRequiredModelId(baseFields);
        String entityTypeCode = EntityFieldMapsSupport.getRequiredEntityTypeCode(baseFields);

        ModelDO model = validateModelExists(modelId);
        validateModelEntityType(modelId, entityTypeCode);
        validateBaseFields(entityTypeCode, baseFields);
        validateCustomFields(modelId, reqVO.getCustomFields());

        EntityDO data = EntityConvert.INSTANCE.convert(reqVO);
        data.setTenantId(getTenantId());

        validateEntityReferences(data, model, data.getCustomFields(), entityTypeCode);

        if (data.getCustomFields() != null) {
            data.setCustomFields(customFieldValidationService.normalizeAndEncryptCustomFields(
                    data.getCustomFields(), modelId));
        }

        return data;
    }

    public EntityDO prepareUpdateEntity(EntityUpdateReqVO reqVO, EntityDO dbEntity) {
        Map<String, Object> baseFields = EntityFieldMapsSupport.normalizeMap(reqVO.getBaseFields());
        Long modelId = EntityFieldMapsSupport.getModelId(baseFields);
        if (modelId == null) {
            modelId = dbEntity.getModelId();
            baseFields.put("modelId", modelId);
        }
        String entityTypeCode = EntityFieldMapsSupport.getEntityTypeCode(baseFields);
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            entityTypeCode = dbEntity.getEntityTypeCode();
            baseFields.put("entityTypeCode", entityTypeCode);
        }

        ModelDO model = validateModelExists(modelId);
        validateModelEntityType(modelId, entityTypeCode);
        validateBaseFields(entityTypeCode, baseFields);
        validateCustomFields(modelId, reqVO.getCustomFields());

        reqVO.setBaseFields(baseFields);
        EntityDO update = EntityConvert.INSTANCE.convert(reqVO);
        update.setTenantId(dbEntity.getTenantId());

        validateEntityReferences(update, model, update.getCustomFields(), entityTypeCode);

        if (update.getCustomFields() != null) {
            update.setCustomFields(customFieldValidationService.normalizeAndEncryptCustomFields(
                    update.getCustomFields(), modelId));
        }

        return update;
    }

    /**
     * Excel 导入等文本边界：将 JSON 字符串解析为 Map。
     */
    public Map<String, Object> parseCustomFieldsFromJson(String customFieldsJson) {
        if (customFieldsJson == null || customFieldsJson.isEmpty()) {
            return new HashMap<>();
        }
        try {
            Map<String, Object> parsed = JsonbMapTypeHandler.parse(customFieldsJson);
            return parsed != null ? parsed : new HashMap<>();
        } catch (Exception e) {
            log.warn("[parseCustomFieldsFromJson] 解析失败: {}", customFieldsJson, e);
            return new HashMap<>();
        }
    }

    public Map<String, Object> emptyIfNull(Map<String, Object> customFields) {
        return customFields != null ? customFields : Collections.emptyMap();
    }

    public List<String> extractFieldCodes(Map<String, Object> baseFields, Map<String, Object> customFields) {
        return new ArrayList<>(EntityFieldMapsSupport.extractFieldCodes(baseFields, customFields));
    }

    public Long getTenantId() {
        return Objects.requireNonNullElse(TenantContextHolder.getRequiredTenantId(), 0L);
    }
}
