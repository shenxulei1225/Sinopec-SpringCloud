package cn.cheers.x.iot.api.alert;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.iot.api.alert.dto.IotAlertRecordRespDTO;
import cn.cheers.x.iot.dal.dataobject.alert.IotAlertRecordDO;
import cn.cheers.x.iot.enums.ErrorCodeConstants;
import cn.cheers.x.iot.service.alert.IotAlertRecordService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class IotAlertRecordApiImpl implements IotAlertRecordApi {

    @Resource
    private IotAlertRecordService alertRecordService;

    @Override
    public CommonResult<IotAlertRecordRespDTO> getAlertRecord(Long id) {
        IotAlertRecordDO row = alertRecordService.getAlertRecord(id);
        if (row == null) {
            throw exception(ErrorCodeConstants.ALERT_RECORD_NOT_EXISTS);
        }
        return success(IotAlertRecordRespDTO.builder()
                .id(row.getId())
                .configId(row.getConfigId())
                .configName(row.getConfigName())
                .configLevel(row.getConfigLevel())
                .deviceId(row.getDeviceId())
                .productId(row.getProductId())
                .processStatus(row.getProcessStatus())
                .build());
    }
}
