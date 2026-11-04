package cn.cheers.x.module.dynamicbusiness.devicehistory;

import java.util.List;

/**
 * 网关把这一秒解析后的样本一次交出来。
 * 不负责写历史、读策略或生成告警。
 */
public interface PointSampleBatchReporter {

    void accept(List<ParsedPointSample> samples);
}
