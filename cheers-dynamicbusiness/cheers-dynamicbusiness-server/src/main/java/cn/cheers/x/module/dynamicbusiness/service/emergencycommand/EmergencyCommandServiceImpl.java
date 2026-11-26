package cn.cheers.x.module.dynamicbusiness.service.emergencycommand;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandAssessReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandChangeLevelReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandCloseReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandCompleteStepReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandDetailRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandEventRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandFromAlertReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandIntakeReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandJudgementReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandLevelRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandLocateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandRenameReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandReviseIntakeReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyOwnershipFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandPlanLevelRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandPlanRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandStartRuleRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandStartReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandSubmitReportReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandTimelineRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandStageRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandStepNodeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandStepRespVO;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
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
import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogOwnershipQueryService;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.DataScopeQueryService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRpcFacadeService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRpcFacadeService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeStepStructureProvisionService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 应急指挥写入实现。步骤正文在预案上。这起事件只记选定的预案、当前级别和已经发生的事。
 *
 * 研判把预案编号写到事件上。启动读这个编号，不复制步骤。
 * 完成、上报、变级、结案都追加到事件脉络。变级不清掉原级别的记录。结案只改这起事件的结案状态。
 * 禁止按事件分类另找预案，也禁止在查询时补一份预案。
 *
 * 事件的所属场站、所属管线段和中心点只在接报、转入、补位置时写。研判、启动、变级、结案不改它们，
 * 也不读右上角选的站。每次按编号取事件都先核对登录人看得到这起事件。
 */
@Service
public class EmergencyCommandServiceImpl implements EmergencyCommandService {

    private static final int MATCH_PAGE_SIZE = 20;

    @Resource
    private EntityService entityService;
    @Resource
    private EntityRpcFacadeService entityRpcFacadeService;
    @Resource
    private CatalogOwnershipQueryService catalogOwnershipQueryService;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private ModelService modelService;
    @Resource
    private EntityTypeStepStructureProvisionService stepStructureProvisionService;
    @Resource
    private DataScopeQueryService dataScopeQueryService;
    @Resource
    private EmergencyCommandOrgService emergencyCommandOrgService;

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

    /**
     * 接报弹窗的归属项。只读当前租户对应急事件目录的声明。
     * 这个租户没声明时返回空列表，页面不画场站，也不画管线。
     */
    @Override
    public List<EmergencyOwnershipFieldRespVO> listOwnershipFields() {
        List<EmergencyOwnershipFieldRespVO> fields = new ArrayList<>();
        for (CatalogOwnershipQueryService.OwnershipDeclarationView view
                : catalogOwnershipQueryService.listDirectDeclarations(EmergencyCommandFields.EVENT_CATALOG)) {
            EmergencyOwnershipFieldRespVO field = new EmergencyOwnershipFieldRespVO();
            field.setFieldCode(view.hop().fieldCode());
            field.setLabel(view.hop().fieldName());
            field.setTargetRegistryCode(view.hop().targetRegistryCode());
            field.setRequired(view.required());
            fields.add(field);
        }
        return fields;
    }

    /**
     * 人工接报。不写告警编号。事件停在待研判，等人选定预案。
     * 中心点必填，是之后列周边资源的圆心。发生时间写入事件的发生时间列。
     * 照片只存已经上传的地址。归属只写这本目录已经声明的字段，没声明的不写入。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String intake(EmergencyCommandIntakeReqVO reqVO) {
        prepareEventCatalog();
        Map<String, Object> placement = new LinkedHashMap<>();
        applyDeclaredOwnership(placement, reqVO.getOwnership(), null);
        putCenter(placement, reqVO.getLongitude(), reqVO.getLatitude(), reqVO.getLocationText());
        Map<String, Object> custom = pendingEvent();
        String now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        custom.put(EmergencyCommandFields.OCCURRED_AT, occurAt(reqVO.getOccurTime(), now));
        custom.put(EmergencyCommandFields.TRIGGER_TIME, now);
        custom.put(EmergencyCommandFields.EVENT_TIMELINE, EmergencyCommandRecords.append(
                new JSONArray(),
                EmergencyCommandRecords.KIND_INTAKE,
                EmergencyCommandRecords.fields("source", "manual", "summary", reqVO.getName().trim())));
        if (StringUtils.hasText(reqVO.getIntakeNote())) {
            custom.put(EmergencyCommandFields.INTAKE_NOTE, intakeNote(reqVO.getIntakeNote()));
        }
        List<String> photos = photoUrls(reqVO.getPhotoUrls());
        if (!photos.isEmpty()) {
            custom.put(EmergencyCommandFields.INTAKE_PHOTOS, new JSONArray(photos));
        }
        Long id = entityService.create(newEntity(
                EmergencyCommandFields.EVENT_CATALOG, reqVO.getName().trim(), custom, placement));
        return String.valueOf(id);
    }

    /** 告警转入。告警上没有坐标，中心点留空，进入指挥前由人在补位置里点出来。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String openFromAlert(EmergencyCommandFromAlertReqVO reqVO) {
        prepareEventCatalog();
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
        Map<String, Object> placement = new LinkedHashMap<>();
        applyDeclaredOwnership(placement, reqVO.getOwnership(), loadAlert(reqVO.getAlertId()));
        Map<String, Object> custom = pendingEvent();
        String now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();
        custom.put(EmergencyCommandFields.ALERT_ID, alertId);
        custom.put(EmergencyCommandFields.TRIGGER_TIME, now);
        custom.put(EmergencyCommandFields.EVENT_TIMELINE, EmergencyCommandRecords.append(
                new JSONArray(),
                EmergencyCommandRecords.KIND_INTAKE,
                EmergencyCommandRecords.fields("source", "alert", "summary", reqVO.getName().trim())));
        Long id = entityService.create(newEntity(
                EmergencyCommandFields.EVENT_CATALOG, reqVO.getName().trim(), custom, placement));
        return String.valueOf(id);
    }

    /** 补上或改正事件中心点。结案后不再改。所属场站、所属管线段不在这里改。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void locate(EmergencyCommandLocateReqVO reqVO) {
        EntityRespVO event = requireEvent(reqVO.getEventId());
        assertOpen(event);
        Map<String, Object> center = new LinkedHashMap<>();
        putCenter(center, reqVO.getLongitude(), reqVO.getLatitude(), reqVO.getLocationText());
        save(event, EmergencyCommandFields.EVENT_CATALOG, copyCustom(event), center);
    }

    /**
     * 待研判时改正事故经过和照片。
     * 名称、位置、发生时间不在这里改。研判之后拒绝，避免把后来的说法写回接报。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviseIntake(EmergencyCommandReviseIntakeReqVO reqVO) {
        EntityRespVO event = requireEvent(reqVO.getEventId());
        assertOpen(event);
        if (!EmergencyCommandFields.STATUS_PENDING.equals(text(field(event, EmergencyCommandFields.EVENT_STATUS)))) {
            throw new ServiceException(400, "只有待研判的事件可以改正接报内容");
        }
        Map<String, Object> custom = copyCustom(event);
        String note = text(reqVO.getIntakeNote());
        if (StringUtils.hasText(note)) {
            custom.put(EmergencyCommandFields.INTAKE_NOTE, intakeNote(note));
        } else {
            custom.remove(EmergencyCommandFields.INTAKE_NOTE);
        }
        List<String> photos = photoUrls(reqVO.getPhotoUrls());
        if (photos.isEmpty()) {
            custom.remove(EmergencyCommandFields.INTAKE_PHOTOS);
        } else {
            custom.put(EmergencyCommandFields.INTAKE_PHOTOS, new JSONArray(photos));
        }
        save(event, EmergencyCommandFields.EVENT_CATALOG, custom);
    }

    /** 只改这一条事件的名称。已结案拒绝。不改预案，不追加脉络。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rename(EmergencyCommandRenameReqVO reqVO) {
        EntityRespVO event = requireEvent(reqVO.getEventId());
        assertOpen(event);
        String name = reqVO.getName().trim();
        if (!StringUtils.hasText(name)) {
            throw new ServiceException(400, "事件名称不能为空");
        }
        Map<String, Object> base = new LinkedHashMap<>();
        base.put("name", name);
        save(event, EmergencyCommandFields.EVENT_CATALOG, copyCustom(event), base);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assess(EmergencyCommandAssessReqVO reqVO) {
        EntityRespVO event = requireEvent(reqVO.getEventId());
        assertOpen(event);
        if (!EmergencyCommandFields.STATUS_PENDING.equals(text(field(event, EmergencyCommandFields.EVENT_STATUS)))) {
            throw new ServiceException(400, "只有待研判的事件可以研判");
        }
        Map<String, Object> custom = copyCustom(event);
        custom.put(EmergencyCommandFields.JUDGED_REAL, reqVO.getReal());
        custom.put(EmergencyCommandFields.JUDGE_COMMENT, text(reqVO.getComment()));
        custom.put(EmergencyCommandFields.ASSESS_TIME,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
        if (Boolean.TRUE.equals(reqVO.getReal())) {
            if (!StringUtils.hasText(reqVO.getPlanId())) {
                throw new ServiceException(400, "成立时必须选择预案");
            }
            EntityRespVO plan = requireEffectivePlan(reqVO.getPlanId());
            String level = text(reqVO.getResponseLevel());
            if (EmergencyCommandSteps.hasResponseLevels(field(plan, EmergencyCommandFields.STEP_TREE))) {
                if (!StringUtils.hasText(level)) {
                    throw new ServiceException(400, "这份预案按响应等级分开，要选择等级");
                }
                if (!EmergencyCommandSteps.levelHasSteps(
                        field(plan, EmergencyCommandFields.STEP_TREE),
                        field(plan, EmergencyCommandFields.WARNING_STEPS),
                        level)) {
                    throw new ServiceException(400, "该级别没有步骤");
                }
            } else {
                level = "";
            }
            if (StringUtils.hasText(reqVO.getEventType())) {
                custom.put(EmergencyCommandFields.EVENT_TYPE, reqVO.getEventType().trim());
            }
            custom.put(EmergencyCommandFields.RESPONSE_LEVEL, level);
            custom.put(EmergencyCommandFields.PLAN_ID, String.valueOf(plan.getId()));
            custom.put(EmergencyCommandFields.EVENT_STATUS, EmergencyCommandFields.STATUS_READY);
        } else {
            custom.put(EmergencyCommandFields.EVENT_STATUS, EmergencyCommandFields.STATUS_REJECTED);
        }
        Map<String, String> assessFields = new java.util.LinkedHashMap<>();
        assessFields.put("comment", text(reqVO.getComment()));
        if (Boolean.TRUE.equals(reqVO.getReal())) {
            assessFields.put("planId", text(custom.get(EmergencyCommandFields.PLAN_ID)));
            assessFields.put("level", text(custom.get(EmergencyCommandFields.RESPONSE_LEVEL)));
        }
        custom.put(EmergencyCommandFields.EVENT_TIMELINE, EmergencyCommandRecords.append(
                EmergencyCommandRecords.read(custom.get(EmergencyCommandFields.EVENT_TIMELINE)),
                EmergencyCommandRecords.KIND_ASSESS,
                assessFields));
        save(event, EmergencyCommandFields.EVENT_CATALOG, custom);
    }

    /**
     * 图层上点确认，或在修改弹框里点确认。
     * 弹框选预警或后期处置时不改级别。选应急响应时，预案分了档才写入等级。没分档不写等级。
     * 响应中在已有等级之间改级才追加一条级别变化。后期处置可以再回到应急响应。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmJudgement(EmergencyCommandJudgementReqVO reqVO) {
        EntityRespVO event = requireEvent(reqVO.getEventId());
        assertOpen(event);
        String status = text(field(event, EmergencyCommandFields.EVENT_STATUS));
        String stage = text(reqVO.getStage());
        EntityRespVO plan = requireEffectivePlan(reqVO.getPlanId());
        String planId = String.valueOf(plan.getId());
        if ("warning".equals(stage)) {
            moveToPhase(event, status, planId, EmergencyCommandFields.STATUS_READY, "预警");
            return;
        }
        if ("aftermath".equals(stage)) {
            moveToPhase(event, status, planId, EmergencyCommandFields.STATUS_AFTERMATH, "后期处置");
            return;
        }
        String level = text(reqVO.getResponseLevel());
        if (EmergencyCommandSteps.hasResponseLevels(field(plan, EmergencyCommandFields.STEP_TREE))) {
            if (!StringUtils.hasText(level)) {
                throw new ServiceException(400, "进入响应阶段要选择响应等级");
            }
            if (!EmergencyCommandSteps.levelHasSteps(
                    field(plan, EmergencyCommandFields.STEP_TREE),
                    field(plan, EmergencyCommandFields.WARNING_STEPS),
                    level)) {
                throw new ServiceException(400, "该级别没有步骤");
            }
        } else {
            level = "";
        }
        if (EmergencyCommandFields.STATUS_PENDING.equals(status)) {
            EmergencyCommandAssessReqVO assessReq = new EmergencyCommandAssessReqVO();
            assessReq.setEventId(reqVO.getEventId());
            assessReq.setReal(true);
            assessReq.setPlanId(planId);
            assessReq.setResponseLevel(level);
            assess(assessReq);
            if ("response".equals(stage)) {
                EmergencyCommandStartReqVO startReq = new EmergencyCommandStartReqVO();
                startReq.setEventId(reqVO.getEventId());
                start(startReq);
            }
            return;
        }
        if (EmergencyCommandFields.STATUS_READY.equals(status)) {
            Map<String, Object> custom = copyCustom(event);
            custom.put(EmergencyCommandFields.PLAN_ID, planId);
            custom.put(EmergencyCommandFields.RESPONSE_LEVEL, level);
            save(event, EmergencyCommandFields.EVENT_CATALOG, custom);
            if ("response".equals(stage)) {
                EmergencyCommandStartReqVO startReq = new EmergencyCommandStartReqVO();
                startReq.setEventId(reqVO.getEventId());
                start(startReq);
            }
            return;
        }
        if (EmergencyCommandFields.STATUS_RESPONDING.equals(status)) {
            String currentPlan = text(field(event, EmergencyCommandFields.PLAN_ID));
            String currentLevel = text(field(event, EmergencyCommandFields.RESPONSE_LEVEL));
            if (!planId.equals(currentPlan)) {
                Map<String, Object> custom = copyCustom(event);
                custom.put(EmergencyCommandFields.PLAN_ID, planId);
                custom.put(EmergencyCommandFields.CURRENT_PLAN_STATE,
                        EmergencyCommandRecords.currentState(planId, currentLevel));
                save(event, EmergencyCommandFields.EVENT_CATALOG, custom);
            }
            if (!level.equals(currentLevel)) {
                if (!StringUtils.hasText(level)) {
                    clearResponseLevel(reqVO.getEventId(), planId);
                } else {
                    EmergencyCommandChangeLevelReqVO change = new EmergencyCommandChangeLevelReqVO();
                    change.setEventId(reqVO.getEventId());
                    change.setResponseLevel(level);
                    changeLevel(change);
                }
            }
            return;
        }
        if (EmergencyCommandFields.STATUS_AFTERMATH.equals(status)) {
            String currentLevel = text(field(event, EmergencyCommandFields.RESPONSE_LEVEL));
            Map<String, Object> custom = copyCustom(event);
            custom.put(EmergencyCommandFields.PLAN_ID, planId);
            custom.put(EmergencyCommandFields.EVENT_STATUS, EmergencyCommandFields.STATUS_RESPONDING);
            custom.put(EmergencyCommandFields.CURRENT_PLAN_STATE,
                    EmergencyCommandRecords.currentState(planId, currentLevel));
            save(event, EmergencyCommandFields.EVENT_CATALOG, custom);
            if (!level.equals(currentLevel)) {
                if (!StringUtils.hasText(level)) {
                    clearResponseLevel(reqVO.getEventId(), planId);
                } else {
                    EmergencyCommandChangeLevelReqVO change = new EmergencyCommandChangeLevelReqVO();
                    change.setEventId(reqVO.getEventId());
                    change.setResponseLevel(level);
                    changeLevel(change);
                }
            }
            return;
        }
        throw new ServiceException(400, "当前阶段不能修改研判");
    }

    /** 换到一份不分等级的预案时，清掉事件上原来的响应等级。 */
    private void clearResponseLevel(String eventId, String planId) {
        EntityRespVO latest = requireEvent(eventId);
        Map<String, Object> custom = copyCustom(latest);
        custom.put(EmergencyCommandFields.PLAN_ID, planId);
        custom.put(EmergencyCommandFields.RESPONSE_LEVEL, "");
        custom.put(EmergencyCommandFields.CURRENT_PLAN_STATE, EmergencyCommandRecords.currentState(planId, ""));
        save(latest, EmergencyCommandFields.EVENT_CATALOG, custom);
    }

    /**
     * 改到不选等级的阶段。响应等级保留，状态改成这一阶段。
     * 应急响应之后还可以再改到后期处置，不在响应这里结束。
     */
    private void moveToPhase(EntityRespVO event, String status, String planId, String nextStatus, String stageName) {
        if (!EmergencyCommandFields.STATUS_READY.equals(status)
                && !EmergencyCommandFields.STATUS_RESPONDING.equals(status)
                && !EmergencyCommandFields.STATUS_AFTERMATH.equals(status)) {
            throw new ServiceException(400, "当前阶段不能改到" + stageName);
        }
        Map<String, Object> custom = copyCustom(event);
        custom.put(EmergencyCommandFields.PLAN_ID, planId);
        custom.put(EmergencyCommandFields.EVENT_STATUS, nextStatus);
        JSONArray state = new JSONArray();
        JSONObject row = new JSONObject();
        row.put("planId", planId);
        row.put("currentLevel", text(field(event, EmergencyCommandFields.RESPONSE_LEVEL)));
        row.put("currentStage", stageName);
        state.add(row);
        custom.put(EmergencyCommandFields.CURRENT_PLAN_STATE, state);
        if (EmergencyCommandFields.STATUS_AFTERMATH.equals(nextStatus)) {
            custom.put(EmergencyCommandFields.EVENT_TIMELINE, EmergencyCommandRecords.append(
                    EmergencyCommandRecords.read(custom.get(EmergencyCommandFields.EVENT_TIMELINE)),
                    EmergencyCommandRecords.KIND_AFTERMATH,
                    EmergencyCommandRecords.fields("planId", planId)));
        }
        save(event, EmergencyCommandFields.EVENT_CATALOG, custom);
    }

    /**
     * 启动响应：读事件上已经选定的预案。预案分了档时再读等级。
     * 没分档的预案不要求等级，也不因为没有等级而拒绝启动。
     * 只追加一条启动记录。不把步骤复制出来。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String start(EmergencyCommandStartReqVO reqVO) {
        EntityRespVO event = requireEvent(reqVO.getEventId());
        assertOpen(event);
        String status = text(field(event, EmergencyCommandFields.EVENT_STATUS));
        if (!EmergencyCommandFields.STATUS_READY.equals(status)) {
            throw new ServiceException(400, "只有研判成立、处于预警的事件可以启动响应");
        }
        String level = text(field(event, EmergencyCommandFields.RESPONSE_LEVEL));
        String planId = text(field(event, EmergencyCommandFields.PLAN_ID));
        if (!StringUtils.hasText(planId)) {
            throw new ServiceException(400, "研判时没有选定预案");
        }
        EntityRespVO plan = requireEffectivePlan(planId);
        if (EmergencyCommandSteps.hasResponseLevels(field(plan, EmergencyCommandFields.STEP_TREE))) {
            if (!StringUtils.hasText(level)) {
                throw new ServiceException(400, "研判时没有选定响应等级");
            }
            if (!EmergencyCommandSteps.levelHasSteps(
                    field(plan, EmergencyCommandFields.STEP_TREE),
                    field(plan, EmergencyCommandFields.WARNING_STEPS),
                    level)) {
                throw new ServiceException(400, "该级别没有步骤");
            }
        } else {
            level = "";
        }
        Map<String, Object> custom = copyCustom(event);
        JSONArray timeline = EmergencyCommandRecords.append(
                EmergencyCommandRecords.read(custom.get(EmergencyCommandFields.EVENT_TIMELINE)),
                EmergencyCommandRecords.KIND_START,
                EmergencyCommandRecords.fields("planId", planId, "level", level));
        custom.put(EmergencyCommandFields.EVENT_TIMELINE, timeline);
        custom.put(EmergencyCommandFields.CURRENT_PLAN_STATE, EmergencyCommandRecords.currentState(planId, level));
        custom.put(EmergencyCommandFields.EVENT_STATUS, EmergencyCommandFields.STATUS_RESPONDING);
        custom.put(EmergencyCommandFields.COMMAND_ORG, emergencyCommandOrgService.snapshot(plan, event));
        save(event, EmergencyCommandFields.EVENT_CATALOG, custom);
        return String.valueOf(event.getId());
    }

    @Override
    public List<EmergencyCommandPlanRespVO> listPlans() {
        List<EmergencyCommandPlanRespVO> plans = new ArrayList<>();
        for (EntityRespVO plan : effectivePlans()) {
            plans.add(toPlan(plan));
        }
        return plans;
    }

    @Override
    public EmergencyCommandDetailRespVO detail(String eventId) {
        EntityRespVO event = requireEvent(eventId);
        EmergencyCommandDetailRespVO detail = new EmergencyCommandDetailRespVO();
        detail.setEvent(toEvent(event));
        detail.setSteps(List.of());
        detail.setLevels(List.of());
        detail.setTimeline(List.of());
        String planId = text(field(event, EmergencyCommandFields.PLAN_ID));
        detail.setPlanId(planId);
        detail.setResponseLevel(text(field(event, EmergencyCommandFields.RESPONSE_LEVEL)));
        Object timeline = field(event, EmergencyCommandFields.EVENT_TIMELINE);
        detail.setStartedAt(EmergencyCommandRecords.startedAt(timeline));
        EntityRespVO plan = loadPlan(planId);
        Object planSteps = plan == null ? null : field(plan, EmergencyCommandFields.STEP_TREE);
        Object warningSteps = plan == null ? null : field(plan, EmergencyCommandFields.WARNING_STEPS);
        detail.setLevels(toLevels(EmergencyCommandSteps.present(planSteps, warningSteps, timeline)));
        detail.setTimeline(toTimeline(timeline, planSteps, warningSteps));
        detail.setStages(readStages(planSteps));
        String stepLevel = detail.getResponseLevel();
        if (!StringUtils.hasText(stepLevel)) {
            stepLevel = EmergencyCommandSteps.ungradedResponseKey(planSteps);
        }
        if (StringUtils.hasText(stepLevel)) {
            for (EmergencyCommandLevelRespVO level : detail.getLevels()) {
                if (stepLevel.equals(level.getLevel())) {
                    detail.setSteps(flattenCurrent(level));
                    break;
                }
            }
        }
        fillPlans(detail, plan);
        fillCommandOrg(detail, event);
        return detail;
    }

    /**
     * 改变当前级别。原级别已经记下的完成和上报留在脉络里。
     * 之后再完成或上报原级别的步骤，仍按那个级别追加。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeLevel(EmergencyCommandChangeLevelReqVO reqVO) {
        EntityRespVO event = requireEvent(reqVO.getEventId());
        assertOpen(event);
        if (!EmergencyCommandFields.STATUS_RESPONDING.equals(text(field(event, EmergencyCommandFields.EVENT_STATUS)))) {
            throw new ServiceException(400, "只有响应中的事件可以改变级别");
        }
        String planId = text(field(event, EmergencyCommandFields.PLAN_ID));
        if (!StringUtils.hasText(planId)) {
            throw new ServiceException(400, "这起事件还没有选定预案");
        }
        String nextLevel = reqVO.getResponseLevel().trim();
        String currentLevel = text(field(event, EmergencyCommandFields.RESPONSE_LEVEL));
        if (nextLevel.equals(currentLevel)) {
            return;
        }
        EntityRespVO plan = requireEffectivePlan(planId);
        if (!EmergencyCommandSteps.levelHasSteps(
                field(plan, EmergencyCommandFields.STEP_TREE),
                field(plan, EmergencyCommandFields.WARNING_STEPS),
                nextLevel)) {
            throw new ServiceException(400, "该级别没有步骤");
        }
        Map<String, Object> custom = copyCustom(event);
        JSONArray timeline = EmergencyCommandRecords.append(
                EmergencyCommandRecords.read(custom.get(EmergencyCommandFields.EVENT_TIMELINE)),
                EmergencyCommandRecords.KIND_LEVEL,
                EmergencyCommandRecords.fields(
                        "planId", planId, "fromLevel", currentLevel, "toLevel", nextLevel, "level", nextLevel));
        custom.put(EmergencyCommandFields.EVENT_TIMELINE, timeline);
        custom.put(EmergencyCommandFields.CURRENT_PLAN_STATE, EmergencyCommandRecords.currentState(planId, nextLevel));
        custom.put(EmergencyCommandFields.RESPONSE_LEVEL, nextLevel);
        save(event, EmergencyCommandFields.EVENT_CATALOG, custom);
    }

    /**
     * 后期处置之后结案。结案状态改为已结案，脉络追加一条没有附加栏的结案。
     * 当前阶段保持后期处置。已结案的事件不再改。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeCase(EmergencyCommandCloseReqVO reqVO) {
        EntityRespVO event = requireEvent(reqVO.getEventId());
        if (EmergencyCommandFields.CASE_CLOSED.equals(text(field(event, EmergencyCommandFields.CASE_STATUS)))) {
            throw new ServiceException(400, "这起事件已经结案");
        }
        if (!EmergencyCommandFields.STATUS_AFTERMATH.equals(text(field(event, EmergencyCommandFields.EVENT_STATUS)))) {
            throw new ServiceException(400, "进入后期处置后才能结案");
        }
        Map<String, Object> custom = copyCustom(event);
        custom.put(EmergencyCommandFields.CASE_STATUS, EmergencyCommandFields.CASE_CLOSED);
        custom.put(EmergencyCommandFields.EVENT_TIMELINE, EmergencyCommandRecords.append(
                EmergencyCommandRecords.read(custom.get(EmergencyCommandFields.EVENT_TIMELINE)),
                EmergencyCommandRecords.KIND_CLOSE,
                null));
        save(event, EmergencyCommandFields.EVENT_CATALOG, custom);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeStep(EmergencyCommandCompleteStepReqVO reqVO) {
        appendStep(reqVO, EmergencyCommandRecords.KIND_STEP_COMPLETED, "这一步已经完成");
    }

    /**
     * 记下这一步开始。已经完成的步骤不再改。已经开始过的保持第一条开始记录。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startStep(EmergencyCommandCompleteStepReqVO reqVO) {
        appendStep(reqVO, EmergencyCommandRecords.KIND_STEP_STARTED, "这一步已经完成");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitReport(EmergencyCommandSubmitReportReqVO reqVO) {
        ParsedStep step = requireStep(reqVO.getEventId(), reqVO.getLevel(), reqVO.getNodeKey());
        if (!StringUtils.hasText(step.reportFormId())) {
            throw new ServiceException(400, "这一步没有信息表");
        }
        Map<String, Object> custom = copyCustom(step.event());
        JSONArray timeline = EmergencyCommandRecords.append(
                EmergencyCommandRecords.read(custom.get(EmergencyCommandFields.EVENT_TIMELINE)),
                EmergencyCommandRecords.KIND_REPORT,
                EmergencyCommandRecords.fields(
                        "planId", step.planId(),
                        "level", step.level(),
                        "nodeKey", step.nodeKey(),
                        "reportFormId", step.reportFormId()));
        if (reqVO.getFormData() != null && !reqVO.getFormData().isEmpty()) {
            timeline.getJSONObject(timeline.size() - 1).put("formData", reqVO.getFormData());
        }
        custom.put(EmergencyCommandFields.EVENT_TIMELINE, timeline);
        save(step.event(), EmergencyCommandFields.EVENT_CATALOG, custom);
    }

    /**
     * 按级别和步骤编号追加开始或完成。原级别的记录保留。
     * 同一步的完成只保留第一条。
     */
    private void appendStep(EmergencyCommandCompleteStepReqVO reqVO, String kind, String doneMessage) {
        ParsedStep step = requireStep(reqVO.getEventId(), reqVO.getLevel(), reqVO.getNodeKey());
        Map<String, Object> custom = copyCustom(step.event());
        JSONArray timeline = EmergencyCommandRecords.read(custom.get(EmergencyCommandFields.EVENT_TIMELINE));
        if (EmergencyCommandRecords.hasStepRecord(
                timeline, EmergencyCommandRecords.KIND_STEP_COMPLETED, step.level(), step.nodeKey())) {
            throw new ServiceException(400, doneMessage);
        }
        if (EmergencyCommandRecords.KIND_STEP_STARTED.equals(kind)
                && EmergencyCommandRecords.hasStepRecord(
                        timeline, EmergencyCommandRecords.KIND_STEP_STARTED, step.level(), step.nodeKey())) {
            return;
        }
        custom.put(EmergencyCommandFields.EVENT_TIMELINE, EmergencyCommandRecords.append(
                timeline,
                kind,
                EmergencyCommandRecords.fields("planId", step.planId(), "level", step.level(), "nodeKey", step.nodeKey())));
        save(step.event(), EmergencyCommandFields.EVENT_CATALOG, custom);
    }

    /**
     * 步骤必须在事件已选定的预案上。预警在待启动和响应中都能记。响应级别只在响应中记。
     */
    private ParsedStep requireStep(String eventId, String levelRaw, String nodeKeyRaw) {
        EntityRespVO event = requireEvent(eventId);
        assertOpen(event);
        String status = text(field(event, EmergencyCommandFields.EVENT_STATUS));
        String level = levelRaw == null ? "" : levelRaw.trim();
        String nodeKey = nodeKeyRaw == null ? "" : nodeKeyRaw.trim();
        boolean warning = EmergencyCommandFields.LEVEL_WARNING.equals(level);
        if (warning) {
            if (!EmergencyCommandFields.STATUS_READY.equals(status)
                    && !EmergencyCommandFields.STATUS_RESPONDING.equals(status)) {
                throw new ServiceException(400, "只有预警或响应中的事件可以记录预警步骤");
            }
        } else if (!EmergencyCommandFields.STATUS_RESPONDING.equals(status)) {
            throw new ServiceException(400, "只有响应中的事件可以记录响应步骤");
        }
        String planId = text(field(event, EmergencyCommandFields.PLAN_ID));
        if (!StringUtils.hasText(planId)) {
            throw new ServiceException(400, "这起事件还没有选定预案");
        }
        EntityRespVO plan = requireEffectivePlan(planId);
        Object planSteps = field(plan, EmergencyCommandFields.STEP_TREE);
        Object warningSteps = field(plan, EmergencyCommandFields.WARNING_STEPS);
        if (!EmergencyCommandSteps.hasStep(planSteps, warningSteps, level, nodeKey)) {
            throw new ServiceException(400, "步骤不存在");
        }
        return new ParsedStep(event, planId, level, nodeKey,
                EmergencyCommandSteps.reportFormId(planSteps, warningSteps, level, nodeKey));
    }

    private List<EmergencyCommandLevelRespVO> toLevels(List<EmergencyCommandSteps.LevelNodes> source) {
        List<EmergencyCommandLevelRespVO> levels = new ArrayList<>();
        for (EmergencyCommandSteps.LevelNodes level : source) {
            EmergencyCommandLevelRespVO row = new EmergencyCommandLevelRespVO();
            row.setLevel(level.level());
            List<EmergencyCommandStepNodeRespVO> nodes = new ArrayList<>();
            for (EmergencyCommandSteps.StepNode node : level.nodes()) {
                nodes.add(toNode(node));
            }
            row.setNodes(nodes);
            levels.add(row);
        }
        return levels;
    }

    private static EmergencyCommandStepNodeRespVO toNode(EmergencyCommandSteps.StepNode node) {
        EmergencyCommandStepNodeRespVO item = new EmergencyCommandStepNodeRespVO();
        item.setNodeKey(node.nodeKey());
        item.setParentNodeKey(node.parentNodeKey());
        item.setDescription(node.description());
        item.setOrder(node.order());
        item.setRunStatus(node.runStatus());
        item.setCompletedAt(node.completedAt());
        item.setStartedAt(node.startedAt());
        item.setStageKey(node.stageKey());
        item.setReportFormId(node.reportFormId());
        item.setDetailDescription(node.detailDescription());
        return item;
    }

    private List<EmergencyCommandStepRespVO> flattenCurrent(EmergencyCommandLevelRespVO level) {
        List<EmergencyCommandStepRespVO> steps = new ArrayList<>();
        if (level.getNodes() == null) {
            return steps;
        }
        for (EmergencyCommandStepNodeRespVO node : level.getNodes()) {
            EmergencyCommandStepRespVO step = new EmergencyCommandStepRespVO();
            step.setNodeKey(node.getNodeKey());
            step.setDescription(node.getDescription());
            step.setRunStatus(node.getRunStatus());
            steps.add(step);
        }
        return steps;
    }

    private List<EmergencyCommandTimelineRespVO> toTimeline(Object raw, Object planSteps, Object warningSteps) {
        List<EmergencyCommandTimelineRespVO> rows = new ArrayList<>();
        JSONArray timeline = EmergencyCommandRecords.read(raw);
        for (int i = 0; i < timeline.size(); i++) {
            JSONObject row = timeline.getJSONObject(i);
            if (row == null) {
                continue;
            }
            EmergencyCommandTimelineRespVO item = new EmergencyCommandTimelineRespVO();
            item.setRecordId(text(row.get("recordId")));
            item.setKind(text(row.get("kind")));
            item.setAt(text(row.get("at")));
            item.setLevel(text(row.get("level")));
            item.setNodeKey(text(row.get("nodeKey")));
            item.setFromLevel(text(row.get("fromLevel")));
            item.setToLevel(text(row.get("toLevel")));
            item.setReportFormId(text(row.get("reportFormId")));
            item.setPlanId(text(row.get("planId")));
            item.setSource(text(row.get("source")));
            item.setSummary(text(row.get("summary")));
            item.setComment(text(row.get("comment")));
            if (StringUtils.hasText(item.getLevel()) && StringUtils.hasText(item.getNodeKey())) {
                item.setDescription(EmergencyCommandSteps.stepDescription(
                        planSteps, warningSteps, item.getLevel(), item.getNodeKey()));
            }
            rows.add(item);
        }
        return rows;
    }

    private List<EmergencyCommandStageRespVO> readStages(Object planSteps) {
        List<EmergencyCommandStageRespVO> stages = new ArrayList<>();
        for (EmergencyCommandSteps.StageDef stage : EmergencyCommandSteps.listStages(planSteps)) {
            EmergencyCommandStageRespVO row = new EmergencyCommandStageRespVO();
            row.setStageKey(stage.stageKey());
            row.setName(stage.name());
            stages.add(row);
        }
        return stages;
    }

    private void fillPlans(EmergencyCommandDetailRespVO detail, EntityRespVO selected) {
        List<EmergencyCommandPlanRespVO> plans = new ArrayList<>();
        for (EntityRespVO plan : effectivePlans()) {
            plans.add(toPlan(plan));
        }
        if (selected != null && plans.stream().noneMatch(plan -> detail.getPlanId().equals(plan.getId()))) {
            plans.add(0, toPlan(selected));
        }
        if (selected != null) {
            detail.setPlanName(text(selected.getName()));
        }
        detail.setPlans(plans);
    }

    private EntityRespVO loadPlan(String planId) {
        if (!StringUtils.hasText(planId)) {
            return null;
        }
        return entityService.get(parseId(planId, "预案编号不正确"), EmergencyCommandFields.PLAN_CATALOG);
    }

    /** 按预案编号读取。事件分类不参与这次查找。未生效则拒绝。 */
    private EntityRespVO requireEffectivePlan(String planId) {
        EntityRespVO plan = loadPlan(planId);
        if (plan == null) {
            throw new ServiceException(404, "应急预案不存在");
        }
        if (!EmergencyCommandFields.PLAN_EFFECTIVE.equals(text(field(plan, EmergencyCommandFields.PLAN_STATUS)))) {
            throw new ServiceException(400, "这份预案还没有生效");
        }
        return plan;
    }

    /**
     * 指挥下拉里的预案：生效状态写成「生效」的那些。
     * 读预案上已经保存的这个值。不走列表筛选。
     * 型号上「可筛选」关掉时，按字段筛选会把已经生效的预案全部丢掉，下拉就空了。
     * 没写生效状态的预案不放进下拉，也不补成生效。
     */
    private List<EntityRespVO> effectivePlans() {
        List<EntityRespVO> plans = new ArrayList<>();
        int pageSize = 200;
        for (int pageNo = 1; pageNo <= 20; pageNo++) {
            EntitySceneQueryRespVO queried = query(
                    EmergencyCommandFields.PLAN_CATALOG, List.of(), pageNo, pageSize);
            PageResult<EntityRespVO> page = queried.getPage();
            List<EntityRespVO> list = page == null || page.getList() == null ? List.of() : page.getList();
            for (EntityRespVO plan : list) {
                if (EmergencyCommandFields.PLAN_EFFECTIVE.equals(
                        text(field(plan, EmergencyCommandFields.PLAN_STATUS)))) {
                    plans.add(plan);
                }
            }
            long total = page == null || page.getTotal() == null ? list.size() : page.getTotal();
            if (list.isEmpty() || (long) pageNo * pageSize >= total) {
                break;
            }
        }
        return plans;
    }

    private EmergencyCommandPlanRespVO toPlan(EntityRespVO plan) {
        EmergencyCommandPlanRespVO item = new EmergencyCommandPlanRespVO();
        item.setId(String.valueOf(plan.getId()));
        item.setName(text(plan.getName()));
        item.setLevels(planLevels(field(plan, EmergencyCommandFields.STEP_TREE)));
        item.setStartRules(startRules(field(plan, EmergencyCommandFields.START_RULES)));
        return item;
    }

    private static List<EmergencyCommandPlanLevelRespVO> planLevels(Object raw) {
        List<EmergencyCommandPlanLevelRespVO> levels = new ArrayList<>();
        JSONObject pack = asPack(raw);
        if (pack == null) {
            return levels;
        }
        JSONArray methods = pack.getJSONArray("methods");
        if (methods == null) {
            return levels;
        }
        for (int i = 0; i < methods.size(); i++) {
            JSONObject method = methods.getJSONObject(i);
            if (method == null || !StringUtils.hasText(text(method.get("methodKey")))) {
                continue;
            }
            if (EmergencyCommandSteps.isUngradedResponseBucket(
                    text(method.get("methodKey")), text(method.get("label")))) {
                continue;
            }
            EmergencyCommandPlanLevelRespVO level = new EmergencyCommandPlanLevelRespVO();
            level.setLevel(text(method.get("methodKey")));
            String label = text(method.get("label"));
            level.setLabel(label.isEmpty() ? level.getLevel() : label);
            levels.add(level);
        }
        return levels;
    }

    /**
     * 读预案上的启动规则。级别键与步骤页签一致。原文没写的级别不补一条。
     */
    private static List<EmergencyCommandStartRuleRespVO> startRules(Object raw) {
        List<EmergencyCommandStartRuleRespVO> rules = new ArrayList<>();
        JSONArray rows = EmergencyCommandRecords.read(raw);
        for (int i = 0; i < rows.size(); i++) {
            JSONObject row = rows.getJSONObject(i);
            if (row == null) {
                continue;
            }
            String level = levelKey(text(row.get("level")));
            if (!StringUtils.hasText(level)) {
                continue;
            }
            EmergencyCommandStartRuleRespVO rule = new EmergencyCommandStartRuleRespVO();
            rule.setLevel(level);
            rule.setStarterOrg(text(row.get("starter_org")));
            rule.setCondition(text(row.get("condition")));
            rules.add(rule);
        }
        return rules;
    }

    /** 预案级别和步骤页签用同一套 I、II、III、IV。 */
    private static String levelKey(String raw) {
        return switch (raw) {
            case "Ⅰ", "I" -> "I";
            case "Ⅱ", "II" -> "II";
            case "Ⅲ", "III" -> "III";
            case "Ⅳ", "IV" -> "IV";
            default -> raw;
        };
    }

    /** 已结案的事件不再研判、启动、改级或记步骤。 */
    private static void assertOpen(EntityRespVO event) {
        if (EmergencyCommandFields.CASE_CLOSED.equals(text(field(event, EmergencyCommandFields.CASE_STATUS)))) {
            throw new ServiceException(400, "这起事件已经结案");
        }
    }

    private record ParsedStep(
            EntityRespVO event, String planId, String level, String nodeKey, String reportFormId) {
    }

    /** 按编号取事件。登录人看不到这起事件时 403，指挥、周边资源都从这里进。 */
    private EntityRespVO requireEvent(String eventId) {
        Long id = parseId(eventId, "事件编号不正确");
        dataScopeQueryService.assertVisible(EmergencyCommandFields.EVENT_CATALOG, id);
        EntityRespVO event = entityService.get(id, EmergencyCommandFields.EVENT_CATALOG);
        if (event == null) {
            throw new ServiceException(404, "应急事件不存在");
        }
        return event;
    }

    /**
     * 接报先保证事件目录和预案目录能写入。新事件停在待研判，预案和脉络都空着。
     * 不碰已经删除的应急响应目录。执行记录写在事件上。
     */
    private void prepareEventCatalog() {
        ensureWritableModel(EmergencyCommandFields.EVENT_CATALOG, "应急事件");
        ensureWritableModel(EmergencyCommandFields.PLAN_CATALOG, "应急预案");
        stepStructureProvisionService.ensureProvisioned(EmergencyCommandFields.PLAN_CATALOG, null, null);
    }

    /** 新事件的处置状态。所属场站只写基础字段的专用列（见 newEntity），不再在自定义字段里留一份。 */
    private Map<String, Object> pendingEvent() {
        Map<String, Object> custom = new LinkedHashMap<>();
        custom.put(EmergencyCommandFields.EVENT_STATUS, EmergencyCommandFields.STATUS_PENDING);
        custom.put(EmergencyCommandFields.CASE_STATUS, EmergencyCommandFields.CASE_OPEN);
        return custom;
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

    /** @param placement 要写进基础字段的所属场站、所属管线段、中心点；没有就传空表 */
    private EntityCreateReqVO newEntity(
            String catalog, String name, Map<String, Object> custom, Map<String, Object> placement) {
        String catalogName = EmergencyCommandFields.EVENT_CATALOG.equals(catalog) ? "应急事件"
                : EmergencyCommandFields.PLAN_CATALOG.equals(catalog) ? "应急预案" : null;
        if (catalogName == null) {
            throw new ServiceException(400, "指挥只写入应急事件和应急预案");
        }
        Map<String, Object> base = new LinkedHashMap<>();
        base.put("entityTypeCode", catalog);
        base.put("modelId", writableModelId(catalog, catalogName));
        base.put("name", name);
        base.put("status", 1);
        base.putAll(placement);
        EntityCreateReqVO req = new EntityCreateReqVO();
        req.setRegistryCode(catalog);
        req.setBaseFields(base);
        req.setCustomFields(custom);
        return req;
    }

    private void save(EntityRespVO current, String catalog, Map<String, Object> custom) {
        save(current, catalog, custom, Map.of());
    }

    /** @param baseChanges 这次要改的基础字段；其余基础字段按详情原样带回 */
    private void save(EntityRespVO current, String catalog, Map<String, Object> custom, Map<String, Object> baseChanges) {
        Map<String, Object> base = new LinkedHashMap<>();
        if (current.getBaseFields() != null) {
            base.putAll(current.getBaseFields());
        }
        base.putAll(baseChanges);
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
                null,
                catalog);
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
        row.setFacilityId(refId(field(entity, FacilityOwningFieldCodes.FIELD_CODE)));
        row.setPipelineSegmentId(refId(field(entity, EmergencyCommandFields.PIPELINE_SEGMENT)));
        row.setLongitude(number(field(entity, EmergencyCommandFields.LONGITUDE)));
        row.setLatitude(number(field(entity, EmergencyCommandFields.LATITUDE)));
        row.setLocationText(text(field(entity, EmergencyCommandFields.LOCATION_TEXT)));
        row.setOccurredAt(text(field(entity, EmergencyCommandFields.OCCURRED_AT)));
        row.setIntakeNote(text(field(entity, EmergencyCommandFields.INTAKE_NOTE)));
        row.setIntakePhotos(readPhotos(field(entity, EmergencyCommandFields.INTAKE_PHOTOS)));
        row.setCaseStatus(text(field(entity, EmergencyCommandFields.CASE_STATUS)));
        return row;
    }

    /** 指挥机构只读启动时写下的那一份。缺了用一句话说明，不临时去预案上现组。 */
    private void fillCommandOrg(EmergencyCommandDetailRespVO detail, EntityRespVO event) {
        EmergencyCommandOrg.Shown shown = EmergencyCommandOrg.show(
                field(event, EmergencyCommandFields.COMMAND_ORG),
                text(field(event, EmergencyCommandFields.RESPONSE_LEVEL)));
        detail.setCommandOrg(shown.groups());
        detail.setCommandOrgGap(shown.gap());
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

    /** 引用字段在详情里可能是编号，也可能是带名称的引用对象 {id, name}；这里只取编号。 */
    private static String refId(Object raw) {
        if (raw instanceof Map<?, ?> ref) {
            return text(ref.get("id"));
        }
        return text(raw);
    }

    private static Double number(Object raw) {
        if (raw instanceof Number value) {
            return value.doubleValue();
        }
        if (raw instanceof String value && StringUtils.hasText(value)) {
            try {
                return Double.valueOf(value.trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    /**
     * 按应急事件目录的归属声明写入。
     * 告警记录上同一栏已有编号时用告警上的。没有时用提交里的这一栏。
     * 声明里没有的字段不写入。必填的声明没值就拒绝。
     */
    private void applyDeclaredOwnership(Map<String, Object> placement, Map<String, String> submitted, EntityRespDTO alarm) {
        for (CatalogOwnershipQueryService.OwnershipDeclarationView view
                : catalogOwnershipQueryService.listDirectDeclarations(EmergencyCommandFields.EVENT_CATALOG)) {
            String field = view.hop().fieldCode();
            if (!StringUtils.hasText(field)) {
                continue;
            }
            field = field.trim();
            Long fromAlarm = alarm == null ? null : owningId(alarm, field);
            if (fromAlarm != null) {
                placement.put(field, fromAlarm);
                continue;
            }
            String raw = submitted == null ? null : submitted.get(field);
            if (view.required() && !StringUtils.hasText(raw)) {
                throw new ServiceException(400, view.hop().fieldName() + "不能为空");
            }
            putOptionalId(placement, field, raw, view.hop().fieldName() + "不正确");
        }
    }

    private EntityRespDTO loadAlert(String alertId) {
        if (!StringUtils.hasText(alertId)) {
            return null;
        }
        EntityRespDTO byCode = entityRpcFacadeService.getByCode(alertId.trim(), "alarm");
        if (byCode != null) {
            return byCode;
        }
        try {
            long id = Long.parseLong(alertId.trim());
            return id > 0 ? entityRpcFacadeService.get(id, "alarm") : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static Long owningId(EntityRespDTO alarm, String fieldCode) {
        Object raw = alarm.getBaseFields() == null ? null : alarm.getBaseFields().get(fieldCode);
        if (raw == null && alarm.getCustomFields() != null) {
            raw = alarm.getCustomFields().get(fieldCode);
        }
        return FacilityOwningFieldCodes.extractId(raw);
    }

    private static void putOptionalId(Map<String, Object> base, String fieldCode, String raw, String message) {
        if (StringUtils.hasText(raw)) {
            base.put(fieldCode, parseId(raw, message));
        }
    }

    /** 事故经过。过长就拒绝，不截断。 */
    private static String intakeNote(String raw) {
        String note = raw.trim();
        if (note.length() > 4000) {
            throw new ServiceException(400, "事故经过不能超过 4000 字");
        }
        return note;
    }

    private static List<String> readPhotos(Object raw) {
        List<String> photos = new ArrayList<>();
        if (raw instanceof JSONArray array) {
            for (int i = 0; i < array.size(); i++) {
                String url = text(array.get(i));
                if (StringUtils.hasText(url)) {
                    photos.add(url);
                }
            }
            return photos;
        }
        if (raw instanceof List<?> list) {
            for (Object item : list) {
                String url = text(item);
                if (StringUtils.hasText(url)) {
                    photos.add(url);
                }
            }
        }
        return photos;
    }

    /** 接报填写的发生时间。空着就用接报这一刻。格式不对就拒绝，不另编一个时间。 */
    private static String occurAt(String raw, String now) {
        String value = text(raw);
        if (!StringUtils.hasText(value)) {
            return now;
        }
        try {
            LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException ex) {
            throw new ServiceException(400, "发生时间格式应为 yyyy-MM-dd HH:mm:ss");
        }
        return value;
    }

    /** 照片地址。最多 9 张。只接受已经上传得到的地址。 */
    private static List<String> photoUrls(List<String> raw) {
        if (raw == null || raw.isEmpty()) {
            return List.of();
        }
        if (raw.size() > 9) {
            throw new ServiceException(400, "照片最多 9 张");
        }
        List<String> urls = new ArrayList<>();
        for (String item : raw) {
            String url = text(item);
            if (!StringUtils.hasText(url) || url.length() > 1000) {
                throw new ServiceException(400, "照片地址不正确");
            }
            String lower = url.toLowerCase();
            if (lower.startsWith("javascript:") || lower.startsWith("data:")) {
                throw new ServiceException(400, "照片地址不正确");
            }
            urls.add(url);
        }
        return urls;
    }

    private static void putCenter(Map<String, Object> base, Double longitude, Double latitude, String locationText) {
        base.put(EmergencyCommandFields.LONGITUDE, longitude);
        base.put(EmergencyCommandFields.LATITUDE, latitude);
        base.put(EmergencyCommandFields.LOCATION_TEXT, StringUtils.hasText(locationText) ? locationText.trim() : null);
    }
}
