package cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 一个人的数据范围：他可以访问哪些管理单元。
 * 一条记录是一本目录里的一个单元，例如某座场站、某条管线、某个组织节点。
 * 查看和维护由角色决定，不写在这张表上。
 * 不负责：这个人担任什么职务。
 */
@TableName("dynamic_user_data_scope")
@KeySequence("dynamic_user_data_scope_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDataScopeDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户目录里的人员编号 */
    private Long userId;

    /** 管理单元所在目录：organization、facility、pipeline，或租户另外登记的目录 */
    private String registryCode;

    /** 该目录里的记录编号 */
    private Long unitId;
}
