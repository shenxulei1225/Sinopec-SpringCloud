package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.framework.field.StructuredFieldSemantics;
import cn.cheers.x.module.dynamicbusiness.service.capability.BusinessCapabilityService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.field.FieldService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelFieldAssignmentService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import cn.cheers.x.module.dynamicbusiness.service.model.relation.ModelCategoryRelationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 在策略目录上准备「通用策略」这个型号。
 *
 * 管什么：补判定要读的列，挂到通用策略型号上，并在策略目录左边的分类树挂上「通用策略」。
 * 不管什么：不改告警策略型号，不写策略记录，不做判定。
 * 禁止：目录还没有时自己建一本策略目录。数据页只列出挂了分类的型号，不挂分类这页就看不到。
 * 建型号时目录上的基础列会整份抄过来。告警规则抄到通用策略上，详情会多出一页，条件和动作就不能并排。这些列从通用策略上拿掉。
 */
@Service
public class StrategyPublicationCatalog {

    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private FieldService fieldService;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private ModelService modelService;
    @Resource
    private ModelFieldAssignmentService modelFieldAssignmentService;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private CategoryService categoryService;
    @Resource
    private ModelCategoryRelationService modelCategoryRelationService;
    @Resource
    private BusinessCapabilityService businessCapabilityService;

    private static final String CATEGORY_CODE = "strategy-generic";

    public void ensure() {
        EntityTypeDO directory = entityTypeMapper.selectByCode(StrategyRecordFields.DIRECTORY);
        if (directory == null) {
            return;
        }
        String kind = ensureEnum(StrategyRecordFields.KIND, StrategyRecordFields.KIND_OPTIONS, "这条记录是域、一条策略，还是目录里的一条条件。");
        String conditions = ensureChoices(StrategyRecordFields.CONDITION_IDS, "从所属域公布的条件目录里勾选。");
        String match = ensureEnum(StrategyRecordFields.MATCH, StrategyRecordFields.MATCH_OPTIONS, "这些条件有一条成立，或要全部成立。");
        String actions = ensureChoices(StrategyRecordFields.ACTION_IDS, "从所属域公布的动作目录里勾选。");
        String mode = ensureText(StrategyRecordFields.ACTION_MODE, "这条策略选定的动作做法。必须是所属域公布过的一种。");
        String domain = ensureText(StrategyRecordFields.DOMAIN_CODE, "这条策略属于哪个域。填写该域记录的编码。");
        String conditionCatalog = ensureText(StrategyRecordFields.CONDITION_CATALOG, "这个域的条件来自哪本目录。");
        String actionCatalog = ensureText(StrategyRecordFields.ACTION_CATALOG, "这个域的动作来自哪本目录。");
        String combine = ensureEnum(StrategyRecordFields.COMBINE, StrategyRecordFields.COMBINE_OPTIONS, "多条策略同时成立时，全部返回，或只取第一条。");
        String allowed = ensureText(StrategyRecordFields.ALLOWED_MODES, "这个域允许的动作做法，用顿号或逗号分开。");
        Long modelId = ensureModel();
        for (String code : List.of(kind, conditions, match, actions, mode, domain, conditionCatalog, actionCatalog, combine, allowed)) {
            FieldDO field = fieldMapper.selectByCode(code);
            if (field == null || field.getId() == null) {
                throw new ServiceException(500, "策略字段没有准备好");
            }
            modelFieldAssignmentService.assignFieldToModel(modelId, field.getId(), false, false, false, false, null, null);
        }
        markSemantic(StrategyRecordFields.CONDITION_IDS, StructuredFieldSemantics.CONDITION_GROUP);
        markSemantic(StrategyRecordFields.MATCH, StructuredFieldSemantics.CONDITION_GROUP);
        markSemantic(StrategyRecordFields.ACTION_IDS, StructuredFieldSemantics.EXECUTION_RULES);
        markSemantic(StrategyRecordFields.ACTION_MODE, StructuredFieldSemantics.EXECUTION_RULES);
        dropInheritedAlarmColumns(modelId);
        fileModel(modelId);
        businessCapabilityService.refreshModelCrudFormDefinition(modelId);
    }

    /**
     * 通用策略不带告警型号上的列。
     * 告警规则留在这个型号上时，详情下半区会多出一页，条件和动作被拆成页签。
     * 这些列在目录上已标明不显示。从本型号卸掉分配后，表单不再画出。
     */
    private void dropInheritedAlarmColumns(Long modelId) {
        for (String name : List.of(
                AlarmStrategyCatalogEnsure.FIELD_POINT,
                AlarmStrategyCatalogEnsure.FIELD_OP,
                AlarmStrategyCatalogEnsure.FIELD_VALUE,
                AlarmStrategyCatalogEnsure.FIELD_TYPE,
                AlarmStrategyCatalogEnsure.FIELD_DEVICES,
                AlarmStrategyCatalogEnsure.FIELD_DEVICE_TYPE,
                AlarmStrategyCatalogEnsure.FIELD_HIGH,
                AlarmStrategyCatalogEnsure.FIELD_LOW,
                AlarmStrategyCatalogEnsure.FIELD_HIGH_RECOVER,
                AlarmStrategyCatalogEnsure.FIELD_LOW_RECOVER,
                AlarmStrategyCatalogEnsure.FIELD_RULES,
                AlarmTypeCatalog.FIELD_LEVEL)) {
            FieldDO field = findField(name);
            if (field == null || field.getId() == null) {
                continue;
            }
            try {
                modelFieldAssignmentService.unassignFieldFromModel(modelId, field.getId());
            } catch (ServiceException ex) {
                // 系统字段卸不掉。通用策略的判断不读它。
            }
        }
    }

    /** 数据页左边的分类树。型号挂到「通用策略」上，点开这一类才能看到这个型号。 */
    private void fileModel(Long modelId) {
        Long categoryId = categoryId(CATEGORY_CODE);
        if (categoryId == null) {
            Long parentId = topCategoryId();
            if (parentId == null) {
                throw new ServiceException(500, "策略目录还没有分类顶层，不能挂通用策略");
            }
            CategoryCreateReqVO request = new CategoryCreateReqVO();
            request.setParentId(parentId);
            request.setName(StrategyRecordFields.MODEL);
            request.setCode(CATEGORY_CODE);
            request.setCategoryTypeCode(StrategyRecordFields.DIRECTORY);
            request.setSort(3);
            request.setStatus(1);
            categoryService.createCategory(request);
            categoryId = categoryId(CATEGORY_CODE);
        }
        if (categoryId == null) {
            throw new ServiceException(500, "策略目录还没有通用策略分类");
        }
        if (!modelCategoryRelationService.existsRelation(modelId, categoryId, StrategyRecordFields.DIRECTORY)) {
            modelCategoryRelationService.associate(modelId, categoryId, StrategyRecordFields.DIRECTORY);
        }
    }

    private Long categoryId(String code) {
        for (CategoryDO row : categoryMapper.selectByCategoryTypeCode(StrategyRecordFields.DIRECTORY)) {
            if (code.equals(row.getCode()) && !Boolean.TRUE.equals(row.getDeleted())) {
                return row.getId();
            }
        }
        return null;
    }

    private Long topCategoryId() {
        for (CategoryDO row : categoryMapper.selectByCategoryTypeCode(StrategyRecordFields.DIRECTORY)) {
            if (!Boolean.TRUE.equals(row.getDeleted()) && (row.getParentId() == null || row.getParentId() == 0L)) {
                return row.getId();
            }
        }
        return null;
    }

    private Long ensureModel() {
        ModelDO named = modelMapper.selectByNameAndEntityTypeCode(StrategyRecordFields.MODEL, StrategyRecordFields.DIRECTORY);
        if (named != null && named.getId() != null) {
            return named.getId();
        }
        ModelCreateReqVO request = new ModelCreateReqVO();
        request.setName(StrategyRecordFields.MODEL);
        request.setEntityTypeCode(StrategyRecordFields.DIRECTORY);
        request.setStatus(1);
        request.setGovernanceStatus("COMPANY");
        request.setDescription("各业务共用的策略。一个域公布来源和做法，一条策略勾选条件和动作。");
        return modelService.createModel(request);
    }

    private String ensureText(String name, String description) {
        FieldDO existing = findField(name);
        if (existing != null) {
            return existing.getCode();
        }
        FieldCreateReqVO request = new FieldCreateReqVO();
        request.setName(name);
        request.setType("LONG_TEXT");
        request.setSource("CUSTOM");
        request.setStatus(1);
        request.setDescription(description);
        return createdCode(name, fieldService.createField(request));
    }

    private String ensureChoices(String name, String description) {
        FieldDO existing = findField(name);
        if (existing != null) {
            if (!"MULTI_SELECT".equals(existing.getType()) || existing.getOptions() == null) {
                saveField(existing, name, "MULTI_SELECT", description, "[]");
            }
            return existing.getCode();
        }
        FieldCreateReqVO request = new FieldCreateReqVO();
        request.setName(name);
        request.setType("MULTI_SELECT");
        request.setOptions("[]");
        request.setSource("CUSTOM");
        request.setStatus(1);
        request.setDescription(description);
        return createdCode(name, fieldService.createField(request));
    }

    private String ensureEnum(String name, String options, String description) {
        FieldDO existing = findField(name);
        if (existing != null) {
            if (!options.equals(existing.getOptions())) {
                saveField(existing, name, "ENUM", description, options);
            }
            return existing.getCode();
        }
        FieldCreateReqVO request = new FieldCreateReqVO();
        request.setName(name);
        request.setType("ENUM");
        request.setOptions(options);
        request.setSource("CUSTOM");
        request.setStatus(1);
        request.setDescription(description);
        return createdCode(name, fieldService.createField(request));
    }

    private String createdCode(String name, Long fieldId) {
        FieldDO created = fieldMapper.selectById(fieldId);
        if (created == null || created.getCode() == null || created.getCode().isBlank()) {
            throw new ServiceException(500, "字段「" + name + "」创建后没有编码");
        }
        return created.getCode();
    }

    private FieldDO findField(String name) {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        List<FieldDO> rows = fieldMapper.selectList(new LambdaQueryWrapperX<FieldDO>()
                .eq(FieldDO::getName, name)
                .eq(FieldDO::getTenantId, tenantId)
                .eq(FieldDO::getDeleted, false));
        if (rows.size() > 1) {
            throw new ServiceException(500, "字段「" + name + "」有多条，对不上要挂的那一列");
        }
        return rows.isEmpty() ? null : rows.get(0);
    }

    private void saveField(FieldDO existing, String name, String type, String description, String options) {
        FieldUpdateReqVO update = new FieldUpdateReqVO();
        update.setId(existing.getId());
        update.setName(name);
        update.setType(type);
        update.setSource(existing.getSource());
        update.setStatus(existing.getStatus() == null ? 1 : existing.getStatus());
        update.setDescription(description);
        update.setOptions(options);
        update.setUnit(existing.getUnit());
        fieldService.updateField(update);
    }

    /** 详情按用途把条件和动作画成两栏。用途写在列上，不按列名认。 */
    private void markSemantic(String name, String semantic) {
        FieldDO field = findField(name);
        if (field == null || semantic.equals(field.getSemanticType())) {
            return;
        }
        field.setSemanticType(semantic);
        fieldMapper.updateById(field);
    }
}
