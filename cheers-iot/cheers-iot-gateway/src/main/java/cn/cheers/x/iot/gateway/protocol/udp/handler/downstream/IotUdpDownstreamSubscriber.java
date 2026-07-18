package cn.cheers.x.iot.gateway.protocol.udp.handler.downstream;

import cn.cheers.x.iot.core.messagebus.core.IotMessageBus;
import cn.cheers.x.iot.core.mq.message.IotDeviceMessage;
import cn.cheers.x.iot.gateway.protocol.AbstractIotProtocolDownstreamSubscriber;
import cn.cheers.x.iot.gateway.protocol.IotProtocol;
import lombok.extern.slf4j.Slf4j;

/**
 * IoT 网关 UDP 下游订阅者：接收下行给设备的消息
 *
 * 
 */
@Slf4j
public class IotUdpDownstreamSubscriber extends AbstractIotProtocolDownstreamSubscriber {

    private final IotUdpDownstreamHandler downstreamHandler;

    public IotUdpDownstreamSubscriber(IotProtocol protocol,
                                      IotUdpDownstreamHandler downstreamHandler,
                                      IotMessageBus messageBus) {
        super(protocol, messageBus);
        this.downstreamHandler = downstreamHandler;
    }

    @Override
    protected void handleMessage(IotDeviceMessage message) {
        downstreamHandler.handle(message);
    }

}
