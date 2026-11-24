package cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 任职：某人在某个管理单元担任某个职务，带起止日期。
 * 人的功能来自职务绑定的角色。能看哪些单元写在人员数据范围里，不读这张表。
 * 不是所属部门。所属部门只表示审批归哪里，不参与数据权限。
 */
@TableName("dynamic_post_assignment")
@KeySequence("dynamic_post_assignment_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class PostAssignmentDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 人员（登录账号）编号 */
    private Long userId;

    /** 职务目录（duty_post）里的职务编号 */
    private Long dutyPostId;

    /**
     * 管理单元所在目录。组织、场站、管线各自一本。
     * 空值在迁移里收成 organization。
     */
    private String managementUnitRegistry;

    /** 任职所在的管理单元：上面那本目录里的记录编号 */
    private Long managementUnitId;

    /** 起始日期；空表示立即 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private LocalDate validFrom;

    /** 截止日期（含当天）；空表示长期 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private LocalDate validTo;
}
