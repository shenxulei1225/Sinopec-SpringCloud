package cn.cheers.x.module.dynamicbusiness.service.tenantcapability;

import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.service.dynamictable.DynamicTableService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 刚复制维抢修目录时，把模板上的标准字段、标准型号和分类一起写上。
 *
 * 管什么：管道、管线构筑物、人员资质、外部施工人员、抢修方案、作业许可、现场作业记录第一次插入后的字段、型号、专用表和分类。管线段是管线目录里的型号，不在这里另建目录。作业许可、人员资质、外部施工人员和现场作业记录归作业管理，抢修方案归抢修，这里只在目录刚插入时抄模板。
 * 不管什么：目录已经在之后的再次开通。缺的栏不再补。应急事件、预案、资源、场站上的字段由迁移写入，不在这里补。
 * 禁止：启动、查询、打开数据管理页时调用。
 */
@Component
public class RepairBaseCatalogWriter {

    static final Set<String> CATALOGS = Set.of(
            "pipeline_pipe",
            "pipeline_structure",
            "person_certificate",
            "external_worker",
            "repair_scheme",
            "work_permit",
            "repair_field_record");

    private static final long TEMPLATE_TENANT_ID = 1L;

    private static final Logger log = LoggerFactory.getLogger(RepairBaseCatalogWriter.class);

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private DynamicTableService dynamicTableService;
    @Resource
    private EntityTypeBaseFieldMapper baseFieldMapper;

    /**
     * 只在目标租户刚刚插入这一本目录之后调用。
     * 模板上还没有这本目录时不写。
     */
    public void writeForNewCatalog(Long tenantId, String catalogCode) {
        if (tenantId == null || tenantId <= 0 || tenantId == TEMPLATE_TENANT_ID || !CATALOGS.contains(catalogCode)) {
            return;
        }
        copyFieldLibrary(tenantId, catalogCode);
        copyBaseFields(tenantId, catalogCode);
        copyModel(tenantId, catalogCode);
        copyCategories(tenantId, catalogCode);
        linkModelToCategories(tenantId, catalogCode);
        createPhysicalColumns(tenantId, catalogCode);
    }

    private void copyFieldLibrary(Long tenantId, String catalogCode) {
        jdbcTemplate.update(
                """
                        INSERT INTO dynamicbusiness.dynamic_field (
                            code, name, type, unit, description, source, status, max_relations,
                            index_strategy, options, provider_code, semantic_type,
                            creator, create_time, updater, update_time, deleted, tenant_id,
                            governance_status, origin_facility_id, creator_user_id)
                        SELECT src.code, src.name, src.type, src.unit, src.description, src.source, src.status,
                               src.max_relations, src.index_strategy, src.options, src.provider_code, src.semantic_type,
                               'pack-open', CURRENT_TIMESTAMP, 'pack-open', CURRENT_TIMESTAMP, false, ?,
                               src.governance_status, NULL, NULL
                        FROM dynamicbusiness.dynamic_field src
                        WHERE src.tenant_id = ? AND src.deleted = false
                          AND src.code IN (
                              SELECT field_code FROM dynamicbusiness.dynamic_entity_type_base_field
                              WHERE tenant_id = ? AND entity_type_code = ? AND deleted = false)
                          AND NOT EXISTS (
                              SELECT 1 FROM dynamicbusiness.dynamic_field own
                              WHERE own.tenant_id = ? AND own.code = src.code AND own.deleted = false)
                        """,
                tenantId, TEMPLATE_TENANT_ID, TEMPLATE_TENANT_ID, catalogCode, tenantId);
    }

    private void copyBaseFields(Long tenantId, String catalogCode) {
        jdbcTemplate.update(
                """
                        INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
                            entity_type_code, field_code, field_name, data_type, required, default_value,
                            description, type_config, sort_order, status, creator, create_time, updater,
                            update_time, deleted, tenant_id, library_field_id, is_searchable, is_filterable, is_sortable)
                        SELECT src.entity_type_code, src.field_code, src.field_name, src.data_type, src.required,
                               src.default_value, src.description, src.type_config, src.sort_order, src.status,
                               'pack-open', CURRENT_TIMESTAMP, 'pack-open', CURRENT_TIMESTAMP, false, ?,
                               lib.id, src.is_searchable, src.is_filterable, src.is_sortable
                        FROM dynamicbusiness.dynamic_entity_type_base_field src
                        JOIN dynamicbusiness.dynamic_field lib
                          ON lib.tenant_id = ? AND lib.code = src.field_code AND lib.deleted = false
                        WHERE src.tenant_id = ? AND src.entity_type_code = ? AND src.deleted = false
                          AND NOT EXISTS (
                              SELECT 1 FROM dynamicbusiness.dynamic_entity_type_base_field own
                              WHERE own.tenant_id = ? AND own.entity_type_code = src.entity_type_code
                                AND own.field_code = src.field_code AND own.deleted = false)
                        """,
                tenantId, tenantId, TEMPLATE_TENANT_ID, catalogCode, tenantId);
    }

    private void copyModel(Long tenantId, String catalogCode) {
        jdbcTemplate.update(
                """
                        INSERT INTO dynamicbusiness.dynamic_model (
                            code, name, entity_type_code, description, status, sort,
                            creator, create_time, updater, update_time, deleted, tenant_id, governance_status)
                        SELECT src.code, src.name, src.entity_type_code, src.description, src.status, src.sort,
                               'pack-open', CURRENT_TIMESTAMP, 'pack-open', CURRENT_TIMESTAMP, false, ?, src.governance_status
                        FROM dynamicbusiness.dynamic_model src
                        WHERE src.tenant_id = ? AND src.entity_type_code = ? AND src.deleted = false
                          AND NOT EXISTS (
                              SELECT 1 FROM dynamicbusiness.dynamic_model own
                              WHERE own.tenant_id = ? AND own.code = src.code AND own.deleted = false)
                        """,
                tenantId, TEMPLATE_TENANT_ID, catalogCode, tenantId);
        // 证书类型的准许作业写在字段默认值上。开通时把这份默认值一起抄过来。
        jdbcTemplate.update(
                """
                        INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
                            model_id, field_id, required, is_searchable, is_filterable, is_sortable, sort,
                            creator, updater, deleted, tenant_id, model_code, field_code, collection_point,
                            create_visible, edit_visible, detail_visible, field_source, default_value)
                        SELECT own_model.id, lib.id, src.required, src.is_searchable, src.is_filterable, src.is_sortable, src.sort,
                               'pack-open', 'pack-open', false, ?, own_model.code, src.field_code, false,
                               src.create_visible, src.edit_visible, src.detail_visible, src.field_source, src.default_value
                        FROM dynamicbusiness.dynamic_model_field_assignment src
                        JOIN dynamicbusiness.dynamic_model src_model
                          ON src_model.id = src.model_id AND src_model.tenant_id = ? AND src_model.deleted = false
                        JOIN dynamicbusiness.dynamic_model own_model
                          ON own_model.tenant_id = ? AND own_model.code = src_model.code AND own_model.deleted = false
                        JOIN dynamicbusiness.dynamic_field lib
                          ON lib.tenant_id = ? AND lib.code = src.field_code AND lib.deleted = false
                        WHERE src.tenant_id = ? AND src_model.entity_type_code = ? AND src.deleted = false
                          AND NOT EXISTS (
                              SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment own
                              WHERE own.tenant_id = ? AND own.model_id = own_model.id AND own.field_code = src.field_code
                                AND own.deleted = false)
                        """,
                tenantId, TEMPLATE_TENANT_ID, tenantId, tenantId, TEMPLATE_TENANT_ID, catalogCode, tenantId);
    }

    /** 分类种类和分类树跟目录一起复制。根分类已经在时，不再插第二棵。 */
    private void copyCategories(Long tenantId, String catalogCode) {
        jdbcTemplate.update(
                """
                        INSERT INTO dynamicbusiness.dynamic_category_type (
                            category_type_code, name, description, status, tenant_id, creator, updater, deleted,
                            category_mode, entity_association_mode)
                        SELECT src.category_type_code, src.name, src.description, src.status, ?,
                               'pack-open', 'pack-open', false, src.category_mode, src.entity_association_mode
                        FROM dynamicbusiness.dynamic_category_type src
                        WHERE src.tenant_id = ? AND src.category_type_code = ? AND src.deleted = false
                          AND NOT EXISTS (
                              SELECT 1 FROM dynamicbusiness.dynamic_category_type own
                              WHERE own.tenant_id = ? AND own.category_type_code = src.category_type_code
                                AND own.deleted = false)
                        """,
                tenantId, TEMPLATE_TENANT_ID, catalogCode, tenantId);
        jdbcTemplate.update(
                """
                        INSERT INTO dynamicbusiness.dynamic_category (
                            parent_id, name, code, category_type_code, level, sort, status, description,
                            creator, updater, deleted, tenant_id, parent_code)
                        SELECT NULL, src.name, src.code, src.category_type_code, src.level, src.sort, src.status,
                               src.description, 'pack-open', 'pack-open', false, ?, src.parent_code
                        FROM dynamicbusiness.dynamic_category src
                        WHERE src.tenant_id = ? AND src.category_type_code = ? AND src.deleted = false
                          AND src.parent_id IS NULL
                          AND NOT EXISTS (
                              SELECT 1 FROM dynamicbusiness.dynamic_category own
                              WHERE own.tenant_id = ? AND own.code = src.code AND own.deleted = false)
                        """,
                tenantId, TEMPLATE_TENANT_ID, catalogCode, tenantId);
        jdbcTemplate.update(
                """
                        INSERT INTO dynamicbusiness.dynamic_category (
                            parent_id, name, code, category_type_code, level, sort, status, description,
                            creator, updater, deleted, tenant_id, parent_code)
                        SELECT parent.id, src.name, src.code, src.category_type_code, src.level, src.sort, src.status,
                               src.description, 'pack-open', 'pack-open', false, ?, src_parent.code
                        FROM dynamicbusiness.dynamic_category src
                        JOIN dynamicbusiness.dynamic_category src_parent
                          ON src_parent.id = src.parent_id AND src_parent.tenant_id = src.tenant_id
                         AND src_parent.deleted = false
                        JOIN dynamicbusiness.dynamic_category parent
                          ON parent.tenant_id = ? AND parent.code = src_parent.code AND parent.deleted = false
                        WHERE src.tenant_id = ? AND src.category_type_code = ? AND src.deleted = false
                          AND src.parent_id IS NOT NULL
                          AND NOT EXISTS (
                              SELECT 1 FROM dynamicbusiness.dynamic_category own
                              WHERE own.tenant_id = ? AND own.code = src.code AND own.deleted = false)
                        """,
                tenantId, tenantId, TEMPLATE_TENANT_ID, catalogCode, tenantId);
        jdbcTemplate.update(
                """
                        UPDATE dynamicbusiness.dynamic_category c
                        SET tree_path = '/' || c.id::text || '/'
                        WHERE c.tenant_id = ? AND c.category_type_code = ? AND c.deleted = false
                          AND c.parent_id IS NULL
                          AND (c.tree_path IS NULL OR btrim(c.tree_path) = '')
                        """,
                tenantId, catalogCode);
        jdbcTemplate.update(
                """
                        UPDATE dynamicbusiness.dynamic_category c
                        SET tree_path = p.tree_path || c.id::text || '/'
                        FROM dynamicbusiness.dynamic_category p
                        WHERE c.parent_id = p.id AND c.tenant_id = ? AND c.category_type_code = ?
                          AND c.deleted = false AND p.deleted = false
                          AND (c.tree_path IS NULL OR btrim(c.tree_path) = '')
                        """,
                tenantId, catalogCode);
        jdbcTemplate.update(
                """
                        UPDATE dynamicbusiness.dynamic_category_type t
                        SET top_level_category_id = c.id
                        FROM dynamicbusiness.dynamic_category c
                        WHERE t.tenant_id = ? AND c.tenant_id = t.tenant_id
                          AND t.category_type_code = ? AND c.category_type_code = t.category_type_code
                          AND c.code = t.category_type_code || '_root'
                          AND t.deleted = false AND c.deleted = false
                          AND t.top_level_category_id IS NULL
                        """,
                tenantId, catalogCode);
    }

    /** 型号挂到这本目录已经有的分类上。分类表按租户分表时，两处都写，缺表则跳过那一张。 */
    private void linkModelToCategories(Long tenantId, String catalogCode) {
        jdbcTemplate.update(
                """
                        INSERT INTO dynamicbusiness.dynamic_model_category_relation (
                            model_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id,
                            model_code, category_code)
                        SELECT model.id, category.id, model.entity_type_code, category.sort,
                               'pack-open', 'pack-open', false, model.tenant_id, model.code, category.code
                        FROM dynamicbusiness.dynamic_model model
                        JOIN dynamicbusiness.dynamic_category category
                          ON category.tenant_id = model.tenant_id
                         AND category.category_type_code = model.entity_type_code
                         AND category.deleted = false
                        WHERE model.tenant_id = ? AND model.entity_type_code = ? AND model.deleted = false
                          AND NOT EXISTS (
                              SELECT 1 FROM dynamicbusiness.dynamic_model_category_relation own
                              WHERE own.tenant_id = model.tenant_id
                                AND own.model_code = model.code
                                AND own.category_code = category.code
                                AND own.entity_type_code = model.entity_type_code
                                AND own.deleted = false)
                        """,
                tenantId, catalogCode);
        String physical = "dynamic_model_category_relation_t" + tenantId;
        String reg = jdbcTemplate.query(
                "SELECT to_regclass(?)",
                rs -> rs.next() ? rs.getString(1) : null,
                "dynamicbusiness." + physical);
        if (reg == null) {
            log.info("[linkModelToCategories][tenant={}] 没有分表 {}，只写了共用关系表", tenantId, physical);
            return;
        }
        jdbcTemplate.update(
                """
                        INSERT INTO dynamicbusiness.%s (
                            model_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id,
                            model_code, category_code)
                        SELECT model.id, category.id, model.entity_type_code, category.sort,
                               'pack-open', 'pack-open', false, model.tenant_id, model.code, category.code
                        FROM dynamicbusiness.dynamic_model model
                        JOIN dynamicbusiness.dynamic_category category
                          ON category.tenant_id = model.tenant_id
                         AND category.category_type_code = model.entity_type_code
                         AND category.deleted = false
                        WHERE model.tenant_id = ? AND model.entity_type_code = ? AND model.deleted = false
                          AND NOT EXISTS (
                              SELECT 1 FROM dynamicbusiness.%s own
                              WHERE own.tenant_id = model.tenant_id
                                AND own.model_code = model.code
                                AND own.category_code = category.code
                                AND own.entity_type_code = model.entity_type_code
                                AND own.deleted = false)
                        """.formatted(physical, physical),
                tenantId, catalogCode);
    }

    /** 专用表和基础字段列与目录一起创建。表或列已经在时，创建方法自己跳过。 */
    private void createPhysicalColumns(Long tenantId, String catalogCode) {
        String table = jdbcTemplate.query(
                """
                        SELECT dedicated_table_name FROM dynamicbusiness.dynamic_entity_type
                        WHERE tenant_id = ? AND code = ? AND deleted = false
                        """,
                rs -> rs.next() ? rs.getString(1) : null,
                tenantId, catalogCode);
        if (table == null || table.isBlank()) {
            return;
        }
        TenantUtils.execute(tenantId, () -> {
            dynamicTableService.createDynamicTableForEntityType(catalogCode, table, catalogCode);
            for (EntityTypeBaseFieldDO field : baseFieldMapper.selectByEntityTypeCode(catalogCode)) {
                dynamicTableService.addColumn(table, field);
            }
        });
    }
}
