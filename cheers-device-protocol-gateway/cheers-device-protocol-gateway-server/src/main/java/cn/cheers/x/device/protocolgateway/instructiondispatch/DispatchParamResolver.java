package cn.cheers.x.device.protocolgateway.instructiondispatch;

import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolDispatchProfile;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 翻译前把步骤参数袋与计划级上下文合成填包用的来源袋。
 * <p>负责：透传 {@code actions[].params}；对照为执行设备挂载、系统生成的负载挂载位置、资源文件、媒体槽时在此解析。
 * 负载挂载位置取这架飞机上唯一一台云台相机的挂载点。没有或不止一台时不写，由填包报缺。
 * <p>不负责：RPC 读动作库补动作参数默认（默认应在任务冻结步骤图时写入）；猜一个挂载点；改步骤图。
 */
@Component
@RequiredArgsConstructor
public class DispatchParamResolver {

    /** 单测无 RPC 时：只保留步骤参数与媒体地址，不补挂载与资源路径。 */
    public static DispatchParamResolver noop() {
        return new DispatchParamResolver(null, new ObjectMapper(), new ExecutorMountLoader(null, new ObjectMapper()));
    }

    private static final String ACTION_TYPE = "action";
    private static final String STREAM_URL = "stream_url";

    private final EntityRpcApi entityRpcApi;
    private final ObjectMapper objectMapper;
    private final ExecutorMountLoader executorMountLoader;

    /**
     * @return 填包用的参数袋；仍缺键时由 {@link InstructionPacketFiller} 报错
     */
    public Map<String, Object> resolve(
            DeviceMissionPlan plan,
            DispatchAction action,
            List<SlotMapping> slots
    ) {
        Map<String, Object> bag = new LinkedHashMap<>();
        if (action.params() != null) {
            bag.putAll(action.params());
        }
        if (plan.mediaStreamUrl() != null && !plan.mediaStreamUrl().isBlank()) {
            putBlank(bag, STREAM_URL, plan.mediaStreamUrl().trim());
        }

        boolean needMount = false;
        boolean needReference = false;
        if (slots != null) {
            for (SlotMapping slot : slots) {
                if ("EXECUTOR_MOUNT".equals(slot.sourceKind()) || isGimbalMountSystemSlot(slot)) {
                    needMount = true;
                }
                if ("REFERENCE_RESOURCE".equals(slot.sourceKind())) {
                    needReference = true;
                }
            }
        }

        ExecutorGimbalMount gimbal = needMount
                ? executorMountLoader.loadGimbalCamera(plan.executorEquipmentId())
                : null;
        if (slots != null) {
            for (SlotMapping slot : slots) {
                if ("EXECUTOR_MOUNT".equals(slot.sourceKind())) {
                    putExecutorMountSlot(bag, slot.slot(), gimbal, plan);
                } else if (isGimbalMountSystemSlot(slot)) {
                    putBlank(bag, slot.slot(), gimbal == null ? null : gimbal.mountPoint());
                }
            }
        }

        if (needReference && entityRpcApi != null && action.actionId() != null && action.actionId() > 0) {
            EntityRespDTO actionEntity = optionalEntity(entityRpcApi.getEntity(action.actionId(), ACTION_TYPE));
            Object slotJson = actionEntity == null ? null : bagOf(actionEntity).get("param_slots_json");
            if (slotJson != null) {
                ActionParamSlotReader.fillBlankFromDefaults(
                        bag, ActionParamSlotReader.orientedResourceDefaults(slotJson, objectMapper));
            }
        }

        ActionParamSlotReader.writePayloadPositionFromSelectedCamera(bag);
        ActionParamSlotReader.writeSpeakerIndexFromSelectedSpeaker(bag);
        ActionParamSlotReader.writeSpeakerTtsMd5(bag);
        return Map.copyOf(bag);
    }

    /** 系统生成里已登记的负载挂载位置。只认这一格，不把别的系统字段当成挂载点。 */
    private static boolean isGimbalMountSystemSlot(SlotMapping slot) {
        return slot != null
                && "SYSTEM".equals(slot.sourceKind())
                && "payloadPositionIndex".equals(slot.slot());
    }

    private void putExecutorMountSlot(
            Map<String, Object> bag,
            String slot,
            ExecutorGimbalMount gimbal,
            DeviceMissionPlan plan
    ) {
        if (gimbal == null || slot == null || slot.isBlank()) {
            return;
        }
        Object value = executorMountValue(slot.trim(), gimbal, plan);
        putBlank(bag, slot.trim(), value);
    }

    private static Object executorMountValue(String slot, ExecutorGimbalMount gimbal, DeviceMissionPlan plan) {
        return ProtocolDispatchProfile.resolveExecutorMountValue(
                slot,
                plan.protocolVersion(),
                gimbal.mountPoint(),
                gimbal.cameraType(),
                gimbal.payloadIndex());
    }

    private static void putBlank(Map<String, Object> params, String key, Object value) {
        if (value == null || !ActionParamSlotReader.isFilled(value)) {
            return;
        }
        if (!ActionParamSlotReader.isFilled(params.get(key))) {
            params.put(key, value);
        }
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
}
