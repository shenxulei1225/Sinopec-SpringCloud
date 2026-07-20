package cn.iocoder.yudao.module.emergency.dal.dataobject;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应急管理基础实体对象
 *
 * 继承多租户基础实体对象，复用 TenantBaseDO 的时间戳和租户处理
 *
 * @author 应急管理系统
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class EmergencyBaseDO extends TenantBaseDO {
    // 继承 TenantBaseDO 的 createTime、updateTime 和 tenantId 字段
    // 如果需要 PostgreSQL 特定的时间戳处理，可以在配置层面统一处理
}
