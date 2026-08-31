package cn.cheers.x.device.protocolgateway.protocol.uplink;

import cn.cheers.x.device.protocolgateway.config.DeviceProtocolGatewayProperties;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UplinkDispatcherTest {

    @Test
    void readOpcode_fromCommandResultPayload() {
        EnvelopeJsonCodec codec = new EnvelopeJsonCodec(new ObjectMapper());
        assertEquals(500202, codec.readOpcode("{\"opcode\":500202,\"msgId\":\"1\"}"));
    }

    @Test
    void dispatch_unknownOpcode_doesNotThrow() {
        DeviceProtocolGatewayProperties properties = new DeviceProtocolGatewayProperties();
        DeviceUplinkNotifier notifier = new DeviceUplinkNotifier(properties, RestClient.builder());
        UplinkDispatcher dispatcher = new UplinkDispatcher(
                new EnvelopeJsonCodec(new ObjectMapper()), notifier);
        assertDoesNotThrow(() -> dispatcher.dispatch("d1", "{\"opcode\":999999,\"msgId\":\"x\"}"));
    }
}
