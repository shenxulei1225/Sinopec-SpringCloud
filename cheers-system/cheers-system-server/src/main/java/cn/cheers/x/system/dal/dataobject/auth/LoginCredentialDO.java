package cn.cheers.x.system.dal.dataobject.auth;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录凭证。只保存用户编号和口令摘要。
 * 不保存昵称、手机、所属场站、部门、角色。
 */
@TableName("system_login_credential")
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginCredentialDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户目录里这条用户记录的编号。 */
    private Long userId;

    /** 口令摘要。不存明文。 */
    private String password;
}
