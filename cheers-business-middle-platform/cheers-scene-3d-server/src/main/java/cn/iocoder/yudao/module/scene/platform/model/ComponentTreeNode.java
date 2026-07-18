package cn.iocoder.yudao.module.scene.platform.model;



import lombok.Data;

import java.util.ArrayList;

import java.util.List;



/**

 * 组件树节点模型

 *

 * 用途：表示 Actor 模板中的单个组件节点，以及其在组件树中的层级关系。

 * 该模型是场景定义层的核心数据结构，用于存储模板预设属性、构造参数等静态数据。

 *

 * 与运行时数据的关系：

 * 当 Actor 模板被实例化为 Actor 实例时，此节点的 propertiesJson 和 constructArgsJson

 * 会被复制到 ActorInstanceComponentDO 中，作为该实例组件的初始值。

 * 运行时修改的属性会存储在 overrideJson 中，加载时合并 propertiesJson 和 overrideJson。

 *

 * 属性继承机制：

 * - propertiesJson: 从父节点继承模板预设属性，存储组件的静态预设属性（JSON格式）。

 *     例如: {"opacity": 0.8, "color": "red", "speed": 30}

 *     这些属性在 Actor 模板编辑时被定义，创建实例时自动继承。

 * - constructArgsJson: 存储组件实例化时的构造参数（JSON格式），

 *     例如: {"lightType": "directional", "castShadow": true, "range": 100}

 *     这些参数决定了组件的类型和行为特征，实例化后一般不修改。

 * - overrideJson: 运行时用户修改的属性覆盖值（存储于 ActorInstanceComponentDO），

 *     加载时与 propertiesJson 合并，覆盖值优先。

 */

@Data

public class ComponentTreeNode {



    /**

     * 组件唯一标识代码

     * 例如: "mainLight", "collider_box", "camera_main"

     * 在同一 Actor 模板内，组件代码必须唯一。

     */

    private String componentCode;



    /**

     * 组件类型名称

     * 例如: "DirectionalLight", "BoxCollider", "Camera"

     * 该值决定了组件支持哪些属性和构造参数。

     */

    private String componentTypeName;



    /**

     * 组件启用标志

     * 默认值: TRUE（启用）

     * 设置为 FALSE 时，该组件在场景中不可见且不参与物理碰撞。

     */

    private Boolean enabledFlag = Boolean.TRUE;



    /**

     * 组件在 siblings 中的排序号

     * 默认值: 0

     * 排序号越小，组件在编辑器树形结构中越靠前，也在 UE 场景中越先初始化。

     */

    private Integer sortNo = 0;



    /**

     * 组件的相对变换矩阵

     * 包含位置（position）、旋转（rotation）、缩放（scale）信息。

     * 相对于父组件的变换，形成层级变换链。

     * 注意：Actor 实例实例化时，此值会被写入运行时位置（Redis），

     *       运行时位置的修改不会回写到此定义数据。

     */

    private Transform transform;



    /**

     * 父组件的代码

     * 如果该组件是顶级组件（无父组件），此字段为 null 或空字符串。

     * 通过此字段构建组件树结构。

     */

    private String parentComponentCode;



    /**

     * 元数据（JSON格式）

     * 存储组件的扩展信息，如 UUID、创建时间、备注等。

     * 此字段为通用扩展字段，不绑定特定业务含义。

     */

    private String metadataJson;



    /**

     * 模板预设属性（JSON格式）

     *

     * 这是组件模板的属性字典，在 Actor 模板定义时由用户配置。

     * 当 Actor 模板被实例化为 Actor 实例时，此字段值会被复制到

     * 每个实例组件的 propertiesJson 字段中。

     *

     * 业务含义：决定组件的静态预设属性。

     * 例如：灯光组件的亮度、颜色；物理组件的质量、摩擦系数；

     *       动画组件的播放速度、循环模式等。

     *

     * 运行时修改：用户不直接修改此字段。运行时修改的属性存储在

     *            ActorInstanceComponentDO.overrideJson 中。

     * 加载时合并策略：先加载 propertiesJson 作为基值，再合并 overrideJson 覆盖值。

     */

    private String propertiesJson;



    /**

     * 构造参数（JSON格式）

     *

     * 这是组件实例化时传入的构造参数，用于创建组件时的初始化配置。

     * 与 propertiesJson 的区别：

     * - constructArgsJson: 决定"组件是什么"，例如灯光类型（方向光/点光/聚光灯）

     * - propertiesJson: 决定"组件长什么样"，例如颜色、强度、阴影等

     *

     * 业务含义：在 Actor 模板编辑中，用户可能需要选择组件的具体配置类型。

     * 例如：相机组件可选择透视相机或正交相机，并设置视场角（FOV）。

     *

     * 运行时修改：构造参数一般不在运行时修改，修改后需要重建组件。

     */

    private String constructArgsJson;



    /**

     * 子组件节点列表

     *

     * 组件之间可以形成父子层级关系，例如：

     * - 场景根组件（相机）

     *   - 子组件：灯光

     *   - 子组件：地形

     *     - 子组件：装饰物

     *

     * 该列表是递归结构，子组件的 transform 相对于父组件的 transform。

     * 遍历此树形结构可以获取组件的完整层级关系。

     */

    private List<ComponentTreeNode> children = new ArrayList<>();

}

