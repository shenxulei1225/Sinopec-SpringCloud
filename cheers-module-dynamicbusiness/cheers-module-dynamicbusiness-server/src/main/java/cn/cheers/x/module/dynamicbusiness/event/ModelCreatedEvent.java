package cn.cheers.x.module.dynamicbusiness.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Model 创建事件
 * 
 * 当新的 Model 被创建时发布此事件，用于：
 * 1. 展开已有的 BusinessType 关联到新 Model
 * 2. 其他需要响应 Model 创建的业务逻辑
 * 
 * 需求：FR-BDA-005, FR-BDA-023, FR-BDA-072
 * 
 * @author yudao
 */
@Getter
public class ModelCreatedEvent extends ApplicationEvent {

    /** Model ID */
    private final Long modelId;

    /** Model 编码 */
    private final String modelCode;

    /** Model 名称 */
    private final String modelName;

    /** 所属业务类型编码 */
    private final String businessTypeCode;

    /** 租户 ID */
    private final Long tenantId;

    /**
     * 构造函数
     */
    public ModelCreatedEvent(Object source, Long modelId, String modelCode, String modelName,
                             String businessTypeCode, Long tenantId) {
        super(source);
        this.modelId = modelId;
        this.modelCode = modelCode;
        this.modelName = modelName;
        this.businessTypeCode = businessTypeCode;
        this.tenantId = tenantId;
    }

    /**
     * 创建 Model 创建事件的静态工厂方法
     * 
     * @deprecated 使用新的 of 方法（不含 parentModelId 和 isTemplate 参数）
     */
    @Deprecated
    public static ModelCreatedEvent of(Object source, Long modelId, String modelCode, String modelName,
                                       String businessTypeCode, Long parentModelId, Boolean isTemplate, Long tenantId) {
        return new ModelCreatedEvent(source, modelId, modelCode, modelName, businessTypeCode, tenantId);
    }

    /**
     * 创建 Model 创建事件的静态工厂方法
     */
    public static ModelCreatedEvent of(Object source, Long modelId, String modelCode, String modelName,
                                       String businessTypeCode, Long tenantId) {
        return new ModelCreatedEvent(source, modelId, modelCode, modelName, businessTypeCode, tenantId);
    }

    @Override
    public String toString() {
        return "ModelCreatedEvent{" +
                "modelId=" + modelId +
                ", modelCode='" + modelCode + '\'' +
                ", modelName='" + modelName + '\'' +
                ", businessTypeCode='" + businessTypeCode + '\'' +
                ", tenantId=" + tenantId +
                '}';
    }
}
