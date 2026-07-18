package cn.cheers.x.module.dynamicbusiness.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 实体数据变化事件
 * 
 * 当实体数据发生变化（创建、更新、删除）时发布此事件，
 * 用于触发预计算字段的重算。
 * 
 * @author yudao
 */
@Getter
public class EntityDataChangedEvent extends ApplicationEvent {

    /** Model ID */
    private final Long modelId;
    
    /** 实体 ID */
    private final Long entityId;
    
    /** 变化类型 */
    private final ChangeType changeType;
    
    /** 变化的字段编码列表（可选） */
    private final List<String> changedFieldCodes;

    public EntityDataChangedEvent(Object source, Long modelId, Long entityId, ChangeType changeType) {
        this(source, modelId, entityId, changeType, null);
    }

    public EntityDataChangedEvent(Object source, Long modelId, Long entityId, 
            ChangeType changeType, List<String> changedFieldCodes) {
        super(source);
        this.modelId = modelId;
        this.entityId = entityId;
        this.changeType = changeType;
        this.changedFieldCodes = changedFieldCodes;
    }

    /**
     * 变化类型
     */
    public enum ChangeType {
        /** 创建 */
        CREATED,
        /** 更新 */
        UPDATED,
        /** 删除 */
        DELETED
    }
}
