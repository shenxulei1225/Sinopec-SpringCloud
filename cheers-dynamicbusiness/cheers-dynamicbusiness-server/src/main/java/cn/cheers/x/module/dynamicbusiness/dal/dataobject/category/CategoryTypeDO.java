package cn.cheers.x.module.dynamicbusiness.dal.dataobject.category;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
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

    /**
     * 分类建立方式：SIMPLE / ADVANCED。
     * ADVANCED ≡ 分类即实体（创建节点仍走 isEntity + entityModelId），不是第二套管线。
     */
    private String categoryMode;

    /**
     * 实体与本种类分类树的挂靠方式：SINGLE（单归属/换挂）/ MULTI（多归属/加挂）。
     */
    private String entityAssociationMode;


}