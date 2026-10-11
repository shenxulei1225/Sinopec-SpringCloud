package cn.cheers.x.device.protocolgateway.api.mission;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 一次下发里的一步：哪条动作、动作库参数袋、动作库编号。
 * <p>参数袋只含动作库 param_slots 字段（设备检查包 + 动作库默认/生成器）。挂载、媒体等协议槽由网关按对照填，任务代码不写。
 * 动作库编号用来区分写进航线文件的动作和单独下发的报文。没有编号时仍按对照表填包。
 */
public record DispatchAction(
        Long actionId,
        Map<String, Object> params,
        String actionCode
) {
    public DispatchAction(Long actionId, Map<String, Object> params) {
        this(actionId, params, null);
    }

    public DispatchAction {
        if (actionId == null || actionId <= 0) {
            throw new IllegalArgumentException("下发步骤缺少动作 id");
        }
        params = params == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(params));
        actionCode = actionCode == null || actionCode.isBlank() ? null : actionCode.trim();
    }
}
