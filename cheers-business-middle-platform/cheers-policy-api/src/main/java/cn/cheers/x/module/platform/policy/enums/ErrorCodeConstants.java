package cn.cheers.x.module.platform.policy.enums;

import cn.cheers.x.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {

    ErrorCode POLICY_TEMPLATE_NOT_FOUND = new ErrorCode(1_004_053_000, "策略模板不存在");
    ErrorCode POLICY_SET_NOT_FOUND = new ErrorCode(1_004_053_001, "策略集不存在");
    ErrorCode POLICY_SET_NOT_PUBLISHED = new ErrorCode(1_004_053_002, "策略集未发布");
    ErrorCode POLICY_SNAPSHOT_NOT_FOUND = new ErrorCode(1_004_053_003, "策略快照不存在");
}
