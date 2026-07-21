package cn.cheers.x.module.platform.runtime.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.runtime.service.RuntimeQueryService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class RuntimeQueryApiImpl implements RuntimeQueryApi {

    @Resource
    private RuntimeQueryService runtimeQueryService;

    @Override
    public CommonResult<List<ScheduleSlotDTO>> listSlots(
            OffsetDateTime from,
            OffsetDateTime to,
            String resourceId,
            String entityTypeCode,
            Long siteId,
            List<SlotStatus> slotStatuses) {
        return success(runtimeQueryService.listSlots(from, to, resourceId, entityTypeCode, siteId, slotStatuses));
    }

    @Override
    public CommonResult<List<ScheduleSlotDTO>> listSlotsByJobId(String runtimeJobId) {
        return success(runtimeQueryService.listSlotsByJobId(runtimeJobId));
    }
}
