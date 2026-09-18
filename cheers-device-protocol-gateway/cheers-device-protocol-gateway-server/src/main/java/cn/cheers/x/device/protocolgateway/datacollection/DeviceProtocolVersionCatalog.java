package cn.cheers.x.device.protocolgateway.datacollection;

import java.util.Optional;

/**
 * 设备台账上的协议版本。
 * <p>负责：按逻辑设备标识取出已登记数组的第一种。
 * <p>不负责：猜默认版本、改设备台账、发明厂商名。
 * <p>禁止：找不到设备或未登记还返回 robot-ws。
 */
public interface DeviceProtocolVersionCatalog {

    /**
     * @return 第一种协议版本；设备对不上、未登记、多台对上同一标识 → empty
     */
    Optional<String> firstVersion(String logicalDeviceId);
}
