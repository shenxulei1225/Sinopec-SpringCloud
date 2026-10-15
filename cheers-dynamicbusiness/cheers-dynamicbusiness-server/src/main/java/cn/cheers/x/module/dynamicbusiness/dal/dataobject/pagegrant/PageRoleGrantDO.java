package cn.cheers.x.module.dynamicbusiness.dal.dataobject.pagegrant;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色在某个页面上的一项动作。
 * 不记录数据目录。数据目录仍走目录授权。
 * 本表只给角色页保存和回显。各业务页面的接口这次不读本表。
 */
@TableName("dynamic_page_role_grant")
@KeySequence("dynamic_page_role_grant_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class PageRoleGrantDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色编号 */
    private Long roleId;

    /** 页面编码，与角色页分组里的页面一致 */
    private String pageCode;

    /** 这个页面上的一项勾选 */
    private String action;
}
