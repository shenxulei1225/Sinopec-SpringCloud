package cn.cheers.x.module.dynamicbusiness.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 实体名称变更事件
 * 
 * <p>当实体的名称字段发生变更时发布此事件，用于：</p>
 * <ul>
 *   <li>更新所有引用该实体的 customFields 中的 `_names` 数组</li>
 *   <li>保持关联展示数据的一致性</li>
 * </ul>
 * 
 * <h3>使用场景</h3>
 * <p>当目标实体（被引用的实体）的名称发生变更时，所有引用该实体的源实体
 * 需要更新其 customFields 中存储的目标实体名称（如 `devices_names` 数组）。</p>
 * 
 * <h3>需求引用</h3>
 * <ul>
 *   <li>数据一致性：确保关联展示的名称与实际名称保持同步</li>
 * </ul>
 * 
 * @author 基础服务模块
 */
@Getter
public class EntityNameChangedEvent extends ApplicationEvent {

    /** 实体 ID */
    private final Long entityId;

    /** 旧名称 */
    private final String oldName;

    /** 新名称 */
    private final String newName;

    /** 业务类型编码 */
    private final String entityTypeCode;

    /** Model 编码 */
    private final String modelCode;

    /** 租户 ID */
    private final Long tenantId;

    /**
     * 构造函数
     * 
     * @param source 事件源
     * @param entityId 实体 ID
     * @param oldName 旧名称
     * @param newName 新名称
     * @param entityTypeCode 业务类型编码
     * @param modelCode Model 编码
     * @param tenantId 租户 ID
     */
    public EntityNameChangedEvent(Object source, Long entityId, String oldName, String newName,
                                  String entityTypeCode, String modelCode, Long tenantId) {
        super(source);
        this.entityId = entityId;
        this.oldName = oldName;
        this.newName = newName;
        this.entityTypeCode = entityTypeCode;
        this.modelCode = modelCode;
        this.tenantId = tenantId;
    }

    /**
     * 静态工厂方法：创建名称变更事件
     * 
     * @param source 事件源
     * @param entityId 实体 ID
     * @param oldName 旧名称
     * @param newName 新名称
     * @param entityTypeCode 业务类型编码
     * @param modelCode Model 编码
     * @param tenantId 租户 ID
     * @return 名称变更事件
     */
    public static EntityNameChangedEvent of(Object source, Long entityId, String oldName, String newName,
                                            String entityTypeCode, String modelCode, Long tenantId) {
        return new EntityNameChangedEvent(source, entityId, oldName, newName, 
                entityTypeCode, modelCode, tenantId);
    }

    /**
     * 判断名称是否真正发生了变更
     * 
     * @return 如果名称发生了变更返回 true
     */
    public boolean isNameActuallyChanged() {
        if (oldName == null && newName == null) {
            return false;
        }
        if (oldName == null || newName == null) {
            return true;
        }
        return !oldName.equals(newName);
    }

    @Override
    public String toString() {
        return "EntityNameChangedEvent{" +
                "entityId=" + entityId +
                ", oldName='" + oldName + '\'' +
                ", newName='" + newName + '\'' +
                ", entityTypeCode='" + entityTypeCode + '\'' +
                ", modelCode='" + modelCode + '\'' +
                ", tenantId=" + tenantId +
                '}';
    }
}
