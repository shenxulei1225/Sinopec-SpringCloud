package cn.cheers.x.module.dynamicbusiness.service.entity.sync;

/**
 * Entity 同步异常
 * 
 * <p>当 Entity 数据同步到查询索引失败时抛出此异常。</p>
 * 
 * @author 扩展字段查询服务
 */
public class EntitySyncException extends RuntimeException {

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

    public EntitySyncException(String message) {
        super(message);
        this.entityId = null;
        this.modelId = null;
        this.engineType = null;
    }

    public EntitySyncException(String message, Throwable cause) {
        super(message, cause);
        this.entityId = null;
        this.modelId = null;
        this.engineType = null;
    }

    public EntitySyncException(Long entityId, Long modelId, String engineType, String message) {
        super(message);
        this.entityId = entityId;
        this.modelId = modelId;
        this.engineType = engineType;
    }

    public EntitySyncException(Long entityId, Long modelId, String engineType, String message, Throwable cause) {
        super(message, cause);
        this.entityId = entityId;
        this.modelId = modelId;
        this.engineType = engineType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public Long getModelId() {
        return modelId;
    }

    public String getEngineType() {
        return engineType;
    }

    @Override
    public String toString() {
        return String.format("EntitySyncException{entityId=%d, modelId=%d, engineType='%s', message='%s'}",
                entityId, modelId, engineType, getMessage());
    }
}
