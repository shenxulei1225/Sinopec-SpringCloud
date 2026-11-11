package cn.cheers.x.module.dynamicbusiness.service.ledgerstate;

import java.util.List;
import java.util.Map;

/**
 * 台账办事状态。
 *
 * 管什么：按已发布的状态定义列出当前页能做的转移，校验后改状态并追加一行脉络。
 * 不管什么：作业标准步骤、审批流节点。
 * 禁止：页面上没有的转移仍放行；失败后仍改状态或补一条没发生的脉络。
 */
public interface LedgerStateMachineService {

    List<LedgerTransitionView> listAvailable(String entityTypeCode, Long entityId, String pageCode);

    void execute(String entityTypeCode, Long entityId, String toState, String pageCode, Map<String, Object> payload);

    /** 新建报修已写成已上报之后，补第一行脉络。状态不再改一次。 */
    void recordSubmitted(String entityTypeCode, Long entityId);

    /**
     * 工单开工或完工只追加脉络。
     * 不改报修状态。句子只能是已开始维修、维修已完工。
     */
    void appendProgress(String entityTypeCode, Long entityId, Long workOrderId, String sentence);

    /** 工单验收通过后，走「已派工 → 已关闭」。受理页不出现这个按钮。 */
    void closeAfterAcceptance(String entityTypeCode, Long entityId);

    List<String> listTraceLines(String entityTypeCode, Long entityId);
}
