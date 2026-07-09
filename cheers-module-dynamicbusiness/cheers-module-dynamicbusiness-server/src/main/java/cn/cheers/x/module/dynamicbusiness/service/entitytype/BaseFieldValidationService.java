package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;

import java.util.List;
import java.util.Map;

/**
 * 固定列字段验证服务接口
 * 
 * <p>提供固定列字段的验证功能，用于在创建/更新实体时验证固定列字段的必填性和数据格式。</p>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>BR-STG-017: 创建业务实体时验证固定列字段的必填性和数据格式</li>
 * </ul>
 * 
 * @author 基础服务模块
 */
public interface BaseFieldValidationService {

    /**
     * 验证固定列字段值
     * 
     * <p>验证指定业务类型的所有固定列字段值，包括：</p>
     * <ul>
     *   <li>必填字段不能为空</li>
     *   <li>数据格式必须符合字段定义</li>
     *   <li>数值范围必须在配置的范围内</li>
     *   <li>枚举值必须在允许的选项中</li>
     * </ul>
     * 
     * @param entityTypeCode 业务类型编码
     * @param fieldValues 字段值映射（字段编码 -> 字段值）
     * @return 验证结果，包含所有验证错误信息
     */
    BaseFieldValidationResult validateBaseFields(String entityTypeCode, Map<String, Object> fieldValues);

    /**
     * 验证单个固定列字段值
     * 
     * @param entityTypeCode 业务类型编码
     * @param fieldCode 字段编码
     * @param value 字段值
     * @return 验证错误信息，null 表示验证通过
     */
    String validateSingleField(String entityTypeCode, String fieldCode, Object value);

    /**
     * 获取业务类型的所有必填固定列字段
     * 
     * @param entityTypeCode 业务类型编码
     * @return 必填字段列表
     */
    List<EntityTypeBaseFieldDO> getRequiredFields(String entityTypeCode);

    /**
     * 检查业务类型是否有固定列字段
     * 
     * @param entityTypeCode 业务类型编码
     * @return 是否有固定列字段
     */
    boolean hasBaseFields(String entityTypeCode);

    /**
     * 固定列字段验证结果
     */
    class BaseFieldValidationResult {
        
        /** 是否验证通过 */
        private boolean valid;
        
        /** 验证错误列表 */
        private List<FieldError> errors;

        public BaseFieldValidationResult() {
            this.valid = true;
            this.errors = new java.util.ArrayList<>();
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public List<FieldError> getErrors() {
            return errors;
        }

        public void setErrors(List<FieldError> errors) {
            this.errors = errors;
        }

        public void addError(String fieldCode, String fieldName, String message) {
            this.valid = false;
            this.errors.add(new FieldError(fieldCode, fieldName, message));
        }

        /**
         * 获取格式化的错误消息
         * 
         * @return 格式化的错误消息，多个错误用分号分隔
         */
        public String getFormattedErrorMessage() {
            if (errors.isEmpty()) {
                return null;
            }
            StringBuilder sb = new StringBuilder("固定列字段验证失败：");
            for (int i = 0; i < errors.size(); i++) {
                if (i > 0) {
                    sb.append("；");
                }
                FieldError error = errors.get(i);
                sb.append(error.getFieldName()).append("(").append(error.getFieldCode()).append(")").append(error.getMessage());
            }
            return sb.toString();
        }

        /**
         * 字段错误信息
         */
        public static class FieldError {
            
            /** 字段编码 */
            private String fieldCode;
            
            /** 字段名称 */
            private String fieldName;
            
            /** 错误消息 */
            private String message;

            public FieldError(String fieldCode, String fieldName, String message) {
                this.fieldCode = fieldCode;
                this.fieldName = fieldName;
                this.message = message;
            }

            public String getFieldCode() {
                return fieldCode;
            }

            public void setFieldCode(String fieldCode) {
                this.fieldCode = fieldCode;
            }

            public String getFieldName() {
                return fieldName;
            }

            public void setFieldName(String fieldName) {
                this.fieldName = fieldName;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }
        }
    }
}
