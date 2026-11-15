package cn.cheers.x.module.dynamicbusiness.api.security;

/**
 * 车辆进出经协议网关收成的两条标准事件。
 * <p>负责：报文种类用这两个编码。一家说明书的转发到主题对准其中一条。
 * <p>不负责：抬杆、下发限制。那两条是指令，不是这两条事件。
 * <p>禁止：把人员门禁的事件编码写到这里。
 */
public final class VehiclePassEventCodes {

    /** 识别到一辆车，平台还没决定放行。平台抬杆的车道用这一条。 */
    public static final String SEEN = "vehicle_pass_seen";

    /** 厂商已经记下一次进入或离开。厂商自己抬杆的车道用这一条。 */
    public static final String DONE = "vehicle_pass_done";

    private VehiclePassEventCodes() {
    }

    public static boolean isVehiclePass(String messageKind) {
        return SEEN.equals(messageKind) || DONE.equals(messageKind);
    }
}
