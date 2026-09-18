package cn.cheers.x.inspection.task.service.execution;

import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;

/**
 * 巡检采集适配：把样本交给策略运行时记账。
 * <p>不管：协议编解码、WebSocket 会话、字段说明核对、自己往账上拼过程列表。
 * <p>禁止：再拆原始报文；工业样本走进本方法。
 */
public interface InspectionDeviceUplinkService {

    /**
     * 消费采集样本。先走策略记过程；步骤完成暂留过渡。
     * 不合格：只记错误，不写步骤完成。
     */
    void applyCollection(CollectionSample sample);
}
