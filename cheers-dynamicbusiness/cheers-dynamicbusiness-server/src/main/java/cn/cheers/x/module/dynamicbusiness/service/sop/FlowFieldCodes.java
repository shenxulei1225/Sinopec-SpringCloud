package cn.cheers.x.module.dynamicbusiness.service.sop;

/**
 * 流程/步骤字段编码常量（= 物理列名 / baseFields key）。
 * 步骤权威：step_tree_json；参数按节点：default_params_by_node_json / param_override_json。
 */
public final class FlowFieldCodes {

    public static final String ENTITY_TYPE_CODE = "sop";

    public static final String ACTION_TREE_OVERRIDE_JSON = "action_tree_override_json";
    public static final String PARAM_OVERRIDE_JSON = "param_override_json";
    public static final String STEP_TREE_JSON = "step_tree_json";
    /** @deprecated 迁移兼容：历史步骤字段，权威已切到 step_tree_json。 */
    public static final String ACTION_TREE_JSON = "action_tree_json";
    public static final String FLOW_GRAPH_JSON = "flow_graph_json";
    public static final String DEFAULT_PARAMS_BY_NODE_JSON = "default_params_by_node_json";
    public static final String EXECUTION_MEANS = "execution_means";
    public static final String PROCEDURE_KIND = "procedure_kind";
    public static final String VERSION_NO = "version_no";
    public static final String PUBLISH_STATUS = "publish_status";
    public static final String STANDARD_PDF_URL = "standard_pdf_url";

    private FlowFieldCodes() {
    }
}
