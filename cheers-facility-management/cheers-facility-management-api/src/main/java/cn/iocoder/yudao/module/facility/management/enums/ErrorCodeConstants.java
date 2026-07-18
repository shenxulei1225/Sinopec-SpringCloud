package cn.iocoder.yudao.module.facility.management.enums;

import cn.cheers.x.framework.common.exception.ErrorCode;

/**
 * 设施管理错误码枚举类
 *
 * facility 系统，使用 1-010-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 站场相关 1-010-001-000 ==========
    ErrorCode SITE_NOT_EXISTS = new ErrorCode(1_010_001_000, "站场不存在");
    ErrorCode SITE_CODE_DUPLICATE = new ErrorCode(1_010_001_001, "站场编码已存在");
    ErrorCode SITE_NAME_DUPLICATE = new ErrorCode(1_010_001_002, "站场名称已存在");
    ErrorCode SITE_HAS_CHILDREN = new ErrorCode(1_010_001_003, "站场存在子节点，无法删除");
    ErrorCode SITE_PARENT_NOT_SELF = new ErrorCode(1_010_001_004, "父节点不能是自己");
    ErrorCode SITE_PARENT_NOT_CHILD = new ErrorCode(1_010_001_005, "父节点不能是自己的子节点");

    // ========== 站场类型相关 1-010-003-000 ==========
    ErrorCode SITE_TYPE_NOT_EXISTS = new ErrorCode(1_010_003_000, "站场类型不存在");
    ErrorCode SITE_TYPE_CODE_DUPLICATE = new ErrorCode(1_010_003_001, "站场类型编码已存在");

    // ========== 设施相关 1-010-002-000 ==========
    ErrorCode FACILITY_NOT_EXISTS = new ErrorCode(1_010_002_000, "设施不存在");
    ErrorCode FACILITY_CODE_DUPLICATE = new ErrorCode(1_010_002_001, "设施编码已存在");

}
