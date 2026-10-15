package cn.cheers.x.module.dynamicbusiness.service.emergencycommand;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandAssessReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandCompleteStepReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandDetailRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandEventRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandFromAlertReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandLevelRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandPlanRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandStartReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandStageRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandStepNodeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandStepRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntitySceneQueryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.FieldFilterReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryScene;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeStepStructureProvisionService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.FacilityOwningFieldEnsureService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 应急指挥写入实现。权威是数据目录里的应急事件、应急预案、应急响应。
 *
 * 告警转入只写事件。研判只改这起事件。启动用事件上的类型和级别写成一条响应。
 * 禁止在查询列表时补型号或补预案。
 */
@Service
public class EmergencyCommandServiceImpl implements EmergencyCommandService {

    private static final int MATCH_PAGE_SIZE = 20;

    @Resource
    private EntityService entityService;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private ModelService modelService;
    @Resource
    private EntityTypeStepStructureProvisionService stepStructureProvisionService;
    @Resource
    private FacilityOwningFieldEnsureService facilityOwningFieldEnsureService;

    @Override
    public PageResult<EmergencyCommandEventRespVO> pageEvents(
            String eventStatus, String eventType, Integer pageNo, Integer pageSize) {
        List<FieldFilterReqVO> filters = new ArrayList<>();
        if (StringUtils.hasText(eventStatus)) {
            filters.add(eq(EmergencyCommandFields.EVENT_STATUS, eventStatus.trim()));
        }
        if (StringUtils.hasText(eventType)) {
            filters.add(eq(EmergencyCommandFields.EVENT_TYPE, eventType.trim()));
        }
        EntitySceneQueryRespVO queried = query(
                EmergencyCommandFields.EVENT_CATALOG, filters, pageNo, pageSize);
        PageResult<EntityRespVO> page = queried.getPage();
        List<EmergencyCommandEventRespVO> rows = new ArrayList<>();
        if (page != null && page.getList() != null) {
            for (EntityRespVO entity : page.getList()) {
                rows.add(toEvent(entity));
            }
        }
        long total = page == null || page.getTotal() == null ? rows.size() : page.getTotal();
        return new PageResult<>(rows, total);
    }

    @Override
    public EmergencyCommandEventRespVO getEvent(String eventId) {
        return toEvent(requireEvent(eventId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String openFromAlert(EmergencyCommandFromAlertReqVO reqVO) {
        ensureWritableModel(EmergencyCommandFields.EVENT_CATALOG, "应急事件");
        ensureWritableModel(EmergencyCommandFields.PLAN_CATALOG, "应急预案");
        ensureWritableModel(EmergencyCommandFields.RESPONSE_CATALOG, "应急响应");
        stepStructureProvisionService.ensureProvisioned(EmergencyCommandFields.PLAN_CATALOG, null, null);
        stepStructureProvisionService.ensureProvisioned(EmergencyCommandFields.RESPONSE_CATALOG, null, null);
        String alertId = reqVO.getAlertId().trim();
        List<EntityRespVO> existing = match(
                EmergencyCommandFields.EVENT_CATALOG,
                List.of(eq(EmergencyCommandFields.ALERT_ID, alertId)));
        if (existing.size() > 1) {
            throw new ServiceException(400, "同一告警对应了多起应急事件");
        }
        if (existing.size() == 1) {
            throw new ServiceException(400, "该告警已经转入应急事件");
        }
        Map<String, Object> custom = new LinkedHashMap<>();
        custom.put(EmergencyCommandFields.ALERT_ID, alertId);
        custom.put(EmergencyCommandFields.EVENT_STATUS, EmergencyCommandFields.STATUS_PENDING);
        custom.put(EmergencyCommandFields.TRIGGER_TIME,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
        Long id = entityService.create(newEntity(
                EmergencyCommandFields.EVENT_CATALOG, reqVO.getName().trim(), custom, null));
        return String.valueOf(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assess(EmergencyCommandAssessReqVO reqVO) {
        EntityRespVO event = requireEvent(reqVO.getEventId());
        if (!EmergencyCommandFields.STATUS_PENDING.equals(text(field(event, EmergencyCommandFields.EVENT_STATUS)))) {
            throw new ServiceException(400, "只有待研判的事件可以研判");
        }
        Map<String, Object> custom = copyCustom(event);
        custom.put(EmergencyCommandFields.JUDGED_REAL, reqVO.getReal());
        custom.put(EmergencyCommandFields.JUDGE_COMMENT, text(reqVO.getComment()));
        custom.put(EmergencyCommandFields.ASSESS_TIME,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
        if (Boolean.TRUE.equals(reqVO.getReal())) {
            if (!StringUtils.hasText(reqVO.getEventType()) || !StringUtils.hasText(reqVO.getResponseLevel())) {
                throw new ServiceException(400, "成立时必须填写事件类型和响应级别");
            }
            custom.put(EmergencyCommandFields.EVENT_TYPE, reqVO.getEventType().trim());
            custom.put(EmergencyCommandFields.RESPONSE_LEVEL, reqVO.getResponseLevel().trim());
            if (StringUtils.hasText(reqVO.getPlanId())) {
                EntityRespVO plan = resolvePlan(reqVO.getEventType().trim(), reqVO.getPlanId());
                custom.put(EmergencyCommandFields.PLAN_ID, String.valueOf(plan.getId()));
            }
            custom.put(EmergencyCommandFields.EVENT_STATUS, EmergencyCommandFields.STATUS_READY);
        } else {
            custom.put(EmergencyCommandFields.EVENT_STATUS, EmergencyCommandFields.STATUS_REJECTED);
        }
        save(event, EmergencyCommandFields.EVENT_CATALOG, custom);
    }

    /**
     * 启动响应：只在已经研判成立、处于预警的事件上，按当时选定的预案和级别生成响应。
     * 不改研判时间。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String start(EmergencyCommandStartReqVO reqVO) {
        EntityRespVO event = requireEvent(reqVO.getEventId());
        String status = text(field(event, EmergencyCommandFields.EVENT_STATUS));
        if (!EmergencyCommandFields.STATUS_READY.equals(status)) {
            throw new ServiceException(400, "只有研判成立、处于预警的事件可以启动响应");
        }
        String eventType = text(field(event, EmergencyCommandFields.EVENT_TYPE));
        String level = text(field(event, EmergencyCommandFields.RESPONSE_LEVEL));
        String planId = text(field(event, EmergencyCommandFields.PLAN_ID));
        if (!StringUtils.hasText(eventType) || !StringUtils.hasText(level)) {
            throw new ServiceException(400, "研判时没有选定事件类型和级别");
        }
        Long facilityId = parseId(reqVO.getFacilityId(), "场站编号不正确");
        prepareResponseCatalog();
        EntityRespVO plan = resolvePlan(eventType, planId);
        JSONObject steps = EmergencyCommandSteps.copyLevel(field(plan, EmergencyCommandFields.STEP_TREE), level);
        if (steps == null) {
            throw new ServiceException(400, "该级别没有步骤");
        }
        assertNoResponse(event.getId(), facilityId);
        Map<String, Object> eventCustom = copyCustom(event);
        eventCustom.put(EmergencyCommandFields.EVENT_STATUS, EmergencyCommandFields.STATUS_RESPONDING);
        Map<String, Object> custom = new LinkedHashMap<>();
        custom.put(EmergencyCommandFields.EVENT_ID, String.valueOf(event.getId()));
        custom.put(EmergencyCommandFields.PLAN_ID, String.valueOf(plan.getId()));
        custom.put(EmergencyCommandFields.RESPONSE_LEVEL, level);
        custom.put(EmergencyCommandFields.RESPONSE_STATUS, EmergencyCommandFields.RESPONSE_RUNNING);
        custom.put(EmergencyCommandFields.STEP_TREE, steps);
        String responseName = text(event.getName()) + " " + level + "级响应";
        Long responseId = entityService.create(newEntity(
                EmergencyCommandFields.RESPONSE_CATALOG, responseName, custom, facilityId));
        save(event, EmergencyCommandFields.EVENT_CATALOG, eventCustom);
        return String.valueOf(responseId);
    }

    @Override
    public EmergencyCommandDetailRespVO detail(String eventId, String facilityId) {
        EntityRespVO event = requireEvent(eventId);
        EmergencyCommandDetailRespVO detail = new EmergencyCommandDetailRespVO();
        detail.setEvent(toEvent(event));
        detail.setSteps(List.of());
        detail.setLevels(List.of());
        Long facility = parseId(facilityId, "场站编号不正确");
        List<EntityRespVO> responses = match(
                EmergencyCommandFields.RESPONSE_CATALOG,
                List.of(
                        eq(EmergencyCommandFields.EVENT_ID, String.valueOf(event.getId())),
                        eq(FacilityOwningFieldCodes.FIELD_CODE, facility)));
        Object responseSteps = null;
        String startedLevel = null;
        if (responses.size() > 1) {
            throw new ServiceException(400, "这起事件有多条响应");
        }
        if (responses.size() == 1) {
            EntityRespVO response = responses.get(0);
            detail.setResponseId(String.valueOf(response.getId()));
            detail.setPlanId(text(field(response, EmergencyCommandFields.PLAN_ID)));
            startedLevel = text(field(response, EmergencyCommandFields.RESPONSE_LEVEL));
            detail.setResponseLevel(startedLevel);
            detail.setStartedAt(response.getCreateTime() == null ? "" : response.getCreateTime().toString());
            responseSteps = field(response, EmergencyCommandFields.STEP_TREE);
            detail.setSteps(readSteps(responseSteps));
        }
        if (!StringUtils.hasText(detail.getPlanId())) {
            detail.setPlanId(text(field(event, EmergencyCommandFields.PLAN_ID)));
        }
        detail.setLevels(readLevels(event, responseSteps, startedLevel));
        detail.setStages(readStages(event, detail.getPlanId()));
        fillPlans(detail, event);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeStep(EmergencyCommandCompleteStepReqVO reqVO) {
        EntityRespVO event = requireEvent(reqVO.getEventId());
        Long facilityId = parseId(reqVO.getFacilityId(), "场站编号不正确");
        List<EntityRespVO> responses = match(
                EmergencyCommandFields.RESPONSE_CATALOG,
                List.of(
                        eq(EmergencyCommandFields.EVENT_ID, String.valueOf(event.getId())),
                        eq(FacilityOwningFieldCodes.FIELD_CODE, facilityId)));
        if (responses.size() != 1) {
            throw new ServiceException(400, "这起事件还没有唯一的响应");
        }
        EntityRespVO response = responses.get(0);
        JSONObject updated = EmergencyCommandSteps.complete(
                field(response, EmergencyCommandFields.STEP_TREE), reqVO.getNodeKey().trim());
        if (updated == null) {
            throw new ServiceException(400, "步骤不存在");
        }
        Map<String, Object> custom = copyCustom(response);
        custom.put(EmergencyCommandFields.STEP_TREE, updated);
        save(response, EmergencyCommandFields.RESPONSE_CATALOG, custom);
    }

    /**
     * 把响应上的这一步标成正在执行。仍写这一份步骤，不另开响应。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startStep(EmergencyCommandCompleteStepReqVO reqVO) {
        EntityRespVO event = requireEvent(reqVO.getEventId());
        Long facilityId = parseId(reqVO.getFacilityId(), "场站编号不正确");
        List<EntityRespVO> responses = match(
                EmergencyCommandFields.RESPONSE_CATALOG,
                List.of(
                        eq(EmergencyCommandFields.EVENT_ID, String.valueOf(event.getId())),
                        eq(FacilityOwningFieldCodes.FIELD_CODE, facilityId)));
        if (responses.size() != 1) {
            throw new ServiceException(400, "这起事件还没有唯一的响应");
        }
        EntityRespVO response = responses.get(0);
        JSONObject updated = EmergencyCommandSteps.begin(
                field(response, EmergencyCommandFields.STEP_TREE), reqVO.getNodeKey().trim());
        if (updated == null) {
            throw new ServiceException(400, "步骤不存在");
        }
        Map<String, Object> custom = copyCustom(response);
        custom.put(EmergencyCommandFields.STEP_TREE, updated);
        save(response, EmergencyCommandFields.RESPONSE_CATALOG, custom);
    }

    /**
     * 启动写入响应之前补上步骤列和所属场站列。查询指挥页时不再补。
     */
    private void prepareResponseCatalog() {
        stepStructureProvisionService.ensureProvisioned(EmergencyCommandFields.PLAN_CATALOG, null, null);
        stepStructureProvisionService.ensureProvisioned(EmergencyCommandFields.RESPONSE_CATALOG, null, null);
        facilityOwningFieldEnsureService.ensureForEntityTypeCode(EmergencyCommandFields.RESPONSE_CATALOG);
    }

    /**
     * 指挥页按类型读出每一级步骤。没有唯一生效预案时不猜，级别列表为空。
     */
    private List<EmergencyCommandLevelRespVO> readLevels(
            EntityRespVO event, Object responseSteps, String startedLevel) {
        String eventType = text(field(event, EmergencyCommandFields.EVENT_TYPE));
        Object planSteps = null;
        if (StringUtils.hasText(eventType)) {
            EntityRespVO plan = findSinglePlan(eventType);
            if (plan != null) {
                planSteps = field(plan, EmergencyCommandFields.STEP_TREE);
            }
        }
        List<EmergencyCommandLevelRespVO> levels = new ArrayList<>();
        for (EmergencyCommandSteps.LevelNodes level : EmergencyCommandSteps.listLevels(
                planSteps, responseSteps, startedLevel)) {
            EmergencyCommandLevelRespVO row = new EmergencyCommandLevelRespVO();
            row.setLevel(level.level());
            List<EmergencyCommandStepNodeRespVO> nodes = new ArrayList<>();
            for (EmergencyCommandSteps.StepNode node : level.nodes()) {
                EmergencyCommandStepNodeRespVO item = new EmergencyCommandStepNodeRespVO();
                item.setNodeKey(node.nodeKey());
                item.setParentNodeKey(node.parentNodeKey());
                item.setDescription(node.description());
                item.setOrder(node.order());
                item.setRunStatus(node.runStatus());
                item.setCompletedAt(node.completedAt());
                item.setStartedAt(node.startedAt());
                item.setStageKey(node.stageKey());
                nodes.add(item);
            }
            row.setNodes(nodes);
            levels.add(row);
        }
        return levels;
    }

    /**
     * 阶段名称来自预案上用户写的清单。已启动时读这一份预案，不按中文名猜测。
     */
    private List<EmergencyCommandStageRespVO> readStages(EntityRespVO event, String planId) {
        Object planSteps = null;
        if (StringUtils.hasText(planId)) {
            EntityRespVO plan = entityService.get(parseId(planId, "预案编号不正确"), EmergencyCommandFields.PLAN_CATALOG);
            if (plan != null) {
                planSteps = field(plan, EmergencyCommandFields.STEP_TREE);
            }
        }
        if (planSteps == null) {
            String eventType = text(field(event, EmergencyCommandFields.EVENT_TYPE));
            if (StringUtils.hasText(eventType)) {
                EntityRespVO plan = findSinglePlan(eventType);
                if (plan != null) {
                    planSteps = field(plan, EmergencyCommandFields.STEP_TREE);
                }
            }
        }
        List<EmergencyCommandStageRespVO> stages = new ArrayList<>();
        for (EmergencyCommandSteps.StageDef stage : EmergencyCommandSteps.listStages(planSteps)) {
            EmergencyCommandStageRespVO row = new EmergencyCommandStageRespVO();
            row.setStageKey(stage.stageKey());
            row.setName(stage.name());
            stages.add(row);
        }
        return stages;
    }

    private void fillPlans(EmergencyCommandDetailRespVO detail, EntityRespVO event) {
        String eventType = text(field(event, EmergencyCommandFields.EVENT_TYPE));
        List<EmergencyCommandPlanRespVO> plans = new ArrayList<>();
        if (StringUtils.hasText(eventType)) {
            for (EntityRespVO plan : effectivePlans(eventType)) {
                plans.add(toPlan(plan));
            }
        }
        if (StringUtils.hasText(detail.getPlanId())) {
            String name = "";
            for (EmergencyCommandPlanRespVO plan : plans) {
                if (detail.getPlanId().equals(plan.getId())) {
                    name = text(plan.getName());
                    break;
                }
            }
            if (!StringUtils.hasText(name)) {
                Long planId = parseId(detail.getPlanId(), "预案编号不正确");
                EntityRespVO plan = entityService.get(planId, EmergencyCommandFields.PLAN_CATALOG);
                if (plan != null) {
                    name = text(plan.getName());
                    plans.add(0, toPlan(plan));
                }
            }
            detail.setPlanName(name);
        } else if (plans.size() == 1) {
            detail.setPlanId(plans.get(0).getId());
            detail.setPlanName(plans.get(0).getName());
        }
        detail.setPlans(plans);
    }

    private EntityRespVO resolvePlan(String eventType, String planId) {
        if (!StringUtils.hasText(planId)) {
            return requireSinglePlan(eventType);
        }
        EntityRespVO plan = entityService.get(parseId(planId, "预案编号不正确"), EmergencyCommandFields.PLAN_CATALOG);
        if (plan == null) {
            throw new ServiceException(404, "应急预案不存在");
        }
        if (!eventType.equals(text(field(plan, EmergencyCommandFields.EVENT_TYPE)))) {
            throw new ServiceException(400, "这份预案不属于当前事件类型");
        }
        if (!EmergencyCommandFields.PLAN_EFFECTIVE.equals(text(field(plan, EmergencyCommandFields.PLAN_STATUS)))) {
            throw new ServiceException(400, "这份预案还没有生效");
        }
        return plan;
    }

    private List<EntityRespVO> effectivePlans(String eventType) {
        return match(
                EmergencyCommandFields.PLAN_CATALOG,
                List.of(
                        eq(EmergencyCommandFields.EVENT_TYPE, eventType),
                        eq(EmergencyCommandFields.PLAN_STATUS, EmergencyCommandFields.PLAN_EFFECTIVE)));
    }

    private static EmergencyCommandPlanRespVO toPlan(EntityRespVO plan) {
        EmergencyCommandPlanRespVO item = new EmergencyCommandPlanRespVO();
        item.setId(String.valueOf(plan.getId()));
        item.setName(text(plan.getName()));
        return item;
    }

    private EntityRespVO findSinglePlan(String eventType) {
        List<EntityRespVO> plans = match(
                EmergencyCommandFields.PLAN_CATALOG,
                List.of(
                        eq(EmergencyCommandFields.EVENT_TYPE, eventType),
                        eq(EmergencyCommandFields.PLAN_STATUS, EmergencyCommandFields.PLAN_EFFECTIVE)));
        if (plans.size() != 1) {
            return null;
        }
        return plans.get(0);
    }

    private EntityRespVO requireSinglePlan(String eventType) {
        List<EntityRespVO> plans = match(
                EmergencyCommandFields.PLAN_CATALOG,
                List.of(
                        eq(EmergencyCommandFields.EVENT_TYPE, eventType),
                        eq(EmergencyCommandFields.PLAN_STATUS, EmergencyCommandFields.PLAN_EFFECTIVE)));
        if (plans.isEmpty()) {
            throw new ServiceException(400, "该类型没有生效预案");
        }
        if (plans.size() > 1) {
            throw new ServiceException(400, "该类型有多份生效预案，不能启动");
        }
        return plans.get(0);
    }

    private void assertNoResponse(Long eventId, Long facilityId) {
        List<EntityRespVO> responses = match(
                EmergencyCommandFields.RESPONSE_CATALOG,
                List.of(
                        eq(EmergencyCommandFields.EVENT_ID, String.valueOf(eventId)),
                        eq(FacilityOwningFieldCodes.FIELD_CODE, facilityId)));
        if (!responses.isEmpty()) {
            throw new ServiceException(400, "这起事件已经启动过响应");
        }
    }

    private EntityRespVO requireEvent(String eventId) {
        Long id = parseId(eventId, "事件编号不正确");
        EntityRespVO event = entityService.get(id, EmergencyCommandFields.EVENT_CATALOG);
        if (event == null) {
            throw new ServiceException(404, "应急事件不存在");
        }
        return event;
    }

    /**
     * 告警转入时补齐还能写入的唯一型号。已经有一个则沿用。多于一个则拒绝。
     */
    private void ensureWritableModel(String catalog, String catalogName) {
        List<ModelDO> models = modelMapper.selectByEntityTypeCode(catalog);
        if (models != null && models.size() > 1) {
            throw new ServiceException(400, catalogName + "有多个型号，不能确定写入哪一个");
        }
        if (models != null && models.size() == 1) {
            return;
        }
        ModelCreateReqVO create = new ModelCreateReqVO();
        create.setName(catalogName);
        create.setEntityTypeCode(catalog);
        create.setStatus(1);
        create.setGovernanceStatus("COMPANY");
        modelService.createModel(create);
    }

    private Long writableModelId(String catalog, String catalogName) {
        List<ModelDO> models = modelMapper.selectByEntityTypeCode(catalog);
        if (models == null || models.size() != 1) {
            throw new ServiceException(400, catalogName + "没有唯一型号，请先从告警转入");
        }
        return models.get(0).getId();
    }

    private EntityCreateReqVO newEntity(
            String catalog, String name, Map<String, Object> custom, Long facilityId) {
        String catalogName = EmergencyCommandFields.EVENT_CATALOG.equals(catalog) ? "应急事件"
                : EmergencyCommandFields.PLAN_CATALOG.equals(catalog) ? "应急预案" : "应急响应";
        Map<String, Object> base = new LinkedHashMap<>();
        base.put("entityTypeCode", catalog);
        base.put("modelId", writableModelId(catalog, catalogName));
        base.put("name", name);
        base.put("status", 1);
        if (facilityId != null) {
            base.put(FacilityOwningFieldCodes.FIELD_CODE, facilityId);
        }
        EntityCreateReqVO req = new EntityCreateReqVO();
        req.setRegistryCode(catalog);
        req.setBaseFields(base);
        req.setCustomFields(custom);
        return req;
    }

    private void save(EntityRespVO current, String catalog, Map<String, Object> custom) {
        Map<String, Object> base = new LinkedHashMap<>();
        if (current.getBaseFields() != null) {
            base.putAll(current.getBaseFields());
        }
        base.put("entityTypeCode", catalog);
        if (base.get("modelId") == null) {
            base.put("modelId", current.getModelId());
        }
        EntityUpdateReqVO req = new EntityUpdateReqVO();
        req.setId(current.getId());
        req.setRegistryCode(catalog);
        req.setBaseFields(base);
        req.setCustomFields(custom);
        entityService.update(req);
    }

    private List<EntityRespVO> match(String catalog, List<FieldFilterReqVO> filters) {
        EntitySceneQueryRespVO queried = query(catalog, filters, 1, MATCH_PAGE_SIZE);
        PageResult<EntityRespVO> page = queried.getPage();
        List<EntityRespVO> list = page == null || page.getList() == null ? List.of() : page.getList();
        long total = page == null || page.getTotal() == null ? list.size() : page.getTotal();
        if (total > list.size()) {
            throw new ServiceException(400, "匹配记录超过一页，不能确定唯一结果");
        }
        return list;
    }

    private EntitySceneQueryRespVO query(
            String catalog, List<FieldFilterReqVO> filters, Integer pageNo, Integer pageSize) {
        return entityService.queryEntities(
                EntityQueryScene.ENTITIES_BY_MODEL,
                "PAGE",
                "FULL",
                null,
                catalog,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                pageNo,
                pageSize,
                null,
                null,
                filters,
                null,
                null,
                null,
                null,
                null);
    }

    private EmergencyCommandEventRespVO toEvent(EntityRespVO entity) {
        EmergencyCommandEventRespVO row = new EmergencyCommandEventRespVO();
        row.setId(String.valueOf(entity.getId()));
        row.setName(entity.getName());
        row.setAlertId(text(field(entity, EmergencyCommandFields.ALERT_ID)));
        row.setEventStatus(text(field(entity, EmergencyCommandFields.EVENT_STATUS)));
        Object real = field(entity, EmergencyCommandFields.JUDGED_REAL);
        if (real instanceof Boolean judged) {
            row.setJudgedReal(judged);
        }
        row.setEventType(text(field(entity, EmergencyCommandFields.EVENT_TYPE)));
        row.setResponseLevel(text(field(entity, EmergencyCommandFields.RESPONSE_LEVEL)));
        row.setJudgeComment(text(field(entity, EmergencyCommandFields.JUDGE_COMMENT)));
        row.setAssessTime(text(field(entity, EmergencyCommandFields.ASSESS_TIME)));
        row.setTriggerTime(text(field(entity, EmergencyCommandFields.TRIGGER_TIME)));
        row.setCreateTime(entity.getCreateTime() == null ? "" : entity.getCreateTime().toString());
        return row;
    }

    private List<EmergencyCommandStepRespVO> readSteps(Object raw) {
        JSONObject pack = asPack(raw);
        List<EmergencyCommandStepRespVO> steps = new ArrayList<>();
        if (pack == null) {
            return steps;
        }
        JSONArray methods = pack.getJSONArray("methods");
        if (methods == null || methods.isEmpty()) {
            return steps;
        }
        JSONObject method = methods.getJSONObject(0);
        if (method != null) {
            collectSteps(method.getJSONArray("stepTree"), steps);
        }
        return steps;
    }

    private void collectSteps(JSONArray nodes, List<EmergencyCommandStepRespVO> steps) {
        if (nodes == null) {
            return;
        }
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            if (node == null || node.get("nodeKey") == null) {
                continue;
            }
            EmergencyCommandStepRespVO step = new EmergencyCommandStepRespVO();
            step.setNodeKey(text(node.get("nodeKey")));
            String description = text(node.get("stepDescription"));
            step.setDescription(description.isEmpty() ? step.getNodeKey() : description);
            String runStatus = text(node.get(EmergencyCommandFields.STEP_RUN_STATUS));
            step.setRunStatus(runStatus.isEmpty() ? EmergencyCommandFields.STEP_OPEN : runStatus);
            steps.add(step);
            collectSteps(node.getJSONArray("children"), steps);
        }
    }

    private static JSONObject asPack(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof String text) {
            return text.isBlank() ? null : JSONObject.parseObject(text);
        }
        return JSONObject.parseObject(com.alibaba.fastjson2.JSON.toJSONString(raw));
    }

    private static Map<String, Object> copyCustom(EntityRespVO entity) {
        Map<String, Object> custom = new LinkedHashMap<>();
        if (entity.getCustomFields() != null) {
            custom.putAll(entity.getCustomFields());
        }
        return custom;
    }

    private static Object field(EntityRespVO entity, String code) {
        if (entity.getCustomFields() != null && entity.getCustomFields().get(code) != null) {
            return entity.getCustomFields().get(code);
        }
        if (entity.getBaseFields() != null) {
            return entity.getBaseFields().get(code);
        }
        return null;
    }

    private static FieldFilterReqVO eq(String fieldCode, Object value) {
        FieldFilterReqVO filter = new FieldFilterReqVO();
        filter.setFieldCode(fieldCode);
        filter.setOp("EQ");
        filter.setValue(value);
        return filter;
    }

    private static Long parseId(String raw, String message) {
        if (!StringUtils.hasText(raw)) {
            throw new ServiceException(400, message);
        }
        try {
            return Long.valueOf(raw.trim());
        } catch (NumberFormatException ex) {
            throw new ServiceException(400, message);
        }
    }

    private static String text(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }
}
