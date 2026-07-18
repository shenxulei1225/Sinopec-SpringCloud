package cn.cheers.x.sinopec.device.dal.dataobject;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 设备详情 DO。
 */
@Data
public class DeviceDetailsDO {

    private Long id;
    private Integer siteId;
    private String equipmentSerialNo;
    private Integer equipmentType;
    private Integer stateCode;
    private String stateDesc;
    private String taskId;
    private String taskName;
    private String power;
    private String longitude;
    private String latitude;
    private String ipAddress;
    private LocalDateTime reportTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
