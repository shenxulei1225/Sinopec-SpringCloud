package cn.cheers.x.inspection.inspection_content.enums;

import cn.cheers.x.framework.common.exception.ErrorCode;

/**
 * 巡检内容域错误码。
 */
public interface ErrorCodeConstants {

    ErrorCode PATROL_SAVE_ROUTE_WORK_ITEMS_EMPTY = new ErrorCode(1_009_001_001, "保存路线缺少工作项");
    ErrorCode PATROL_SAVE_ROUTE_PLANNED_ROUTE_REQUIRED = new ErrorCode(1_009_001_003, "保存路线缺少规划结果 plannedRoute");
    ErrorCode PATROL_SAVE_ROUTE_NETWORK_REF_REQUIRED = new ErrorCode(1_009_001_004, "保存路线缺少路网引用 networkRef");
    ErrorCode PATROL_SAVE_ROUTE_INSPECTION_TYPE_REQUIRED = new ErrorCode(1_009_001_005, "保存路线缺少巡检类型 inspectionType");
    ErrorCode PATROL_SAVE_ROUTE_TASK_ID_REQUIRED = new ErrorCode(1_009_001_006, "保存路线写入缺少 taskId");
    ErrorCode PATROL_SAVE_ROUTE_FACILITY_ID_REQUIRED = new ErrorCode(1_009_001_007, "保存路线写入缺少 facilityId");
    ErrorCode PATROL_SAVE_ROUTE_TASK_NOT_FOUND = new ErrorCode(1_009_001_008, "保存路线任务不存在");

    ErrorCode PATROL_FACADE_TASK_ID_REQUIRED = new ErrorCode(1_009_002_001, "门面操作缺少 taskId");
    ErrorCode PATROL_FACADE_TASK_NOT_FOUND = new ErrorCode(1_009_002_002, "任务不存在");
    ErrorCode PATROL_FACADE_ROUTE_NOT_SAVED = new ErrorCode(1_009_002_003, "任务尚未保存路线，无法排期");
    ErrorCode PATROL_FACADE_NOT_ARRANGED = new ErrorCode(1_009_002_004, "请先完成排期核对（无冲突进入或智能编排），再生成任务");
    ErrorCode PATROL_FACADE_SCHEDULE_STILL_CONFLICTS = new ErrorCode(1_009_002_011, "仍有排期冲突，请先改时间点或交给智能编排");
    ErrorCode PATROL_FACADE_NO_ACTIVE_SLOTS = new ErrorCode(1_009_002_005, "生成任务失败：没有有效的计划时刻");
    ErrorCode PATROL_FACADE_OCCUPANCY_CONFLICT = new ErrorCode(1_009_002_006, "生成任务失败：设备时间已被其他任务占用");
    ErrorCode PATROL_FACADE_RUNTIME_JOB_REQUIRED = new ErrorCode(1_009_002_007, "缺少 runtimeJobId");
    ErrorCode PATROL_FACADE_EXECUTION_STEPS_REQUIRED = new ErrorCode(1_009_002_009, "任务没有执行步骤图，请先在排期与资源处理完冲突");
    ErrorCode PATROL_FACADE_SCHEDULE_ALREADY_ENABLED = new ErrorCode(1_009_002_010, "任务已生成并排期。确认重排后可在本页重新检测并替换未执行占窗");
}
