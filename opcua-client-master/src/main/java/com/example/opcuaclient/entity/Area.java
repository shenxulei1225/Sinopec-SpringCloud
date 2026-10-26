package com.example.opcuaclient.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 区域实体类
 */
@Data
@TableName("area")
public class Area {
    
    /**
     * 主键ID，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 区域编号（自定义）
     */
    @TableField("area_code")
    private String areaCode;
    
    /**
     * 区域名称
     */
    @TableField("area_name")
    private String areaName;
    
    /**
     * GPS经度
     */
    @TableField("longitude")
    private Double longitude;
    
    /**
     * GPS纬度
     */
    @TableField("latitude")
    private Double latitude;
    
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