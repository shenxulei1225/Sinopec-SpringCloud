package cn.cheers.x.module.dynamicbusiness.service.execution;

import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
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
import static org.mockito.Mockito.mock;
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
    void start_writesTaskFacilityAsOwningStation() {
        ModelDO model = new ModelDO();
        model.setId(3L);
        when(modelMapper.selectByCode("exec_patrol_round")).thenReturn(model);
        when(entityService.create(any())).thenReturn(77L);
        TaskExecutionBootstrapRespVO bootstrap = new TaskExecutionBootstrapRespVO();
        bootstrap.setExecutionRecordId(77L);
        bootstrap.setStepIds(List.of(1L));
        when(bootstrapService.bootstrap(any())).thenReturn(bootstrap);

        TaskExecutionStartReqDTO req = new TaskExecutionStartReqDTO();
        req.setTaskDefinitionId(88L);
        req.setEntityTypeCode("task_record_patrol");
        req.setModelCode("exec_patrol_round");
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
    }
}
