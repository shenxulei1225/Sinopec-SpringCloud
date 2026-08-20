package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmFiveWOrchestrationBundleRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmFiveWOrchestrationBundleSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.enums.datamgmt.FiveWRecipeIdEnum;
import org.springframework.util.StringUtils;

/**
 * 五维编排场景配方 → bundle 保存请求（写路径 bootstrap / platform seed 复用）。
 * <p>
 * 只按 {@link FiveWRecipeIdEnum} + {@link FiveWRecipeParams} 生成，不按 registryCode 分支。
 * 开哪些栏不在这里写，认数据 Tab 布局。
 */
public final class FiveWRecipeTemplate {

    private FiveWRecipeTemplate() {
    }

    public static DmFiveWOrchestrationBundleSaveReqVO toSaveReq(
            FiveWRecipeIdEnum recipeId,
            FiveWRecipeParams params) {
        if (recipeId == null) {
            throw new ServiceException(500, "五维编排配方 id 不能为空");
        }
        if (params == null || !StringUtils.hasText(params.getRegistryCode())) {
            throw new ServiceException(500, "五维编排配方参数 registryCode 不能为空");
        }
        return switch (recipeId) {
            case LEDGER_3COL -> ledgerThreeCol(params);
            case CATEGORY_AS_ENTITY -> categoryAsEntity(params);
            case FOREIGN_MODEL, CONFIG_OBJECT_3COL, PARTITION_ENTITY ->
                    throw new ServiceException(500, "配方 " + recipeId.getRecipeId()
                            + " 仅用于 platform seed，建类型 bootstrap 不自动应用");
        };
    }

    private static DmFiveWOrchestrationBundleSaveReqVO ledgerThreeCol(FiveWRecipeParams params) {
        DmFiveWOrchestrationBundleSaveReqVO req = baseReq(params);
        req.setObjectPickFrom("LIST_ROW");
        req.setWhatSlot(noneWhat());
        req.setHowSlot(noneHow());
        return req;
    }

    private static DmFiveWOrchestrationBundleSaveReqVO categoryAsEntity(FiveWRecipeParams params) {
        DmFiveWOrchestrationBundleSaveReqVO req = baseReq(params);
        req.setObjectPickFrom("CATEGORY_NODE");
        req.setWhatSlot(viewDetailWhat());
        req.setHowSlot(noneHow());
        return req;
    }

    private static DmFiveWOrchestrationBundleSaveReqVO baseReq(FiveWRecipeParams params) {
        String code = params.getRegistryCode().trim();
        String storage = StringUtils.hasText(params.getStorageEntityTypeCode())
                ? params.getStorageEntityTypeCode().trim() : code;
        DmFiveWOrchestrationBundleSaveReqVO req = new DmFiveWOrchestrationBundleSaveReqVO();
        req.setRegistryCode(code);
        req.setStorageEntityTypeCode(storage);
        DmFiveWOrchestrationBundleRespVO.Semantic semantic = new DmFiveWOrchestrationBundleRespVO.Semantic();
        semantic.setEnabled(true);
        req.setSemantic(semantic);
        return req;
    }

    private static DmFiveWOrchestrationBundleRespVO.WhatSlot viewDetailWhat() {
        DmFiveWOrchestrationBundleRespVO.WhatSlot what = new DmFiveWOrchestrationBundleRespVO.WhatSlot();
        what.setMode("VIEW_DETAIL");
        what.setBindLayer("ENTITY");
        return what;
    }

    private static DmFiveWOrchestrationBundleRespVO.WhatSlot noneWhat() {
        DmFiveWOrchestrationBundleRespVO.WhatSlot what = new DmFiveWOrchestrationBundleRespVO.WhatSlot();
        what.setMode("NONE");
        return what;
    }

    private static DmFiveWOrchestrationBundleRespVO.HowSlot noneHow() {
        DmFiveWOrchestrationBundleRespVO.HowSlot how = new DmFiveWOrchestrationBundleRespVO.HowSlot();
        how.setMode("NONE");
        return how;
    }
}
