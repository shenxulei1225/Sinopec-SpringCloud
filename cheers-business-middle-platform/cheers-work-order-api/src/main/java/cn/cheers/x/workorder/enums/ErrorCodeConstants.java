package cn.cheers.x.workorder.enums;

import cn.cheers.x.framework.common.exception.ErrorCode;

/**
 * 工单标准服务错误码
 *
 * <p>work-order 系统，使用 1-021-000-000 段（紧邻 alarm 1-020）</p>
 */
public interface ErrorCodeConstants {

    // ========== 现场作业标准 1-021-000-000 ==========
    ErrorCode FIELD_WORK_STANDARD_NOT_EXISTS = new ErrorCode(1_021_000_000, "现场作业标准不存在");
    ErrorCode FIELD_WORK_STANDARD_PUBLISHED_IMMUTABLE = new ErrorCode(1_021_000_001, "已发布的现场作业标准不可修改");
    ErrorCode FIELD_WORK_STANDARD_STEPS_EMPTY = new ErrorCode(1_021_000_002, "现场作业标准步骤不能为空");
    ErrorCode FIELD_WORK_STANDARD_PUBLISH_NOT_DRAFT = new ErrorCode(1_021_000_003, "仅草稿状态的现场作业标准可发布");

}
