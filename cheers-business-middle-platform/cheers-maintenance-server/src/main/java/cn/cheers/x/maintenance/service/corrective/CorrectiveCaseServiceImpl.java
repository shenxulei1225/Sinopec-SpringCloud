package cn.cheers.x.maintenance.service.corrective;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.maintenance.api.MaintenanceApi;
import cn.cheers.x.maintenance.api.dto.BindingResolveReqDTO;
import cn.cheers.x.maintenance.controller.admin.vo.corrective.*;
import cn.cheers.x.maintenance.dal.dataobject.CorrectiveCaseDO;
import cn.cheers.x.maintenance.dal.mysql.CorrectiveCaseMapper;
import cn.cheers.x.maintenance.framework.config.MaintenanceCorrectiveProperties;
import cn.cheers.x.workorder.api.WorkOrderApi;
import cn.cheers.x.workorder.api.dto.WorkOrderCreateReqDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.maintenance.enums.ErrorCodeConstants.*;

@Service
@Validated
public class CorrectiveCaseServiceImpl implements CorrectiveCaseService {

    public static final String ACCEPTED = "ACCEPTED";
    public static final String PENDING_APPROVAL = "PENDING_APPROVAL";
    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";
    public static final String IN_WORK = "IN_WORK";
    public static final String CLOSED = "CLOSED";

    @Resource private CorrectiveCaseMapper correctiveCaseMapper;
    @Resource private WorkOrderApi workOrderApi;
    @Resource private MaintenanceApi maintenanceApi;
    @Resource private CorrectiveApprovalGateway approvalGateway;
    @Resource private MaintenanceCorrectiveProperties correctiveProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CorrectiveCaseCreateReqVO reqVO) {
        CorrectiveCaseDO row = BeanUtils.toBean(reqVO, CorrectiveCaseDO.class);
        row.setCaseNo(nextCaseNo());
        row.setStatus(ACCEPTED);
        correctiveCaseMapper.insert(row);
        return row.getId();
    }

    @Override
    public CorrectiveCaseRespVO get(Long id) {
        return BeanUtils.toBean(validateExists(id), CorrectiveCaseRespVO.class);
    }

    @Override
    public PageResult<CorrectiveCaseRespVO> page(CorrectiveCasePageReqVO reqVO) {
        PageResult<CorrectiveCaseDO> page = correctiveCaseMapper.selectPage(reqVO);
        return new PageResult<>(page.getList().stream()
                .map(r -> BeanUtils.toBean(r, CorrectiveCaseRespVO.class)).toList(), page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitApproval(Long id) {
        CorrectiveCaseDO row = validateExists(id);
        assertStatus(row, ACCEPTED);
        if (correctiveProperties.isSkipApproval()) {
            updateStatus(id, APPROVED, null);
            return;
        }
        String key = approvalGateway.startApproval(id, row.getTitle());
        CorrectiveCaseDO update = new CorrectiveCaseDO();
        update.setId(id);
        update.setStatus(PENDING_APPROVAL);
        update.setProcessInstanceKey(key);
        correctiveCaseMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id, boolean approved) {
        // 同步审批（测试/skip 旁路）；生产由 onApprovalResult
        onApprovalResult(id, approved);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onApprovalResult(Long id, boolean approved) {
        CorrectiveCaseDO row = validateExists(id);
        if (!PENDING_APPROVAL.equals(row.getStatus()) && !ACCEPTED.equals(row.getStatus())) {
            throw exception(CORRECTIVE_STATUS_INVALID);
        }
        updateStatus(id, approved ? APPROVED : REJECTED, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long dispatch(Long id, CorrectiveDispatchReqVO reqVO) {
        CorrectiveCaseDO row = validateExists(id);
        assertStatus(row, APPROVED);
        Long standardId = reqVO != null ? reqVO.getFieldStandardId() : null;
        if (standardId == null) {
            standardId = row.getFieldStandardId();
        }
        if (standardId == null) {
            standardId = maintenanceApi.resolveBinding(BindingResolveReqDTO.builder()
                    .assetId(row.getAssetId())
                    .assetTypeCode(row.getAssetTypeCode())
                    .scope("corrective")
                    .build()).getCheckedData().getFieldStandardId();
        }
        WorkOrderCreateReqDTO dto = new WorkOrderCreateReqDTO();
        dto.setScope("corrective");
        dto.setTitle(row.getTitle());
        dto.setStandardId(standardId);
        dto.setAssetId(row.getAssetId());
        dto.setAssetTypeCode(row.getAssetTypeCode());
        dto.setBusinessKey(row.getCaseNo());
        dto.setCorrectiveCaseId(row.getId());
        Long woId = workOrderApi.create(dto).getCheckedData();
        CorrectiveCaseDO update = new CorrectiveCaseDO();
        update.setId(id);
        update.setStatus(IN_WORK);
        update.setWorkOrderId(woId);
        update.setFieldStandardId(standardId);
        correctiveCaseMapper.updateById(update);
        return woId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeFromWorkOrder(Long id) {
        CorrectiveCaseDO row = validateExists(id);
        assertStatus(row, IN_WORK);
        if (row.getWorkOrderId() == null) {
            throw exception(CORRECTIVE_WORK_ORDER_REQUIRED);
        }
        // MVP：由调用方保证工单已 COMPLETED；后续可查工单详情 API
        updateStatus(id, CLOSED, null);
    }

    private String nextCaseNo() {
        return "CR-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-"
                + String.format("%04d", ThreadLocalRandom.current().nextInt(1, 9999));
    }

    private CorrectiveCaseDO validateExists(Long id) {
        CorrectiveCaseDO row = correctiveCaseMapper.selectById(id);
        if (row == null) {
            throw exception(CORRECTIVE_CASE_NOT_EXISTS);
        }
        return row;
    }

    private void assertStatus(CorrectiveCaseDO row, String expected) {
        if (!expected.equals(row.getStatus())) {
            throw exception(CORRECTIVE_STATUS_INVALID);
        }
    }

    private void updateStatus(Long id, String status, String processKey) {
        CorrectiveCaseDO update = new CorrectiveCaseDO();
        update.setId(id);
        update.setStatus(status);
        if (StringUtils.hasText(processKey)) {
            update.setProcessInstanceKey(processKey);
        }
        correctiveCaseMapper.updateById(update);
    }
}
