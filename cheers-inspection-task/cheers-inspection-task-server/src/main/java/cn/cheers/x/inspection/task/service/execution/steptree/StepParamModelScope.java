package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.module.dynamicbusiness.action.paramslots.ActionParamSlotsJsonCore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 生成步骤图、打开任务补耗时时，这一步跟哪一个型号。
 * <p>所有型号一致：不按型号取。按型号分开：先对执行设备型号，对不上再看挂载里唯一一个适用型号。
 * 没有或不止一个，返回空，不拿另一型号的数来填。
 * <p>不负责：读秒数、写任务。
 */
public final class StepParamModelScope {

    private StepParamModelScope() {
    }

    public static Long resolve(
            Object slotJson,
            String means,
            Long executorModelId,
            List<Long> mountedModelIds,
            ObjectMapper objectMapper
    ) {
        Object value = ActionParamSlotsJsonCore.unwrap(slotJson, objectMapper);
        if (!(value instanceof Map<?, ?> root)) {
            return null;
        }
        String meansUpper = means == null ? "" : means.trim().toUpperCase();
        if (!ActionParamSlotsJsonCore.paramsPerEquipmentModel(root, meansUpper)) {
            return null;
        }
        List<Long> allowed = ActionParamSlotsJsonCore.listedModelIds(root, meansUpper);
        if (executorModelId != null && allowed.contains(executorModelId)) {
            return executorModelId;
        }
        List<Long> matched = new ArrayList<>();
        if (mountedModelIds != null) {
            for (Long id : mountedModelIds) {
                if (id != null && id > 0 && allowed.contains(id) && !matched.contains(id)) {
                    matched.add(id);
                }
            }
        }
        if (matched.size() == 1) {
            return matched.get(0);
        }
        return null;
    }
}
