package cn.cheers.x.workorder.service.order;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.util.json.JsonUtils;
import cn.cheers.x.framework.common.util.object.BeanUtils;
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
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.FIELD_WORK_STANDARD_NOT_EXISTS;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_NOT_EXISTS;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_REQUIRED_STEPS_INCOMPLETE;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_STANDARD_NOT_PUBLISHED;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_STANDARD_REQUIRED;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_STATUS_INVALID;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_STEPS_EMPTY;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.WORK_ORDER_STEP_NOT_EXISTS;

/**
 * 工单生命周期 Service 实现
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFromDispatch(WorkOrderCreateReqDTO reqDTO) {
        FieldWorkStandardDO standard = resolvePublishedStandard(reqDTO);
        List<FieldWorkStandardStepVO> steps = deepCopySteps(standard.getStepsJson());
        if (steps.isEmpty()) {
            throw exception(WORK_ORDER_STEPS_EMPTY);
        }

        String snapshotJson = JsonUtils.toJsonString(steps);
        WorkOrderDO workOrder = new WorkOrderDO();
        workOrder.setWoNo(nextWoNo());
        workOrder.setScope(reqDTO.getScope());
        workOrder.setStatus(WorkOrderStatusEnum.DISPATCHED.getStatus());
        workOrder.setTitle(reqDTO.getTitle());
        workOrder.setAssetId(reqDTO.getAssetId());
        workOrder.setAssetTypeCode(reqDTO.getAssetTypeCode());
        workOrder.setFrequencyCode(reqDTO.getFrequencyCode());
        workOrder.setStandardId(standard.getId());
        workOrder.setStandardVersionNo(standard.getVersionNo());
        workOrder.setStandardSnapshotJson(snapshotJson);
        workOrder.setRuntimeJobId(reqDTO.getRuntimeJobId());
        workOrder.setScheduleSlotId(reqDTO.getScheduleSlotId());
        workOrder.setBusinessKey(reqDTO.getBusinessKey());
        workOrder.setAssigneeUserId(reqDTO.getAssigneeUserId());
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

        log.info("[createFromDispatch][id={}, woNo={}, standardId={}, versionNo={}, steps={}]",
                workOrder.getId(), workOrder.getWoNo(), standard.getId(), standard.getVersionNo(), steps.size());
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

    private FieldWorkStandardDO resolvePublishedStandard(WorkOrderCreateReqDTO reqDTO) {
        FieldWorkStandardDO standard;
        if (reqDTO.getStandardId() != null) {
            standard = fieldWorkStandardMapper.selectById(reqDTO.getStandardId());
            if (standard == null) {
                throw exception(FIELD_WORK_STANDARD_NOT_EXISTS);
            }
        } else if (StringUtils.hasText(reqDTO.getStandardCode())) {
            standard = fieldWorkStandardMapper.selectLatestPublishedByCode(reqDTO.getStandardCode());
            if (standard == null) {
                throw exception(FIELD_WORK_STANDARD_NOT_EXISTS);
            }
        } else {
            throw exception(WORK_ORDER_STANDARD_REQUIRED);
        }
        if (!FieldWorkStandardStatusEnum.PUBLISHED.getStatus().equals(standard.getStatus())) {
            throw exception(WORK_ORDER_STANDARD_NOT_PUBLISHED);
        }
        return standard;
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
