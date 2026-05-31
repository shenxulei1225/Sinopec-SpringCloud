package cn.cheers.x.module.platformresource.dal.dataobject.component;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 组件 Props 模板 / 实例
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_component_props")
public class ComponentPropsDO extends BaseDO {

    @TableId
    private Long id;

    /** true=模板（props_json 全量）；false=实例（props_override 差异） */
    private Boolean isTemplate;

    /** 关联 system_component.id */
    private Long componentId;

    /** 语义化组件编码，与 system_component.key 一致 */
    private String componentCode;

    /** 数据来源能力键（与 props_json.dataSourceKey 同步） */
    @TableField("data_source_key")
    private String dataSourceKey;

    /** 实例引用的模板 propsId */
    private Long templateId;

    /** Props Schema 版本 */
    private String schemaVersion;

    /** 模板：完整 props JSON 文本 */
    private String propsJson;

    /** 实例：相对模板的差异 JSON 文本 */
    private String propsOverride;

    private String name;

    private Integer status;

    private Integer sort;

    private String description;
}
