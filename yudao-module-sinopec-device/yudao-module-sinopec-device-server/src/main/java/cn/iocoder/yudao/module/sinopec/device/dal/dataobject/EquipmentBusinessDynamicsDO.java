package cn.iocoder.yudao.module.sinopec.device.dal.dataobject;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 设备业务动态 DO。
 */
@Data
public class EquipmentBusinessDynamicsDO {

    private Long id;
    private Integer siteId;
    private String equipmentSerialNo;
    private String equipmentName;
    private Integer equipmentType;
    private String model;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
