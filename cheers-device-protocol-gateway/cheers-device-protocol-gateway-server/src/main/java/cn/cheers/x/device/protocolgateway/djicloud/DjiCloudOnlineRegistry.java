package cn.cheers.x.device.protocolgateway.djicloud;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 上云在线会话：只记「拓扑应答已经发出」的网关。
 * <p>不写库，不代表航线或任务已经开始。MQTT 没开时列表为空。
 */
@Component
public class DjiCloudOnlineRegistry {

    private final ConcurrentMap<String, DjiCloudOnlineDevice> online = new ConcurrentHashMap<>();

    /**
     * 拓扑应答发出后记一笔。同一网关再次上报就换成最新的飞机列表。
     */
    public void markOnline(String gatewaySn, List<String> subDeviceSns) {
        online.put(gatewaySn, new DjiCloudOnlineDevice(gatewaySn, List.copyOf(subDeviceSns)));
    }

    public boolean isOnline(String gatewaySn) {
        return gatewaySn != null && online.containsKey(gatewaySn);
    }

    /**
     * 这台网关拓扑应答里挂着的飞机。还没应答过就是空列表，不编一架。
     */
    public List<String> aircraftSns(String gatewaySn) {
        if (gatewaySn == null) {
            return List.of();
        }
        DjiCloudOnlineDevice device = online.get(gatewaySn);
        if (device == null || device.subDeviceSns() == null) {
            return List.of();
        }
        return device.subDeviceSns();
    }

    /**
     * 当前在线的机场或遥控器。没有上云会话时是空列表。
     */
    public List<DjiCloudOnlineDevice> listOnline() {
        return List.copyOf(online.values());
    }

    /**
     * 这台序列号只挂在一个已应答的网关上时，返回那个网关。
     * 没有，或同时挂在多个网关上，返回 null，不挑一个。
     */
    public String singleGatewayOf(String deviceSn) {
        if (deviceSn == null || deviceSn.isBlank()) {
            return null;
        }
        List<String> hits = new ArrayList<>();
        for (DjiCloudOnlineDevice device : online.values()) {
            if (device == null || device.gatewaySn() == null) {
                continue;
            }
            boolean self = deviceSn.equals(device.gatewaySn());
            boolean child = device.subDeviceSns() != null && device.subDeviceSns().contains(deviceSn);
            if (self || child) {
                hits.add(device.gatewaySn());
            }
        }
        return hits.size() == 1 ? hits.get(0) : null;
    }
}
