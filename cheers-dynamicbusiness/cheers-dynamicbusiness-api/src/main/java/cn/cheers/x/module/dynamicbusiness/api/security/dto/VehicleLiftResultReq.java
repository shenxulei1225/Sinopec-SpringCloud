package cn.cheers.x.module.dynamicbusiness.api.security.dto;

/**
 * 抬杆指令发出之后，回写这一条进出。
 *
 * @param passId 进出
 * @param sent 厂商已经接受这条抬杆
 * @param message 没发出时的原因
 * @param enter 为真写进入时间
 */
public record VehicleLiftResultReq(long passId, boolean sent, String message, boolean enter) {
}
