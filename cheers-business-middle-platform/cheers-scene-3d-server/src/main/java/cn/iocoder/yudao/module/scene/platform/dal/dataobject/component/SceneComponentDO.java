package cn.iocoder.yudao.module.scene.platform.dal.dataobject.component;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.JsonStringTypeHandler;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.TransformTypeHandler;
import cn.iocoder.yudao.module.scene.platform.model.Transform;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 场景级全局组件
 *
 * 对应 UE 的 USceneComponent 派生类，管理场景级别的全局组件，如：
 * - 环境雾效 / 后处理体积 (PostProcessVolume)
 * - 全局天气系统 (WeatherSystem)
 * - 场景边界 / 触发器 Volume (TriggerVolume)
 *
 * @author Sinopec
 */
@TableName(value = "scene_component", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneComponentDO extends TenantBaseDO {

    /**
     * 主键 ID
     */
    @TableId
    private Long id;

    /**
     * 组件类型
     * - POST_PROCESS_VOLUME: 后处理体积（环境雾效、色调映射、色彩校正等）
     * - WEATHER_SYSTEM: 全局天气系统（晴天、雨天、雪天等）
     * - TRIGGER_VOLUME: 场景边界/触发器（碰撞区域、触发事件等）
     */
    private String componentType;

    /**
     * 所属场景 ID
     */
    private Long sceneId;

    /**
     * 组件名称（UE 组件实例名，如 "PostProcessVolume_01"）
     */
    private String componentName;

    /**
     * 显示名称（用于前端 UI 展示）
     */
    private String displayName;

    /**
     * 相对变换（位置、旋转、缩放）
     * 对应 UE 的 RelativeLocation/RelativeRotation/RelativeScale3D
     */
    @TableField(typeHandler = TransformTypeHandler.class)
    private Transform relativeTransform;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 可见性
     */
    private Boolean visible;

    /**
     * 渲染顺序（后处理组件需要）
     */
    private Integer renderOrder;

    /**
     * 组件配置 JSON（不同组件类型存储不同的业务配置）
     */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String configJson;

    /**
     * 关联的 Actor ID 列表（触发器 Volume 关联需要触发事件的 Actor）
     */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String associatedActorIds;

    /**
     * 扩展元数据
     */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson;
}
