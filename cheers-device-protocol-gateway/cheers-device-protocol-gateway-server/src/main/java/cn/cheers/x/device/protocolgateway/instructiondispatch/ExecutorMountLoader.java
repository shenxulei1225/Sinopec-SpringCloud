package cn.cheers.x.device.protocolgateway.instructiondispatch;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 从执行设备实体读挂载，解析云台相机快照。
 * <p>云台相机：型号名以「云台相机」开头，或这台设备的名称里写了云台相机且不是机身相机。
 * 挂载点序号可以是数字，也可以是数字字符串。
 * <p>不负责：猜默认负载；没有或不止一台云台相机时返回空。
 */
@Component
@RequiredArgsConstructor
class ExecutorMountLoader {

    private static final String EQUIPMENT_TYPE = "equipment";

    private final EntityRpcApi entityRpcApi;
    private final ObjectMapper objectMapper;

    /** 执行设备型号 id；读不到时 null。 */
    Long loadExecutorModelId(Long executorEquipmentId) {
        if (entityRpcApi == null || executorEquipmentId == null || executorEquipmentId <= 0) {
            return null;
        }
        EntityRespDTO host = optionalEntity(entityRpcApi.getEntity(executorEquipmentId, EQUIPMENT_TYPE));
        return host == null ? null : idOf(bagOf(host).get("modelId"));
    }

    ExecutorGimbalMount loadGimbalCamera(Long executorEquipmentId) {
        if (entityRpcApi == null || executorEquipmentId == null || executorEquipmentId <= 0) {
            return null;
        }
        EntityRespDTO host = optionalEntity(entityRpcApi.getEntity(executorEquipmentId, EQUIPMENT_TYPE));
        if (host == null) {
            return null;
        }
        List<ExecutorGimbalMount> cameras = new ArrayList<>();
        boolean missed = false;
        for (MountRow row : mountRows(bagOf(host).get("payload_mounts"))) {
            EntityRespDTO camera = optionalEntity(entityRpcApi.getEntity(row.equipmentId(), EQUIPMENT_TYPE));
            if (camera == null) {
                missed = true;
                continue;
            }
            Map<String, Object> cameraBag = bagOf(camera);
            if (!isGimbalCamera(camera.getModelName(), text(cameraBag.get("name")))) {
                continue;
            }
            Long modelId = idOf(bagOf(camera).get("modelId"));
            if (modelId == null) {
                missed = true;
                continue;
            }
            String payloadIndex = DjiPayloadIndexSupport.format(
                    cameraBag.get("dji_main_type"),
                    cameraBag.get("dji_sub_type"),
                    row.mountPoint());
            cameras.add(new ExecutorGimbalMount(
                    modelId,
                    row.mountPoint(),
                    cameraTypeOf(cameraBag.get("dji_main_type")),
                    payloadIndex));
        }
        if (missed || cameras.size() != 1) {
            return null;
        }
        return cameras.get(0);
    }

    private record MountRow(int mountPoint, long equipmentId) {
    }

    private List<MountRow> mountRows(Object raw) {
        Object value = raw;
        if (raw instanceof String text && !text.isBlank()) {
            try {
                value = objectMapper.readValue(text, new TypeReference<List<Map<String, Object>>>() {
                });
            } catch (Exception ex) {
                return List.of();
            }
        }
        if (!(value instanceof List<?> list)) {
            return List.of();
        }
        List<MountRow> rows = new ArrayList<>();
        for (Object item : list) {
            if (!(item instanceof Map<?, ?> map)) {
                continue;
            }
            Integer mountPoint = intOf(map.get("mountPoint"));
            Long equipmentId = longId(map.get("equipmentId"));
            if (mountPoint == null || equipmentId == null) {
                continue;
            }
            rows.add(new MountRow(mountPoint, equipmentId));
        }
        return rows;
    }

    private static String cameraTypeOf(Object mainType) {
        String segment = DjiPayloadIndexSupport.lastNumberSegment(mainType);
        return segment == null ? "" : segment;
    }

    private static EntityRespDTO optionalEntity(CommonResult<EntityRespDTO> result) {
        if (result == null || !result.isSuccess() || result.getData() == null) {
            return null;
        }
        return result.getData();
    }

    private static Map<String, Object> bagOf(EntityRespDTO entity) {
        Map<String, Object> bag = new LinkedHashMap<>();
        if (entity.getBaseFields() != null) {
            bag.putAll(entity.getBaseFields());
        }
        if (entity.getCustomFields() != null) {
            bag.putAll(entity.getCustomFields());
        }
        return bag;
    }

    /** 型号名优先。型号名空着时，用设备名称里的「云台相机」辨认，机身相机不算。 */
    private static boolean isGimbalCamera(String modelName, String equipmentName) {
        if (modelName != null && modelName.startsWith("云台相机")) {
            return true;
        }
        if (modelName != null && !modelName.isBlank()) {
            return false;
        }
        return equipmentName != null
                && equipmentName.contains("云台相机")
                && !equipmentName.contains("机身相机");
    }

    private static String text(Object raw) {
        if (raw == null) {
            return "";
        }
        return String.valueOf(raw).trim();
    }

    private static Integer intOf(Object raw) {
        if (raw instanceof Number number) {
            return number.intValue();
        }
        if (raw instanceof String text && !text.isBlank()) {
            try {
                return Integer.valueOf(text.trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private static Long longId(Object raw) {
        if (raw instanceof Number number && number.longValue() > 0) {
            return number.longValue();
        }
        if (raw instanceof String text && !text.isBlank()) {
            try {
                long value = Long.parseLong(text.trim());
                return value > 0 ? value : null;
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private static Long idOf(Object raw) {
        if (raw instanceof Number number && number.longValue() > 0) {
            return number.longValue();
        }
        return null;
    }
}
