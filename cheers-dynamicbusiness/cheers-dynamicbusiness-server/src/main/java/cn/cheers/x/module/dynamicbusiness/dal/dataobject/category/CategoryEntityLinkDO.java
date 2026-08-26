package cn.cheers.x.module.dynamicbusiness.dal.dataobject.category;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
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

/**
 * 分类即实体（Pattern C）1:1 关联行。
 *
 * <p>一行表示：某个分类树节点（categoryId）对应台账里的一条实体（entityId）。
 * 例如运营区域「新疆煤制」分类 id=1729 ↔ ent_region_t1 里 id=100027。</p>
 *
 * <p><b>不是</b>「拖入归类」用的 dynamic_entity_category_relation（一对多挂实体）。</p>
 *
 * <p><b>存哪张表</b>：代码里写基名 {@code dynamic_category_entity_link}，运行时由
 * EntityTableNameHandler 落到 {@code dynamic_category_entity_link_t{tenantId}}。
 * 手工 seed / SQL 须直接写物理表名，见 scripts/platform-import/SEED-TENANT-PHYSICAL.md。</p>
 *
 * <p><b>谁读</b>：CategoryServiceImpl.fillEntityCategoryFlags 批量查 link → 树 API 填 entityId；
 * EntityServiceImpl.getCategoryLinkedEntity 按 categoryId 查实体详情。</p>
 *
 * <p><b>谁写</b>：CategoryServiceImpl.createCategory（高级分类）→ linkCategoryEntityWithStorage；
 * 禁止读路径补 link。</p>
 */
@TableName("dynamic_category_entity_link")
@KeySequence("dynamic_category_entity_link_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryEntityLinkDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类树节点 id（dynamic_category.id） */
    private Long categoryId;

    /** 绑定实体 id（ent_*_t{tenant} 主键） */
    private Long entityId;

    /**
     * 绑定实体使用的型号 id（决定实体有哪些字段）
     */
    private Long entityModelId;

    /**
     * 实体类型码（如 region、facility），与专用表类型一致；
     * 多类型表可能出现相同 entityId 数字时靠此字段区分。
     */
    private String entityTypeCode;

    /** 业务域（可空），与实体行 domain 对齐 */
    private String domain;
}
