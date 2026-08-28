package cn.cheers.x.module.dynamicbusiness.service.sop;

/**
 * SOP 字段编码常量（= 物理列名 / baseFields key）。
 */
public final class SopFieldCodes {

    public static final String ENTITY_TYPE_CODE = "sop";

    public static final String IS_TEMPLATE = "is_template";
    public static final String SOP_TEMPLATE_ID = "sop_template_id";
    public static final String STEP_OVERRIDE_JSON = "step_override_json";
    public static final String PARAM_OVERRIDE_JSON = "param_override_json";
    public static final String DEFAULT_STEPS_JSON = "default_steps_json";
    public static final String DEFAULT_PARAMS_JSON = "default_params_json";
    public static final String EXECUTION_MEANS = "execution_means";
    public static final String PROCEDURE_KIND = "procedure_kind";
    public static final String VERSION_NO = "version_no";
    public static final String PUBLISH_STATUS = "publish_status";
    public static final String STEPS_JSON = "steps_json";

    private SopFieldCodes() {
    }
}
