package cn.cheers.x.inspection.task.service.execution.openrun;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 读这台执行设备的最长飞行时间和耗电曲线。
 * <p>管什么：这一台有最长飞行时间用这一台。这一台空着时，读这台设备型号上已经写下的默认值。
 * <p>不管什么：不编分钟数，不编一条曲线写回设备。
 */
@Service
public class EquipmentFlightBudgetQuery {

    private static final String EQUIPMENT_TYPE = "equipment";
    private static final String MAX_FLIGHT_TIME = "max_flight_time";
    private static final String DISCHARGE_CURVE = "battery_discharge_curve";

    private final EntityRpcApi entityRpcApi;

    public EquipmentFlightBudgetQuery(EntityRpcApi entityRpcApi) {
        this.entityRpcApi = entityRpcApi;
    }

    /**
     * 设备和型号都没有最长飞行时间时，满电秒数空着。
     */
    public Snapshot load(Long equipmentId) {
        if (equipmentId == null || equipmentId <= 0) {
            return Snapshot.empty();
        }
        CommonResult<EntityRespDTO> loaded = entityRpcApi.getEntity(equipmentId, EQUIPMENT_TYPE);
        if (loaded == null || !loaded.isSuccess() || loaded.getData() == null) {
            return Snapshot.empty();
        }
        EntityRespDTO equipment = loaded.getData();
        Object minutes = field(equipment, MAX_FLIGHT_TIME);
        if (!hasText(minutes)) {
            minutes = modelDefault(equipment, MAX_FLIGHT_TIME);
        }
        return new Snapshot(
                DongguBatteryRemain.fullBatterySeconds(minutes),
                field(equipment, DISCHARGE_CURVE));
    }

    private Object modelDefault(EntityRespDTO equipment, String fieldCode) {
        Long modelId = longOf(field(equipment, "modelId"));
        if (modelId == null) {
            modelId = longOf(field(equipment, "model_id"));
        }
        if (modelId == null) {
            return null;
        }
        CommonResult<String> loaded = entityRpcApi.getModelFieldDefault(modelId, fieldCode);
        if (loaded == null || !loaded.isSuccess()) {
            return null;
        }
        return loaded.getData();
    }

    private static Object field(EntityRespDTO equipment, String code) {
        if (equipment.getCustomFields() != null && equipment.getCustomFields().get(code) != null) {
            return equipment.getCustomFields().get(code);
        }
        if (equipment.getBaseFields() != null) {
            return equipment.getBaseFields().get(code);
        }
        return null;
    }

    private static boolean hasText(Object raw) {
        return raw != null && StringUtils.hasText(String.valueOf(raw));
    }

    private static Long longOf(Object raw) {
        if (raw instanceof Number number) {
            return number.longValue();
        }
        if (raw == null) {
            return null;
        }
        try {
            return Long.valueOf(String.valueOf(raw).trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * 满电秒数来自最长飞行时间。耗电曲线是这台设备上已经写下的点。
     */
    public record Snapshot(Integer fullBatterySeconds, Object dischargeCurve) {
        static Snapshot empty() {
            return new Snapshot(null, null);
        }
    }
}
