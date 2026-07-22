package cn.iocoder.yudao.module.emergency.service.process;

import cn.cheers.x.bpm.api.task.dto.BpmActivityNodeRespDTO;

import java.util.List;
import java.util.Map;

/**
 * 应急指挥流程运行时：start / complete 用户任务 / 服务任务回调 / 查询当前节点。
 */
public interface EmergencyProcessRuntimeService {

    /**
     * 接报后启动流程实例，并将实例 id 写回事件。
     *
     * @return 流程实例 id
     */
    String startOnEventCreated(Long eventId, Long userId);

    /**
     * 完成指定 definitionKey 的用户任务；找不到任务则显式失败。
     */
    void completeUserTask(Long eventId, Long userId, String taskDefinitionKey, Map<String, Object> vars);

    /**
     * 列出事件当前运行中的用户任务节点；未绑定时显式失败。
     */
    List<BpmActivityNodeRespDTO> listCurrentNodes(Long eventId);

    /**
     * 服务任务：启动响应 → 能力编排（禁止域内再写一套 expand）。
     *
     * @param responseLevelHint 流程变量透传的级别；为空则从最新研判台账读取
     */
    void onServiceTaskStartResponse(Long eventId, String responseLevelHint);

    /**
     * 按当前流程节点投影台账 status（展示用，非跳转真源）。
     */
    void projectLedgerStatus(Long eventId);

}
