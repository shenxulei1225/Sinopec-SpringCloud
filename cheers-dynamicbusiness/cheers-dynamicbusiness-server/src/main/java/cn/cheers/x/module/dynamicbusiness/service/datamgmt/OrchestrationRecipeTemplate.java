package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmCatalogOrchestrationBundleRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmCatalogOrchestrationBundleSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.enums.datamgmt.OrchestrationRecipeIdEnum;
import org.springframework.util.StringUtils;

/**
 * 创建数据目录时写入编排头：只写点树还是点列表行。
 * <p>
 * 详情跟谁认布局里的详情栏 + 关系图连线。禁止再写 What/How 槽。
 */
public final class OrchestrationRecipeTemplate {

    private OrchestrationRecipeTemplate() {
    }

    public static DmCatalogOrchestrationBundleSaveReqVO toSaveReq(
            OrchestrationRecipeIdEnum recipeId,
            OrchestrationRecipeParams params) {
        if (recipeId == null) {
            throw new ServiceException(500, "编排配方 id 不能为空");
        }
        if (params == null || !StringUtils.hasText(params.getRegistryCode())) {
            throw new ServiceException(500, "编排配方参数 registryCode 不能为空");
        }
        return switch (recipeId) {
            case LEDGER_3COL -> ledgerThreeCol(params);
            case CATEGORY_AS_ENTITY -> categoryAsEntity(params);
            case FOREIGN_MODEL, CONFIG_OBJECT_3COL, PARTITION_ENTITY ->
                    throw new ServiceException(500, "配方 " + recipeId.getRecipeId()
                            + " 仅用于 platform seed，建类型 bootstrap 不自动应用");
        };
    }

    private static DmCatalogOrchestrationBundleSaveReqVO ledgerThreeCol(OrchestrationRecipeParams params) {
        DmCatalogOrchestrationBundleSaveReqVO req = baseReq(params);
        req.setSelectionSource("LIST_ROW");
        return req;
    }

    private static DmCatalogOrchestrationBundleSaveReqVO categoryAsEntity(OrchestrationRecipeParams params) {
        DmCatalogOrchestrationBundleSaveReqVO req = baseReq(params);
        req.setSelectionSource("CATEGORY_NODE");
        return req;
    }

    private static DmCatalogOrchestrationBundleSaveReqVO baseReq(OrchestrationRecipeParams params) {
        String code = params.getRegistryCode().trim();
        String storage = StringUtils.hasText(params.getStorageEntityTypeCode())
                ? params.getStorageEntityTypeCode().trim() : code;
        DmCatalogOrchestrationBundleSaveReqVO req = new DmCatalogOrchestrationBundleSaveReqVO();
        req.setRegistryCode(code);
        req.setStorageEntityTypeCode(storage);
        DmCatalogOrchestrationBundleRespVO.Semantic semantic = new DmCatalogOrchestrationBundleRespVO.Semantic();
        semantic.setEnabled(true);
        req.setSemantic(semantic);
        return req;
    }
}
