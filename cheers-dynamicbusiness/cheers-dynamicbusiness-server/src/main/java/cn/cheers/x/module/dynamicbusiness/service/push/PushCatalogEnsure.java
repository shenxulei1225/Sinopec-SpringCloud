package cn.cheers.x.module.dynamicbusiness.service.push;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmModelTabCategoryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmModelTabCategorySaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.group.GroupDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.group.GroupMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.enums.group.GroupTypeEnum;
import cn.cheers.x.module.dynamicbusiness.service.group.GroupService;
import cn.cheers.x.module.dynamicbusiness.service.datamgmt.DmModelTabCategoryService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRpcFacadeService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.BaseFieldLibrarySyncService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeBaseFieldService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeService;
import cn.cheers.x.module.dynamicbusiness.service.field.FieldService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import cn.cheers.x.module.platformresource.api.component.ComponentPropsApi;
import cn.cheers.x.module.platformresource.api.component.dto.ComponentDataSourceDTO;
import cn.cheers.x.module.platformresource.api.component.dto.ComponentPropsCreateTemplateReqDTO;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 准备事件主题目录和事件订阅目录。
 * 服务启动时为每个租户准备一次。页面连接第一次订阅时，缺了再补。
 * 已经按旧编码建过的目录，改成事件主题、事件订阅，分组改成「事件」。不另建一本。
 * 目录、字段、基础字段、型号、型号列表都走数据管理的创建接口。
 * 打开数据管理页面不调用这里。
 * 不负责投递，也不在发消息时被查询。
 * 不直接插入字段行，也不指定字段编码。物理表名保持创建时那张，避免把已有行搬表。
 */
@Service
public class PushCatalogEnsure {

    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private EntityTypeService entityTypeService;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private FieldService fieldService;
    @Resource
    private EntityTypeBaseFieldMapper baseFieldMapper;
    @Resource
    private EntityTypeBaseFieldService entityTypeBaseFieldService;
    @Resource
    private DmModelTabCategoryService dmModelTabCategoryService;
    @Resource
    private ComponentPropsApi componentPropsApi;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private ModelService modelService;
    @Resource
    private BaseFieldLibrarySyncService baseFieldLibrarySyncService;
    @Resource
    private EntityService entityService;
    @Resource
    private EntityRpcFacadeService entityRpcFacadeService;
    @Resource
    private GroupMapper groupMapper;
    @Resource
    private GroupService groupService;
    @Resource
    private JdbcTemplate jdbcTemplate;

    @Transactional(rollbackFor = Exception.class)
    public void ensure() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            throw new ServiceException(401, "页面连接没有用户，推送目录没有准备");
        }
        adoptConfirmedCatalogNames();
        ensureTopicCatalog();
        ensureSubscriptionCatalog();
        ensureModelAdmin(PushCatalogCodes.TOPIC_TYPE);
        ensureModelAdmin(PushCatalogCodes.SUBSCRIPTION_TYPE);
        seedTopicKinds();
        ensureDetailFollow();
    }

    /**
     * 按数据管理里看到的列名找回字段编码。
     * 编码由字段创建生成。这里不猜编码。缺了就报错。
     */
    public String fieldCode(String name) {
        FieldDO field = fieldMapper.selectByName(name, TenantContextHolder.getRequiredTenantId());
        if (field == null || !StringUtils.hasText(field.getCode())) {
            throw new ServiceException(500, "推送目录缺少字段「" + name + "」");
        }
        if (!"TEXT".equalsIgnoreCase(field.getType())) {
            throw new ServiceException(400, "字段「" + name + "」已经存在，但不是文本");
        }
        return field.getCode();
    }

    /**
     * 按列名找回多选引用的字段编码。详情栏用它列出订了哪些事件主题。
     */
    public String refFieldCode(String name) {
        FieldDO field = fieldMapper.selectByName(name, TenantContextHolder.getRequiredTenantId());
        if (field == null || !StringUtils.hasText(field.getCode())) {
            throw new ServiceException(500, "推送目录缺少字段「" + name + "」");
        }
        if (!"REF_Multi".equalsIgnoreCase(field.getType())) {
            throw new ServiceException(400, "字段「" + name + "」已经存在，但不是多选引用");
        }
        return field.getCode();
    }

    public boolean subscriptionCatalogExists() {
        return entityTypeMapper.selectByCode(PushCatalogCodes.SUBSCRIPTION_TYPE) != null;
    }

    public Long subscriptionModelId() {
        ModelDO model = modelMapper.selectByNameAndEntityTypeCode(
                PushCatalogCodes.SUBSCRIPTION_MODEL, PushCatalogCodes.SUBSCRIPTION_TYPE);
        if (model == null || model.getId() == null) {
            throw new ServiceException(500, "事件订阅目录还没有型号");
        }
        return model.getId();
    }

    private void ensureTopicCatalog() {
        ensureCatalog(PushCatalogCodes.TOPIC_TYPE, PushCatalogCodes.TOPIC_CATALOG_NAME, "固定的几种事件名字，不为每台设备建一行");
        String patternCode = ensureTextField(PushCatalogCodes.NAME_PATTERN);
        attachBaseField(PushCatalogCodes.TOPIC_TYPE, patternCode, PushCatalogCodes.NAME_PATTERN);
        ensureModel(PushCatalogCodes.TOPIC_TYPE, PushCatalogCodes.TOPIC_MODEL, "主题种类");
    }

    private void ensureSubscriptionCatalog() {
        ensureCatalog(PushCatalogCodes.SUBSCRIPTION_TYPE, PushCatalogCodes.SUBSCRIPTION_CATALOG_NAME, "当前有哪些连接正在听哪个主题");
        String clientCode = ensureTextField(PushCatalogCodes.CLIENT_ID);
        String topicCode = ensureTextField(PushCatalogCodes.TOPIC_NAME);
        String listenerCode = ensureTextField(PushCatalogCodes.LISTENER_USER_ID);
        String originCode = ensureTextField(PushCatalogCodes.ORIGIN_TEXT);
        String topicRefCode = ensureTopicRefField();
        attachBaseField(PushCatalogCodes.SUBSCRIPTION_TYPE, clientCode, PushCatalogCodes.CLIENT_ID);
        attachBaseField(PushCatalogCodes.SUBSCRIPTION_TYPE, topicCode, PushCatalogCodes.TOPIC_NAME);
        attachBaseField(PushCatalogCodes.SUBSCRIPTION_TYPE, listenerCode, PushCatalogCodes.LISTENER_USER_ID);
        attachBaseField(PushCatalogCodes.SUBSCRIPTION_TYPE, originCode, PushCatalogCodes.ORIGIN_TEXT);
        attachBaseField(PushCatalogCodes.SUBSCRIPTION_TYPE, topicRefCode, PushCatalogCodes.SUBSCRIBED_TOPICS);
        ensureModel(PushCatalogCodes.SUBSCRIPTION_TYPE, PushCatalogCodes.SUBSCRIPTION_MODEL, "当前订阅");
    }

    private void seedTopicKinds() {
        ModelDO model = modelMapper.selectByNameAndEntityTypeCode(
                PushCatalogCodes.TOPIC_MODEL, PushCatalogCodes.TOPIC_TYPE);
        if (model == null || model.getId() == null) {
            throw new ServiceException(500, "事件主题目录还没有型号");
        }
        for (PushTopicKinds.Kind kind : PushTopicKinds.ALL) {
            EntityRespDTO existing = entityRpcFacadeService.getByCode(kind.code(), PushCatalogCodes.TOPIC_TYPE);
            if (existing != null) {
                continue;
            }
            EntityCreateReqVO request = new EntityCreateReqVO();
            request.setRegistryCode(PushCatalogCodes.TOPIC_TYPE);
            Map<String, Object> baseFields = new LinkedHashMap<>();
            baseFields.put("entityTypeCode", PushCatalogCodes.TOPIC_TYPE);
            baseFields.put("modelId", model.getId());
            baseFields.put("name", kind.name());
            baseFields.put("code", kind.code());
            baseFields.put("status", 1);
            request.setBaseFields(baseFields);
            Map<String, Object> fields = new LinkedHashMap<>();
            fields.put(fieldCode(PushCatalogCodes.NAME_PATTERN), kind.namePattern());
            request.setCustomFields(fields);
            entityService.create(request);
        }
    }

    /**
     * 数据目录里已经有的旧编码改到确认后的名称。
     * 分组从「事件分组」改成「事件」。两本目录改成事件主题、事件订阅。
     * 物理表不改名。表里的类型编码跟着目录编码改，打开原来的数据仍在这本目录下。
     */
    private void adoptConfirmedCatalogNames() {
        adoptGroupName();
        adoptCatalog(
                PushCatalogCodes.LEGACY_TOPIC_TYPE,
                PushCatalogCodes.TOPIC_TYPE,
                PushCatalogCodes.LEGACY_TOPIC_NAME,
                PushCatalogCodes.TOPIC_CATALOG_NAME,
                "固定的几种事件名字，不为每台设备建一行");
        adoptCatalog(
                PushCatalogCodes.LEGACY_SUBSCRIPTION_TYPE,
                PushCatalogCodes.SUBSCRIPTION_TYPE,
                PushCatalogCodes.LEGACY_SUBSCRIPTION_NAME,
                PushCatalogCodes.SUBSCRIPTION_CATALOG_NAME,
                "当前有哪些连接正在听哪个主题");
        replaceEmbeddedCatalogText();
    }

    private void adoptGroupName() {
        String groupType = GroupTypeEnum.ENTITY_TYPE.getCode();
        GroupDO legacy = groupMapper.selectByTypeAndName(groupType, PushCatalogCodes.LEGACY_GROUP_NAME);
        if (legacy == null || legacy.getId() == null) {
            return;
        }
        if (groupMapper.selectByTypeAndName(groupType, PushCatalogCodes.GROUP_NAME) != null) {
            return;
        }
        groupService.updateGroup(
                groupType,
                legacy.getId(),
                PushCatalogCodes.GROUP_NAME,
                legacy.getDescription(),
                legacy.getParentId(),
                legacy.getSort(),
                legacy.getStatus());
    }

    private void adoptCatalog(String legacyCode, String code, String legacyName, String name, String description) {
        if (entityTypeMapper.selectByCode(code) != null) {
            alignCatalogText(code, name, description);
            return;
        }
        if (entityTypeMapper.selectByCode(legacyCode) == null) {
            return;
        }
        String table = jdbcTemplate.query(
                "SELECT dedicated_table_name FROM dynamicbusiness.dynamic_entity_type WHERE code = ? AND deleted = false",
                rs -> rs.next() ? rs.getString(1) : null,
                legacyCode);
        replaceCodeColumn("dynamicbusiness.dynamic_model", "entity_type_code", legacyCode, code);
        replaceCodeColumn("dynamicbusiness.dynamic_entity_type_base_field", "entity_type_code", legacyCode, code);
        replaceCodeColumn("dynamicbusiness.dm_data_tab_layout", "entity_type_code", legacyCode, code);
        replaceCodeColumn("dynamicbusiness.dm_catalog_orchestration", "entity_type_code", legacyCode, code);
        replaceCodeColumn("dynamicbusiness.dm_model_tab_category", "entity_type_code", legacyCode, code);
        replaceCodeColumn("dynamicbusiness.dm_model_tab_category", "category_type_code", legacyCode, code);
        replaceCodeColumn("dynamicbusiness.dm_data_tab_column_relation", "entity_type_code", legacyCode, code);
        replaceCodeColumn("dynamicbusiness.dm_data_tab_column_relation", "from_type_code", legacyCode, code);
        replaceCodeColumn("dynamicbusiness.dm_data_tab_column_relation", "to_type_code", legacyCode, code);
        replaceCodeColumn("dynamicbusiness.capability_component_projection", "entity_type_code", legacyCode, code);
        replaceCodeColumn("dynamicbusiness.business_capability", "entity_type_code", legacyCode, code);
        replaceCodeColumn("dynamicbusiness.dynamic_entity_category_relation_t1", "entity_type_code", legacyCode, code);
        replaceCodeColumn("dynamicbusiness.dynamic_model_category_relation_t1", "entity_type_code", legacyCode, code);
        replaceCodeColumn("dynamicbusiness.model_crud_form_definition", "entity_type_code", legacyCode, code);
        replaceCodeColumn("dynamicbusiness.dynamic_dynamic_table", "entity_type_code", legacyCode, code);
        jdbcTemplate.update(
                "UPDATE dynamicbusiness.dynamic_category SET name = ? WHERE category_type_code = ? AND name = ? AND deleted = false",
                name, legacyCode, legacyName);
        replaceCodeColumn("dynamicbusiness.dynamic_category", "category_type_code", legacyCode, code);
        jdbcTemplate.update(
                "UPDATE dynamicbusiness.dynamic_category_type SET category_type_code = ?, name = ? WHERE category_type_code = ? AND deleted = false",
                code, name, legacyCode);
        if (table != null && table.matches("[a-z][a-z0-9_]*")) {
            replaceCodeColumn("dynamicbusiness." + table, "entity_type_code", legacyCode, code);
        }
        replacePropsText(legacyCode, code, legacyName, name);
        jdbcTemplate.update(
                "UPDATE dynamicbusiness.dynamic_entity_type SET code = ?, name = ?, alias = ?, group_name = ?, description = ? WHERE code = ? AND deleted = false",
                code, name, name, PushCatalogCodes.GROUP_NAME, description, legacyCode);
    }

    private void alignCatalogText(String code, String name, String description) {
        jdbcTemplate.update(
                "UPDATE dynamicbusiness.dynamic_entity_type SET name = ?, alias = ?, group_name = ?, description = ? WHERE code = ? AND deleted = false",
                name, name, PushCatalogCodes.GROUP_NAME, description, code);
        jdbcTemplate.update(
                "UPDATE dynamicbusiness.dynamic_category_type SET name = ? WHERE category_type_code = ? AND deleted = false",
                name, code);
        jdbcTemplate.update(
                "UPDATE dynamicbusiness.dynamic_category SET name = ? WHERE category_type_code = ? AND name IN (?, ?) AND deleted = false",
                name, code, PushCatalogCodes.LEGACY_TOPIC_NAME, PushCatalogCodes.LEGACY_SUBSCRIPTION_NAME);
    }

    private void replaceCodeColumn(String table, String column, String legacyCode, String code) {
        jdbcTemplate.update("UPDATE " + table + " SET " + column + " = ? WHERE " + column + " = ?", code, legacyCode);
    }

    /**
     * 页面布局、分类说明、关联字段里还写着旧目录名时，改成事件主题、事件订阅。
     * 物理表名保持原样。
     */
    private void replaceEmbeddedCatalogText() {
        replaceTextColumn("dynamicbusiness.business_capability", "capability_full");
        replaceTextColumn("dynamicbusiness.capability_component_projection", "component_interface");
        replaceTextColumn("dynamicbusiness.dm_data_tab_column_relation", "from_type_code");
        replaceTextColumn("dynamicbusiness.dm_data_tab_column_relation", "to_type_code");
        replaceTextColumn("dynamicbusiness.dm_data_tab_layout", "column_meta");
        replaceTextColumn("dynamicbusiness.dm_workbench_layout", "name");
        replaceTextColumn("dynamicbusiness.model_crud_form_definition", "crud_form_fields");
        replaceTextColumn("dynamicbusiness.dynamic_category", "code");
        replaceTextColumn("dynamicbusiness.dynamic_category", "parent_code");
        replaceTextColumn("dynamicbusiness.dynamic_category", "description");
        replaceTextColumn("dynamicbusiness.dynamic_category_type", "description");
        replaceTextColumn("dynamicbusiness.dynamic_dynamic_table", "table_comment");
        replaceTextColumn("dynamicbusiness.dynamic_field", "name");
        replaceTextColumn("dynamicbusiness.dynamic_field", "description");
    }

    /**
     * 旧目录名还写在文本或 JSON 里时换成事件主题、事件订阅。
     * JSON 列先按文本替换，再存回 JSON。
     */
    private void replaceTextColumn(String table, String column) {
        String[] parts = table.split("\\.");
        String dataType = jdbcTemplate.queryForObject(
                "SELECT data_type FROM information_schema.columns WHERE table_schema = ? AND table_name = ? AND column_name = ?",
                String.class, parts[0], parts[1], column);
        String source = "jsonb".equals(dataType) ? column + "::text" : column;
        String replaced = "replace(replace(replace(replace(" + source
                + ", 'push_subscription', 'event_subscription'), 'push_topic', 'event_topic'), '推送订阅', '事件订阅'), '推送主题', '事件主题')";
        String assigned = "jsonb".equals(dataType) ? "(" + replaced + ")::jsonb" : replaced;
        jdbcTemplate.update(
                "UPDATE " + table + " SET " + column + " = " + assigned
                        + " WHERE " + source + " LIKE '%push_topic%' OR " + source + " LIKE '%push_subscription%' OR "
                        + source + " LIKE '%推送主题%' OR " + source + " LIKE '%推送订阅%'");
    }

    private void replacePropsText(String legacyCode, String code, String legacyName, String name) {
        jdbcTemplate.update(
                "UPDATE platformresource.pr_component_props SET "
                        + "data_source = replace(replace(data_source, ?, ?), ?, ?), "
                        + "props = replace(replace(props, ?, ?), ?, ?), "
                        + "props_override = replace(replace(props_override, ?, ?), ?, ?) "
                        + "WHERE data_source LIKE ? OR props LIKE ? OR props_override LIKE ?",
                legacyCode, code, legacyName, name,
                legacyCode, code, legacyName, name,
                legacyCode, code, legacyName, name,
                "%" + legacyCode + "%", "%" + legacyCode + "%", "%" + legacyCode + "%");
    }

    private void ensureCatalog(String code, String name, String description) {
        if (entityTypeMapper.selectByCode(code) != null) {
            return;
        }
        EntityTypeCreateReqVO request = new EntityTypeCreateReqVO();
        request.setSort(90);
        request.setName(name);
        request.setCode(code);
        request.setAlias(name);
        request.setStatus("active");
        request.setStorageType("DEDICATED");
        request.setEntryKind("NATIVE");
        request.setWorkScope("NETWORK");
        request.setGroupName(PushCatalogCodes.GROUP_NAME);
        request.setDescription(description);
        request.setAssociationFields("{}");
        entityTypeService.create(request);
    }

    /**
     * 订阅方指向事件主题的多选引用。详情栏用它列出订了哪些主题。
     * 同名字段已经在字段库里，就用它，不再建第二列。
     */
    private String ensureTopicRefField() {
        FieldDO existing = fieldMapper.selectByName(
                PushCatalogCodes.SUBSCRIBED_TOPICS, TenantContextHolder.getRequiredTenantId());
        if (existing != null && StringUtils.hasText(existing.getCode())) {
            pointTopicRef(existing);
            return existing.getCode();
        }
        FieldCreateReqVO request = new FieldCreateReqVO();
        request.setName(PushCatalogCodes.SUBSCRIBED_TOPICS);
        request.setType("REF_Multi");
        request.setSource("CUSTOM");
        request.setStatus(1);
        request.setDescription("这个订阅方正在听的事件主题");
        request.setTargetEntityType(PushCatalogCodes.TOPIC_TYPE);
        request.setTargetEntityTypeName(PushCatalogCodes.TOPIC_CATALOG_NAME);
        Long fieldId = fieldService.createField(request);
        FieldDO created = fieldMapper.selectById(fieldId);
        if (created == null || !StringUtils.hasText(created.getCode())) {
            throw new ServiceException(500, "字段「订阅的主题」创建后没有编码");
        }
        pointTopicRef(created);
        return created.getCode();
    }

    /**
     * 引用指向事件主题。详情栏靠这个声明列出订了哪些主题。
     */
    private void pointTopicRef(FieldDO field) {
        String provider = "dynamic-entity:" + PushCatalogCodes.TOPIC_TYPE;
        if (provider.equals(field.getProviderCode())) {
            return;
        }
        FieldDO update = new FieldDO();
        update.setId(field.getId());
        update.setProviderCode(provider);
        fieldMapper.updateById(update);
    }

    /**
     * 数据页点中订阅方后，详情栏跟这一行。
     * 没有这条跟随线时，详情栏一直是空提示。
     */
    private void ensureDetailFollow() {
        jdbcTemplate.update(
                "INSERT INTO dynamicbusiness.dm_data_tab_column_relation "
                        + "(entity_type_code, edge_id, from_column_identity, to_column_identity, relation_kind, "
                        + "from_type_code, to_type_code, relation_meta, tenant_id, layout_id, deleted, creator) "
                        + "SELECT e.entity_type_code, 'edge-subscriber-detail-' || e.layout_id, "
                        + "'ENTITY:' || e.tab_id, 'DETAIL:' || d.tab_id, 'ENTITY_DETAIL', "
                        + "e.entity_type_code, e.entity_type_code, "
                        + "'{\"edgeAction\":\"detail_follow\",\"enabledInteractions\":[]}'::jsonb, "
                        + "e.tenant_id, e.layout_id, false, '1' "
                        + "FROM dynamicbusiness.dm_data_tab_layout e "
                        + "JOIN dynamicbusiness.dm_data_tab_layout d "
                        + "ON d.layout_id = e.layout_id AND d.column_kind = 'DETAIL' AND d.deleted = false "
                        + "AND d.entity_type_code = e.entity_type_code "
                        + "WHERE e.entity_type_code = ? AND e.column_kind = 'ENTITY' AND e.deleted = false "
                        + "AND NOT EXISTS (SELECT 1 FROM dynamicbusiness.dm_data_tab_column_relation r "
                        + "WHERE r.layout_id = e.layout_id AND r.deleted = false "
                        + "AND r.from_column_identity = 'ENTITY:' || e.tab_id "
                        + "AND r.to_column_identity = 'DETAIL:' || d.tab_id)",
                PushCatalogCodes.SUBSCRIPTION_TYPE);
    }

    /**
     * 数据管理里新建文本字段的那一步。来源是自定义，编码由字段服务生成。
     * 同名文本字段已经在字段库里，就用它，不再建第二列。
     */
    private String ensureTextField(String name) {
        FieldDO existing = fieldMapper.selectByName(name, TenantContextHolder.getRequiredTenantId());
        if (existing != null) {
            return fieldCode(name);
        }
        FieldCreateReqVO request = new FieldCreateReqVO();
        request.setName(name);
        request.setType("TEXT");
        request.setSource("CUSTOM");
        request.setStatus(1);
        request.setDescription(name);
        Long fieldId = fieldService.createField(request);
        FieldDO created = fieldMapper.selectById(fieldId);
        if (created == null || !StringUtils.hasText(created.getCode())) {
            throw new ServiceException(500, "字段「" + name + "」创建后没有编码");
        }
        return created.getCode();
    }

    /**
     * 数据管理创建目录之后写下的型号列表和分类树。
     * 两份展示配置都已经有了，就不再建第二份。
     */
    private void ensureModelAdmin(String entityTypeCode) {
        DmModelTabCategoryRespVO current = dmModelTabCategoryService.getByEntityTypeCode(entityTypeCode);
        boolean hasList = current.getModelListPropsId() != null && current.getModelListPropsId() > 0;
        boolean hasTree = current.getPropsId() != null && current.getPropsId() > 0;
        if (hasList && hasTree) {
            return;
        }
        Long listId = hasList ? current.getModelListPropsId() : createListProps(entityTypeCode);
        Long treeId = hasTree ? current.getPropsId() : createTreeProps(entityTypeCode);
        DmModelTabCategorySaveReqVO save = new DmModelTabCategorySaveReqVO();
        save.setEntityTypeCode(entityTypeCode);
        save.setEnabled(false);
        save.setLabel("");
        save.setCategoryTypeCode(entityTypeCode);
        save.setPropsId(treeId);
        save.setModelListPropsId(listId);
        dmModelTabCategoryService.save(save);
    }

    private Long createListProps(String entityTypeCode) {
        ComponentPropsCreateTemplateReqDTO request = new ComponentPropsCreateTemplateReqDTO();
        request.setComponentCode("list");
        request.setSchemaVersion("list@1");
        request.setName("模型管理列表-" + entityTypeCode);
        request.setProps(modelListProps());
        request.setDataSource(dataSource("dynamic", entityTypeCode, "model"));
        return createTemplate(request, "型号列表");
    }

    private Long createTreeProps(String entityTypeCode) {
        ComponentPropsCreateTemplateReqDTO request = new ComponentPropsCreateTemplateReqDTO();
        request.setComponentCode("tree");
        request.setSchemaVersion("tree@1");
        request.setName("dm-tree:" + entityTypeCode + ":" + entityTypeCode + ":分类");
        request.setProps(categoryTreeProps());
        request.setDataSource(dataSource("category", entityTypeCode, "entity"));
        return createTemplate(request, "分类树");
    }

    private Long createTemplate(ComponentPropsCreateTemplateReqDTO request, String what) {
        try {
            Long id = componentPropsApi.createTemplate(request).getCheckedData();
            if (id == null || id <= 0) {
                throw new ServiceException(500, "推送目录的" + what + "展示配置没有返回编号");
            }
            return id;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            String detail = ex.getMessage() == null ? "未知错误" : ex.getMessage();
            throw new ServiceException(500, "推送目录的" + what + "展示配置没有写成（" + detail + "）");
        }
    }

    private static Map<String, Object> modelListProps() {
        Map<String, Object> search = new LinkedHashMap<>();
        search.put("showSearch", true);
        search.put("searchPlaceholder", "搜索…");
        Map<String, Object> title = new LinkedHashMap<>();
        title.put("showTitle", false);
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("emptyText", "暂无模型");
        props.put("search", search);
        props.put("className", "h-full min-h-0 flex-1 border-0 bg-transparent shadow-none");
        props.put("toolbarClassName", "gap-1 border-b border-white/8 bg-transparent px-1.5 py-1");
        props.put("tableClassName", "min-h-0 flex-1");
        props.put("showDataSourceEndpoint", false);
        props.put("selectedPanel", false);
        props.put("selectedPanelDetail", false);
        props.put("toolbarDensity", "compact");
        props.put("title", title);
        props.put("showSelectModeBadge", false);
        props.put("displayContent", List.of("name"));
        return props;
    }

    private static Map<String, Object> categoryTreeProps() {
        Map<String, Object> search = new LinkedHashMap<>();
        search.put("showSearch", true);
        search.put("searchPlaceholder", "搜索分类");
        search.put("searchScopeSelectedOptions", List.of());
        Map<String, Object> expand = new LinkedHashMap<>();
        expand.put("expandAll", false);
        expand.put("expandLevel", 1);
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("selectMode", "single");
        props.put("categoryAssociation", "MULTI");
        props.put("search", search);
        props.put("autoCrud", true);
        props.put("defaultExpandAll", expand);
        props.put("autoLoad", true);
        props.put("emptyText", "暂无分类");
        props.put("displayContent", List.of("name"));
        return props;
    }

    private static ComponentDataSourceDTO dataSource(String businessCategory, String entityTypeCode, String dataKind) {
        ComponentDataSourceDTO dto = new ComponentDataSourceDTO();
        dto.setBusinessCategory(businessCategory);
        dto.setEntityTypeCode(entityTypeCode);
        dto.setDataKind(dataKind);
        return dto;
    }

    private void attachBaseField(String entityTypeCode, String fieldCode, String name) {
        if (baseFieldMapper.selectByEntityTypeCodeAndFieldCode(entityTypeCode, fieldCode) != null) {
            return;
        }
        FieldDO libraryField = fieldMapper.selectByCode(fieldCode);
        if (libraryField == null || libraryField.getId() == null) {
            throw new ServiceException(400, "字段库缺少 " + fieldCode);
        }
        EntityTypeBaseFieldSaveReqVO request = new EntityTypeBaseFieldSaveReqVO();
        request.setEntityTypeCode(entityTypeCode);
        request.setLibraryFieldId(libraryField.getId());
        request.setRequired(false);
        request.setIsSearchable(false);
        request.setIsFilterable(true);
        request.setIsSortable(false);
        request.setFieldName(name);
        request.setDescription(name);
        entityTypeBaseFieldService.createBaseField(request);
    }

    private Long ensureModel(String entityTypeCode, String modelName, String description) {
        ModelDO named = modelMapper.selectByNameAndEntityTypeCode(modelName, entityTypeCode);
        if (named != null) {
            baseFieldLibrarySyncService.assignAllBaseFieldsToModel(named.getId());
            return named.getId();
        }
        ModelCreateReqVO request = new ModelCreateReqVO();
        request.setName(modelName);
        request.setEntityTypeCode(entityTypeCode);
        request.setStatus(1);
        request.setGovernanceStatus("COMPANY");
        request.setDescription(description);
        Long modelId = modelService.createModel(request);
        baseFieldLibrarySyncService.assignAllBaseFieldsToModel(modelId);
        return modelId;
    }
}
