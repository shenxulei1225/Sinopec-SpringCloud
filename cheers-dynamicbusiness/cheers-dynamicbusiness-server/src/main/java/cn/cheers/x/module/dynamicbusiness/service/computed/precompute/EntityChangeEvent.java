package cn.cheers.x.module.dynamicbusiness.service.computed.precompute;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 实体变化事件
 * 
 * 用于触发计算字段的预计算
 * 
 * @author yudao
 */
@Getter
public class EntityChangeEvent extends ApplicationEvent {

    /** 事件类型 */
    private final EventType eventType;

    /** Model ID */
    private final Long modelId;

    /** 实体 ID */
    private final Long entityId;

    /** 变化的字段列表（仅 UPDATE 事件有效） */
    private final List<String> changedFields;

    /** 业务类型编码 */
    private final String entityTypeCode;

    /**
     * 事件类型枚举
     */
    public enum EventType {
        /** 实体创建 */
        CREATED,
        /** 实体更新 */
        UPDATED,
        /** 实体删除 */
        DELETED
    }

    /**
     * 构造函数
     */
    public EntityChangeEvent(Object source, EventType eventType, Long modelId, Long entityId, 
                             String entityTypeCode, List<String> changedFields) {
        super(source);
        this.eventType = eventType;
        this.modelId = modelId;
        this.entityId = entityId;
        this.entityTypeCode = entityTypeCode;
        this.changedFields = changedFields;
    }

    /**
     * 创建实体创建事件
     */
    public static EntityChangeEvent created(Object source, Long modelId, Long entityId, String entityTypeCode) {
        return new EntityChangeEvent(source, EventType.CREATED, modelId, entityId, entityTypeCode, null);
    }

    /**
     * 创建实体更新事件
     */
    public static EntityChangeEvent updated(Object source, Long modelId, Long entityId, 
                                            String entityTypeCode, List<String> changedFields) {
        return new EntityChangeEvent(source, EventType.UPDATED, modelId, entityId, entityTypeCode, changedFields);
    }

    /**
     * 创建实体删除事件
     */
    public static EntityChangeEvent deleted(Object source, Long modelId, Long entityId, String entityTypeCode) {
        return new EntityChangeEvent(source, EventType.DELETED, modelId, entityId, entityTypeCode, null);
    }
}
