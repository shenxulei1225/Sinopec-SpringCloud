package cn.cheers.x.module.platform.orchestration.handler.emergency;

import cn.cheers.x.module.platform.orchestration.phase.PhaseContext;
import cn.cheers.x.module.platform.orchestration.phase.PhaseHandler;
import cn.iocoder.yudao.module.emergency.api.orchestration.EmergencyResourceDispatchApi;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyResourceDispatchReqDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.ORCHESTRATION_PAYLOAD_INVALID;

@Component
public class EmergencyResourceDispatchValidateHandler implements PhaseHandler {

    public static final String ID = "emergency.resource_dispatch.validate";

    @Resource
    private EmergencyResourceDispatchApi emergencyResourceDispatchApi;

    @Override
    public String handlerId() {
        return ID;
    }

    @Override
    public void execute(PhaseContext context) {
        EmergencyResourceDispatchReqDTO req = toReq(context);
        emergencyResourceDispatchApi.validate(req).checkError();
        context.putAttr("dispatchReq", req);
    }

    static EmergencyResourceDispatchReqDTO toReq(PhaseContext context) {
        Map<String, Object> payload = null;
        if (context.getOrchestrationRunRequest() != null) {
            payload = context.getOrchestrationRunRequest().getPayload();
        }
        if (payload == null) {
            throw exception(ORCHESTRATION_PAYLOAD_INVALID);
        }
        Long eventId = asLong(payload.get("eventId"));
        Long resourceId = asLong(payload.get("resourceId"));
        if (eventId == null || resourceId == null) {
            throw exception(ORCHESTRATION_PAYLOAD_INVALID);
        }
        return EmergencyResourceDispatchReqDTO.builder()
                .eventId(eventId)
                .resourceId(resourceId)
                .responseId(asLong(payload.get("responseId")))
                .stage(asString(payload.get("stage")))
                .windowStart(asString(payload.get("windowStart")))
                .windowEnd(asString(payload.get("windowEnd")))
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
        String s = String.valueOf(v).trim();
        if (!StringUtils.hasText(s) || "null".equalsIgnoreCase(s)) {
            return null;
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String asString(Object v) {
        if (v == null) {
            return null;
        }
        String s = String.valueOf(v).trim();
        return StringUtils.hasText(s) && !"null".equalsIgnoreCase(s) ? s : null;
    }
}
