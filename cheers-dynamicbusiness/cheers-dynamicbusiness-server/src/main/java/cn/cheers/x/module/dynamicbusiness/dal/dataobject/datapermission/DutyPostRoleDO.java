package cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 职务绑定角色：担任这个职务的人自动带上这个角色。
 * 在角色页「绑定职务」里维护。判断任职的功能时读取。
 * 不直接给人分配角色。
 */
@TableName("dynamic_duty_post_role")
@KeySequence("dynamic_duty_post_role_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class DutyPostRoleDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 职务目录（duty_post）里的职务编号 */
    private Long dutyPostId;

    /** 角色编号 */
    private Long roleId;
}
