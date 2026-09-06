package cn.cheers.x.module.dynamicbusiness.service.hierarchy;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;
import cn.cheers.x.module.dynamicbusiness.framework.hierarchy.OrgTreeParentFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryModeSupport;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.BaseFieldLibrarySyncService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeBaseFieldService;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 高级分类（分类即实体）：确保数据类型挂上系统「组织上级」字段（parentId）。
 *
 * <p>管什么：创建高级分类时自动挂组织上级（名称如上级运营区域/上级分区），目标固定为本类型并锁死。</p>
 * <p>不负责：用户自定义「引用数据」的数据源配置。</p>
 * <p>禁止：开放字段库修改本字段的引用目标；手建字段不得标 TREE_PARENT。</p>
 */
@Service
@Slf4j
public class OrgTreeParentFieldEnsureService {

    private static final String DYNAMIC_ENTITY_PROVIDER_PREFIX = "dynamic-entity:";

    @Resource
    private CategoryTypeMapper categoryTypeMapper;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private EntityTypeBaseFieldMapper baseFieldMapper;
    @Resource
    @Lazy
    private EntityTypeBaseFieldService entityTypeBaseFieldService;
    @Resource
    @Lazy
    private BaseFieldLibrarySyncService baseFieldLibrarySyncService;

    /** 当前租户：凡存在 ADVANCED 分类种类且种类编码=数据类型编码的，补组织上级字段。 */
    @Transactional(rollbackFor = Exception.class)
    public int ensureAllAdvancedEntityTypes() {
        int n = 0;
        List<CategoryTypeDO> types = categoryTypeMapper.selectList(new LambdaQueryWrapperX<CategoryTypeDO>());
        for (CategoryTypeDO type : types) {
            if (!isAdvancedType(type) || !StringUtils.hasText(type.getCategoryTypeCode())) {
                continue;
            }
            if (ensureForEntityTypeCode(type.getCategoryTypeCode().trim())) {
                n++;
            }
        }
        if (n > 0) {
            log.info("[OrgTreeParentFieldEnsure] ensureAllAdvancedEntityTypes attachedOrSynced={}", n);
        }
        return n;
    }

    /**
     * 若该数据类型对应高级分类种类，则确保组织上级字段已挂接（幂等）。
     *
     * @return true 表示本次新挂了类型基础字段；false 表示跳过或已存在
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean ensureForEntityTypeCode(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode) || !hasAdvancedCategoryType(entityTypeCode)) {
            return false;
        }
        String code = entityTypeCode.trim();
        FieldDO libraryField = ensureLibraryField(code);
        EntityTypeBaseFieldDO existing =
                baseFieldMapper.selectByEntityTypeCodeAndFieldCode(code, OrgTreeParentFieldCodes.FIELD_CODE);
        if (existing != null) {
            baseFieldLibrarySyncService.assignLibraryFieldToAllModels(code, libraryField, existing);
            return false;
        }

        EntityTypeBaseFieldSaveReqVO req = new EntityTypeBaseFieldSaveReqVO();
        req.setEntityTypeCode(code);
        req.setLibraryFieldId(libraryField.getId());
        req.setRequired(false);
        req.setIsSearchable(false);
        req.setIsFilterable(true);
        req.setIsSortable(false);
        req.setFieldName(resolveDisplayName(code));
        req.setDescription("系统组织上级：读写实体 parentId/treePath，并与分类树同步；非用户手建同类引用");
        entityTypeBaseFieldService.createBaseField(req);
        log.info("[OrgTreeParentFieldEnsure] attached parentId to entityType={}", code);
        return true;
    }

    public boolean hasAdvancedCategoryType(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            return false;
        }
        List<CategoryTypeDO> list = categoryTypeMapper.selectList(new LambdaQueryWrapperX<CategoryTypeDO>()
                .eq(CategoryTypeDO::getCategoryTypeCode, entityTypeCode.trim()));
        return list.stream().anyMatch(this::isAdvancedType);
    }

    private boolean isAdvancedType(CategoryTypeDO type) {
        return type != null && CategoryModeSupport.isAdvanced(CategoryModeSupport.resolveFromType(type));
    }

    private FieldDO ensureLibraryField(String entityTypeCode) {
        FieldDO existing = fieldMapper.selectByCode(OrgTreeParentFieldCodes.FIELD_CODE);
        if (existing != null) {
            boolean dirty = false;
            if (!FieldTypeEnum.isEntitySelfRef(existing.getType())) {
                existing.setType(FieldTypeEnum.ENTITY_SELF_REF.getCode());
                dirty = true;
            }
            if (!OrgTreeParentFieldCodes.isTreeParentSemantic(existing.getSemanticType())) {
                existing.setSemanticType(OrgTreeParentFieldCodes.SEMANTIC_TREE_PARENT);
                dirty = true;
            }
            // 库中仅一行 parentId，供多种高级分类数据类型复用；表单目标取当前 entityTypeCode，不改写 provider
            if (!"SYSTEM".equalsIgnoreCase(existing.getSource())) {
                existing.setSource("SYSTEM");
                dirty = true;
            }
            if (existing.getStatus() == null || existing.getStatus() != 1) {
                existing.setStatus(1);
                dirty = true;
            }
            if (dirty) {
                fieldMapper.updateById(existing);
            }
            return existing;
        }

        FieldDO created = FieldDO.builder()
                .code(OrgTreeParentFieldCodes.FIELD_CODE)
                .name(OrgTreeParentFieldCodes.DISPLAY_NAME)
                .type(FieldTypeEnum.ENTITY_SELF_REF.getCode())
                .source("SYSTEM")
                .status(1)
                .semanticType(OrgTreeParentFieldCodes.SEMANTIC_TREE_PARENT)
                // provider 可选；组织上级渲染时强制目标=当前数据类型，避免多类型共用一行时串目标
                .providerCode(DYNAMIC_ENTITY_PROVIDER_PREFIX + entityTypeCode)
                .description("系统组织上级（同数据类型引用 / TREE_PARENT）")
                .build();
        fieldMapper.insert(created);
        return created;
    }

    private static String resolveDisplayName(String entityTypeCode) {
        if ("region".equalsIgnoreCase(entityTypeCode)) {
            return "上级运营区域";
        }
        if ("facility".equalsIgnoreCase(entityTypeCode)) {
            return "上级设施";
        }
        if ("zone".equalsIgnoreCase(entityTypeCode)) {
            return "上级分区";
        }
        if ("department".equalsIgnoreCase(entityTypeCode)) {
            return "上级部门";
        }
        return OrgTreeParentFieldCodes.DISPLAY_NAME;
    }
}
