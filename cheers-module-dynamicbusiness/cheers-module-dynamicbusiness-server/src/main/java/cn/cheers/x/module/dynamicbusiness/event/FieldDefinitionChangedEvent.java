package cn.cheers.x.module.dynamicbusiness.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 字段定义变更事件
 * 
 * 当字段定义（FieldDefinition）发生变更时发布此事件，用于：
 * 1. 清除关联发现缓存
 * 2. 更新依赖该字段的计算字段
 * 3. 其他需要响应字段变更的业务逻辑
 * 
 * 需求：缓存一致性、FR-BDA-090~093
 * 
 * @author yudao
 */
@Getter
public class FieldDefinitionChangedEvent extends ApplicationEvent {

    /** 变更类型 */
    private final ChangeType changeType;

    /** 字段 ID */
    private final Long fieldId;

    /** 字段编码 */
    private final String fieldCode;

    /** 字段名称 */
    private final String fieldName;

    /** 字段类型 */
    private final String fieldType;

    /** 所属 Model ID（如果是 Model 字段分配） */
    private final Long modelId;

    /** 所属 Model 编码 */
    private final String modelCode;

    /** 租户 ID */
    private final Long tenantId;

    /**
     * 变更类型枚举
     */
    public enum ChangeType {
        /** 字段创建 */
        CREATED,
        /** 字段更新 */
        UPDATED,
        /** 字段删除 */
        DELETED,
        /** 字段分配到 Model */
        ASSIGNED,
        /** 字段从 Model 移除 */
        UNASSIGNED
    }

    /**
     * 构造函数
     */
    public FieldDefinitionChangedEvent(Object source, ChangeType changeType, Long fieldId, 
                                       String fieldCode, String fieldName, String fieldType,
                                       Long modelId, String modelCode, Long tenantId) {
        super(source);
        this.changeType = changeType;
        this.fieldId = fieldId;
        this.fieldCode = fieldCode;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.modelId = modelId;
        this.modelCode = modelCode;
        this.tenantId = tenantId;
    }

    /**
     * 创建字段创建事件
     */
    public static FieldDefinitionChangedEvent created(Object source, Long fieldId, String fieldCode, 
                                                      String fieldName, String fieldType, Long tenantId) {
        return new FieldDefinitionChangedEvent(source, ChangeType.CREATED, fieldId, 
                fieldCode, fieldName, fieldType, null, null, tenantId);
    }

    /**
     * 创建字段更新事件
     */
    public static FieldDefinitionChangedEvent updated(Object source, Long fieldId, String fieldCode, 
                                                      String fieldName, String fieldType, Long tenantId) {
        return new FieldDefinitionChangedEvent(source, ChangeType.UPDATED, fieldId, 
                fieldCode, fieldName, fieldType, null, null, tenantId);
    }

    /**
     * 创建字段删除事件
     */
    public static FieldDefinitionChangedEvent deleted(Object source, Long fieldId, String fieldCode, 
                                                      String fieldName, String fieldType, Long tenantId) {
        return new FieldDefinitionChangedEvent(source, ChangeType.DELETED, fieldId, 
                fieldCode, fieldName, fieldType, null, null, tenantId);
    }

    /**
     * 创建字段分配事件
     */
    public static FieldDefinitionChangedEvent assigned(Object source, Long fieldId, String fieldCode, 
                                                       String fieldName, String fieldType,
                                                       Long modelId, String modelCode, Long tenantId) {
        return new FieldDefinitionChangedEvent(source, ChangeType.ASSIGNED, fieldId, 
                fieldCode, fieldName, fieldType, modelId, modelCode, tenantId);
    }

    /**
     * 创建字段移除事件
     */
    public static FieldDefinitionChangedEvent unassigned(Object source, Long fieldId, String fieldCode, 
                                                         String fieldName, String fieldType,
                                                         Long modelId, String modelCode, Long tenantId) {
        return new FieldDefinitionChangedEvent(source, ChangeType.UNASSIGNED, fieldId, 
                fieldCode, fieldName, fieldType, modelId, modelCode, tenantId);
    }

    /**
     * 判断是否为关联字段变更（影响双向关联查询缓存）
     */
    public boolean isRelationFieldChange() {
        return "ENTITY_REF".equalsIgnoreCase(fieldType);
    }

    /**
     * 判断是否为 Model 级别的变更
     */
    public boolean isModelLevelChange() {
        return modelId != null;
    }

    @Override
    public String toString() {
        return "FieldDefinitionChangedEvent{" +
                "changeType=" + changeType +
                ", fieldId=" + fieldId +
                ", fieldCode='" + fieldCode + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", fieldType='" + fieldType + '\'' +
                ", modelId=" + modelId +
                ", modelCode='" + modelCode + '\'' +
                ", tenantId=" + tenantId +
                '}';
    }
}
