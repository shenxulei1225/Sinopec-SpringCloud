package cn.cheers.x.alarm.api;

import cn.cheers.x.alarm.api.dto.AlarmNotifyReqDTO;
import cn.cheers.x.alarm.api.dto.AlarmNotifyRespDTO;
import cn.cheers.x.alarm.enums.ApiConstants;
import cn.cheers.x.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 跨模块按告警单给相关人员发通知。
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 告警通知")
public interface AlarmNotifyApi {

    String PREFIX = ApiConstants.PREFIX + "/notify";

    @PostMapping(PREFIX)
    @Operation(summary = "给相关人员发通知")
    CommonResult<AlarmNotifyRespDTO> notifyRecipients(@Valid @RequestBody AlarmNotifyReqDTO req);
}
