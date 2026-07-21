package cn.cheers.x.module.platform.runtime.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.runtime.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.OffsetDateTime;
import java.util.List;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 平台 L4 计划点查询")
public interface RuntimeQueryApi {

    String PREFIX = ApiConstants.PREFIX + "/slots";

    @GetMapping(PREFIX)
    @Operation(summary = "按计划时间范围查询计划点（日历 / 已占用窗）")
    CommonResult<List<ScheduleSlotDTO>> listSlots(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @RequestParam(value = "resourceId", required = false) String resourceId,
            @RequestParam(value = "entityTypeCode", required = false) String entityTypeCode,
            @RequestParam(value = "siteId", required = false) Long siteId,
            @RequestParam(value = "slotStatuses", required = false) List<SlotStatus> slotStatuses);

    @GetMapping(PREFIX + "/jobs/{runtimeJobId}/slots")
    @Operation(summary = "按运行作业列出计划点")
    CommonResult<List<ScheduleSlotDTO>> listSlotsByJobId(@PathVariable("runtimeJobId") String runtimeJobId);
}
