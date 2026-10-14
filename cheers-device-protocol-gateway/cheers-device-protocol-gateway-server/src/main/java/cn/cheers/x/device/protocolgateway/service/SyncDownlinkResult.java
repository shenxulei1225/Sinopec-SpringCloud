package cn.cheers.x.device.protocolgateway.service;

/**
 * 一次同步下发的结果：写出、是否等到同号答卷、答卷业务是否成功。
 * <p>写出成功还不算这次下发成功；accepted 才表示对端用同号答卷收下（code=200）。
 */
public record SyncDownlinkResult(
        boolean written,
        boolean replied,
        boolean businessSuccess,
        String replyJson,
        String failureReason
) {

    public boolean accepted() {
        return written && replied && businessSuccess;
    }

    public static SyncDownlinkResult writeFailed(String reason) {
        return new SyncDownlinkResult(false, false, false, null, reason);
    }

    public static SyncDownlinkResult timeout(String reason) {
        return new SyncDownlinkResult(true, false, false, null, reason);
    }

    public static SyncDownlinkResult rejected(String replyJson, String reason) {
        return new SyncDownlinkResult(true, true, false, replyJson, reason);
    }

    public static SyncDownlinkResult accepted(String replyJson) {
        return new SyncDownlinkResult(true, true, true, replyJson, null);
    }

    /**
     * 写出已经完成。没记进任务时，把原因接在失败说明后面。下发是否成功不因此改掉。
     */
    public SyncDownlinkResult withTranscript(String reason) {
        if (reason == null || reason.isBlank()) {
            return this;
        }
        String combined = failureReason == null || failureReason.isBlank()
                ? reason
                : failureReason + "。" + reason;
        return new SyncDownlinkResult(written, replied, businessSuccess, replyJson, combined);
    }
}
