package cn.cheers.x.workorder.service.order;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.util.json.JsonUtils;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.module.dynamicbusiness.api.persongroup.PersonGroupMemberApi;
import cn.cheers.x.maintenance.api.MaintenanceApi;
import cn.cheers.x.maintenance.api.dto.FieldWorkStandardRespDTO;
import cn.cheers.x.workorder.api.dto.WorkOrderCreateReqDTO;
import cn.cheers.x.workorder.controller.admin.vo.order.WorkOrderPageReqVO;
import cn.cheers.x.workorder.controller.admin.vo.order.WorkOrderRespVO;
import cn.cheers.x.workorder.controller.admin.vo.order.WorkOrderStepCompleteReqVO;
import cn.cheers.x.workorder.controller.admin.vo.order.WorkOrderStepResultRespVO;
import cn.cheers.x.workorder.controller.admin.vo.standard.FieldWorkStandardStepVO;
import cn.cheers.x.workorder.dal.dataobject.FieldWorkStandardDO;
import cn.cheers.x.workorder.dal.dataobject.WorkOrderDO;
import cn.cheers.x.workorder.dal.dataobject.WorkOrderStepResultDO;
import cn.cheers.x.workorder.dal.mysql.FieldWorkStandardMapper;
import cn.cheers.x.workorder.dal.mysql.WorkOrderMapper;
import cn.cheers.x.workorder.dal.mysql.WorkOrderStepResultMapper;
import cn.cheers.x.workorder.enums.FieldWorkStandardStatusEnum;
import cn.cheers.x.workorder.enums.WorkOrderStatusEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.FIELD_WORK_STANDARD_NOT_EXISTS;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_ALREADY_ASSIGNED;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_ASSIGN_NOT_MEMBER;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_CLAIM_NOT_MEMBER;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_CLAIM_TEAM_MISSING;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_NOT_EXISTS;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_REQUIRED_STEPS_INCOMPLETE;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_STANDARD_NOT_PUBLISHED;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_STANDARD_REQUIRED;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_STATUS_INVALID;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_STEPS_EMPTY;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_STEP_NOT_EXISTS;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_CONTENT_REQUIRED;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_HANDLING_TIME_REQUIRED;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_PLAN_WINDOW_INCOMPLETE;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_TEAM_REQUIRED;

/**
 * 工单生命周期 Service 实现。
 *
 * <p>管什么：创建快照、状态流转、班组必选与执行人可选、认领/指定。</p>
 * <p>不管什么：排班在岗。认领和指定只核对人员编组里的在册成员。</p>
 * <p>禁止：缺班组仍创建；用 BPM 替代现场步骤快照。</p>
 */
@Service
@Validated
@Slf4j
public class WorkOrderServiceImpl implements WorkOrderService {

    private static final DateTimeFormatter WO_DATE = DateTimeFormatter.BASIC_ISO_DATE;

    @Resource
    private WorkOrderMapper workOrderMapper;
    @Resource
    private WorkOrderStepResultMapper workOrderStepResultMapper;
    @Resource
    private FieldWorkStandardMapper fieldWorkStandardMapper;
    @Resource
    private MaintenanceApi maintenanceApi;
    @Resource
    private PersonGroupMemberApi personGroupMemberApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFromDispatch(WorkOrderCreateReqDTO reqDTO) {
        if (reqDTO.getAssigneeTeamId() == null || reqDTO.getAssigneeTeamId() <= 0) {
            throw exception(WORK_ORDER_TEAM_REQUIRED);
        }
        boolean fromRepair = reqDTO.getBusinessKey() != null && reqDTO.getBusinessKey().startsWith("repair:");
        boolean manual = "manual".equals(reqDTO.getBusinessKey());
        if (fromRepair && reqDTO.getHandlingTime() == null) {
            throw exception(WORK_ORDER_HANDLING_TIME_REQUIRED);
        }
        if ((fromRepair || manual) && !StringUtils.hasText(reqDTO.getWorkContent())) {
            throw exception(WORK_ORDER_CONTENT_REQUIRED);
        }
        boolean hasPlanStart = reqDTO.getPlannedStartTime() != null;
        boolean hasPlanFinish = reqDTO.getPlannedFinishTime() != null;
        if (hasPlanStart != hasPlanFinish) {
            throw exception(WORK_ORDER_PLAN_WINDOW_INCOMPLETE);
        }
        // 请求带了标准编号，才抄那一本已发布流程。没带表示这一次的设备或事件还没挂流程，不套用维修域通用本。
        boolean namedStandard = reqDTO.getStandardId() != null
                || StringUtils.hasText(reqDTO.getStandardCode());
        FieldWorkStandardDO standard = namedStandard ? resolvePublishedStandard(reqDTO) : null;
        List<FieldWorkStandardStepVO> steps = standard == null
                ? Collections.emptyList()
                : deepCopySteps(standard.getStepsJson());
        if (standard != null && steps.isEmpty()) {
            throw exception(WORK_ORDER_STEPS_EMPTY);
        }

        WorkOrderDO workOrder = new WorkOrderDO();
        workOrder.setWoNo(nextWoNo());
        workOrder.setScope(reqDTO.getScope());
        workOrder.setStatus(WorkOrderStatusEnum.DISPATCHED.getStatus());
        workOrder.setTitle(reqDTO.getTitle());
        workOrder.setWorkContent(StringUtils.hasText(reqDTO.getWorkContent()) ? reqDTO.getWorkContent().trim() : null);
        workOrder.setAssetId(reqDTO.getAssetId());
        workOrder.setAssetTypeCode(reqDTO.getAssetTypeCode());
        workOrder.setFrequencyCode(reqDTO.getFrequencyCode());
        if (standard != null) {
            workOrder.setStandardId(standard.getId());
            workOrder.setStandardVersionNo(standard.getVersionNo());
            workOrder.setStandardSnapshotJson(JsonUtils.toJsonString(steps));
        }
        workOrder.setRuntimeJobId(reqDTO.getRuntimeJobId());
        workOrder.setScheduleSlotId(reqDTO.getScheduleSlotId());
        workOrder.setBusinessKey(reqDTO.getBusinessKey());
        workOrder.setAssigneeTeamId(reqDTO.getAssigneeTeamId());
        // 执行人可空：空则班组待领
        workOrder.setAssigneeUserId(reqDTO.getAssigneeUserId());
        workOrder.setCorrectiveCaseId(reqDTO.getCorrectiveCaseId());
        // 处理时刻与计划档期分开写。空着不编当前时刻。
        workOrder.setHandlingTime(reqDTO.getHandlingTime());
        workOrder.setPlannedStartTime(reqDTO.getPlannedStartTime());
        workOrder.setPlannedFinishTime(reqDTO.getPlannedFinishTime());
        workOrderMapper.insert(workOrder);

        int order = 1;
        for (FieldWorkStandardStepVO step : steps) {
            WorkOrderStepResultDO stepResult = new WorkOrderStepResultDO();
            stepResult.setWorkOrderId(workOrder.getId());
            stepResult.setStepCode(step.getCode());
            stepResult.setStepOrder(order++);
            stepResult.setCompleted(false);
            workOrderStepResultMapper.insert(stepResult);
        }

        log.info("[createFromDispatch][id={}, woNo={}, standardId={}, steps={}]",
                workOrder.getId(), workOrder.getWoNo(),
                standard == null ? null : standard.getId(), steps.size());
        return workOrder.getId();
    }

    @Override
    public WorkOrderRespVO getWorkOrder(Long id) {
        return convert(validateExists(id), true);
    }

    @Override
    public PageResult<WorkOrderRespVO> getWorkOrderPage(WorkOrderPageReqVO pageReqVO) {
        PageResult<WorkOrderDO> page = workOrderMapper.selectPage(pageReqVO);
        return new PageResult<>(page.getList().stream().map(row -> convert(row, false)).toList(), page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void start(Long id) {
        WorkOrderDO workOrder = validateExists(id);
        assertStatus(workOrder, WorkOrderStatusEnum.DISPATCHED);
        updateStatus(id, WorkOrderStatusEnum.IN_PROGRESS.getStatus());
        log.info("[start][id={}, woNo={}]", id, workOrder.getWoNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeStep(Long id, String stepCode, WorkOrderStepCompleteReqVO reqVO) {
        WorkOrderDO workOrder = validateExists(id);
        assertStatus(workOrder, WorkOrderStatusEnum.IN_PROGRESS);
        WorkOrderStepResultDO stepResult = workOrderStepResultMapper.selectByWorkOrderIdAndStepCode(id, stepCode);
        if (stepResult == null) {
            throw exception(WORK_ORDER_STEP_NOT_EXISTS);
        }

        WorkOrderStepResultDO update = new WorkOrderStepResultDO();
        update.setId(stepResult.getId());
        update.setCompleted(true);
        if (reqVO != null) {
            update.setResultJson(reqVO.getResultJson());
            update.setAttachmentIds(reqVO.getAttachmentIds());
        }
        workOrderStepResultMapper.updateById(update);
        log.info("[completeStep][id={}, stepCode={}]", id, stepCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(Long id) {
        WorkOrderDO workOrder = validateExists(id);
        assertStatus(workOrder, WorkOrderStatusEnum.IN_PROGRESS);
        assertRequiredStepsCompleted(workOrder);
        updateStatus(id, WorkOrderStatusEnum.COMPLETED.getStatus());
        log.info("[complete][id={}, woNo={}]", id, workOrder.getWoNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        WorkOrderDO workOrder = validateExists(id);
        String status = workOrder.getStatus();
        if (!WorkOrderStatusEnum.DISPATCHED.getStatus().equals(status)
                && !WorkOrderStatusEnum.IN_PROGRESS.getStatus().equals(status)
                && !WorkOrderStatusEnum.DRAFT.getStatus().equals(status)) {
            throw exception(WORK_ORDER_STATUS_INVALID);
        }
        updateStatus(id, WorkOrderStatusEnum.CANCELLED.getStatus());
        log.info("[cancel][id={}, woNo={}]", id, workOrder.getWoNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claim(Long id) {
        WorkOrderDO workOrder = validateExists(id);
        if (workOrder.getAssigneeTeamId() == null) {
            throw exception(WORK_ORDER_CLAIM_TEAM_MISSING);
        }
        if (workOrder.getAssigneeUserId() != null) {
            throw exception(WORK_ORDER_ALREADY_ASSIGNED);
        }
        Long userId = getLoginUserId();
        assertTeamMember(workOrder.getAssigneeTeamId(), userId, true);
        WorkOrderDO update = new WorkOrderDO();
        update.setId(id);
        update.setAssigneeUserId(userId);
        workOrderMapper.updateById(update);
        log.info("[claim][id={}, userId={}]", id, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assign(Long id, Long assigneeUserId) {
        if (assigneeUserId == null || assigneeUserId <= 0) {
            throw exception(WORK_ORDER_STATUS_INVALID);
        }
        WorkOrderDO workOrder = validateExists(id);
        if (workOrder.getAssigneeTeamId() == null) {
            throw exception(WORK_ORDER_CLAIM_TEAM_MISSING);
        }
        assertTeamMember(workOrder.getAssigneeTeamId(), assigneeUserId, false);
        WorkOrderDO update = new WorkOrderDO();
        update.setId(id);
        update.setAssigneeUserId(assigneeUserId);
        workOrderMapper.updateById(update);
        log.info("[assign][id={}, userId={}]", id, assigneeUserId);
    }

    /** 认领看当前登录人，指定看被指定的人。都要在这个班组的在册成员里。 */
    private void assertTeamMember(Long teamId, Long userId, boolean claim) {
        if (userId == null || userId <= 0) {
            throw exception(claim ? WORK_ORDER_CLAIM_NOT_MEMBER : WORK_ORDER_ASSIGN_NOT_MEMBER);
        }
        Boolean member = personGroupMemberApi.contains(teamId, userId).getCheckedData();
        if (!Boolean.TRUE.equals(member)) {
            throw exception(claim ? WORK_ORDER_CLAIM_NOT_MEMBER : WORK_ORDER_ASSIGN_NOT_MEMBER);
        }
    }

    /**
     * 优先从维护手册服务取已发布标准；本地表仅作波次 1 兼容回退。
     */
    private FieldWorkStandardDO resolvePublishedStandard(WorkOrderCreateReqDTO reqDTO) {
        if (reqDTO.getStandardId() != null) {
            FieldWorkStandardDO fromMaintenance = tryLoadFromMaintenance(reqDTO.getStandardId());
            if (fromMaintenance != null) {
                return fromMaintenance;
            }
            FieldWorkStandardDO standard = fieldWorkStandardMapper.selectById(reqDTO.getStandardId());
            if (standard == null) {
                throw exception(FIELD_WORK_STANDARD_NOT_EXISTS);
            }
            if (!FieldWorkStandardStatusEnum.PUBLISHED.getStatus().equals(standard.getStatus())) {
                throw exception(WORK_ORDER_STANDARD_NOT_PUBLISHED);
            }
            return standard;
        }
        if (StringUtils.hasText(reqDTO.getStandardCode())) {
            FieldWorkStandardDO standard = fieldWorkStandardMapper.selectLatestPublishedByCode(reqDTO.getStandardCode());
            if (standard == null) {
                throw exception(FIELD_WORK_STANDARD_NOT_EXISTS);
            }
            if (!FieldWorkStandardStatusEnum.PUBLISHED.getStatus().equals(standard.getStatus())) {
                throw exception(WORK_ORDER_STANDARD_NOT_PUBLISHED);
            }
            return standard;
        }
        throw exception(WORK_ORDER_STANDARD_REQUIRED);
    }

    private FieldWorkStandardDO tryLoadFromMaintenance(Long standardId) {
        try {
            CommonResult<FieldWorkStandardRespDTO> result = maintenanceApi.getPublishedStandard(standardId);
            FieldWorkStandardRespDTO dto = result.getCheckedData();
            FieldWorkStandardDO standard = new FieldWorkStandardDO();
            standard.setId(dto.getId());
            standard.setCode(dto.getCode());
            standard.setName(dto.getName());
            standard.setVersionNo(dto.getVersionNo());
            standard.setScope(dto.getScope());
            standard.setStepsJson(dto.getStepsJson());
            standard.setStatus(dto.getStatus());
            return standard;
        } catch (Exception ex) {
            log.debug("[tryLoadFromMaintenance][id={} fallback local] {}", standardId, ex.getMessage());
            return null;
        }
    }

    /**
     * 深拷贝 steps_json：parse → 新 List → 再序列化写入快照，与标准表解耦。
     */
    private List<FieldWorkStandardStepVO> deepCopySteps(String stepsJson) {
        List<FieldWorkStandardStepVO> parsed = parseSteps(stepsJson);
        if (parsed.isEmpty()) {
            return Collections.emptyList();
        }
        return JsonUtils.parseObject(JsonUtils.toJsonString(parsed),
                new TypeReference<List<FieldWorkStandardStepVO>>() {});
    }

    private String nextWoNo() {
        String prefix = "WO-" + LocalDate.now().format(WO_DATE) + "-";
        WorkOrderDO latest = workOrderMapper.selectLatestByWoNoPrefix(prefix);
        int seq = 1;
        if (latest != null && latest.getWoNo() != null && latest.getWoNo().startsWith(prefix)) {
            String suffix = latest.getWoNo().substring(prefix.length());
            try {
                seq = Integer.parseInt(suffix) + 1;
            } catch (NumberFormatException ignored) {
                seq = 1;
            }
        }
        return prefix + String.format("%04d", seq);
    }

    private void assertRequiredStepsCompleted(WorkOrderDO workOrder) {
        List<FieldWorkStandardStepVO> steps = parseSteps(workOrder.getStandardSnapshotJson());
        Set<String> requiredCodes = steps.stream()
                .filter(step -> Boolean.TRUE.equals(step.getRequired()))
                .map(FieldWorkStandardStepVO::getCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (requiredCodes.isEmpty()) {
            return;
        }
        List<WorkOrderStepResultDO> results = workOrderStepResultMapper.selectListByWorkOrderId(workOrder.getId());
        Map<String, Boolean> completedByCode = new HashMap<>();
        for (WorkOrderStepResultDO result : results) {
            completedByCode.put(result.getStepCode(), Boolean.TRUE.equals(result.getCompleted()));
        }
        for (String code : requiredCodes) {
            if (!Boolean.TRUE.equals(completedByCode.get(code))) {
                throw exception(WORK_ORDER_REQUIRED_STEPS_INCOMPLETE);
            }
        }
    }

    private WorkOrderDO validateExists(Long id) {
        WorkOrderDO workOrder = workOrderMapper.selectById(id);
        if (workOrder == null) {
            throw exception(WORK_ORDER_NOT_EXISTS);
        }
        return workOrder;
    }

    private void assertStatus(WorkOrderDO workOrder, WorkOrderStatusEnum expected) {
        if (!expected.getStatus().equals(workOrder.getStatus())) {
            throw exception(WORK_ORDER_STATUS_INVALID);
        }
    }

    private void updateStatus(Long id, String status) {
        WorkOrderDO update = new WorkOrderDO();
        update.setId(id);
        update.setStatus(status);
        workOrderMapper.updateById(update);
    }

    private WorkOrderRespVO convert(WorkOrderDO workOrder, boolean withSteps) {
        WorkOrderRespVO respVO = BeanUtils.toBean(workOrder, WorkOrderRespVO.class);
        if (withSteps) {
            List<WorkOrderStepResultDO> results = workOrderStepResultMapper.selectListByWorkOrderId(workOrder.getId());
            respVO.setSteps(results.stream()
                    .map(row -> BeanUtils.toBean(row, WorkOrderStepResultRespVO.class))
                    .toList());
        }
        return respVO;
    }

    private List<FieldWorkStandardStepVO> parseSteps(String stepsJson) {
        if (stepsJson == null || stepsJson.isBlank()) {
            return Collections.emptyList();
        }
        List<FieldWorkStandardStepVO> steps = JsonUtils.parseObject(stepsJson,
                new TypeReference<List<FieldWorkStandardStepVO>>() {});
        return steps == null ? Collections.emptyList() : steps;
    }

}
