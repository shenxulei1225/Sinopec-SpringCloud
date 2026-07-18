package cn.iocoder.yudao.module.alarm.enums;

import cn.cheers.x.framework.common.exception.ErrorCode;

/**
 * 告警模块 错误码枚举类
 *
 * alarm 系统，使用 1-020-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 告警模块 1-020-000-000 ==========
    ErrorCode ALARM_NOT_EXISTS = new ErrorCode(1_020_000_000, "告警不存在");
    ErrorCode ALARM_ALREADY_CLOSED = new ErrorCode(1_020_000_001, "告警已关闭，无法操作");
    ErrorCode ALARM_STATUS_INVALID_TRANSITION = new ErrorCode(1_020_000_002, "非法的状态转换：当前状态为【{}】，不能转换为【{}】");
    ErrorCode ALARM_CLOSE_REASON_REQUIRED = new ErrorCode(1_020_000_003, "关闭告警必须填写关闭原因");
    ErrorCode ALARM_HANDLE_MEASURE_REQUIRED = new ErrorCode(1_020_000_004, "处理告警必须填写处理措施");
    ErrorCode ALARM_CONTENT_LENGTH_INVALID = new ErrorCode(1_020_000_005, "告警内容长度必须在10-500字之间");
    ErrorCode ALARM_LEVEL_INVALID = new ErrorCode(1_020_000_006, "告警级别无效");
    ErrorCode ALARM_TYPE_NOT_EXISTS = new ErrorCode(1_020_000_007, "告警类型不存在");
    ErrorCode ALARM_STATUS_CANNOT_ACKNOWLEDGE = new ErrorCode(1_020_000_008, "当前状态不允许确认，只有待确认状态的告警可以确认");
    ErrorCode ALARM_STATUS_CANNOT_HANDLE = new ErrorCode(1_020_000_009, "当前状态不允许处理，只有已确认状态的告警可以处理");
    ErrorCode ALARM_STATUS_CANNOT_CLOSE = new ErrorCode(1_020_000_010, "当前状态不允许关闭，只有处理中状态的告警可以关闭");
    ErrorCode ALARM_HANDLE_RESULT_INVALID = new ErrorCode(1_020_000_011, "处理结果无效");
    ErrorCode ALARM_CLOSE_REASON_INVALID = new ErrorCode(1_020_000_012, "关闭原因无效");
    ErrorCode ALARM_QUERY_TIME_RANGE_EXCEED = new ErrorCode(1_020_000_013, "历史告警查询时间范围不能超过1年");

    // ========== 告警附件 1-020-001-000 ==========
    ErrorCode ALARM_ATTACHMENT_COUNT_EXCEED = new ErrorCode(1_020_001_000, "告警附件数量超过限制（最多5个）");
    ErrorCode ALARM_ATTACHMENT_SIZE_EXCEED = new ErrorCode(1_020_001_001, "告警附件大小超过限制（单个不超过10MB）");
    ErrorCode ALARM_ATTACHMENT_TYPE_INVALID = new ErrorCode(1_020_001_002, "告警附件类型不支持，仅支持jpg/png/mp4格式");

    // ========== 告警规则 1-020-002-000 ==========
    ErrorCode ALARM_RULE_NOT_EXISTS = new ErrorCode(1_020_002_000, "告警规则不存在");
    ErrorCode ALARM_RULE_CODE_DUPLICATE = new ErrorCode(1_020_002_001, "告警规则编码已存在");
    ErrorCode ALARM_RULE_NAME_DUPLICATE = new ErrorCode(1_020_002_002, "告警规则名称已存在");
    ErrorCode ALARM_RULE_CONDITION_INVALID = new ErrorCode(1_020_002_003, "告警规则条件表达式无效：{}");

    // ========== 联动规则 1-020-003-000 ==========
    ErrorCode LINKAGE_RULE_NOT_EXISTS = new ErrorCode(1_020_003_000, "联动规则不存在");
    ErrorCode LINKAGE_RULE_CODE_DUPLICATE = new ErrorCode(1_020_003_001, "联动规则编码已存在");
    ErrorCode LINKAGE_RULE_NAME_DUPLICATE = new ErrorCode(1_020_003_002, "联动规则名称已存在");
    ErrorCode LINKAGE_RULE_ACTIONS_INVALID = new ErrorCode(1_020_003_003, "联动规则动作配置无效：{}");

    // ========== 联动执行 1-020-004-000 ==========
    ErrorCode LINKAGE_EXECUTION_NOT_EXISTS = new ErrorCode(1_020_004_000, "联动执行记录不存在");
    ErrorCode LINKAGE_EXECUTION_FAILED = new ErrorCode(1_020_004_001, "联动执行失败：{}");
    ErrorCode LINKAGE_EXECUTION_TIMEOUT = new ErrorCode(1_020_004_002, "联动执行超时");
    ErrorCode LINKAGE_DEVICE_NOT_AVAILABLE = new ErrorCode(1_020_004_003, "联动设备不可用：{}");

}
