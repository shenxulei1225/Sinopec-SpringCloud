package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityConvert;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityFieldMapsSupport;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.framework.mybatis.core.type.JsonbMapTypeHandler;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.StorageTypeEnum;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.FacilityOwningFieldEnsureService;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
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
    private final EntityTypeMapper entityTypeMapper;
    private final EntityTypeBaseFieldMapper entityTypeBaseFieldMapper;
    private final CustomFieldValidationService customFieldValidationService;
    private final EntityRepository entityRepository;
    private final EntityDedicatedColumnService entityDedicatedColumnService;
    private final FacilityOwningFieldEnsureService facilityOwningFieldEnsureService;

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
        validateCustomFields(modelId, null, customFields);
    }

    /**
     * 校验型号分配字段；写前分桶后 BASE 来源（如所属场站 facility_id）在 baseFields，须一并传入。
     */
    public void validateCustomFields(Long modelId,
                                     Map<String, Object> baseFields,
                                     Map<String, Object> customFields) {
        if ((customFields == null || customFields.isEmpty())
                && (baseFields == null || baseFields.isEmpty())) {
            return;
        }
        customFieldValidationService.validateCustomFields(modelId, baseFields, customFields);
    }

    public void validateBaseFields(String entityTypeCode, Map<String, Object> baseFields) {
        validateBaseFields(entityTypeCode, baseFields, null);
    }

    /**
     * 校验核心列 + 业务类型必填基础字段；{@code customFields} 一并参与取值（写前分桶前后都可调用）。
     */
    public void validateBaseFields(String entityTypeCode,
                                   Map<String, Object> baseFields,
                                   Map<String, Object> customFields) {
        EntityFieldMapsSupport.getRequiredEntityTypeCode(baseFields);
        EntityFieldMapsSupport.getRequiredModelId(baseFields);
        Map<String, Object> base = EntityFieldMapsSupport.normalizeMap(baseFields);
        Map<String, Object> custom = EntityFieldMapsSupport.normalizeMap(customFields);

        List<String> missing = new ArrayList<>();
        if (isBlankScalar(base.get("name"))) {
            missing.add("名称");
        }
        if (base.get("status") == null) {
            missing.add("状态");
        }

        if (StrUtil.isNotBlank(entityTypeCode)) {
            List<EntityTypeBaseFieldDO> configured =
                    entityTypeBaseFieldMapper.selectByEntityTypeCode(entityTypeCode.trim());
            Set<String> seen = new HashSet<>();
            if (configured != null) {
                for (EntityTypeBaseFieldDO field : configured) {
                    if (field == null || !field.isEnabled() || !Boolean.TRUE.equals(field.getRequired())) {
                        continue;
                    }
                    String code = field.getFieldCode() == null ? "" : field.getFieldCode().trim();
                    if (code.isEmpty() || !seen.add(code)) {
                        continue;
                    }
                    if (EntityFieldMapsSupport.isCoreBaseFieldKey(code)) {
                        continue;
                    }
                    Object value = base.containsKey(code) ? base.get(code) : custom.get(code);
                    if (isEmptyWriteValue(value, field.getDataType())) {
                        String label = StrUtil.isNotBlank(field.getFieldName())
                                ? field.getFieldName().trim()
                                : code;
                        missing.add(label);
                    }
                }
            }
        }

        if (!missing.isEmpty()) {
            throw new ServiceException(400, "以下必填字段未填写：" + String.join("、", missing));
        }
    }

    private static boolean isBlankScalar(Object value) {
        return value == null || String.valueOf(value).isBlank();
    }

    private static boolean isEmptyWriteValue(Object value, String dataType) {
        if (value == null) {
            return true;
        }
        if (value instanceof String text) {
            return text.isBlank();
        }
        if (value instanceof Collection<?> collection) {
            return collection.isEmpty();
        }
        if (value instanceof Map<?, ?> map) {
            if (map.isEmpty()) {
                return true;
            }
            Object id = map.get("id");
            if (id == null) {
                id = map.get("entityId");
            }
            return id == null || String.valueOf(id).isBlank();
        }
        String type = dataType == null ? "" : dataType.trim().toUpperCase(Locale.ROOT).replace('-', '_');
        if ("REF_MULTI".equals(type) || "ENTITY_REF_MULTI".equals(type) || "BATCH_ENTITY_REF".equals(type)) {
            return false;
        }
        return false;
    }

    public void validateEntityReferences(EntityDO entity, ModelDO model,
            Map<String, Object> customFields, String entityTypeCode) {
        // TODO: 与 EntityValidationService 对齐后在此实现引用校验
    }

    public EntityDO prepareCreateEntity(EntityCreateReqVO reqVO) {
        Map<String, Object> baseFields = EntityFieldMapsSupport.normalizeMap(reqVO.getBaseFields());
        Long modelId = EntityFieldMapsSupport.getRequiredModelId(baseFields);
        String entityTypeCode = EntityFieldMapsSupport.getRequiredEntityTypeCode(baseFields);

        applyFacilityOwningOnCreate(baseFields, reqVO.getCustomFields(), entityTypeCode);
        reqVO.setBaseFields(baseFields);

        ModelDO model = validateModelExists(modelId);
        validateModelEntityType(modelId, entityTypeCode);
        validateRequestedDomainMatchesModel(baseFields, model);
        validateBaseFields(entityTypeCode, baseFields, reqVO.getCustomFields());
        validateCustomFields(modelId, baseFields, reqVO.getCustomFields());

        EntityDO data = EntityConvert.INSTANCE.convert(reqVO);
        data.setTenantId(getTenantId());
        // 业务域（Domain）最终以型号为准写入；请求域仅作选项校验
        data.setDomain(normalizeDomain(model.getDomain()));
        ensureDedicatedEntityCode(data, entityTypeCode);
        validateEntityCodeUnique(data, entityTypeCode, null);

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

        applyFacilityOwningOnUpdate(baseFields, reqVO.getCustomFields(), entityTypeCode, dbEntity);
        reqVO.setBaseFields(baseFields);

        ModelDO model = validateModelExists(modelId);
        validateModelEntityType(modelId, entityTypeCode);
        validateRequestedDomainMatchesModel(baseFields, model);
        validateBaseFields(entityTypeCode, baseFields, reqVO.getCustomFields());
        validateCustomFields(modelId, baseFields, reqVO.getCustomFields());

        reqVO.setBaseFields(baseFields);
        EntityDO update = EntityConvert.INSTANCE.convert(reqVO);
        update.setTenantId(dbEntity.getTenantId());
        // 业务域（Domain）随当前型号抄写
        update.setDomain(normalizeDomain(model.getDomain()));
        // 未传编码时沿用原值，避免把唯一校验落在空串上
        if (StrUtil.isBlank(update.getCode()) && StrUtil.isNotBlank(dbEntity.getCode())) {
            update.setCode(dbEntity.getCode());
        }
        validateEntityCodeUnique(update, entityTypeCode, dbEntity.getId());

        validateEntityReferences(update, model, update.getCustomFields(), entityTypeCode);

        if (update.getCustomFields() != null) {
            update.setCustomFields(customFieldValidationService.normalizeAndEncryptCustomFields(
                    update.getCustomFields(), modelId));
        }

        return update;
    }

    /**
     * 站场级创建：请求须显式带所属场站；规范化为 REF 对象写入 baseFields。
     */
    private void applyFacilityOwningOnCreate(
            Map<String, Object> baseFields, Map<String, Object> customFields, String entityTypeCode) {
        if (!requiresFacilityOwning(entityTypeCode)) {
            return;
        }
        facilityOwningFieldEnsureService.ensureForEntityTypeCode(entityTypeCode);
        Object raw = firstFacilityOwningRaw(baseFields, customFields);
        Long facilityId = FacilityOwningFieldCodes.extractId(raw);
        if (facilityId == null) {
            throw new ServiceException(400, "站场级数据须指定所属场站，请先选择要管理的场站");
        }
        baseFields.put(FacilityOwningFieldCodes.FIELD_CODE, FacilityOwningFieldCodes.toApiRef(facilityId));
        if (customFields != null) {
            customFields.remove(FacilityOwningFieldCodes.FIELD_CODE);
        }
    }

    /**
     * 站场级更新：所属场站只读；试图改站则 400；未传则回填原值。
     */
    private void applyFacilityOwningOnUpdate(
            Map<String, Object> baseFields,
            Map<String, Object> customFields,
            String entityTypeCode,
            EntityDO dbEntity) {
        if (!requiresFacilityOwning(entityTypeCode)) {
            return;
        }
        Map<String, Object> oldBag = new LinkedHashMap<>(emptyIfNull(dbEntity.getCustomFields()));
        entityDedicatedColumnService.mergePhysicalColumnsIntoBaseFields(dbEntity, oldBag);
        Long oldId = FacilityOwningFieldCodes.extractId(oldBag.get(FacilityOwningFieldCodes.FIELD_CODE));
        Object requestedRaw = firstFacilityOwningRaw(baseFields, customFields);
        Long requestedId = FacilityOwningFieldCodes.extractId(requestedRaw);

        if (oldId != null && requestedId != null && !Objects.equals(oldId, requestedId)) {
            throw new ServiceException(400, "所属场站创建后不可修改（挪站请走迁移能力）");
        }
        Long lockedId = oldId != null ? oldId : requestedId;
        if (lockedId != null) {
            baseFields.put(FacilityOwningFieldCodes.FIELD_CODE, FacilityOwningFieldCodes.toApiRef(lockedId));
            if (customFields != null) {
                customFields.remove(FacilityOwningFieldCodes.FIELD_CODE);
            }
        }
    }

    private boolean requiresFacilityOwning(String entityTypeCode) {
        if (StrUtil.isBlank(entityTypeCode)) {
            return false;
        }
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entityTypeCode.trim());
        if (entityType == null) {
            return false;
        }
        if (FacilityOwningFieldCodes.TARGET_ENTITY_TYPE.equalsIgnoreCase(entityType.getCode())) {
            return false;
        }
        String scope = entityType.getWorkScope();
        if (StrUtil.isBlank(scope)) {
            scope = EntityTypeDO.WORK_SCOPE_FACILITY;
        }
        return EntityTypeDO.WORK_SCOPE_FACILITY.equalsIgnoreCase(scope.trim());
    }

    private static Object firstFacilityOwningRaw(Map<String, Object> baseFields, Map<String, Object> customFields) {
        if (baseFields != null && baseFields.containsKey(FacilityOwningFieldCodes.FIELD_CODE)) {
            return baseFields.get(FacilityOwningFieldCodes.FIELD_CODE);
        }
        if (customFields != null) {
            return customFields.get(FacilityOwningFieldCodes.FIELD_CODE);
        }
        return null;
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

    /**
     * 专用表（ent_*）普遍有 code 列且常为 NOT NULL。
     * 创建时若未传 code，自动生成，避免 NOT NULL 插入失败。
     */
    private void ensureDedicatedEntityCode(EntityDO data, String entityTypeCode) {
        if (data == null || StrUtil.isNotBlank(data.getCode())) {
            return;
        }
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entityTypeCode);
        if (entityType == null) {
            return;
        }
        StorageTypeEnum storageType = StorageTypeEnum.getByCode(entityType.getStorageType());
        if (storageType == null || !storageType.isDedicated()) {
            throw new ServiceException(400, "实体类型未使用专用表存储，无法写入实体：" + entityTypeCode);
        }
        data.setCode(entityTypeCode + "-" + IdUtil.fastSimpleUUID());
    }

    /**
     * 提交兜底：同实体类型物理表内编码唯一（与 uk_ent_*_code_tenant 口径一致）。
     */
    private void validateEntityCodeUnique(EntityDO data, String entityTypeCode, Long excludeId) {
        if (data == null || !StrUtil.isNotBlank(data.getCode()) || !StrUtil.isNotBlank(entityTypeCode)) {
            return;
        }
        String code = data.getCode().trim();
        data.setCode(code);
        if (entityRepository.existsByExactCode(entityTypeCode.trim(), code, excludeId)) {
            throw new ServiceException(400, "编码已存在");
        }
    }

    /**
     * 请求若携带 domain（选项），须与型号 domain 一致；最终仍以型号写入实体。
     */
    private void validateRequestedDomainMatchesModel(Map<String, Object> baseFields, ModelDO model) {
        if (baseFields == null || model == null) {
            return;
        }
        Object raw = baseFields.get("domain");
        if (raw == null) {
            return;
        }
        String reqDomain = normalizeDomain(String.valueOf(raw));
        if (reqDomain == null) {
            return;
        }
        String modelDomain = normalizeDomain(model.getDomain());
        if (!Objects.equals(reqDomain, modelDomain)) {
            throw new ServiceException(400, "请求业务域与型号业务域不一致");
        }
    }

    /** 将型号上的业务域规范化写入实体；空白则为 null。 */
    private static String normalizeDomain(String domain) {
        if (StrUtil.isBlank(domain)) {
            return null;
        }
        return domain.trim();
    }
}
