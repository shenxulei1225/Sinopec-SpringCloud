package cn.cheers.x.module.dynamicbusiness.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * EntityType 关联创建事件
 * 
 * 当 EntityType 级别的关联被创建时发布此事件，用于：
 * 1. 自动展开关联到所有子 Model
 * 2. 自动创建关联字段
 * 3. 其他需要响应关联创建的业务逻辑
 * 
 * 需求：FR-BDA-070~075
 * 
 * @author yudao
 */
@Getter
public class EntityTypeRelationCreatedEvent extends ApplicationEvent {

    /** 关联 ID */
    private final Long relationId;

    /** 源业务类型编码 */
    private final String sourceEntityTypeCode;

    /** 目标业务类型编码 */
    private final String targetEntityTypeCode;

    /** 租户 ID */
    private final Long tenantId;

    /**
     * 构造函数
     */
    public EntityTypeRelationCreatedEvent(Object source, Long relationId, 
                                            String sourceEntityTypeCode, String targetEntityTypeCode,
                                            Long tenantId) {
        super(source);
        this.relationId = relationId;
        this.sourceEntityTypeCode = sourceEntityTypeCode;
        this.targetEntityTypeCode = targetEntityTypeCode;
        this.tenantId = tenantId;
    }

    /**
     * 创建事件的静态工厂方法
     */
    public static EntityTypeRelationCreatedEvent of(Object source, Long relationId,
                                                      String sourceEntityTypeCode, String targetEntityTypeCode,
                                                      Long tenantId) {
        return new EntityTypeRelationCreatedEvent(source, relationId, 
                sourceEntityTypeCode, targetEntityTypeCode, tenantId);
    }

    @Override
    public String toString() {
        return "业务类型关联创建事件{" +
                "关联ID=" + relationId +
                ", 源业务类型编码='" + sourceEntityTypeCode + '\'' +
                ", 目标业务类型编码='" + targetEntityTypeCode + '\'' +
                ", 租户ID=" + tenantId +
                '}';
    }
}
