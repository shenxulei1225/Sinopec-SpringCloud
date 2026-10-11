package cn.cheers.x.module.dynamicbusiness.service.analysis.result;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.analysis.AnalysisJobDO;

import java.util.Map;

/**
 * 分析结果交付边界。
 *
 * <p>智能分析内核只保存标准结果。具体业务通过明确的引用种类接收结果，禁止按动作名称判断消费者。</p>
 */
public interface AnalysisJobResultSink {

    boolean supports(String businessRefType);

    void deliver(AnalysisJobDO job, Map<String, Object> normalizedResult);
}
