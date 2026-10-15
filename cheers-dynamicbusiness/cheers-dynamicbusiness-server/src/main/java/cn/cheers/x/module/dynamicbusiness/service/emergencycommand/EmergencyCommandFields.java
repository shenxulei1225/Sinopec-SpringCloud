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
    public static final String RESPONSE_STATUS = "response_status";
    public static final String RESPONSE_RUNNING = "进行中";

    public static final String STATUS_PENDING = "待研判";
    public static final String STATUS_REJECTED = "不成立";
    public static final String STATUS_READY = "待启动";
    public static final String STATUS_RESPONDING = "响应中";

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
