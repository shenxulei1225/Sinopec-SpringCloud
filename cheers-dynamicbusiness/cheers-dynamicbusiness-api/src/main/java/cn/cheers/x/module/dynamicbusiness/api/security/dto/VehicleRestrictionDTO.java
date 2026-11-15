package cn.cheers.x.module.dynamicbusiness.api.security.dto;

import java.util.List;

/**
 * 下发车辆进出限制时读出的一辆车。
 * <p>负责：车牌，以及车辆登记上已经写下的允许或禁止、有效期、车道。公司车是在用或停用。
 * <p>不负责：进出记录。读到这份不表示车已经进场或离场。
 * <p>禁止：在这里另存一份名单。
 *
 * @param plate 车牌
 * @param kind 个人车辆、公司车辆或外来车
 * @param entry 个人车是允许进入或禁止进入。公司车是在用或停用。外来车是已同意、已拒绝或待同意
 * @param validFrom 有效期起。公司车是还没结束的调度开始。没有则空
 * @param validTo 有效期止。公司车是还没结束的调度结束。没有则空
 * @param laneIds 允许走的车道。空表表示车道不限。公司车不限车道
 */
public record VehicleRestrictionDTO(
        String plate,
        String kind,
        String entry,
        String validFrom,
        String validTo,
        List<Long> laneIds) {
}
