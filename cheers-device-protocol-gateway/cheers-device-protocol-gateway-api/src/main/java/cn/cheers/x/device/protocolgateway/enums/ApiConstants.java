package cn.cheers.x.device.protocolgateway.enums;

import cn.cheers.x.framework.common.enums.RpcConstants;

/**
 * 设备协议网关 RPC 常量。
 * <p>服务名须与网关 {@code spring.application.name} 一致。
 */
public final class ApiConstants {

    public static final String NAME = "cheers-device-protocol-gateway-server";

    public static final String PREFIX = RpcConstants.RPC_API_PREFIX + "/device-protocol";

    private ApiConstants() {
    }
}
