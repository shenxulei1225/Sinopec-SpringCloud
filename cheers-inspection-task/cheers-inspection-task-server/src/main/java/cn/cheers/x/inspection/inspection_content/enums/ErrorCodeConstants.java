package cn.cheers.x.inspection.inspection_content.enums;

import cn.cheers.x.framework.common.exception.ErrorCode;

/**
 * 巡检内容域错误码。
 */
public interface ErrorCodeConstants {

    ErrorCode PATROL_CONFIRM_WORK_ITEMS_EMPTY = new ErrorCode(1_009_001_001, "确认路线缺少工作项");
    ErrorCode PATROL_CONFIRM_DURATION_REQUIRED = new ErrorCode(1_009_001_002, "确认路线缺少预估时长 durationEstimateMinutes");
    ErrorCode PATROL_CONFIRM_PLANNED_ROUTE_REQUIRED = new ErrorCode(1_009_001_003, "确认路线缺少规划结果 plannedRoute");
    ErrorCode PATROL_CONFIRM_NETWORK_REF_REQUIRED = new ErrorCode(1_009_001_004, "确认路线缺少路网引用 networkRef");
    ErrorCode PATROL_CONFIRM_INSPECTION_TYPE_REQUIRED = new ErrorCode(1_009_001_005, "确认路线缺少巡检类型 inspectionType");
    ErrorCode PATROL_CONFIRM_TASK_ID_REQUIRED = new ErrorCode(1_009_001_006, "确认路线写入缺少 taskId");
    ErrorCode PATROL_CONFIRM_FACILITY_ID_REQUIRED = new ErrorCode(1_009_001_007, "确认路线写入缺少 facilityId");
    ErrorCode PATROL_CONFIRM_TASK_NOT_FOUND = new ErrorCode(1_009_001_008, "确认路线任务不存在");

    ErrorCode PATROL_FACADE_TASK_ID_REQUIRED = new ErrorCode(1_009_002_001, "门面操作缺少 taskId");
    ErrorCode PATROL_FACADE_TASK_NOT_FOUND = new ErrorCode(1_009_002_002, "任务不存在");
    ErrorCode PATROL_FACADE_ROUTE_NOT_CONFIRMED = new ErrorCode(1_009_002_003, "任务尚未确认路线，无法排期");
    ErrorCode PATROL_FACADE_NOT_RESERVED = new ErrorCode(1_009_002_004, "任务尚未排期预占，无法启用");
    ErrorCode PATROL_FACADE_NO_ACTIVE_SLOTS = new ErrorCode(1_009_002_005, "启用验窗失败：无有效计划点");
    ErrorCode PATROL_FACADE_OCCUPANCY_CONFLICT = new ErrorCode(1_009_002_006, "启用验窗失败：时间轴占用冲突");
    ErrorCode PATROL_FACADE_RUNTIME_JOB_REQUIRED = new ErrorCode(1_009_002_007, "缺少 runtimeJobId");
    ErrorCode PATROL_FACADE_REPLAN_CONTEXT_REQUIRED = new ErrorCode(1_009_002_008, "恢复重排缺少 remainingStopIds 或 completedSlotIds");
}
