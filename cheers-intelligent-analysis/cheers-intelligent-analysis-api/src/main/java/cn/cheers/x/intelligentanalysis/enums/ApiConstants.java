package cn.cheers.x.intelligentanalysis.enums;

import cn.cheers.x.framework.common.enums.RpcConstants;

/**
 * 智能分析服务的远程调用常量。
 *
 * <p>服务名必须与 spring.application.name 一致。本服务不挂在动态业务进程上。</p>
 */
public final class ApiConstants {

    public static final String NAME = "intelligent-analysis-server";

    public static final String PREFIX = RpcConstants.RPC_API_PREFIX + "/intelligent-analysis";

    public static final String VERSION = "1.0.0";

    private ApiConstants() {
    }
}
