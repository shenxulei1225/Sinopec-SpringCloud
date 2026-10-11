package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.task.ActionDurationParamSupport;
import cn.cheers.x.inspection.task.service.task.ActionParamSlotsSupport;
import cn.cheers.x.inspection.task.service.task.PatrolItemActionDurationSupport;
import cn.cheers.x.inspection.task.service.task.PatrolPlannedRouteSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.inspection.task.service.task.TaskPatrolRecordingSupport;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolDispatchProfile;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 生成总任务步骤图并写回。第 3 步无冲突或智能编排时由门面调用，本服务不负责占窗。
 * <p>树上顺序：起点与头动作 → 巡检对象（下挂检查项中文名）→ 检查项下挂动作 → 终点。头动作拆成动作库子动作，标题写中文名。机器人开头是开机自检、电量检查。电量检查写下这次任务需要的秒数，秒数是检查项动作耗时加路径耗时。大疆机场和大疆 Pilot 接着准备并启动航线。东古无人机到电量检查为止，路线步骤在下发时打成指令包。飞完回机库还是在指定点降落由这次任务已保存的路线选择。路线没记下安全起飞高度、飞行速度、返航高度或飞完之后时，用这台执行设备上已经写下的默认值。
 * <p>生成任务时读取设备上的参数，连同检查方法槽、动作库该档槽和动作库 {@code action_duration} 写入动作节点。到达点、选择位置写成同一格：点位编号、已有的业务编码、名称，以及点位上已经写下的坐标。拍照动作不带经度、纬度、地面高度。起点、终点按点位业务编码读到那一条，节点上写下点位编号，标题写成「起点 · 名称」；路网节点编号留在 refCode 给算路，不写成点位编码。对象/检查项写合计秒数。设备已经填了的值优先。没填的槽用动作库该档当前默认值，不沿用步骤上的旧数。选了摄像机时，把编号最后一段写入航线负载挂载位置。默认值也空着才留空，仍列出该参数，不写成无参数。要去的点在设备没写时留下步骤上已有的点。定点拍照的照片路径、校验和文件大小，各型号临时值一致时抄到这一步。正好一台云台相机时，对焦用这一型号的默认值，云台口和负载位置用这一行挂载点，相机类型用这一型号主类型冒号后的数字。机身相机不参与。动作耗时跟这一步的检查参数用同一档型号。执行设备型号对得上就用这一档的默认秒数。对不上时，挂载里只有一个适用型号，就用那一档的默认秒数。没有或不止一个就不填，不拿别的型号的数。还没选执行设备时，按型号分开的动作不算秒数。不从设备参数包取。执行任务只读这里写下的参数，不在执行时再读设备。
 * <p>检查方法与任务页同一条读法：先读检查项 {@code step_tree_json}，按任务已选巡检方式取该档。
 * 旧 {@code action_tree_json} 只读历史，不编一棵树。
 * <p>禁止：路径预览写步骤图；草稿保存时生成；执行时再算树；人工硬挂机器人头尾；把读空旧列说成没配方法；按已删除的无人机任务头生成；把秒数换算成电量百分比。
 */
@Service
@RequiredArgsConstructor
public class TaskStepTreeGenerateService {

    static final String ITEM_TYPE = "inspection_item";
    static final String FIELD_STEP_TREE = "step_tree_json";
    static final String FIELD_ACTION_TREE = "action_tree_json";
    /** 机器人电量检查。这一步写下这次任务需要的秒数。 */
    static final String BATTERY_ROBOT = "act-robot-battery-check";
    /** 电量检查上的秒数。检查项动作耗时加路径耗时。 */
    static final String REQUIRED_DURATION = "required_duration";
    /** 任务级全程录像：动作库编码。 */
    /** 飞向目标点。降落到指定点时，用任务终点再写一个航点，到了那里降落。 */
    static final String WAYPOINT_ANCHOR = "action-065340437bcb46788b7dc261fab81c63";
    static final String FINISH_GO_HOME = "goHome";
    static final String FINISH_LAND_AT_POINT = "landAtPoint";
    static final String LOCATION_REF = "location_ref";
    /** 字段库「选择位置」；到达指定位置动作用此槽，不是 location_ref。 */
    static final String SELECT_LOCATION = "F-e49f76bdca3e4e1393e8e2f1cc0e7867";
    /** 大疆飞向目标点指令里的编号；与已选位置的点位编号一致。 */
    static final String FLY_TO_ID = "fly_to_id";
    static final String ACTION_TYPE = "action";
    static final String POINT_TYPE = "point";
    static final String FIELD_ACTION_DOMAIN = "action_domain";
    static final String FIELD_ANALYSIS_CAPABILITY_ID = "analysis_capability_id";
    static final String DOMAIN_DEVICE = "DEVICE";
    static final String DOMAIN_INTELLIGENT_ANALYSIS = "INTELLIGENT_ANALYSIS";
    static final Set<String> ACTION_DOMAINS = Set.of(
            DOMAIN_DEVICE, DOMAIN_INTELLIGENT_ANALYSIS, "MANUAL", "PLATFORM");
    /** 动作参数里留给算路的路网节点编号。不是点位编号，也不是点位业务编码。 */
    static final String STATION_NODE_ID = "stationNodeId";

    private final PatrolTaskEntityStore patrolTaskEntityStore;
    private final PatrolTaskStepTreeReader catalog;
    private final EntityRpcApi entityRpcApi;
    private final ObjectMapper objectMapper;
    private final ActionGroupMemberParser actionGroupMemberParser;

    public Map<String, Object> generate(Long taskId, List<Long> equipmentOrder) {
        return generate(taskId, equipmentOrder, null, null);
    }

    /**
     * 按勾选检查项生成步骤图并写回，返回刚写下的树。
     * 起点和终点来自任务创建时已经算好的路线，不从设备检查配置读。
     * 返回充电不在这里自动加上。用户把这个动作放进检查方法时，位置用动作上选的充电点。
     */
    public Map<String, Object> generate(Long taskId, List<Long> equipmentOrder, String startStopId, String endStopId) {
        PatrolTaskDraft draft = patrolTaskEntityStore.require(taskId);
        Map<String, Object> tree = compose(draft, equipmentOrder, startStopId, endStopId);
        patrolTaskEntityStore.writeStepTree(taskId, tree);
        return tree;
    }

    /**
     * 按传入的勾选和起点终点编出步骤图，不写回任务。
     * 向导排期在页面上生成步骤图时用这份；生成任务时才由 {@link #generate} 写入已发布字段。
     */
    public Map<String, Object> compose(
            PatrolTaskDraft draft, List<Long> equipmentOrder, String startStopId, String endStopId) {
        if (!StringUtils.hasText(draft.patrolExecutionMode())) {
            throw ServiceExceptionUtil.invalidParamException("还没有选择巡检方式");
        }
        List<TaskStepTreeGenerator.SelectedEquipment> selected = selectedOf(draft.inspectionContent());
        Map<Long, String> itemNames = itemNamesOf(draft.inspectionContent());
        Map<String, Object> planned = plannedMap(draft.plannedRoute());
        String start = firstText(startStopId, plannedText(planned, "startStopId"));
        String end = firstText(endStopId, plannedText(planned, "endStopId"));
        if (!StringUtils.hasText(end) && StringUtils.hasText(start)
                && planned != null && Boolean.TRUE.equals(planned.get("returnToStart"))) {
            end = start;
        }
        List<TaskStepTreeGenerator.MethodAction> head = List.of();
        List<TaskStepTreeGenerator.MethodAction> tail = List.of();
        String means = draft.patrolExecutionMode().trim();
        if (TaskStepTreeGenerator.MEANS_ROBOT.equals(means)
                || TaskStepTreeGenerator.MEANS_UAV.equals(means)
                || TaskStepTreeGenerator.MEANS_MANUAL.equals(means)) {
            if (!StringUtils.hasText(start) || !StringUtils.hasText(end)) {
                throw ServiceExceptionUtil.invalidParamException("请先在任务中设置起点和终点");
            }
        }
        Long equipmentModelId = executionEquipmentModelId(draft, means);
        BigDecimal requiredSeconds = requiredFlightSeconds(draft);
        String protocol = protocolOf(draft);
        if (TaskStepTreeGenerator.MEANS_ROBOT.equals(means)) {
            head = expandHeadTailSteps(
                    ProtocolDispatchProfile.robotOpeningSteps(), means, equipmentModelId, null, protocol);
            tail = new ArrayList<>();
            appendTaskLevelRecording(means, protocol, equipmentModelId, head, tail);
        } else if (TaskStepTreeGenerator.MEANS_UAV.equals(means)) {
            if (!ProtocolDispatchProfile.supportsUavOpeningSteps(protocol)) {
                throw ServiceExceptionUtil.invalidParamException("执行设备的对接协议无法生成开头步骤");
            }
            Map<String, Object> flightPlan = ProtocolDispatchProfile.includesWaylinePrepareInOpening(protocol)
                    ? withDeviceFlightDefaults(planned, executionDeviceFlightDefaults(draft, means))
                    : null;
            head = expandHeadTailSteps(
                    ProtocolDispatchProfile.uavOpeningSteps(protocol), means, equipmentModelId, flightPlan, protocol);
            tail = ProtocolDispatchProfile.includesLandingTailAfterMission(protocol) && flightPlan != null
                    ? new ArrayList<>(landingTail(flightPlan, end, means, equipmentModelId, protocol))
                    : new ArrayList<>();
            appendTaskLevelRecording(means, protocol, equipmentModelId, head, tail);
        }
        selected = withVisitStops(selected, means);
        Map<String, String> itemStops = itemStopsOf(selected, means);
        Map<Long, List<TaskStepTreeGenerator.MethodAction>> methods = loadMethods(
                selected, means, itemNames, equipmentModelId, protocol);
        Map<String, Object> tree = TaskStepTreeGenerator.generate(
                means, selected, equipmentOrder, methods, head, tail,
                start, "起点", end, "终点", itemNames,
                itemStops, Set.of(LOCATION_REF, SELECT_LOCATION));
        enrichReviewSnapshot(
                tree, selected, means, draft.facilityId(), equipmentModelId,
                executionEquipmentId(draft, means), requiredSeconds, protocolOf(draft));
        attachPointIdentity(tree, draft.facilityId());
        return tree;
    }

    private Map<Long, List<TaskStepTreeGenerator.MethodAction>> loadMethods(
            List<TaskStepTreeGenerator.SelectedEquipment> selected,
            String means,
            Map<Long, String> itemNames,
            Long equipmentModelId,
            String protocol
    ) {
        Map<Long, List<TaskStepTreeGenerator.MethodAction>> methods = new LinkedHashMap<>();
        for (TaskStepTreeGenerator.SelectedEquipment equipment : selected) {
            for (Long itemId : equipment.itemIds()) {
                if (methods.containsKey(itemId)) {
                    continue;
                }
                EntityRespDTO item = requireItem(itemId);
                Map<String, Object> bag = bagOf(item);
                putItemNameIfAbsent(itemNames, itemId, bag);
                Object raw = methodSnapshotOf(bag);
                List<TaskStepTreeGenerator.MethodAction> actions = resolveLibraryActions(
                        InspectionMethodSnapshotParser.actionsForMeans(raw, means, objectMapper),
                        itemId,
                        itemNames,
                        means,
                        equipmentModelId,
                        protocol);
                // 读到的该档步骤必须放进结果。漏放时后面一律当成没配方法。
                methods.put(itemId, actions);
            }
        }
        return methods;
    }

    private EntityRespDTO requireItem(Long itemId) {
        CommonResult<EntityRespDTO> result = entityRpcApi.getEntity(itemId, ITEM_TYPE);
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException("读不到检查项");
        }
        return result.getData();
    }

    /**
     * 检查项步骤树权威列；旧动作树列只在权威列空白时读历史。
     */
    private static Object methodSnapshotOf(Map<String, Object> bag) {
        Object stepTree = bag.get(FIELD_STEP_TREE);
        if (hasSnapshot(stepTree)) {
            return stepTree;
        }
        return bag.get(FIELD_ACTION_TREE);
    }

    private static boolean hasSnapshot(Object raw) {
        if (raw == null) {
            return false;
        }
        if (raw instanceof String text) {
            return StringUtils.hasText(text);
        }
        return true;
    }

    /**
     * 树上 actionId 是动作编码；写入步骤图前补动作库主键。
     * 动作已删除则跳过，不写进任务步骤图，也不因此停下。
     * 编码对不上、并且动作没有被删除，仍报缺口。
     * 这一步要哪些参数读动作库当前这一档，不读检查项步骤上留下的名单。
     */
    private List<TaskStepTreeGenerator.MethodAction> resolveLibraryActions(
            List<TaskStepTreeGenerator.MethodAction> actions,
            Long itemId,
            Map<Long, String> itemNames,
            String means,
            Long equipmentModelId,
            String protocol
    ) {
        if (actions == null || actions.isEmpty()) {
            return List.of();
        }
        List<TaskStepTreeGenerator.MethodAction> resolved = new ArrayList<>();
        for (TaskStepTreeGenerator.MethodAction action : actions) {
            if (TaskPatrolRecordingSupport.dropFromInspectionMethod(action.refCode(), means, protocol)) {
                continue;
            }
            Long actionId = action.refId();
            if (actionId == null) {
                Optional<Long> resolvedId = catalog.resolveActionId(action.refCode());
                if (resolvedId.isEmpty()) {
                    if (catalog.isDeletedAction(action.refCode())) {
                        continue;
                    }
                    String item = itemNames != null && StringUtils.hasText(itemNames.get(itemId))
                            ? "检查项「" + itemNames.get(itemId).trim() + "」"
                            : "检查项 #" + itemId;
                    String step = StringUtils.hasText(action.title())
                            ? "步骤「" + action.title().trim() + "」"
                            : "步骤";
                    throw ServiceExceptionUtil.invalidParamException(
                            item + "的" + step + "对不上动作库");
                }
                actionId = resolvedId.get();
            }
            TaskStepTreeGenerator.MethodAction libraryAction = new TaskStepTreeGenerator.MethodAction(
                    actionId, action.refCode(), action.title(), action.params(),
                    action.sourceNodeKey(), librarySlots(action.refCode(), means, equipmentModelId));
            // 检查方法引用动作组时也必须在这里展开。头尾与普通检查动作共用同一条展开路径。
            resolved.addAll(expandAction(
                    libraryAction,
                    action.params(),
                    means,
                    equipmentModelId,
                    protocol,
                    new HashSet<>()));
        }
        return resolved;
    }

    /** 动作库当前巡检方式和型号声明的参数。读不到动作则空名单，不退回步骤上的旧名单。 */
    private List<String> librarySlots(String actionCode, String means, Long equipmentModelId) {
        EntityRespDTO entity = loadAction(actionCode);
        if (entity == null) {
            return List.of();
        }
        return ActionParamSlotsSupport.fieldCodes(
                bagOf(entity).get("param_slots_json"), means, equipmentModelId, objectMapper);
    }

    /**
     * 任务上这四项空着时，用执行设备台账里已经写下的默认值。
     * 安全起飞高度、返航高度、飞行速度、飞完之后。设备上没有的项保持空，不编一个数。
     */
    private Map<String, Object> executionDeviceFlightDefaults(PatrolTaskDraft draft, String means) {
        if (!TaskStepTreeGenerator.MEANS_UAV.equals(means) || draft.executionDeviceBinding() == null) {
            return Map.of();
        }
        Long equipmentId = draft.executionDeviceBinding().getEquipmentId();
        if (equipmentId == null || equipmentId <= 0) {
            return Map.of();
        }
        CommonResult<EntityRespDTO> result = entityRpcApi.getEntity(equipmentId, "equipment");
        if (result == null || !result.isSuccess() || result.getData() == null) {
            return Map.of();
        }
        Map<String, Object> bag = bagOf(result.getData());
        Map<String, Object> defaults = new LinkedHashMap<>();
        putText(defaults, "securityTakeoffHeight", textOf(bag.get("security_takeoff_height")));
        putText(defaults, "flightSpeed", textOf(bag.get("max_speed")));
        putText(defaults, "rthAltitude", textOf(bag.get("rth_altitude")));
        putText(defaults, "waylineFinish", textOf(bag.get("wayline_finish")));
        return defaults;
    }

    /** 任务上已有的数留下。空着的格子才写入这台无人机的默认值。 */
    private static Map<String, Object> withDeviceFlightDefaults(
            Map<String, Object> planned, Map<String, Object> deviceDefaults
    ) {
        Map<String, Object> merged = new LinkedHashMap<>();
        if (planned != null) {
            merged.putAll(planned);
        }
        if (deviceDefaults == null || deviceDefaults.isEmpty()) {
            return merged;
        }
        for (String key : List.of("securityTakeoffHeight", "flightSpeed", "rthAltitude", "waylineFinish")) {
            if (!StringUtils.hasText(plannedText(merged, key))) {
                String value = plannedText(deviceDefaults, key);
                if (StringUtils.hasText(value)) {
                    merged.put(key, value);
                }
            }
        }
        return merged;
    }

    /**
     * 执行设备挂载上的设备型号。用来对上按型号分开的检查参数。
     * 读不到挂载就空列表，不编一个型号。
     */
    private List<Long> mountedModelIds(Long executionEquipmentId) {
        if (executionEquipmentId == null || entityRpcApi == null) {
            return List.of();
        }
        CommonResult<EntityRespDTO> host = entityRpcApi.getEntity(executionEquipmentId, "equipment");
        if (host == null || !host.isSuccess() || host.getData() == null) {
            return List.of();
        }
        Object raw = bagOf(host.getData()).get("payload_mounts");
        Object value = raw;
        if (raw instanceof String text && !text.isBlank()) {
            try {
                value = objectMapper.readValue(text, new TypeReference<List<Map<String, Object>>>() {
                });
            } catch (Exception ex) {
                return List.of();
            }
        }
        if (!(value instanceof List<?> rows)) {
            return List.of();
        }
        List<Long> modelIds = new ArrayList<>();
        for (Object row : rows) {
            if (!(row instanceof Map<?, ?> map)) {
                continue;
            }
            Long childId = idOf(map.get("equipmentId"));
            if (childId == null) {
                continue;
            }
            CommonResult<EntityRespDTO> child = entityRpcApi.getEntity(childId, "equipment");
            if (child == null || !child.isSuccess() || child.getData() == null) {
                continue;
            }
            Long modelId = idOf(bagOf(child.getData()).get("modelId"));
            if (modelId != null && !modelIds.contains(modelId)) {
                modelIds.add(modelId);
            }
        }
        return modelIds;
    }

    /**
     * 无人机生成步骤时用这次执行设备的型号。没有执行设备或读不到型号则空，不用别的设备顶上。
     */
    private Long executionEquipmentId(PatrolTaskDraft draft, String means) {
        if (draft.executionDeviceBinding() == null) {
            return null;
        }
        if (!TaskStepTreeGenerator.MEANS_UAV.equals(means) && !TaskStepTreeGenerator.MEANS_ROBOT.equals(means)) {
            return null;
        }
        Long equipmentId = draft.executionDeviceBinding().getEquipmentId();
        if (equipmentId == null || equipmentId <= 0) {
            return null;
        }
        return equipmentId;
    }

    private Long executionEquipmentModelId(PatrolTaskDraft draft, String means) {
        Long equipmentId = executionEquipmentId(draft, means);
        if (equipmentId == null) {
            return null;
        }
        CommonResult<EntityRespDTO> result = entityRpcApi.getEntity(equipmentId, "equipment");
        if (result == null || !result.isSuccess() || result.getData() == null) {
            return null;
        }
        Object raw = bagOf(result.getData()).get("modelId");
        if (raw instanceof Number number && number.longValue() > 0) {
            return number.longValue();
        }
        if (raw instanceof String text && !text.isBlank()) {
            try {
                long value = Long.parseLong(text.trim());
                return value > 0 ? value : null;
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private TaskStepTreeGenerator.MethodAction requireLibraryAction(
            String code, String title, Map<String, Object> params, String protocol
    ) {
        Optional<Long> actionId = catalog.resolveActionId(code);
        if (actionId.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("动作库没有本巡检方式的头尾动作");
        }
        return new TaskStepTreeGenerator.MethodAction(actionId.get(), code, title, params);
    }

    /**
     * 复合头尾拆成库里的子动作；叶子用动作库名称。缺子动作或对不上库 → 报缺口。
     */
    private void appendTaskLevelRecording(
            String means,
            String protocol,
            Long equipmentModelId,
            List<TaskStepTreeGenerator.MethodAction> head,
            List<TaskStepTreeGenerator.MethodAction> tail) {
        if (!TaskPatrolRecordingSupport.taskLevelRecordingEnabled(means, protocol)) {
            return;
        }
        ProtocolDispatchProfile.MissionHeadTailStep start = ProtocolDispatchProfile.taskRecordStartStep();
        ProtocolDispatchProfile.MissionHeadTailStep stop = ProtocolDispatchProfile.taskRecordStopStep();
        List<TaskStepTreeGenerator.MethodAction> recordStart = expandLibraryAction(
                start.actionCode(), start.title(), Map.of(), means, equipmentModelId, protocol);
        // 大疆开头顺序：电量检查 → 开始录像 → 准备航线（录像须在起飞前开始）。
        int recordInsertAt = head.size();
        for (int i = 0; i < head.size(); i++) {
            if (ProtocolDispatchProfile.ACTION_UAV_WAYLINE_PREPARE.equals(head.get(i).refCode())) {
                recordInsertAt = i;
                break;
            }
        }
        head.addAll(recordInsertAt, recordStart);
        tail.addAll(0, expandLibraryAction(
                stop.actionCode(), stop.title(), Map.of(), means, equipmentModelId, protocol));
    }

    private List<TaskStepTreeGenerator.MethodAction> expandHeadTailSteps(
            List<ProtocolDispatchProfile.MissionHeadTailStep> blueprint,
            String means,
            Long equipmentModelId,
            Map<String, Object> flightPlan,
            String protocol) {
        List<TaskStepTreeGenerator.MethodAction> expanded = new ArrayList<>();
        for (ProtocolDispatchProfile.MissionHeadTailStep step : blueprint) {
            Map<String, Object> params = Map.of();
            if (step.paramsTemplate() == ProtocolDispatchProfile.ParamsTemplate.UAV_FLIGHT_MISSION
                    && flightPlan != null) {
                params = missionParams(flightPlan);
            }
            expanded.addAll(expandLibraryAction(
                    step.actionCode(), step.title(), params, means, equipmentModelId, protocol));
        }
        return expanded;
    }

    private List<TaskStepTreeGenerator.MethodAction> expandLibraryAction(
            String code, String fallbackTitle, Map<String, Object> inheritParams,
            String means, Long equipmentModelId, String protocol
    ) {
        return expandAction(
                requireLibraryAction(code, fallbackTitle, inheritParams, protocol),
                inheritParams, means, equipmentModelId, protocol, new HashSet<>());
    }

    private List<TaskStepTreeGenerator.MethodAction> expandAction(
            TaskStepTreeGenerator.MethodAction action,
            Map<String, Object> inheritParams,
            String means,
            Long equipmentModelId,
            String protocol,
            Set<String> visiting
    ) {
        EntityRespDTO entity = loadAction(action.refCode());
        if (entity == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    "动作库没有「" + firstText(action.title(), action.refCode()) + "」");
        }
        Map<String, Object> bag = bagOf(entity);
        String code = firstText(textOf(bag.get("code")), action.refCode());
        Set<String> branch = new HashSet<>(visiting);
        if (code != null && !branch.add(code)) {
            throw ServiceExceptionUtil.invalidParamException("动作库头尾动作存在循环引用");
        }
        String title = displayActionTitle(bag);
        List<ActionGroupMemberParser.Member> members = actionGroupMemberParser.parse(
                bag.get(FIELD_ACTION_TREE));
        if (members.isEmpty()) {
            ActionOwnership ownership = requireActionOwnership(bag, code, title, protocol);
            List<String> declaredSlots = action.declaredSlots().isEmpty()
                    ? librarySlots(code, means, equipmentModelId)
                    : action.declaredSlots();
            return List.of(new TaskStepTreeGenerator.MethodAction(
                    entity.getId(), code, title, paramsForLeaf(bag, inheritParams, means, equipmentModelId),
                    action.sourceNodeKey(), declaredSlots, ownership.actionDomain(), ownership.analysisCapabilityId(),
                    action.required()));
        }
        List<TaskStepTreeGenerator.MethodAction> expanded = new ArrayList<>();
        for (ActionGroupMemberParser.Member member : members) {
            Map<String, Object> memberParams = new LinkedHashMap<>(member.params());
            memberParams.putAll(inheritParams);
            String sourceNodeKey = StringUtils.hasText(action.sourceNodeKey())
                    ? action.sourceNodeKey() + "/" + member.memberKey()
                    : member.memberKey();
            TaskStepTreeGenerator.MethodAction memberAction = requireLibraryAction(
                    member.actionCode(), member.actionCode(), memberParams, protocol);
            memberAction = new TaskStepTreeGenerator.MethodAction(
                    memberAction.refId(),
                    memberAction.refCode(),
                    memberAction.title(),
                    memberAction.params(),
                    sourceNodeKey,
                    memberAction.declaredSlots(),
                    memberAction.actionDomain(),
                    memberAction.analysisCapabilityId(),
                    member.required());
            expanded.addAll(expandAction(
                    memberAction, memberParams, means, equipmentModelId, protocol, branch));
        }
        return expanded;
    }

    /**
     * 动作模型派生的归属投影是任务快照输入。缺归属直接阻断，不按标题或编码猜分流。
     * 设备动作统一交给协议网关；具体指令与打包方式只读指令协议对接。
     */
    private ActionOwnership requireActionOwnership(
            Map<String, Object> bag, String actionCode, String actionTitle, String protocol
    ) {
        String display = StringUtils.hasText(actionTitle) ? actionTitle.trim() : actionCode;
        String domain = textOf(bag.get(FIELD_ACTION_DOMAIN)).toUpperCase();
        if (!StringUtils.hasText(domain)) {
            throw ServiceExceptionUtil.invalidParamException("动作「" + display + "」缺少 actionDomain");
        }
        if (!ACTION_DOMAINS.contains(domain)) {
            throw ServiceExceptionUtil.invalidParamException(
                    "动作「" + display + "」的 actionDomain 无法识别：" + domain);
        }
        Long capabilityId = idOf(bag.get(FIELD_ANALYSIS_CAPABILITY_ID));
        if (DOMAIN_INTELLIGENT_ANALYSIS.equals(domain) && capabilityId == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    "智能分析动作「" + display + "」缺少 analysisCapabilityId");
        }
        return new ActionOwnership(domain, capabilityId);
    }

    private record ActionOwnership(
            String actionDomain,
            Long analysisCapabilityId
    ) {
    }

    private List<TaskStepTreeGenerator.SelectedEquipment> withVisitStops(
            List<TaskStepTreeGenerator.SelectedEquipment> selected,
            String means
    ) {
        List<TaskStepTreeGenerator.SelectedEquipment> visits = new ArrayList<>();
        for (TaskStepTreeGenerator.SelectedEquipment equipment : selected) {
            HostSopParamPack pack = catalog.loadHostPackOrEmpty(equipment.equipmentId());
            String stopId = stopIdOf(pack, equipment.itemIds(), means);
            visits.add(equipment.withVisit(stopId, objectTitle(equipment.equipmentName())));
        }
        return visits;
    }

    /**
     * 每个检查项自己的点。没填位置的项跟上这台设备已经有的点，不另造一个点。
     */
    private Map<String, String> itemStopsOf(
            List<TaskStepTreeGenerator.SelectedEquipment> selected,
            String means
    ) {
        Map<String, String> stops = new LinkedHashMap<>();
        for (TaskStepTreeGenerator.SelectedEquipment equipment : selected) {
            HostSopParamPack pack = catalog.loadHostPackOrEmpty(equipment.equipmentId());
            String deviceStop = null;
            Map<Long, String> own = new LinkedHashMap<>();
            for (Long itemId : equipment.itemIds()) {
                String location = pack == null ? null : pack.firstLocationRef(itemId, means);
                if (location != null && !location.isBlank()) {
                    deviceStop = location.trim();
                }
                own.put(itemId, location == null || location.isBlank() ? null : location.trim());
            }
            for (Long itemId : equipment.itemIds()) {
                String location = own.get(itemId);
                if (location == null) {
                    location = deviceStop;
                }
                if (location != null) {
                    stops.put(TaskStepTreeGenerator.itemStopKey(equipment.equipmentId(), itemId), location);
                }
            }
        }
        return stops;
    }

    private static String stopIdOf(HostSopParamPack pack, List<Long> itemIds, String means) {
        if (pack == null) {
            return null;
        }
        if (itemIds != null) {
            for (Long itemId : itemIds) {
                String location = pack.firstLocationRef(itemId, means);
                if (StringUtils.hasText(location)) {
                    return location.trim();
                }
            }
        }
        return null;
    }

    /**
     * 生成步骤图时读取设备上的参数和动作库耗时，写进动作节点，供核对计划显示。
     * 电量检查在这里写下这次任务需要的秒数。执行任务读的就是这一份，不在执行时再读设备上的参数。
     */
    @SuppressWarnings("unchecked")
    private void enrichReviewSnapshot(
            Map<String, Object> tree,
            List<TaskStepTreeGenerator.SelectedEquipment> selected,
            String means,
            Long facilityId,
            Long equipmentModelId,
            Long executionEquipmentId,
            BigDecimal requiredSeconds,
            String protocol
    ) {
        Object nodesRaw = tree.get("nodes");
        if (!(nodesRaw instanceof List<?> list) || list.isEmpty()) {
            return;
        }
        Map<String, Map<String, Object>> byKey = new LinkedHashMap<>();
        List<Map<String, Object>> nodes = new ArrayList<>();
        for (Object raw : list) {
            if (raw instanceof Map<?, ?> row) {
                Map<String, Object> node = (Map<String, Object>) row;
                nodes.add(node);
                Object key = node.get("nodeKey");
                if (key != null && StringUtils.hasText(String.valueOf(key))) {
                    byKey.put(String.valueOf(key).trim(), node);
                }
            }
        }
        Map<Long, HostSopParamPack> packs = new HashMap<>();
        for (TaskStepTreeGenerator.SelectedEquipment equipment : selected) {
            packs.put(equipment.equipmentId(), catalog.loadHostPackOrEmpty(equipment.equipmentId()));
        }
        List<Long> mountedModelIds = mountedModelIds(executionEquipmentId);
        Map<String, String> pointNames = new LinkedHashMap<>();
        Map<String, BigDecimal> actionMinutes = new LinkedHashMap<>();
        for (Map<String, Object> node : nodes) {
            if (!TaskStepNode.HANG_ACTION.equals(textOf(node.get("hangTypeCode")))) {
                continue;
            }
            Map<String, Object> previous = paramsOf(node.get("params"));
            Long itemId = ancestorRefId(node, byKey, TaskStepNode.HANG_INSPECTION_ITEM);
            if (itemId == null) {
                itemId = idOf(node.get("hostItemId"));
            }
            Long equipmentId = ancestorRefId(node, byKey, TaskStepNode.HANG_EQUIPMENT);
            if (equipmentId == null) {
                equipmentId = idOf(node.get("equipmentId"));
            }
            if (equipmentId == null) {
                equipmentId = equipmentIdOnItem(node, byKey);
            }
            String refCode = textOf(node.get("refCode"));
            // 只合并动作库 param_slots 与设备检查包；负载编号等协议字段由网关在翻译时按对照填。
            // 按型号分开的参数整份跟适用型号走：执行设备型号优先，否则挂载里唯一一个适用型号。
            EntityRespDTO action = loadAction(refCode);
            Object slotJson = action != null ? bagOf(action).get("param_slots_json") : null;
            Long paramModelId = StepParamModelScope.resolve(
                    slotJson, means, equipmentModelId, mountedModelIds, objectMapper);
            List<String> declared = List.of();
            Map<String, Object> defaults = Map.of();
            if (action != null && slotJson != null) {
                declared = ActionParamSlotsSupport.fieldCodes(slotJson, means, paramModelId, objectMapper);
                defaults = ActionParamSlotsSupport.defaultValues(slotJson, means, paramModelId, objectMapper);
            }
            Map<String, Object> equipmentValues = Map.of();
            if (itemId != null && equipmentId != null) {
                HostSopParamPack pack = packs.getOrDefault(equipmentId, HostSopParamPack.empty());
                equipmentValues = pack.paramsFor(
                        itemId, textOf(node.get("sourceNodeKey")), refCode, means, paramModelId);
            }
            Map<String, Object> merged = ActionParamSlotsSupport.resolveStepParams(
                    previous, declared, equipmentValues, defaults, Set.of(LOCATION_REF, SELECT_LOCATION));
            normalizeLocationParam(merged, LOCATION_REF, pointNames, facilityId);
            normalizeLocationParam(merged, SELECT_LOCATION, pointNames, facilityId);
            fillFlyToIdFromSelectedLocation(merged);
            Long durationModelId = paramModelId != null ? paramModelId : equipmentModelId;
            Optional<ActionDurationParamSupport.SlotDuration> duration = durationOfAction(
                    textOf(node.get("refCode")), means, durationModelId);
            if (duration.isPresent()) {
                merged.put(ActionDurationParamSupport.FIELD_CODE, duration.get().seconds());
                if (StringUtils.hasText(duration.get().displayUnit())) {
                    merged.put("durationUnit", duration.get().displayUnit().trim());
                }
                actionMinutes.put(textOf(node.get("nodeKey")), duration.get().seconds());
            }
            if (ProtocolDispatchProfile.ACTION_UAV_BATTERY_CHECK.equals(refCode) || BATTERY_ROBOT.equals(refCode)) {
                if (requiredSeconds != null) {
                    merged.put(REQUIRED_DURATION, requiredSeconds);
                } else {
                    merged.remove(REQUIRED_DURATION);
                }
            }
            if (ProtocolDispatchProfile.ACTION_UAV_WAYLINE_PREPARE.equals(refCode)) {
                keepFilled(merged, previous, "security_takeoff_height");
                keepFilled(merged, previous, "max_speed");
                keepFilled(merged, previous, "rth_altitude");
                keepFilled(merged, previous, "finish_action");
            }
            if (!merged.isEmpty()) {
                node.put("params", merged);
            }
        }
        writeDurationSums(nodes, byKey, actionMinutes);
        nameVisitPoints(nodes, pointNames);
        if (!pointNames.isEmpty()) {
            tree.put("pointNames", pointNames);
        }
    }

    /**
     * 到达位置、选择位置写成同一格：点位编号、业务编码（有才写）、名称。
     * 核对计划读这一格里的名称。纯字符串是路网节点编号，按同步后的业务编码读到点位，编号和名称写回这一格，路网节点编号另记，不写入 code。
     */
    /**
     * 飞向目标点编号写已选位置的点位编号。选了位置才写；没选则清掉，不编 UUID。
     */
    private void fillFlyToIdFromSelectedLocation(Map<String, Object> params) {
        String pointId = pointIdTextFromLocation(params.get(SELECT_LOCATION));
        if (!StringUtils.hasText(pointId)) {
            pointId = pointIdTextFromLocation(params.get(LOCATION_REF));
        }
        if (StringUtils.hasText(pointId)) {
            params.put(FLY_TO_ID, pointId.trim());
        } else {
            params.remove(FLY_TO_ID);
        }
    }

    private static String pointIdTextFromLocation(Object raw) {
        if (raw == null) {
            return null;
        }
        Long id = locationIdOf(raw);
        if (id != null) {
            return String.valueOf(id);
        }
        if (raw instanceof String text && StringUtils.hasText(text.trim())) {
            return text.trim();
        }
        if (raw instanceof Map<?, ?> map) {
            Object nested = map.get(LOCATION_REF);
            if (nested == null) {
                nested = map.get("location_ref");
            }
            if (nested != null) {
                return pointIdTextFromLocation(nested);
            }
        }
        return null;
    }

    private void normalizeLocationParam(
            Map<String, Object> params, String field, Map<String, String> pointNames, Long facilityId
    ) {
        if (!params.containsKey(field)) {
            return;
        }
        Object raw = params.get(field);
        if (!(raw instanceof Map<?, ?>)) {
            writeNetworkNodeLocation(params, field, pointNames, facilityId, raw);
            return;
        }
        Long id = locationIdOf(raw);
        String code = locationCodeOf(raw);
        String name = locationNameOf(raw);
        EntityRespDTO point = null;
        if (!StringUtils.hasText(name) && id != null) {
            point = optionalEntity(entityRpcApi.getEntity(id, POINT_TYPE));
            name = pointNameOf(point);
            if (point != null && point.getId() != null) {
                id = point.getId();
            }
        }
        if (!StringUtils.hasText(name) && StringUtils.hasText(code)) {
            point = optionalEntity(entityRpcApi.getEntityByCode(code.trim(), POINT_TYPE));
            name = pointNameOf(point);
            if (id == null && point != null) {
                id = point.getId();
            }
        }
        if (StringUtils.hasText(code) && StringUtils.hasText(name)) {
            pointNames.put(code, name);
        }
        if (id != null && StringUtils.hasText(name)) {
            pointNames.put(String.valueOf(id), name);
        }
        if (id != null && point == null) {
            point = optionalEntity(entityRpcApi.getEntity(id, POINT_TYPE));
        }
        if (StringUtils.hasText(name) || id != null || StringUtils.hasText(code)) {
            Map<String, Object> value = locationValue(id, code, name);
            copyNetworkNode(raw, point, value);
            copyExistingCoordinate(raw, value);
            copyPointCoordinates(point, value);
            params.put(field, value);
        }
    }

    /**
     * 起点、终点动作上带来的是路网节点编号。按点位业务编码读到这一条，格子里写点位编号和名称。
     * 读不到就保持原值，不把路网节点编号写成点位编码。
     */
    private void writeNetworkNodeLocation(
            Map<String, Object> params,
            String field,
            Map<String, String> pointNames,
            Long facilityId,
            Object raw
    ) {
        String nodeId = raw == null ? "" : String.valueOf(raw).trim();
        if (!StringUtils.hasText(nodeId) || "null".equalsIgnoreCase(nodeId)) {
            return;
        }
        EntityRespDTO point = loadPoint(facilityId, nodeId);
        boolean loadedByNetworkCode = point != null;
        if (point == null) {
            // 格子里写下的是点位编号。按编号读到这一条，路网节点用点位上已有的，不把编号当成路网节点。
            Long pointId = idOf(nodeId);
            if (pointId != null) {
                point = optionalEntity(entityRpcApi.getEntity(pointId, POINT_TYPE));
            }
        }
        if (point == null) {
            return;
        }
        Long id = point.getId();
        String name = pointNameOf(point);
        Map<String, Object> value = locationValue(id, null, name);
        if (loadedByNetworkCode) {
            copyNetworkNode(nodeId, value);
        } else {
            copyNetworkNode(firstText(bagOf(point).get("FLD-PNT-002")), value);
        }
        copyPointCoordinates(point, value);
        params.put(field, value);
        if (StringUtils.hasText(name)) {
            pointNames.put(nodeId, name);
            if (id != null) {
                pointNames.put(String.valueOf(id), name);
            }
        }
    }

    /**
     * 路网节点编号留给指令协议对接按字段库「路网节点」取值。没有这一项就不写，不猜别的编号。
     */
    private static void copyNetworkNode(Object raw, EntityRespDTO point, Map<String, Object> value) {
        String nodeId = null;
        if (raw instanceof Map<?, ?> map) {
            nodeId = firstText(map.get("FLD-PNT-002"), map.get(STATION_NODE_ID));
        }
        if (!StringUtils.hasText(nodeId) && point != null) {
            nodeId = firstText(bagOf(point).get("FLD-PNT-002"));
        }
        copyNetworkNode(nodeId, value);
    }

    /**
     * 检查项里已经写下的坐标先留下。点位上有、这一格还空着的项再补上，地面高度有就写上。
     * 点位没有的项不补，不另编一个高度。已经填了的经纬度不被点位盖掉。
     */
    @SuppressWarnings("unchecked")
    private static void copyExistingCoordinate(Object raw, Map<String, Object> value) {
        if (!(raw instanceof Map<?, ?> map)) {
            return;
        }
        Object loc = map.get("FLD-LOC-014");
        if (loc instanceof Map<?, ?> coordinates && !coordinates.isEmpty()) {
            value.put("FLD-LOC-014", new LinkedHashMap<>((Map<String, Object>) coordinates));
        }
    }

    @SuppressWarnings("unchecked")
    private static void copyPointCoordinates(EntityRespDTO point, Map<String, Object> value) {
        if (point == null) {
            return;
        }
        Object loc = bagOf(point).get("FLD-LOC-014");
        if (!(loc instanceof Map<?, ?> map) || map.isEmpty()) {
            return;
        }
        Object existing = value.get("FLD-LOC-014");
        Map<String, Object> coordinates = existing instanceof Map<?, ?> current
                ? new LinkedHashMap<>((Map<String, Object>) current)
                : new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() == null || !isFilledCoordinate(entry.getValue())) {
                continue;
            }
            String key = String.valueOf(entry.getKey());
            if (!isFilledCoordinate(coordinates.get(key))) {
                coordinates.put(key, entry.getValue());
            }
        }
        if (!coordinates.isEmpty()) {
            value.put("FLD-LOC-014", coordinates);
        }
    }

    private static boolean isFilledCoordinate(Object raw) {
        if (raw == null) {
            return false;
        }
        if (raw instanceof String text) {
            return StringUtils.hasText(text);
        }
        return true;
    }

    private static void copyNetworkNode(String nodeId, Map<String, Object> value) {
        if (!StringUtils.hasText(nodeId)) {
            return;
        }
        String trimmed = nodeId.trim();
        value.put("FLD-PNT-002", trimmed);
        value.put(STATION_NODE_ID, trimmed);
    }

    private static String firstText(Object... values) {
        if (values == null) {
            return null;
        }
        for (Object value : values) {
            if (value != null && StringUtils.hasText(String.valueOf(value))) {
                return String.valueOf(value).trim();
            }
        }
        return null;
    }

    /**
     * 点位在步骤参数里的一格。id 只写点位编号，code 只写调用方已经给出的业务编码，name 写名称。
     */
    private static Map<String, Object> locationValue(Long id, String code, String name) {
        Map<String, Object> value = new LinkedHashMap<>();
        if (id != null) {
            value.put("id", id);
        }
        if (StringUtils.hasText(code)) {
            value.put("code", code.trim());
        }
        if (StringUtils.hasText(name)) {
            value.put("name", name.trim());
        }
        return value;
    }

    /** 只认格子里的 id 字段或数字。一段路网节点编号不是点位编号。 */
    private static Long locationIdOf(Object raw) {
        if (raw instanceof Map<?, ?> obj) {
            return idOf(obj.get("id"));
        }
        if (raw instanceof Number) {
            return idOf(raw);
        }
        return null;
    }

    /** 只认格子里的 code 字段。不把 id 抄进 code，也不把一段纯文本当成业务编码。 */
    private static String locationCodeOf(Object raw) {
        if (!(raw instanceof Map<?, ?> obj)) {
            return null;
        }
        Object code = obj.get("code");
        if (code != null && StringUtils.hasText(String.valueOf(code))) {
            return String.valueOf(code).trim();
        }
        return null;
    }

    private static String locationNameOf(Object raw) {
        if (!(raw instanceof Map<?, ?> obj)) {
            return null;
        }
        Object name = obj.get("name");
        return name == null || !StringUtils.hasText(String.valueOf(name))
                ? null : String.valueOf(name).trim();
    }

    /**
     * 起点、终点按路网同步后的业务编码读到点位。返回的节点带上点位编号和名称，标题写成「起点 · 名称」。
     * refCode 仍是路网节点编号，算路和三维按它走。读不到点位时标题只留「起点」或「终点」。
     */
    @SuppressWarnings("unchecked")
    private void attachPointIdentity(Map<String, Object> tree, Long facilityId) {
        Object nodesRaw = tree.get("nodes");
        if (!(nodesRaw instanceof List<?> list)) {
            return;
        }
        for (Object raw : list) {
            if (!(raw instanceof Map<?, ?> row)) {
                continue;
            }
            Map<String, Object> node = (Map<String, Object>) row;
            if (!"point".equals(textOf(node.get("hangTypeCode")))) {
                continue;
            }
            String nodeId = textOf(node.get("refCode"));
            EntityRespDTO point = loadPoint(facilityId, nodeId);
            String name = pointNameOf(point);
            String head = anchorHead(textOf(node.get("title")));
            if (point == null || !StringUtils.hasText(name)) {
                if (StringUtils.hasText(head)) {
                    node.put("title", head);
                }
                continue;
            }
            if (point.getId() != null) {
                node.put("refId", point.getId());
            }
            node.put("name", name);
            if (StringUtils.hasText(head)) {
                node.put("title", head + " · " + name);
            }
        }
    }

    private static String anchorHead(String title) {
        if (title.startsWith("起点")) {
            return "起点";
        }
        if (title.startsWith("终点")) {
            return "终点";
        }
        return "";
    }

    /** 按点位业务编码读这一条。路网节点编号本身不是编码。 */
    private EntityRespDTO loadPoint(Long facilityId, String networkNodeId) {
        if (facilityId == null || facilityId <= 0 || !StringUtils.hasText(networkNodeId)) {
            return null;
        }
        String code = RoutePointCode.of(facilityId, networkNodeId.trim());
        return optionalEntity(entityRpcApi.getEntityByCode(code, POINT_TYPE));
    }

    private static String pointNameOf(EntityRespDTO point) {
        if (point == null) {
            return null;
        }
        Object name = bagOf(point).get("name");
        return name == null || !StringUtils.hasText(String.valueOf(name)) ? null : String.valueOf(name).trim();
    }

    private Optional<ActionDurationParamSupport.SlotDuration> durationOfAction(
            String actionCode, String means, Long equipmentModelId) {
        EntityRespDTO entity = loadAction(actionCode);
        if (entity == null) {
            return Optional.empty();
        }
        return ActionDurationParamSupport.readSlot(
                bagOf(entity).get("param_slots_json"), means, equipmentModelId, objectMapper);
    }

    private static Map<String, Object> paramsOf(Object raw) {
        if (raw instanceof Map<?, ?> map) {
            Map<String, Object> copy = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() != null) {
                    copy.put(String.valueOf(entry.getKey()), entry.getValue());
                }
            }
            return copy;
        }
        return new LinkedHashMap<>();
    }

    /**
     * 检查项挂在点位下时，设备编号写在检查项上，不另挂一层巡检对象。
     */
    private static Long equipmentIdOnItem(
            Map<String, Object> node,
            Map<String, Map<String, Object>> byKey
    ) {
        String parentKey = textOf(node.get("parentNodeKey"));
        int guard = 0;
        while (StringUtils.hasText(parentKey) && guard++ < byKey.size()) {
            Map<String, Object> parent = byKey.get(parentKey);
            if (parent == null) {
                return null;
            }
            if (TaskStepNode.HANG_INSPECTION_ITEM.equals(textOf(parent.get("hangTypeCode")))) {
                return idOf(parent.get("equipmentId"));
            }
            parentKey = textOf(parent.get("parentNodeKey"));
        }
        return null;
    }

    /**
     * 停靠这一步的标题写成点位名称。起点和终点不在这里改。
     * 名称取到达参数里已经写下的 name；没有 name 时用本次整理出的点位名称。编号不拿来当标题。
     */
    private static void nameVisitPoints(List<Map<String, Object>> nodes, Map<String, String> pointNames) {
        Map<String, Map<String, Object>> byKey = new LinkedHashMap<>();
        for (Map<String, Object> node : nodes) {
            String key = textOf(node.get("nodeKey"));
            if (StringUtils.hasText(key)) {
                byKey.put(key, node);
            }
        }
        for (Map<String, Object> node : nodes) {
            if (!TaskStepNode.HANG_POINT.equals(textOf(node.get("hangTypeCode")))) {
                continue;
            }
            String key = textOf(node.get("nodeKey"));
            if (!StringUtils.hasText(key) || "stop-start".equals(key) || "stop-end".equals(key)) {
                continue;
            }
            String name = "";
            Long id = null;
            for (Map<String, Object> action : nodes) {
                if (!TaskStepNode.HANG_ACTION.equals(textOf(action.get("hangTypeCode")))) {
                    continue;
                }
                if (!hangsUnder(action, key, byKey)) {
                    continue;
                }
                Map<String, Object> params = paramsOf(action.get("params"));
                Map<String, Object> place = locationMap(params.get(SELECT_LOCATION));
                if (place.isEmpty()) {
                    place = locationMap(params.get(LOCATION_REF));
                }
                if (place.isEmpty()) {
                    continue;
                }
                if (id == null) {
                    id = idOf(place.get("id"));
                }
                name = textOf(place.get("name"));
                if (!StringUtils.hasText(name) && id != null) {
                    name = pointNames.get(String.valueOf(id));
                }
                if (!StringUtils.hasText(name)) {
                    name = pointNames.get(textOf(place.get("code")));
                }
                if (StringUtils.hasText(name)) {
                    break;
                }
            }
            if (!StringUtils.hasText(name)) {
                name = pointNames.get(textOf(node.get("refCode")));
            }
            if (!StringUtils.hasText(name) && node.get("refId") != null) {
                name = pointNames.get(String.valueOf(node.get("refId")));
            }
            if (StringUtils.hasText(name)) {
                node.put("title", name);
            }
            if (id != null) {
                node.put("refId", id);
            }
        }
    }

    private static boolean hangsUnder(
            Map<String, Object> node,
            String ancestorKey,
            Map<String, Map<String, Object>> byKey
    ) {
        String parentKey = textOf(node.get("parentNodeKey"));
        int guard = 0;
        while (StringUtils.hasText(parentKey) && guard++ < byKey.size()) {
            if (ancestorKey.equals(parentKey)) {
                return true;
            }
            Map<String, Object> parent = byKey.get(parentKey);
            parentKey = parent == null ? "" : textOf(parent.get("parentNodeKey"));
        }
        return false;
    }

    private static Map<String, Object> locationMap(Object raw) {
        if (!(raw instanceof Map<?, ?> map)) {
            return Map.of();
        }
        Map<String, Object> copy = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() != null) {
                copy.put(String.valueOf(entry.getKey()), entry.getValue());
            }
        }
        return copy;
    }

    private static Long ancestorRefId(
            Map<String, Object> node,
            Map<String, Map<String, Object>> byKey,
            String hangType
    ) {
        String parentKey = textOf(node.get("parentNodeKey"));
        int guard = 0;
        while (StringUtils.hasText(parentKey) && guard++ < byKey.size()) {
            Map<String, Object> parent = byKey.get(parentKey);
            if (parent == null) {
                return null;
            }
            if (hangType.equals(textOf(parent.get("hangTypeCode")))) {
                return idOf(parent.get("refId"));
            }
            parentKey = textOf(parent.get("parentNodeKey"));
        }
        return null;
    }

    private static void writeDurationSums(
            List<Map<String, Object>> nodes,
            Map<String, Map<String, Object>> byKey,
            Map<String, BigDecimal> actionMinutes
    ) {
        if (actionMinutes.isEmpty()) {
            return;
        }
        Map<String, BigDecimal> sums = new LinkedHashMap<>();
        for (Map.Entry<String, BigDecimal> entry : actionMinutes.entrySet()) {
            Map<String, Object> action = byKey.get(entry.getKey());
            if (action == null) {
                continue;
            }
            String parentKey = textOf(action.get("parentNodeKey"));
            int guard = 0;
            while (StringUtils.hasText(parentKey) && guard++ < byKey.size()) {
                sums.merge(parentKey, entry.getValue(), BigDecimal::add);
                Map<String, Object> parent = byKey.get(parentKey);
                parentKey = parent == null ? "" : textOf(parent.get("parentNodeKey"));
            }
        }
        for (Map<String, Object> node : nodes) {
            String key = textOf(node.get("nodeKey"));
            BigDecimal minutes = sums.get(key);
            if (minutes == null) {
                continue;
            }
            Map<String, Object> params = paramsOf(node.get("params"));
            params.put(ActionDurationParamSupport.FIELD_CODE, ActionDurationParamSupport.plainMinutes(minutes));
            node.put("params", params);
        }
    }

    private static Long idOf(Object raw) {
        if (raw instanceof Number number && number.longValue() > 0) {
            return number.longValue();
        }
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                long value = Long.parseLong(text.trim());
                return value > 0 ? value : null;
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static String objectTitle(String equipmentName) {
        if (StringUtils.hasText(equipmentName)) {
            return equipmentName.trim();
        }
        throw ServiceExceptionUtil.invalidParamException("巡检对象缺少名称");
    }

    /** 巡检读动作只认业务编码。返回和打开这条动作相同的档案，含参数清单。 */
    private EntityRespDTO loadAction(String actionCode) {
        if (!StringUtils.hasText(actionCode)) {
            return null;
        }
        return optionalEntity(entityRpcApi.getEntityByCode(actionCode.trim(), ACTION_TYPE));
    }

    private static EntityRespDTO optionalEntity(CommonResult<EntityRespDTO> result) {
        if (result == null || !result.isSuccess() || result.getData() == null) {
            return null;
        }
        return result.getData();
    }

    /**
     * 头动作带下来的数，只留下这一步自己声明的槽。
     * 准备航线的高度、速度和结束方式这样写上。没声明的槽不带，避免把别的动作的位置写进来。
     */
    private Map<String, Object> paramsForLeaf(
            Map<String, Object> bag, Map<String, Object> inheritParams, String means, Long equipmentModelId
    ) {
        if (inheritParams == null || inheritParams.isEmpty()) {
            return Map.of();
        }
        List<String> declared = ActionParamSlotsSupport.fieldCodes(
                bag.get("param_slots_json"), means, equipmentModelId, objectMapper);
        if (declared.isEmpty()) {
            return Map.of();
        }
        Map<String, Object> kept = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : inheritParams.entrySet()) {
            if (entry.getValue() != null && declared.contains(entry.getKey())) {
                kept.put(entry.getKey(), entry.getValue());
            }
        }
        return kept;
    }

    /**
     * 检查步骤的名称读动作库里这个动作现在的名称。
     * 不读步骤上另存的标题。动作库没有名称时留空，不拿旧标题顶上。
     */
    private static String displayActionTitle(Map<String, Object> bag) {
        return textOf(bag.get("name"));
    }

    private static String textOf(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    /**
     * 这次任务要飞或要走的秒数。检查项动作耗时加路径耗时，两份都按秒。
     * 缺任何一份就空着，不拿距离估一个数，也不换成电量百分比。
     */
    private static BigDecimal requiredFlightSeconds(PatrolTaskDraft draft) {
        BigDecimal action = PatrolItemActionDurationSupport.minutesOf(draft.inspectionContent());
        Integer travel = storedTravelSeconds(draft.plannedRoute(), draft.patrolExecutionMode());
        if (action == null || travel == null || action.signum() < 0 || travel < 0) {
            return null;
        }
        return action.add(BigDecimal.valueOf(travel));
    }

    /** 路径耗时只读路线上已经写下的秒数。按巡检方式分档存放时，读这一档。 */
    private static Integer storedTravelSeconds(Object plannedRoute, String means) {
        Integer top = PatrolPlannedRouteSupport.estimatedTravelDuration(plannedRoute);
        if (top != null) {
            return top;
        }
        Map<String, Object> planned = PatrolPlannedRouteSupport.asPlannedMap(plannedRoute);
        if (planned == null || !StringUtils.hasText(means)) {
            return null;
        }
        Object slice = planned.get(means.trim());
        if (!(slice instanceof Map<?, ?> nested)) {
            return null;
        }
        return PatrolPlannedRouteSupport.estimatedTravelDuration(nested);
    }

    private static String protocolOf(PatrolTaskDraft draft) {
        if (draft.executionDeviceBinding() == null) {
            return null;
        }
        String code = draft.executionDeviceBinding().getProtocolCode();
        return StringUtils.hasText(code) ? code.trim() : null;
    }

    /**
     * 任务路线上的安全起飞高度、飞行速度、返航高度和飞完之后，写到准备航线这一步。
     * 任务上没记下的，调用前已经用这台无人机上的默认值补进同一份。这里不再另编一个数。
     * 回到机库写成 goHome，降落到指定点写成 autoLand。
     */
    private static Map<String, Object> missionParams(Map<String, Object> planned) {
        Map<String, Object> params = new LinkedHashMap<>();
        putText(params, "security_takeoff_height", plannedText(planned, "securityTakeoffHeight"));
        putText(params, "max_speed", plannedText(planned, "flightSpeed"));
        putText(params, "rth_altitude", plannedText(planned, "rthAltitude"));
        String finish = plannedText(planned, "waylineFinish");
        if ("goHome".equals(finish)) {
            params.put("finish_action", FINISH_GO_HOME);
        } else if (FINISH_LAND_AT_POINT.equals(finish)) {
            params.put("finish_action", "autoLand");
        }
        return params;
    }

    /**
     * 回到机库不另加降落步骤。降落到指定点时，任务终点作为最后一个航点。
     */
    private List<TaskStepTreeGenerator.MethodAction> landingTail(
            Map<String, Object> planned, String end, String means, Long equipmentModelId, String protocol
    ) {
        if (!FINISH_LAND_AT_POINT.equals(plannedText(planned, "waylineFinish")) || !StringUtils.hasText(end)) {
            return List.of();
        }
        return expandLibraryAction(
                WAYPOINT_ANCHOR,
                "飞向目标点",
                Map.of(SELECT_LOCATION, end.trim()),
                means,
                equipmentModelId,
                protocol);
    }

    /** 准备航线这一步已经带上的高度、速度和结束方式留下。核对参数时不把它们清成空。 */
    private static void keepFilled(Map<String, Object> merged, Map<String, Object> previous, String key) {
        if (previous == null || merged == null) {
            return;
        }
        Object kept = previous.get(key);
        if (kept == null || !StringUtils.hasText(String.valueOf(kept))) {
            return;
        }
        Object current = merged.get(key);
        if (current == null || !StringUtils.hasText(String.valueOf(current))) {
            merged.put(key, kept);
        }
    }

    private static void putText(Map<String, Object> params, String key, String value) {
        if (StringUtils.hasText(value)) {
            params.put(key, value.trim());
        }
    }

    private static String firstText(String preferred, String fallback) {
        if (StringUtils.hasText(preferred)) {
            return preferred.trim();
        }
        return StringUtils.hasText(fallback) ? fallback.trim() : null;
    }

    private Map<String, Object> plannedMap(Object plannedRoute) {
        if (plannedRoute instanceof Map<?, ?> map) {
            Map<String, Object> copied = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() != null) {
                    copied.put(String.valueOf(entry.getKey()), entry.getValue());
                }
            }
            return copied;
        }
        if (plannedRoute instanceof String json && StringUtils.hasText(json)) {
            try {
                return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {
                });
            } catch (Exception ex) {
                throw ServiceExceptionUtil.invalidParamException("已保存路线读不出起点终点");
            }
        }
        return Map.of();
    }

    private static String plannedText(Map<String, Object> planned, String key) {
        if (planned == null) {
            return null;
        }
        Object raw = planned.get(key);
        return raw == null ? null : String.valueOf(raw);
    }

    private static List<TaskStepTreeGenerator.SelectedEquipment> selectedOf(InspectionContent content) {
        List<InspectionContent.ObjectContent> objects = allObjects(content);
        if (objects.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("任务草稿还没有勾选被巡检设备和检查项");
        }
        List<TaskStepTreeGenerator.SelectedEquipment> selected = new ArrayList<>();
        for (InspectionContent.ObjectContent object : objects) {
            List<Long> itemIds = new ArrayList<>();
            if (object.getItems() != null) {
                for (InspectionContent.ItemContent item : object.getItems()) {
                    if (item.getItemId() != null) {
                        itemIds.add(item.getItemId());
                    }
                }
            }
            selected.add(new TaskStepTreeGenerator.SelectedEquipment(
                    object.getObjectId(), itemIds, object.getObjectName()));
        }
        return selected;
    }

    /**
     * 检查项名称先取任务草稿里用户看到的名字，读实体时再补空白。
     * 模板组和自行添加的对象都要收，漏一组树上就没有检查项名。
     */
    private static Map<Long, String> itemNamesOf(InspectionContent content) {
        Map<Long, String> names = new LinkedHashMap<>();
        for (InspectionContent.ObjectContent object : allObjects(content)) {
            if (object.getItems() == null) {
                continue;
            }
            for (InspectionContent.ItemContent item : object.getItems()) {
                if (item.getItemId() == null || !StringUtils.hasText(item.getItemName())) {
                    continue;
                }
                names.put(item.getItemId(), item.getItemName().trim());
            }
        }
        return names;
    }

    private static List<InspectionContent.ObjectContent> allObjects(InspectionContent content) {
        List<InspectionContent.ObjectContent> objects = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        collectObjects(content == null ? null : content.getCustomObjects(), objects, seen);
        if (content != null && content.getGroups() != null) {
            for (InspectionContent.ObjectGroup group : content.getGroups()) {
                collectObjects(group == null ? null : group.getObjects(), objects, seen);
            }
        }
        return objects;
    }

    private static void collectObjects(
            List<InspectionContent.ObjectContent> source,
            List<InspectionContent.ObjectContent> sink,
            Set<Long> seen
    ) {
        if (source == null) {
            return;
        }
        for (InspectionContent.ObjectContent object : source) {
            if (object == null || object.getObjectId() == null || !seen.add(object.getObjectId())) {
                continue;
            }
            sink.add(object);
        }
    }

    private static void putItemNameIfAbsent(Map<Long, String> itemNames, Long itemId, Map<String, Object> bag) {
        if (itemNames.containsKey(itemId) && StringUtils.hasText(itemNames.get(itemId))) {
            return;
        }
        Object name = bag.get("name");
        if (name == null) {
            name = bag.get("item_name");
        }
        if (name == null || !StringUtils.hasText(String.valueOf(name))) {
            return;
        }
        itemNames.put(itemId, String.valueOf(name).trim());
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
