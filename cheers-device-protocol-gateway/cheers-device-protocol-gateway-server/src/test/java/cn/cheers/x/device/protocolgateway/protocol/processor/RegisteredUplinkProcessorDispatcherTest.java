package cn.cheers.x.device.protocolgateway.protocol.processor;

import cn.cheers.x.device.protocolgateway.api.channel.AccessChannelCodes;
import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.api.datacollection.ProtocolQualifyStatus;
import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;
import cn.cheers.x.device.protocolgateway.api.identity.UplinkIdentity;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 只跑已登记方法：巡检占位认巡检通道；工业通道不走进巡检处理。
 */
class RegisteredUplinkProcessorDispatcherTest {

    @Test
    void inspectionProcessor_supportsInspectionOnly() {
        InspectionIdentifyOnlyProcessor processor = new InspectionIdentifyOnlyProcessor();
        assertTrue(processor.supports(event(AccessChannelCodes.INSPECTION).identity()));
        assertFalse(processor.supports(event(AccessChannelCodes.INDUSTRIAL).identity()));
    }

    @Test
    void dispatcher_runsMatchingProcessor_skipsIndustrial() {
        AtomicInteger inspectionCalls = new AtomicInteger();
        InspectionIdentifyOnlyProcessor inspectionProcessor = new InspectionIdentifyOnlyProcessor() {
            @Override
            public void process(UplinkIdentity identity, DeviceUplinkEventDTO event) {
                inspectionCalls.incrementAndGet();
            }
        };
        RegisteredUplinkProcessorDispatcher dispatcher =
                new RegisteredUplinkProcessorDispatcher(List.of(inspectionProcessor));

        dispatcher.consume(event(AccessChannelCodes.INSPECTION));
        dispatcher.consume(event(AccessChannelCodes.INDUSTRIAL));

        assertEquals(1, inspectionCalls.get());
    }

    @Test
    void dispatcher_noProcessor_doesNotThrow() {
        RegisteredUplinkProcessorDispatcher dispatcher =
                new RegisteredUplinkProcessorDispatcher(List.of());
        dispatcher.consume(event(AccessChannelCodes.INSPECTION));
    }

    private static DeviceUplinkEventDTO event(String channel) {
        return DeviceUplinkEventDTO.of(new CollectionSample(
                channel,
                "d1",
                "500202",
                ProtocolQualifyStatus.UNCHECKED,
                List.of(),
                Map.of("opcode", 500202),
                null,
                "m1",
                1L));
    }
}
