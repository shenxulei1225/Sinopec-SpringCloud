package cn.cheers.x.alarm.api;

import cn.cheers.x.alarm.api.dto.AlarmTriggerReqDTO;
import cn.cheers.x.alarm.api.dto.AlarmTriggerRespDTO;
import cn.cheers.x.alarm.controller.admin.vo.alarm.AlarmRespVO;
import cn.cheers.x.alarm.controller.admin.vo.alarm.AlarmTriggerReqVO;
import cn.cheers.x.alarm.service.alarm.AlarmService;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 跨模块新增一条告警。只转已点名的类型/级别/设备/位置，不补默认身份。
 */
@RestController
@Validated
public class AlarmTriggerApiImpl implements AlarmTriggerApi {

    @Resource
    private AlarmService alarmService;

    @Override
    public CommonResult<AlarmTriggerRespDTO> trigger(AlarmTriggerReqDTO req) {
        AlarmTriggerReqVO vo = new AlarmTriggerReqVO();
        vo.setAlarmTypeId(req.getAlarmTypeId());
        vo.setAlarmLevel(req.getAlarmLevel());
        vo.setAlarmContent(req.getAlarmContent());
        vo.setDeviceId(req.getDeviceId());
        vo.setLocationId(req.getLocationId());
        vo.setTriggerValue(req.getTriggerValue());
        vo.setThresholdValue(req.getThresholdValue());
        vo.setRuleId(req.getRuleId());
        AlarmRespVO created = alarmService.triggerAlarm(vo);
        if (created == null || created.getId() == null) {
            throw new ServiceException(500, "新增一条告警没有返回账本编号");
        }
        AlarmTriggerRespDTO resp = new AlarmTriggerRespDTO();
        resp.setAlarmId(created.getId());
        resp.setAlarmCode(created.getAlarmCode());
        return success(resp);
    }
}
