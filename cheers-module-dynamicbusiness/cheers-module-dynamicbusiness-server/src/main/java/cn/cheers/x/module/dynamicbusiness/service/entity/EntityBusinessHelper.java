package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityConvert;
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

    public void validateModelBusinessType(Long modelId, String businessTypeCode) {
        ModelDO model = modelMapper.selectById(modelId);
        if (model != null && !Objects.equals(model.getBusinessTypeCode(), businessTypeCode)) {
            throw new ServiceException(400, "模型与业务类型不匹配");
        }
    }

    public void validateCustomFields(Long modelId, Map<String, Object> customFields) {
        if (customFields != null && !customFields.isEmpty()) {
            customFieldValidationService.validateCustomFields(modelId, customFields);
        }
    }

    public void validateBaseFields(String businessTypeCode, Map<String, Object> customFields) {
        // TODO: 与 BaseFieldValidationService 对齐后在此实现固定列校验
    }

    public void validateEntityReferences(EntityDO entity, ModelDO model,
            Map<String, Object> customFields, String businessTypeCode) {
        // TODO: 与 EntityValidationService 对齐后在此实现引用校验
    }

    public EntityDO prepareCreateEntity(EntityCreateReqVO reqVO) {
        ModelDO model = validateModelExists(reqVO.getModelId());
        validateModelBusinessType(reqVO.getModelId(), reqVO.getBusinessTypeCode());
        validateCustomFields(reqVO.getModelId(), reqVO.getCustomFields());
        validateBaseFields(reqVO.getBusinessTypeCode(), reqVO.getCustomFields());

        EntityDO data = EntityConvert.INSTANCE.convert(reqVO);
        data.setTenantId(getTenantId());

        validateEntityReferences(data, model, reqVO.getCustomFields(), reqVO.getBusinessTypeCode());

        if (data.getCustomFields() != null) {
            data.setCustomFields(customFieldValidationService.normalizeAndEncryptCustomFields(
                    data.getCustomFields(), reqVO.getModelId()));
        }

        return data;
    }

    public EntityDO prepareUpdateEntity(EntityUpdateReqVO reqVO, EntityDO dbEntity) {
        Long modelId = reqVO.getModelId() != null ? reqVO.getModelId() : dbEntity.getModelId();
        ModelDO model = validateModelExists(modelId);
        validateModelBusinessType(modelId, reqVO.getBusinessTypeCode());
        validateCustomFields(modelId, reqVO.getCustomFields());
        validateBaseFields(reqVO.getBusinessTypeCode(), reqVO.getCustomFields());

        EntityDO update = EntityConvert.INSTANCE.convert(reqVO);
        update.setTenantId(dbEntity.getTenantId());

        validateEntityReferences(update, model, reqVO.getCustomFields(), reqVO.getBusinessTypeCode());

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

    public List<String> extractFieldCodes(Map<String, Object> customFields) {
        return new ArrayList<>(emptyIfNull(customFields).keySet());
    }

    public Long getTenantId() {
        return Objects.requireNonNullElse(TenantContextHolder.getRequiredTenantId(), 0L);
    }
}
