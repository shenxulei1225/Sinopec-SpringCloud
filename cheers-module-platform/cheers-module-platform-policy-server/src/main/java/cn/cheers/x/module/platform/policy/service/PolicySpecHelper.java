package cn.cheers.x.module.platform.policy.service;

import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.util.Map;

/**
 * 策略规格合并与解析辅助。
 */
public final class PolicySpecHelper {

    private static final String SCHEDULING_SPEC = "schedulingSpec";
    private static final String MODE = "mode";
    private static final String HORIZON_START = "horizonStart";
    private static final String HORIZON_END = "horizonEnd";
    private static final String CONFLICT_STRATEGY = "conflictStrategy";

    private PolicySpecHelper() {
    }

    /**
     * 将模板 default_spec 与用户 params 合并，params 覆盖 schedulingSpec 同名字段。
     */
    public static String mergeTemplateWithParams(String defaultSpec, Map<String, Object> params) {
        JSONObject root = JSON.parseObject(defaultSpec != null ? defaultSpec : "{}");
        JSONObject schedulingSpec = root.getJSONObject(SCHEDULING_SPEC);
        if (schedulingSpec == null) {
            schedulingSpec = new JSONObject();
            root.put(SCHEDULING_SPEC, schedulingSpec);
        }
        if (params != null) {
            mergeField(params, schedulingSpec, MODE);
            mergeField(params, schedulingSpec, HORIZON_START);
            mergeField(params, schedulingSpec, HORIZON_END);
            mergeField(params, schedulingSpec, CONFLICT_STRATEGY);
        }
        return root.toJSONString();
    }

    public static SchedulingSpecDTO extractSchedulingSpec(String specJson) {
        if (specJson == null || specJson.isBlank()) {
            return SchedulingSpecDTO.builder().build();
        }
        JSONObject root = JSON.parseObject(specJson);
        JSONObject schedulingSpec = root.getJSONObject(SCHEDULING_SPEC);
        if (schedulingSpec == null) {
            schedulingSpec = root;
        }
        return SchedulingSpecDTO.builder()
                .mode(schedulingSpec.getString(MODE))
                .horizonStart(schedulingSpec.getString(HORIZON_START))
                .horizonEnd(schedulingSpec.getString(HORIZON_END))
                .conflictStrategy(schedulingSpec.getString(CONFLICT_STRATEGY))
                .build();
    }

    private static void mergeField(Map<String, Object> params, JSONObject target, String field) {
        if (params.containsKey(field) && params.get(field) != null) {
            target.put(field, params.get(field));
        }
    }
}
