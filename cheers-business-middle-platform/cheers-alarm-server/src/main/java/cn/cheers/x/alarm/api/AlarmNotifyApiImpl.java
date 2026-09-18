package cn.cheers.x.alarm.api;

import cn.cheers.x.alarm.api.dto.AlarmNotifyReqDTO;
import cn.cheers.x.alarm.api.dto.AlarmNotifyRespDTO;
import cn.cheers.x.alarm.dal.dataobject.AlarmDO;
import cn.cheers.x.alarm.service.alarm.AlarmService;
import cn.cheers.x.alarm.service.notify.NotificationService;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 按告警单发通知。没有告警编号不猜；策略没勾人时用该级别已配好的默认名单。
 */
@RestController
@Validated
public class AlarmNotifyApiImpl implements AlarmNotifyApi {

    @Resource
    private AlarmService alarmService;

    @Resource
    private NotificationService notificationService;

    @Override
    public CommonResult<AlarmNotifyRespDTO> notifyRecipients(AlarmNotifyReqDTO req) {
        if (req == null || req.getAlarmId() == null) {
            throw new ServiceException(400, "给相关人员发通知必须带告警编号");
        }
        AlarmDO alarm = alarmService.getAlarm(req.getAlarmId());
        if (alarm == null || alarm.getId() == null) {
            throw new ServiceException(404, "没有这本告警账，不能发通知");
        }
        if (CollectionUtils.isEmpty(req.getRecipientUserIds())) {
            notificationService.sendAlarmNotification(alarm);
        } else {
            notificationService.sendAlarmNotification(alarm, req.getRecipientUserIds());
        }
        AlarmNotifyRespDTO resp = new AlarmNotifyRespDTO();
        resp.setAlarmId(alarm.getId());
        return success(resp);
    }
}
