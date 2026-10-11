package cn.cheers.x.device.protocolgateway.datacollection;

/**
 * 巡检设备台账上已经登记的默认协议。
 * <p>负责：按当前连上的逻辑标识找出这台设备，读「设备默认使用的协议」。
 * <p>不负责：改台账、猜一份协议。
 * <p>禁止：没登记还返回机器人或无人机协议。
 */
public interface DeviceRegistrationLookup {

    /**
     * 对上了且登记了默认协议时，protocol 有值、reason 为空。
     * 对不上、多台对上同一标识、或默认协议空着时，protocol 为空，reason 写给页面。
     */
    Registration registrationOf(String logicalDeviceId);

    /**
     * @param protocol 设备默认使用的协议，如 uav-ws
     * @param reason   不能下发时给页面的原因
     */
    record Registration(String protocol, String reason) {

        public boolean ok() {
            return protocol != null && !protocol.isBlank();
        }
    }
}
