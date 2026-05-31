package cn.cheers.x.module.dynamicbusiness.dal.dataobject.model;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 模型分类关联 DO
 *
 * 业务含义：Model（业务模型/品类）与 Category（分类）的多对多关联关系，用于归类与导航。
 * 一个 Model 可以出现在多个分类下，一个分类也可以包含多个 Model。
 * 分类只负责归类与筛选，不影响字段规则。
 *
 * @author yudao
 */
@TableName("dynamic_model_category_relation")
@KeySequence("dynamic_model_category_relation_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelCategoryRelationDO extends TenantBaseDO {

    /**
     * 关联ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模型ID
     */
    private Long modelId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 业务类型编码（与实体侧保持一致的分区维度）
     */
    private String businessTypeCode;

    /**
     * 排序值（同一分类内）
     */
    private Integer sort;
}












































