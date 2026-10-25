package cn.cheers.x.module.dynamicbusiness.service.useraccount;

/**
 * 用户目录的注册编码。系统能力里的 user 仍指向旧的系统用户接口，这里不用那个编码。
 */
public final class UserAccountCodes {

    public static final String ENTITY_TYPE_CODE = "user_account";
    public static final String MODEL_NAME = "用户";
    public static final String MOBILE = "mobile";
    public static final String VISIT_TENANT_IDS = "visit_tenant_ids";
    /**
     * 所属场站。引用设施目录里的那条设施。
     * 不用 facility_id：那个编码只允许挂在站场级目录上，用户目录是全网目录。
     */
    public static final String STATION = "station_facility_id";
    public static final String STATION_TARGET = "facility";
    /** 所属部门。引用部门目录里的那条部门记录。 */
    public static final String DEPARTMENT = "department_id";
    public static final String DEPARTMENT_TARGET = "department";
    /** 创建时交给认证，不写入用户记录。 */
    public static final String PASSWORD = "login_password";

    /** 用户目录上的启用。和系统用户表的状态码不同。 */
    public static final int ENABLED = 1;
    /** 用户目录上的停用。 */
    public static final int DISABLED = 0;

    private UserAccountCodes() {
    }

    public static boolean isUserCatalog(String entityTypeCode) {
        return ENTITY_TYPE_CODE.equals(entityTypeCode);
    }
}
