package cn.cheers.x.module.dynamicbusiness.service.action.execution;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.devicehistory.IndustrialEquipmentControl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 设备动作的工业写点适配器。
 *
 * <p>负责：把标准设备动作调用交给现有工业控制入口。</p>
 * <p>不负责：选择动作、选择设备、读取联动绑定或猜协议参数。</p>
 */
@Component
@RequiredArgsConstructor
public class IndustrialDeviceActionExecutor implements ActionExecutor {

    private static final String ACTION_MODEL = "action_device";
    private static final String EQUIPMENT = "equipment";

    private final IndustrialEquipmentControl industrialEquipmentControl;

    @Override
    public boolean supports(String actionModelCode, String targetEntityTypeCode) {
        return ACTION_MODEL.equals(actionModelCode) && EQUIPMENT.equals(targetEntityTypeCode);
    }

    @Override
    public void execute(EntityDO action, long targetEntityId, Map<String, Object> parameters) {
        if (action == null || action.getCode() == null || action.getCode().isBlank()) {
            throw new ServiceException(400, "设备动作缺少稳定编码");
        }
        if (parameters != null && !parameters.isEmpty()) {
            throw new ServiceException(400, "当前工业写点动作不接收调用参数");
        }
        industrialEquipmentControl.submit(
                TenantContextHolder.getRequiredTenantId(),
                action.getCode().trim(),
                targetEntityId);
    }
}
