package cn.cheers.x.iot.gateway.protocol.mqtt.handler.downstream;

import cn.cheers.x.iot.core.messagebus.core.IotMessageBus;
import cn.cheers.x.iot.core.mq.message.IotDeviceMessage;
import cn.cheers.x.iot.gateway.protocol.AbstractIotProtocolDownstreamSubscriber;
import cn.cheers.x.iot.gateway.protocol.mqtt.IotMqttProtocol;
import lombok.extern.slf4j.Slf4j;

/**
 * IoT 网关 MQTT 协议：接收下行给设备的消息
 *
 * 
 */
@Slf4j
public class IotMqttDownstreamSubscriber extends AbstractIotProtocolDownstreamSubscriber {

    private final IotMqttDownstreamHandler downstreamHandler;

    public IotMqttDownstreamSubscriber(IotMqttProtocol protocol,
                                       IotMqttDownstreamHandler downstreamHandler,
                                       IotMessageBus messageBus) {
        super(protocol, messageBus);
        this.downstreamHandler = downstreamHandler;
    }

    @Override
    protected void handleMessage(IotDeviceMessage message) {
        downstreamHandler.handle(message);
    }

}
