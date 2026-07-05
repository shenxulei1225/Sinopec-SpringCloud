package cn.cheers.x.module.platformresource.dal.dataobject.view;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视图配置（模板 / 实例）
 *
 * <h3>模板 vs 实例</h3>
 * <ul>
 *   <li>模板（isTemplate=true）：存完整 config_json（slots + relations），可被多个实例复用。</li>
 *   <li>实例（isTemplate=false）：仅存 config_override（相对模板的差量），通过 template_id 引用模板；
 *       运行时 merge(template.configJson, instance.configOverride) 得到最终配置。</li>
 * </ul>
 *
 * <h3>config_json 结构（模板完整示例）</h3>
 * <pre>{@code
 * {
 *   "slots": {
 *     "tree": { "propsId": 2001, "componentCode": "tree" },
 *     "list": { "propsId": 1001, "componentCode": "list" }
 *   },
 *   "relations": [
 *     { "from": "tree.active.id", "to": "list.query.categoryId", "optional": true }
 *   ]
 * }
 * }</pre>
 *
 * <h3>config_override 结构（实例差量示例）</h3>
 * <pre>{@code
 * {
 *   "slots": {
 *     "list": { "propsId": 1005 }
 *   }
 * }
 * }</pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_view_config")
public class ViewConfigDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * true = 视图模板（config_json 完整）；false = 视图实例（config_override 差量）。
     * 与 pr_component_props.is_template 语义一致。
     */
    private Boolean isTemplate;

    /**
     * 实例指向的视图模板 id（is_template=false 时必填）。
     */
    private Long templateId;

    /**
     * 视图类型，对应前端视图组件名：
     * TreeView | TreeListView | DataManagementView | ListView
     */
    private String viewType;

    /**
     * 业务唯一标识，例如 "inspection:facility-by-category"。
     * 可为 null（匿名视图仅靠 id 引用）。
     */
    private String viewCode;

    /** 显示名称 */
    private String name;

    /** 描述 */
    private String description;

    /**
     * 视图分类节点 id（Category.categoryTypeCode = view）。
     */
    private Long categoryId;

    /**
     * 模板：完整视图配置 JSON（slots + relations），详见类注释。
     * 实例此字段为 null 或 "{}"，以 config_override 替代。
     */
    private String configJson;

    /**
     * 实例：相对模板的差量配置 JSON，仅覆盖需要修改的 slots 或 relations。
     * 模板此字段为 null。
     */
    private String configOverride;

    /** 1=启用 0=禁用 */
    private Integer status;

    /** 排序权重 */
    private Integer sort;
}
