package cn.cheers.x.module.dynamicbusiness.service.department;

import cn.cheers.x.framework.common.exception.ServiceException;

/**
 * 一条部门只挂一处：场站，或组织机构里的一个节点。
 * 两处都写、两处都空，保存时拒绝。
 */
public final class DepartmentOwnerRules {

    public static final String BOTH = "所属场站和所属组织只填一处";
    public static final String NEITHER = "部门要挂在一座场站上，或挂在组织机构的一个节点上";
    public static final String ORGANIZATION_LOCKED = "所属组织创建后不可修改";
    public static final String ORGANIZATION_INVALID = "所属组织不正确";

    private DepartmentOwnerRules() {
    }

    public static void checkExactlyOne(Long facilityId, Long organizationId) {
        if (facilityId != null && organizationId != null) {
            throw new ServiceException(400, BOTH);
        }
        if (facilityId == null && organizationId == null) {
            throw new ServiceException(400, NEITHER);
        }
    }
}
