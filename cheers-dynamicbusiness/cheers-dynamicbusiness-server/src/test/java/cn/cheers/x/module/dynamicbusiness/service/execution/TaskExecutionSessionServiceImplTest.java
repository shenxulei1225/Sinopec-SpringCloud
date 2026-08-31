package cn.cheers.x.module.dynamicbusiness.service.execution;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.inspection.TaskExecutionBootstrapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskExecutionSessionServiceImplTest {

    private EntityService entityService;
    private ModelMapper modelMapper;
    private TaskExecutionBootstrapService bootstrapService;
    private TaskExecutionSessionServiceImpl service;

    @BeforeEach
    void setUp() {
        entityService = mock(EntityService.class);
        modelMapper = mock(ModelMapper.class);
        bootstrapService = mock(TaskExecutionBootstrapService.class);
        service = new TaskExecutionSessionServiceImpl();
        ReflectionTestUtils.setField(service, "entityService", entityService);
        ReflectionTestUtils.setField(service, "modelMapper", modelMapper);
        ReflectionTestUtils.setField(service, "taskExecutionBootstrapService", bootstrapService);
    }

    @Test
    void start_createsRecordThenBootstraps() {
        ModelDO model = new ModelDO();
        model.setId(55L);
        when(modelMapper.selectByCode("exec_patrol_round")).thenReturn(model);
        when(entityService.create(any(EntityCreateReqVO.class))).thenReturn(9001L);
        TaskExecutionBootstrapRespVO bootstrapResp = new TaskExecutionBootstrapRespVO();
        bootstrapResp.setExecutionRecordId(9001L);
        bootstrapResp.setStepIds(List.of(11L, 12L));
        when(bootstrapService.bootstrap(any())).thenReturn(bootstrapResp);

        TaskExecutionStartReqDTO req = sampleStartReq();
        TaskExecutionStartRespDTO resp = service.start(req);

        assertEquals(9001L, resp.getExecutionRecordId());
        assertEquals(List.of(11L, 12L), resp.getStepIds());

        ArgumentCaptor<EntityCreateReqVO> createCaptor = ArgumentCaptor.forClass(EntityCreateReqVO.class);
        verify(entityService).create(createCaptor.capture());
        assertEquals(42L, createCaptor.getValue().getCustomFields().get("task_id"));
        assertEquals("slot-1", createCaptor.getValue().getCustomFields().get("pending_execution_id"));
        assertEquals(TaskExecutionSessionServiceImpl.STATUS_PENDING,
                createCaptor.getValue().getCustomFields().get("execution_status"));

        ArgumentCaptor<TaskExecutionBootstrapReqVO> bootCaptor =
                ArgumentCaptor.forClass(TaskExecutionBootstrapReqVO.class);
        verify(bootstrapService).bootstrap(bootCaptor.capture());
        assertEquals(9001L, bootCaptor.getValue().getExecutionRecordId());
        assertEquals(1, bootCaptor.getValue().getSteps().size());
    }

    @Test
    void start_rejectsWhenGapCodesPresent() {
        TaskExecutionStartReqDTO req = sampleStartReq();
        req.setGapCodes(List.of("MISSING_BINDING"));
        assertThrows(ServiceException.class, () -> service.start(req));
        verify(entityService, never()).create(any());
        verify(bootstrapService, never()).bootstrap(any());
    }

    @Test
    void writeback_updatesRecordAndStepByCode() {
        EntityRespVO record = new EntityRespVO();
        record.setId(9001L);
        record.setBaseFields(new LinkedHashMap<>(Map.of("name", "执行-42", "modelId", 55L)));
        record.setCustomFields(new LinkedHashMap<>(Map.of("task_id", 42L, "execution_status", "pending")));
        when(entityService.get(9001L, "task_excution_record")).thenReturn(record);

        EntityRespVO step = new EntityRespVO();
        step.setId(11L);
        step.setBaseFields(new LinkedHashMap<>(Map.of(
                "name", "停靠 s1", "modelId", 66L, "step_code", "s1", "step_status", "pending")));
        EntityRespVO stepListItem = new EntityRespVO();
        stepListItem.setId(11L);
        when(entityService.pageSearchEntities(any(EntityPageReqVO.class)))
                .thenReturn(new PageResult<>(List.of(stepListItem), 1L));
        when(entityService.get(11L, "task_execution_step")).thenReturn(step);

        TaskExecutionWritebackReqDTO req = new TaskExecutionWritebackReqDTO();
        req.setExecutionRecordId(9001L);
        req.setExecutionStatus(TaskExecutionSessionServiceImpl.STATUS_IN_PROGRESS);
        TaskExecutionWritebackReqDTO.StepUpdate stepUpdate = new TaskExecutionWritebackReqDTO.StepUpdate();
        stepUpdate.setStepCode("s1");
        stepUpdate.setStatus(TaskExecutionSessionServiceImpl.STATUS_COMPLETED);
        req.setStepUpdates(List.of(stepUpdate));

        service.writeback(req);

        ArgumentCaptor<EntityUpdateReqVO> updateCaptor = ArgumentCaptor.forClass(EntityUpdateReqVO.class);
        verify(entityService, org.mockito.Mockito.atLeast(2)).update(updateCaptor.capture());
        boolean statusUpdated = updateCaptor.getAllValues().stream().anyMatch(u ->
                u.getId().equals(9001L)
                        && TaskExecutionSessionServiceImpl.STATUS_IN_PROGRESS.equals(
                        u.getCustomFields().get("execution_status")));
        boolean stepUpdated = updateCaptor.getAllValues().stream().anyMatch(u ->
                u.getId().equals(11L)
                        && TaskExecutionSessionServiceImpl.STATUS_COMPLETED.equals(
                        u.getBaseFields().get("step_status")));
        assertTrue(statusUpdated);
        assertTrue(stepUpdated);
    }

    @Test
    void writeback_missingRecord_fails() {
        when(entityService.get(eq(1L), any())).thenReturn(null);
        TaskExecutionWritebackReqDTO req = new TaskExecutionWritebackReqDTO();
        req.setExecutionRecordId(1L);
        req.setExecutionStatus("completed");
        assertThrows(ServiceException.class, () -> service.writeback(req));
    }

    private static TaskExecutionStartReqDTO sampleStartReq() {
        TaskExecutionStartReqDTO req = new TaskExecutionStartReqDTO();
        req.setTaskDefinitionId(42L);
        req.setModelCode("exec_patrol_round");
        req.setPendingRef("slot-1");
        req.setStandardSnapshot(Map.of("version", 1));
        TaskExecutionStartReqDTO.StepDraft step = new TaskExecutionStartReqDTO.StepDraft();
        step.setName("停靠 s1");
        step.setStepCode("s1");
        step.setStepOrder(0);
        req.setSteps(List.of(step));
        return req;
    }
}
