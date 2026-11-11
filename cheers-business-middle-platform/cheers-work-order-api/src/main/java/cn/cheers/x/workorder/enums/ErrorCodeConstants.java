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

    // ========== 工单生命周期 1-021-001-000 ==========
    ErrorCode WORK_ORDER_NOT_EXISTS = new ErrorCode(1_021_001_000, "工单不存在");
    ErrorCode WORK_ORDER_STANDARD_NOT_PUBLISHED = new ErrorCode(1_021_001_001, "仅已发布的现场作业标准可生成工单");
    ErrorCode WORK_ORDER_STANDARD_REQUIRED = new ErrorCode(1_021_001_002, "生成工单时必须指定标准 ID 或标准编码");
    ErrorCode WORK_ORDER_STATUS_INVALID = new ErrorCode(1_021_001_003, "工单当前状态不允许该操作");
    ErrorCode WORK_ORDER_STEP_NOT_EXISTS = new ErrorCode(1_021_001_004, "工单步骤不存在");
    ErrorCode WORK_ORDER_REQUIRED_STEPS_INCOMPLETE = new ErrorCode(1_021_001_005, "存在未完成的必填步骤，无法完工");
    ErrorCode WORK_ORDER_STEPS_EMPTY = new ErrorCode(1_021_001_006, "现场作业标准步骤不能为空，无法生成工单");
    ErrorCode WORK_ORDER_TEAM_REQUIRED = new ErrorCode(1_021_001_007, "派单班组不能为空");
    ErrorCode WORK_ORDER_HANDLING_TIME_REQUIRED = new ErrorCode(1_021_001_008, "请填写处理时刻");
    ErrorCode WORK_ORDER_PLAN_WINDOW_INCOMPLETE = new ErrorCode(1_021_001_009, "占档期的工单要同时填写计划开始和计划结束");
    ErrorCode WORK_ORDER_CONTENT_REQUIRED = new ErrorCode(1_021_001_010, "请填写工作内容");
    ErrorCode WORK_ORDER_ALREADY_ASSIGNED = new ErrorCode(1_021_001_008, "工单已有执行人，无法认领");
    ErrorCode WORK_ORDER_CLAIM_TEAM_MISSING = new ErrorCode(1_021_001_009, "工单未指定班组，无法认领");
    ErrorCode WORK_ORDER_CLAIM_NOT_MEMBER = new ErrorCode(1_021_001_010, "你不在这个班组里，不能认领");
    ErrorCode WORK_ORDER_ASSIGN_NOT_MEMBER = new ErrorCode(1_021_001_011, "只能指定这个班组里的人");

}
