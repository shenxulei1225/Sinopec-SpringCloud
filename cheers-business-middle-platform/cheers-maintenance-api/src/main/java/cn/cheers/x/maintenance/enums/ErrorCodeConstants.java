package cn.cheers.x.maintenance.enums;

import cn.cheers.x.framework.common.exception.ErrorCode;

/**
 * 维护手册服务错误码（1-022 段，紧邻 work-order 1-021）
 */
public interface ErrorCodeConstants {

    ErrorCode FIELD_WORK_STANDARD_NOT_EXISTS = new ErrorCode(1_022_000_000, "现场作业标准不存在");
    ErrorCode FIELD_WORK_STANDARD_PUBLISHED_IMMUTABLE = new ErrorCode(1_022_000_001, "已发布的现场作业标准不可修改");
    ErrorCode FIELD_WORK_STANDARD_STEPS_EMPTY = new ErrorCode(1_022_000_002, "现场作业标准步骤不能为空");
    ErrorCode FIELD_WORK_STANDARD_PUBLISH_NOT_DRAFT = new ErrorCode(1_022_000_003, "仅草稿状态的现场作业标准可发布");
    ErrorCode FIELD_WORK_STANDARD_NOT_PUBLISHED = new ErrorCode(1_022_000_004, "现场作业标准未发布");

    ErrorCode HANDBOOK_NOT_EXISTS = new ErrorCode(1_022_001_000, "维护手册不存在");
    ErrorCode HANDBOOK_PUBLISHED_IMMUTABLE = new ErrorCode(1_022_001_001, "已发布的维护手册不可修改");
    ErrorCode HANDBOOK_PUBLISH_NOT_DRAFT = new ErrorCode(1_022_001_002, "仅草稿状态的维护手册可发布");
    ErrorCode HANDBOOK_STANDARD_NOT_PUBLISHED = new ErrorCode(1_022_001_003, "发布手册要求关联已发布的现场作业标准");

    ErrorCode BINDING_RULE_NOT_EXISTS = new ErrorCode(1_022_002_000, "绑定规则不存在");
    ErrorCode BINDING_RULE_NOT_FOUND = new ErrorCode(1_022_002_001, "未匹配到绑定规则");
    ErrorCode BINDING_RULE_PUBLISH_NOT_DRAFT = new ErrorCode(1_022_002_002, "仅草稿状态的绑定规则可发布");
    ErrorCode BINDING_RULE_PUBLISHED_IMMUTABLE = new ErrorCode(1_022_002_003, "已发布的绑定规则不可修改");

    ErrorCode CALENDAR_HANDBOOK_NOT_PUBLISHED = new ErrorCode(1_022_003_000, "日历展开要求已发布的维护手册");
    ErrorCode CALENDAR_FREQUENCY_UNSUPPORTED = new ErrorCode(1_022_003_001, "不支持的频率编码");
    ErrorCode CALENDAR_ENTRY_NOT_EXISTS = new ErrorCode(1_022_003_002, "日历条目不存在");
    ErrorCode CALENDAR_SCHEDULING_SPEC_REQUIRED = new ErrorCode(1_022_003_003, "未配置日历触发所需的排程规格");
    ErrorCode CALENDAR_STATUS_INVALID = new ErrorCode(1_022_003_004, "日历条目状态不允许触发");

    ErrorCode CORRECTIVE_CASE_NOT_EXISTS = new ErrorCode(1_022_004_000, "故障维修主单不存在");
    ErrorCode CORRECTIVE_STATUS_INVALID = new ErrorCode(1_022_004_001, "故障主单当前状态不允许该操作");
    ErrorCode CORRECTIVE_WORK_ORDER_REQUIRED = new ErrorCode(1_022_004_002, "故障主单尚未派工生成工单");
    ErrorCode CORRECTIVE_WORK_ORDER_NOT_COMPLETED = new ErrorCode(1_022_004_003, "工单未完工，不能关闭主单");
}



