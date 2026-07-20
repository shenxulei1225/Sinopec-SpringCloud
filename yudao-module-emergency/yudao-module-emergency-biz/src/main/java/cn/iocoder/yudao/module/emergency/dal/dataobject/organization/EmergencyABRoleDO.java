package cn.iocoder.yudao.module.emergency.dal.dataobject.organization;

import cn.iocoder.yudao.module.emergency.dal.dataobject.EmergencyBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;

/**
 * 应急A/B角 DO
 *
 * @author 芋道源码
 */
@TableName("emergency_ab_role")
@KeySequence("emergency_ab_role_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyABRoleDO extends EmergencyBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 角色编号（唯一）
     */
    @TableField("role_code")
    private String roleCode;

    /**
     * 角色名称
     */
    @TableField("role_name")
    private String roleName;

    /**
     * A角用户ID
     */
    @TableField("a_user_id")
    private Long aUserId;

    /**
     * B角用户ID
     */
    @TableField("b_user_id")
    private Long bUserId;

    /**
     * 当前激活角色（A/B）
     */
    @TableField("current_active")
    private String currentActive;

    /**
     * 是否启用
     */
    @TableField("is_enabled")
    private Boolean isEnabled;

    /**
     * 部门ID
     */
    @TableField("department_id")
    private Long departmentId;
}
