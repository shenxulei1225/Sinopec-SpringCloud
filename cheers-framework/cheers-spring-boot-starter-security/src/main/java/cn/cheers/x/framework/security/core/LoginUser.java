package cn.cheers.x.framework.security.core;

import cn.hutool.core.map.MapUtil;
import cn.cheers.x.framework.common.enums.UserTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录用户信息
 *
 * 
 */
@Data
public class LoginUser {

    public static final String INFO_KEY_NICKNAME = "nickname";
    public static final String INFO_KEY_DEPT_ID = "deptId";
    /** 令牌上的账号范围。PLATFORM 表示系统账号，家在平台，不在某个业务租户。 */
    public static final String INFO_KEY_ACCOUNT_SCOPE = "accountScope";
    public static final String ACCOUNT_SCOPE_PLATFORM = "PLATFORM";
    /** 值为 true 时，这个登录人是超级管理员。进入其他租户时才跳过功能权限。 */
    public static final String INFO_KEY_SUPER_ADMIN = "superAdmin";

    /**
     * 用户编号
     */
    private Long id;
    /**
     * 用户类型
     *
     * 关联 {@link UserTypeEnum}
     */
    private Integer userType;
    /**
     * 额外的用户信息
     */
    private Map<String, String> info;
    /**
     * 租户编号
     */
    private Long tenantId;
    /**
     * 授权范围
     */
    private List<String> scopes;
    /**
     * 过期时间
     */
    private LocalDateTime expiresTime;

    // ========== 上下文 ==========
    /**
     * 上下文字段，不进行持久化
     *
     * 1. 用于基于 LoginUser 维度的临时缓存
     */
    @JsonIgnore
    private Map<String, Object> context;
    /**
     * 访问的租户编号
     */
    private Long visitTenantId;

    public void setContext(String key, Object value) {
        if (context == null) {
            context = new HashMap<>();
        }
        context.put(key, value);
    }

    public <T> T getContext(String key, Class<T> type) {
        return MapUtil.get(context, key, type);
    }

}
