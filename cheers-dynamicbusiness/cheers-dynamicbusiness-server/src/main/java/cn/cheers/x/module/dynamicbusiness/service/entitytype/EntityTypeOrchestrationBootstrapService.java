package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmCatalogOrchestrationBundleSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmCatalogOrchestrationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.enums.datamgmt.OrchestrationRecipeIdEnum;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.EntityTypeEntryKindEnum;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryModeSupport;
import cn.cheers.x.module.dynamicbusiness.service.datamgmt.DmCatalogOrchestrationService;
import cn.cheers.x.module.dynamicbusiness.service.datamgmt.OrchestrationRecipeParams;
import cn.cheers.x.module.dynamicbusiness.service.datamgmt.OrchestrationRecipeTemplate;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 建类型时自动写入编排头（幂等：已有行不覆盖）。
 * <p>
 * 只写点树还是点列表行。详情跟谁认布局栏 + 关系图连线，不写 What/How 槽。
 */
@Service
@Slf4j
public class EntityTypeOrchestrationBootstrapService {

    @Resource
    private EntityTypeMapper entityTypeMapper;

    @Resource
    private CategoryTypeMapper categoryTypeMapper;

    @Resource
    private DmCatalogOrchestrationMapper orchestrationMapper;

    @Resource
    private DmCatalogOrchestrationService catalogOrchestrationService;

    @Transactional(rollbackFor = Exception.class)
    public void ensureForEntityTypeCode(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            return;
        }
        String code = entityTypeCode.trim();
        if (orchestrationMapper.selectByEntityTypeCode(code) != null) {
            return;
        }

        EntityTypeDO entityType = entityTypeMapper.selectByCode(code);
        if (entityType == null) {
            return;
        }

        OrchestrationRecipeIdEnum recipeId = resolveRecipe(entityType);
        OrchestrationRecipeParams params = buildParams(entityType, recipeId);
        DmCatalogOrchestrationBundleSaveReqVO saveReq = OrchestrationRecipeTemplate.toSaveReq(recipeId, params);
        catalogOrchestrationService.saveBundle(saveReq);
        log.info("[orchestration-bootstrap] entityTypeCode={}, recipe={}", code, recipeId.getRecipeId());
    }

    OrchestrationRecipeIdEnum resolveRecipe(EntityTypeDO entityType) {
        EntityTypeEntryKindEnum kind = EntityTypeEntryKindEnum.fromCode(entityType.getEntryKind());
        if (kind.isCategory()) {
            return OrchestrationRecipeIdEnum.CATEGORY_AS_ENTITY;
        }
        if (kind.isDomainEntry() || kind.isReuseEntry() || kind.isScopeEntry()) {
            return OrchestrationRecipeIdEnum.LEDGER_3COL;
        }
        CategoryTypeDO categoryType = categoryTypeMapper.selectByCategoryTypeCode(entityType.getCode());
        if (categoryType != null && CategoryModeSupport.isAdvanced(categoryType)) {
            return OrchestrationRecipeIdEnum.CATEGORY_AS_ENTITY;
        }
        return OrchestrationRecipeIdEnum.LEDGER_3COL;
    }

    OrchestrationRecipeParams buildParams(EntityTypeDO entityType, OrchestrationRecipeIdEnum recipeId) {
        String code = entityType.getCode().trim();
        String name = StringUtils.hasText(entityType.getName()) ? entityType.getName().trim() : code;

        EntityTypeEntryKindEnum kind = EntityTypeEntryKindEnum.fromCode(entityType.getEntryKind());
        String storageCode = code;
        String categoryTypeCode = code;
        if (kind.reusesBaseStorage()) {
            String baseCode = entityType.getBaseEntityTypeCode();
            if (!StringUtils.hasText(baseCode)) {
                log.warn("数据类型 {} 缺少 baseEntityTypeCode，编排 bootstrap 存储/分类回退为自身编码", code);
            } else {
                storageCode = baseCode.trim();
                categoryTypeCode = storageCode;
            }
        }

        OrchestrationRecipeParams.OrchestrationRecipeParamsBuilder builder = OrchestrationRecipeParams.builder()
                .registryCode(code)
                .storageEntityTypeCode(storageCode)
                .categoryTypeCode(categoryTypeCode)
                .typeName(name);

        if (recipeId == OrchestrationRecipeIdEnum.CATEGORY_AS_ENTITY) {
            builder.categorySlotRef(code + "-tree");
        }
        return builder.build();
    }
}
