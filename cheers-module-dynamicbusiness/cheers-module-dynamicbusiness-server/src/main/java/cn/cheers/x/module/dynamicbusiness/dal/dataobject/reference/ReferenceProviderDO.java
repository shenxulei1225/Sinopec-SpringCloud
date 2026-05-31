package cn.cheers.x.module.dynamicbusiness.dal.dataobject.reference;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 外部域 Provider 注册 DO
 */
@TableName("system_reference_provider")
@KeySequence("system_reference_provider_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceProviderDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** Provider 编码，全局唯一 */
    private String providerCode;

    /** Provider 名称 */
    private String providerName;

    /** Provider 类型：INTERNAL/HTTP/FEIGN */
    private String providerType;

    /** 语义类型：USER/DEPT/ROLE/MATERIAL... */
    private String semanticType;

    /** 能力声明（JSON） */
    private String capabilityFlags;

    /** 配置（JSON） */
    private String configJson;

    /** 状态：1启用 0禁用 */
    private Integer status;

    /** 租户作用域：GLOBAL/TENANT */
    private String tenantScope;

    /** 优先级，值越小优先级越高 */
    private Integer priority;

    /** 健康状态：UP/DOWN/UNKNOWN */
    private String healthStatus;
}
