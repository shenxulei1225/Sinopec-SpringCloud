package cn.cheers.x.module.dynamicbusiness.dal.dataobject.category;

import cn.cheers.x.module.dynamicbusiness.framework.category.core.CategoryContract;
import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 通用业务分类 DO。
 */
@TableName("dynamic_category")
@KeySequence("dynamic_category_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = false)
public class CategoryDO extends TenantBaseDO implements CategoryContract<Long> {

    /**
     * 分类ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 父分类编码（迁移用，对应 {@code dynamic_category.code}）
     */
    private String parentCode;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类编码，全局唯一
     */
    private String code;

    /**
     * 分类类型编码（必填）
     * 定义该分类所属的维度体系
     * 例如：region（区域）、equipment_type（设备类型）、organization（组织架构）
     */
    private String categoryTypeCode;

    /**
     * CategoryContract 要求的 getter
     */

    /**
     * 树路径
     */
    private String treePath;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 排序（同父级唯一）
     */
    private Integer sort;

    /**
     * 状态（1启用，0禁用）
     */
    @TableField("status")
    private Integer status;

    /**
     * 描述
     */
    private String description;

    /**
     * CategoryContract 要求的 setter（避免链式返回类型与接口不兼容）
     */
    @Override
    public void setSort(Integer sort) {
        this.sort = sort;
    }
}

