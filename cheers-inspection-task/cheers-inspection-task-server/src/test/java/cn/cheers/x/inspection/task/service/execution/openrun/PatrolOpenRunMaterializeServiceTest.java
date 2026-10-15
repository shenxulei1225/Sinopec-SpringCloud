package cn.cheers.x.inspection.task.service.execution.openrun;

import cn.cheers.x.device.protocolgateway.api.dto.MissionDownlinkPieceDTO;
import cn.cheers.x.device.protocolgateway.api.dto.MissionTranslateCommitRespDTO;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.execution.steptree.PatrolTaskStepTreeReader;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepNode;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.inspection.task.service.task.PatrolTaskRecordIngestAllocator;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartBatchReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartBatchRespDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PatrolOpenRunMaterializeServiceTest {

    private PatrolTaskEntityStore store;
    private PatrolTaskStepTreeReader catalog;
    private TaskExecutionSessionApi sessionApi;
    private EntityRpcApi entityRpc;
    private PatrolMissionTranslateService translateService;
    private PatrolTaskRecordIngestAllocator ingestAllocator;
    private PatrolOpenRunMaterializeService service;

    @BeforeEach
    void setUp() {
        store = mock(PatrolTaskEntityStore.class);
        catalog = mock(PatrolTaskStepTreeReader.class);
        sessionApi = mock(TaskExecutionSessionApi.class);
        entityRpc = mock(EntityRpcApi.class);
        translateService = mock(PatrolMissionTranslateService.class);
        ingestAllocator = new PatrolTaskRecordIngestAllocator("rtsp://127.0.0.1:8554/ingest", "");
        when(translateService.commitForMaterialize(any())).thenReturn(sampleCommittedDownlink());
        when(translateService.commitForMaterialize(any(), any(), nullable(String.class)))
                .thenReturn(sampleCommittedDownlink());
        service = new PatrolOpenRunMaterializeService(
                store, catalog, sessionApi, entityRpc, new ObjectMapper(), translateService, ingestAllocator);
        when(catalog.loadPatrolTaskStepTree(1L)).thenReturn(List.of(
                new TaskStepNode(
                        "a1", 0, null, TaskStepNode.HANG_ACTION, "capture", 11L, "拍照", null,
                        "DEVICE", null)));
        when(catalog.resolveActionId("capture")).thenReturn(Optional.of(11L));
    }

    @Test
    void materialize_writesTaskFacilityOntoPendingRecord() {
        when(store.pendingExecutionRecordId(1L, "s1")).thenReturn(null);
        TaskExecutionStartBatchRespDTO started = new TaskExecutionStartBatchRespDTO();
        TaskExecutionStartBatchRespDTO.RecordRef ref = new TaskExecutionStartBatchRespDTO.RecordRef();
        ref.setPendingRef("s1");
        ref.setExecutionRecordId(77L);
        started.setRecords(List.of(ref));
        when(sessionApi.startBatch(any())).thenReturn(CommonResult.success(started));

        service.materializeAllSlots(sampleDraft(8L), List.of(slot("s1")));

        ArgumentCaptor<TaskExecutionStartBatchReqDTO> captor =
                ArgumentCaptor.forClass(TaskExecutionStartBatchReqDTO.class);
        verify(sessionApi).startBatch(captor.capture());
        assertEquals(8L, captor.getValue().getFacilityId());
        assertEquals(1, captor.getValue().getOccurrences().size());
        assertEquals("样例 2026-09-28 09:00", captor.getValue().getOccurrences().get(0).getName());
        assertEquals("2026-09-28T09:00:00+08:00", captor.getValue().getOccurrences().get(0).getPlannedStart());
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Long>> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(store).putPendingExecutionRecordIds(org.mockito.ArgumentMatchers.eq(1L), mapCaptor.capture());
        assertEquals(77L, mapCaptor.getValue().get("s1"));
    }

    @Test
    void materialize_worldClockBecomesBeijingOffset() {
        when(store.pendingExecutionRecordId(1L, "s1")).thenReturn(null);
        TaskExecutionStartBatchRespDTO started = new TaskExecutionStartBatchRespDTO();
        TaskExecutionStartBatchRespDTO.RecordRef ref = new TaskExecutionStartBatchRespDTO.RecordRef();
        ref.setPendingRef("s1");
        ref.setExecutionRecordId(78L);
        started.setRecords(List.of(ref));
        when(sessionApi.startBatch(any())).thenReturn(CommonResult.success(started));

        ScheduleSlotDTO world = slot("s1");
        world.setPlannedStart("2026-10-03T01:00:00Z");
        service.materializeAllSlots(sampleDraft(8L), List.of(world));

        ArgumentCaptor<TaskExecutionStartBatchReqDTO> captor =
                ArgumentCaptor.forClass(TaskExecutionStartBatchReqDTO.class);
        verify(sessionApi).startBatch(captor.capture());
        assertEquals("样例 2026-10-03 09:00", captor.getValue().getOccurrences().get(0).getName());
        assertEquals("2026-10-03T09:00:00+08:00", captor.getValue().getOccurrences().get(0).getPlannedStart());
    }

    @Test
    void materialize_updateDeletesPreviousPendingPlanAndKeepsStarted() {
        when(store.pendingExecutionRecordIds(1L)).thenReturn(Map.of("old-pending", 55L, "old-started", 56L));
        EntityRespDTO pending = new EntityRespDTO();
        pending.setCustomFields(Map.of("execution_status", "pending"));
        EntityRespDTO startedRecord = new EntityRespDTO();
        startedRecord.setCustomFields(Map.of("execution_status", "in_progress"));
        when(entityRpc.getEntity(eq(55L), any())).thenReturn(CommonResult.success(pending));
        when(entityRpc.getEntity(eq(56L), any())).thenReturn(CommonResult.success(startedRecord));
        when(sessionApi.deletePending(55L)).thenReturn(CommonResult.success(true));
        when(store.pendingExecutionRecordId(1L, "s1")).thenReturn(null);
        TaskExecutionStartBatchRespDTO started = new TaskExecutionStartBatchRespDTO();
        TaskExecutionStartBatchRespDTO.RecordRef ref = new TaskExecutionStartBatchRespDTO.RecordRef();
        ref.setPendingRef("s1");
        ref.setExecutionRecordId(79L);
        started.setRecords(List.of(ref));
        when(sessionApi.startBatch(any())).thenReturn(CommonResult.success(started));

        service.materializeAllSlots(sampleDraft(8L), List.of(slot("s1")));

        verify(sessionApi).deletePending(55L);
        verify(sessionApi, never()).deletePending(56L);
        verify(store).dropPendingExecutionSlots(eq(1L), eq(List.of("old-pending")));
        verify(sessionApi).startBatch(any());
        verify(store, never()).dropPendingExecutionSlots(eq(1L), eq(List.of("old-started")));
    }

    @Test
    void materialize_robotWritesTranslatedPackageOntoEachOccurrence() {
        when(store.pendingExecutionRecordId(1L, "s1")).thenReturn(null);
        when(store.pendingExecutionRecordId(1L, "s2")).thenReturn(null);
        when(sessionApi.startBatch(any())).thenAnswer(invocation -> {
            TaskExecutionStartBatchReqDTO req = invocation.getArgument(0);
            String pendingRef = req.getOccurrences().get(0).getPendingRef();
            TaskExecutionStartBatchRespDTO started = new TaskExecutionStartBatchRespDTO();
            TaskExecutionStartBatchRespDTO.RecordRef ref = new TaskExecutionStartBatchRespDTO.RecordRef();
            ref.setPendingRef(pendingRef);
            ref.setExecutionRecordId("s1".equals(pendingRef) ? 81L : 82L);
            started.setRecords(List.of(ref));
            return CommonResult.success(started);
        });

        when(translateService.commitForMaterialize(any(), any(), any(String.class))).thenAnswer(invocation -> {
            String media = invocation.getArgument(2);
            return new MissionTranslateCommitRespDTO(
                    List.of(new MissionDownlinkPieceDTO(
                            "package", 500104, "m1", "wire-" + media, null, null, null, "pkg-" + media)),
                    null,
                    null);
        });

        service.materializeAllSlots(sampleDraft(8L), List.of(slot("s1"), slot("s2")));

        ArgumentCaptor<String> storageKey = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> translatedUrl = ArgumentCaptor.forClass(String.class);
        verify(translateService, times(1)).commitForMaterialize(any(), storageKey.capture(), translatedUrl.capture());
        assertEquals("patrol-task-1", storageKey.getValue());
        assertEquals("rtsp://127.0.0.1:8554/ingest/patrol-1-s1", translatedUrl.getValue());
        ArgumentCaptor<TaskExecutionStartBatchReqDTO> captor =
                ArgumentCaptor.forClass(TaskExecutionStartBatchReqDTO.class);
        verify(sessionApi, times(2)).startBatch(captor.capture());
        assertEquals(2, captor.getAllValues().size());
        for (TaskExecutionStartBatchReqDTO req : captor.getAllValues()) {
            assertEquals(1, req.getOccurrences().size());
            String pendingRef = req.getOccurrences().get(0).getPendingRef();
            @SuppressWarnings("unchecked")
            Map<String, Object> snapshot = (Map<String, Object>) req.getStandardSnapshot();
            String ingestUrl = "rtsp://127.0.0.1:8554/ingest/patrol-1-" + pendingRef;
            assertTrue(snapshot.get("dispatchDownlinkPieces") instanceof List<?> pieces && pieces.size() == 1);
            @SuppressWarnings("unchecked")
            Map<String, Object> piece = (Map<String, Object>) ((List<?>) snapshot.get("dispatchDownlinkPieces")).get(0);
            assertEquals("pkg-" + ingestUrl, piece.get("packagesJson"));
            assertEquals(ingestUrl, snapshot.get("taskRecordIngestUrl"));
            assertFalse(snapshot.containsKey("waylineFileId"));
        }
    }

    @Test
    void materialize_djiWritesWaylineOntoEveryOccurrence() {
        when(translateService.commitForMaterialize(any(), eq("patrol-task-1"), nullable(String.class)))
                .thenReturn(sampleWaylineDownlink());
        when(store.pendingExecutionRecordId(1L, "s1")).thenReturn(null);
        when(store.pendingExecutionRecordId(1L, "s2")).thenReturn(null);
        TaskExecutionStartBatchRespDTO started = new TaskExecutionStartBatchRespDTO();
        TaskExecutionStartBatchRespDTO.RecordRef first = new TaskExecutionStartBatchRespDTO.RecordRef();
        first.setPendingRef("s1");
        first.setExecutionRecordId(91L);
        TaskExecutionStartBatchRespDTO.RecordRef second = new TaskExecutionStartBatchRespDTO.RecordRef();
        second.setPendingRef("s2");
        second.setExecutionRecordId(92L);
        started.setRecords(List.of(first, second));
        when(sessionApi.startBatch(any())).thenReturn(CommonResult.success(started));

        service.materializeAllSlots(sampleDraft(8L, "dji-dock-1.16.1", "UAV"), List.of(slot("s1"), slot("s2")));

        verify(translateService, times(1)).commitForMaterialize(any(), eq("patrol-task-1"), eq(null));
        verify(translateService, never()).commitForMaterialize(any());
        ArgumentCaptor<TaskExecutionStartBatchReqDTO> captor =
                ArgumentCaptor.forClass(TaskExecutionStartBatchReqDTO.class);
        verify(sessionApi).startBatch(captor.capture());
        assertEquals(2, captor.getValue().getOccurrences().size());
        @SuppressWarnings("unchecked")
        Map<String, Object> snapshot = (Map<String, Object>) captor.getValue().getStandardSnapshot();
        assertTrue(snapshot.get("dispatchDownlinkPieces") instanceof List<?> pieces && pieces.size() == 1);
        assertEquals("wayline-88", snapshot.get("waylineFileId"));
        assertEquals("fp-88", snapshot.get("waylineFingerprint"));
    }

    @Test
    void materialize_missingTaskFacility_stopsWithoutCreatingRecord() {
        assertThrows(RuntimeException.class,
                () -> service.materializeAllSlots(sampleDraft(null), List.of(slot("s1"))));
    }

    @Test
    void materialize_missingPlannedStart_stopsWithoutCreatingRecord() {
        ScheduleSlotDTO slot = new ScheduleSlotDTO();
        slot.setSlotId("s1");
        assertThrows(RuntimeException.class,
                () -> service.materializeAllSlots(sampleDraft(8L), List.of(slot)));
    }

    private static ScheduleSlotDTO slot(String id) {
        ScheduleSlotDTO slot = new ScheduleSlotDTO();
        slot.setSlotId(id);
        slot.setPlannedStart("2026-09-28T09:00:00+08:00");
        return slot;
    }

    private static MissionTranslateCommitRespDTO sampleCommittedDownlink() {
        return new MissionTranslateCommitRespDTO(
                List.of(new MissionDownlinkPieceDTO(
                        "direct", 1, "m1", "{}", null, null, null, null)),
                null,
                null);
    }

    private static MissionTranslateCommitRespDTO sampleWaylineDownlink() {
        return new MissionTranslateCommitRespDTO(
                List.of(new MissionDownlinkPieceDTO(
                        "direct", 1, "m1", "{}", null, null, null, null)),
                "wayline-88",
                "fp-88");
    }

    private static PatrolTaskDraft sampleDraft(Long facilityId) {
        return sampleDraft(facilityId, "robot-ws", "ROBOT");
    }

    private static PatrolTaskDraft sampleDraft(Long facilityId, String protocolCode, String means) {
        ExecutionDeviceBinding binding = new ExecutionDeviceBinding();
        binding.setEquipmentId(99L);
        binding.setProtocolCode(protocolCode);
        binding.setLogicalDeviceId("SN-001");
        InspectionContent content = new InspectionContent();
        InspectionContent.ObjectContent object = new InspectionContent.ObjectContent();
        object.setObjectId(200L);
        InspectionContent.ItemContent item = new InspectionContent.ItemContent();
        item.setItemId(7L);
        object.setItems(List.of(item));
        content.setCustomObjects(List.of(object));
        return new PatrolTaskDraft(
                1L, "样例", "巡检", facilityId, means, content, binding,
                null, null, null, "draft", 3, null, null, null, null, null, null, null, null, null, null);
    }
}
