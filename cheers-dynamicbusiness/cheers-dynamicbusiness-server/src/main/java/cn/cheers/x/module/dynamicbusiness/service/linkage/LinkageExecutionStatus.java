package cn.cheers.x.module.dynamicbusiness.service.linkage;

/**
 * 联动整体及单条绑定的执行状态。
 */
public final class LinkageExecutionStatus {

    public static final String PENDING = "PENDING";
    /** 选了人工确认后执行。事件已记下，还没提交动作。 */
    public static final String WAITING_CONFIRM = "WAITING_CONFIRM";
    public static final String RUNNING = "RUNNING";
    public static final String SUCCEEDED = "SUCCEEDED";
    public static final String PARTIAL_FAILED = "PARTIAL_FAILED";
    public static final String FAILED = "FAILED";

    private LinkageExecutionStatus() {
    }
}
