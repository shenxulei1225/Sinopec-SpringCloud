package cn.iocoder.yudao.module.emergency.dal.dataobject.organization;

import cn.iocoder.yudao.module.emergency.dal.dataobject.EmergencyBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Map;

/**
 * 组织成员
 *
 * 按照数据模型.md中的定义实现
 */
@TableName(value = "emergency_organization_member", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyOrganizationMemberDO extends EmergencyBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联组织ID
     */
    private Long orgId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色：director/deputy_director/member/leader/deputy_leader/expert
     */
    private String role;

    /**
     * 联系方式（JSON格式，包含电话、邮箱等）
     */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Map<String, Object> contactInfo;

    /**
     * 是否启用
     */
    private Boolean isEnabled;
}
