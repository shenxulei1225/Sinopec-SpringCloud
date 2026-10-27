package cn.cheers.x.device.protocolgateway.protocol.exchange;

import cn.cheers.x.device.protocolgateway.datacollection.DeviceProtocolVersionCatalog;
import cn.cheers.x.device.protocolgateway.datacollection.ProtocolInstructionCatalog;
import cn.cheers.x.device.protocolgateway.protocol.uplink.UplinkEventPublisher;
import cn.cheers.x.inspection.task.api.InspectionDeviceUplinkApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

class RunningTaskExchangeRecorderLinkTest {

    private static RunningTaskExchangeRecorder recorder() {
        return new RunningTaskExchangeRecorder(
                mock(InspectionDeviceUplinkApi.class),
                mock(ProtocolInstructionCatalog.class),
                mock(DeviceProtocolVersionCatalog.class),
                mock(UplinkEventPublisher.class),
                new ObjectMapper());
    }

    @Test
    void linkedReplyIsUsedOnce() {
        RunningTaskExchangeRecorder recorder = recorder();
        recorder.remember("d1", "m1", 88L);

        assertEquals(88L, recorder.linked("d1", "m1"));
        assertNull(recorder.linked("d1", "m1"));
    }

    @Test
    void ackCanLookWithoutTakingTheReplyLink() {
        RunningTaskExchangeRecorder recorder = recorder();
        recorder.remember("d1", "m1", 88L);

        assertEquals(88L, recorder.peek("d1", "m1"));
        assertEquals(88L, recorder.linked("d1", "m1"));
        assertNull(recorder.linked("d1", "m1"));
    }

    @Test
    void timedStatusStaysOnTheExecutionThatOpenedIt() {
        RunningTaskExchangeRecorder recorder = recorder();
        recorder.bindTimedStatus("d1", 88L);

        assertEquals(88L, recorder.timedStatusRecord("d1"));
        assertEquals(88L, recorder.timedStatusRecord("d1"));
        recorder.clearTimedStatus("d1");
        assertNull(recorder.timedStatusRecord("d1"));
    }

    @Test
    void forgetDropsTheLink() {
        RunningTaskExchangeRecorder recorder = recorder();
        recorder.remember("d1", "m1", 88L);
        recorder.forget("d1", "m1");

        assertNull(recorder.linked("d1", "m1"));
    }
}
