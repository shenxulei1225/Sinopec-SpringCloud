package cn.cheers.x.module.dynamicbusiness.dataprocess;

/**
 * 通用策略记录上的列名。
 * 判定模块不认这些中文名。动态业务按这些列把已发布的记录交给判定模块。
 * 不占用告警策略已经在用的「条件」「告警规则」。
 */
public final class StrategyRecordFields {

    public static final String DIRECTORY = AlarmStrategyCatalogEnsure.DIRECTORY;
    public static final String MODEL = "通用策略";

    public static final String KIND = "记录种类";
    public static final String KIND_DOMAIN = "DOMAIN";
    public static final String KIND_STRATEGY = "STRATEGY";
    public static final String KIND_CONDITION = "CONDITION";

    public static final String DOMAIN_ALARM = "alarm";
    public static final String ACTION_SKIP = "不执行";
    public static final String ACTION_RUN = "执行";

    public static final String CONDITION_IDS = "策略条件";
    public static final String MATCH = "条件算法";
    public static final String ACTION_IDS = "策略动作";
    public static final String ACTION_MODE = "动作做法";
    public static final String DOMAIN_CODE = "所属域";

    public static final String CONDITION_CATALOG = "条件目录";
    public static final String ACTION_CATALOG = "动作目录";
    public static final String COMBINE = "合并办法";
    public static final String ALLOWED_MODES = "允许做法";

    public static final String MATCH_OPTIONS = """
            [{"label":"有一条成立","value":"ANY"},{"label":"全部成立","value":"ALL"}]""";
    public static final String COMBINE_OPTIONS = """
            [{"label":"全部返回","value":"ALL"},{"label":"只取第一条","value":"FIRST"}]""";
    public static final String KIND_OPTIONS = """
            [{"label":"域","value":"DOMAIN"},{"label":"策略","value":"STRATEGY"},{"label":"条件","value":"CONDITION"}]""";

    private StrategyRecordFields() {
    }
}
