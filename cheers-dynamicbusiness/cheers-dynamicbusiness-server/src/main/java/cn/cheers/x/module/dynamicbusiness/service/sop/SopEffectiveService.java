package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopMergeResult;

/**
 * SOP 生效配置查询服务。
 *
 * <p><b>负责</b>：读取单条 SOP 的动作树与默认参数并返回 merge 生效视图。</p>
 * <p><b>不负责</b>：升格模板、实例派生、绑定写入。</p>
 */
public interface SopEffectiveService {

    /**
     * 读取 SOP 的 merge 生效配置；缺口返回 gapCodes。
     */
    SopMergeResult getEffective(long sopId);
}
