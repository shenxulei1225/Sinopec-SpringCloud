package cn.cheers.x.module.platform.topology.enums;

import cn.cheers.x.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {

    ErrorCode TOPOLOGY_GRAPH_NOT_FOUND = new ErrorCode(1_004_055_000, "站场拓扑图不存在");
    ErrorCode TOPOLOGY_GRAPH_NOT_PUBLISHED = new ErrorCode(1_004_055_001, "站场拓扑图未发布");
    ErrorCode LEGACY_TOPOLOGY_DISABLED = new ErrorCode(1_004_055_010, "旧库拓扑导入未启用");
    ErrorCode LEGACY_TOPOLOGY_EMPTY = new ErrorCode(1_004_055_011, "旧库未找到该站场拓扑数据");
    ErrorCode LEGACY_TOPOLOGY_SOURCE_UNAVAILABLE = new ErrorCode(1_004_055_012, "旧库拓扑数据源不可用");

    ErrorCode NETWORK_NOT_FOUND = new ErrorCode(1_004_055_020, "路径网络不存在");
    ErrorCode NETWORK_DRAFT_INVALID = new ErrorCode(1_004_055_021, "路径网络草稿参数无效");
    ErrorCode NETWORK_VALIDATE_FAILED = new ErrorCode(1_004_055_022, "路径网络校验未通过，无法发布");
    ErrorCode GEOJSON_INVALID = new ErrorCode(1_004_055_030, "GeoJSON 格式无效");
}
