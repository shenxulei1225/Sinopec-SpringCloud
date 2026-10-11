package cn.cheers.x.module.dynamicbusiness.dal.dataobject.model;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 模型—模型多对多。
 *
 * <p>同类型和异类型都在这张表。来源类型与目标类型相同就是同类型，不同就是异类型。
 * 一行一个来源模型、一个目标模型。不写实体自定义字段，不改实体上级。</p>
 *
 * <p>运行时物理表为 {@code dynamic_model_model_relation_t{tenantId}}。</p>
 */
@TableName("dynamic_model_model_relation")
@KeySequence("dynamic_model_model_relation_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelModelRelationDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 来源模型所属存储类型 */
    private String sourceEntityTypeCode;

    /** 来源模型 */
    private Long sourceModelId;

    /** 目标模型所属存储类型 */
    private String targetEntityTypeCode;

    /** 目标模型 */
    private Long targetModelId;

    /** 同一来源下的排序 */
    private Integer sort;
}
