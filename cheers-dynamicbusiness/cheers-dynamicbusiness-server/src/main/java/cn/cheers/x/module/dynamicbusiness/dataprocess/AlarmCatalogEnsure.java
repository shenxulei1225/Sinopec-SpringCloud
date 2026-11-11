package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmModelTabCategoryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmModelTabCategorySaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.datamgmt.DmModelTabCategoryService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.BaseFieldLibrarySyncService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeBaseFieldService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.FacilityOwningFieldEnsureService;
import cn.cheers.x.module.dynamicbusiness.service.field.FieldService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import cn.cheers.x.module.platformresource.api.component.ComponentPropsApi;
import cn.cheers.x.module.platformresource.api.component.dto.ComponentDataSourceDTO;
import cn.cheers.x.module.platformresource.api.component.dto.ComponentPropsCreateTemplateReqDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 在数据管理里准备告警目录。
 * 目录、字段、型号走数据管理的创建。告警类型写成这本目录的分类。
 * 启动时每个租户准备一次。记下一条告警时若还没有，再补一次。
 * 不在打开数据管理页面时调用。不写告警记录。
 * 原因、后果、联动、允许响应时间挂在告警记录上，给人看。原文在告警规则上，这里不编字。
 * 告警按场站分开。目录是站场级，每一行写所属场站。
 */
@Service
public class AlarmCatalogEnsure {

    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private EntityTypeService entityTypeService;
    @Resource
    private FacilityOwningFieldEnsureService facilityOwningFieldEnsureService;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private FieldService fieldService;
    @Resource
    private EntityTypeBaseFieldMapper baseFieldMapper;
    @Resource
    private EntityTypeBaseFieldService entityTypeBaseFieldService;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private ModelService modelService;
    @Resource
    private BaseFieldLibrarySyncService baseFieldLibrarySyncService;
    @Resource
    private CategoryTypeMapper categoryTypeMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private CategoryService categoryService;
    @Resource
    private DmModelTabCategoryService dmModelTabCategoryService;
    @Resource
    private ComponentPropsApi componentPropsApi;

    @Transactional(rollbackFor = Exception.class)
    public void ensure() {
        if (SecurityFrameworkUtils.getLoginUserId() == null) {
            throw new ServiceException(401, "没有登录用户，告警目录没有准备");
        }
        ensureDirectory();
        attachBaseField(ensureField(AlarmTypeCatalog.FIELD_LEVEL, "FLD-ALM-002"), AlarmTypeCatalog.FIELD_LEVEL);
        attachBaseField(ensureField(AlarmTypeCatalog.FIELD_CONTENT, "FLD-ALM-005"), AlarmTypeCatalog.FIELD_CONTENT);
        attachBaseField(ensureField(AlarmTypeCatalog.FIELD_HANDLING, "FLD-ALM-006"), AlarmTypeCatalog.FIELD_HANDLING);
        for (String name : List.of(
                AlarmTypeCatalog.FIELD_DEVICE,
                AlarmTypeCatalog.FIELD_LOCATION,
                AlarmTypeCatalog.FIELD_TRIGGER,
                AlarmTypeCatalog.FIELD_THRESHOLD,
                AlarmTypeCatalog.FIELD_POINT,
                AlarmTypeCatalog.FIELD_REVIEW,
                AlarmTypeCatalog.FIELD_TRACE)) {
            attachBaseField(ensureField(name, null), name);
        }
        attachHelpText(ensureHelpField(AlarmTypeCatalog.FIELD_CAUSE, null, true,
                "这条告警单独对应哪些情况。可以写多条。"), AlarmTypeCatalog.FIELD_CAUSE);
        attachHelpText(ensureHelpField(AlarmTypeCatalog.FIELD_CONSEQUENCE, null, true,
                "没人处理时，马上、直接会发生什么。"), AlarmTypeCatalog.FIELD_CONSEQUENCE);
        attachHelpText(ensureHelpField(AlarmTypeCatalog.FIELD_ACTION, AlarmTypeCatalog.FIELD_ACTION_PREVIOUS, true,
                "这条告警成立时要启动的联动。例如温度超高启动消防。"), AlarmTypeCatalog.FIELD_ACTION);
        attachHelpText(ensureHelpField(AlarmTypeCatalog.FIELD_RESPONSE_TIME, null, false,
                "从告警出现，到再动手也来不及阻止这个后果的时间。"), AlarmTypeCatalog.FIELD_RESPONSE_TIME);
        attachHelpText(ensureHelpField(AlarmTypeCatalog.FIELD_SCENE_OBJECTS, null, true,
                "这次告警已经写下的人和车，以及各自在场景上的位置点。没有就不写。"),
                AlarmTypeCatalog.FIELD_SCENE_OBJECTS);
        attachBaseField(ensureDateTime(AlarmTypeCatalog.FIELD_SHELF_UNTIL), AlarmTypeCatalog.FIELD_SHELF_UNTIL);
        ensureModel();
        ensureModelAdmin();
        ensureTypes();
    }

    public Long modelId() {
        ModelDO model = modelMapper.selectByNameAndEntityTypeCode(
                AlarmTypeCatalog.MODEL_NAME, AlarmTypeCatalog.DIRECTORY);
        if (model == null || model.getId() == null) {
            throw new ServiceException(500, "告警目录还没有型号");
        }
        return model.getId();
    }

    public String fieldCode(String name) {
        String preferred = switch (name) {
            case AlarmTypeCatalog.FIELD_LEVEL -> "FLD-ALM-002";
            case AlarmTypeCatalog.FIELD_CONTENT -> "FLD-ALM-005";
            case AlarmTypeCatalog.FIELD_HANDLING -> "FLD-ALM-006";
            default -> null;
        };
        FieldDO field = findField(name, preferred);
        if (field == null || !StringUtils.hasText(field.getCode())) {
            throw new ServiceException(500, "告警目录缺少字段「" + name + "」");
        }
        return field.getCode();
    }

    /**
     * 选项里已经有这个值就用它。
     * 原先的严重、一般、异常、提示，对上这列已有的紧急、一般、重要、提示。
     * 对不上就失败，不另写一个值。
     */
    public String fit(String name, String value) {
        String text = value == null ? "" : value.trim();
        FieldDO field = findField(name, preferredCode(name));
        if (field == null || !"ENUM".equalsIgnoreCase(field.getType()) || !StringUtils.hasText(field.getOptions())) {
            return text;
        }
        List<String> options = optionValues(field.getOptions());
        if (options.contains(text)) {
            return text;
        }
        String mapped = mappedOption(text);
        if (mapped != null && options.contains(mapped)) {
            return mapped;
        }
        throw new ServiceException(400, "字段「" + name + "」没有选项「" + text + "」");
    }

    /** 按分类编码取告警类型。没有这个分类就失败，不改挂到别的类型。 */
    public Long categoryId(String code) {
        CategoryDO found = findType(code);
        if (found == null || found.getId() == null) {
            throw new ServiceException(500, "告警目录没有类型「" + code + "」");
        }
        return found.getId();
    }

    private void ensureDirectory() {
        EntityTypeDO existing = entityTypeMapper.selectByCode(AlarmTypeCatalog.DIRECTORY);
        if (existing == null) {
            EntityTypeCreateReqVO request = new EntityTypeCreateReqVO();
            request.setSort(80);
            request.setName(AlarmTypeCatalog.DIRECTORY_NAME);
            request.setCode(AlarmTypeCatalog.DIRECTORY);
            request.setAlias(AlarmTypeCatalog.DIRECTORY_NAME);
            request.setStatus("active");
            request.setStorageType("DEDICATED");
            request.setEntryKind("NATIVE");
            request.setWorkScope(EntityTypeDO.WORK_SCOPE_FACILITY);
            request.setGroupName(AlarmTypeCatalog.GROUP_NAME);
            request.setDescription("告警记录。分类是告警类型。新记录的处理状态是未处理。每一条属于一座站。");
            request.setAssociationFields("{}");
            entityTypeService.create(request);
        } else if (!EntityTypeDO.WORK_SCOPE_FACILITY.equalsIgnoreCase(
                existing.getWorkScope() == null ? "" : existing.getWorkScope().trim())) {
            EntityTypeUpdateReqVO update = new EntityTypeUpdateReqVO();
            update.setId(existing.getId());
            update.setWorkScope(EntityTypeDO.WORK_SCOPE_FACILITY);
            entityTypeService.update(update);
        } else {
            facilityOwningFieldEnsureService.ensureForEntityTypeCode(AlarmTypeCatalog.DIRECTORY);
        }
        facilityOwningFieldEnsureService.fillBlankOwningStationWhenTenantHasOne(AlarmTypeCatalog.DIRECTORY);
    }

    /**
     * 原因、后果、联动、允许响应时间。
     * 已有旧名时改名，不另建一列。说明与当前口径不一致时改说明。
     */
    private String ensureHelpField(String name, String previousName, boolean longText, String description) {
        FieldDO existing = findField(name, null);
        if (existing == null && StringUtils.hasText(previousName)) {
            existing = findField(previousName, null);
        }
        if (existing == null) {
            FieldCreateReqVO request = new FieldCreateReqVO();
            request.setName(name);
            request.setType(longText ? "LONG_TEXT" : "TEXT");
            request.setSource("CUSTOM");
            request.setStatus(1);
            request.setDescription(description);
            Long fieldId = fieldService.createField(request);
            FieldDO created = fieldMapper.selectById(fieldId);
            if (created == null || !StringUtils.hasText(created.getCode())) {
                throw new ServiceException(500, "字段「" + name + "」创建后没有编码");
            }
            return created.getCode();
        }
        if (!name.equals(existing.getName()) || !description.equals(existing.getDescription())) {
            FieldUpdateReqVO update = new FieldUpdateReqVO();
            update.setId(existing.getId());
            update.setName(name);
            update.setType(existing.getType());
            update.setSource(existing.getSource());
            update.setStatus(existing.getStatus() == null ? 1 : existing.getStatus());
            update.setDescription(description);
            update.setOptions(existing.getOptions());
            update.setUnit(existing.getUnit());
            fieldService.updateField(update);
        }
        return existing.getCode();
    }

    private String ensureField(String name, String preferredCode) {
        return ensureField(name, preferredCode, name);
    }

    private String ensureField(String name, String preferredCode, String description) {
        FieldDO existing = findField(name, preferredCode);
        if (existing != null && StringUtils.hasText(existing.getCode())) {
            return existing.getCode();
        }
        FieldCreateReqVO request = new FieldCreateReqVO();
        request.setName(name);
        request.setType("TEXT");
        request.setSource("CUSTOM");
        request.setStatus(1);
        request.setDescription(description);
        Long fieldId = fieldService.createField(request);
        FieldDO created = fieldMapper.selectById(fieldId);
        if (created == null || !StringUtils.hasText(created.getCode())) {
            throw new ServiceException(500, "字段「" + name + "」创建后没有编码");
        }
        return created.getCode();
    }

    /** 搁置到用日期时间，人在告警记录上选到哪一刻。 */
    private String ensureDateTime(String name) {
        FieldDO existing = findField(name, null);
        if (existing != null && StringUtils.hasText(existing.getCode())) {
            if (!"DATETIME".equalsIgnoreCase(existing.getType())) {
                throw new ServiceException(400, "字段「" + name + "」已经存在，但不是日期时间");
            }
            return existing.getCode();
        }
        FieldCreateReqVO request = new FieldCreateReqVO();
        request.setName(name);
        request.setType("DATETIME");
        request.setSource("CUSTOM");
        request.setStatus(1);
        request.setDescription("操作员把这条告警搁置到这个时间。到点后按读数再决定要不要报。");
        Long fieldId = fieldService.createField(request);
        FieldDO created = fieldMapper.selectById(fieldId);
        if (created == null || !StringUtils.hasText(created.getCode())) {
            throw new ServiceException(500, "字段「" + name + "」创建后没有编码");
        }
        return created.getCode();
    }

    private FieldDO findField(String name, String preferredCode) {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        if (StringUtils.hasText(preferredCode)) {
            List<FieldDO> byCode = fieldMapper.selectList(new LambdaQueryWrapperX<FieldDO>()
                    .eq(FieldDO::getCode, preferredCode)
                    .eq(FieldDO::getTenantId, tenantId)
                    .eq(FieldDO::getDeleted, false));
            if (byCode.size() == 1) {
                return byCode.get(0);
            }
        }
        List<FieldDO> byName = fieldMapper.selectList(new LambdaQueryWrapperX<FieldDO>()
                .eq(FieldDO::getName, name)
                .eq(FieldDO::getTenantId, tenantId)
                .eq(FieldDO::getDeleted, false));
        if (byName.isEmpty()) {
            return null;
        }
        if (byName.size() > 1) {
            throw new ServiceException(500, "字段「" + name + "」有多条，对不上");
        }
        return byName.get(0);
    }

    private static String preferredCode(String name) {
        return switch (name) {
            case AlarmTypeCatalog.FIELD_LEVEL -> "FLD-ALM-002";
            case AlarmTypeCatalog.FIELD_CONTENT -> "FLD-ALM-005";
            case AlarmTypeCatalog.FIELD_HANDLING -> "FLD-ALM-006";
            default -> null;
        };
    }

    private static String mappedOption(String value) {
        return switch (value) {
            case "严重告警", "CRITICAL", "紧急" -> "紧急";
            case "异常告警", "重要" -> "重要";
            case "一般告警", "WARNING", "一般" -> "一般";
            case "提示告警", "提示" -> "提示";
            case "已关闭", "已处理" -> "已处理";
            default -> null;
        };
    }

    private static List<String> optionValues(String options) {
        List<String> values = new java.util.ArrayList<>();
        int cursor = 0;
        while (cursor < options.length()) {
            int key = options.indexOf("\"value\"", cursor);
            if (key < 0) {
                break;
            }
            int colon = options.indexOf(':', key);
            int quote = options.indexOf('"', colon + 1);
            int end = options.indexOf('"', quote + 1);
            if (colon < 0 || quote < 0 || end < 0) {
                break;
            }
            values.add(options.substring(quote + 1, end));
            cursor = end + 1;
        }
        return values;
    }

    /** 原因、后果、联动、允许响应时间。长文本只给搜索，不进筛选。已挂上的列只改显示名和说明。 */
    private void attachHelpText(String fieldCode, String name) {
        EntityTypeBaseFieldDO attached = baseFieldMapper.selectByEntityTypeCodeAndFieldCode(
                AlarmTypeCatalog.DIRECTORY, fieldCode);
        if (attached != null) {
            FieldDO libraryField = fieldMapper.selectByCode(fieldCode);
            String description = libraryField == null ? name : libraryField.getDescription();
            if (name.equals(attached.getFieldName()) && java.util.Objects.equals(description, attached.getDescription())) {
                return;
            }
            EntityTypeBaseFieldSaveReqVO update = new EntityTypeBaseFieldSaveReqVO();
            update.setId(attached.getId());
            update.setEntityTypeCode(AlarmTypeCatalog.DIRECTORY);
            update.setLibraryFieldId(attached.getLibraryFieldId());
            update.setFieldName(name);
            update.setDescription(description);
            entityTypeBaseFieldService.updateBaseField(update);
            return;
        }
        FieldDO libraryField = fieldMapper.selectByCode(fieldCode);
        if (libraryField == null || libraryField.getId() == null) {
            throw new ServiceException(400, "字段库缺少 " + fieldCode);
        }
        EntityTypeBaseFieldSaveReqVO request = new EntityTypeBaseFieldSaveReqVO();
        request.setEntityTypeCode(AlarmTypeCatalog.DIRECTORY);
        request.setLibraryFieldId(libraryField.getId());
        request.setRequired(false);
        request.setIsSearchable(true);
        request.setIsFilterable(false);
        request.setIsSortable(false);
        request.setFieldName(name);
        request.setDescription(libraryField.getDescription());
        entityTypeBaseFieldService.createBaseField(request);
    }

    private void attachBaseField(String fieldCode, String name) {
        if (baseFieldMapper.selectByEntityTypeCodeAndFieldCode(AlarmTypeCatalog.DIRECTORY, fieldCode) != null) {
            return;
        }
        FieldDO libraryField = fieldMapper.selectByCode(fieldCode);
        if (libraryField == null || libraryField.getId() == null) {
            throw new ServiceException(400, "字段库缺少 " + fieldCode);
        }
        EntityTypeBaseFieldSaveReqVO request = new EntityTypeBaseFieldSaveReqVO();
        request.setEntityTypeCode(AlarmTypeCatalog.DIRECTORY);
        request.setLibraryFieldId(libraryField.getId());
        request.setRequired(false);
        request.setIsSearchable(false);
        request.setIsFilterable(true);
        request.setIsSortable(false);
        request.setFieldName(name);
        request.setDescription(name);
        entityTypeBaseFieldService.createBaseField(request);
    }

    private void ensureModel() {
        ModelDO named = modelMapper.selectByNameAndEntityTypeCode(
                AlarmTypeCatalog.MODEL_NAME, AlarmTypeCatalog.DIRECTORY);
        if (named != null) {
            baseFieldLibrarySyncService.assignAllBaseFieldsToModel(named.getId());
            return;
        }
        ModelCreateReqVO request = new ModelCreateReqVO();
        request.setName(AlarmTypeCatalog.MODEL_NAME);
        request.setEntityTypeCode(AlarmTypeCatalog.DIRECTORY);
        request.setStatus(1);
        request.setGovernanceStatus("COMPANY");
        request.setDescription("一条告警记录");
        Long modelId = modelService.createModel(request);
        baseFieldLibrarySyncService.assignAllBaseFieldsToModel(modelId);
    }

    private void ensureTypes() {
        CategoryTypeDO categoryType = categoryTypeMapper.selectByCategoryTypeCode(AlarmTypeCatalog.DIRECTORY);
        if (categoryType == null || categoryType.getTopLevelCategoryId() == null) {
            throw new ServiceException(500, "告警目录还没有分类根");
        }
        Map<String, Long> ids = new LinkedHashMap<>();
        for (AlarmTypeCatalog.Node node : AlarmTypeCatalog.nodes()) {
            if (node.parentCode() != null) {
                continue;
            }
            ids.put(node.code(), ensureType(node, categoryType.getTopLevelCategoryId()));
        }
        for (AlarmTypeCatalog.Node node : AlarmTypeCatalog.nodes()) {
            if (node.parentCode() == null) {
                continue;
            }
            Long parentId = ids.get(node.parentCode());
            if (parentId == null) {
                parentId = categoryId(node.parentCode());
            }
            ensureType(node, parentId);
        }
    }

    private Long ensureType(AlarmTypeCatalog.Node node, Long parentId) {
        CategoryDO existing = findType(node.code());
        if (existing != null && existing.getId() != null) {
            return existing.getId();
        }
        CategoryCreateReqVO request = new CategoryCreateReqVO();
        request.setCategoryTypeCode(AlarmTypeCatalog.DIRECTORY);
        request.setParentId(parentId);
        request.setCode(node.code());
        request.setName(node.name());
        request.setSort(node.sort());
        request.setStatus(1);
        request.setDescription(node.name());
        return categoryService.createCategory(request);
    }

    private CategoryDO findType(String code) {
        List<CategoryDO> rows = categoryMapper.selectByCategoryTypeCode(AlarmTypeCatalog.DIRECTORY);
        if (rows == null) {
            return null;
        }
        for (CategoryDO row : rows) {
            if (row != null && code.equals(row.getCode())) {
                return row;
            }
        }
        return null;
    }

    private void ensureModelAdmin() {
        DmModelTabCategoryRespVO current = dmModelTabCategoryService.getByEntityTypeCode(AlarmTypeCatalog.DIRECTORY);
        boolean hasList = current.getModelListPropsId() != null && current.getModelListPropsId() > 0;
        boolean hasTree = current.getPropsId() != null && current.getPropsId() > 0;
        if (hasList && hasTree) {
            return;
        }
        Long listId = hasList ? current.getModelListPropsId() : createListProps();
        Long treeId = hasTree ? current.getPropsId() : createTreeProps();
        DmModelTabCategorySaveReqVO save = new DmModelTabCategorySaveReqVO();
        save.setEntityTypeCode(AlarmTypeCatalog.DIRECTORY);
        save.setEnabled(false);
        save.setLabel("");
        save.setCategoryTypeCode(AlarmTypeCatalog.DIRECTORY);
        save.setPropsId(treeId);
        save.setModelListPropsId(listId);
        dmModelTabCategoryService.save(save);
    }

    private Long createListProps() {
        ComponentPropsCreateTemplateReqDTO request = new ComponentPropsCreateTemplateReqDTO();
        request.setComponentCode("list");
        request.setSchemaVersion("list@1");
        request.setName("模型管理列表-" + AlarmTypeCatalog.DIRECTORY);
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("emptyText", "暂无模型");
        props.put("displayContent", List.of("name"));
        request.setProps(props);
        request.setDataSource(dataSource("dynamic", "model"));
        return createTemplate(request, "型号列表");
    }

    private Long createTreeProps() {
        ComponentPropsCreateTemplateReqDTO request = new ComponentPropsCreateTemplateReqDTO();
        request.setComponentCode("tree");
        request.setSchemaVersion("tree@1");
        request.setName("dm-tree:" + AlarmTypeCatalog.DIRECTORY + ":分类");
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("selectMode", "single");
        props.put("emptyText", "暂无分类");
        props.put("displayContent", List.of("name"));
        request.setProps(props);
        request.setDataSource(dataSource("category", "entity"));
        return createTemplate(request, "分类树");
    }

    private Long createTemplate(ComponentPropsCreateTemplateReqDTO request, String what) {
        try {
            Long id = componentPropsApi.createTemplate(request).getCheckedData();
            if (id == null || id <= 0) {
                throw new ServiceException(500, "告警目录的" + what + "展示配置没有返回编号");
            }
            return id;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            String detail = ex.getMessage() == null ? "未知错误" : ex.getMessage();
            throw new ServiceException(500, "告警目录的" + what + "展示配置没有写成（" + detail + "）");
        }
    }

    private static ComponentDataSourceDTO dataSource(String businessCategory, String dataKind) {
        ComponentDataSourceDTO dto = new ComponentDataSourceDTO();
        dto.setBusinessCategory(businessCategory);
        dto.setEntityTypeCode(AlarmTypeCatalog.DIRECTORY);
        dto.setDataKind(dataKind);
        return dto;
    }
}
