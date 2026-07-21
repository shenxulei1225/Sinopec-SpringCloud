package cn.cheers.x.module.platform.orchestration.handler.patrol;

import cn.cheers.x.inspection.orchestration.PatrolOrchestrationApi;
import cn.cheers.x.inspection.orchestration.dto.PatrolExpandReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolExpandRespDTO;
import cn.cheers.x.module.platform.orchestration.phase.PhaseContext;
import cn.cheers.x.module.platform.orchestration.phase.PhaseHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;

import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.SCHEDULE_RUN_WORK_ITEMS_EMPTY;

/**
 * 巡检 EXPAND 薄适配：解析种子工作项 payload，调用巡检域 Feign。
 */
@Component
public class PatrolExpandMapHandler implements PhaseHandler {

    public static final String ID = "patrol.expand_map_v1";

    @Resource
    private PatrolOrchestrationApi patrolOrchestrationApi;

    @Override
    public String handlerId() {
        return ID;
    }

    @Override
    public void execute(PhaseContext context) {
        PatrolExpandReqDTO req = PatrolExpandMapHandlerSupport.toReq(context);
        PatrolExpandRespDTO resp = patrolOrchestrationApi.expand(req).getCheckedData();
        context.setWorkItems(resp.getWorkItems());
        context.putAttr("expandResult", Map.of(
                "networkRef", resp.getNetworkRef(),
                "stopIds", resp.getStopIds(),
                "inspectionType", resp.getInspectionType()));
    }
}
