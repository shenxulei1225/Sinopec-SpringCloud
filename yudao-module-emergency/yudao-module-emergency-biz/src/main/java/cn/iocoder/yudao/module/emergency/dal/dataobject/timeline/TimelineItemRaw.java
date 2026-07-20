package cn.iocoder.yudao.module.emergency.dal.dataobject.timeline;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Timeline 聚合原始结果
 */
@Data
public class TimelineItemRaw {
    private String type;
    private LocalDateTime createTime;
    private String data; // 改为String类型，存储JSON字符串
}

















