package cn.cheers.x.scene.platform.dal.dataobject.scene;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import cn.cheers.x.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 场景图层 DO
 * 对应 UE 场景编辑器中的 Layer
 */
@TableName(value = "scene_layer", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneLayerDO extends TenantBaseDO implements Serializable {

    @TableId
    private Long id;

    /** 场景ID */
    private Long sceneId;

    /** 图层编码 */
    private String layerCode;

    /** 图层名称 */
    private String layerName;

    /** 图层显示名称 */
    private String layerDisplayName;

    /** 图层类型: geometry / overlay / info */
    private String layerType;

    /** 图层配置（JSON）：包括样式、渲染顺序等 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String layerConfigJson;

    /** 是否可见 */
    private Boolean visibleFlag;

    /** 是否可交互 */
    private Boolean interactiveFlag;

    /** 排序号 */
    private Integer sortNo;

    /** 图层中的实例ID列表（JSON数组） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String instanceIdsJson;

    /** 图层筛选规则（JSON）：按属性、空间等条件筛选实例 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String filterRulesJson;
}
