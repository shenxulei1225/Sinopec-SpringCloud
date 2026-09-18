package cn.cheers.x.module.dynamicbusiness.service.entity.refcategory;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryTypeMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeResolver;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryModeSupport;
import cn.cheers.x.module.dynamicbusiness.service.category.relation.CategoryCategoryRelationService;
import cn.cheers.x.module.dynamicbusiness.service.model.relation.ModelCategoryRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * REF 投影 · 分类—分类（组合 3）实现。
 *
 * <p><strong>负责</strong>：宿主是分类即实体、成员是主体存数类型的纯分类时，REF 保存后 upsert
 * 分类—分类，供数据管理按分类—分类裁树。</p>
 * <p><strong>不负责</strong>：分类—实体 / 宿主侧分类—型号（{@code EntityCategoryRelationService}）；
 * REF 解绑时删 CC；组合 1 双 Pattern C 裁树并集（读侧另定）。</p>
 * <p><strong>禁止</strong>：把型号挂过的其它种类分类（例如巡检任务型号挂了区域分类）当成成员纯分类去建边；
 * 禁止因此整笔打回主体 REF 写入（所属设施等）。种类对不上只跳过该成员并打日志，缺口留在型号—分类挂接。</p>
 */
@Service
@Slf4j
public class EntityRefCategoryCategoryProjectionServiceImpl
        implements EntityRefCategoryCategoryProjectionService {

    private final CategoryTypeMapper categoryTypeMapper;
    private final CategoryMapper categoryMapper;
    private final ModelCategoryRelationService modelCategoryRelationService;
    private final CategoryCategoryRelationService categoryCategoryRelationService;
    private final EntityTypeScopeResolver entityTypeScopeResolver;

    public EntityRefCategoryCategoryProjectionServiceImpl(
            CategoryTypeMapper categoryTypeMapper,
            CategoryMapper categoryMapper,
            ModelCategoryRelationService modelCategoryRelationService,
            @Lazy CategoryCategoryRelationService categoryCategoryRelationService,
            EntityTypeScopeResolver entityTypeScopeResolver) {
        this.categoryTypeMapper = categoryTypeMapper;
        this.categoryMapper = categoryMapper;
        this.modelCategoryRelationService = modelCategoryRelationService;
        this.categoryCategoryRelationService = categoryCategoryRelationService;
        this.entityTypeScopeResolver = entityTypeScopeResolver;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncCategoryCategoryOnRefAssociate(
            Long subjectModelId,
            String subjectEntityTypeCode,
            Long hostCategoryId,
            String hostCategoryTypeCode) {
        if (subjectModelId == null || subjectModelId <= 0
                || hostCategoryId == null || hostCategoryId <= 0) {
            return;
        }
        String hostType = normalizeTypeCode(hostCategoryTypeCode);
        String memberType = resolveMemberCategoryTypeCode(subjectEntityTypeCode);
        if (hostType == null || memberType == null || hostType.equalsIgnoreCase(memberType)) {
            return;
        }
        if (!isAdvancedCategoryType(hostType) || isAdvancedCategoryType(memberType)) {
            log.debug("[ref→cc] 跳过：非组合3 hostType={}, memberType={}", hostType, memberType);
            return;
        }

        List<Long> memberCategoryIds =
                modelCategoryRelationService.listCategoryIdsByModelId(subjectModelId, memberType);
        if (CollectionUtils.isEmpty(memberCategoryIds)) {
            log.debug("[ref→cc] 跳过：型号未挂成员纯分类 memberType={}, modelId={}", memberType, subjectModelId);
            return;
        }

        Set<Long> uniqueMemberIds = new LinkedHashSet<>(memberCategoryIds);
        for (Long memberCategoryId : uniqueMemberIds) {
            if (memberCategoryId == null || memberCategoryId <= 0) {
                continue;
            }
            if (!isMemberPureCategory(memberCategoryId, memberType)) {
                log.warn("[ref→cc] 跳过：型号挂接的分类不是成员纯分类 memberCategoryId={}, expectedType={}, modelId={}",
                        memberCategoryId, memberType, subjectModelId);
                continue;
            }
            categoryCategoryRelationService.associate(
                    hostCategoryId, memberCategoryId, hostType, memberType);
            log.info("[ref→cc] 分类—分类: hostCategoryId={}, memberCategoryId={}, hostType={}, memberType={}, modelId={}",
                    hostCategoryId, memberCategoryId, hostType, memberType, subjectModelId);
        }
    }

    /**
     * 成员必须是「种类 = 主体存数类型」的纯分类。
     * 型号—分类挂接只记业务类型码，不能证明该分类就是任务/设备纯分类；种类对不上则不能建分类—分类。
     */
    private boolean isMemberPureCategory(Long memberCategoryId, String memberType) {
        CategoryDO member = categoryMapper.selectByIdAndCategoryTypeCode(memberCategoryId, memberType);
        if (member == null || !StringUtils.hasText(member.getCategoryTypeCode())) {
            return false;
        }
        return memberType.equalsIgnoreCase(member.getCategoryTypeCode().trim());
    }

    /**
     * 成员纯分类种类：与主体存储类型同码（场景 1 设备分类等）。
     */
    private String resolveMemberCategoryTypeCode(String subjectEntityTypeCode) {
        if (!StringUtils.hasText(subjectEntityTypeCode)) {
            return null;
        }
        String storage = entityTypeScopeResolver.resolveStorageEntityTypeCode(subjectEntityTypeCode.trim());
        return StringUtils.hasText(storage) ? storage.trim() : null;
    }

    private static String normalizeTypeCode(String categoryTypeCode) {
        if (!StringUtils.hasText(categoryTypeCode)) {
            return null;
        }
        return categoryTypeCode.trim();
    }

    private boolean isAdvancedCategoryType(String categoryTypeCode) {
        CategoryTypeDO type = categoryTypeMapper.selectByCategoryTypeCode(categoryTypeCode);
        if (type == null) {
            return false;
        }
        return CategoryModeSupport.isAdvanced(type);
    }
}
