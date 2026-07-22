package cn.cheers.x.iot.enums;

import cn.cheers.x.framework.common.enums.RpcConstants;

/**
 * API 相关的枚举
 *
 * 
 */
public class ApiConstants {

    /**
     * 服务名，须与 spring.application.name 一致
     */
    public static final String NAME = "iot-server";

    public static final String PREFIX = RpcConstants.RPC_API_PREFIX + "/iot";

    public static final String VERSION = "1.0.0";

}
