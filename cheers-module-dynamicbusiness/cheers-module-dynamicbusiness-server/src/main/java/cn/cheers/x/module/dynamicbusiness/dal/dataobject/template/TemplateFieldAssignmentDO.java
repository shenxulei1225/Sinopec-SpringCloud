package cn.cheers.x.module.dynamicbusiness.dal.dataobject.template;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 模板字段分配 DO
 * 
 * 业务含义：将 Template（字段模板）与 Field（字段）进行多对多关联，
 * 用于定义某个 Template 包含哪些字段。
 * 
 * 当用户基于 Template 创建 Model 时，这些字段会被复制到新 Model 的
 * ModelFieldAssignment 中，复制后 Model 与 Template 完全独立。
 * 
 * @author yudao
 */
@TableName("dynamic_template_field_assignment")
@KeySequence("dynamic_template_field_assignment_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateFieldAssignmentDO extends TenantBaseDO {

    /**
     * 分配ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 模板编码（迁移幂等键，对应 {@link TemplateDO#getCode()}）
     */
    private String templateCode;

    /**
     * 字段ID
     */
    private Long fieldId;

    /**
     * 字段编码（迁移幂等键，对应字段池 {@code dynamic_field.code}）
     */
    private String fieldCode;

    /**
     * 排序值
     * 
     * 用于控制字段在模板中的显示顺序
     */
    private Integer sortOrder;

    /**
     * 是否必填
     * 
     * 当基于此模板创建 Model 时，此设置会被复制到 ModelFieldAssignment
     */
    private Boolean required;

    /**
     * 默认值
     * 
     * 当基于此模板创建 Model 时，此设置会被复制到 ModelFieldAssignment
     */
    private String defaultValue;
}
