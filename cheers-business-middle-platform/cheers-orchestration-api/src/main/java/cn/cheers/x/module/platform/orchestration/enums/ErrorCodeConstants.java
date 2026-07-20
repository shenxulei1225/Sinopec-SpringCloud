package cn.cheers.x.module.platform.orchestration.enums;

import cn.cheers.x.framework.common.exception.ErrorCode;

/**
 * platform-orchestration 错误码（1-004-052-000 段）
 */
public interface ErrorCodeConstants {

    ErrorCode SCHEDULE_RUN_WORK_ITEMS_EMPTY = new ErrorCode(1_004_052_000, "排程运行缺少作业项");
    ErrorCode SCHEDULE_RUN_ORCHESTRATION_UNKNOWN = new ErrorCode(1_004_052_001, "不支持的编排模板");
    ErrorCode SCHEDULE_RUN_SCHEDULING_SPEC_REQUIRED = new ErrorCode(1_004_052_002, "排程运行缺少 schedulingSpec");
    ErrorCode SCHEDULE_RUN_WORK_OR_SOURCE_REQUIRED = new ErrorCode(1_004_052_003, "排程运行缺少作业项或 L1 实例引用");
    ErrorCode SCHEDULE_RUN_MAPPING_PROFILE_REQUIRED = new ErrorCode(1_004_052_004, "排程运行缺少已发布的映射配置");
    ErrorCode SCHEDULE_DISPATCH_STANDARD_REQUIRED = new ErrorCode(1_004_052_005, "派工生成工单时必须指定现场作业标准 ID");
    ErrorCode ORCHESTRATION_PHASE_HANDLER_MISSING = new ErrorCode(1_004_052_006, "编排阶段缺少处理器");
    ErrorCode ORCHESTRATION_PAYLOAD_INVALID = new ErrorCode(1_004_052_007, "编排载荷缺少必填字段");

}
