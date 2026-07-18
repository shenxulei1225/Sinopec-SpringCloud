package cn.cheers.x.module.dynamicbusiness.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 关联目标创建事件
 * 
 * 当关联字段库中引用的目标（EntityType 或 Model）被创建时发布此事件，用于：
 * 1. 更新关联字段库中字段的状态（从"待建"变为"可用"）
 * 2. 其他需要响应关联目标创建的业务逻辑
 * 
 * 需求：FR-BDA-014
 * 
 * @author yudao
 */
@Getter
public class RelationTargetCreatedEvent extends ApplicationEvent {

    /** 目标类型 */
    private final TargetType targetType;

    /** 目标 ID */
    private final Long targetId;

    /** 目标编码 */
    private final String targetCode;

    /** 目标名称 */
    private final String targetName;

    /** 业务类型编码（如果目标是 Model，则为所属业务类型编码） */
    private final String entityTypeCode;

    /** 租户 ID */
    private final Long tenantId;

    /**
     * 目标类型枚举
     */
    public enum TargetType {
        /** 业务类型 */
        BUSINESS_TYPE,
        /** 业务模型 */
        MODEL
    }

    /**
     * 构造函数
     */
    public RelationTargetCreatedEvent(Object source, TargetType targetType, Long targetId, 
                                      String targetCode, String targetName, 
                                      String entityTypeCode, Long tenantId) {
        super(source);
        this.targetType = targetType;
        this.targetId = targetId;
        this.targetCode = targetCode;
        this.targetName = targetName;
        this.entityTypeCode = entityTypeCode;
        this.tenantId = tenantId;
    }

    /**
     * 创建 EntityType 创建事件
     */
    public static RelationTargetCreatedEvent entityTypeCreated(Object source, Long targetId, 
                                                                  String targetCode, String targetName, 
                                                                  Long tenantId) {
        return new RelationTargetCreatedEvent(source, TargetType.BUSINESS_TYPE, targetId, 
                targetCode, targetName, targetCode, tenantId);
    }

    /**
     * 创建 Model 创建事件
     */
    public static RelationTargetCreatedEvent modelCreated(Object source, Long targetId, 
                                                          String targetCode, String targetName, 
                                                          String entityTypeCode, Long tenantId) {
        return new RelationTargetCreatedEvent(source, TargetType.MODEL, targetId, 
                targetCode, targetName, entityTypeCode, tenantId);
    }

    /**
     * 判断是否为 EntityType 创建事件
     */
    public boolean isEntityTypeCreated() {
        return targetType == TargetType.BUSINESS_TYPE;
    }

    /**
     * 判断是否为 Model 创建事件
     */
    public boolean isModelCreated() {
        return targetType == TargetType.MODEL;
    }

    @Override
    public String toString() {
        return "RelationTargetCreatedEvent{" +
                "targetType=" + targetType +
                ", targetId=" + targetId +
                ", targetCode='" + targetCode + '\'' +
                ", targetName='" + targetName + '\'' +
                ", entityTypeCode='" + entityTypeCode + '\'' +
                ", tenantId=" + tenantId +
                '}';
    }
}
