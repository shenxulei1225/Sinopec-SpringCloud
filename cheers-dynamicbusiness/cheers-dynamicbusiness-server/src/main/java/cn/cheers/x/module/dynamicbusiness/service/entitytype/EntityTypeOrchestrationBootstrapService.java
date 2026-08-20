package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmFiveWOrchestrationBundleSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmFiveWOrchestrationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.enums.datamgmt.FiveWRecipeIdEnum;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.EntityTypeEntryKindEnum;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryModeSupport;
import cn.cheers.x.module.dynamicbusiness.service.datamgmt.DmFiveWOrchestrationService;
import cn.cheers.x.module.dynamicbusiness.service.datamgmt.FiveWRecipeParams;
import cn.cheers.x.module.dynamicbusiness.service.datamgmt.FiveWRecipeTemplate;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 建类型时自动写入默认五维编排 bundle（幂等：已有行不覆盖）。
 */
@Service
@Slf4j
public class EntityTypeOrchestrationBootstrapService {

    @Resource
    private EntityTypeMapper entityTypeMapper;

    @Resource
    private CategoryTypeMapper categoryTypeMapper;

    @Resource
    private DmFiveWOrchestrationMapper orchestrationMapper;

    @Resource
    private DmFiveWOrchestrationService dmFiveWOrchestrationService;

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

        FiveWRecipeIdEnum recipeId = resolveRecipe(entityType);
        FiveWRecipeParams params = buildParams(entityType, recipeId);
        DmFiveWOrchestrationBundleSaveReqVO saveReq = FiveWRecipeTemplate.toSaveReq(recipeId, params);
        dmFiveWOrchestrationService.saveBundle(saveReq);
        log.info("[orchestration-bootstrap] entityTypeCode={}, recipe={}", code, recipeId.getRecipeId());
    }

    FiveWRecipeIdEnum resolveRecipe(EntityTypeDO entityType) {
        EntityTypeEntryKindEnum kind = EntityTypeEntryKindEnum.fromCode(entityType.getEntryKind());
        if (kind.isCategory()) {
            return FiveWRecipeIdEnum.CATEGORY_AS_ENTITY;
        }
        if (kind.isDomainEntry() || kind.isReuseEntry() || kind.isScopeEntry()) {
            return FiveWRecipeIdEnum.LEDGER_3COL;
        }
        CategoryTypeDO categoryType = categoryTypeMapper.selectByCategoryTypeCode(entityType.getCode());
        if (categoryType != null && CategoryModeSupport.isAdvanced(categoryType)) {
            return FiveWRecipeIdEnum.CATEGORY_AS_ENTITY;
        }
        return FiveWRecipeIdEnum.LEDGER_3COL;
    }

    FiveWRecipeParams buildParams(EntityTypeDO entityType, FiveWRecipeIdEnum recipeId) {
        String code = entityType.getCode().trim();
        String name = StringUtils.hasText(entityType.getName()) ? entityType.getName().trim() : code;

        FiveWRecipeParams.FiveWRecipeParamsBuilder builder = FiveWRecipeParams.builder()
                .registryCode(code)
                .storageEntityTypeCode(code)
                .typeName(name);

        EntityTypeEntryKindEnum kind = EntityTypeEntryKindEnum.fromCode(entityType.getEntryKind());
        if (kind.isDomainEntry() || kind.isReuseEntry() || kind.isScopeEntry()) {
            String baseCode = entityType.getBaseEntityTypeCode();
            if (!StringUtils.hasText(baseCode)) {
                log.warn("数据类型 {} 缺少 baseEntityTypeCode，五维 bootstrap 分类种类回退为自身编码", code);
                builder.categoryTypeCode(code);
            } else {
                builder.categoryTypeCode(baseCode.trim());
            }
        } else {
            builder.categoryTypeCode(code);
        }

        if (recipeId == FiveWRecipeIdEnum.CATEGORY_AS_ENTITY) {
            builder.categorySlotRef(code + "-tree");
        }
        return builder.build();
    }
}
