package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopMergeResult;

import java.util.List;

/**
 * SOP 模板命令：升格实例为新模板；读生效配置。
 *
 * <p><b>禁止</b>：升格时改原实例、自动改设备检查绑定。</p>
 */
public interface SopTemplateCommandService {

    /**
     * 读实例或模板的 merge 生效配置；缺口返回 gapCodes。
     *
     * @param sopId SOP 实体 id（模板或实例）
     */
    SopMergeResult getEffective(long sopId);

    /**
     * 将实例 merge 结果升格为新 SOP 模板行。
     *
     * @param instanceId  实例 id（is_template=false）
     * @param name        新模板名称
     * @param categoryIds 挂接的 SOP 分类 id 列表（至少一个）
     * @return 新模板实体 id
     */
    long promoteInstanceToTemplate(long instanceId, String name, List<Long> categoryIds);
}
