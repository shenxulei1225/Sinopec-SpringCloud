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
 * 组件能力投影（capability_component_projection）数据对象。
 *
 * <p>职责：</p>
 * <ul>
 *   <li>每条记录表示一个业务类型在一个组件维度下的能力子集；</li>
 *   <li>组件维度由 componentCode 标识（如 list/tree/table/card）；</li>
 *   <li>子集契约以 JSONB 存在 component_interface 列，供组件按需读取。</li>
 * </ul>
 *
 * <p>索引语义：</p>
 * <ul>
 *   <li>唯一键是 (entityTypeCode, componentCode, dataKind, tenantId)；</li>
 *   <li>该表是 business_capability 的投影结果，不是运行时临时拼装缓存。</li>
 * </ul>
 */
@TableName(value = "capability_component_projection", autoResultMap = true)
@KeySequence("capability_component_projection_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapabilityComponentProjectionDO extends TenantBaseDO {

    /** 自增主键。仅数据库内部使用。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务类型编码（entityTypeCode）。
     *
     * <p>与 business_capability.entity_type_code 对齐，表示投影来源业务能力。</p>
     */
    @TableField("entity_type_code")
    private String entityTypeCode;

    /**
     * 组件维度编码（componentCode）。
     *
     * <p>投影切分维度，典型值：list/tree/table/card。</p>
     */
    @TableField("component_code")
    private String componentCode;

    /**
     * 数据种类（dataKind）：model（模型目录）/ entity（实例数据）。
     *
     * <p>与组件配置 dataSource.dataKind 对齐；system 业务能力仅使用 entity。</p>
     */
    @TableField("data_kind")
    private String dataKind;

    /**
     * 组件投影契约 JSON（component_interface）。
     *
     * <p>JSONB 存储，仅包含当前 componentCode 所需端点与字段定义。</p>
     */
    @TableField(value = "component_interface", jdbcType = JdbcType.OTHER, typeHandler = JsonbStringTypeHandler.class)
    private String componentInterface;

    /**
     * 投影版本号。
     *
     * <p>应与同批 business_capability.version 保持一致，确保读到的全集与投影同版本。</p>
     */
    @TableField("version")
    private Long version;
}
