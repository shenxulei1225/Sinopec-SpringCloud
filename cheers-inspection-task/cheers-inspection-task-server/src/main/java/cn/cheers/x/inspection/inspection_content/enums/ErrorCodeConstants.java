package cn.cheers.x.inspection.inspection_content.enums;

import cn.cheers.x.framework.common.exception.ErrorCode;

/**
 * 巡检内容域错误码。
 */
public interface ErrorCodeConstants {

    ErrorCode PATROL_FACADE_TASK_ID_REQUIRED = new ErrorCode(1_009_002_001, "门面操作缺少 taskId");
    ErrorCode PATROL_FACADE_TASK_NOT_FOUND = new ErrorCode(1_009_002_002, "任务不存在");
    ErrorCode PATROL_FACADE_ROUTE_NOT_SAVED = new ErrorCode(1_009_002_003, "还没有路线结果，请先开始规划");
    /** 路径规划没有得到可用路线。开始编排只转交这个结果。向导提示由页面来写。 */
    ErrorCode PATROL_FACADE_ROUTE_PLAN_UNUSABLE = new ErrorCode(1_009_002_012, "路线不可用");
    ErrorCode PATROL_FACADE_NOT_ARRANGED = new ErrorCode(1_009_002_004, "请先完成排期核对（无冲突进入或智能编排），再生成任务");
    ErrorCode PATROL_FACADE_NO_ACTIVE_SLOTS = new ErrorCode(1_009_002_005, "生成任务失败：没有有效的计划时刻");
    ErrorCode PATROL_FACADE_OCCUPANCY_CONFLICT = new ErrorCode(1_009_002_006, "生成任务失败：设备时间已被其他任务占用");
    ErrorCode PATROL_FACADE_RUNTIME_JOB_REQUIRED = new ErrorCode(1_009_002_007, "缺少 runtimeJobId");
    ErrorCode PATROL_FACADE_EXECUTION_STEPS_REQUIRED = new ErrorCode(1_009_002_009, "任务没有执行步骤图，请先在排期与资源处理完冲突");
    ErrorCode PATROL_FACADE_SCHEDULE_ALREADY_ENABLED = new ErrorCode(1_009_002_010, "任务已生成并排期。确认重排后可在本页重新检测并替换未执行占窗");
}
