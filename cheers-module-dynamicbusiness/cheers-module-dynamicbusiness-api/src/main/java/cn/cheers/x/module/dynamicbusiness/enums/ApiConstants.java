package cn.cheers.x.module.dynamicbusiness.enums;

import cn.iocoder.yudao.framework.common.enums.RpcConstants;

/**
 * API 相关的枚举
 *
 * 
 */
public class ApiConstants {

    /**
     * 服务名
     *
     * 注意，需要保证和 spring.application.name 保持一致
     */
    public static final String NAME = "dynamicbusiness-server";

    /** 历史分类 RPC 前缀（CategoryCommonApi） */
    public static final String PREFIX = RpcConstants.RPC_API_PREFIX + "/system";

    /** 动态业务模块 RPC 根前缀 */
    public static final String DYNAMICBUSINESS_PREFIX = RpcConstants.RPC_API_PREFIX + "/dynamicbusiness";

    public static final String VERSION = "1.0.0";

}
