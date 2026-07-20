package cn.cheers.x.module.platform.orchestration.enums;

/**
 * 编排模板标识。
 */
public interface OrchestrationRefs {

    String STANDARD_EXPAND_SOLVE_PERSIST_V1 = "orch.standard_expand_solve_persist_v1";

    /** 应急启动响应：validate → expand → persist（不占窗） */
    String EMERGENCY_START_RESPONSE_V1 = "orch.emergency.start_response_v1";

    String PATROL_ROUTE_PREVIEW_V1 = "orch.patrol.route_preview_v1";

    String PATROL_ROUTE_CONFIRM_V1 = "orch.patrol.route_confirm_v1";

    String PATROL_SCHEDULE_ENABLE_V1 = "orch.patrol.schedule_enable_v1";

    String PATROL_REPLAN_V1 = "orch.patrol.replan_v1";
}
