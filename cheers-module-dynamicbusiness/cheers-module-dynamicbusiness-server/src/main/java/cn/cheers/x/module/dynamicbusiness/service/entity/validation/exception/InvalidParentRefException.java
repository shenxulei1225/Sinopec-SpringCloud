package cn.cheers.x.module.dynamicbusiness.service.entity.validation.exception;

import cn.cheers.x.module.dynamicbusiness.enums.ExtendFieldQueryErrorCodeConstants;

/**
 * parent_id 引用无效异常
 * 
 * <p>当 Entity 的 parent_id 引用验证失败时抛出此异常。</p>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>BR-VAL-003: parent_id 必须指向同 Model 的 Entity（系统强制）</li>
 * </ul>
 * 
 * <h3>验证场景</h3>
 * <ul>
 *   <li>目标 Entity 不存在</li>
 *   <li>目标 Entity 与当前 Entity 不属于同一个 Model</li>
 * </ul>
 * 
 * @author 扩展字段查询服务
 */
public class InvalidParentRefException extends EntityValidationException {

    /**
     * 无效的 parent_id 值
     */
    private final Long invalidParentId;

    /**
     * 当前 Entity 的 Model 编码
     */
    private final String currentModelCode;

    /**
     * 目标 Entity 的 Model 编码（如果存在）
     */
    private final String targetModelCode;

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
         * 目标 Entity 与当前 Entity 不属于同一个 Model
         */
        DIFFERENT_MODEL
    }

    /**
     * 构造 parent_id 引用无效异常 - 目标不存在
     * 
     * @param entityId 被验证的 Entity ID
     * @param modelId 被验证的 Model ID
     * @param invalidParentId 无效的 parent_id 值
     */
    public static InvalidParentRefException notExists(Long entityId, Long modelId, Long invalidParentId) {
        return new InvalidParentRefException(
                entityId, modelId, invalidParentId, null, null, FailureType.NOT_EXISTS,
                ExtendFieldQueryErrorCodeConstants.INVALID_PARENT_REF_NOT_EXISTS, invalidParentId);
    }

    /**
     * 构造 parent_id 引用无效异常 - 目标 Model 不同
     * 
     * @param entityId 被验证的 Entity ID
     * @param modelId 被验证的 Model ID
     * @param invalidParentId 无效的 parent_id 值
     * @param currentModelCode 当前 Model 编码
     * @param targetModelCode 目标 Model 编码
     */
    public static InvalidParentRefException differentModel(Long entityId, Long modelId, Long invalidParentId,
                                                           String currentModelCode, String targetModelCode) {
        return new InvalidParentRefException(
                entityId, modelId, invalidParentId, currentModelCode, targetModelCode, FailureType.DIFFERENT_MODEL,
                ExtendFieldQueryErrorCodeConstants.INVALID_PARENT_REF_DIFFERENT_MODEL, 
                currentModelCode, targetModelCode);
    }

    /**
     * 私有构造函数
     */
    private InvalidParentRefException(Long entityId, Long modelId, Long invalidParentId,
                                      String currentModelCode, String targetModelCode,
                                      FailureType failureType,
                                      cn.iocoder.yudao.framework.common.exception.ErrorCode errorCode, 
                                      Object... args) {
        super(errorCode, entityId, modelId, args);
        this.invalidParentId = invalidParentId;
        this.currentModelCode = currentModelCode;
        this.targetModelCode = targetModelCode;
        this.failureType = failureType;
    }

    /**
     * 获取无效的 parent_id 值
     */
    public Long getInvalidParentId() {
        return invalidParentId;
    }

    /**
     * 获取当前 Entity 的 Model 编码
     */
    public String getCurrentModelCode() {
        return currentModelCode;
    }

    /**
     * 获取目标 Entity 的 Model 编码
     */
    public String getTargetModelCode() {
        return targetModelCode;
    }

    /**
     * 获取验证失败类型
     */
    public FailureType getFailureType() {
        return failureType;
    }

    @Override
    public String toString() {
        return String.format("InvalidParentRefException{entityId=%d, modelId=%d, invalidParentId=%d, " +
                        "currentModelCode='%s', targetModelCode='%s', failureType=%s, code=%d, message='%s'}", 
                getEntityId(), getModelId(), invalidParentId, currentModelCode, targetModelCode, 
                failureType, getCode(), getMessage());
    }
}
