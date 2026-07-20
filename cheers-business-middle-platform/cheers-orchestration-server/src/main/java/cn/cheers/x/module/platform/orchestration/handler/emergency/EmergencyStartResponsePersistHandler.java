package cn.cheers.x.module.platform.orchestration.handler.emergency;

import cn.cheers.x.module.platform.orchestration.phase.PhaseContext;
import cn.cheers.x.module.platform.orchestration.phase.PhaseHandler;
import cn.iocoder.yudao.module.emergency.api.orchestration.EmergencyStartResponseApi;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyStartResponseExpandRespDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class EmergencyStartResponsePersistHandler implements PhaseHandler {

    public static final String ID = "emergency.start_response.persist";

    @Resource
    private EmergencyStartResponseApi emergencyStartResponseApi;

    @Override
    public String handlerId() {
        return ID;
    }

    @Override
    public void execute(PhaseContext context) {
        if (context.isDryRun()) {
            return;
        }
        EmergencyStartResponseExpandRespDTO expand = context.getAttr("expandResultDto");
        if (expand == null) {
            return;
        }
        emergencyStartResponseApi.persist(expand).checkError();
    }
}
