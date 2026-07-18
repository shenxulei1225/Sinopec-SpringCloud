package cn.cheers.x.module.platform.policy.dal.dataobject;

import cn.cheers.x.framework.mybatis.core.type.JsonbStringTypeHandler;
import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@TableName(value = "platform_policy_template", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyTemplateDO extends TenantBaseDO {

    @TableId(type = IdType.INPUT)
    private String id;

    private String displayName;

    private String domain;

    private String version;

    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String defaultSpec;

    private Boolean enabled;
}
