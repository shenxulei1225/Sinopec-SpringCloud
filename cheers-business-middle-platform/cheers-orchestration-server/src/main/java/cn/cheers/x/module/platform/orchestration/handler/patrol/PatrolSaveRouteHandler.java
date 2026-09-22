package cn.cheers.x.module.platform.orchestration.handler.patrol;

import cn.cheers.x.inspection.orchestration.PatrolOrchestrationApi;
import cn.cheers.x.inspection.orchestration.dto.PatrolSaveRouteReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolSaveRouteRespDTO;
import cn.cheers.x.module.platform.orchestration.phase.PhaseContext;
import cn.cheers.x.module.platform.orchestration.phase.PhaseHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 保存路线薄适配：解析工作项与 dryRun，调用巡检域 Feign 写 plannedRoute。
 */
@Component
public class PatrolSaveRouteHandler implements PhaseHandler {

    public static final String ID = "patrol.save_route_v1";

    @Resource
    private PatrolOrchestrationApi patrolOrchestrationApi;

    @Override
    public String handlerId() {
        return ID;
    }

    @Override
    public void execute(PhaseContext context) {
        PatrolSaveRouteReqDTO req = PatrolSaveRouteHandlerSupport.toReq(context);
        PatrolSaveRouteRespDTO resp = patrolOrchestrationApi.saveRoute(req).getCheckedData();
        context.putAttr("saveRouteResult", resp);
    }
}
