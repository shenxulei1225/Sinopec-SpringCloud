package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskCreateReqVO;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskUpdateReqVO;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.model.task.ResourcePolicy;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRpcDtoSupport;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityWriteReqDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 巡检任务草稿只写总任务（巡检任务目录 → 底座任务 → patrol_task）。
 * <p>保存草稿把勾选的被巡检设备和检查项写入 FLD-TSK-019（JSON 文本），不写步骤图。
 * <p>所属设施 FLD-TSK-024 是引用字段，必须写成 {entityTypeCode,id}，禁止写裸数字。
 * <p>建任务已放到哪一步写在草稿袋 createUnlockedStep。更新草稿时必须留下这个键，禁止整袋重写冲掉进度。
 * <p>起点终点写在同一草稿袋 startStopId / endStopId。保存草稿就要落库；禁止只在确认路线时才写。
 * <p>禁止：再写入固定表 inspection_task；读路径猜巡检方式、设施、创建进度或起终点。
 */
@Component
@RequiredArgsConstructor
public class PatrolTaskEntityStore {

    public static final String TASK_TYPE = "task";
    public static final String MODEL_PATROL = "patrol_task";
    public static final String FIELD_DOMAIN = "FLD-TSK-003";
    public static final String FIELD_STATUS = "FLD-TSK-022";
    public static final String FIELD_SCHEDULE = "FLD-TSK-018";
    public static final String FIELD_CONTENT = "FLD-TSK-019";
    public static final String FIELD_RESOURCE = "FLD-TSK-020";
    public static final String FIELD_FACILITY = "FLD-TSK-024";
    public static final String FACILITY_TYPE = "facility";
    public static final String FIELD_ROUTE = "FLD-TSK-027";
    public static final String FIELD_ITEMS = "FLD-TSK-031";
    public static final String FIELD_DRAFT = "FLD-TSK-033";
    public static final String DRAFT_KEY_UNLOCKED = "createUnlockedStep";
    public static final String DRAFT_KEY_START = "startStopId";
    public static final String DRAFT_KEY_END = "endStopId";
    public static final String FIELD_STEP_TREE = "step_tree_json";
    public static final String EMPTY_SCHEDULE = "{\"items\":[],\"version\":1}";

    private final EntityRpcApi entityRpcApi;
    private final ObjectMapper objectMapper;

    public Long createDraft(InspectionTaskCreateReqVO req) {
        if (req == null || !StringUtils.hasText(req.getTaskName())) {
            throw ServiceExceptionUtil.invalidParamException("请先填写任务名称");
        }
        if (req.getFacilityId() == null) {
            throw ServiceExceptionUtil.invalidParamException("请先选择所属设施");
        }
        if (!StringUtils.hasText(req.getPatrolExecutionMode())) {
            throw ServiceExceptionUtil.invalidParamException("请先选择巡检方式");
        }
        EntityWriteReqDTO write = new EntityWriteReqDTO();
        write.setEntityTypeCode(TASK_TYPE);
        write.setModelCode(MODEL_PATROL);
        write.setName(req.getTaskName().trim());
        write.setFields(toFields(req.getDomain(), req.getFacilityId(), req.getPatrolExecutionMode(),
                req.getStartStopId(), req.getEndStopId(),
                req.getInspectionContent(), req.getExecutionDeviceBinding(), req.getResourcePolicy(),
                null, null, null));
        return requireId(entityRpcApi.createEntity(write), "创建总任务失败");
    }

    public void updateDraft(InspectionTaskUpdateReqVO req) {
        if (req == null || req.getId() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务 id 不能为空");
        }
        EntityRespDTO existing = optional(entityRpcApi.getEntity(req.getId(), TASK_TYPE));
        if (existing == null || existing.getId() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务不存在");
        }
        EntityWriteReqDTO write = new EntityWriteReqDTO();
        write.setId(req.getId());
        write.setEntityTypeCode(TASK_TYPE);
        write.setModelCode(MODEL_PATROL);
        if (StringUtils.hasText(req.getTaskName())) {
            write.setName(req.getTaskName().trim());
        }
        write.setFields(toFields(req.getDomain(), req.getFacilityId(), req.getPatrolExecutionMode(),
                req.getStartStopId(), req.getEndStopId(),
                req.getInspectionContent(), req.getExecutionDeviceBinding(), req.getResourcePolicy(),
                null, null, asMap(bagOf(existing).get(FIELD_DRAFT))));
        requireOk(entityRpcApi.updateEntityFields(write), "更新总任务失败");
    }

    /**
     * 只改草稿袋里的键，其它草稿键原样留下。
     * <p>用来写建任务进度；禁止借这里重写巡检方式或勾选对象。
     */
    public void mergeDraftFields(Long taskId, Map<String, Object> patch) {
        if (patch == null || patch.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("草稿补丁不能为空");
        }
        EntityRespDTO existing = optional(entityRpcApi.getEntity(taskId, TASK_TYPE));
        if (existing == null || existing.getId() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务不存在");
        }
        Map<String, Object> draft = asMap(bagOf(existing).get(FIELD_DRAFT));
        draft.putAll(patch);
        EntityWriteReqDTO write = new EntityWriteReqDTO();
        write.setId(taskId);
        write.setEntityTypeCode(TASK_TYPE);
        write.setModelCode(MODEL_PATROL);
        write.setFields(Map.of(FIELD_DRAFT, draft));
        requireOk(entityRpcApi.updateEntityFields(write), "写入建任务进度失败");
    }

    /**
     * 前面步骤改了，已保存路线作废。写成空对象，读出来没有停靠点就不算已保存。
     */
    public void clearPlannedRoute(Long taskId) {
        require(taskId);
        EntityWriteReqDTO write = new EntityWriteReqDTO();
        write.setId(taskId);
        write.setEntityTypeCode(TASK_TYPE);
        write.setModelCode(MODEL_PATROL);
        write.setFields(Map.of(FIELD_ROUTE, Map.of()));
        requireOk(entityRpcApi.updateEntityFields(write), "作废已保存路线失败");
    }

    public void writeStepTree(Long taskId, Map<String, Object> tree) {
        require(taskId);
        EntityWriteReqDTO write = new EntityWriteReqDTO();
        write.setId(taskId);
        write.setEntityTypeCode(TASK_TYPE);
        write.setModelCode(MODEL_PATROL);
        write.setFields(Map.of(FIELD_STEP_TREE, tree));
        requireOk(entityRpcApi.updateEntityFields(write), "写入任务步骤图失败");
    }

    public void writePlannedRoute(Long taskId, Object plannedRoute) {
        require(taskId);
        EntityWriteReqDTO write = new EntityWriteReqDTO();
        write.setId(taskId);
        write.setEntityTypeCode(TASK_TYPE);
        write.setModelCode(MODEL_PATROL);
        write.setFields(Map.of(FIELD_ROUTE, plannedRoute));
        requireOk(entityRpcApi.updateEntityFields(write), "写入路径规划结果失败");
    }

    public PatrolTaskDraft require(Long taskId) {
        if (taskId == null) {
            throw ServiceExceptionUtil.invalidParamException("任务 id 不能为空");
        }
        EntityRespDTO entity = optional(entityRpcApi.getEntity(taskId, TASK_TYPE));
        if (entity == null || entity.getId() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务不存在");
        }
        return fromEntity(entity);
    }

    public void delete(Long taskId) {
        require(taskId);
        requireOk(entityRpcApi.deleteEntity(taskId, TASK_TYPE), "删除总任务失败");
    }

    public List<PatrolTaskDraft> listAll() {
        Long modelId = requireId(entityRpcApi.getModelIdByCode(MODEL_PATROL), "读不到巡检任务型号");
        CommonResult<List<EntityRespDTO>> result = entityRpcApi.listEntities(TASK_TYPE, modelId);
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    result != null && StringUtils.hasText(result.getMsg())
                            ? result.getMsg()
                            : "读不到巡检任务列表");
        }
        List<PatrolTaskDraft> drafts = new ArrayList<>();
        for (EntityRespDTO entity : result.getData()) {
            drafts.add(fromEntity(entity));
        }
        return drafts;
    }

    private Map<String, Object> toFields(
            String domain,
            Long facilityId,
            String means,
            String startStopId,
            String endStopId,
            InspectionContent content,
            ExecutionDeviceBinding binding,
            ResourcePolicy resourcePolicy,
            Object stepTree,
            Object plannedRoute,
            Map<String, Object> existingDraft
    ) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(FIELD_DOMAIN, StringUtils.hasText(domain) ? domain.trim() : "巡检");
        fields.put(FIELD_STATUS, "draft");
        fields.put(FIELD_SCHEDULE, EMPTY_SCHEDULE);
        if (facilityId != null) {
            fields.put(FIELD_FACILITY, facilityRef(facilityId));
        }
        if (content != null) {
            // LONG_TEXT 只收 JSON 文本；禁止塞 POJO，否则勾选设备落不进总任务
            fields.put(FIELD_CONTENT, writeJson(content));
            fields.put(FIELD_ITEMS, itemIdsOf(content));
        }
        if (resourcePolicy != null) {
            fields.put(FIELD_RESOURCE, resourcePolicy);
        }
        Map<String, Object> draft = existingDraft == null
                ? new LinkedHashMap<>()
                : new LinkedHashMap<>(existingDraft);
        if (StringUtils.hasText(means)) {
            draft.put("patrolExecutionMode", means.trim());
        }
        applyDraftText(draft, DRAFT_KEY_START, startStopId);
        applyDraftText(draft, DRAFT_KEY_END, endStopId);
        if (binding != null) {
            draft.put("executionDeviceBinding", binding);
        }
        if (!draft.containsKey(DRAFT_KEY_UNLOCKED)) {
            draft.put(DRAFT_KEY_UNLOCKED, 0);
        }
        fields.put(FIELD_DRAFT, draft);
        if (stepTree != null) {
            fields.put(FIELD_STEP_TREE, stepTree);
        }
        if (plannedRoute != null) {
            fields.put(FIELD_ROUTE, plannedRoute);
        }
        return fields;
    }

    private PatrolTaskDraft fromEntity(EntityRespDTO entity) {
        Map<String, Object> bag = bagOf(entity);
        Map<String, Object> draft = asMap(bag.get(FIELD_DRAFT));
        return new PatrolTaskDraft(
                entity.getId(),
                EntityRpcDtoSupport.readName(entity),
                text(bag.get(FIELD_DOMAIN)),
                asLong(bag.get(FIELD_FACILITY)),
                firstText(draft.get("patrolExecutionMode")),
                asContent(bag.get(FIELD_CONTENT)),
                asBinding(draft.get("executionDeviceBinding")),
                asResource(bag.get(FIELD_RESOURCE)),
                bag.get(FIELD_STEP_TREE),
                bag.get(FIELD_ROUTE),
                text(bag.get(FIELD_STATUS)),
                asUnlockedStep(draft.get(DRAFT_KEY_UNLOCKED)),
                firstText(draft.get(DRAFT_KEY_START)),
                firstText(draft.get(DRAFT_KEY_END))
        );
    }

    private List<Long> itemIdsOf(InspectionContent content) {
        List<Long> ids = new ArrayList<>();
        if (content.getCustomObjects() != null) {
            for (InspectionContent.ObjectContent object : content.getCustomObjects()) {
                if (object.getItems() == null) {
                    continue;
                }
                for (InspectionContent.ItemContent item : object.getItems()) {
                    if (item.getItemId() != null) {
                        ids.add(item.getItemId());
                    }
                }
            }
        }
        return ids;
    }

    /**
     * 巡检内容按 LONG_TEXT 存 JSON。缺勾选也要写成空对象，读出来才能判断「没选设备」。
     */
    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            throw ServiceExceptionUtil.invalidParamException("巡检内容无法写入总任务");
        }
    }

    private InspectionContent asContent(Object raw) {
        return convert(raw, InspectionContent.class);
    }

    private ExecutionDeviceBinding asBinding(Object raw) {
        return convert(raw, ExecutionDeviceBinding.class);
    }

    private ResourcePolicy asResource(Object raw) {
        return convert(raw, ResourcePolicy.class);
    }

    private <T> T convert(Object raw, Class<T> type) {
        if (raw == null) {
            return null;
        }
        return objectMapper.convertValue(unwrap(raw), type);
    }

    private Object unwrap(Object raw) {
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                return objectMapper.readValue(text, new TypeReference<Object>() {
                });
            } catch (Exception ex) {
                return raw;
            }
        }
        return raw;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object raw) {
        Object value = unwrap(raw);
        if (value instanceof Map<?, ?> map) {
            return new LinkedHashMap<>((Map<String, Object>) map);
        }
        return new LinkedHashMap<>();
    }

    /**
     * 草稿袋文本键：null 表示这次请求没带，留下原值；空串表示用户清掉。
     */
    private static void applyDraftText(Map<String, Object> draft, String key, String value) {
        if (value == null) {
            return;
        }
        if (StringUtils.hasText(value)) {
            draft.put(key, value.trim());
        } else {
            draft.remove(key);
        }
    }

    /**
     * 缺这个键 = 还没走过创建流程 = 第 0 步。不是按已有路线猜进度。
     */
    private static Integer asUnlockedStep(Object raw) {
        if (raw instanceof Number number) {
            return number.intValue();
        }
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                return Integer.parseInt(text.trim());
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }
        return 0;
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

    private static String text(Object raw) {
        return raw == null ? null : String.valueOf(raw).trim();
    }

    private static String firstText(Object raw) {
        String value = text(raw);
        return StringUtils.hasText(value) ? value : null;
    }

    /**
     * 所属设施只认设施台账引用。写路径交给实体关联同步，读路径只取 id。
     */
    private static Map<String, Object> facilityRef(long facilityId) {
        Map<String, Object> ref = new LinkedHashMap<>(2);
        ref.put("entityTypeCode", FACILITY_TYPE);
        ref.put("id", facilityId);
        return ref;
    }

    private static Long asLong(Object raw) {
        if (raw instanceof Number number) {
            return number.longValue();
        }
        if (raw instanceof Map<?, ?> map) {
            Object id = map.get("id");
            if (id == null) {
                id = map.get("entityId");
            }
            return asLong(id);
        }
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                return Long.parseLong(text.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static Long requireId(CommonResult<Long> result, String gap) {
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    result != null && StringUtils.hasText(result.getMsg()) ? result.getMsg() : gap);
        }
        return result.getData();
    }

    private static void requireOk(CommonResult<Boolean> result, String gap) {
        if (result == null || !result.isSuccess()) {
            throw ServiceExceptionUtil.invalidParamException(
                    result != null && StringUtils.hasText(result.getMsg()) ? result.getMsg() : gap);
        }
    }

    private static EntityRespDTO optional(CommonResult<EntityRespDTO> result) {
        if (result == null || !result.isSuccess()) {
            return null;
        }
        return result.getData();
    }
}
