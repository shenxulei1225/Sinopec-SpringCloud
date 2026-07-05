package cn.cheers.x.module.platform.runtime.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * platform-runtime 错误码（1-004-051-000 段）
 */
public interface ErrorCodeConstants {

    ErrorCode RUNTIME_JOB_NOT_EXISTS = new ErrorCode(1_004_051_000, "运行作业不存在");
    ErrorCode SCHEDULE_SLOT_NOT_EXISTS = new ErrorCode(1_004_051_001, "计划点不存在");

}
