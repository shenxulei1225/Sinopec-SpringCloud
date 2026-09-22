package cn.cheers.x.module.platform.orchestration.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.orchestration.service.ScheduleOrchestrationService;
import jakarta.annotation.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class ScheduleRunApiImpl implements ScheduleRunApi {

    @Resource
    private ScheduleOrchestrationService scheduleOrchestrationService;

    @Override
    public CommonResult<ScheduleRunResponse> run(ScheduleRunRequest request) {
        return success(scheduleOrchestrationService.runSchedule(request, resolveFacilityId(request)));
    }

    /**
     * 巡检排期种子 payload 可带 facilityId；缺省则 persist 不带设施（兼容旧调用）。
     */
    private static Long resolveFacilityId(ScheduleRunRequest request) {
        if (request == null || CollectionUtils.isEmpty(request.getWorkItems())) {
            return null;
        }
        for (WorkItemDTO item : request.getWorkItems()) {
            if (item == null || item.getPayload() == null) {
                continue;
            }
            Object raw = item.getPayload().get("facilityId");
            if (raw instanceof Number number) {
                return number.longValue();
            }
            if (raw instanceof String text && !text.isBlank()) {
                try {
                    return Long.parseLong(text.trim());
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }
        return null;
    }
}
