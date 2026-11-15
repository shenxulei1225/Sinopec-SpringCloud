package cn.cheers.x.module.dynamicbusiness.api.security.dto;

/**
 * 识别到车写入进出账之后，要不要再抬杆。
 *
 * @param passId 刚写下的进出
 * @param lift 比对结果允许放行，并且这条车道的道闸有设备编号
 * @param deviceCode 道闸在设备账上的编号。不抬杆时为空
 * @param plate 车牌
 * @param laneId 车道
 * @param direction 进场或出场
 * @param enter 为真写进入时间。为假写离开时间
 */
public record VehiclePassNotifyResp(
        long passId,
        boolean lift,
        String deviceCode,
        String plate,
        long laneId,
        String direction,
        boolean enter) {
}
