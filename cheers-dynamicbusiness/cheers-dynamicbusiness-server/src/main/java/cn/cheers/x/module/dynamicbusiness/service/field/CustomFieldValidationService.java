package cn.cheers.x.module.dynamicbusiness.service.field;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 自定义字段校验服务接口
 */
public interface CustomFieldValidationService {

    /**
     * 验证自定义字段数据
     *
     * @param modelId 模型ID
     * @param customFields 自定义字段（键为字段 ID 或字段编码）
     */
    void validateCustomFields(Long modelId, Map<String, Object> customFields);

    /**
     * 验证型号分配字段；{@code field_source=BASE} 的分配从 {@code baseFields} 取值（写前分桶后不在 custom 里）。
     */
    void validateCustomFields(Long modelId,
                              Map<String, Object> baseFields,
                              Map<String, Object> customFields);

    /**
     * 验证自定义字段数据（使用预加载的字段配置）
     */
    void validateCustomFields(Map<Long, FieldDO> fieldMap,
                              Map<Long, ModelFieldAssignmentDO> assignmentMap,
                              Map<String, Object> customFields);

    /**
     * 验证型号分配字段（预加载配置）；BASE 来源字段从 baseFields 解析。
     */
    void validateCustomFields(Map<Long, FieldDO> fieldMap,
                              Map<Long, ModelFieldAssignmentDO> assignmentMap,
                              Map<String, Object> baseFields,
                              Map<String, Object> customFields);

    void validateFieldValue(FieldDO field, ModelFieldAssignmentDO assignment, Object value);

    List<FieldValidationConfig> getFieldConfigs(Long modelId);

    /**
     * 按多个型号一次加载字段配置（列表装 VO 用，避免按行 N+1）。
     */
    Map<Long, List<FieldValidationConfig>> getFieldConfigsByModelIds(Collection<Long> modelIds);

    /**
     * 规范化并加密自定义字段数据
     */
    Map<String, Object> normalizeAndEncryptCustomFields(Map<String, Object> customFields, Long modelId);

    /**
     * 解密自定义字段数据
     */
    Map<String, Object> decryptCustomFields(Map<String, Object> customFields, Long modelId);

    /**
     * 解密：使用预加载的型号字段配置（列表热路径）。
     */
    Map<String, Object> decryptCustomFields(Map<String, Object> customFields, List<FieldValidationConfig> configs);

    /**
     * 将 customFields 的键规范为字段编码（fieldCode），供 API / 列表列展示使用。
     * 库内仍以字段 ID 为键存储时，读路径在此做 ID → code 映射。
     */
    Map<String, Object> presentCustomFieldsForApi(Map<String, Object> customFields, Long modelId);

    /**
     * 展示键规范化：使用预加载的型号字段配置（列表热路径）。
     */
    Map<String, Object> presentCustomFieldsForApi(Map<String, Object> customFields, List<FieldValidationConfig> configs);

    class FieldValidationConfig {
        private final FieldDO field;
        private final ModelFieldAssignmentDO assignment;

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
