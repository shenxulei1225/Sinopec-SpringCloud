package cn.cheers.x.module.dynamicbusiness.service.inspection;

import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapRespVO;

import java.util.List;

/**
 * 生成任务时写入待执行账和步骤。不在执行任务时再读设备参数。
 * 不调用路径服务。
 */
public interface TaskExecutionBootstrapService {

    TaskExecutionBootstrapRespVO bootstrap(TaskExecutionBootstrapReqVO req);

    /**
     * 把同一份步骤图写进已经存在的执行账。不另建步骤行。
     * 不负责重算步骤图。账不存在就报错。
     */
    void writeSameStepsOntoRecords(
            String recordType,
            String recordModelCode,
            Long facilityId,
            List<Long> executionRecordIds,
            List<TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO> steps);

    /**
     * 一次写下本批待执行账。所属场站、任务编号、计划点编号、计划执行时间、快照和步骤图写在账上。
     * 返回的编号与 accounts 顺序一致。不另建步骤行。
     */
    List<Long> insertPendingRecords(
            String recordType,
            Long modelId,
            Long facilityId,
            Long taskDefinitionId,
            Object standardSnapshot,
            Object parameterSnapshot,
            List<PendingAccount> accounts,
            List<TaskExecutionBootstrapReqVO.TaskExecutionStepDraftVO> steps);

    /**
     * 一条待执行账：给用户看的名称、计划点编号、计划执行时间。
     * plannedStart 写入 custom_fields.planned_start；缺则该行没有计划时刻，列表按空展示。
     */
    record PendingAccount(String name, String pendingRef, String plannedStart) {
    }
}
