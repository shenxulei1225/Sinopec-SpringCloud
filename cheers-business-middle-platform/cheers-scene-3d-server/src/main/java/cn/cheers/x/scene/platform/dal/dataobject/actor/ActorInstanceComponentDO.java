package cn.cheers.x.scene.platform.dal.dataobject.actor;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import cn.cheers.x.scene.platform.dal.dataobject.JsonStringTypeHandler;
import cn.cheers.x.scene.platform.dal.dataobject.TransformTypeHandler;
import cn.cheers.x.scene.platform.model.Transform;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Actor 实例组件数据对象
 *
 * 用途：存储场景中 Actor 实例的每个组件的完整数据。
 * 每个 Actor 实例包含多个组件，每个组件对应一个 ActorInstanceComponentDO 记录。
 *
 * 与模板组件的关系：
 * - ActorInstanceComponentDO.propertiesJson 继承自 ComponentTreeNode.propertiesJson（模板预设）
 * - ActorInstanceComponentDO.constructArgsJson 继承自 ComponentTreeNode.constructArgsJson（构造参数）
 * - ActorInstanceComponentDO.overrideJson 存储运行时用户修改的覆盖值
 * 加载合并策略：先加载 propertiesJson 作为基值，再合并 overrideJson 覆盖值
 *
 * 职责：
 * 1. 存储组件的静态属性（propertiesJson、constructArgsJson）
 * 2. 存储组件的运行时覆盖属性（overrideJson）
 * 3. 存储组件的空间变换信息（relativeTransform + 各维度JSON）
 * 4. 维护组件层级关系（componentCode + parentComponentCode）
 */
@TableName(value = "actor_instance_component", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class ActorInstanceComponentDO extends TenantBaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 所属 Actor 实例的 ID
     * 关联 actor_instance 表的主键
     */
    private Long actorInstanceId;

    /**
     * 所属 Actor 的代码
     * 例如: "npc_soldier", "building_entrance"
     * 用于关联 actor_template 表，确定 Actor 的类型。
     */
    private String actorCode;

    /**
     * Actor 实例的编号
     * 与 actor_instance.instance_code 对应
     * 唯一标识场景中的一个 Actor 实例。
     */
    private String instanceCode;

    /**
     * 组件在 Actor 中的唯一代码
     * 例如: "mainLight", "collider_box", "camera_main"
     * 在同一 Actor 实例内，组件代码必须唯一。
     * 该代码来自 Actor 模板定义，创建实例时复制。
     */
    private String componentCode;

    /**
     * 组件类型名称
     * 例如: "DirectionalLight", "BoxCollider", "Camera"
     * 该值来自 Actor 模板，决定组件支持哪些属性和构造参数。
     */
    private String componentTypeName;

    /**
     * 组件启用标志
     * TRUE: 组件在场景中可见且参与物理碰撞
     * FALSE: 组件在场景中不可见且不参与物理碰撞
     */
    private Boolean enabledFlag;

    /**
     * 组件在 siblings 中的排序号
     * 排序号越小，组件在编辑器树形结构中越靠前，也在 UE 场景中越先初始化。
     */
    private Integer sortNo;

    /**
     * 组件的相对变换（相对于父组件）
     * 包含位置（position）、旋转（rotation）、缩放（scale）信息。
     * 注意：这是定义层数据，创建实例时从 Actor 模板继承。
     * 运行时位置存储在 Redis（scene:runtime:{sceneCode}:actor:{instanceCode}:position），
     *       运行时位置的修改不会回写到此定义数据。
     */
    @TableField(typeHandler = TransformTypeHandler.class)
    private Transform relativeTransform;

    /**
     * 父组件的代码
     * 如果该组件是顶级组件（无父组件），此字段为 null 或空字符串。
     * 通过此字段 + componentCode 可以构建组件树结构。
     */
    private String parentComponentCode;

    /**
     * 位置信息（JSON格式）
     * 存储组件的位置数据，例如: {"x": 100.0, "y": 0.0, "z": 50.0}
     * 注意：运行时位置数据主要存储在 Redis，此字段为持久化副本。
     */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String positionJson;

    /**
     * 旋转信息（JSON格式）
     * 存储组件的旋转数据，例如: {"x": 0.0, "y": 0.0, "z": 0.0, "w": 1.0}
     * 注意：运行时旋转数据主要存储在 Redis，此字段为持久化副本。
     */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String rotationJson;

    /**
     * 缩放信息（JSON格式）
     * 存储组件的缩放数据，例如: {"x": 1.0, "y": 1.0, "z": 1.0}
     * 注意：运行时缩放数据主要存储在 Redis，此字段为持久化副本。
     */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String scaleJson;

    /**
     * 模板预设属性（JSON格式）
     *
     * 这是组件模板的属性字典，从 Actor 模板的 ComponentTreeNode.propertiesJson 继承而来。
     * 包含组件的静态预设属性，例如：
     * - 灯光组件：{"opacity": 0.8, "color": "red", "speed": 30}
     * - 物理组件：{"mass": 10.0, "friction": 0.5}
     *
     * 运行时修改：用户不直接修改此字段。
     * 运行时修改的属性存储在 overrideJson 中。
     *
     * 加载时合并策略：先加载 propertiesJson 作为基值，再合并 overrideJson 覆盖值。
     */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String propertiesJson;

    /**
     * 运行时覆盖属性（JSON格式）
     *
     * 这是用户运行时修改的属性覆盖值。当用户在编辑器中修改组件属性时，
     * 修改值会存储在此字段中。
     *
     * 合并策略：
     * - 属性 A 只在 propertiesJson 中定义 → 使用 propertiesJson 的值
     * - 属性 A 在 propertiesJson 和 overrideJson 中都定义 → 使用 overrideJson 的值
     * - 属性 A 只在 overrideJson 中定义 → 使用 overrideJson 的值
     *
     * 业务场景：
     * - 用户在编辑器中修改灯光亮度：{"brightness": 0.9}（覆盖默认值）
     * - 用户将属性恢复为模板默认值：删除对应字段或设为 null
     *
     * 注意：此字段仅存储被覆盖的属性，不是完整属性字典。
     */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String overrideJson;

    /**
     * 构造参数（JSON格式）
     *
     * 这是组件实例化时传入的构造参数，从 Actor 模板的 ComponentTreeNode.constructArgsJson 继承而来。
     * 与 propertiesJson 的区别：
     * - constructArgsJson: 决定"组件是什么"，例如灯光类型（方向光/点光/聚光灯）
     * - propertiesJson: 决定"组件长什么样"，例如颜色、强度、阴影等
     *
     * 运行时修改：构造参数一般不在运行时修改，修改后需要重建组件。
     */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String constructArgsJson;

    /**
     * 元数据（JSON格式）
     * 存储组件的扩展信息，如 UUID、创建时间、备注等。
     * 此字段为通用扩展字段，不绑定特定业务含义。
     */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson;
}
