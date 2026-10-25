package cn.cheers.x.module.dynamicbusiness.service.department;

/**
 * 部门目录上的所属公司。写的是运营区域里这家公司或分公司的记录编号。
 * 挂在场站上的部门不写这一列。
 */
public final class DepartmentCompanyCodes {

    public static final String ENTITY_TYPE = "department";
    public static final String FIELD_CODE = "company_id";
    public static final String DISPLAY_NAME = "所属公司";
    /** 运营区域里的公司、分公司记录。 */
    public static final String TARGET = "region";

    private DepartmentCompanyCodes() {
    }

    public static boolean isDepartment(String entityTypeCode) {
        return ENTITY_TYPE.equals(entityTypeCode == null ? "" : entityTypeCode.trim());
    }
}
