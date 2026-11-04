package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.module.dynamicbusiness.devicehistory.ParsedPointSample;

/**
 * 数据处理已经决定要记账时，叫告警目录。
 * 不判断阈值，也不订阅网关。
 * 缺类型、缺设备、缺位置时由告警目录拒绝，这里不补。
 */
public interface AlarmSignalGateway {

    void raise(long tenantId, long alarmTypeId, String alarmLevel, String name, String content, long deviceId,
               long locationId, String triggerValue, String thresholdValue, long ruleId, String pointCode);

    void eliminate(long tenantId, long deviceId, long ruleId);

    /** 读数质量不合格。类型用设备功能模块异常。没有位置则由调用方先拦住。 */
    void recordAbnormalQuality(ParsedPointSample sample);

    /** 这条告警被人搁置，并且时间还没到。 */
    boolean shelved(long tenantId, long deviceId, long signalKey);

    /** 先成立的那条规则在这台设备上还有未关闭的告警。高于、低于都算。 */
    boolean leadingOpen(long tenantId, long deviceId, long ruleId);
}
