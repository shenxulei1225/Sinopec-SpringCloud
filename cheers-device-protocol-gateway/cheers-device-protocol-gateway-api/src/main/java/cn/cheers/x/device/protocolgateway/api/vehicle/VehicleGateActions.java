package cn.cheers.x.device.protocolgateway.api.vehicle;

/**
 * 车辆进出下发用的动作编码。动作库里的编码要和这里一致，再在指令协议对接里对照到这一家的指令。
 * <p>不负责：厂商地址。地址写在那条指令上。
 */
public final class VehicleGateActions {

    /** 平台抬杆。 */
    public static final String LIFT = "抬杆";

    /** 把车辆登记上的限制发给厂商。 */
    public static final String PUSH = "下发车辆限制";

    private VehicleGateActions() {
    }
}
