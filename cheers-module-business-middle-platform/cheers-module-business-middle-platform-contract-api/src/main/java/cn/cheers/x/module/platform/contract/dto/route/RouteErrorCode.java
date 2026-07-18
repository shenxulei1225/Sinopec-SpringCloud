package cn.cheers.x.module.platform.contract.dto.route;

/**
 * 路径规划稳定错误码（字符串常量）— 供 Routing 服务与消费方对齐语义。
 */
public final class RouteErrorCode {

    private RouteErrorCode() {
    }

    public static final String ROUTE_PROFILE_NETWORK_FORBIDDEN = "ROUTE_PROFILE_NETWORK_FORBIDDEN";
    public static final String ROUTE_PROFILE_PORTAL_FORBIDDEN = "ROUTE_PROFILE_PORTAL_FORBIDDEN";
    public static final String ROUTE_UNREACHABLE = "ROUTE_UNREACHABLE";
    public static final String ROUTE_CROSS_ZONE_NO_DOOR = "ROUTE_CROSS_ZONE_NO_DOOR";
    public static final String ROUTE_TOO_MANY_STOPS = "ROUTE_TOO_MANY_STOPS";
    public static final String NETWORK_NOT_FOUND = "NETWORK_NOT_FOUND";
}
