package cn.cheers.x.module.dynamicbusiness.service.event;

/**
 * outbox 投递状态。
 */
public final class PlatformEventOutboxStatus {

    public static final String PENDING = "PENDING";
    public static final String PROCESSING = "PROCESSING";
    public static final String PROCESSED = "PROCESSED";

    private PlatformEventOutboxStatus() {
    }
}
