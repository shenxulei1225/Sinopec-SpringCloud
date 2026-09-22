package cn.cheers.x.inspection.task.service.task.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskCreateReqVO;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskUpdateReqVO;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.service.task.InspectionTaskService;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;

/**
 * 巡检任务写服务：只写总任务（ent_task），不读固定表 inspection_task。
 */
@Service
@RequiredArgsConstructor
public class InspectionTaskServiceImpl implements InspectionTaskService {

    private final PatrolTaskEntityStore patrolTaskEntityStore;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTask(InspectionTaskCreateReqVO reqVO) {
        validateExecutionDeviceBinding(reqVO.getExecutionDeviceBinding());
        return patrolTaskEntityStore.createDraft(reqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTask(InspectionTaskUpdateReqVO reqVO) {
        validateExecutionDeviceBinding(reqVO.getExecutionDeviceBinding());
        patrolTaskEntityStore.updateDraft(reqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(Long id) {
        patrolTaskEntityStore.delete(id);
    }

    private void validateExecutionDeviceBinding(ExecutionDeviceBinding binding) {
        if (binding == null) {
            return;
        }
        boolean hasEquipment = binding.getEquipmentId() != null;
        boolean hasProtocol = StringUtils.hasText(binding.getProtocolCode());
        boolean hasLogical = StringUtils.hasText(binding.getLogicalDeviceId());
        if (!hasEquipment && !hasProtocol && !hasLogical) {
            return;
        }
        if (!hasEquipment || !hasProtocol || !hasLogical) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST,
                    "执行设备绑定须同时提供 equipmentId、protocolCode、logicalDeviceId");
        }
    }
}
