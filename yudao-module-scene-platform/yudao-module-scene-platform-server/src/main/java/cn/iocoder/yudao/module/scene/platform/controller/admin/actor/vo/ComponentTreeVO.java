package cn.iocoder.yudao.module.scene.platform.controller.admin.actor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 组件树 VO
 *
 * 用途：前端从 Actor 模板获取 componentTree 后，可自行构建实例组件树，
 * 包含 propertiesJson（模板预设属性）和 overrideJson（运行时覆盖属性），
 * 在 spawn 时直接回传给后端持久化。
 *
 * 与 ComponentTree 模型的区别：
 * - ComponentTree 是后端模型类，使用 Java 对象结构
 * - ComponentTreeVO 是前端传输对象，使用 JSON 序列化
 * - 两者结构一致，但 VO 用于 HTTP 传输
 *
 * 属性继承机制：
 * - propertiesJson: 模板预设属性（继承自模板）
 * - overrideJson: 运行时覆盖属性（用户修改的值）
 * - 加载合并策略：先加载 propertiesJson 作为基值，再合并 overrideJson 覆盖值
 */
@Schema(description = "管理后台 - 组件树 VO")
@Data
public class ComponentTreeVO implements Serializable {

    @Schema(description = "组件树根节点")
    private ComponentTreeNodeVO root;
}
