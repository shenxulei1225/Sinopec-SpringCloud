package cn.cheers.x.module.platform.orchestration.service;

import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;

/**
 * 排程编排服务。
 */
public interface ScheduleOrchestrationService {

    ScheduleRunResponse runSchedule(ScheduleRunRequest request, Long facilityId);
}
