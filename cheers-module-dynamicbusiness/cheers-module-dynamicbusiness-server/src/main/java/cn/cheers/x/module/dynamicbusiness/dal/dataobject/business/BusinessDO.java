package cn.cheers.x.module.dynamicbusiness.dal.dataobject.business;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 门户业务 DO（与实体类型 EntityType 分层）。
 */
@TableName("dynamic_business")
@KeySequence("dynamic_business_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 业务编码 businessCode */
    private String code;

    private String name;

    /** 父级业务 id；需 ALWAYS 以便清空为根业务时能写入 NULL */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long parentId;

    /** GROUP / LEAF */
    private String nodeKind;

    private String description;

    private String icon;

    private String alias;

    private Integer sort;

    private String status;

    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";
}
