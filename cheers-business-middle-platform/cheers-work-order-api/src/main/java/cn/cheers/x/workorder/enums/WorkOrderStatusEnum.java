package cn.cheers.x.workorder.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 工单状态
 */
@Getter
@AllArgsConstructor
public enum WorkOrderStatusEnum {

    DRAFT("DRAFT", "草稿"),
    DISPATCHED("DISPATCHED", "已派发"),
    IN_PROGRESS("IN_PROGRESS", "执行中"),
    COMPLETED("COMPLETED", "已完成"),
    CANCELLED("CANCELLED", "已取消");

    private final String status;
    private final String name;

}
