package cn.cheers.x.device.protocolgateway.api.dto;

/**
 * 巡检待执行 standard_snapshot 中与协议下行相关的键（与 inspection-task 快照类对齐）。
 */
public final class PatrolOpenRunSnapshotKeys {

    public static final String DISPATCH_DOWNLINK_PIECES = "dispatchDownlinkPieces";
    public static final String WAYLINE_FILE_ID = "waylineFileId";
    public static final String WAYLINE_FINGERPRINT = "waylineFingerprint";
    public static final String TASK_RECORD_INGEST_URL = "taskRecordIngestUrl";
    public static final String TASK_RECORD_STREAM_NAME = "taskRecordStreamName";

    private PatrolOpenRunSnapshotKeys() {
    }
}
