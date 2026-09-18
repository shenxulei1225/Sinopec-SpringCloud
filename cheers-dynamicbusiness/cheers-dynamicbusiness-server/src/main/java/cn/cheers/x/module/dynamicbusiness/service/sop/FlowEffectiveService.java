package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.service.sop.dto.FlowMergeResult;

/**
 * 标准流程生效配置查询服务。
 *
 * <p><b>负责</b>：读取单条流程的动作树与默认参数并返回 merge 生效视图。</p>
 * <p><b>不负责</b>：升格模板、实例派生、绑定写入。</p>
 */
public interface FlowEffectiveService {

    /**
     * 读取流程的 merge 生效配置；缺口返回 gapCodes。
     */
    FlowMergeResult getEffective(long flowId);
}
