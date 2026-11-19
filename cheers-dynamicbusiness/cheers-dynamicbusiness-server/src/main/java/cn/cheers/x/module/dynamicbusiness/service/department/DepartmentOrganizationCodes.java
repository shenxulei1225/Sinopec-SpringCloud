package cn.cheers.x.module.dynamicbusiness.service.department;

/**
 * 部门目录上的「所属组织」。写的是组织机构目录里一个节点（集团、省公司、分公司、作业区）的记录编号。
 * 挂在场站上的部门不写这一列，只写所属场站。
 * 不负责：运营区域（region）。运营区域是管理区域，不是公司组织，部门不挂在它上面。
 */
public final class DepartmentOrganizationCodes {

    public static final String ENTITY_TYPE = "department";
    public static final String FIELD_CODE = "organization_id";
    public static final String DISPLAY_NAME = "所属组织";
    /** 组织机构目录。 */
    public static final String TARGET = "organization";

    private DepartmentOrganizationCodes() {
    }

    public static boolean isDepartment(String entityTypeCode) {
        return ENTITY_TYPE.equals(entityTypeCode == null ? "" : entityTypeCode.trim());
    }
}
