package com.example.opcuaclient.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("plc")
public class Plc {
    
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 分区编码
     */
    @TableField("area_code")
    private String areaCode;
    
    /**
     * 对应的分区
     */
    @TableField("plc_partition")
    private String plcPartition;
    
    /**
     * PLC定义的topic
     */
    @TableField("plc_code")
    private String plcCode;
    
    /**
     * PLC对应的IP地址
     */
    @TableField("ip")
    private String ip;
    
    /**
     * 录入时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 最近修改时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}