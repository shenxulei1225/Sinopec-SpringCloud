package cn.cheers.x.module.platform.runtime.controller.admin;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionAppendReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionRespDTO;
import cn.cheers.x.module.platform.runtime.service.ProcessTimelineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 过程时间线")
@RestController
@RequestMapping("/platform/runtime/process-timeline")
@Validated
public class ProcessTimelineController {

    @Resource
    private ProcessTimelineService processTimelineService;

    @PostMapping("/append")
    @Operation(summary = "追加过程时间线动作")
    public CommonResult<Long> append(@Valid @RequestBody ProcessTimelineActionAppendReqDTO request) {
        return success(processTimelineService.append(request));
    }

    @GetMapping("/page")
    @Operation(summary = "按目标分页查询过程时间线")
    public CommonResult<PageResult<ProcessTimelineActionRespDTO>> page(
            @RequestParam("targetType") String targetType,
            @RequestParam("targetId") String targetId,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        return success(processTimelineService.pageByTarget(targetType, targetId, pageNo, pageSize));
    }
}
