package cn.cheers.x.module.dynamicbusiness.service.linkage;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.service.action.execution.ActionExecutionReceipt;
import cn.cheers.x.module.dynamicbusiness.service.action.execution.ActionExecutionRequest;
import cn.cheers.x.module.dynamicbusiness.service.action.execution.ActionExecutionService;
import cn.cheers.x.module.dynamicbusiness.service.action.execution.ActionExecutionStatus;
import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventKinds;
import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinkageEventHandlerTest {

    @Mock
    private LinkageRuleQueryService ruleQueryService;
    @Mock
    private LinkageModeQueryService modeQueryService;
    @Mock
    private LinkageExecutionService linkageExecutionService;
    @Mock
    private ActionExecutionService actionExecutionService;
    @InjectMocks
    private LinkageEventHandler handler;

    @Test
    void eventReferenceFindsPairAndSubmitsStableActionCall() {
        PlatformEventMessage event = new PlatformEventMessage(
                "evt-1", 41L, "alarm.water.high", PlatformEventKinds.ALARM_LIFECYCLE,
                123L, 9L, "equipment", 77L, "RAISED",
                Instant.parse("2026-11-02T01:00:00Z"), Map.of("reading", 12.3));
        EntityDO rule = EntityDO.builder().id(101L).status(1).domain("alarm").build();
        when(ruleQueryService.findEnabledAlarmRules(41L, 9L)).thenReturn(List.of(rule));
        when(modeQueryService.findMatchingPairs(rule, "equipment", 77L))
                .thenReturn(List.of(new LinkagePair(1L, 77L, 88L, 31L)));
        when(linkageExecutionService.begin(event, 101L)).thenReturn(new LinkageExecutionStart(501L, true));
        when(actionExecutionService.submit(org.mockito.ArgumentMatchers.any()))
                .thenReturn(new ActionExecutionReceipt(601L, ActionExecutionStatus.PENDING));

        handler.handle(event);

        ArgumentCaptor<ActionExecutionRequest> request = ArgumentCaptor.forClass(ActionExecutionRequest.class);
        verify(actionExecutionService).submit(request.capture());
        assertEquals("evt-1:101:1", request.getValue().idempotencyKey());
        assertEquals(88L, request.getValue().targetEntityId());
        assertEquals(31L, request.getValue().actionId());
        verify(linkageExecutionService).recordAcceptedItem(501L, 1L, 601L);
    }

    @Test
    void confirmModeDoesNotSubmitAction() {
        PlatformEventMessage event = new PlatformEventMessage(
                "evt-2", 41L, "alarm.water.high", PlatformEventKinds.ALARM_LIFECYCLE,
                123L, 9L, "equipment", 77L, "RAISED",
                Instant.parse("2026-11-02T01:00:00Z"), Map.of());
        EntityDO rule = EntityDO.builder()
                .id(101L)
                .status(1)
                .domain("alarm")
                .dedicatedBaseFieldValues(Map.of(LinkageFieldCodes.EXECUTION, LinkageFieldCodes.EXECUTION_CONFIRM))
                .build();
        when(ruleQueryService.findEnabledAlarmRules(41L, 9L)).thenReturn(List.of(rule));
        when(linkageExecutionService.begin(event, 101L)).thenReturn(new LinkageExecutionStart(501L, true));

        handler.handle(event);

        verify(linkageExecutionService).recordWaitingConfirm(501L);
        verify(actionExecutionService, never()).submit(org.mockito.ArgumentMatchers.any());
    }
}
