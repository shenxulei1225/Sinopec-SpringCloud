package com.example.opcuaclient.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Tag实体类
 */
@Data
@TableName("tag")
public class Tag {
    
    /**
     * 主键ID，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 区域编码
     */
    @TableField("area_code")
    private String areaCode;
    
    /**
     * PLC分区
     */
    @TableField("plc_partition")
    private String plcPartition;
    
    /**
     * PLC编码
     */
    @TableField("plc_code")
    private String plcCode;
    
    /**
     * 标签名称
     */
    @TableField("tag_name")
    private String tagName;
    
    /**
     * 标签描述
     */
    @TableField("tag_description")
    private String tagDescription;
    
    /**
     * 读写方式（1：read 2: write）
     */
    @TableField("read_write_mode")
    private Integer readWriteMode;
    
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