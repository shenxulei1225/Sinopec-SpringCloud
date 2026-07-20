package cn.cheers.x.module.platform.scheduling.enums;

import cn.cheers.x.framework.common.exception.ErrorCode;

/**
 * platform-scheduling 错误码（1-004-057-000 段）
 */
public interface ErrorCodeConstants {

    ErrorCode SCHEDULING_DURATION_REQUIRED = new ErrorCode(1_004_057_000, "工作项缺少有效预估时长");
    ErrorCode SCHEDULING_BATCH_REJECTED = new ErrorCode(1_004_057_001, "排程冲突：按策略拒绝本批");
    ErrorCode SCHEDULING_CANNOT_PLACE = new ErrorCode(1_004_057_002, "排程冲突：无法在时间窗内安置工作项");

}
