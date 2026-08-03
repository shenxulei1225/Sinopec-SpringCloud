package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmFiveWOrchestrationBundleRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmFiveWOrchestrationBundleSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.enums.datamgmt.DmDataTabLayoutKindEnum;
import cn.cheers.x.module.dynamicbusiness.enums.datamgmt.FiveWRecipeIdEnum;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 五维编排场景配方 → bundle 保存请求（写路径 bootstrap / platform seed 复用）。
 * <p>
 * 只按 {@link FiveWRecipeIdEnum} + {@link FiveWRecipeParams} 生成，不按 registryCode 分支。
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
        String code = params.getRegistryCode().trim();
        String storage = StringUtils.hasText(params.getStorageEntityTypeCode())
                ? params.getStorageEntityTypeCode().trim() : code;
        String categoryTypeCode = requireText(params.getCategoryTypeCode(), "categoryTypeCode");
        String label = label(params);

        DmFiveWOrchestrationBundleSaveReqVO req = baseReq(code, storage);

        DmFiveWOrchestrationBundleRespVO.WhoSlot category = whoSlot(
                code + "-category",
                DmDataTabLayoutKindEnum.CATEGORY.getCode(),
                List.of("categoryId"),
                null,
                categoryColumn(label, categoryTypeCode));

        DmFiveWOrchestrationBundleRespVO.WhoSlot model = whoSlot(
                code + "-model",
                DmDataTabLayoutKindEnum.MODEL.getCode(),
                List.of("modelId"),
                null,
                null);

        DmFiveWOrchestrationBundleRespVO.WhoSlot entity = whoSlot(
                code + "-entity",
                DmDataTabLayoutKindEnum.ENTITY.getCode(),
                List.of("entityId"),
                "rowSelection",
                null);

        req.setWhoSlots(List.of(category, model, entity));
        req.setWhatSlot(viewDetailWhat());
        req.setHowSlot(noneHow());
        return req;
    }

    private static DmFiveWOrchestrationBundleSaveReqVO categoryAsEntity(FiveWRecipeParams params) {
        String code = params.getRegistryCode().trim();
        String storage = StringUtils.hasText(params.getStorageEntityTypeCode())
                ? params.getStorageEntityTypeCode().trim() : code;
        String categoryTypeCode = requireText(params.getCategoryTypeCode(), "categoryTypeCode");
        String label = label(params);
        String slotRef = StringUtils.hasText(params.getCategorySlotRef())
                ? params.getCategorySlotRef().trim() : code + "-tree";

        DmFiveWOrchestrationBundleSaveReqVO req = baseReq(code, storage);

        DmFiveWOrchestrationBundleRespVO.WhoSlot category = whoSlot(
                slotRef,
                DmDataTabLayoutKindEnum.CATEGORY.getCode(),
                List.of("categoryId", "entityId"),
                "categoryLinkedEntity",
                categoryColumn(label, categoryTypeCode));

        req.setWhoSlots(List.of(category));
        req.setWhatSlot(viewDetailWhat());
        req.setHowSlot(noneHow());
        return req;
    }

    private static DmFiveWOrchestrationBundleSaveReqVO baseReq(String registryCode, String storageCode) {
        DmFiveWOrchestrationBundleSaveReqVO req = new DmFiveWOrchestrationBundleSaveReqVO();
        req.setRegistryCode(registryCode);
        req.setStorageEntityTypeCode(storageCode);

        DmFiveWOrchestrationBundleRespVO.Semantic semantic = new DmFiveWOrchestrationBundleRespVO.Semantic();
        semantic.setEnabled(true);
        semantic.setSelectionLevel("ENTITY");
        req.setSemantic(semantic);
        return req;
    }

    private static DmFiveWOrchestrationBundleRespVO.WhatSlot viewDetailWhat() {
        DmFiveWOrchestrationBundleRespVO.WhatSlot what = new DmFiveWOrchestrationBundleRespVO.WhatSlot();
        what.setMode("VIEW_DETAIL");
        what.setBindLayer("ENTITY");
        return what;
    }

    private static DmFiveWOrchestrationBundleRespVO.HowSlot noneHow() {
        DmFiveWOrchestrationBundleRespVO.HowSlot how = new DmFiveWOrchestrationBundleRespVO.HowSlot();
        how.setMode("NONE");
        return how;
    }

    private static DmFiveWOrchestrationBundleRespVO.WhoSlot whoSlot(
            String slotRef,
            String columnKind,
            List<String> contextOutputs,
            String entityIdRule,
            Map<String, Object> categoryColumn) {
        DmFiveWOrchestrationBundleRespVO.WhoSlot who = new DmFiveWOrchestrationBundleRespVO.WhoSlot();
        who.setSlotRef(slotRef);
        who.setColumnKind(columnKind);
        who.setEnabled(true);
        who.setContextOutputs(contextOutputs);
        who.setEntityIdRule(entityIdRule);
        who.setCategoryColumn(categoryColumn);
        return who;
    }

    private static Map<String, Object> categoryColumn(String label, String categoryTypeCode) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("label", label);
        map.put("categoryTypeCode", categoryTypeCode);
        return map;
    }

    private static String label(FiveWRecipeParams params) {
        if (StringUtils.hasText(params.getTypeName())) {
            return params.getTypeName().trim();
        }
        return params.getRegistryCode().trim();
    }

    private static String requireText(String value, String field) {
        if (!StringUtils.hasText(value)) {
            throw new ServiceException(500, "五维编排配方缺少 " + field);
        }
        return value.trim();
    }
}
