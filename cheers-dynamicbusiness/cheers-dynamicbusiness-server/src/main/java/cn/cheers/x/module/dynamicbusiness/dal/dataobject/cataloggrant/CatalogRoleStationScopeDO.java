package cn.cheers.x.module.dynamicbusiness.dal.dataobject.cataloggrant;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 一个角色在一个数据目录上的场站范围。
 * 查看范围管列表和详情。写入范围管创建、修改、删除。
 * 不表示这个人是全局还是场站。那由他挂的角色决定。
 */
@TableName("dynamic_catalog_role_station_scope")
@KeySequence("dynamic_catalog_role_station_scope_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class CatalogRoleStationScopeDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色编号 */
    private Long roleId;

    /** 数据目录注册编码 */
    private String registryCode;

    /** UNRESTRICTED 不限制 / ALL 全部场站 / CURRENT 仅当前站 */
    private String viewScope;

    /** UNRESTRICTED 不限制 / ALL 全部场站 / CURRENT 仅当前站 */
    private String writeScope;
}
