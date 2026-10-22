package cn.cheers.x.device.protocolgateway.protocol.monitor;

import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorDownlinkResultDTO;
import cn.cheers.x.device.protocolgateway.datacollection.DeviceRegistrationLookup;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.api.dto.PatrolGeneratedTaskDownlinkDTO;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.protocol.exchange.RunningTaskExchangeRecorder;
import cn.cheers.x.device.protocolgateway.service.MissionTranslationService;
import cn.cheers.x.device.protocolgateway.transport.DeviceTransport;
import cn.cheers.x.inspection.task.api.InspectionDeviceUplinkApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 监控页下发写出后就返回，不等设备同号答卷。
 */
class ProtocolMonitorDownlinkServiceTest {

    @Test
    void syncOpcode_returnsAfterWrite() {
        DeviceRegistrationLookup registration = mock(DeviceRegistrationLookup.class);
        ProtocolMonitorDownlinkCatalog catalog = mock(ProtocolMonitorDownlinkCatalog.class);
        EnvelopeJsonCodec codec = mock(EnvelopeJsonCodec.class);
        DeviceTransport transport = mock(DeviceTransport.class);
        RunningTaskExchangeRecorder recorder = mock(RunningTaskExchangeRecorder.class);
        ProtocolMonitorDownlinkService service = new ProtocolMonitorDownlinkService(
                registration,
                catalog,
                codec,
                transport,
                recorder,
                mock(InspectionDeviceUplinkApi.class),
                mock(MissionTranslationService.class));
        when(registration.registrationOf("dev-1"))
                .thenReturn(new DeviceRegistrationLookup.Registration("robot-ws", ""));
        when(catalog.require("robot-ws", 500101)).thenReturn(new ProtocolMonitorDownlinkCatalog.Prepared(
                "获取设备实时状态",
                500101,
                Map.of("opcode", 500101, "msgId", "", "deviceId", ""),
                true,
                null,
                "",
                "500101",
                "downlink"));
        when(codec.encodeObject(any())).thenReturn("{\"opcode\":500101}");
        when(transport.isOnline(anyString())).thenReturn(true);
        when(transport.sendText(eq("dev-1"), anyString())).thenReturn(true);

        ProtocolMonitorDownlinkResultDTO result = service.send("dev-1", 500101);

        assertTrue(result.written());
        assertFalse(result.businessSuccess());
        assertTrue(result.note().contains("已提交下发"));
        verify(transport).sendText(eq("dev-1"), eq("{\"opcode\":500101}"));
        verify(recorder).enqueue(eq("dev-1"), anyString(), eq("{\"opcode\":500101}"), isNull());
        verify(recorder, never()).remember(anyString(), anyString(), anyLong());
    }

    @Test
    void plainStatus_withSelectedExecution_isRecordedOnThatExecution() {
        DeviceRegistrationLookup registration = mock(DeviceRegistrationLookup.class);
        ProtocolMonitorDownlinkCatalog catalog = mock(ProtocolMonitorDownlinkCatalog.class);
        EnvelopeJsonCodec codec = mock(EnvelopeJsonCodec.class);
        DeviceTransport transport = mock(DeviceTransport.class);
        RunningTaskExchangeRecorder recorder = mock(RunningTaskExchangeRecorder.class);
        ProtocolMonitorDownlinkService service = new ProtocolMonitorDownlinkService(
                registration,
                catalog,
                codec,
                transport,
                recorder,
                mock(InspectionDeviceUplinkApi.class),
                mock(MissionTranslationService.class));
        when(registration.registrationOf("dev-1"))
                .thenReturn(new DeviceRegistrationLookup.Registration("robot-ws", ""));
        when(catalog.require("robot-ws", 500101)).thenReturn(new ProtocolMonitorDownlinkCatalog.Prepared(
                "获取设备实时状态",
                500101,
                new LinkedHashMap<>(Map.of("opcode", 500101, "msgId", "", "deviceId", "")),
                true,
                null,
                "",
                "500101",
                "downlink"));
        when(codec.encodeObject(any())).thenReturn("{\"opcode\":500101}");
        when(transport.isOnline(anyString())).thenReturn(true);
        when(transport.sendText(eq("dev-1"), anyString())).thenReturn(true);

        ProtocolMonitorDownlinkResultDTO result = service.send(
                "dev-1", 500101, null, null, null, null, 88L);

        assertTrue(result.written());
        verify(recorder).remember(eq("dev-1"), anyString(), eq(88L));
        verify(recorder).enqueue(eq("dev-1"), anyString(), eq("{\"opcode\":500101}"), eq(88L));
    }

    @Test
    void timedReport_start_usesProtocolSample() {
        DeviceRegistrationLookup registration = mock(DeviceRegistrationLookup.class);
        ProtocolMonitorDownlinkCatalog catalog = mock(ProtocolMonitorDownlinkCatalog.class);
        EnvelopeJsonCodec codec = mock(EnvelopeJsonCodec.class);
        DeviceTransport transport = mock(DeviceTransport.class);
        ProtocolMonitorDownlinkService service = new ProtocolMonitorDownlinkService(
                registration,
                catalog,
                codec,
                transport,
                mock(RunningTaskExchangeRecorder.class),
                mock(InspectionDeviceUplinkApi.class),
                mock(MissionTranslationService.class));
        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("opcode", 500102);
        outbound.put("msgId", "");
        outbound.put("deviceId", "");
        outbound.put("enable", false);
        outbound.put("frequency", 0);
        outbound.put("total", 0);
        when(registration.registrationOf("dev-1"))
                .thenReturn(new DeviceRegistrationLookup.Registration("robot-ws", ""));
        when(catalog.require("robot-ws", 500102)).thenReturn(new ProtocolMonitorDownlinkCatalog.Prepared(
                "启动/关闭定时上报", 500102, outbound, true, null, "", "500102", "downlink"));
        when(transport.isOnline(anyString())).thenReturn(true);
        when(transport.sendText(eq("dev-1"), anyString())).thenReturn(true);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> packet = ArgumentCaptor.forClass(Map.class);
        when(codec.encodeObject(packet.capture())).thenReturn("{\"opcode\":500102}");

        ProtocolMonitorDownlinkResultDTO result = service.send("dev-1", 500102, true, 30, 100);

        assertEquals(Boolean.TRUE, packet.getValue().get("enable"));
        assertEquals(30, ((Number) packet.getValue().get("frequency")).intValue());
        assertEquals(100, ((Number) packet.getValue().get("total")).intValue());
        assertTrue(result.note().contains("启动定时上报"));
    }

    @Test
    void streamStart_writesPageAddress() {
        DeviceRegistrationLookup registration = mock(DeviceRegistrationLookup.class);
        ProtocolMonitorDownlinkCatalog catalog = mock(ProtocolMonitorDownlinkCatalog.class);
        EnvelopeJsonCodec codec = mock(EnvelopeJsonCodec.class);
        DeviceTransport transport = mock(DeviceTransport.class);
        ProtocolMonitorDownlinkService service = new ProtocolMonitorDownlinkService(
                registration,
                catalog,
                codec,
                transport,
                mock(RunningTaskExchangeRecorder.class),
                mock(InspectionDeviceUplinkApi.class),
                mock(MissionTranslationService.class));
        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("opcode", 500401);
        outbound.put("msgId", "");
        outbound.put("deviceId", "");
        outbound.put("streamUrl", "");
        when(registration.registrationOf("dev-1"))
                .thenReturn(new DeviceRegistrationLookup.Registration("robot-ws", ""));
        when(catalog.require("robot-ws", 500401)).thenReturn(new ProtocolMonitorDownlinkCatalog.Prepared(
                "启动推流", 500401, outbound, true, null, "", "500401", "downlink"));
        when(transport.isOnline(anyString())).thenReturn(true);
        when(transport.sendText(eq("dev-1"), anyString())).thenReturn(true);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> packet = ArgumentCaptor.forClass(Map.class);
        when(codec.encodeObject(packet.capture())).thenReturn("{\"opcode\":500401}");

        ProtocolMonitorDownlinkResultDTO result = service.send(
                "dev-1", 500401, null, null, null, "rtsp://127.0.0.1:8554/patrol-1");

        assertEquals("rtsp://127.0.0.1:8554/patrol-1", packet.getValue().get("streamUrl"));
        assertTrue(result.note().contains("rtsp://127.0.0.1:8554/patrol-1"));
    }

    @Test
    void streamStart_withoutPageAddress_rejects() {
        DeviceRegistrationLookup registration = mock(DeviceRegistrationLookup.class);
        ProtocolMonitorDownlinkCatalog catalog = mock(ProtocolMonitorDownlinkCatalog.class);
        DeviceTransport transport = mock(DeviceTransport.class);
        ProtocolMonitorDownlinkService service = new ProtocolMonitorDownlinkService(
                registration,
                catalog,
                mock(EnvelopeJsonCodec.class),
                transport,
                mock(RunningTaskExchangeRecorder.class),
                mock(InspectionDeviceUplinkApi.class),
                mock(MissionTranslationService.class));
        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("opcode", 500401);
        outbound.put("msgId", "");
        outbound.put("deviceId", "");
        outbound.put("streamUrl", "");
        when(registration.registrationOf("dev-1"))
                .thenReturn(new DeviceRegistrationLookup.Registration("robot-ws", ""));
        when(catalog.require("robot-ws", 500401)).thenReturn(new ProtocolMonitorDownlinkCatalog.Prepared(
                "启动推流", 500401, outbound, true, null, "", "500401", "downlink"));
        when(transport.isOnline("dev-1")).thenReturn(true);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> service.send("dev-1", 500401));

        assertEquals("启动推流要填写推流地址", error.getMessage());
    }

    @Test
    void commandPackage_writesTheGeneratedPackage() throws Exception {
        DeviceRegistrationLookup registration = mock(DeviceRegistrationLookup.class);
        DeviceTransport transport = mock(DeviceTransport.class);
        InspectionDeviceUplinkApi uplink = mock(InspectionDeviceUplinkApi.class);
        MissionTranslationService translation = mock(MissionTranslationService.class);
        ObjectMapper mapper = new ObjectMapper();
        ProtocolMonitorDownlinkService service = new ProtocolMonitorDownlinkService(
                registration,
                mock(ProtocolMonitorDownlinkCatalog.class),
                new EnvelopeJsonCodec(mapper),
                transport,
                mock(RunningTaskExchangeRecorder.class),
                uplink,
                translation);
        PatrolGeneratedTaskDownlinkDTO task = new PatrolGeneratedTaskDownlinkDTO();
        task.setExecutionRecordId(923020L);
        task.setRecordName("每周巡检");
        task.setProtocolVersion("robot-ws");
        task.setCommandTemplateId("patrol-task-910101");
        task.setCommandPackagesJson(
                "[{\"opcode\":200102,\"sequence\":1,\"request\":{\"lat\":\"\",\"lng\":\"\",\"height\":\"\",\"heading\":\"\",\"pointId\":\"P1\"}}]");
        when(uplink.generatedTask("dev-1")).thenReturn(CommonResult.success(task));
        when(registration.registrationOf("dev-1"))
                .thenReturn(new DeviceRegistrationLookup.Registration("robot-ws", ""));
        when(transport.isOnline(anyString())).thenReturn(true);
        when(transport.sendText(eq("dev-1"), anyString())).thenReturn(true);
        ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);

        ProtocolMonitorDownlinkResultDTO result = service.send("dev-1", 500104);

        verify(translation, never()).translate(any(), eq(false));
        verify(transport).sendText(eq("dev-1"), payload.capture());
        JsonNode root = mapper.readTree(payload.getValue());
        assertEquals(500104, root.get("opcode").asInt());
        assertEquals("patrol-task-910101", root.get("templateId").asText());
        assertEquals("P1", root.get("packages").get(0).get("request").get("pointId").asText());
        assertTrue(root.get("packages").get(0).get("request").has("heading"));
        assertTrue(result.note().contains("生成时记下的指令包"));
    }

    @Test
    void commandPackage_usesTheRecordSelectedOnThePage() {
        DeviceRegistrationLookup registration = mock(DeviceRegistrationLookup.class);
        DeviceTransport transport = mock(DeviceTransport.class);
        InspectionDeviceUplinkApi uplink = mock(InspectionDeviceUplinkApi.class);
        ProtocolMonitorDownlinkService service = new ProtocolMonitorDownlinkService(
                registration,
                mock(ProtocolMonitorDownlinkCatalog.class),
                new EnvelopeJsonCodec(new ObjectMapper()),
                transport,
                mock(RunningTaskExchangeRecorder.class),
                uplink,
                mock(MissionTranslationService.class));
        PatrolGeneratedTaskDownlinkDTO task = new PatrolGeneratedTaskDownlinkDTO();
        task.setExecutionRecordId(923053L);
        task.setProtocolVersion("robot-ws");
        task.setCommandPackagesJson("[{\"opcode\":200102,\"sequence\":1,\"request\":{}}]");
        when(uplink.generatedTaskRecord("dev-1", 923053L)).thenReturn(CommonResult.success(task));
        when(registration.registrationOf("dev-1"))
                .thenReturn(new DeviceRegistrationLookup.Registration("robot-ws", ""));
        when(transport.isOnline(anyString())).thenReturn(true);
        when(transport.sendText(eq("dev-1"), anyString())).thenReturn(true);

        service.send("dev-1", 500104, null, null, null, null, 923053L);

        verify(uplink).generatedTaskRecord("dev-1", 923053L);
        verify(uplink, never()).generatedTask(any());
    }

    @Test
    void timedReport_withoutSwitch_isRejected() {
        DeviceRegistrationLookup registration = mock(DeviceRegistrationLookup.class);
        ProtocolMonitorDownlinkCatalog catalog = mock(ProtocolMonitorDownlinkCatalog.class);
        DeviceTransport transport = mock(DeviceTransport.class);
        when(transport.isOnline(anyString())).thenReturn(true);
        ProtocolMonitorDownlinkService service = new ProtocolMonitorDownlinkService(
                registration,
                catalog,
                mock(EnvelopeJsonCodec.class),
                transport,
                mock(RunningTaskExchangeRecorder.class),
                mock(InspectionDeviceUplinkApi.class),
                mock(MissionTranslationService.class));
        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("opcode", 500102);
        outbound.put("msgId", "");
        outbound.put("deviceId", "");
        outbound.put("enable", false);
        outbound.put("frequency", 0);
        outbound.put("total", 0);
        when(registration.registrationOf("dev-1"))
                .thenReturn(new DeviceRegistrationLookup.Registration("robot-ws", ""));
        when(catalog.require("robot-ws", 500102)).thenReturn(new ProtocolMonitorDownlinkCatalog.Prepared(
                "启动/关闭定时上报", 500102, outbound, true, null, "", "500102", "downlink"));

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class, () -> service.send("dev-1", 500102));

        assertTrue(error.getMessage().contains("开关"));
    }

    @Test
    void startup_usesTemplateIdFromTheCommandPackage() throws Exception {
        DeviceRegistrationLookup registration = mock(DeviceRegistrationLookup.class);
        DeviceTransport transport = mock(DeviceTransport.class);
        InspectionDeviceUplinkApi uplink = mock(InspectionDeviceUplinkApi.class);
        ObjectMapper mapper = new ObjectMapper();
        ProtocolMonitorDownlinkService service = new ProtocolMonitorDownlinkService(
                registration,
                mock(ProtocolMonitorDownlinkCatalog.class),
                new EnvelopeJsonCodec(mapper),
                transport,
                mock(RunningTaskExchangeRecorder.class),
                uplink,
                mock(MissionTranslationService.class));
        PatrolGeneratedTaskDownlinkDTO task = new PatrolGeneratedTaskDownlinkDTO();
        task.setExecutionRecordId(923020L);
        task.setProtocolVersion("robot-ws");
        task.setExecutionTimeEpochSeconds(1_700_000_000L);
        task.setCommandTemplateId("patrol-task-910101");
        when(uplink.generatedTaskRecord("dev-1", 923020L)).thenReturn(CommonResult.success(task));
        when(registration.registrationOf("dev-1"))
                .thenReturn(new DeviceRegistrationLookup.Registration("robot-ws", ""));
        when(transport.isOnline(anyString())).thenReturn(true);
        when(transport.sendText(eq("dev-1"), anyString())).thenReturn(true);
        ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);

        service.send("dev-1", 500201, null, null, null, null, 923020L);

        verify(transport).sendText(eq("dev-1"), payload.capture());
        JsonNode root = mapper.readTree(payload.getValue());
        assertEquals("923020", root.get("taskId").asText());
        assertEquals("patrol-task-910101", root.get("templateId").asText());
    }

    @Test
    void startup_typedTemplateIdReplacesThePackageOne() throws Exception {
        DeviceRegistrationLookup registration = mock(DeviceRegistrationLookup.class);
        DeviceTransport transport = mock(DeviceTransport.class);
        InspectionDeviceUplinkApi uplink = mock(InspectionDeviceUplinkApi.class);
        ObjectMapper mapper = new ObjectMapper();
        ProtocolMonitorDownlinkService service = new ProtocolMonitorDownlinkService(
                registration,
                mock(ProtocolMonitorDownlinkCatalog.class),
                new EnvelopeJsonCodec(mapper),
                transport,
                mock(RunningTaskExchangeRecorder.class),
                uplink,
                mock(MissionTranslationService.class));
        PatrolGeneratedTaskDownlinkDTO task = new PatrolGeneratedTaskDownlinkDTO();
        task.setExecutionRecordId(923020L);
        task.setProtocolVersion("robot-ws");
        task.setExecutionTimeEpochSeconds(1_700_000_000L);
        task.setCommandTemplateId("patrol-task-910101");
        when(uplink.generatedTaskRecord("dev-1", 923020L)).thenReturn(CommonResult.success(task));
        when(registration.registrationOf("dev-1"))
                .thenReturn(new DeviceRegistrationLookup.Registration("robot-ws", ""));
        when(transport.isOnline(anyString())).thenReturn(true);
        when(transport.sendText(eq("dev-1"), anyString())).thenReturn(true);
        ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);

        service.send("dev-1", 500201, null, null, null, null, 923020L, "typed-9");

        verify(transport).sendText(eq("dev-1"), payload.capture());
        JsonNode root = mapper.readTree(payload.getValue());
        assertEquals("923020", root.get("taskId").asText());
        assertEquals("typed-9", root.get("templateId").asText());
    }

    @Test
    void startup_withoutTemplateId_isRejected() {
        DeviceRegistrationLookup registration = mock(DeviceRegistrationLookup.class);
        DeviceTransport transport = mock(DeviceTransport.class);
        InspectionDeviceUplinkApi uplink = mock(InspectionDeviceUplinkApi.class);
        ProtocolMonitorDownlinkService service = new ProtocolMonitorDownlinkService(
                registration,
                mock(ProtocolMonitorDownlinkCatalog.class),
                new EnvelopeJsonCodec(new ObjectMapper()),
                transport,
                mock(RunningTaskExchangeRecorder.class),
                uplink,
                mock(MissionTranslationService.class));
        PatrolGeneratedTaskDownlinkDTO task = new PatrolGeneratedTaskDownlinkDTO();
        task.setExecutionRecordId(923020L);
        task.setProtocolVersion("robot-ws");
        task.setExecutionTimeEpochSeconds(1_700_000_000L);
        when(uplink.generatedTaskRecord("dev-1", 923020L)).thenReturn(CommonResult.success(task));
        when(registration.registrationOf("dev-1"))
                .thenReturn(new DeviceRegistrationLookup.Registration("robot-ws", ""));
        when(transport.isOnline(anyString())).thenReturn(true);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> service.send("dev-1", 500201, null, null, null, null, 923020L));

        assertTrue(error.getMessage().contains("模板编号"));
        verify(transport, never()).sendText(anyString(), anyString());
    }

    @Test
    void stop_typedTaskIdReplacesTheRecordId() throws Exception {
        DeviceRegistrationLookup registration = mock(DeviceRegistrationLookup.class);
        DeviceTransport transport = mock(DeviceTransport.class);
        InspectionDeviceUplinkApi uplink = mock(InspectionDeviceUplinkApi.class);
        ObjectMapper mapper = new ObjectMapper();
        ProtocolMonitorDownlinkService service = new ProtocolMonitorDownlinkService(
                registration,
                mock(ProtocolMonitorDownlinkCatalog.class),
                new EnvelopeJsonCodec(mapper),
                transport,
                mock(RunningTaskExchangeRecorder.class),
                uplink,
                mock(MissionTranslationService.class));
        PatrolGeneratedTaskDownlinkDTO task = new PatrolGeneratedTaskDownlinkDTO();
        task.setExecutionRecordId(923020L);
        task.setProtocolVersion("robot-ws");
        when(uplink.generatedTaskRecord("dev-1", 923020L)).thenReturn(CommonResult.success(task));
        when(registration.registrationOf("dev-1"))
                .thenReturn(new DeviceRegistrationLookup.Registration("robot-ws", ""));
        when(transport.isOnline(anyString())).thenReturn(true);
        when(transport.sendText(eq("dev-1"), anyString())).thenReturn(true);
        ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);

        service.send("dev-1", 500205, null, null, null, null, 923020L, null, "typed-task");

        verify(transport).sendText(eq("dev-1"), payload.capture());
        JsonNode root = mapper.readTree(payload.getValue());
        assertEquals(500205, root.get("opcode").asInt());
        assertEquals("typed-task", root.get("taskId").asText());
    }

    @Test
    void deletePackage_writesTemplateIdFromTheCommandPackage() throws Exception {
        DeviceRegistrationLookup registration = mock(DeviceRegistrationLookup.class);
        DeviceTransport transport = mock(DeviceTransport.class);
        InspectionDeviceUplinkApi uplink = mock(InspectionDeviceUplinkApi.class);
        ObjectMapper mapper = new ObjectMapper();
        ProtocolMonitorDownlinkService service = new ProtocolMonitorDownlinkService(
                registration,
                mock(ProtocolMonitorDownlinkCatalog.class),
                new EnvelopeJsonCodec(mapper),
                transport,
                mock(RunningTaskExchangeRecorder.class),
                uplink,
                mock(MissionTranslationService.class));
        PatrolGeneratedTaskDownlinkDTO task = new PatrolGeneratedTaskDownlinkDTO();
        task.setExecutionRecordId(923020L);
        task.setProtocolVersion("robot-ws");
        task.setCommandTemplateId("patrol-task-910101");
        when(uplink.generatedTaskRecord("dev-1", 923020L)).thenReturn(CommonResult.success(task));
        when(registration.registrationOf("dev-1"))
                .thenReturn(new DeviceRegistrationLookup.Registration("robot-ws", ""));
        when(transport.isOnline(anyString())).thenReturn(true);
        when(transport.sendText(eq("dev-1"), anyString())).thenReturn(true);
        ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);

        service.send("dev-1", 500302, null, null, null, null, 923020L);

        verify(transport).sendText(eq("dev-1"), payload.capture());
        JsonNode root = mapper.readTree(payload.getValue());
        assertEquals(500302, root.get("opcode").asInt());
        assertEquals("patrol-task-910101", root.get("templateId").asText());
    }

    @Test
    void offline_doesNotSendCommandPackage() {
        DeviceRegistrationLookup registration = mock(DeviceRegistrationLookup.class);
        DeviceTransport transport = mock(DeviceTransport.class);
        InspectionDeviceUplinkApi uplink = mock(InspectionDeviceUplinkApi.class);
        ProtocolMonitorDownlinkService service = new ProtocolMonitorDownlinkService(
                registration,
                mock(ProtocolMonitorDownlinkCatalog.class),
                mock(EnvelopeJsonCodec.class),
                transport,
                mock(RunningTaskExchangeRecorder.class),
                uplink,
                mock(MissionTranslationService.class));
        when(registration.registrationOf("dev-1"))
                .thenReturn(new DeviceRegistrationLookup.Registration("robot-ws", ""));
        when(transport.isOnline("dev-1")).thenReturn(false);

        ProtocolMonitorDownlinkResultDTO result = service.send("dev-1", 500104);

        assertFalse(result.written());
        assertEquals(ProtocolMonitorDownlinkService.OFFLINE_NOTE, result.note());
        verify(transport, never()).sendText(anyString(), anyString());
        verify(uplink, never()).generatedTask(any());
        verify(uplink, never()).generatedTaskRecord(anyString(), anyLong());
    }
}
