package cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytypescope;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 划分数据（SCOPE）入口与实体关系。
 * <p>
 * 不复用 {@code dynamic_category_entity_link}（那是旧「分类即实体」）。
 */
@TableName("dynamic_entity_type_scope")
@KeySequence("dynamic_entity_type_scope_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityTypeScopeDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 划分数据入口编码（dynamic_entity_type.code，entry_kind=SCOPE） */
    private String entityTypeCode;

    /** 基础类型存储表中的实体 id */
    private Long entityId;
}
