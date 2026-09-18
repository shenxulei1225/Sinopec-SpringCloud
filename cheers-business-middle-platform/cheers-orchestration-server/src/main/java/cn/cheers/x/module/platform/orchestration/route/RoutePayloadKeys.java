package cn.cheers.x.module.platform.orchestration.route;

/**
 * WorkItemDTO.payload 中路径阶段约定键（波次 1 写死）。
 */
public final class RoutePayloadKeys {

    public static final String NETWORK_REF = "networkRef";
    public static final String STOP_IDS = "stopIds";
    public static final String INSPECTION_TYPE = "inspectionType";
    public static final String STRATEGY = "strategy";
    public static final String START_STOP_ID = "startStopId";
    public static final String END_STOP_ID = "endStopId";
    public static final String RETURN_TO_START = "returnToStart";
    public static final String SPEED_METERS_PER_MINUTE = "speedMetersPerMinute";
    public static final String WORK_MINUTES = "workMinutes";
    public static final String PLANNED_ROUTE = "plannedRoute";
    /** 实际走过的点位序（含途径点，可重复） */
    public static final String VISIT_NODE_IDS = "visitNodeIds";

    private RoutePayloadKeys() {
    }
}
