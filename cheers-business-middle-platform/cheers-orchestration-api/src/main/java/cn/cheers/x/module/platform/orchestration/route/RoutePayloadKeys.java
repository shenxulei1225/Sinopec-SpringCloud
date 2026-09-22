package cn.cheers.x.module.platform.orchestration.route;

/**
 * WorkItemDTO.payload 中路径/排期阶段约定键（跨模块共享契约）。
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
    /** 第 1 步写入：非到达类动作预估时长（分钟） */
    public static final String ESTIMATED_ACTION_DURATION = "estimatedActionDuration";
    /** 第 2 步 saveRoute 写入：预估行驶时长（分钟） */
    public static final String ESTIMATED_TRAVEL_DURATION = "estimatedTravelDuration";
    public static final String PLANNED_ROUTE = "plannedRoute";
    public static final String VISIT_NODE_IDS = "visitNodeIds";
    public static final String VISIT_POSITIONS = "visitPositions";
    public static final String SEGMENTS = "segments";

    private RoutePayloadKeys() {
    }
}
