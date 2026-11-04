package cn.cheers.x.module.dynamicbusiness.service.event;

/**
 * 平台内部处理器识别的事件契约种类。
 *
 * <p>这些值标识平台协议，不是用户创建的具体事件名称。</p>
 */
public final class PlatformEventKinds {

    public static final String ALARM_LIFECYCLE = "ALARM_LIFECYCLE";
    public static final String ACTION_EXECUTION_STATUS_CHANGED = "ACTION_EXECUTION_STATUS_CHANGED";

    private PlatformEventKinds() {
    }
}
