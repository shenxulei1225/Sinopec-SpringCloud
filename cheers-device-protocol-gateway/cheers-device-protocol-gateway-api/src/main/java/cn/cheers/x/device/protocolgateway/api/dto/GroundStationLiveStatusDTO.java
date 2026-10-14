package cn.cheers.x.device.protocolgateway.api.dto;

/**
 * 东古地面站最近一次状态里报上的电量。
 * <p>没有报到时 electricity 为空。这不是剩余可飞秒数。
 */
public record GroundStationLiveStatusDTO(String electricity, Long receivedAtEpochMs) {
}
