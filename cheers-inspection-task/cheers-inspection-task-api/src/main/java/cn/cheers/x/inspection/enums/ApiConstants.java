package cn.cheers.x.inspection.enums;

import cn.cheers.x.framework.common.enums.RpcConstants;

/**
 * inspection-task RPC 常量（与 Nacos 服务名 inspection-task-server 一致）。
 */
public interface ApiConstants {

    String NAME = "inspection-task-server";

    String PREFIX = RpcConstants.RPC_API_PREFIX + "/inspection-task";
}
