package cn.cheers.x.module.dynamicbusiness.service.field;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;
import cn.cheers.x.module.dynamicbusiness.util.SensitiveDataEncryptor;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自定义字段校验服务实现
 * 
 * 提供统一的字段校验逻辑，供各业务模块复用。
 * 
 * 支持的验证类型：
 * - 数据类型验证（来自字段定义 FieldDO.type）
 * - 必填验证（来自 ModelFieldAssignment.required）
 * - 范围验证（来自 ModelFieldAssignment.validation_rules）
 * - 格式验证（来自字段定义和 ModelFieldAssignment.validation_rules）
 * - 多选关联字段验证（ENTITY_REF_MULTI）：格式、数量限制、目标实体存在性
 * 
 * @author yudao
 */
@Service
@Validated
public class CustomFieldValidationServiceImpl implements CustomFieldValidationService {

    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;

    @Resource
    private FieldMapper fieldMapper;

    @Override
    public void validateCustomFields(Long modelId, Map<String, Object> customFields) {
        validateCustomFields(modelId, null, customFields);
    }

    @Override
    public void validateCustomFields(Long modelId,
                                     Map<String, Object> baseFields,
                                     Map<String, Object> customFields) {
        List<FieldValidationConfig> configs = getFieldConfigs(modelId);
        if (configs.isEmpty()) {
            return;
        }

        Map<Long, FieldDO> fieldMap = new HashMap<>();
        Map<Long, ModelFieldAssignmentDO> assignmentMap = new HashMap<>();
        for (FieldValidationConfig config : configs) {
            fieldMap.put(config.getField().getId(), config.getField());
            assignmentMap.put(config.getField().getId(), config.getAssignment());
        }

        validateCustomFields(fieldMap, assignmentMap, baseFields, customFields);
    }

    @Override
    public void validateCustomFields(Map<Long, FieldDO> fieldMap,
                                     Map<Long, ModelFieldAssignmentDO> assignmentMap,
                                     Map<String, Object> customFields) {
        validateCustomFields(fieldMap, assignmentMap, null, customFields);
    }

    @Override
    public void validateCustomFields(Map<Long, FieldDO> fieldMap,
                                     Map<Long, ModelFieldAssignmentDO> assignmentMap,
                                     Map<String, Object> baseFields,
                                     Map<String, Object> customFields) {
        if (fieldMap == null || fieldMap.isEmpty()) {
            return;
        }
        Map<String, Object> base = baseFields == null ? Collections.emptyMap() : baseFields;
        Map<String, Object> custom = customFields == null ? Collections.emptyMap() : customFields;

        List<String> missingRequired = new ArrayList<>();
        for (Map.Entry<Long, FieldDO> entry : fieldMap.entrySet()) {
            Long fieldId = entry.getKey();
            FieldDO field = entry.getValue();
            ModelFieldAssignmentDO assignment = assignmentMap.get(fieldId);
            if (assignment == null || !Boolean.TRUE.equals(assignment.getRequired())) {
                continue;
            }
            Object value = resolveAssignedFieldValue(base, custom, fieldId, field, assignment);
            if (isRequiredValueMissing(field, value)) {
                String label = field.getName() != null && !field.getName().isBlank()
                        ? field.getName().trim()
                        : (field.getCode() != null ? field.getCode() : String.valueOf(fieldId));
                missingRequired.add(label);
            }
        }
        if (!missingRequired.isEmpty()) {
            throw new ServiceException(400, "以下必填字段未填写：" + String.join("、", missingRequired));
        }

        for (Map.Entry<Long, FieldDO> entry : fieldMap.entrySet()) {
            Long fieldId = entry.getKey();
            FieldDO field = entry.getValue();
            ModelFieldAssignmentDO assignment = assignmentMap.get(fieldId);
            Object value = resolveAssignedFieldValue(base, custom, fieldId, field, assignment);
            // 必填已在上方汇总；此处只做有值时的类型/规则校验
            if (value != null && !(value instanceof String && ((String) value).isEmpty())) {
                validateValueType(field, value);
                if (assignment != null) {
                    validateValueWithRules(field, value, assignment);
                }
                if (FieldTypeEnum.isMultiEntityRef(field.getType())) {
                    validateEntityRefMultiField(field, assignment, value);
                }
            }
        }
    }

    /**
     * 型号分配字段取值：BASE 来源（如 facility_id 物理列）在写前分桶后只存在于 baseFields。
     */
    private static Object resolveAssignedFieldValue(Map<String, Object> baseFields,
                                                    Map<String, Object> customFields,
                                                    Long fieldId,
                                                    FieldDO field,
                                                    ModelFieldAssignmentDO assignment) {
        if (isBaseSourcedAssignment(assignment)) {
            Object fromBase = resolveFieldValueFromMap(baseFields, fieldId, field);
            if (fromBase != null) {
                return fromBase;
            }
        }
        return resolveFieldValueFromMap(customFields, fieldId, field);
    }

    private static boolean isBaseSourcedAssignment(ModelFieldAssignmentDO assignment) {
        if (assignment == null || assignment.getFieldSource() == null) {
            return false;
        }
        return "BASE".equalsIgnoreCase(assignment.getFieldSource().trim());
    }

    private static Object resolveFieldValueFromMap(Map<String, Object> fields, Long fieldId, FieldDO field) {
        if (fields == null || fields.isEmpty()) {
            return null;
        }
        String idKey = String.valueOf(fieldId);
        Object value = fields.get(idKey);
        if (value == null && field != null && field.getCode() != null) {
            value = fields.get(field.getCode());
        }
        return value;
    }

    private boolean isRequiredValueMissing(FieldDO field, Object value) {
        if (value == null || (value instanceof String && ((String) value).isEmpty())) {
            return true;
        }
        if (field != null && FieldTypeEnum.isMultiEntityRef(field.getType())) {
            List<Long> ids = parseEntityIdList(value);
            return CollectionUtils.isEmpty(ids);
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
        return false;
    }

    @Override
    public void validateFieldValue(FieldDO field, ModelFieldAssignmentDO assignment, Object value) {
        if (assignment != null && Boolean.TRUE.equals(assignment.getRequired())
                && isRequiredValueMissing(field, value)) {
            String label = field != null && field.getName() != null && !field.getName().isBlank()
                    ? field.getName().trim()
                    : "未命名字段";
            throw new ServiceException(400, "以下必填字段未填写：" + label);
        }

        // 如果字段有值，进行类型和规则校验
        if (value != null && !(value instanceof String && ((String) value).isEmpty())) {
            validateValueType(field, value);
            if (assignment != null) {
                validateValueWithRules(field, value, assignment);
            }
            if (FieldTypeEnum.isMultiEntityRef(field.getType())) {
                validateEntityRefMultiField(field, assignment, value);
            }
        }
    }

    @Override
    public List<FieldValidationConfig> getFieldConfigs(Long modelId) {
        if (modelId == null) {
            return new ArrayList<>();
        }
        return getFieldConfigsByModelIds(Collections.singletonList(modelId))
                .getOrDefault(modelId, new ArrayList<>());
    }

    @Override
    public Map<Long, List<FieldValidationConfig>> getFieldConfigsByModelIds(Collection<Long> modelIds) {
        Map<Long, List<FieldValidationConfig>> out = new LinkedHashMap<>();
        if (CollectionUtils.isEmpty(modelIds)) {
            return out;
        }
        List<Long> ids = modelIds.stream().filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            return out;
        }
        for (Long id : ids) {
            out.put(id, new ArrayList<>());
        }
        List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelIds(ids);
        if (CollectionUtils.isEmpty(assignments)) {
            return out;
        }
        Set<Long> fieldIds = assignments.stream()
                .map(ModelFieldAssignmentDO::getFieldId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, FieldDO> fieldById = new HashMap<>();
        if (!fieldIds.isEmpty()) {
            List<FieldDO> fields = fieldMapper.selectBatchIds(fieldIds);
            if (fields != null) {
                for (FieldDO field : fields) {
                    if (field != null && field.getId() != null) {
                        fieldById.put(field.getId(), field);
                    }
                }
            }
        }
        for (ModelFieldAssignmentDO assignment : assignments) {
            if (assignment == null || assignment.getModelId() == null || assignment.getFieldId() == null) {
                continue;
            }
            FieldDO field = fieldById.get(assignment.getFieldId());
            if (field == null) {
                continue;
            }
            out.computeIfAbsent(assignment.getModelId(), k -> new ArrayList<>())
                    .add(new FieldValidationConfig(field, assignment));
        }
        return out;
    }

    @Override
    public Map<String, Object> normalizeAndEncryptCustomFields(Map<String, Object> customFields, Long modelId) {
        if (customFields == null || customFields.isEmpty()) {
            return customFields;
        }

        try {
            Map<String, Object> result = new HashMap<>(customFields);
            List<FieldValidationConfig> configs = getFieldConfigs(modelId);

            boolean modified = false;
            for (FieldValidationConfig config : configs) {
                FieldDO field = config.getField();

                if (isSensitiveField(field)) {
                    String idKey = String.valueOf(field.getId());
                    Object val = result.get(idKey);
                    if (val == null && field.getCode() != null) {
                        idKey = field.getCode();
                        val = result.get(idKey);
                    }
                    if (val instanceof String) {
                        result.put(idKey, SensitiveDataEncryptor.encrypt((String) val));
                        modified = true;
                    }
                }
            }

            return modified ? result : customFields;
        } catch (Exception e) {
            return customFields;
        }
    }

    @Override
    public Map<String, Object> decryptCustomFields(Map<String, Object> customFields, Long modelId) {
        if (customFields == null || customFields.isEmpty() || modelId == null) {
            return customFields;
        }
        return decryptCustomFields(customFields, getFieldConfigs(modelId));
    }

    @Override
    public Map<String, Object> decryptCustomFields(Map<String, Object> customFields,
                                                   List<FieldValidationConfig> configs) {
        if (customFields == null || customFields.isEmpty()) {
            return customFields;
        }
        if (CollectionUtils.isEmpty(configs)) {
            return customFields;
        }

        try {
            Map<String, Object> result = new HashMap<>(customFields);
            boolean modified = false;
            for (FieldValidationConfig config : configs) {
                FieldDO field = config.getField();
                if (field == null || !isSensitiveField(field)) {
                    continue;
                }
                String idKey = String.valueOf(field.getId());
                Object val = result.get(idKey);
                String writeKey = idKey;
                if (val == null && field.getCode() != null) {
                    writeKey = field.getCode();
                    val = result.get(writeKey);
                }
                if (val instanceof String) {
                    result.put(writeKey, SensitiveDataEncryptor.decrypt((String) val));
                    modified = true;
                }
            }
            return modified ? result : customFields;
        } catch (Exception e) {
            return customFields;
        }
    }

    @Override
    public Map<String, Object> presentCustomFieldsForApi(Map<String, Object> customFields, Long modelId) {
        if (customFields == null || customFields.isEmpty()) {
            return customFields;
        }
        List<FieldValidationConfig> configs = modelId == null
                ? Collections.emptyList()
                : getFieldConfigs(modelId);
        return presentCustomFieldsForApi(customFields, configs);
    }

    @Override
    public Map<String, Object> presentCustomFieldsForApi(Map<String, Object> customFields,
                                                         List<FieldValidationConfig> configs) {
        if (customFields == null || customFields.isEmpty()) {
            return customFields;
        }

        Map<String, Object> presented = new LinkedHashMap<>();
        Set<String> mappedKeys = new HashSet<>();
        Map<Long, String> fieldIdToCode = new HashMap<>();

        if (!CollectionUtils.isEmpty(configs)) {
            for (FieldValidationConfig config : configs) {
                FieldDO field = config.getField();
                if (field == null || !StringUtils.hasText(field.getCode())) {
                    continue;
                }
                if (field.getId() != null) {
                    fieldIdToCode.put(field.getId(), field.getCode());
                }
                String idKey = String.valueOf(field.getId());
                String codeKey = field.getCode();
                Object value = customFields.get(idKey);
                if (value == null) {
                    value = customFields.get(codeKey);
                }
                if (value != null) {
                    presented.put(codeKey, value);
                    mappedKeys.add(idKey);
                    mappedKeys.add(codeKey);
                }
            }
        }

        for (Map.Entry<String, Object> entry : customFields.entrySet()) {
            if (mappedKeys.contains(entry.getKey())) {
                continue;
            }
            String storageKey = entry.getKey();
            String codeKey = resolveFieldCodeByStorageKey(storageKey, fieldIdToCode);
            if (StringUtils.hasText(codeKey)) {
                presented.putIfAbsent(codeKey, entry.getValue());
                mappedKeys.add(storageKey);
            } else {
                presented.putIfAbsent(storageKey, entry.getValue());
            }
        }

        return presented;
    }

    /**
     * 将 customFields 存储键（字段 id 或历史 code）解析为对外 fieldCode。
     */
    private String resolveFieldCodeByStorageKey(String storageKey) {
        return resolveFieldCodeByStorageKey(storageKey, Collections.emptyMap());
    }

    private String resolveFieldCodeByStorageKey(String storageKey, Map<Long, String> fieldIdToCode) {
        if (!StringUtils.hasText(storageKey)) {
            return null;
        }
        String trimmed = storageKey.trim();
        if (trimmed.startsWith("F-")) {
            return trimmed;
        }
        try {
            long fieldId = Long.parseLong(trimmed);
            String cached = fieldIdToCode.get(fieldId);
            if (StringUtils.hasText(cached)) {
                return cached;
            }
            FieldDO field = fieldMapper.selectById(fieldId);
            if (field != null && StringUtils.hasText(field.getCode())) {
                return field.getCode();
            }
        } catch (NumberFormatException ignored) {
            // 非数字键保持原样
        }
        return null;
    }

    /**
     * 验证字段值类型
     */
    private void validateValueType(FieldDO field, Object value) {
        String type = field.getType();
        if (type == null) {
            return;
        }

        switch (type.toUpperCase()) {
            case "NUMBER":
            case "INTEGER":
            case "DECIMAL":
                validateNumberType(field, value);
                break;
            case "BOOLEAN":
                validateBooleanType(field, value);
                break;
            case "DATE":
                validateDateType(field, value);
                break;
            case "DATETIME":
                validateDateTimeType(field, value);
                break;
            case "ENUM":
                validateEnumType(field, value);
                break;
            case "JSON":
                validateJsonType(field, value);
                break;
            case "ENTITY_REF_MULTI":
                validateEntityRefMultiType(field, value);
                break;
            // TEXT 类型不需要严格验证
            default:
                break;
        }
    }

    /**
     * 验证数值类型
     */
    private void validateNumberType(FieldDO field, Object value) {
        if (value instanceof Number) {
            return;
        }
        try {
            Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            throw new ServiceException(400, String.format("字段[%s]类型错误，期望数字", field.getName()));
        }
    }

    /**
     * 验证布尔类型
     */
    private void validateBooleanType(FieldDO field, Object value) {
        if (value instanceof Boolean) {
            return;
        }
        String strValue = value.toString().toLowerCase();
        if (!"true".equals(strValue) && !"false".equals(strValue)) {
            throw new ServiceException(400, String.format("字段[%s]类型错误，期望布尔值", field.getName()));
        }
    }

    /**
     * 验证日期类型
     */
    private void validateDateType(FieldDO field, Object value) {
        if (!(value instanceof String)) {
            throw new ServiceException(400, String.format("字段[%s]类型错误，期望日期字符串", field.getName()));
        }
        try {
            LocalDate.parse((String) value, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            // 尝试其他常见格式
            try {
                LocalDate.parse((String) value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e2) {
                throw new ServiceException(400, String.format("字段[%s]日期格式错误，期望格式：yyyy-MM-dd", field.getName()));
            }
        }
    }

    /**
     * 验证日期时间类型
     */
    private void validateDateTimeType(FieldDO field, Object value) {
        if (!(value instanceof String)) {
            throw new ServiceException(400, String.format("字段[%s]类型错误，期望日期时间字符串", field.getName()));
        }
        try {
            LocalDateTime.parse((String) value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            try {
                LocalDateTime.parse((String) value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (DateTimeParseException e2) {
                throw new ServiceException(400, 
                    String.format("字段[%s]日期时间格式错误，期望格式：yyyy-MM-dd HH:mm:ss", field.getName()));
            }
        }
    }

    /**
     * 验证枚举类型
     * 
     * 需求：FR-015-A, BR-VAL-016
     * 验证枚举字段值是否在选项列表的 value 范围内
     */
    private void validateEnumType(FieldDO field, Object value) {
        String strValue = value.toString();
        
        // 优先从 options 字段获取枚举选项（新方式）
        String options = field.getOptions();
        if (options != null && !options.isEmpty() && JSONUtil.isTypeJSONArray(options)) {
            JSONArray optionsArray = JSONUtil.parseArray(options);
            Set<String> allowedValues = new HashSet<>();
            for (int i = 0; i < optionsArray.size(); i++) {
                cn.hutool.json.JSONObject option = optionsArray.getJSONObject(i);
                if (option != null && option.containsKey("value")) {
                    allowedValues.add(option.getStr("value"));
                }
            }
            
            if (!allowedValues.isEmpty() && !allowedValues.contains(strValue)) {
                throw new ServiceException(400, 
                    String.format("字段[%s]值不在允许的枚举范围内，允许值：%s", field.getName(), allowedValues));
            }
            return;
        }
        
        // 兼容旧方式：从字段描述中获取枚举值列表
        JSONObject descJson = parseDescription(field);
        if (descJson != null && descJson.containsKey("enumValues")) {
            List<String> enumValues = descJson.getList("enumValues", String.class);
            if (enumValues != null && !enumValues.isEmpty()) {
                if (!enumValues.contains(strValue)) {
                    throw new ServiceException(400, 
                        String.format("字段[%s]值不在允许的枚举范围内，允许值：%s", field.getName(), enumValues));
                }
            }
        }
    }

    /**
     * 验证JSON类型
     */
    private void validateJsonType(FieldDO field, Object value) {
        if (value instanceof String) {
            try {
                JSON.parse((String) value);
            } catch (Exception e) {
                throw new ServiceException(400, String.format("字段[%s]不是有效的JSON格式", field.getName()));
            }
        }
    }

    /**
     * 验证字段值业务规则
     */
    private void validateValueWithRules(FieldDO field, Object value, ModelFieldAssignmentDO assignment) {
        JSONObject rules = parseValidationRules(assignment);
        if (rules == null || rules.isEmpty()) {
            return;
        }

        String type = field.getType();
        if (type == null) {
            return;
        }

        switch (type.toUpperCase()) {
            case "NUMBER":
            case "INTEGER":
            case "DECIMAL":
                validateNumberRules(field, value, rules);
                break;
            case "TEXT":
                validateTextRules(field, value, rules);
                break;
            default:
                break;
        }
    }

    /**
     * 验证数值类型的业务规则
     */
    private void validateNumberRules(FieldDO field, Object value, JSONObject rules) {
        Double num = parseNumber(value);
        if (num == null) {
            return;
        }

        if (rules.containsKey("min")) {
            Double min = rules.getDouble("min");
            if (min != null && num < min) {
                throw new ServiceException(400, 
                    String.format("字段[%s]小于最小值%.2f", field.getName(), min));
            }
        }

        if (rules.containsKey("max")) {
            Double max = rules.getDouble("max");
            if (max != null && num > max) {
                throw new ServiceException(400, 
                    String.format("字段[%s]大于最大值%.2f", field.getName(), max));
            }
        }
    }

    /**
     * 验证文本类型的业务规则
     */
    private void validateTextRules(FieldDO field, Object value, JSONObject rules) {
        String str = value.toString();

        if (rules.containsKey("pattern")) {
            String pattern = rules.getString("pattern");
            if (pattern != null && !str.matches(pattern)) {
                throw new ServiceException(400, String.format("字段[%s]格式不符合要求", field.getName()));
            }
        }

        if (rules.containsKey("maxLength")) {
            Integer maxLength = rules.getInteger("maxLength");
            if (maxLength != null && str.length() > maxLength) {
                throw new ServiceException(400, 
                    String.format("字段[%s]长度超限（最大长度：%d）", field.getName(), maxLength));
            }
        }

        if (rules.containsKey("minLength")) {
            Integer minLength = rules.getInteger("minLength");
            if (minLength != null && str.length() < minLength) {
                throw new ServiceException(400, 
                    String.format("字段[%s]长度不足（最小长度：%d）", field.getName(), minLength));
            }
        }
    }

    /**
     * 检查字段是否为敏感字段
     */
    private boolean isSensitiveField(FieldDO field) {
        JSONObject descJson = parseDescription(field);
        return descJson != null && descJson.getBooleanValue("sensitive");
    }

    /**
     * 解析字段描述JSON
     */
    private JSONObject parseDescription(FieldDO field) {
        if (field.getDescription() == null || field.getDescription().isEmpty()) {
            return null;
        }
        try {
            return JSON.parseObject(field.getDescription());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析验证规则JSON
     */
    private JSONObject parseValidationRules(ModelFieldAssignmentDO assignment) {
        if (assignment.getValidationRules() == null || assignment.getValidationRules().isEmpty()) {
            return null;
        }
        try {
            return JSON.parseObject(assignment.getValidationRules());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析数值
     */
    private Double parseNumber(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ==================== ENTITY_REF_MULTI 多选关联字段验证 ====================

    /**
     * 验证 ENTITY_REF_MULTI 字段类型格式
     * 
     * <p>验证字段值是否为有效的数组格式，支持：</p>
     * <ul>
     *   <li>List 类型</li>
     *   <li>JSON 数组字符串，如 "[301, 302]"</li>
     *   <li>逗号分隔的字符串，如 "301,302"</li>
     * </ul>
     * 
     * @param field 字段定义
     * @param value 字段值
     * @throws ServiceException 格式验证失败时抛出异常
     */
    private void validateEntityRefMultiType(FieldDO field, Object value) {
        List<Long> ids = parseEntityIdList(value);
        if (ids == null) {
            throw new ServiceException(400, 
                String.format("字段[%s]格式错误，期望数组格式（如 [1, 2, 3] 或 \"1,2,3\"）", field.getName()));
        }
    }

    /**
     * 验证 ENTITY_REF_MULTI 字段的完整性
     * 
     * <p>包含以下验证：</p>
     * <ul>
     *   <li>格式验证：必须是有效的数组格式</li>
     *   <li>数量验证：关联数量不超过 maxRelations 限制</li>
     *   <li>存在性验证：所有目标实体必须存在</li>
     * </ul>
     * 
     * @param field 字段定义
     * @param assignment 字段分配配置
     * @param value 字段值
     * @throws ServiceException 验证失败时抛出异常
     */
    private void validateEntityRefMultiField(FieldDO field, ModelFieldAssignmentDO assignment, Object value) {
        // 1. 解析实体 ID 列表
        List<Long> entityIds = parseEntityIdList(value);
        if (entityIds == null) {
            throw new ServiceException(400, 
                String.format("字段[%s]格式错误，期望数组格式（如 [1, 2, 3] 或 \"1,2,3\"）", field.getName()));
        }

        // 空数组不需要进一步验证
        if (entityIds.isEmpty()) {
            return;
        }

        // 2. 验证关联数量不超过 maxRelations 限制
        Integer maxRelations = field.getMaxRelations();
        if (maxRelations != null && maxRelations > 0 && entityIds.size() > maxRelations) {
            throw new ServiceException(400, 
                String.format("字段[%s]关联数量超限，最多可关联 %d 个实体，当前 %d 个", 
                    field.getName(), maxRelations, entityIds.size()));
        }

        // 3. 验证是否有重复的实体 ID
        Set<Long> uniqueIds = new HashSet<>(entityIds);
        if (uniqueIds.size() != entityIds.size()) {
            throw new ServiceException(400, 
                String.format("字段[%s]包含重复的关联实体", field.getName()));
        }

        // 4. 不验证目标实体存在性
    }


    /**
     * 解析实体 ID 列表（用于多选关联字段）
     * 
     * <p>支持以下格式：</p>
     * <ul>
     *   <li>List&lt;Long&gt; 或 List&lt;Integer&gt;</li>
     *   <li>JSON 数组字符串，如 "[301, 302]"</li>
     *   <li>逗号分隔的字符串，如 "301,302"</li>
     * </ul>
     * 
     * @param value 字段值
     * @return 实体 ID 列表，如果解析失败返回 null
     */
    private List<Long> parseEntityIdList(Object value) {
        if (value == null) {
            return null;
        }

        // 如果已经是 List 类型
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            if (list.isEmpty()) {
                return new ArrayList<>();
            }
            
            List<Long> result = new ArrayList<>();
            for (Object item : list) {
                Long id = parseEntityId(item);
                if (id != null) {
                    result.add(id);
                }
            }
            return result;
        }

        // 如果是字符串，尝试解析
        if (value instanceof String) {
            String str = ((String) value).trim();
            if (str.isEmpty()) {
                return new ArrayList<>();
            }

            // 尝试解析 JSON 数组格式 [301, 302]
            if (str.startsWith("[") && str.endsWith("]")) {
                try {
                    com.alibaba.fastjson2.JSONArray jsonArray = JSON.parseArray(str);
                    List<Long> result = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        Long id = parseEntityId(jsonArray.get(i));
                        if (id != null) {
                            result.add(id);
                        }
                    }
                    return result;
                } catch (Exception e) {
                    return null;
                }
            }

            // 尝试解析逗号分隔格式 301,302
            if (str.contains(",")) {
                String[] parts = str.split(",");
                List<Long> result = new ArrayList<>();
                for (String part : parts) {
                    Long id = parseEntityId(part.trim());
                    if (id != null) {
                        result.add(id);
                    }
                }
                return result.isEmpty() ? null : result;
            }

            // 单个值
            Long singleId = parseEntityId(str);
            return singleId != null ? new ArrayList<>(List.of(singleId)) : null;
        }

        // 如果是数组类型
        if (value.getClass().isArray()) {
            Object[] array = (Object[]) value;
            List<Long> result = new ArrayList<>();
            for (Object item : array) {
                Long id = parseEntityId(item);
                if (id != null) {
                    result.add(id);
                }
            }
            return result;
        }

        return null;
    }

    /**
     * 解析单个实体 ID
     * 
     * @param value 值
     * @return 实体 ID，解析失败返回 null
     */
    private Long parseEntityId(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        if (value instanceof String) {
            String str = ((String) value).trim();
            if (str.isEmpty()) {
                return null;
            }
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }
}
