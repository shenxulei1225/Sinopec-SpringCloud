package cn.cheers.x.device.protocolgateway.protocol.monitor;

import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorEventDTO;

/**
 * 把窗口里的原文变成页面上能读的一行行字段。
 * <p>负责：推送和回放前填中文名。
 * <p>不负责：改窗口里保存的原文。
 */
@FunctionalInterface
public interface ProtocolMonitorFieldPresenter {

    ProtocolMonitorEventDTO present(ProtocolMonitorEventDTO event);
}
