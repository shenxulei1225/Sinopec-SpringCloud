package cn.cheers.x.alarm.api;

import cn.cheers.x.alarm.api.dto.AlarmTriggerReqDTO;
import cn.cheers.x.alarm.api.dto.AlarmTriggerRespDTO;
import cn.cheers.x.alarm.enums.ApiConstants;
import cn.cheers.x.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 跨模块新增一条告警。
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 新增告警")
public interface AlarmTriggerApi {

    String PREFIX = ApiConstants.PREFIX + "/trigger";

    @PostMapping(PREFIX)
    @Operation(summary = "新增一条告警")
    CommonResult<AlarmTriggerRespDTO> trigger(@Valid @RequestBody AlarmTriggerReqDTO req);
}
