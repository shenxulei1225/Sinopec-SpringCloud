package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopMergeResult;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopStepOverride;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopTemplateSnapshot;

import java.util.Map;

/**
 * SOP 读时 merge：全平台唯一合并入口（后端）。
 * 与前端 {@code mergeSopEffectiveConfig} 契约对齐；不解析业务巡检点 / 停靠站。
 */
public interface SopMergeService {

    /**
     * 合并模板与实例差量。
     *
     * @param template      模板快照（defaultSteps / defaultParams）
     * @param stepOverride  步骤差量；null 或空 replaceSteps 则用模板步骤
     * @param paramOverride 参数差量；浅覆盖模板 defaultParams
     */
    SopMergeResult merge(SopTemplateSnapshot template,
                         SopStepOverride stepOverride,
                         Map<String, Object> paramOverride);
}
