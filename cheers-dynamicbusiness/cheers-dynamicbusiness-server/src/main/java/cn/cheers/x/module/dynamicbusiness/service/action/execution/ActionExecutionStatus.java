package cn.cheers.x.module.dynamicbusiness.service.action.execution;

/**
 * 一次动作调用的运行状态。
 */
public final class ActionExecutionStatus {

    public static final String PENDING = "PENDING";
    public static final String RUNNING = "RUNNING";
    public static final String SUCCEEDED = "SUCCEEDED";
    public static final String FAILED = "FAILED";

    private ActionExecutionStatus() {
    }
}
