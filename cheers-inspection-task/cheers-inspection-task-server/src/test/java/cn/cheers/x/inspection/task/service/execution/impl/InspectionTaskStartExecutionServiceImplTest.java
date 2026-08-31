package cn.cheers.x.inspection.task.service.execution.impl;

import cn.cheers.x.device.protocolgateway.api.DeviceProtocolMissionApi;
import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.api.mission.MissionPointActionType;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.binding.ObjectStationBindingDO;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.route.InspectionRoutePlanDO;
import cn.cheers.x.inspection.inspection_content.dal.mysql.binding.ObjectStationBindingMapper;
import cn.cheers.x.inspection.inspection_content.dal.mysql.route.InspectionRoutePlanMapper;
import cn.cheers.x.inspection.task.dal.dataobject.task.InspectionTaskDO;
import cn.cheers.x.inspection.task.dal.mysql.task.InspectionTaskMapper;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.dto.topology.TopologyPointDTO;
import cn.cheers.x.module.platform.topology.api.PathNetworkApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InspectionTaskStartExecutionServiceImplTest {

    private InspectionTaskMapper taskMapper;
    private InspectionRoutePlanMapper routePlanMapper;
    private ObjectStationBindingMapper bindingMapper;
    private PathNetworkApi pathNetworkApi;
    private TaskExecutionSessionApi sessionApi;
    private DeviceProtocolMissionApi missionApi;
    private InspectionTaskStartExecutionServiceImpl service;

    @BeforeEach
    void setUp() {
        taskMapper = mock(InspectionTaskMapper.class);
        routePlanMapper = mock(InspectionRoutePlanMapper.class);
        bindingMapper = mock(ObjectStationBindingMapper.class);
        pathNetworkApi = mock(PathNetworkApi.class);
        sessionApi = mock(TaskExecutionSessionApi.class);
        missionApi = mock(DeviceProtocolMissionApi.class);
        service = new InspectionTaskStartExecutionServiceImpl(
                taskMapper, routePlanMapper, bindingMapper, pathNetworkApi,
                sessionApi, missionApi, new ObjectMapper());
    }

    @Test
    void missingBinding_throws() {
        InspectionTaskDO task = new InspectionTaskDO();
        task.setId(1L);
        task.setNetworkRef("net-a");
        when(taskMapper.selectById(1L)).thenReturn(task);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.startExecution(1L));
        assertTrue(ex.getMessage().contains("未绑定执行设备"));
    }

    @Test
    void happyPath_createsExecutionRecord_thenDispatchesWithRecordId() {
        InspectionTaskDO task = sampleTask();
        when(taskMapper.selectById(1L)).thenReturn(task);

        InspectionRoutePlanDO plan = new InspectionRoutePlanDO();
        plan.setId(10L);
        plan.setFacilityId(100L);
        plan.setStopIds("[\"s1\",\"s2\"]");
        when(routePlanMapper.selectById(10L)).thenReturn(plan);

        ObjectStationBindingDO stationBinding = new ObjectStationBindingDO();
        stationBinding.setObjectId(7L);
        stationBinding.setStationNodeId("s1");
        when(bindingMapper.selectByFacilityAndObjectIds(eq(100L), anyCollection()))
                .thenReturn(List.of(stationBinding));

        PathNodeDTO n1 = PathNodeDTO.builder()
                .nodeId("s1")
                .position(TopologyPointDTO.builder().x(117.0).y(39.1).build())
                .build();
        PathNodeDTO n2 = PathNodeDTO.builder()
                .nodeId("s2")
                .position(TopologyPointDTO.builder().x(117.1).y(39.2).build())
                .build();
        when(pathNetworkApi.getNetwork("net-a")).thenReturn(CommonResult.success(
                PathNetworkDTO.builder().networkRef("net-a").nodes(List.of(n1, n2)).build()));

        TaskExecutionStartRespDTO sessionResp = new TaskExecutionStartRespDTO();
        sessionResp.setExecutionRecordId(9001L);
        sessionResp.setStepIds(List.of(11L, 12L));
        when(sessionApi.start(any())).thenReturn(CommonResult.success(sessionResp));
        when(sessionApi.writeback(any())).thenReturn(CommonResult.success(true));
        when(missionApi.dispatchAndStartup(any())).thenReturn(CommonResult.success(
                new MissionStartRespDTO(true, true, true, true, null, "{}")));

        MissionStartRespDTO result = service.startExecution(1L);

        assertTrue(result.success());
        ArgumentCaptor<TaskExecutionStartReqDTO> sessionCaptor =
                ArgumentCaptor.forClass(TaskExecutionStartReqDTO.class);
        verify(sessionApi).start(sessionCaptor.capture());
        assertEquals(1L, sessionCaptor.getValue().getTaskDefinitionId());
        assertEquals(2, sessionCaptor.getValue().getSteps().size());

        ArgumentCaptor<DeviceMissionPlan> missionCaptor = ArgumentCaptor.forClass(DeviceMissionPlan.class);
        verify(missionApi).dispatchAndStartup(missionCaptor.capture());
        assertEquals("9001", missionCaptor.getValue().taskId());
        assertEquals("TASK-1", missionCaptor.getValue().templateId());
        assertEquals(MissionPointActionType.PHOTO, missionCaptor.getValue().waypoints().get(0).action());

        ArgumentCaptor<TaskExecutionWritebackReqDTO> writebackCaptor =
                ArgumentCaptor.forClass(TaskExecutionWritebackReqDTO.class);
        verify(sessionApi).writeback(writebackCaptor.capture());
        assertEquals(9001L, writebackCaptor.getValue().getExecutionRecordId());
        assertEquals("in_progress", writebackCaptor.getValue().getExecutionStatus());

        ArgumentCaptor<InspectionTaskDO> updateCaptor = ArgumentCaptor.forClass(InspectionTaskDO.class);
        verify(taskMapper).updateById(updateCaptor.capture());
        assertEquals("DISPATCHED", updateCaptor.getValue().getDeviceRunStatus());
    }

    private static InspectionTaskDO sampleTask() {
        InspectionTaskDO task = new InspectionTaskDO();
        task.setId(1L);
        task.setTaskCode("TASK-1");
        task.setTaskName("样例");
        task.setNetworkRef("net-a");
        task.setRoutePlanId(10L);
        task.setPlannedRoute("{\"stopIds\":[\"s1\",\"s2\"]}");
        ExecutionDeviceBinding binding = new ExecutionDeviceBinding();
        binding.setEquipmentId(99L);
        binding.setProtocolCode("zhiren-robot-ws");
        binding.setLogicalDeviceId("SN-001");
        task.setExecutionDeviceBinding(binding);
        InspectionContent content = new InspectionContent();
        InspectionContent.ObjectContent object = new InspectionContent.ObjectContent();
        object.setObjectId(7L);
        content.getCustomObjects().add(object);
        task.setInspectionContent(content);
        return task;
    }
}
