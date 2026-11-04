package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.module.dynamicbusiness.service.strategy.PointProcessingRule;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据处理读取的规则账。
 * 版本变了才重新读。不在这里比较读数。
 * 告警策略按设备类型匹配。没写设备类型时，设备关联空着就不判断。
 */
public interface PointRuleSource {

    long version();

    List<PointProcessingRule> load();

    /** 策略编号 → 已经挂上的设备编号。策略写了设备类型时不用这份名单。 */
    Map<Long, Set<Long>> boundDevices();

    /** 设备编号 → 这台设备的型号。告警策略按型号匹配。 */
    default Map<Long, Long> deviceModels() {
        return Map.of();
    }
}
