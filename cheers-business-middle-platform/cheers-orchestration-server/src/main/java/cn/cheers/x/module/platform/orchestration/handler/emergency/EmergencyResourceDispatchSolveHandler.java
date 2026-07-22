package cn.cheers.x.module.platform.orchestration.handler.emergency;

import cn.cheers.x.module.platform.orchestration.phase.PhaseContext;
import cn.cheers.x.module.platform.orchestration.phase.PhaseHandler;
import cn.iocoder.yudao.module.emergency.api.orchestration.EmergencyResourceDispatchApi;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyResourceDispatchExpandRespDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class EmergencyResourceDispatchSolveHandler implements PhaseHandler {

    public static final String ID = "emergency.resource_dispatch.solve";

    @Resource
    private EmergencyResourceDispatchApi emergencyResourceDispatchApi;

    @Override
    public String handlerId() {
        return ID;
    }

    @Override
    public void execute(PhaseContext context) {
        EmergencyResourceDispatchExpandRespDTO expand = context.getAttr("dispatchExpandDto");
        if (expand == null) {
            return;
        }
        EmergencyResourceDispatchExpandRespDTO solved =
                emergencyResourceDispatchApi.solve(expand).getCheckedData();
        context.putAttr("dispatchExpandDto", solved);
        context.putAttr("expandResult", EmergencyResourceDispatchExpandHandler.toResultMap(solved));
    }
}
