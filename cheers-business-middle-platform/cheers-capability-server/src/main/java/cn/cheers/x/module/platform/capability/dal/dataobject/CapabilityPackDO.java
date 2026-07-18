package cn.cheers.x.module.platform.capability.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.type.JsonbStringTypeHandler;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
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

@TableName(value = "platform_capability_pack", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapabilityPackDO extends TenantBaseDO {

    @TableId(type = IdType.INPUT)
    private String id;

    private String displayName;

    private String domain;

    private String version;

    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String requiredEngines;

    private String orchestrationRef;

    private String defaultPolicyTemplateId;

    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String manifest;

    private Boolean enabled;
}
