package cn.cheers.x.module.platform.capability.enums;

import cn.cheers.x.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {

    ErrorCode CAPABILITY_PACK_NOT_FOUND = new ErrorCode(1_004_054_000, "能力包不存在");
    ErrorCode BINDING_NOT_FOUND = new ErrorCode(1_004_054_001, "过程能力绑定不存在");
    ErrorCode BINDING_NOT_PUBLISHED = new ErrorCode(1_004_054_002, "过程能力绑定未发布");
    ErrorCode MAPPING_PROFILE_NOT_FOUND = new ErrorCode(1_004_054_003, "映射配置不存在");
    ErrorCode MAPPING_PROFILE_BUSINESS_TYPE_MISMATCH = new ErrorCode(1_004_054_004, "映射配置与业务类型不匹配");
}
