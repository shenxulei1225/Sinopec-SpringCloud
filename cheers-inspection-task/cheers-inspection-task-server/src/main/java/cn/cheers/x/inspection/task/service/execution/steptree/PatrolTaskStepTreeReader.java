package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.service.task.PatrolMeansKeyedSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 从总任务读智能编排已写入的步骤图；从被检查设备读检查参数包。
 * <p>步骤图只按总任务 id 读 {@code step_tree_json}，不用任务编码去撞另一本账。
 * <p>禁止：开跑时挖台账补绑定上的协议版本；用执行设备参数包冒充被检设备；在路径规划 saveRoute 阶段生成步骤图。
 */
@Component
@RequiredArgsConstructor
public class PatrolTaskStepTreeReader {

    static final String TASK_TYPE = "task";
    static final String ACTION_TYPE = "action";
    static final String EQUIPMENT_TYPE = "equipment";
    static final String FIELD_STEP_TREE = "step_tree_json";
    static final String FIELD_HOST_PACK = "host_sop_param_pack";
    static final String STEP_TREE_GAP_MESSAGE =
            "任务没有执行步骤图，请先点「智能编排」生成后再试";

    private final EntityRpcApi entityRpcApi;
    private final ObjectMapper objectMapper;

    /**
     * 解析总任务上已写入的步骤图。没有或空树 → 报缺口，不拿编码去另一张表猜。
     */
    public List<TaskStepNode> loadPatrolTaskStepTree(Object stepTreeRaw) {
        List<TaskStepNode> nodes = TaskStepTreeParser.parse(stepTreeRaw, objectMapper);
        if (nodes.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException(STEP_TREE_GAP_MESSAGE);
        }
        return nodes;
    }

    /**
     * 只按总任务 id 读步骤图。没有或空树 → 报缺口，不拿编码去另一张表猜。
     */
    public List<TaskStepNode> loadPatrolTaskStepTree(Long taskId) {
        if (taskId == null) {
            throw ServiceExceptionUtil.invalidParamException(STEP_TREE_GAP_MESSAGE);
        }
        EntityRespDTO entity = optionalEntity(entityRpcApi.getEntity(taskId, TASK_TYPE));
        if (entity == null) {
            throw ServiceExceptionUtil.invalidParamException(STEP_TREE_GAP_MESSAGE);
        }
        Map<String, Object> bag = bagOf(entity);
        String means = currentMeans(bag.get(PatrolTaskEntityStore.FIELD_DRAFT));
        return loadPatrolTaskStepTree(
                PatrolMeansKeyedSupport.readSlice(unwrapJson(bag.get(FIELD_STEP_TREE)), means));
    }

    /**
     * 读被检查设备上的检查参数包。读不到 → 报缺口；包是空的 → 空包（缺参由填包暴露）。
     */
    /**
     * 生成步骤图用：读不到设备或包 → 空包，不猜到达位置。
     */
    public HostSopParamPack loadHostPackOrEmpty(Long inspectedEquipmentId) {
        if (inspectedEquipmentId == null) {
            return HostSopParamPack.empty();
        }
        EntityRespDTO entity = optionalEntity(entityRpcApi.getEntity(inspectedEquipmentId, EQUIPMENT_TYPE));
        if (entity == null) {
            return HostSopParamPack.empty();
        }
        return HostSopParamPack.parse(bagOf(entity).get(FIELD_HOST_PACK), objectMapper);
    }

    public HostSopParamPack loadHostPack(Long inspectedEquipmentId) {
        if (inspectedEquipmentId == null) {
            throw ServiceExceptionUtil.invalidParamException("读不到被检查设备的检查参数");
        }
        EntityRespDTO entity = requireEntity(
                entityRpcApi.getEntity(inspectedEquipmentId, EQUIPMENT_TYPE),
                "读不到被检查设备的检查参数");
        return HostSopParamPack.parse(bagOf(entity).get(FIELD_HOST_PACK), objectMapper);
    }

    public Optional<Long> resolveActionId(String actionCode) {
        if (!StringUtils.hasText(actionCode)) {
            return Optional.empty();
        }
        CommonResult<EntityRespDTO> result = entityRpcApi.getEntityByCode(actionCode.trim(), ACTION_TYPE);
        if (result == null || !result.isSuccess() || result.getData() == null
                || result.getData().getId() == null || result.getData().getId() <= 0) {
            return Optional.empty();
        }
        return Optional.of(result.getData().getId());
    }

    private static EntityRespDTO requireEntity(CommonResult<EntityRespDTO> result, String gapMessage) {
        EntityRespDTO entity = optionalEntity(result);
        if (entity == null) {
            throw ServiceExceptionUtil.invalidParamException(gapMessage);
        }
        return entity;
    }

    private static EntityRespDTO optionalEntity(CommonResult<EntityRespDTO> result) {
        if (result == null || !result.isSuccess()) {
            return null;
        }
        return result.getData();
    }

    private String currentMeans(Object draftRaw) {
        Object value = unwrapJson(draftRaw);
        if (value instanceof Map<?, ?> map && map.get("patrolExecutionMode") != null) {
            return String.valueOf(map.get("patrolExecutionMode")).trim();
        }
        return null;
    }

    private Object unwrapJson(Object raw) {
        if (!(raw instanceof String text) || !StringUtils.hasText(text)) {
            return raw;
        }
        try {
            return objectMapper.readValue(text.trim(), Object.class);
        } catch (Exception ignored) {
            return raw;
        }
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
