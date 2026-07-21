package cn.iocoder.yudao.module.emergency.service.alert;

import cn.cheers.x.system.api.notify.NotifyMessageSendApi;
import cn.cheers.x.system.api.notify.dto.NotifySendSingleToUserReqDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmergencyAlertChannelSenderTest {

    @InjectMocks
    private EmergencyAlertChannelSender sender;

    @Mock
    private NotifyMessageSendApi notifyMessageSendApi;

    @BeforeEach
    void setTemplate() {
        ReflectionTestUtils.setField(sender, "notifyTemplateCode", "EMERGENCY_ALERT");
        ReflectionTestUtils.setField(sender, "smsTemplateCode", "EMERGENCY_ALERT");
        ReflectionTestUtils.setField(sender, "mailTemplateCode", "EMERGENCY_ALERT");
    }

    @Test
    void sendSystem_callsNotifyApi() {
        sender.sendByChannel("system", List.of(Map.of("userId", 9L)), "标题", "内容");

        ArgumentCaptor<NotifySendSingleToUserReqDTO> captor =
                ArgumentCaptor.forClass(NotifySendSingleToUserReqDTO.class);
        verify(notifyMessageSendApi).sendSingleMessageToAdmin(captor.capture());
        assertEquals(9L, captor.getValue().getUserId());
        assertEquals("EMERGENCY_ALERT", captor.getValue().getTemplateCode());
    }

    @Test
    void sendSystem_withoutTemplate_skipsApi() {
        ReflectionTestUtils.setField(sender, "notifyTemplateCode", "");
        sender.sendByChannel("system", List.of(Map.of("userId", 9L)), "标题", "内容");
        verifyNoInteractions(notifyMessageSendApi);
    }
}
