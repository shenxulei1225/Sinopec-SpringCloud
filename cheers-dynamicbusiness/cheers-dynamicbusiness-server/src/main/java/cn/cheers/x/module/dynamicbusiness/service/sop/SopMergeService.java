package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopMergeResult;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopStandardSnapshot;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopTreeOverride;

import java.util.Map;

/**
 * SOP 读时 merge：全平台唯一合并入口（后端）。
 * 与前端 {@code mergeSopEffectiveConfig} 契约对齐；不解析业务巡检点 / 停靠站。
 */
public interface SopMergeService {

    /**
     * 合并标准 SOP 与差量，并校验参数槽是否已填值（任务生效用）。
     *
     * @param standard      标准 SOP 快照（actionTree / paramsByNode）
     * @param treeOverride  动作树差量；null 或空 replaceTree 则用标准树
     * @param paramOverride 按 nodeKey 的参数差量
     */
    default SopMergeResult merge(SopStandardSnapshot standard,
                                 SopTreeOverride treeOverride,
                                 Map<String, Map<String, Object>> paramOverride) {
        return merge(standard, treeOverride, paramOverride, true);
    }

    /**
     * @param requireParamValues true：缺参返回 MISSING_PARAM（实例生效）；
     *                           false：只合并树与参数 map，不因空值报缺口（标准流程预览：值在 How 填）
     */
    SopMergeResult merge(SopStandardSnapshot standard,
                         SopTreeOverride treeOverride,
                         Map<String, Map<String, Object>> paramOverride,
                         boolean requireParamValues);
}
