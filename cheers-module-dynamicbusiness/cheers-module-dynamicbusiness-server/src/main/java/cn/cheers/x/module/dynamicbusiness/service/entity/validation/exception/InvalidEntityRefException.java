package cn.cheers.x.module.dynamicbusiness.service.entity.validation.exception;

import cn.cheers.x.module.dynamicbusiness.enums.ExtendFieldQueryErrorCodeConstants;

import java.util.List;

/**
 * ENTITY_REF 字段引用无效异常
 * 
 * <p>当 Entity 的 ENTITY_REF 类型字段引用验证失败时抛出此异常。</p>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>BR-VAL-005: ENTITY_REF 引用目标须存在且可解析</li>
 * </ul>
 * 
 * <h3>验证场景</h3>
 * <ul>
 *   <li>目标 Entity 不存在</li>
 *   <li>目标 Entity 无法按字段分配元数据解析</li>
 * </ul>
 * 
 * @author 扩展字段查询服务
 */
public class InvalidEntityRefException extends EntityValidationException {

    /**
     * 字段编码
     */
    private final String fieldCode;

    /**
     * 字段名称
     */
    private final String fieldName;

    /**
     * 无效的引用值（Entity ID）
     */
    private final Long invalidRefId;

    /**
     * 目标 Model 编码（如果存在）
     */
    private final String targetModelCode;

    /**
     * 允许的 Model 编码列表
     */
    private final List<String> allowedModelCodes;

    /**
     * 验证失败类型
     */
    private final FailureType failureType;

    /**
     * 验证失败类型枚举
     */
    public enum FailureType {
        /**
         * 目标 Entity 不存在
         */
        NOT_EXISTS,
        
        /**
         * 目标 Model 不在允许的范围内
         */
        MODEL_NOT_ALLOWED
    }

    /**
     * 构造 ENTITY_REF 引用无效异常 - 目标不存在
     * 
     * @param entityId 被验证的 Entity ID
     * @param modelId 被验证的 Model ID
     * @param fieldCode 字段编码
     * @param invalidRefId 无效的引用值
     */
    public static InvalidEntityRefException notExists(Long entityId, Long modelId, 
                                                       String fieldCode, Long invalidRefId) {
        return new InvalidEntityRefException(
                entityId, modelId, fieldCode, null, invalidRefId, null, null, FailureType.NOT_EXISTS,
                ExtendFieldQueryErrorCodeConstants.INVALID_ENTITY_REF_NOT_EXISTS, fieldCode, invalidRefId);
    }

    /**
     * 构造 ENTITY_REF 引用无效异常 - 目标不存在（带字段名称）
     * 
     * @param entityId 被验证的 Entity ID
     * @param modelId 被验证的 Model ID
     * @param fieldCode 字段编码
     * @param fieldName 字段名称
     * @param invalidRefId 无效的引用值
     */
    public static InvalidEntityRefException notExists(Long entityId, Long modelId, 
                                                       String fieldCode, String fieldName, Long invalidRefId) {
        return new InvalidEntityRefException(
                entityId, modelId, fieldCode, fieldName, invalidRefId, null, null, FailureType.NOT_EXISTS,
                ExtendFieldQueryErrorCodeConstants.INVALID_ENTITY_REF_NOT_EXISTS, fieldCode, invalidRefId);
    }

    /**
     * 构造 ENTITY_REF 引用无效异常 - 目标 Model 不在允许范围内
     * 
     * @param entityId 被验证的 Entity ID
     * @param modelId 被验证的 Model ID
     * @param fieldCode 字段编码
     * @param invalidRefId 无效的引用值
     * @param targetModelCode 目标 Model 编码
     * @param allowedModelCodes 允许的 Model 编码列表
     */
    public static InvalidEntityRefException modelNotAllowed(Long entityId, Long modelId, 
                                                             String fieldCode, Long invalidRefId,
                                                             String targetModelCode, List<String> allowedModelCodes) {
        return new InvalidEntityRefException(
                entityId, modelId, fieldCode, null, invalidRefId, targetModelCode, allowedModelCodes, 
                FailureType.MODEL_NOT_ALLOWED,
                ExtendFieldQueryErrorCodeConstants.INVALID_ENTITY_REF_MODEL_NOT_ALLOWED, 
                fieldCode, targetModelCode, String.join(", ", allowedModelCodes));
    }

    /**
     * 构造 ENTITY_REF 引用无效异常 - 目标 Model 不在允许范围内（带字段名称）
     * 
     * @param entityId 被验证的 Entity ID
     * @param modelId 被验证的 Model ID
     * @param fieldCode 字段编码
     * @param fieldName 字段名称
     * @param invalidRefId 无效的引用值
     * @param targetModelCode 目标 Model 编码
     * @param allowedModelCodes 允许的 Model 编码列表
     */
    public static InvalidEntityRefException modelNotAllowed(Long entityId, Long modelId, 
                                                             String fieldCode, String fieldName, Long invalidRefId,
                                                             String targetModelCode, List<String> allowedModelCodes) {
        return new InvalidEntityRefException(
                entityId, modelId, fieldCode, fieldName, invalidRefId, targetModelCode, allowedModelCodes, 
                FailureType.MODEL_NOT_ALLOWED,
                ExtendFieldQueryErrorCodeConstants.INVALID_ENTITY_REF_MODEL_NOT_ALLOWED, 
                fieldCode, targetModelCode, String.join(", ", allowedModelCodes));
    }

    /**
     * 私有构造函数
     */
    private InvalidEntityRefException(Long entityId, Long modelId, String fieldCode, String fieldName,
                                      Long invalidRefId, String targetModelCode, List<String> allowedModelCodes,
                                      FailureType failureType,
                                      cn.iocoder.yudao.framework.common.exception.ErrorCode errorCode, 
                                      Object... args) {
        super(errorCode, entityId, modelId, args);
        this.fieldCode = fieldCode;
        this.fieldName = fieldName;
        this.invalidRefId = invalidRefId;
        this.targetModelCode = targetModelCode;
        this.allowedModelCodes = allowedModelCodes;
        this.failureType = failureType;
    }

    /**
     * 获取字段编码
     */
    public String getFieldCode() {
        return fieldCode;
    }

    /**
     * 获取字段名称
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * 获取无效的引用值
     */
    public Long getInvalidRefId() {
        return invalidRefId;
    }

    /**
     * 获取目标 Model 编码
     */
    public String getTargetModelCode() {
        return targetModelCode;
    }

    /**
     * 获取允许的 Model 编码列表
     */
    public List<String> getAllowedModelCodes() {
        return allowedModelCodes;
    }

    /**
     * 获取验证失败类型
     */
    public FailureType getFailureType() {
        return failureType;
    }

    @Override
    public String toString() {
        return String.format("InvalidEntityRefException{entityId=%d, modelId=%d, fieldCode='%s', " +
                        "fieldName='%s', invalidRefId=%d, targetModelCode='%s', allowedModelCodes=%s, " +
                        "failureType=%s, code=%d, message='%s'}", 
                getEntityId(), getModelId(), fieldCode, fieldName, invalidRefId, targetModelCode, 
                allowedModelCodes, failureType, getCode(), getMessage());
    }
}
