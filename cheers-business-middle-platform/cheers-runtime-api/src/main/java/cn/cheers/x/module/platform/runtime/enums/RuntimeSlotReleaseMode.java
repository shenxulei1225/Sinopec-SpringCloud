package cn.cheers.x.module.platform.runtime.enums;

/**
 * 运行时释放未执行占用模式。
 */
public enum RuntimeSlotReleaseMode {

    /** 让路式暂停：取消未执行计划点，释放时间轴占用 */
    YIELD_PAUSE,

    /** 挂起式暂停：标记锁定，不释放占用 */
    HOLD_PAUSE,

    /** 中止：同让路式释放未执行占用 */
    ABORT
}
