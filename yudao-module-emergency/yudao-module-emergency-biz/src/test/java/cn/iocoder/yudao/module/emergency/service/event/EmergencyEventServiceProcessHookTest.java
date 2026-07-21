package cn.iocoder.yudao.module.emergency.service.event;

import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.module.platform.runtime.api.ProcessTimelineApi;
import cn.cheers.x.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.emergency.controller.admin.event.vo.EventAssessReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.event.vo.EventConfirmReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventAssessDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventAssessMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventReportExternalMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventReportInternalMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventStatusHistoryMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanStepMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.task.EmergencyTaskHistoryMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.task.EmergencyTaskMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.timeline.EmergencyTimelineMapper;
import cn.iocoder.yudao.module.emergency.enums.EventStatus;
import cn.iocoder.yudao.module.emergency.service.audit.EmergencyAuditLogService;
import cn.iocoder.yudao.module.emergency.service.process.EmergencyProcessKeys;
import cn.iocoder.yudao.module.emergency.service.process.EmergencyProcessRuntimeService;
import cn.iocoder.yudao.module.emergency.service.report.InformationReportFlowService;
import cn.iocoder.yudao.module.emergency.service.timeline.EmergencyProcessTimelineWriter;
import cn.iocoder.yudao.module.emergency.service.timeline.TimelineCacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmergencyEventServiceProcessHookTest {

    @InjectMocks
    private EmergencyEventServiceImpl eventService;

    @Mock private EmergencyEventMapper eventMapper;
    @Mock private EmergencyEventReportInternalMapper reportInternalMapper;
    @Mock private EmergencyEventReportExternalMapper reportExternalMapper;
    @Mock private EmergencyEventAssessMapper assessMapper;
    @Mock private EmergencyEventStatusHistoryMapper statusHistoryMapper;
    @Mock private EmergencyTimelineMapper timelineMapper;
    @Mock private AdminUserApi adminUserApi;
    @Mock private EventStateMachine stateMachine;
    @Mock private InformationReportFlowService reportFlowService;
    @Mock private EmergencyAuditLogService auditLogService;
    @Mock private EventCategoryValidationService categoryValidationService;
    @Mock private EventDeduplicationService deduplicationService;
    @Mock private EventNotificationService notificationService;
    @Mock private EmergencyPlanStepMapper planStepMapper;
    @Mock private EmergencyTaskMapper taskMapper;
    @Mock private EmergencyTaskHistoryMapper taskHistoryMapper;
    @Mock private TimelineCacheService timelineCacheService;
    @Mock private EmergencyProcessTimelineWriter processTimelineWriter;
    @Mock private ProcessTimelineApi processTimelineApi;
    @Mock private EmergencyProcessRuntimeService processRuntimeService;

    @Test
    void confirm_thinAdapter_completesConfirmUserTask_withoutStateMachine() {
        EmergencyEventDO event = new EmergencyEventDO();
        event.setId(11L);
        event.setStatus(EventStatus.PENDING.getCode());
        event.setProcessInstanceId("pi-11");
        when(eventMapper.selectById(11L)).thenReturn(event);

        EventConfirmReqVO reqVO = new EventConfirmReqVO();
        reqVO.setResult("real");

        try (MockedStatic<SecurityFrameworkUtils> security = mockStatic(SecurityFrameworkUtils.class)) {
            security.when(SecurityFrameworkUtils::getLoginUserId).thenReturn(100L);
            eventService.confirm(11L, reqVO);
        }

        verify(processRuntimeService).completeUserTask(
                eq(11L), eq(100L), eq(EmergencyProcessKeys.TASK_CONFIRM), any(Map.class));
        verify(processTimelineWriter).appendEventAction(eq(11L), eq("event.confirm"), anyString());
        verifyNoInteractions(stateMachine);
    }

    @Test
    void confirm_falseAlarm_projectsCancelled_thenCompletesTask() {
        EmergencyEventDO event = new EmergencyEventDO();
        event.setId(13L);
        event.setStatus(EventStatus.PENDING.getCode());
        event.setProcessInstanceId("pi-13");
        when(eventMapper.selectById(13L)).thenReturn(event);

        EventConfirmReqVO reqVO = new EventConfirmReqVO();
        reqVO.setResult("false_alarm");

        try (MockedStatic<SecurityFrameworkUtils> security = mockStatic(SecurityFrameworkUtils.class)) {
            security.when(SecurityFrameworkUtils::getLoginUserId).thenReturn(100L);
            eventService.confirm(13L, reqVO);
        }

        ArgumentCaptor<EmergencyEventDO> updateCaptor = ArgumentCaptor.forClass(EmergencyEventDO.class);
        verify(eventMapper).updateById(updateCaptor.capture());
        assertEquals(EventStatus.CANCELLED.getCode(), updateCaptor.getValue().getStatus());
        verify(processRuntimeService).completeUserTask(
                eq(13L), eq(100L), eq(EmergencyProcessKeys.TASK_CONFIRM), any(Map.class));
        verifyNoInteractions(stateMachine);
    }

    @Test
    void assess_storesNewLevel_andCompletesAssessUserTask() {
        EmergencyEventDO event = new EmergencyEventDO();
        event.setId(12L);
        event.setStatus(EventStatus.WARNING.getCode());
        event.setProcessInstanceId("pi-12");
        when(eventMapper.selectById(12L)).thenReturn(event);

        EventAssessReqVO reqVO = new EventAssessReqVO();
        reqVO.setResponseLevel("III");

        try (MockedStatic<SecurityFrameworkUtils> security = mockStatic(SecurityFrameworkUtils.class)) {
            security.when(SecurityFrameworkUtils::getLoginUserId).thenReturn(200L);
            eventService.assess(12L, reqVO);
        }

        ArgumentCaptor<EmergencyEventAssessDO> assessCaptor =
                ArgumentCaptor.forClass(EmergencyEventAssessDO.class);
        verify(assessMapper).insert(assessCaptor.capture());
        assertEquals("III", assessCaptor.getValue().getNewLevel());

        verify(processRuntimeService).completeUserTask(
                eq(12L), eq(200L), eq(EmergencyProcessKeys.TASK_ASSESS), any(Map.class));
        verify(processTimelineWriter).appendEventAction(eq(12L), eq("event.assess"), anyString());
        verifyNoInteractions(stateMachine);
    }

}
