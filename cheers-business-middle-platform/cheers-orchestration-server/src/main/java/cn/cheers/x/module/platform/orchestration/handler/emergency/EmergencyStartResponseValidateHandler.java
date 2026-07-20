package cn.cheers.x.module.platform.orchestration.handler.emergency;

import cn.cheers.x.module.platform.orchestration.phase.PhaseContext;
import cn.cheers.x.module.platform.orchestration.phase.PhaseHandler;
import cn.iocoder.yudao.module.emergency.api.orchestration.EmergencyStartResponseApi;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyStartResponseExpandRespDTO;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyStartResponseReqDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.ORCHESTRATION_PAYLOAD_INVALID;
import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;

@Component
public class EmergencyStartResponseValidateHandler implements PhaseHandler {

    public static final String ID = "emergency.start_response.validate";

    @Resource
    private EmergencyStartResponseApi emergencyStartResponseApi;

    @Override
    public String handlerId() {
        return ID;
    }

    @Override
    public void execute(PhaseContext context) {
        EmergencyStartResponseReqDTO req = toReq(context);
        emergencyStartResponseApi.validate(req).checkError();
        context.putAttr("startReq", req);
    }

    static EmergencyStartResponseReqDTO toReq(PhaseContext context) {
        Map<String, Object> payload = null;
        if (context.getOrchestrationRunRequest() != null) {
            payload = context.getOrchestrationRunRequest().getPayload();
        }
        if (payload == null) {
            throw exception(ORCHESTRATION_PAYLOAD_INVALID);
        }
        Long eventId = asLong(payload.get("eventId"));
        Long planId = asLong(payload.get("planId"));
        String responseLevel = asString(payload.get("responseLevel"));
        if (eventId == null || planId == null || !StringUtils.hasText(responseLevel)) {
            throw exception(ORCHESTRATION_PAYLOAD_INVALID);
        }
        return EmergencyStartResponseReqDTO.builder()
                .eventId(eventId)
                .planId(planId)
                .responseLevel(responseLevel.trim())
                .commandOrg(asString(payload.get("commandOrg")))
                .reason(asString(payload.get("reason")))
                .orchestrationRef(context.getOrchestrationRef())
                .build();
    }

    private static Long asLong(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Number n) {
            return n.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(v));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String asString(Object v) {
        return v == null ? null : String.valueOf(v);
    }
}
