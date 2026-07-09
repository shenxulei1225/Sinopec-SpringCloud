package cn.cheers.x.module.platform.runtime.controller.admin;

import cn.cheers.x.module.platform.contract.dto.runtime.RuntimeJobDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.runtime.service.RuntimeQueryService;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 平台 L4 运行时")
@RestController
@RequestMapping("/platform/runtime")
public class RuntimeJobController {

    @Resource
    private RuntimeQueryService runtimeQueryService;

    @GetMapping("/jobs/{runtimeJobId}")
    @Operation(summary = "按 runtimeJobId 获取运行作业")
    public CommonResult<RuntimeJobDTO> getJob(@PathVariable("runtimeJobId") String runtimeJobId) {
        return success(runtimeQueryService.getJob(runtimeJobId));
    }

    @GetMapping("/jobs/{runtimeJobId}/slots")
    @Operation(summary = "按运行作业列出计划点")
    public CommonResult<List<ScheduleSlotDTO>> listSlotsByJob(@PathVariable("runtimeJobId") String runtimeJobId) {
        return success(runtimeQueryService.listSlotsByJobId(runtimeJobId));
    }

    @GetMapping("/slots")
    @Operation(summary = "按计划时间范围查询计划点")
    public CommonResult<List<ScheduleSlotDTO>> listSlots(
            @RequestParam(value = "entityTypeCode", required = false) String entityTypeCode,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to) {
        return success(runtimeQueryService.listSlots(entityTypeCode, from, to));
    }
}
