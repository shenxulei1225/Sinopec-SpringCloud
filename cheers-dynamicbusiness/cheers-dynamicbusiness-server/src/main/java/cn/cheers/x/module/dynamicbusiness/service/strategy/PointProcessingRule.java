package cn.cheers.x.module.dynamicbusiness.service.strategy;

/**
 * 数据处理能直接用的一条点位规则。
 * 同一字段编号可以有多条。告警策略看设备类型：这种型号的设备都套这一条。
 * 不负责比较读数，也不写告警。
 */
public final class PointProcessingRule {

    public static final String THRESHOLD = "THRESHOLD";
    public static final String ALARM_FLAG = "ALARM_FLAG";
    public static final String STATUS = "STATUS";

    private final long id;
    private final String pointFieldCode;
    private final String signalKind;
    private final String compareOp;
    private final Double threshold;
    private final Long alarmTypeId;
    private final String alarmLevel;
    private final Long fixedDeviceId;
    private final Long fixedLocationId;
    /** 策略针对的设备型号。空着表示这条告警策略不判断。 */
    private final Long deviceModelId;
    /** 恢复线。空着时越线之后不自动消警。 */
    private final Double recovery;
    /** 同一条策略的高于、低于各用一个编号记账。 */
    private final long signalKey;
    /** 告警规则在目录里的名称。记告警时写进记录标题。 */
    private final String name;
    /** 采集点给人看的名称。说明里用这个，不用点位编号。 */
    private final String pointName;

    public PointProcessingRule(long id, String pointFieldCode, String signalKind, String compareOp, Double threshold,
                               Long alarmTypeId, String alarmLevel, Long fixedDeviceId, Long fixedLocationId) {
        this(id, pointFieldCode, signalKind, compareOp, threshold, alarmTypeId, alarmLevel,
                fixedDeviceId, fixedLocationId, null);
    }

    public PointProcessingRule(long id, String pointFieldCode, String signalKind, String compareOp, Double threshold,
                               Long alarmTypeId, String alarmLevel, Long fixedDeviceId, Long fixedLocationId,
                               Long deviceModelId) {
        this(id, pointFieldCode, signalKind, compareOp, threshold, alarmTypeId, alarmLevel,
                fixedDeviceId, fixedLocationId, deviceModelId, null, id);
    }

    public PointProcessingRule(long id, String pointFieldCode, String signalKind, String compareOp, Double threshold,
                               Long alarmTypeId, String alarmLevel, Long fixedDeviceId, Long fixedLocationId,
                               Long deviceModelId, Double recovery, long signalKey) {
        this(id, pointFieldCode, signalKind, compareOp, threshold, alarmTypeId, alarmLevel,
                fixedDeviceId, fixedLocationId, deviceModelId, recovery, signalKey, null, null);
    }

    public PointProcessingRule(long id, String pointFieldCode, String signalKind, String compareOp, Double threshold,
                               Long alarmTypeId, String alarmLevel, Long fixedDeviceId, Long fixedLocationId,
                               Long deviceModelId, Double recovery, long signalKey, String name, String pointName) {
        this.id = id;
        this.pointFieldCode = pointFieldCode;
        this.signalKind = signalKind;
        this.compareOp = compareOp;
        this.threshold = threshold;
        this.alarmTypeId = alarmTypeId;
        this.alarmLevel = alarmLevel;
        this.fixedDeviceId = fixedDeviceId;
        this.fixedLocationId = fixedLocationId;
        this.deviceModelId = deviceModelId;
        this.recovery = recovery;
        this.signalKey = signalKey;
        this.name = name;
        this.pointName = pointName;
    }

    public long getId() {
        return id;
    }

    public String getPointFieldCode() {
        return pointFieldCode;
    }

    public String getSignalKind() {
        return signalKind;
    }

    public String getCompareOp() {
        return compareOp;
    }

    public Double getThreshold() {
        return threshold;
    }

    public Long getAlarmTypeId() {
        return alarmTypeId;
    }

    public String getAlarmLevel() {
        return alarmLevel;
    }

    public Long getFixedDeviceId() {
        return fixedDeviceId;
    }

    public Long getFixedLocationId() {
        return fixedLocationId;
    }

    /** 这种型号的设备都套这条策略。空着时告警判断不套任何设备。 */
    public Long getDeviceModelId() {
        return deviceModelId;
    }

    /** 读数回到这条线内才消警。空着则不自动消警。 */
    public Double getRecovery() {
        return recovery;
    }

    /** 高于、低于分开记账时用这个编号。 */
    public long getSignalKey() {
        return signalKey;
    }

    /** 告警记录标题用这个名称。空着时判断侧不记告警。 */
    public String getName() {
        return name;
    }

    /** 告警说明里的采集点名称。 */
    public String getPointName() {
        return pointName;
    }
}
