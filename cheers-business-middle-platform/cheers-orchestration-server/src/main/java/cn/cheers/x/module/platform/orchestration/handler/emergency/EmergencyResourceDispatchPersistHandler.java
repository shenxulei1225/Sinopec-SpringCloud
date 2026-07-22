package cn.cheers.x.module.platform.orchestration.handler.emergency;

import cn.cheers.x.module.platform.orchestration.phase.PhaseContext;
import cn.cheers.x.module.platform.orchestration.phase.PhaseHandler;
import cn.iocoder.yudao.module.emergency.api.orchestration.EmergencyResourceDispatchApi;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyResourceDispatchExpandRespDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class EmergencyResourceDispatchPersistHandler implements PhaseHandler {

    public static final String ID = "emergency.resource_dispatch.persist";

    @Resource
    private EmergencyResourceDispatchApi emergencyResourceDispatchApi;

    @Override
    public String handlerId() {
        return ID;
    }

    @Override
    public void execute(PhaseContext context) {
        if (context.isDryRun()) {
            return;
        }
        EmergencyResourceDispatchExpandRespDTO expand = context.getAttr("dispatchExpandDto");
        if (expand == null) {
            return;
        }
        EmergencyResourceDispatchExpandRespDTO persisted =
                emergencyResourceDispatchApi.persist(expand).getCheckedData();
        context.putAttr("dispatchExpandDto", persisted);
        context.putAttr("expandResult", EmergencyResourceDispatchExpandHandler.toResultMap(persisted));
    }
}
