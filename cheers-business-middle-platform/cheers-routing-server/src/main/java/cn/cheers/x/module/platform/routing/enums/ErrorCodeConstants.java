package cn.cheers.x.module.platform.routing.enums;

import cn.cheers.x.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {

    ErrorCode ROUTE_UNREACHABLE = new ErrorCode(1_004_056_000, "路径不可达");
    ErrorCode ROUTE_PROFILE_NETWORK_FORBIDDEN = new ErrorCode(1_004_056_001, "机动剖面不允许使用该路径网络");
    ErrorCode ROUTE_PROFILE_PORTAL_FORBIDDEN = new ErrorCode(1_004_056_002, "机动剖面不允许使用 Portal 联程");
    ErrorCode ROUTE_NETWORK_NOT_FOUND = new ErrorCode(1_004_056_003, "路径网络不存在");
    ErrorCode ROUTE_STRATEGY_UNSUPPORTED = new ErrorCode(1_004_056_004, "路径规划策略暂不支持");
    ErrorCode ROUTE_MOBILITY_PROFILE_NOT_FOUND = new ErrorCode(1_004_056_005, "机动剖面不存在");
    ErrorCode ROUTE_TOO_MANY_STOPS = new ErrorCode(1_004_056_006,
            "停靠点过多（超过 200 个），请拆分后再规划");
    ErrorCode ROUTE_REQUEST_INVALID = new ErrorCode(1_004_056_007, "路径请求缺少 networkRef 或 stopIds");
}
