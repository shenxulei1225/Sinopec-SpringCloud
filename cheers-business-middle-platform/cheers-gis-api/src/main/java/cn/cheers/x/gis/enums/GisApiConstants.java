package cn.cheers.x.gis.enums;

import cn.iocoder.yudao.framework.common.enums.RpcConstants;

/**
 * GIS RPC 常量。Nacos 服务名与 spring.application.name 对齐。
 */
public final class GisApiConstants {

    public static final String NAME = "gis-server";

    public static final String PREFIX = RpcConstants.RPC_API_PREFIX + "/gis";

    public static final String VERSION = "1.0.0";

    private GisApiConstants() {
    }
}
