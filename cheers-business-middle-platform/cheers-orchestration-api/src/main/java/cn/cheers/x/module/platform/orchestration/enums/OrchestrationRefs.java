package cn.cheers.x.module.platform.orchestration.enums;

/**
 * 编排模板标识。
 */
public interface OrchestrationRefs {

    String STANDARD_EXPAND_SOLVE_PERSIST_V1 = "orch.standard_expand_solve_persist_v1";

    /** 应急启动响应：validate → expand → persist（不占窗） */
    String EMERGENCY_START_RESPONSE_V1 = "orch.emergency.start_response_v1";

    /** 应急资源调度：validate → expand → solve → persist */
    String EMERGENCY_RESOURCE_DISPATCH_V1 = "orch.emergency.resource_dispatch_v1";

    String PATROL_ROUTE_PREVIEW_V1 = "orch.patrol.route_preview_v1";

    String PATROL_ROUTE_SAVE_V1 = "orch.patrol.route_save_v1";

    /** 智能编排流水线（试排 dryRun / commit persist） */
    String PATROL_ORCHESTRATION_V1 = "orch.patrol.orchestration_v1";

    String PATROL_REPLAN_V1 = "orch.patrol.replan_v1";
}
