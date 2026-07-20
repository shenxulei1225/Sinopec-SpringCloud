package cn.iocoder.yudao.module.emergency.enums.error;

import cn.cheers.x.framework.common.exception.ErrorCode;

/**
 * 应急管理系统 错误码枚举类
 *
 * 应急管理系统，使用 1-004-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 事件相关 1-004-001-000 ==========
    ErrorCode EVENT_NOT_EXISTS = new ErrorCode(1004001000, "事件不存在");
    ErrorCode EVENT_CODE_DUPLICATE = new ErrorCode(1004001001, "事件编码重复");
    ErrorCode EVENT_STATUS_INVALID = new ErrorCode(1004001002, "事件状态无效");
    ErrorCode EVENT_STATUS_TRANSITION_INVALID = new ErrorCode(1004001003, "事件状态转换无效：{}");
    ErrorCode EVENT_RESPONDING_REQUIRES_LEADER = new ErrorCode(1004001004, "进入响应中状态必须分配至少一名负责人");
    ErrorCode EVENT_CLOSE_REQUIRES_ALL_TASKS_COMPLETED = new ErrorCode(1004001005, "关闭事件前所有任务必须已完成");
    ErrorCode EVENT_READ_ONLY_STATUS = new ErrorCode(1004001006, "已关闭或已取消的事件处于只读状态，不能修改");
    ErrorCode EVENT_DUPLICATE_DETECTED = new ErrorCode(1004001007, "检测到重复事件：{}");
    ErrorCode EVENT_EXTERNAL_REPORT_ORG_NAME_REQUIRED = new ErrorCode(1004001008, "外部上报必须填写上报单位");
    ErrorCode EVENT_EXTERNAL_REPORTER_NAME_REQUIRED = new ErrorCode(1004001009, "外部上报必须填写上报人姓名");

    // ========== 响应相关 1-004-002-000 ==========
    ErrorCode RESPONSE_NOT_EXISTS = new ErrorCode(1004002000, "响应不存在");

    // ========== 预案相关 1-004-003-000 ==========
    ErrorCode PLAN_NOT_EXISTS = new ErrorCode(1004003000, "预案不存在");
    ErrorCode PLAN_EXISTS = new ErrorCode(1004003001, "预案已存在");
    ErrorCode PLAN_STATUS_TRANSITION_INVALID = new ErrorCode(1004003002, "预案状态转换无效：{}");
    ErrorCode PLAN_NOT_AVAILABLE = new ErrorCode(1004003003, "预案不可用，只有已发布的预案才能被用于创建应急响应");
    ErrorCode PLAN_STEP_CONCURRENT_UPDATE = new ErrorCode(1004003004, "数据已被其他用户修改，请刷新后重试");

    // ========== 任务相关 1-004-004-000 ==========
    ErrorCode TASK_NOT_EXISTS = new ErrorCode(1004004000, "任务不存在");
    ErrorCode TASK_HAS_INCOMPLETE_SUBTASKS = new ErrorCode(1004004001, "任务存在未完成的子任务，无法完成");

    // ========== 调度相关 1-004-005-000 ==========
    ErrorCode DISPATCH_NOT_EXISTS = new ErrorCode(1004005000, "调度不存在");

    // ========== 资源池相关 1-004-006-000 ==========
    ErrorCode RESOURCE_NOT_EXISTS = new ErrorCode(1004006000, "资源不存在");
    ErrorCode RESOURCE_NAME_DUPLICATE = new ErrorCode(1004006001, "资源名称重复");
    ErrorCode RESOURCE_NOT_AVAILABLE = new ErrorCode(1004006002, "资源不可用");
    ErrorCode RESOURCE_ALREADY_DISPATCHED = new ErrorCode(1004006003, "资源已被其他事件占用");

    // ========== 指令相关 1-004-007-000 ==========
    ErrorCode COMMAND_NOT_EXISTS = new ErrorCode(1004007000, "指令不存在");

    // ========== 指令步骤相关 1-004-008-000 ==========
    ErrorCode COMMAND_STEP_NOT_EXISTS = new ErrorCode(1004008000, "指令步骤不存在");
    ErrorCode COMMAND_STEP_TIME_LIMIT_INVALID = new ErrorCode(1004008001, "执行时限必须在1-1440分钟之间");
    ErrorCode COMMAND_STEP_ALREADY_COMPLETED = new ErrorCode(1004008002, "指令步骤已完成，不能修改");
    ErrorCode COMMAND_STEP_ALREADY_TIMEOUT = new ErrorCode(1004008003, "指令步骤已超时，不能修改");
    ErrorCode COMMAND_STEP_CANNOT_DELETE = new ErrorCode(1004008004, "指令步骤已开始执行，不能删除");
    ErrorCode COMMAND_STEP_STATUS_INVALID = new ErrorCode(1004008005, "指令步骤状态无效");
    ErrorCode COMMAND_STEP_TIMEOUT_REASON_REQUIRED = new ErrorCode(1004008006, "超时步骤必须提供超时原因");
    ErrorCode COMMAND_STEP_HANDLING_MEASURES_REQUIRED = new ErrorCode(1004008007, "超时步骤必须提供处理措施");

    // ========== 组织相关 1-004-009-000 ==========
    ErrorCode ORGANIZATION_NOT_EXISTS = new ErrorCode(1004009000, "应急组织不存在");
    ErrorCode ORGANIZATION_CODE_DUPLICATE = new ErrorCode(1004009001, "组织编号重复");
    ErrorCode ORGANIZATION_MEMBER_NOT_EXISTS = new ErrorCode(1004009002, "组织成员不存在");
    ErrorCode ORGANIZATION_MEMBER_EXISTS = new ErrorCode(1004009003, "组织成员已存在");

    // ========== 保障相关 1-004-010-000 ==========
    ErrorCode GUARANTEE_NOT_EXISTS = new ErrorCode(1004010000, "应急保障不存在");
    ErrorCode GUARANTEE_CODE_DUPLICATE = new ErrorCode(1004010001, "保障编号重复");
    ErrorCode GUARANTEE_RESOURCE_NOT_EXISTS = new ErrorCode(1004010002, "保障资源不存在");

    // ========== 通讯录相关 1-004-011-000 ==========
    ErrorCode CONTACT_NOT_EXISTS = new ErrorCode(1004011000, "应急联络通讯录不存在");
    ErrorCode CONTACT_CODE_DUPLICATE = new ErrorCode(1004011001, "联系人编号重复");

    // ========== 信息上报相关 1-004-012-000 ==========
    ErrorCode INFORMATION_REPORT_STATUS_ERROR = new ErrorCode(1004012000, "信息上报状态错误");
    ErrorCode INFORMATION_REPORT_NOT_EXISTS = new ErrorCode(1004012001, "信息上报不存在");

    // ========== 事件分类相关 1-004-013-000 ==========
    ErrorCode EVENT_CATEGORY_NOT_EXISTS = new ErrorCode(1004013000, "事件分类不存在");

    // ========== AB角色相关 1-004-014-000 ==========
    ErrorCode AB_ROLE_USER_SAME = new ErrorCode(1004014000, "AB角色用户不能相同");
    ErrorCode AB_ROLE_NOT_EXISTS = new ErrorCode(1004014001, "AB角色不存在");
    ErrorCode AB_ROLE_CODE_DUPLICATE = new ErrorCode(1004014002, "AB角色编码重复");

    // ========== 分类服务相关 1-004-015-000 ==========
    ErrorCode CATEGORY_SERVICE_UNAVAILABLE = new ErrorCode(1004015000, "分类服务不可用");
    ErrorCode CATEGORY_CREATE_FAILED = new ErrorCode(1004015001, "分类创建失败");
    ErrorCode CATEGORY_UPDATE_FAILED = new ErrorCode(1004015002, "分类更新失败");
    ErrorCode CATEGORY_DELETE_FAILED = new ErrorCode(1004015003, "分类删除失败");
    ErrorCode CATEGORY_NOT_EXISTS = new ErrorCode(1004015004, "分类不存在");
    ErrorCode CATEGORY_QUERY_FAILED = new ErrorCode(1004015005, "分类查询失败");
    ErrorCode CATEGORY_MOVE_FAILED = new ErrorCode(1004015006, "分类移动失败");
    ErrorCode CATEGORY_SORT_FAILED = new ErrorCode(1004015007, "分类排序失败");
    ErrorCode CATEGORY_ENABLE_FAILED = new ErrorCode(1004015008, "分类启用失败");
    ErrorCode CATEGORY_DISABLE_FAILED = new ErrorCode(1004015009, "分类禁用失败");
    ErrorCode CATEGORY_SEARCH_FAILED = new ErrorCode(1004015010, "分类搜索失败");

    // ========== 分布式锁相关 1-004-016-000 ==========
    ErrorCode LOCK_ACQUIRE_FAILED = new ErrorCode(1004016000, "获取分布式锁失败");
    ErrorCode LOCK_ACQUIRE_INTERRUPTED = new ErrorCode(1004016001, "获取分布式锁被中断");

    // ========== 数据库备份相关 1-004-017-000 ==========
    ErrorCode BACKUP_NOT_EXISTS = new ErrorCode(1004017000, "备份不存在");
    ErrorCode RESTORE_FAILED = new ErrorCode(1004017001, "数据库恢复失败");
    ErrorCode BACKUP_FILE_INVALID = new ErrorCode(1004017002, "备份文件无效");

}



