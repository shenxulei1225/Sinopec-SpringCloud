package cn.cheers.x.module.dynamicbusiness.devicehistory;

import cn.cheers.x.framework.common.exception.ServiceException;

import java.time.Duration;
import java.time.Instant;

/**
 * 设备点历史的写入和查询界限。
 * 不管采样存在哪、也不连库。
 * 禁止把没配过的点、没有工程值、或看不清的质量写成一条正常历史。
 */
public final class DevicePointHistoryRules {

    /** 正常：设备详情可以把这条画进曲线。 */
    public static final String QUALITY_NORMAL = "正常";

    /** 异常：行要留下，但不能当成曲线上的正常点。 */
    public static final String QUALITY_ABNORMAL = "异常";

    /** 一次查询最长跨度。再长就让人缩小时间，避免一次拉出整库。 */
    public static final Duration MAX_RANGE = Duration.ofDays(31);

    private static final int BAD_REQUEST = 400;

    private DevicePointHistoryRules() {
    }

    /**
     * 质量只有两种。其它写法直接拒绝。
     */
    public static void requireQuality(String quality) {
        if (!QUALITY_NORMAL.equals(quality) && !QUALITY_ABNORMAL.equals(quality)) {
            throw new ServiceException(BAD_REQUEST, "质量只能是正常或异常");
        }
    }

    /**
     * 没有工程值就不记。不把空值写成 0。
     */
    public static void requireEngineeringValue(Double engineeringValue) {
        if (engineeringValue == null || engineeringValue.isNaN() || engineeringValue.isInfinite()) {
            throw new ServiceException(BAD_REQUEST, "这条采集没有工程值");
        }
    }

    /**
     * 设备详情一次只看一段已经结束的时间。超过 31 天就拒绝。
     */
    public static void requireRange(Instant from, Instant to) {
        if (from == null || to == null || !from.isBefore(to)) {
            throw new ServiceException(BAD_REQUEST, "请选择一段开始早于结束的时间");
        }
        if (Duration.between(from, to).compareTo(MAX_RANGE) > 0) {
            throw new ServiceException(BAD_REQUEST, "一次最多看 31 天");
        }
    }
}
