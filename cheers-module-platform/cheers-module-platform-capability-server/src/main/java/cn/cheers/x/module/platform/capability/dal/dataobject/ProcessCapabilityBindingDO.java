package cn.cheers.x.module.platform.capability.dal.dataobject;

import cn.cheers.x.module.platform.capability.framework.mybatis.JsonbStringTypeHandler;
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

@TableName(value = "platform_process_capability_binding", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessCapabilityBindingDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String businessTypeCode;

    private String capabilityPackId;

    private String orchestrationRef;

    private String policySetId;

    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String mappingProfileIds;

    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String engineBindings;

    private String status;

    private Integer version;

    private Long siteId;
}
