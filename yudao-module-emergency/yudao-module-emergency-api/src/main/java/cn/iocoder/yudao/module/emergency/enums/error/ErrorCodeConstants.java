package cn.iocoder.yudao.module.emergency.enums.error;

import cn.cheers.x.framework.common.exception.ErrorCode;

/**
 * 应急管理 错误码枚举类
 *
 * 应急系统错误码区间 [1-008-000-000 ~ 1-009-000-000)
 */
public interface ErrorCodeConstants {

    // ========== 预案模块 1-008-001-000 ==========
    ErrorCode PLAN_NOT_EXISTS = new ErrorCode(1_008_001_001, "预案不存在");
    ErrorCode PLAN_EXISTS = new ErrorCode(1_008_001_002, "预案已存在");
    ErrorCode PLAN_STATUS_INVALID = new ErrorCode(1_008_001_003, "预案状态不正确");

    // ========== 事件模块 1-008-002-000 ==========
    ErrorCode EVENT_NOT_EXISTS = new ErrorCode(1_008_002_001, "事件不存在");
    ErrorCode EVENT_STATUS_INVALID = new ErrorCode(1_008_002_002, "事件状态不正确");

    // ========== 响应模块 1-008-003-000 ==========
    ErrorCode RESPONSE_NOT_EXISTS = new ErrorCode(1_008_003_001, "响应不存在");
    ErrorCode RESPONSE_STATUS_INVALID = new ErrorCode(1_008_003_002, "响应状态不正确");
    ErrorCode RESPONSE_PLAN_MATCH_FAILED = new ErrorCode(1_008_003_003, "未匹配到合适的预案");

    // ========== 资源模块 1-008-004-000 ==========
    ErrorCode RESOURCE_NOT_EXISTS = new ErrorCode(1_008_004_001, "资源不存在");
    ErrorCode RESOURCE_NOT_AVAILABLE = new ErrorCode(1_008_004_002, "资源不可用");

    // ========== 任务模块 1-008-005-000 ==========
    ErrorCode TASK_NOT_EXISTS = new ErrorCode(1_008_005_001, "任务不存在");

    // ========== 调度模块 1-008-006-000 ==========
    ErrorCode DISPATCH_NOT_EXISTS = new ErrorCode(1_008_006_001, "调度记录不存在");

    // ========== 指令模块 1-008-007-000 ==========
    ErrorCode COMMAND_TEMPLATE_NOT_EXISTS = new ErrorCode(1_008_007_001, "指令模板不存在");
    ErrorCode COMMAND_NOT_EXISTS = new ErrorCode(1_008_007_002, "指令不存在");
    ErrorCode COMMAND_STATUS_INVALID = new ErrorCode(1_008_007_003, "指令状态不正确");
    ErrorCode COMMAND_EXECUTOR_NOT_EXISTS = new ErrorCode(1_008_007_004, "指令执行人不存在");
}

