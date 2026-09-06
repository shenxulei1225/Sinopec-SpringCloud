package cn.cheers.x.module.dynamicbusiness.framework.hierarchy;

/**
 * 高级分类（分类即实体）系统「组织上级」字段约定。
 *
 * <p>编码 = 实体上级列 parentId；内部类型 = 系统组织上级；语义 = TREE_PARENT。
 * 由系统自动挂到对应数据类型的模型上，用户手建「引用数据」不得冒充此语义。</p>
 */
public final class OrgTreeParentFieldCodes {

    /** 与 EntityDO.parentId / 写路径 baseFields.parentId 一致 */
    public static final String FIELD_CODE = "parentId";

    /** 默认展示名；类型侧可按业务改别名（如上级区域） */
    public static final String DISPLAY_NAME = "上级";

    /** 字段库 / 分配行语义：组织树上级，读写实体层级并同步分类 */
    public static final String SEMANTIC_TREE_PARENT = "TREE_PARENT";

    private OrgTreeParentFieldCodes() {
    }

    public static boolean isTreeParentSemantic(String semanticType) {
        return SEMANTIC_TREE_PARENT.equalsIgnoreCase(semanticType == null ? "" : semanticType.trim());
    }

    public static boolean isOrgTreeParentField(String fieldCode, String semanticType) {
        if (FIELD_CODE.equalsIgnoreCase(fieldCode == null ? "" : fieldCode.trim())) {
            return true;
        }
        return isTreeParentSemantic(semanticType);
    }
}
