package cn.cheers.x.module.dynamicbusiness.dal.dataobject.capability;

import cn.cheers.x.module.dynamicbusiness.framework.mybatis.JsonbStringTypeHandler;
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
import org.apache.ibatis.type.JdbcType;

/**
 * 业务能力全集（business_capability）数据对象。
 *
 * <p>职责：</p>
 * <ul>
 *   <li>每条记录表示一个业务类型编码（entityTypeCode）的完整能力契约；</li>
 *   <li>契约以 JSONB 持久化在 capability_full 列；</li>
 *   <li>version 用于标记重建批次，保证前后端可按版本感知变更。</li>
 * </ul>
 *
 * <p>索引语义：</p>
 * <ul>
 *   <li>业务主键是 entityTypeCode（在库层由唯一索引约束）；</li>
 *   <li>不使用 dataSourceKey 等组件侧复合键作为能力索引。</li>
 * </ul>
 */
@TableName(value = "business_capability", autoResultMap = true)
@KeySequence("business_capability_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessCapabilityDO extends TenantBaseDO {

    /** 自增主键。仅数据库内部使用，不作为业务索引。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务类型编码（entityTypeCode）。
     *
     * <p>能力域统一索引字段。示例：equipment、system_dept。</p>
     */
    @TableField("entity_type_code")
    private String entityTypeCode;

    /**
     * 业务分类：dynamic（动态业务）/ system（系统业务）。
     *
     * <p>与组件配置 dataSource.businessCategory 对齐。</p>
     */
    @TableField("business_category")
    private String businessCategory;

    /**
     * 能力全集 JSON（capability_full）。
     *
     * <p>使用 JSONB + JsonbStringTypeHandler 持久化，避免手工序列化/反序列化分散在业务层。</p>
     */
    @TableField(value = "capability_full", jdbcType = JdbcType.OTHER, typeHandler = JsonbStringTypeHandler.class)
    private String capabilityFull;

    /**
     * 重建版本号。
     *
     * <p>同一个 entityTypeCode 每次重建递增，用于缓存失效、变更追踪与对账。</p>
     */
    @TableField("version")
    private Long version;
}
