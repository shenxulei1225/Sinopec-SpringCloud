package cn.iocoder.yudao.module.sinopec.device.dal.dataobject;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 设备实时轨迹 DO。
 */
@Data
public class EquipmentRealTimePathDO {

    private Long id;
    private Integer siteId;
    private String taskId;
    private String deviceSerialNo;
    private String longitude;
    private String latitude;
    private LocalDateTime reportTime;
    private LocalDateTime createTime;

}
