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
    public static final String CHILD_ACTION_IDS_JSON = "child_action_ids_json";
    public static final String IS_COMPOSITE = "is_composite";

    private ActionFieldCodes() {
    }
}
