package cn.cheers.x.module.platform.orchestration.handler.emergency;

import cn.cheers.x.module.platform.orchestration.phase.PhaseContext;
import cn.cheers.x.module.platform.orchestration.phase.PhaseHandler;
import cn.iocoder.yudao.module.emergency.api.orchestration.EmergencyStartResponseApi;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyStartResponseExpandRespDTO;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyStartResponseReqDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EmergencyStartResponseExpandHandler implements PhaseHandler {

    public static final String ID = "emergency.start_response.expand";

    @Resource
    private EmergencyStartResponseApi emergencyStartResponseApi;

    @Override
    public String handlerId() {
        return ID;
    }

    @Override
    public void execute(PhaseContext context) {
        EmergencyStartResponseReqDTO req = context.getAttr("startReq");
        if (req == null) {
            req = EmergencyStartResponseValidateHandler.toReq(context);
        }
        EmergencyStartResponseExpandRespDTO expand =
                emergencyStartResponseApi.expand(req).getCheckedData();
        context.putAttr("expandResultDto", expand);
        Map<String, Object> result = new HashMap<>();
        if (expand != null) {
            result.put("eventId", expand.getEventId());
            result.put("responseId", expand.getResponseId());
            result.put("responseNo", expand.getResponseNo());
            result.put("responseLevel", expand.getResponseLevel());
            result.put("planId", expand.getPlanId());
            result.put("orchestrationRef", expand.getOrchestrationRef());
        }
        context.putAttr("expandResult", result);
    }
}
