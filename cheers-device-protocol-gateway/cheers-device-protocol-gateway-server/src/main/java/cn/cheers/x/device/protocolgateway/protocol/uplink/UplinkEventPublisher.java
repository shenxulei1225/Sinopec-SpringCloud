package cn.cheers.x.device.protocolgateway.protocol.uplink;

import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;

/**
 * 上报事件发布：把异步/主动上报带离 WebSocket 收包线程。
 * <p>正式主路径是持久化总线。本接口不规定底层是进程内队列还是消息中间件。
 * <p>禁止：在收包线程里直接 HTTP / 写库；把本接口实现成「调用即业务成功」。
 */
public interface UplinkEventPublisher {

    /**
     * 投递一条上报。队列满或不可用时必须留下可见失败（日志/指标），不得假装已投递。
     */
    void publish(DeviceUplinkEventDTO event);
}
