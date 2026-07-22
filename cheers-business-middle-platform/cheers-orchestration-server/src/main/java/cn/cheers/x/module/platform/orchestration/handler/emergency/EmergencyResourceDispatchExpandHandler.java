package cn.cheers.x.module.platform.orchestration.handler.emergency;

import cn.cheers.x.module.platform.orchestration.phase.PhaseContext;
import cn.cheers.x.module.platform.orchestration.phase.PhaseHandler;
import cn.iocoder.yudao.module.emergency.api.orchestration.EmergencyResourceDispatchApi;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyResourceDispatchExpandRespDTO;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyResourceDispatchReqDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EmergencyResourceDispatchExpandHandler implements PhaseHandler {

    public static final String ID = "emergency.resource_dispatch.expand";

    @Resource
    private EmergencyResourceDispatchApi emergencyResourceDispatchApi;

    @Override
    public String handlerId() {
        return ID;
    }

    @Override
    public void execute(PhaseContext context) {
        EmergencyResourceDispatchReqDTO req = context.getAttr("dispatchReq");
        if (req == null) {
            req = EmergencyResourceDispatchValidateHandler.toReq(context);
        }
        EmergencyResourceDispatchExpandRespDTO expand =
                emergencyResourceDispatchApi.expand(req).getCheckedData();
        context.putAttr("dispatchExpandDto", expand);
        context.putAttr("expandResult", toResultMap(expand));
    }

    static Map<String, Object> toResultMap(EmergencyResourceDispatchExpandRespDTO expand) {
        Map<String, Object> result = new HashMap<>();
        if (expand == null) {
            return result;
        }
        result.put("eventId", expand.getEventId());
        result.put("resourceId", expand.getResourceId());
        result.put("responseId", expand.getResponseId());
        result.put("stage", expand.getStage());
        result.put("orchestrationRef", expand.getOrchestrationRef());
        result.put("windowStart", expand.getWindowStart());
        result.put("windowEnd", expand.getWindowEnd());
        result.put("solveSkipped", expand.getSolveSkipped());
        result.put("dispatchId", expand.getDispatchId());
        return result;
    }
}
