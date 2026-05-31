package cn.cheers.x.module.dynamicbusiness.dal.dataobject.capability;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 实例能力注册表 DO
 */
@TableName("dynamic_instance_capability_registry")
@KeySequence("dynamic_instance_capability_registry_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstanceCapabilityRegistryDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 稳定引用键，如 dynamic-model:equipment */
    private String instanceKey;

    /** dynamic-entity / dynamic-model / system */
    private String domain;

    private String businessTypeCode;

    private Long modelId;

    private String resourceCode;

    private String label;

    /** 完整契约 JSON，与前端 InstanceCapabilityContract 对齐 */
    private String contractJson;

    private Integer version;

    /** 1-启用 0-禁用 */
    private Integer status;
}
