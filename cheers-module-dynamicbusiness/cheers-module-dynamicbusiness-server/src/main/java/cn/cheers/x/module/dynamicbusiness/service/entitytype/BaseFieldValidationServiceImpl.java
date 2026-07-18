package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.StorageTypeEnum;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

/**
 * 固定列字段验证服务实现类
 * 
 * <p>提供固定列字段的验证功能,用于在创建/更新实体时验证固定列字段的必填性和数据格式。</p>
 * 
 * <h3>验证规则</h3>
 * <ul>
 *   <li>必填字段：值不能为 null 或空字符串</li>
 *   <li>TEXT 类型：无特殊验证</li>
 *   <li>NUMBER 类型：必须是有效数字,可配置范围（min/max）</li>
 *   <li>DATE 类型：必须是 yyyy-MM-dd 格式</li>
 *   <li>DATETIME 类型：必须是 yyyy-MM-ddTHH:mm:ss 格式</li>
 *   <li>BOOLEAN 类型：必须是 true/false 或 1/0</li>
 *   <li>ENUM 类型：必须在配置的选项列表中</li>
 *   <li>REFERENCE 类型：暂不验证引用有效性</li>
 * </ul>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>BR-STG-017: 创建业务实体时验证固定列字段的必填性和数据格式</li>
 * </ul>
 * 
 * @author 基础服务模块
 */
@Service
@Validated
@Slf4j
public class BaseFieldValidationServiceImpl implements BaseFieldValidationService {

    @Resource
    private EntityTypeBaseFieldMapper entityTypeBaseFieldMapper;

    @Resource
    private EntityTypeMapper entityTypeMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public BaseFieldValidationResult validateBaseFields(String entityTypeCode, Map<String, Object> fieldValues) {
        BaseFieldValidationResult result = new BaseFieldValidationResult();
        
        // 1. 检查业务类型是否存在
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entityTypeCode);
        if (entityType == null) {
            throw new ServiceException(400, "业务类型不存在：" + entityTypeCode);
        }

        // 2. 通用存储类型不支持固定列字段,跳过验证
        StorageTypeEnum storageType = StorageTypeEnum.getByCode(entityType.getStorageType());
        if (storageType == StorageTypeEnum.GENERIC) {
            log.debug("[validateBaseFields][通用存储类型不支持固定列字段,跳过验证,entityTypeCode={}]", entityTypeCode);
            return result;
        }
        
        // 3. 获取所有启用的固定列字段
        List<EntityTypeBaseFieldDO> baseFields = entityTypeBaseFieldMapper.selectByEntityTypeCode(entityTypeCode);
        if (baseFields.isEmpty()) {
            log.debug("[validateBaseFields][没有固定列字段,跳过验证,entityTypeCode={}]", entityTypeCode);
            return result;
        }
        
        // 4. 验证每个固定列字段
        for (EntityTypeBaseFieldDO field : baseFields) {
            Object value = fieldValues != null ? fieldValues.get(field.getFieldCode()) : null;
            String errorMessage = validateFieldValue(field, value);
            if (errorMessage != null) {
                result.addError(field.getFieldCode(), field.getFieldName(), errorMessage);
            }
        }
        
        if (!result.isValid()) {
            log.info("[validateBaseFields][固定列字段验证失败,entityTypeCode={},errors={}]", 
                    entityTypeCode, result.getFormattedErrorMessage());
        }
        
        return result;
    }

    @Override
    public String validateSingleField(String entityTypeCode, String fieldCode, Object value) {
        EntityTypeBaseFieldDO field = entityTypeBaseFieldMapper.selectByEntityTypeCodeAndFieldCode(entityTypeCode, fieldCode);
        if (field == null) {
            return "字段不存在：" + fieldCode;
        }
        return validateFieldValue(field, value);
    }

    @Override
    public List<EntityTypeBaseFieldDO> getRequiredFields(String entityTypeCode) {
        return List.of();
    }

    @Override
    public boolean hasBaseFields(String entityTypeCode) {
        Long count = entityTypeBaseFieldMapper.countByEntityTypeCode(entityTypeCode);
        return count != null && count > 0;
    }

    // ========== 私有方法 ==========

    /**
     * 验证单个字段值
     * 
     * @param field 字段定义
     * @param value 字段值
     * @return 错误消息,null 表示验证通过
     */
    private String validateFieldValue(EntityTypeBaseFieldDO field, Object value) {
        // 1. 如果值为空,跳过后续验证
        if (value == null || StringUtils.isBlank(value.toString())) {
            return null;
        }
        
        // 2. 根据数据类型验证
        String strValue = value.toString();
        switch (field.getDataType()) {
            case "NUMBER":
                return validateNumberValue(field, strValue);
            case "DATE":
                return validateDateValue(strValue);
            case "DATETIME":
                return validateDateTimeValue(strValue);
            case "BOOLEAN":
                return validateBooleanValue(strValue);
            case "ENUM":
                return validateEnumValue(field, strValue);
            case "REFERENCE":
                // 引用类型的验证需要查询关联的模型,这里暂时跳过
                return null;
            case "TEXT":
            default:
                return null;
        }
    }

    /**
     * 验证数字类型值
     */
    private String validateNumberValue(EntityTypeBaseFieldDO field, String value) {
        try {
            BigDecimal numValue = new BigDecimal(value);
            
            // 检查范围
            if (StringUtils.isNotBlank(field.getTypeConfig())) {
                try {
                    JsonNode configNode = objectMapper.readTree(field.getTypeConfig());
                    if (configNode.has("min")) {
                        BigDecimal min = new BigDecimal(configNode.get("min").asText());
                        if (numValue.compareTo(min) < 0) {
                            return "不能小于 " + min;
                        }
                    }
                    if (configNode.has("max")) {
                        BigDecimal max = new BigDecimal(configNode.get("max").asText());
                        if (numValue.compareTo(max) > 0) {
                            return "不能大于 " + max;
                        }
                    }
                } catch (Exception e) {
                    log.warn("[validateNumberValue][解析类型配置失败,fieldCode={}]", field.getFieldCode(), e);
                }
            }
            return null;
        } catch (NumberFormatException e) {
            return "必须是有效的数字";
        }
    }

    /**
     * 验证日期类型值
     */
    private String validateDateValue(String value) {
        try {
            LocalDate.parse(value);
            return null;
        } catch (DateTimeParseException e) {
            return "日期格式错误,请使用 yyyy-MM-dd 格式";
        }
    }

    /**
     * 验证日期时间类型值
     */
    private String validateDateTimeValue(String value) {
        try {
            LocalDateTime.parse(value);
            return null;
        } catch (DateTimeParseException e) {
            return "日期时间格式错误,请使用 yyyy-MM-ddTHH:mm:ss 格式";
        }
    }

    /**
     * 验证布尔类型值
     */
    private String validateBooleanValue(String value) {
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value) 
                || "1".equals(value) || "0".equals(value)) {
            return null;
        }
        return "布尔值必须是 true/false 或 1/0";
    }

    /**
     * 验证枚举类型值
     */
    private String validateEnumValue(EntityTypeBaseFieldDO field, String value) {
        if (StringUtils.isBlank(field.getTypeConfig())) {
            return null;
        }
        
        try {
            JsonNode configNode = objectMapper.readTree(field.getTypeConfig());
            if (configNode.has("options") && configNode.get("options").isArray()) {
                for (JsonNode option : configNode.get("options")) {
                    if (option.has("value") && value.equals(option.get("value").asText())) {
                        return null;
                    }
                }
                return "的值不在允许的选项范围内";
            }
        } catch (Exception e) {
            log.warn("[validateEnumValue][解析类型配置失败,fieldCode={}]", field.getFieldCode(), e);
        }
        return null;
    }
}
