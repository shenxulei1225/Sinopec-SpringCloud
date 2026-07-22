package cn.iocoder.yudao.module.emergency.service.event;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.iot.api.alert.IotAlertRecordApi;
import cn.cheers.x.iot.api.alert.dto.IotAlertRecordRespDTO;
import cn.iocoder.yudao.module.emergency.controller.admin.event.vo.EventFromAlertReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventMapper;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmergencyEventCreateFromAlertTest {

    @InjectMocks
    private EmergencyEventServiceImpl eventService;

    @Mock
    private EmergencyEventMapper eventMapper;

    @Mock
    private IotAlertRecordApi iotAlertRecordApi;

    @Test
    void createFromAlert_blankAlertId_fails() {
        EventFromAlertReqVO req = new EventFromAlertReqVO();
        req.setAlertId("  ");
        req.setDiscoveredAt(LocalDateTime.now());
        req.setDescription("leak");

        ServiceException ex = assertThrows(ServiceException.class, () -> eventService.createFromAlert(req));
        assertEquals(ErrorCodeConstants.EVENT_ALERT_ID_REQUIRED.getCode(), ex.getCode());
        verify(iotAlertRecordApi, never()).getAlertRecord(anyLong());
    }

    @Test
    void createFromAlert_nonNumericIotAlertId_fails() {
        EventFromAlertReqVO req = new EventFromAlertReqVO();
        req.setAlertId("SMOKE-ALERT-1");
        req.setDiscoveredAt(LocalDateTime.now());
        req.setDescription("leak");

        ServiceException ex = assertThrows(ServiceException.class, () -> eventService.createFromAlert(req));
        assertEquals(ErrorCodeConstants.EVENT_ALERT_NOT_EXISTS.getCode(), ex.getCode());
        verify(eventMapper, never()).selectBySourceAlert(anyString(), anyString());
    }

    @Test
    void createFromAlert_iotAlertMissing_fails() {
        EventFromAlertReqVO req = new EventFromAlertReqVO();
        req.setAlertId("9001");
        req.setDiscoveredAt(LocalDateTime.now());
        req.setDescription("leak");

        when(iotAlertRecordApi.getAlertRecord(9001L)).thenReturn(CommonResult.success(null));

        ServiceException ex = assertThrows(ServiceException.class, () -> eventService.createFromAlert(req));
        assertEquals(ErrorCodeConstants.EVENT_ALERT_NOT_EXISTS.getCode(), ex.getCode());
        verify(eventMapper, never()).selectBySourceAlert(anyString(), anyString());
    }

    @Test
    void createFromAlert_iotUnavailable_failsExplicitly() {
        EventFromAlertReqVO req = new EventFromAlertReqVO();
        req.setAlertId("9001");
        req.setDiscoveredAt(LocalDateTime.now());
        req.setDescription("leak");

        when(iotAlertRecordApi.getAlertRecord(9001L)).thenThrow(new RuntimeException("iot down"));

        ServiceException ex = assertThrows(ServiceException.class, () -> eventService.createFromAlert(req));
        assertEquals(ErrorCodeConstants.EVENT_ALERT_NOT_EXISTS.getCode(), ex.getCode());
        verify(eventMapper, never()).selectBySourceAlert(anyString(), anyString());
    }

    @Test
    void createFromAlert_alreadyConverted_fails() {
        EventFromAlertReqVO req = new EventFromAlertReqVO();
        req.setAlertId("9001");
        req.setDiscoveredAt(LocalDateTime.now());
        req.setDescription("leak");

        when(iotAlertRecordApi.getAlertRecord(9001L)).thenReturn(CommonResult.success(
                IotAlertRecordRespDTO.builder().id(9001L).build()));
        EmergencyEventDO existing = new EmergencyEventDO();
        existing.setId(1L);
        when(eventMapper.selectBySourceAlert(anyString(), anyString())).thenReturn(existing);

        ServiceException ex = assertThrows(ServiceException.class, () -> eventService.createFromAlert(req));
        assertEquals(ErrorCodeConstants.EVENT_ALERT_ALREADY_CONVERTED.getCode(), ex.getCode());
    }
}
