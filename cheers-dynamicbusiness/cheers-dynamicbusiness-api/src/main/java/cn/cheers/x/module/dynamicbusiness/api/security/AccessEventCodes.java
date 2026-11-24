package cn.cheers.x.module.dynamicbusiness.api.security;

/**
 * 门禁经协议网关收成的两条行业标准事件。
 * <p>负责：报文种类用这两个编码。一家说明书的转发到主题对准其中一句。
 * <p>不管什么：宇视现有协议书。那本书没有开门和读门开闭，网关先不接收这两句。
 * <p>禁止：在这里写厂商登录地址。把车辆进出的事件编码写到这里。没有标准协议就收一条开门或关门。
 */
public final class AccessEventCodes {

    /** 这扇门被打开。对象编码和事件主题里「开门」那一行相同。 */
    public static final String OPENED = "access-door-open";

    /** 这扇门被关上。对象编码和事件主题里「关门」那一行相同。 */
    public static final String CLOSED = "access-door-close";

    private AccessEventCodes() {
    }

    public static boolean isAccessEvent(String messageKind) {
        return OPENED.equals(messageKind) || CLOSED.equals(messageKind);
    }
}
