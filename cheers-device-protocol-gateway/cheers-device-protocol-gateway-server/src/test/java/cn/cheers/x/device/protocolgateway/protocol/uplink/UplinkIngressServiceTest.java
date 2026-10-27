package cn.cheers.x.device.protocolgateway.protocol.uplink;

import cn.cheers.x.device.protocolgateway.api.channel.AccessChannelCodes;
import cn.cheers.x.device.protocolgateway.api.datacollection.ProtocolQualifyStatus;
import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;
import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import cn.cheers.x.device.protocolgateway.datacollection.CatalogLookup;
import cn.cheers.x.device.protocolgateway.datacollection.CollectionQualifyService;
import cn.cheers.x.device.protocolgateway.datacollection.CollectionSampleAssembler;
import cn.cheers.x.device.protocolgateway.datacollection.FieldDescriptionRow;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.protocol.exchange.RunningTaskExchangeRecorder;
import cn.cheers.x.device.protocolgateway.protocol.monitor.ProtocolMonitorHub;
import cn.cheers.x.device.protocolgateway.transport.DeviceTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 收包顺序：心跳、回执不结束等待、同步对上则完成、其余进总线。
 */
class UplinkIngressServiceTest {

    private EnvelopeJsonCodec codec;
    private DeviceTransport transport;
    private SyncReplyWaiter waiter;
    private UplinkEventPublisher publisher;
    private ProtocolMonitorHub monitorHub;
    private RunningTaskExchangeRecorder transcript;
    private UplinkIngressService ingress;

    @BeforeEach
    void setUp() {
        codec = new EnvelopeJsonCodec(new ObjectMapper());
        transport = mock(DeviceTransport.class);
        waiter = new SyncReplyWaiter();
        publisher = mock(UplinkEventPublisher.class);
        monitorHub = mock(ProtocolMonitorHub.class);
        transcript = mock(RunningTaskExchangeRecorder.class);
        CollectionQualifyService qualifyService = new CollectionQualifyService(
                new CollectionSampleAssembler(new ObjectMapper()),
                (opcode, version) -> {
                    CatalogLookup found = CatalogLookup.found(java.util.List.of(
                            new FieldDescriptionRow("opcode", "integer", "指令编码", true),
                            new FieldDescriptionRow("msgId", "string", "消息编号", true, "msgId")
                    ));
                    if (opcode == 500202) {
                        return found.withForwardTopic("inspection_execution_step");
                    }
                    return found;
                },
                deviceId -> java.util.Optional.empty());
        ingress = new UplinkIngressService(
                codec, transport, waiter, publisher, monitorHub, qualifyService, transcript,
                new GroundStationLiveStatusStore(new ObjectMapper()));
        when(transport.sendText(anyString(), anyString())).thenReturn(true);
    }

    @Test
    void heartbeat_doesNotEcho_noAck_noPublish() {
        ingress.handle(AccessChannelCodes.INSPECTION, "d1", "{\"opcode\":500105,\"msgId\":\"h1\"}");

        verify(monitorHub).copyInbound(eq("d1"), anyString());
        verify(transport, never()).sendText(anyString(), anyString());
        verify(publisher, never()).publish(org.mockito.ArgumentMatchers.any());
        verify(transcript, never()).enqueue(anyString(), anyString(), anyString());
    }

    @Test
    void inboundAck_doesNotCompleteWait_andDoesNotAckAgain() {
        CompletableFuture<String> pending = waiter.register("d1", "m-sync", 500104);
        ingress.handle(AccessChannelCodes.INSPECTION, "d1", "{\"opcode\":500106,\"msgId\":\"m-sync\",\"data\":{\"opcode\":500104}}");

        verify(transport, never()).sendText(anyString(), anyString());
        verify(publisher, never()).publish(org.mockito.ArgumentMatchers.any());
        verify(transcript).enqueue(eq("d1"), eq("inbound"), anyString());
        assertFalse(pending.isDone());
    }

    @Test
    void syncReply_completesWait_sendsAck_doesNotPublish() {
        CompletableFuture<String> pending = waiter.register("d1", "m-sync", 500104);
        String reply = "{\"opcode\":500104,\"msgId\":\"m-sync\",\"code\":200,\"msg\":\"ok\",\"data\":{}}";

        ingress.handle(AccessChannelCodes.INSPECTION, "d1", reply);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(transport).sendText(eq("d1"), captor.capture());
        assertEquals(TransportOpcode.ACK.code(), codec.readOpcode(captor.getValue()));
        verify(publisher, never()).publish(org.mockito.ArgumentMatchers.any());
        assertEquals(reply, pending.getNow(""));
        verify(transcript).enqueue(eq("d1"), eq("inbound"), eq(reply));
    }

    @Test
    void commandResult_acksAndPublishes() {
        ingress.handle(AccessChannelCodes.INSPECTION, "d1", "{\"opcode\":500202,\"msgId\":\"u1\",\"taskId\":\"t1\"}");

        ArgumentCaptor<String> ack = ArgumentCaptor.forClass(String.class);
        verify(transport).sendText(eq("d1"), ack.capture());
        assertEquals(500106, codec.readOpcode(ack.getValue()));
        ArgumentCaptor<DeviceUplinkEventDTO> event = ArgumentCaptor.forClass(DeviceUplinkEventDTO.class);
        verify(publisher).publish(event.capture());
        assertEquals(AccessChannelCodes.INSPECTION, event.getValue().channelCode());
        assertEquals("inspection_execution_step", event.getValue().messageKind());
        assertEquals(ProtocolQualifyStatus.QUALIFIED, event.getValue().sample().protocolQualify());
        assertEquals("u1", event.getValue().msgId());
    }

    @Test
    void invalidPayload_doesNotSend() {
        ingress.handle(AccessChannelCodes.INSPECTION, "d1", "not-json");
        verify(monitorHub).copyInbound("d1", "not-json");
        verify(transcript).enqueue(eq("d1"), eq("inbound"), eq("not-json"));
        verify(transport, never()).sendText(anyString(), anyString());
        verify(publisher, never()).publish(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void timedStatus_doesNotEnqueuePoseFromPacketPath() {
        String first = "{\"opcode\":500103,\"msgId\":\"s1\",\"result\":{\"lat\":\"31.1\",\"lng\":\"121.2\",\"height\":\"8\",\"time\":1624511261,\"operatePoint\":{\"lat\":\"1\",\"lng\":\"2\"}}}";
        ingress.handle(AccessChannelCodes.INSPECTION, "d1", first);

        verify(transcript, never()).enqueuePose(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void linkedReply_recordsOnTheExecutionAlreadySent() {
        when(transcript.linked("d1", "m-pkg")).thenReturn(88L);
        String reply = "{\"opcode\":500104,\"msgId\":\"m-pkg\",\"code\":200}";
        ingress.handle(AccessChannelCodes.INSPECTION, "d1", reply);

        verify(transcript).enqueue(eq("d1"), eq("inbound"), eq(reply), eq(88L));
    }

    @Test
    void blankChannel_doesNotPublish() {
        ingress.handle(" ", "d1", "{\"opcode\":500202,\"msgId\":\"u1\"}");
        verify(monitorHub, never()).copyInbound(anyString(), anyString());
        verify(transport, never()).sendText(anyString(), anyString());
        verify(publisher, never()).publish(org.mockito.ArgumentMatchers.any());
    }
}
