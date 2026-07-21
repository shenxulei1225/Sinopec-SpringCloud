package cn.cheers.x.module.platform.runtime.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotStatusUpdateReqDTO;
import cn.cheers.x.module.platform.runtime.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 平台 L4 计划点回写")
public interface RuntimeSlotWriteApi {

    String PREFIX = ApiConstants.PREFIX + "/slots";

    @PostMapping(PREFIX + "/update-status")
    @Operation(summary = "回写计划点状态并追加过程时间线")
    CommonResult<Boolean> updateSlotStatus(@Valid @RequestBody RuntimeSlotStatusUpdateReqDTO request);
}
