package cn.cheers.x.inspection.task.service.execution.openrun;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.execution.steptree.PatrolTaskStepTreeReader;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepNode;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartRespDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PatrolOpenRunMaterializeServiceTest {

    private PatrolTaskEntityStore store;
    private PatrolTaskStepTreeReader catalog;
    private TaskExecutionSessionApi sessionApi;
    private PatrolOpenRunMaterializeService service;

    @BeforeEach
    void setUp() {
        store = mock(PatrolTaskEntityStore.class);
        catalog = mock(PatrolTaskStepTreeReader.class);
        sessionApi = mock(TaskExecutionSessionApi.class);
        service = new PatrolOpenRunMaterializeService(
                store, catalog, sessionApi, mock(EntityRpcApi.class), new ObjectMapper());
        when(catalog.loadPatrolTaskStepTree(1L)).thenReturn(List.of(
                new TaskStepNode("a1", 0, null, TaskStepNode.HANG_ACTION, "boot", 11L, "开机自检", null)));
        when(catalog.resolveActionId("boot")).thenReturn(Optional.of(11L));
    }

    @Test
    void materialize_writesTaskFacilityOntoPendingRecord() {
        when(store.pendingExecutionRecordId(1L, "s1")).thenReturn(null);
        TaskExecutionStartRespDTO started = new TaskExecutionStartRespDTO();
        started.setExecutionRecordId(77L);
        when(sessionApi.start(any())).thenReturn(CommonResult.success(started));

        service.materializeAllSlots(sampleDraft(8L), List.of(slot("s1")));

        ArgumentCaptor<TaskExecutionStartReqDTO> captor = ArgumentCaptor.forClass(TaskExecutionStartReqDTO.class);
        verify(sessionApi).start(captor.capture());
        assertEquals(8L, captor.getValue().getFacilityId());
    }

    @Test
    void materialize_missingTaskFacility_stopsWithoutCreatingRecord() {
        assertThrows(RuntimeException.class,
                () -> service.materializeAllSlots(sampleDraft(null), List.of(slot("s1"))));
    }

    private static ScheduleSlotDTO slot(String id) {
        ScheduleSlotDTO slot = new ScheduleSlotDTO();
        slot.setSlotId(id);
        return slot;
    }

    private static PatrolTaskDraft sampleDraft(Long facilityId) {
        ExecutionDeviceBinding binding = new ExecutionDeviceBinding();
        binding.setEquipmentId(99L);
        binding.setProtocolCode("robot-ws");
        binding.setLogicalDeviceId("SN-001");
        InspectionContent content = new InspectionContent();
        InspectionContent.ObjectContent object = new InspectionContent.ObjectContent();
        object.setObjectId(200L);
        InspectionContent.ItemContent item = new InspectionContent.ItemContent();
        item.setItemId(7L);
        object.setItems(List.of(item));
        content.setCustomObjects(List.of(object));
        return new PatrolTaskDraft(
                1L, "样例", "巡检", facilityId, "ROBOT", content, binding,
                null, null, null, "draft", 3, null, null, null, null, null, null, null, null);
    }
}
