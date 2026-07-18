package cn.cheers.x.module.dynamicbusiness.dal.dataobject.group;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("dynamic_group")
@KeySequence("dynamic_group_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String groupType;

    private String code;

    private String name;

    private String description;

    private Long parentId;

    /** 父分组编码（迁移用，对应同 group_type 下父节点 code） */
    private String parentCode;

    private String path;

    private Integer level;

    private Integer sort;

    private Integer status;
}
