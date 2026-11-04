package cn.cheers.x.module.dynamicbusiness.devicehistory;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DevicePointHistoryRulesTest {

    @Test
    void acceptsNormalAndAbnormalQuality() {
        DevicePointHistoryRules.requireQuality("正常");
        DevicePointHistoryRules.requireQuality("异常");
    }

    @Test
    void rejectsAnyOtherQuality() {
        ServiceException error = assertThrows(ServiceException.class,
                () -> DevicePointHistoryRules.requireQuality("坏点"));
        assertEquals("质量只能是正常或异常", error.getMessage());
    }

    @Test
    void rejectsMissingEngineeringValue() {
        ServiceException error = assertThrows(ServiceException.class,
                () -> DevicePointHistoryRules.requireEngineeringValue(null));
        assertEquals("这条采集没有工程值", error.getMessage());
    }

    @Test
    void rejectsRangeLongerThanThirtyOneDays() {
        Instant from = Instant.parse("2026-10-01T00:00:00Z");
        Instant to = Instant.parse("2026-11-02T00:00:00Z");
        ServiceException error = assertThrows(ServiceException.class,
                () -> DevicePointHistoryRules.requireRange(from, to));
        assertEquals("一次最多看 31 天", error.getMessage());
    }
}
