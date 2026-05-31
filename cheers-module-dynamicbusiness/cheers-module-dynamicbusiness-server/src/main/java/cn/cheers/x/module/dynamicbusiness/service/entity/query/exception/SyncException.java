package cn.cheers.x.module.dynamicbusiness.service.entity.query.exception;

import cn.cheers.x.module.dynamicbusiness.enums.ExtendFieldQueryErrorCodeConstants;

/**
 * 数据同步异常
 *
 * <p>当 Entity 数据同步到查询索引失败时抛出此异常。</p>
 *
 * <h3>业务规则</h3>
 * <ul>
 *   <li>BR-SYN-002: 同步失败自动重试，间隔：1秒、5秒、30秒</li>
 *   <li>BR-SYN-003: 连续失败超过 10 次触发告警</li>
 *   <li>BR-SYN-005: 同步失败记录到 dynamic_entity_sync_fail_log 表</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre>
 * try {
 *     // 同步数据到索引
 *     queryEngine.syncToIndex(entity);
 * } catch (Exception e) {
 *     throw new SyncException(entityId, modelId, engineType, e.getMessage(), e);
 * }
 * </pre>
 *
 * @author 扩展字段查询服务
 */
public class SyncException extends EntityQueryException {

    /**
     * Entity ID
     */
    private final Long entityId;

    /**
     * Model ID
     */
    private final Long modelId;

    /**
     * 引擎类型
     */
    private final String engineType;

    /**
     * 重试次数
     */
    private final Integer retryCount;

    /**
     * 构造数据同步异常（简单消息）
     *
     * @param message 错误消息
     */
    public SyncException(String message) {
        super(ExtendFieldQueryErrorCodeConstants.SYNC_FAILED, message);
        this.entityId = null;
        this.modelId = null;
        this.engineType = null;
        this.retryCount = null;
    }

    /**
     * 构造数据同步异常（带原因）
     *
     * @param message 错误消息
     * @param cause 原因
     */
    public SyncException(String message, Throwable cause) {
        super(ExtendFieldQueryErrorCodeConstants.SYNC_FAILED, cause, message);
        this.entityId = null;
        this.modelId = null;
        this.engineType = null;
        this.retryCount = null;
    }

    /**
     * 构造数据同步异常（带详细信息）
     *
     * @param entityId Entity ID
     * @param modelId Model ID
     * @param engineType 引擎类型
     * @param message 错误消息
     */
    public SyncException(Long entityId, Long modelId, String engineType, String message) {
        super(ExtendFieldQueryErrorCodeConstants.SYNC_FAILED, message);
        this.entityId = entityId;
        this.modelId = modelId;
        this.engineType = engineType;
        this.retryCount = null;
    }

    /**
     * 构造数据同步异常（带详细信息和原因）
     *
     * @param entityId Entity ID
     * @param modelId Model ID
     * @param engineType 引擎类型
     * @param message 错误消息
     * @param cause 原因
     */
    public SyncException(Long entityId, Long modelId, String engineType, String message, Throwable cause) {
        super(ExtendFieldQueryErrorCodeConstants.SYNC_FAILED, cause, message);
        this.entityId = entityId;
        this.modelId = modelId;
        this.engineType = engineType;
        this.retryCount = null;
    }

    /**
     * 构造数据同步异常（带重试次数）
     *
     * @param entityId Entity ID
     * @param modelId Model ID
     * @param engineType 引擎类型
     * @param retryCount 重试次数
     * @param message 错误消息
     * @param cause 原因
     */
    public SyncException(Long entityId, Long modelId, String engineType, Integer retryCount, 
                         String message, Throwable cause) {
        super(ExtendFieldQueryErrorCodeConstants.SYNC_FAILED, cause, message);
        this.entityId = entityId;
        this.modelId = modelId;
        this.engineType = engineType;
        this.retryCount = retryCount;
    }

    /**
     * 获取 Entity ID
     */
    public Long getEntityId() {
        return entityId;
    }

    /**
     * 获取 Model ID
     */
    public Long getModelId() {
        return modelId;
    }

    /**
     * 获取引擎类型
     */
    public String getEngineType() {
        return engineType;
    }

    /**
     * 获取重试次数
     */
    public Integer getRetryCount() {
        return retryCount;
    }

    /**
     * 是否需要告警
     *
     * <p>当重试次数超过阈值时需要告警。</p>
     *
     * @param alertThreshold 告警阈值
     * @return 是否需要告警
     */
    public boolean shouldAlert(int alertThreshold) {
        return retryCount != null && retryCount >= alertThreshold;
    }

    @Override
    public String toString() {
        return String.format("SyncException{entityId=%d, modelId=%d, engineType='%s', retryCount=%d, code=%d, message='%s'}", 
                entityId, modelId, engineType, retryCount, getCode(), getMessage());
    }
}
