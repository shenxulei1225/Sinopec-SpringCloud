package cn.cheers.x.module.dynamicbusiness.dal.dataobject.category;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 分类类型 DO
 * 定义分类的维度体系，如 region（区域）、equipment_type（设备类型）等
 */
@TableName("dynamic_category_type")
@KeySequence("dynamic_category_type_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = false)
public class CategoryTypeDO extends TenantBaseDO {

    /**
     * 分类类型ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类类型编码，全局唯一
     * 例如：region、equipment_type、organization、project_phase
     */
    private String categoryTypeCode;

    /**
     * 分类类型名称
     */
    private String name;

    /**
     * 分类类型描述
     */
    private String description;

    /**
     * 状态（1启用，0禁用）
     */
    private Integer status;

    /**
     * 顶层分类ID（该分类类型的根节点）
     */
    private Long topLevelCategoryId;


}