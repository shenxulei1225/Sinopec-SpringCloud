package cn.cheers.x.module.dynamicbusiness.service.execution;

import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartBatchReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeResolver;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.inspection.TaskExecutionBootstrapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskExecutionSessionServiceImplTest {

    private EntityService entityService;
    private ModelMapper modelMapper;
    private TaskExecutionBootstrapService bootstrapService;
    private EntityTypeScopeResolver entityTypeScopeResolver;
    private TaskExecutionSessionServiceImpl service;

    @BeforeEach
    void setUp() {
        entityService = mock(EntityService.class);
        modelMapper = mock(ModelMapper.class);
        bootstrapService = mock(TaskExecutionBootstrapService.class);
        entityTypeScopeResolver = mock(EntityTypeScopeResolver.class);
        when(entityTypeScopeResolver.resolveStorageEntityTypeCode("task_record_patrol"))
                .thenReturn("task_excution_record");
        service = new TaskExecutionSessionServiceImpl();
        ReflectionTestUtils.setField(service, "entityService", entityService);
        ReflectionTestUtils.setField(service, "modelMapper", modelMapper);
        ReflectionTestUtils.setField(service, "taskExecutionBootstrapService", bootstrapService);
        ReflectionTestUtils.setField(service, "entityTypeScopeResolver", entityTypeScopeResolver);
    }

    @Test
    void start_writesTaskFacilityAsOwningStation() {
        ModelDO model = new ModelDO();
        model.setId(3L);
        when(modelMapper.selectByCode("exec_patrol_record")).thenReturn(model);
        when(entityService.create(any())).thenReturn(77L);
        TaskExecutionBootstrapRespVO bootstrap = new TaskExecutionBootstrapRespVO();
        bootstrap.setExecutionRecordId(77L);
        bootstrap.setStepIds(List.of(1L));
        when(bootstrapService.bootstrap(any())).thenReturn(bootstrap);

        TaskExecutionStartReqDTO req = new TaskExecutionStartReqDTO();
        req.setTaskDefinitionId(88L);
        req.setEntityTypeCode("task_record_patrol");
        req.setModelCode("exec_patrol_record");
        req.setFacilityId(8L);
        req.setName("样例-待执行");
        req.setStandardSnapshot(Map.of("source", "test"));
        TaskExecutionStartReqDTO.StepDraft step = new TaskExecutionStartReqDTO.StepDraft();
        step.setName("任务准备");
        step.setStepCode("prepare");
        step.setStepOrder(0);
        req.setSteps(List.of(step));

        service.start(req);

        ArgumentCaptor<EntityCreateReqVO> captor = ArgumentCaptor.forClass(EntityCreateReqVO.class);
        verify(entityService).create(captor.capture());
        Object raw = captor.getValue().getBaseFields().get(FacilityOwningFieldCodes.FIELD_CODE);
        assertEquals(8L, FacilityOwningFieldCodes.extractId(raw));
        assertEquals("task_excution_record", captor.getValue().getBaseFields().get("entityTypeCode"));
        ArgumentCaptor<TaskExecutionBootstrapReqVO> bootstrapCaptor =
                ArgumentCaptor.forClass(TaskExecutionBootstrapReqVO.class);
        verify(bootstrapService).bootstrap(bootstrapCaptor.capture());
        assertEquals("task_excution_record", bootstrapCaptor.getValue().getEntityTypeCode());
    }

    @Test
    void startBatch_createsEachAccountThenWritesStepsOnce() {
        ModelDO model = new ModelDO();
        model.setId(3L);
        model.setCode("exec_patrol_record");
        when(modelMapper.selectByCode("exec_patrol_record")).thenReturn(model);
        when(modelMapper.selectById(3L)).thenReturn(model);
        when(bootstrapService.insertPendingRecords(
                any(), any(), any(), any(), any(), nullable(Object.class), any()))
                .thenReturn(List.of(77L, 78L));

        TaskExecutionStartBatchReqDTO req = new TaskExecutionStartBatchReqDTO();
        req.setTaskDefinitionId(88L);
        req.setEntityTypeCode("task_record_patrol");
        req.setModelCode("exec_patrol_record");
        req.setFacilityId(8L);
        req.setStandardSnapshot(Map.of("source", "test"));
        TaskExecutionStartReqDTO.StepDraft step = new TaskExecutionStartReqDTO.StepDraft();
        step.setName("任务准备");
        step.setStepCode("prepare");
        step.setStepOrder(0);
        req.setSteps(List.of(step));
        TaskExecutionStartBatchReqDTO.Occurrence first = new TaskExecutionStartBatchReqDTO.Occurrence();
        first.setPendingRef("s1");
        first.setName("早班 2026-09-28 09:00");
        first.setPlannedStart("2026-09-28T09:00:00+08:00");
        TaskExecutionStartBatchReqDTO.Occurrence second = new TaskExecutionStartBatchReqDTO.Occurrence();
        second.setPendingRef("s2");
        second.setName("晚班 2026-09-28 21:00");
        second.setPlannedStart("2026-09-28T21:00:00+08:00");
        req.setOccurrences(List.of(first, second));

        service.startBatch(req);

        verify(entityService, never()).create(any());
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TaskExecutionBootstrapService.PendingAccount>> accountsCaptor =
                ArgumentCaptor.forClass(List.class);
        verify(bootstrapService).insertPendingRecords(
                eq("task_excution_record"),
                eq(3L),
                eq(8L),
                eq(88L),
                any(),
                isNull(),
                accountsCaptor.capture());
        assertEquals("2026-09-28T09:00:00+08:00", accountsCaptor.getValue().get(0).plannedStart());
        assertEquals("2026-09-28T21:00:00+08:00", accountsCaptor.getValue().get(1).plannedStart());
        verify(bootstrapService).writeSameStepsOntoRecords(
                eq("task_excution_record"),
                eq("exec_patrol_record"),
                eq(8L),
                eq(List.of(77L, 78L)),
                any());
    }
}
