package cn.cheers.x.module.dynamicbusiness.framework.field;

import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityBaseFieldColumnNames;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Set;

/**
 * 平台结构化字段用途清单。
 *
 * <p>负责：给结构化列发身份证。读路径只认这些用途，不按列名猜。</p>
 * <p>步骤树、流程图、协议这几列的字段类型是 JSON。用途决定哪个插件来画。不另造一种叫「结构化」的字段类型。</p>
 * <p>处理脉络仍是一行一笔的文本。用途只决定由处理脉络插件画时间轴。</p>
 * <p>不负责：挂载配置、物理列名、某业务「怎么查」落点。</p>
 */
public final class StructuredFieldSemantics {

    public static final String STEP_TREE = "STEP_TREE";
    public static final String PARAM_SCHEMA = "PARAM_SCHEMA";
    public static final String FLOW_GRAPH = "FLOW_GRAPH";
    public static final String FIELD_DESCRIPTION = "FIELD_DESCRIPTION";
    public static final String COMMAND_JSON = "COMMAND_JSON";
    public static final String SAMPLE_JSON = "SAMPLE_JSON";
    /** 处理脉络。列里是一行一笔的文本，不是 JSON。 */
    public static final String PROCESS_TRACE = "PROCESS_TRACE";
    /**
     * 条件组。多条条件，加上这些条件是全部成立还是有一条成立。
     * 值仍写在原来的两列里。用途只决定详情里合成一块来看、来改。
     */
    public static final String CONDITION_GROUP = "CONDITION_GROUP";
    /**
     * 执行的告警规则。勾了哪几条规则，加上成立之后是执行还是不执行。
     * 值仍写在原来的两列里。用途只决定详情里合成一块来看、来改。
     */
    public static final String EXECUTION_RULES = "EXECUTION_RULES";
    /** 联动事件。列是事件主题的标准多引用。详情标题用字段名称。 */
    public static final String LINKAGE_EVENT = "LINKAGE_EVENT";
    /** 触发方式。当前只允许任一事件触发。 */
    public static final String LINKAGE_MATCH = "LINKAGE_MATCH";
    /** 联动方式。一行一对的 JSON。详情标题用字段名称。 */
    public static final String LINKAGE_MODE = "LINKAGE_MODE";
    /** 执行动作。详情不单独一格，标题用在联动方式每一行的右边。 */
    public static final String LINKAGE_ACTION = "LINKAGE_ACTION";
    /** 联动来源。详情用这一列当入口。只有告警事件才画出联动事件和触发方式。值仍写原来的列。 */
    public static final String LINKAGE_SOURCE = "LINKAGE_SOURCE";
    /** 执行方式。自动执行，或人工确认后执行。可空。 */
    public static final String LINKAGE_EXECUTION = "LINKAGE_EXECUTION";
    /** 事件主题载荷契约。每条主题自己的 JSON。不是数据协议字段说明。 */
    public static final String EVENT_PAYLOAD_SCHEMA = "EVENT_PAYLOAD_SCHEMA";

    private static final Set<String> PLUGIN_SLOT_COLUMNS = Set.of(
            "step_tree_json",
            "action_tree_json",
            "child_action_ids_json",
            "param_slots_json",
            "flow_graph_json",
            "field_description_json",
            "command_json",
            "sample_json"
    );

    private StructuredFieldSemantics() {
    }

    /**
     * 这些物理列的用途必须写在字段库里；读路径不得用列名冒充用途。
     */
    public static boolean isPluginSlotColumn(String fieldCode) {
        String col = EntityBaseFieldColumnNames.toColumnName(fieldCode);
        return col != null && PLUGIN_SLOT_COLUMNS.contains(col.toLowerCase(Locale.ROOT));
    }

    public static boolean isOfficialSemantic(String semantic) {
        if (!StringUtils.hasText(semantic)) {
            return false;
        }
        String key = semantic.trim();
        return STEP_TREE.equals(key)
                || PARAM_SCHEMA.equals(key)
                || FLOW_GRAPH.equals(key)
                || FIELD_DESCRIPTION.equals(key)
                || COMMAND_JSON.equals(key)
                || SAMPLE_JSON.equals(key)
                || PROCESS_TRACE.equals(key)
                || CONDITION_GROUP.equals(key)
                || EXECUTION_RULES.equals(key)
                || LINKAGE_EVENT.equals(key)
                || LINKAGE_MATCH.equals(key)
                || LINKAGE_MODE.equals(key)
                || LINKAGE_ACTION.equals(key)
                || LINKAGE_SOURCE.equals(key)
                || LINKAGE_EXECUTION.equals(key)
                || EVENT_PAYLOAD_SCHEMA.equals(key);
    }
}
