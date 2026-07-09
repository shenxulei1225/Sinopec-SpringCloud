package cn.cheers.x.module.dynamicbusiness.dal.dataobject.group;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("dynamic_group_relation")
@KeySequence("dynamic_group_relation_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupRelationDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String groupType;

    /** 分组编码（迁移幂等键，对应 {@link GroupDO#getCode()}） */
    private String groupCode;

    private Long groupId;

    /** 目标编码（FIELD 类型时为字段池 {@code dynamic_field.code}） */
    private String targetCode;

    private Long targetId;

    private Integer sort;
}
