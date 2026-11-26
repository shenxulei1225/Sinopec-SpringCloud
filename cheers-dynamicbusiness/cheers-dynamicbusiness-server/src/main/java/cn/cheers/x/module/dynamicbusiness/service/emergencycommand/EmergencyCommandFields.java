package cn.cheers.x.module.dynamicbusiness.service.emergencycommand;

/**
 * 应急指挥这条链写在数据目录上的字段。
 *
 * 管什么：事件和预案各自保存哪些业务值。执行记录写在事件上。
 * 不管什么：步骤树怎么编辑。步骤仍用平台步骤管理。
 * 禁止：把这些值写进旧应急服务的事件表或预案表。
 */
public final class EmergencyCommandFields {

    public static final String EVENT_CATALOG = "emergency";
    public static final String PLAN_CATALOG = "emergentcy_plan";

    /** 事件所属管线段。线路事件填，和所属场站一起决定谁能看到这起事件。 */
    public static final String PIPELINE_SEGMENT = "pipeline_segment_id";
    /** 事件中心点。列周边资源的圆心，存在事件的专用列上。 */
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";
    public static final String LOCATION_TEXT = "location_text";

    /** 应急资源目录。资源位置在 coordinate_gis 里，资源分类挂在本目录的分类树上。 */
    public static final String RESOURCE_CATALOG = "emergency_resource";
    /** 应急队伍。周边名单可切换到这本目录，位置仍按事件中心算距离。 */
    public static final String TEAM_CATALOG = "emergency_team";
    /** 外部救援资源。周边名单可切换到这本目录。 */
    public static final String EXTERNAL_CATALOG = "external_rescue_resource";
    public static final String RESOURCE_COORDINATE = "coordinate_gis";
    public static final String RESOURCE_AVAILABLE = "available_quantity";
    public static final String RESOURCE_TOTAL = "total_quantity";

    public static final String ALERT_ID = "alert_id";
    /** 事件发生时间。模型字段码，落在事件的发生时间列。人工接报填写。 */
    public static final String OCCURRED_AT = "occurred_at";
    /** 接报时上传的照片地址。只存上传后的地址，不存文件本身。 */
    public static final String INTAKE_PHOTOS = "intake_photos";
    /** 接报时写下的事故经过。照片在 intake_photos。这是这一起事件的事故情景。 */
    public static final String INTAKE_NOTE = "intake_note";
    /** 预案上的指挥班子。启动时抄到事件的 command_org，之后不再跟预案改。 */
    public static final String COMMAND_ORG_TEMPLATE = "command_org_template";
    /** 这一起事件启动时写下的指挥机构和当时的人。 */
    public static final String COMMAND_ORG = "command_org";
    /** 这份预案通常要哪些资源种类。指挥名单据此标出周边已有的，不另建资源账。 */
    public static final String RESOURCE_KINDS = "resource_kinds";
    /** 资源放在场站还是作业区仓库。仓库必须自带坐标，不用场站坐标顶上。 */
    public static final String STORAGE_PLACE = "storage_place";
    public static final String STORAGE_WAREHOUSE = "warehouse";
    public static final String EVENT_STATUS = "event_status";
    public static final String JUDGED_REAL = "judged_real";
    public static final String EVENT_TYPE = "event_type";
    public static final String RESPONSE_LEVEL = "response_level";
    /**
     * 不分等级的预案，整份响应步骤放在这一档。
     * 键和名称都是指挥阶段「应急响应」，不是 I级、II级这种响应等级。
     */
    public static final String UNGRADED_RESPONSE = "response";
    public static final String UNGRADED_RESPONSE_LABEL = "应急响应";
    public static final String JUDGE_COMMENT = "judge_comment";
    /** 提交研判的时间。启动时间记在事件脉络里。 */
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
