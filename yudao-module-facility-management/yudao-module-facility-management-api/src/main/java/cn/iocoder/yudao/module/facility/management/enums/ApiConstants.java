package cn.iocoder.yudao.module.facility.management.enums;

import cn.iocoder.yudao.framework.common.enums.RpcConstants;

/**
 * 设施管理域 API 常量。
 */
public class ApiConstants {

    /** @deprecated 独立服务废弃；读 Facade 由 {@code dynamicbusiness-server} 提供 */
    @Deprecated
    public static final String NAME = "facility-management-server";

    /** Feign 调用目标：动态业务服务上的设施读 Facade */
    public static final String FEIGN_NAME = "dynamicbusiness-server";

    public static final String PREFIX = RpcConstants.RPC_API_PREFIX + "/dynamicbusiness/facility";

    public static final String VERSION = "1.0.0";

}
