package cn.iocoder.yudao.module.system.dal.dataobject.category;

import cn.iocoder.yudao.framework.category.core.CategoryContract;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
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
import lombok.experimental.Accessors;

@TableName("system_category")
@KeySequence("system_category_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = false)
public class CategoryDO extends TenantBaseDO implements CategoryContract<Long> {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId;

    private String name;

    private String code;

    private String categoryTypeCode;

    private String treePath;

    private Integer level;

    private Integer sort;

    @TableField("status")
    private Integer status;

    private String description;

    @Override
    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
