package cn.cheers.x.module.dynamicbusiness.service.department;

import cn.cheers.x.framework.common.exception.ServiceException;

/**
 * 一条部门只挂一处：场站，或公司、分公司。
 * 两处都写、两处都空，保存时拒绝。
 */
public final class DepartmentOwnerRules {

    public static final String BOTH = "所属场站和所属公司只填一处";
    public static final String NEITHER = "部门要挂在一座场站上，或挂在一家公司上";
    public static final String COMPANY_LOCKED = "所属公司创建后不可修改";
    public static final String COMPANY_INVALID = "所属公司不正确";

    private DepartmentOwnerRules() {
    }

    public static void checkExactlyOne(Long facilityId, Long companyId) {
        if (facilityId != null && companyId != null) {
            throw new ServiceException(400, BOTH);
        }
        if (facilityId == null && companyId == null) {
            throw new ServiceException(400, NEITHER);
        }
    }
}
