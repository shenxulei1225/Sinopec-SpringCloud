package cn.iocoder.yudao.module.inspection.task.service.execution.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.execution.InspectionTaskExecutionDO;
import cn.iocoder.yudao.module.inspection.task.dal.mysql.execution.InspectionTaskExecutionMapper;
import cn.iocoder.yudao.module.inspection.task.service.execution.InspectionTaskExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;

/**
 * 巡检任务执行记录 Service 实现类。
 *
 * <p>执行记录的管理，包括创建、更新、查询等操作。</p>
 */
@Service
@RequiredArgsConstructor
public class InspectionTaskExecutionServiceImpl implements InspectionTaskExecutionService {

    private final InspectionTaskExecutionMapper executionMapper;

    // ==================== 基础操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createExecution(InspectionTaskExecutionDO execution) {
        executionMapper.insert(execution);
        return execution.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateExecution(InspectionTaskExecutionDO execution) {
        validateExecutionExists(execution.getId());
        executionMapper.updateById(execution);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteExecution(Long id) {
        validateExecutionExists(id);
        executionMapper.deleteById(id);
    }

    @Override
    public InspectionTaskExecutionDO getExecution(Long id) {
        return executionMapper.selectById(id);
    }

    @Override
    public List<InspectionTaskExecutionDO> getExecutionsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return executionMapper.selectListByIds(ids);
    }

    // ==================== 任务维度操作 ====================

    @Override
    public List<InspectionTaskExecutionDO> getExecutionsByTaskId(Long taskId) {
        return executionMapper.selectListByTaskId(taskId);
    }

    // ==================== 计划点维度操作 ====================

    @Override
    public InspectionTaskExecutionDO getExecutionByScheduleId(Long scheduleId) {
        return executionMapper.selectByScheduleId(scheduleId);
    }

    // ==================== 资源维度操作 ====================

    @Override
    public List<InspectionTaskExecutionDO> getExecutionsByResourceId(Long resourceId) {
        return executionMapper.selectListByResourceId(resourceId);
    }

    // ==================== 私有方法 ====================

    private InspectionTaskExecutionDO validateExecutionExists(Long id) {
        InspectionTaskExecutionDO execution = executionMapper.selectById(id);
        if (execution == null) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "执行记录不存在");
        }
        return execution;
    }
}
