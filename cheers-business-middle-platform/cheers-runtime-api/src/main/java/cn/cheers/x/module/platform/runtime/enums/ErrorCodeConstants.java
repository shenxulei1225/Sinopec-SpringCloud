package cn.cheers.x.module.platform.runtime.enums;

import cn.cheers.x.framework.common.exception.ErrorCode;

/**
 * platform-runtime 错误码（1-004-051-000 段）
 */
public interface ErrorCodeConstants {

    ErrorCode RUNTIME_JOB_NOT_EXISTS = new ErrorCode(1_004_051_000, "运行作业不存在");
    ErrorCode SCHEDULE_SLOT_NOT_EXISTS = new ErrorCode(1_004_051_001, "计划点不存在");
    ErrorCode SCHEDULE_SLOT_QUERY_TIME_RANGE_REQUIRED = new ErrorCode(1_004_051_002, "计划点查询缺少必填时间窗 from/to");

    ErrorCode PROCESS_TIMELINE_APPEND_INVALID = new ErrorCode(1_004_051_100, "过程时间线条目缺少必填字段");

}
