package cn.cheers.x.module.platform.orchestration.handler.patrol;

import cn.cheers.x.inspection.orchestration.PatrolOrchestrationApi;
import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapRespDTO;
import cn.cheers.x.module.platform.orchestration.enums.OrchestrationRefs;
import cn.cheers.x.module.platform.orchestration.phase.PhaseContext;
import cn.cheers.x.module.platform.orchestration.phase.PhaseHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.SCHEDULE_RUN_WORK_ITEMS_EMPTY;

/**
 * 巡检 EXPAND 薄适配：解析种子工作项 payload，调用巡检域 Feign。
 */
@Component
public class PatrolScheduleMapHandler implements PhaseHandler {

    public static final String ID = "patrol.expand_map_v1";

    @Resource
    private PatrolOrchestrationApi patrolOrchestrationApi;

    @Override
    public String handlerId() {
        return ID;
    }

    @Override
    public void execute(PhaseContext context) {
        PatrolScheduleMapReqDTO req = PatrolScheduleMapHandlerSupport.toReq(context);
        req.setExpandWorkItemsFromScheduleTemplate(
                OrchestrationRefs.PATROL_ORCHESTRATION_V1.equals(context.getOrchestrationRef()));
        PatrolScheduleMapRespDTO resp = patrolOrchestrationApi.expandPatrolWorkItems(req).getCheckedData();
        context.setWorkItems(resp.getWorkItems());
        Map<String, Object> expandResult = new LinkedHashMap<>();
        if (resp.getNetworkRef() != null) {
            expandResult.put("networkRef", resp.getNetworkRef());
        }
        if (resp.getStopIds() != null) {
            expandResult.put("stopIds", resp.getStopIds());
        }
        if (resp.getInspectionType() != null) {
            expandResult.put("inspectionType", resp.getInspectionType());
        }
        context.putAttr("expandResult", expandResult);
    }
}
