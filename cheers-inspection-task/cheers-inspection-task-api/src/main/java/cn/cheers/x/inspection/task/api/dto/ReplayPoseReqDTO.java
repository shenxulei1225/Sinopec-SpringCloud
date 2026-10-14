package cn.cheers.x.inspection.task.api.dto;

import lombok.Data;

/**
 * 一次执行里飞机上报的一个位置点，给以后的三维回放用。
 * <p>x 是经度，y 是纬度，z 是高度。frameRef 为 wgs84 时还没换成站场坐标。
 * <p>不进过程列表。执行记录页现在仍按计划路线演示，不读这份点。
 */
@Data
public class ReplayPoseReqDTO {

    /** 已经对上这次执行时才填。没有时用设备编号按唯一任务来对。 */
    private Long executionRecordId;

    /** 网关序列号。报文里没有飞行编号时，用它找唯一的那次执行。 */
    private String deviceId;

    private Long t;

    private Double x;

    private Double y;

    private Double z;

    private Double yaw;

    /** 坐标系。大疆定时上报是 wgs84。 */
    private String frameRef;
}
