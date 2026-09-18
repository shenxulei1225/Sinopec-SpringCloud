package cn.cheers.x.module.dynamicbusiness.framework.field;

import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityBaseFieldColumnNames;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Set;

/**
 * 平台结构化字段用途清单。
 *
 * <p>负责：给结构化列发身份证。读路径只认这些用途，不按列名猜。</p>
 * <p>不负责：挂载配置、物理列名、某业务「怎么查」落点。</p>
 */
public final class StructuredFieldSemantics {

    public static final String STEP_TREE = "STEP_TREE";
    public static final String PARAM_SCHEMA = "PARAM_SCHEMA";
    public static final String FLOW_GRAPH = "FLOW_GRAPH";
    public static final String FIELD_DESCRIPTION = "FIELD_DESCRIPTION";
    public static final String COMMAND_JSON = "COMMAND_JSON";
    public static final String SAMPLE_JSON = "SAMPLE_JSON";

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
                || SAMPLE_JSON.equals(key);
    }
}
