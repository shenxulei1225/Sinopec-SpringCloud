package cn.cheers.x.module.dynamicbusiness.devicehistory;

/**
 * 点位对照里的一个读点。
 * 点位编码给 Gateway 认。历史行只抄设备编号和字段编号。
 * 不管写点，也不存读数。
 */
public class GatewayPointRef {

    private final long tenantId;
    private final long deviceId;
    private final String pointCode;
    private final String bindingCode;
    private final String zoneCode;
    private final String tagName;
    /** 点位上的所属分区。告警要位置时用它，没有就不填。 */
    private final Long zoneId;

    public GatewayPointRef(long tenantId, long deviceId, String pointCode,
                           String bindingCode, String zoneCode, String tagName, Long zoneId) {
        this.tenantId = tenantId;
        this.deviceId = deviceId;
        this.pointCode = pointCode;
        this.bindingCode = bindingCode;
        this.zoneCode = zoneCode;
        this.tagName = tagName;
        this.zoneId = zoneId;
    }

    public long getTenantId() {
        return tenantId;
    }

    public long getDeviceId() {
        return deviceId;
    }

    /** 型号上采集点的字段编号。历史行抄这个。 */
    public String getPointCode() {
        return pointCode;
    }

    /** 分区编号加完整标签名。Gateway 认这一整段。 */
    public String getBindingCode() {
        return bindingCode;
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public String getTagName() {
        return tagName;
    }

    public Long getZoneId() {
        return zoneId;
    }
}
