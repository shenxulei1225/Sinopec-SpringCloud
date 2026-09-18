package cn.cheers.x.device.protocolgateway.api.mission;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 一次下发里的一步：哪条动作、参数袋。
 * <p>参数袋须已带对照要取的值；网关不回读路网或动作库。
 */
public record DispatchAction(
        Long actionId,
        Map<String, Object> params
) {
    public DispatchAction {
        if (actionId == null || actionId <= 0) {
            throw new IllegalArgumentException("下发步骤缺少动作 id");
        }
        params = params == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(params));
    }
}
