package cn.cheers.x.module.platform.runtime.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionAppendReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionRespDTO;
import cn.cheers.x.module.platform.runtime.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 过程时间线")
public interface ProcessTimelineApi {

    String PREFIX = ApiConstants.PREFIX + "/process-timeline";

    @PostMapping(PREFIX + "/append")
    @Operation(summary = "追加过程时间线动作")
    CommonResult<Long> append(@Valid @RequestBody ProcessTimelineActionAppendReqDTO request);

    @GetMapping(PREFIX + "/page")
    @Operation(summary = "按目标分页查询过程时间线")
    CommonResult<PageResult<ProcessTimelineActionRespDTO>> pageByTarget(
            @RequestParam("targetType") String targetType,
            @RequestParam("targetId") String targetId,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize);
}
