package cn.cheers.x.module.dynamicbusiness.service.action;

/**
 * 动作库字段编码常量（= 物理列名 / baseFields key）。
 *
 * <p>与 Flyway V76、{@code scripts/platform-import/action/} 对齐。</p>
 */
public final class ActionFieldCodes {

    public static final String ENTITY_TYPE_CODE = "action";

    public static final String EXECUTION_MEANS = "execution_means";
    public static final String PARAM_SLOTS_JSON = "param_slots_json";
    public static final String ACTION_TREE_JSON = "action_tree_json";
    public static final String ACTION_DOMAIN = "action_domain";
    public static final String ANALYSIS_CAPABILITY_ID = "analysis_capability_id";

    private ActionFieldCodes() {
    }
}
