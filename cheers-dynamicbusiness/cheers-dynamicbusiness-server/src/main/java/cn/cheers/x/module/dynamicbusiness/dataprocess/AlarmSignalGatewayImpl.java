package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.devicehistory.ParsedPointSample;
import org.springframework.stereotype.Component;

/**
 * 把已经作出的决定写进数据管理里的告警目录。
 * 缺了类型、设备或位置就让写入拒绝，这里不补。
 */
@Component
public class AlarmSignalGatewayImpl implements AlarmSignalGateway {

    private final AlarmRecordWriter records;

    public AlarmSignalGatewayImpl(AlarmRecordWriter records) {
        this.records = records;
    }

    @Override
    public void raise(long tenantId, long alarmTypeId, String alarmLevel, String name, String content, long deviceId,
                      long locationId, String triggerValue, String thresholdValue, long ruleId, String pointCode) {
        records.open(tenantId, alarmTypeId, null, openCode(deviceId, ruleId), name, alarmLevel, content,
                deviceId, locationId, triggerValue, thresholdValue, pointCode, ruleId);
    }

    @Override
    public void eliminate(long tenantId, long deviceId, long ruleId) {
        if (!records.stillOpen(tenantId, openCode(deviceId, ruleId))) {
            return;
        }
        records.close(tenantId, openCode(deviceId, ruleId), ruleId);
    }

    @Override
    public void recordAbnormalQuality(ParsedPointSample sample) {
        if (sample.zoneId() == null || sample.zoneId() <= 0) {
            throw new ServiceException(400, "质量异常的读数没有位置，不记告警");
        }
        String content = sample.bindingCode() + " 质量不合格";
        records.open(sample.tenantId(), null, AlarmTypeCatalog.DEVICE_MALFUNCTION,
                qualityCode(sample.deviceId(), sample.pointCode()), content, AlarmTypeCatalog.LEVEL_ABNORMAL, content,
                sample.deviceId(), sample.zoneId(), Double.toString(sample.engineeringValue()), null, sample.pointCode(), null);
    }

    @Override
    public boolean shelved(long tenantId, long deviceId, long signalKey) {
        return records.shelved(tenantId, openCode(deviceId, signalKey));
    }

    @Override
    public boolean leadingOpen(long tenantId, long deviceId, long ruleId) {
        return records.stillOpen(tenantId, openCode(deviceId, ruleId))
                || records.stillOpen(tenantId, openCode(deviceId, -ruleId));
    }

    private static String openCode(long deviceId, long ruleId) {
        return "alarm-open-" + deviceId + "-" + ruleId;
    }

    private static String qualityCode(long deviceId, String pointCode) {
        String point = pointCode == null ? "point" : pointCode.replaceAll("[^A-Za-z0-9_-]", "_");
        if (point.isBlank()) {
            point = "point";
        }
        return "alarm-quality-" + deviceId + "-" + point;
    }
}
