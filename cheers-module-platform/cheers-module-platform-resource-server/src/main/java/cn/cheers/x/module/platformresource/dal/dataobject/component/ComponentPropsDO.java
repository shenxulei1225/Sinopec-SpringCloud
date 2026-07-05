package cn.cheers.x.module.platformresource.dal.dataobject.component;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 组件配置：模板 / 实例。props 仅存用户 UI 偏好，不含接口契约。
 * 数据来源见持久化字段 data_source（JSON 结构体）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_component_props")
public class ComponentPropsDO extends BaseDO {

    @TableId
    private Long id;

    /** true=模板（props 全量）；false=实例（props_override 差异） */
    private Boolean isTemplate;

    /** 关联 pr_component.id */
    private Long componentId;

    /** 语义化组件编码，与 pr_component.component_code 一致 */
    private String componentCode;

    /**
     * 数据来源 JSON：{ businessCategory, businessTypeCode, dataKind }。
     * system 分类保存时 dataKind 强制为 entity。
     */
    private String dataSource;

    /** 实例引用的模板 propsId */
    private Long templateId;

    /** Props Schema 版本 */
    private String schemaVersion;

    /** 模板：完整用户偏好 props JSON */
    @TableField("props")
    private String props;

    /** 实例：相对模板的差异 JSON */
    private String propsOverride;

    private String name;

    private Integer status;

    private Integer sort;

    private String description;
}
