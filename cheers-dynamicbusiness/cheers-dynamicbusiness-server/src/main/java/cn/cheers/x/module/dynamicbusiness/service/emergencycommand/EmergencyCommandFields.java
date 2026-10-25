package cn.cheers.x.module.dynamicbusiness.service.emergencycommand;

/**
 * 应急指挥这条链写在数据目录上的字段。
 *
 * 管什么：事件、预案、响应各自保存哪些业务值。
 * 不管什么：步骤树怎么编辑。步骤仍用平台步骤管理。
 * 禁止：把这些值写进旧应急服务的事件表或预案表。
 */
public final class EmergencyCommandFields {

    public static final String EVENT_CATALOG = "emergency";
    public static final String PLAN_CATALOG = "emergentcy_plan";
    public static final String RESPONSE_CATALOG = "emergency_response";

    public static final String ALERT_ID = "alert_id";
    /** 事件发生时间。人工接报可填，告警转入可空。 */
    public static final String OCCUR_TIME = "occur_time";
    /** 选定预案之前的接报经过。 */
    public static final String INTAKE_NOTE = "intake_note";
    public static final String EVENT_STATUS = "event_status";
    public static final String JUDGED_REAL = "judged_real";
    public static final String EVENT_TYPE = "event_type";
    public static final String RESPONSE_LEVEL = "response_level";
    public static final String JUDGE_COMMENT = "judge_comment";
    /** 提交研判的时间。启动响应的时间在响应的创建时间上，两者分开记。 */
    public static final String ASSESS_TIME = "assess_time";
    /** 用户点「触发应急事件」的时间。没有这次点击时为空。 */
    public static final String TRIGGER_TIME = "trigger_time";

    public static final String PLAN_STATUS = "plan_status";
    public static final String PLAN_EFFECTIVE = "生效";

    public static final String EVENT_ID = "event_id";
    public static final String PLAN_ID = "plan_id";
    public static final String STEP_TREE = "step_tree_json";
    /** 预警阶段的步骤。不分级别，和响应步骤分开存。 */
    public static final String WARNING_STEPS = "warning_step_tree_json";
    /** 这起事件上已经发生的启动、变级、完成和上报。只追加。 */
    public static final String EVENT_TIMELINE = "event_timeline";
    /** 当前用的预案和级别。级别变化只改这里，不改脉络里的旧记录。 */
    public static final String CURRENT_PLAN_STATE = "current_plan_state";
    public static final String LEVEL_WARNING = "warning";
    public static final String RESPONSE_STATUS = "response_status";
    public static final String RESPONSE_RUNNING = "进行中";

    public static final String STATUS_PENDING = "待研判";
    public static final String STATUS_REJECTED = "不成立";
    public static final String STATUS_READY = "待启动";
    public static final String STATUS_RESPONDING = "响应中";
    /** 应急响应之后的阶段。图层状态显示「后期处置」，不再显示等级。 */
    public static final String STATUS_AFTERMATH = "后期处置";
    /** 结案状态写在事件上。处置中、已结案。不按预案。 */
    public static final String CASE_STATUS = "case_status";
    public static final String CASE_OPEN = "处置中";
    public static final String CASE_CLOSED = "已结案";
    /** 预案上的启动规则。指挥只读。 */
    public static final String START_RULES = "start_rules";

    public static final String STEP_RUN_STATUS = "runStatus";
    public static final String STEP_COMPLETED_AT = "completedAt";
    public static final String STEP_OPEN = "未完成";
    public static final String STEP_RUNNING = "执行中";
    public static final String STEP_DONE = "已完成";
    public static final String STEP_STARTED_AT = "startedAt";
    public static final String STEP_STAGE_KEY = "stageKey";

    public static final String STAGE_REPORT = "report";
    public static final String STAGE_ASSESS = "assess";
    public static final String STAGE_WARNING = "warning";
    public static final String STAGE_RESPONSE = "response";

    private EmergencyCommandFields() {
    }
}
