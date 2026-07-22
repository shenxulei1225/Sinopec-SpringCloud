package cn.iocoder.yudao.module.emergency.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事件状态枚举
 */
@Getter
@AllArgsConstructor
public enum EventStatus {

    PENDING("pending", "待响应"),
    WARNING("warning", "预警"),
    RESPONDING("responding", "响应中"),
    PROCESSING("processing", "处理中"),
    MONITORING("monitoring", "监控中"),
    CLOSED("closed", "已关闭"),
    CANCELLED("cancelled", "已取消");

    private final String code;
    private final String name;
}

