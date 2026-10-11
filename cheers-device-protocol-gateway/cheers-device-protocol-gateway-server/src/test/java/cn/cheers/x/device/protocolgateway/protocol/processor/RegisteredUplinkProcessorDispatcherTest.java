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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 协议书允许之后，才跑对得上的处理。工业通道不走进巡检处理。
 */
class RegisteredUplinkProcessorDispatcherTest {

    @Test
    void dispatcher_runsMatchingProcessor_skipsIndustrial() {
        AtomicInteger inspectionCalls = new AtomicInteger();
        UplinkProcessor inspectionProcessor = new UplinkProcessor() {
            @Override
            public boolean supports(UplinkIdentity identity) {
                return AccessChannelCodes.INSPECTION.equals(identity.channelCode());
            }

            @Override
            public void process(UplinkIdentity identity, DeviceUplinkEventDTO event) {
                inspectionCalls.incrementAndGet();
            }
        };
        RegisteredUplinkProcessorDispatcher dispatcher =
                new RegisteredUplinkProcessorDispatcher(List.of(inspectionProcessor), admitAll());

        dispatcher.consume(event(AccessChannelCodes.INSPECTION));
        dispatcher.consume(event(AccessChannelCodes.INDUSTRIAL));

        assertEquals(1, inspectionCalls.get());
    }

    @Test
    void dispatcher_rejectsBeforeProcessors_whenBookSaysNo() {
        AtomicInteger calls = new AtomicInteger();
        UplinkProcessor processor = new UplinkProcessor() {
            @Override
            public boolean supports(UplinkIdentity identity) {
                return true;
            }

            @Override
            public void process(UplinkIdentity identity, DeviceUplinkEventDTO event) {
                calls.incrementAndGet();
            }
        };
        ProtocolBookAdmission admission = mock(ProtocolBookAdmission.class);
        when(admission.decide(org.mockito.ArgumentMatchers.any()))
                .thenReturn(ProtocolBookAdmission.Decision.reject("协议书没有操作码 200201，不送给处理模块"));
        RegisteredUplinkProcessorDispatcher dispatcher =
                new RegisteredUplinkProcessorDispatcher(List.of(processor), admission);

        dispatcher.consume(event(AccessChannelCodes.INSPECTION));

        assertEquals(0, calls.get());
    }

    @Test
    void dispatcher_noProcessor_doesNotThrow() {
        RegisteredUplinkProcessorDispatcher dispatcher =
                new RegisteredUplinkProcessorDispatcher(List.of(), admitAll());
        dispatcher.consume(event(AccessChannelCodes.INSPECTION));
    }

    private static ProtocolBookAdmission admitAll() {
        ProtocolBookAdmission admission = mock(ProtocolBookAdmission.class);
        when(admission.decide(org.mockito.ArgumentMatchers.any()))
                .thenReturn(ProtocolBookAdmission.Decision.admit("robot-ws"));
        return admission;
    }

    private static DeviceUplinkEventDTO event(String channel) {
        return DeviceUplinkEventDTO.of(new CollectionSample(
                channel,
                "d1",
                "500202",
                ProtocolQualifyStatus.QUALIFIED,
                List.of(),
                Map.of("opcode", 500202),
                null,
                "m1",
                1L));
    }
}
