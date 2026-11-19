package cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 职务上的人员：这个人担任这个职务。
 * 组织架构「职务管理」分配人员时写入。
 * 不记管理单元。谁能看哪一层数据不读这张表。
 */
@TableName("dynamic_duty_post_user")
@KeySequence("dynamic_duty_post_user_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class DutyPostUserDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 职务目录（duty_post）里的职务编号 */
    private Long dutyPostId;

    /** 用户目录里的人员编号 */
    private Long userId;
}
