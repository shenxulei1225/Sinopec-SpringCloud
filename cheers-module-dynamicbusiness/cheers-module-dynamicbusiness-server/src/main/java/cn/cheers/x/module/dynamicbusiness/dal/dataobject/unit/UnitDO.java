package cn.cheers.x.module.dynamicbusiness.dal.dataobject.unit;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("dynamic_unit")
@KeySequence("dynamic_unit_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String code;

    private String unitType;

    private Integer sort;

    private Integer status;

    private String remark;
}
