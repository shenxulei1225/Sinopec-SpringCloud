package cn.cheers.x.module.dynamicbusiness.dal.dataobject.capability;

import cn.cheers.x.framework.mybatis.core.type.JsonbStringTypeHandler;
import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
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
 * 动态业务插件清单数据对象。
 */
@TableName(value = "dynamic_business_plugin_manifest", autoResultMap = true)
@KeySequence("dynamic_business_plugin_manifest_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicBusinessPluginManifestDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("plugin_id")
    private String pluginId;

    @TableField("version")
    private String version;

    @TableField("enabled")
    private Boolean enabled;

    @TableField("platform_range")
    private String platformRange;

    @TableField("entry")
    private String entry;

    @TableField(value = "depends_on", jdbcType = JdbcType.OTHER, typeHandler = JsonbStringTypeHandler.class)
    private String dependsOn;

    @TableField(value = "permissions", jdbcType = JdbcType.OTHER, typeHandler = JsonbStringTypeHandler.class)
    private String permissions;

    @TableField(value = "semantic_contributions", jdbcType = JdbcType.OTHER, typeHandler = JsonbStringTypeHandler.class)
    private String semanticContributions;
}
