package cn.cheers.x.module.dynamicbusiness.service.repair;

import java.util.Map;

/**
 * 报修单自己的状态。
 *
 * 管什么：受理人改报修单状态，派工时生成工单，执行人开工完工后回写进度；完工时在没有验收审批的情况下关闭报修单。
 * 不管什么：流程引擎。当前没有派工前审批，也没有完工验收，所以这里不发起流程。
 * 禁止：用另一套状态图决定这张单能不能往下走。
 */
public interface RepairRecordService {

    /** 新建后记下「已上报」。已有进度行时不再写。 */
    void recordSubmitted(Long repairId);

    void accept(Long repairId);

    void reject(Long repairId, String remark);

    /** 受理回退。原因必填。还没派工才能回退，回退后状态为已回退。 */
    void needInfo(Long repairId, String remark);

    /**
     * 报修人按要求补完材料后交回。
     * 只有已回退的单可以交回，交回后状态回到已上报，并追加一行「已补充材料」。
     */
    void resubmit(Long repairId);

    void dispatch(Long repairId, Map<String, Object> payload);

    /** 工单已开工。报修单仍是已派工，只追加一行进度。 */
    void noteStarted(Long repairId, Long workOrderId);

    /**
     * 工单已完工。先记下「维修已完工」，再把报修单改为已关闭。
     * 没有配置完工验收，所以不再等一次审批。
     */
    void finishFromWorkOrder(Long repairId, Long workOrderId);

    /**
     * 班组里的人领走，或班长指定到人之后。
     * 报修单仍是已派工，只追加一行「已由某人领走」。不写谁指定的。
     */
    void noteAssignee(Long repairId, Long workOrderId, String personName, boolean reassign);
}
