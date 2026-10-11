package cn.cheers.x.inspection.task.service.execution.impl;

import cn.cheers.x.device.protocolgateway.api.channel.AccessChannelCodes;
import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.api.datacollection.ProtocolQualifyStatus;
import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityWriteReqDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeviceCollectionAnomalyRecorderTest {

    private EntityRpcApi entityRpcApi;
    private DeviceCollectionAnomalyRecorder recorder;

    @BeforeEach
    void setUp() {
        entityRpcApi = mock(EntityRpcApi.class);
        recorder = new DeviceCollectionAnomalyRecorder(entityRpcApi);
        when(entityRpcApi.getEntityByCode(any(), eq(DeviceCollectionAnomalyRecorder.ANOMALY_TYPE)))
                .thenReturn(CommonResult.success(null));
        when(entityRpcApi.listEntities(DeviceCollectionAnomalyRecorder.EQUIPMENT_TYPE, null))
                .thenReturn(CommonResult.success(List.of(equipment(8L, "SN-1"))));
        when(entityRpcApi.getEntity(8L, DeviceCollectionAnomalyRecorder.EQUIPMENT_TYPE))
                .thenReturn(CommonResult.success(equipment(8L, "SN-1")));
        when(entityRpcApi.createEntity(any())).thenReturn(CommonResult.success(100L));
        when(entityRpcApi.updateEntityFields(any())).thenReturn(CommonResult.success(true));
    }

    @Test
    void qualifiedFault_writesAnomalyOnTheMatchedDevice() {
        recorder.record(fault(ProtocolQualifyStatus.QUALIFIED, "m1"));

        ArgumentCaptor<EntityWriteReqDTO> captor = ArgumentCaptor.forClass(EntityWriteReqDTO.class);
        verify(entityRpcApi).createEntity(captor.capture());
        EntityWriteReqDTO write = captor.getValue();
        assertEquals(DeviceCollectionAnomalyRecorder.ANOMALY_TYPE, write.getEntityTypeCode());
        assertEquals(DeviceCollectionAnomalyRecorder.MODEL_CODE, write.getModelCode());
        assertEquals("device-fault-m1", write.getFields().get("code"));
        assertNull(write.getFields().get("equipment_id"));

        ArgumentCaptor<EntityWriteReqDTO> updateCaptor = ArgumentCaptor.forClass(EntityWriteReqDTO.class);
        verify(entityRpcApi).updateEntityFields(updateCaptor.capture());
        EntityWriteReqDTO update = updateCaptor.getValue();
        assertEquals(8L, update.getId());
        assertEquals(DeviceCollectionAnomalyRecorder.EQUIPMENT_TYPE, update.getEntityTypeCode());
        assertEquals(
                List.of(Map.of("entityTypeCode", "anomaly", "id", 100L)),
                update.getFields().get(DeviceCollectionAnomalyRecorder.ANOMALY_REF_FIELD));
    }

    @Test
    void sameMessage_doesNotWriteAgain() {
        EntityRespDTO existing = new EntityRespDTO();
        existing.setId(100L);
        when(entityRpcApi.getEntityByCode("device-fault-m1", DeviceCollectionAnomalyRecorder.ANOMALY_TYPE))
                .thenReturn(CommonResult.success(existing));

        recorder.record(fault(ProtocolQualifyStatus.QUALIFIED, "m1"));

        verify(entityRpcApi, never()).createEntity(any());
    }

    @Test
    void unqualifiedFault_doesNotWrite() {
        recorder.record(fault(ProtocolQualifyStatus.UNQUALIFIED, "m1"));
        verify(entityRpcApi, never()).createEntity(any());
    }

    @Test
    void twoDevicesWithSameCode_refusesToGuess() {
        when(entityRpcApi.listEntities(DeviceCollectionAnomalyRecorder.EQUIPMENT_TYPE, null))
                .thenReturn(CommonResult.success(List.of(equipment(8L, "SN-1"), equipment(9L, "SN-1"))));

        ServiceException ex = assertThrows(
                ServiceException.class,
                () -> recorder.record(fault(ProtocolQualifyStatus.QUALIFIED, "m2")));
        assertEquals(400, ex.getCode());
        verify(entityRpcApi, never()).createEntity(any());
    }

    private static EntityRespDTO equipment(long id, String deviceCode) {
        EntityRespDTO row = new EntityRespDTO();
        row.setId(id);
        row.setCustomFields(Map.of("device_code", deviceCode));
        return row;
    }

    private static CollectionSample fault(ProtocolQualifyStatus qualify, String msgId) {
        return new CollectionSample(
                AccessChannelCodes.INSPECTION,
                "SN-1",
                String.valueOf(TransportOpcode.FAULT_REPORT.code()),
                qualify,
                qualify == ProtocolQualifyStatus.UNQUALIFIED ? List.of("缺必填") : List.of(),
                Map.of(),
                null,
                msgId,
                1L);
    }
}
