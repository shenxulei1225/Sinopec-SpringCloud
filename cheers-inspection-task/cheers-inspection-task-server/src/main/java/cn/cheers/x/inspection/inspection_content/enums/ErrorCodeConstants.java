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
}
