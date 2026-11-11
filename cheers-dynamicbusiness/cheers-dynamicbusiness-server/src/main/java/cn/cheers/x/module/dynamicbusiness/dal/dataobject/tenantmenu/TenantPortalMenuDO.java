package cn.cheers.x.module.dynamicbusiness.dal.dataobject.tenantmenu;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 本租户顶栏上的一项。
 * 一级的 parentId 为空。二级的 parentId 指向一级。
 * pageCode 指向已开通的业务页面。一级分组可以不挂页面。
 * 不存角色动作，不表示这个人能不能进页面。
 */
@TableName("tenant_portal_menu")
@KeySequence("tenant_portal_menu_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantPortalMenuDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 上一级。空表示这一项自己就是一级。 */
    private Long parentId;

    /** 顶栏上显示的名字。可以和页面登记名不同。 */
    private String title;

    /** 打开的业务页面。一级分组可以空着。 */
    private String pageCode;

    /** 同一级里的先后。 */
    private Integer sort;
}
