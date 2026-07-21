package cn.cheers.x.module.platform.orchestration.handler.patrol;

import cn.cheers.x.inspection.orchestration.PatrolOrchestrationApi;
import cn.cheers.x.inspection.orchestration.dto.PatrolConfirmReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolConfirmRespDTO;
import cn.cheers.x.module.platform.orchestration.phase.PhaseContext;
import cn.cheers.x.module.platform.orchestration.phase.PhaseHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 巡检 CONFIRM 薄适配：解析工作项与 dryRun，调用巡检域 Feign 写台账。
 */
@Component
public class PatrolConfirmHandler implements PhaseHandler {

    public static final String ID = "patrol.confirm_route_v1";

    @Resource
    private PatrolOrchestrationApi patrolOrchestrationApi;

    @Override
    public String handlerId() {
        return ID;
    }

    @Override
    public void execute(PhaseContext context) {
        PatrolConfirmReqDTO req = PatrolConfirmHandlerSupport.toReq(context);
        PatrolConfirmRespDTO resp = patrolOrchestrationApi.confirm(req).getCheckedData();
        context.putAttr("confirmResult", resp);
    }
}
