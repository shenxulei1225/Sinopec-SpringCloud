package cn.cheers.x.module.platformresource.enums;

import cn.cheers.x.framework.common.enums.RpcConstants;

/**
 * platform-resource RPC：服务名须与 spring.application.name 一致。
 */
public final class ApiConstants {

    public static final String NAME = "platformresource-server";

    public static final String PREFIX = RpcConstants.RPC_API_PREFIX + "/platformresource";

    public static final String VERSION = "1.0.0";

    private ApiConstants() {
    }
}
