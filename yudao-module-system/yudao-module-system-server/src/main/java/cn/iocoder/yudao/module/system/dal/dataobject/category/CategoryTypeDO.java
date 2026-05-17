package cn.iocoder.yudao.module.system.dal.dataobject.category;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
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

@TableName("system_category_type")
@KeySequence("system_category_type_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTypeDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String categoryTypeCode;

    private String name;

    private String description;

    private Integer status;

    private Long topLevelCategoryId;
}
