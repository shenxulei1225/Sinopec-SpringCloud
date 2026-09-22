package cn.cheers.x.module.platform.contract.enums;

/**
 * 排期候选占窗段类型（L4 {@code platform_resource_reservation.candidate_type}）。
 */
public enum CandidateType {

    /** 一次任务执行占段 */
    TASK_EXECUTION,

    /** 充电占设备 */
    BATTERY_CHARGING,

    /** 任务间隔占段 */
    INTER_TASK_GAP
}
