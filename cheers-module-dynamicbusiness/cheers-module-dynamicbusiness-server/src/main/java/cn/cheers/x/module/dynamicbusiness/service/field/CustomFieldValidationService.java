package cn.cheers.x.module.dynamicbusiness.service.field;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;

import java.util.List;
import java.util.Map;

/**
 * 自定义字段校验服务接口
 * 
 * 提供统一的字段校验逻辑，供各业务模块复用。
 * 支持系统级业务类型（设备管理、任务管理等）通过 business_type_code 区分不同业务，
 * 并复用同一套字段校验逻辑。
 * 
 * 校验规则包括：
 * - 数据类型验证（来自字段定义 FieldDO.type）
 * - 必填验证（来自 ModelFieldAssignment.required）
 * - 范围验证（来自 ModelFieldAssignment.validation_rules）
 * - 格式验证（来自字段定义和 ModelFieldAssignment.validation_rules）
 * 
 * @author yudao
 */
public interface CustomFieldValidationService {

    /**
     * 验证自定义字段数据
     * 
     * @param modelId 模型ID，用于获取字段分配配置
     * @param customFieldsJson 自定义字段数据（JSON格式，键为字段ID，值为字段值）
     * @throws cn.iocoder.yudao.framework.common.exception.ServiceException 验证失败时抛出异常
     */
    void validateCustomFields(Long modelId, String customFieldsJson);

    /**
     * 验证自定义字段数据（使用预加载的字段配置）
     * 
     * @param fieldMap 字段定义映射（键为字段ID）
     * @param assignmentMap 字段分配配置映射（键为字段ID）
     * @param customFieldsJson 自定义字段数据（JSON格式）
     * @throws cn.iocoder.yudao.framework.common.exception.ServiceException 验证失败时抛出异常
     */
    void validateCustomFields(Map<Long, FieldDO> fieldMap, 
                              Map<Long, ModelFieldAssignmentDO> assignmentMap, 
                              String customFieldsJson);

    /**
     * 验证单个字段值
     * 
     * @param field 字段定义
     * @param assignment 字段分配配置（可为null）
     * @param value 字段值
     * @throws cn.iocoder.yudao.framework.common.exception.ServiceException 验证失败时抛出异常
     */
    void validateFieldValue(FieldDO field, ModelFieldAssignmentDO assignment, Object value);

    /**
     * 获取模型的字段配置
     * 
     * @param modelId 模型ID
     * @return 字段配置列表（包含字段定义和分配配置）
     */
    List<FieldValidationConfig> getFieldConfigs(Long modelId);

    /**
     * 规范化并加密自定义字段数据
     * 
     * @param customFieldsJson 原始自定义字段数据
     * @param modelId 模型ID
     * @return 规范化并加密后的数据
     */
    String normalizeAndEncryptCustomFields(String customFieldsJson, Long modelId);

    /**
     * 解密自定义字段数据
     * 
     * @param customFieldsJson 加密的自定义字段数据
     * @param modelId 模型ID
     * @return 解密后的数据
     */
    String decryptCustomFields(String customFieldsJson, Long modelId);

    /**
     * 字段验证配置
     */
    class FieldValidationConfig {
        private FieldDO field;
        private ModelFieldAssignmentDO assignment;

        public FieldValidationConfig(FieldDO field, ModelFieldAssignmentDO assignment) {
            this.field = field;
            this.assignment = assignment;
        }

        public FieldDO getField() {
            return field;
        }

        public ModelFieldAssignmentDO getAssignment() {
            return assignment;
        }
    }
}
