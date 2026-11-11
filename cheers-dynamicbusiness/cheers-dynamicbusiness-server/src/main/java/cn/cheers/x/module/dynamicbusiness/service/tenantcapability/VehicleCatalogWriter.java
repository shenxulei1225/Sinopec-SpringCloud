package cn.cheers.x.module.dynamicbusiness.service.tenantcapability;

import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.service.dynamictable.DynamicTableService;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 刚复制车辆名单、车道或车辆进出目录时，把模板上的标准字段和型号一起写上。
 *
 * 管什么：车辆进出、名单、车道、公司车辆、调度第一次插入后的字段、型号和专用表。
 * 不管什么：目录已经在之后的再次开通。缺的栏不再补。
 * 禁止：启动、查询、打开车辆管理页时调用。
 */
@Component
public class VehicleCatalogWriter {

    static final Set<String> CATALOGS = Set.of(
            "vehicle_pass", "vehicle_list", "vehicle_lane", "company_vehicle", "vehicle_dispatch");

    private static final long TEMPLATE_TENANT_ID = 1L;

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
        Long modelId = jdbcTemplate.query(
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
                              WHERE own.tenant_id = ? AND own.entity_type_code = ? AND own.deleted = false)
                        ORDER BY src.id
                        LIMIT 1
                        RETURNING id
                        """,
                rs -> rs.next() ? rs.getLong(1) : null,
                tenantId, TEMPLATE_TENANT_ID, catalogCode, tenantId, catalogCode);
        if (modelId == null) {
            return;
        }
        jdbcTemplate.update(
                """
                        INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
                            model_id, field_id, required, is_searchable, is_filterable, is_sortable, sort,
                            creator, updater, deleted, tenant_id, model_code, field_code, collection_point,
                            create_visible, edit_visible, detail_visible, field_source)
                        SELECT ?, lib.id, src.required, src.is_searchable, src.is_filterable, src.is_sortable, src.sort,
                               'pack-open', 'pack-open', false, ?, model.code, src.field_code, false,
                               src.create_visible, src.edit_visible, src.detail_visible, src.field_source
                        FROM dynamicbusiness.dynamic_model_field_assignment src
                        JOIN dynamicbusiness.dynamic_model model
                          ON model.tenant_id = ? AND model.entity_type_code = ? AND model.deleted = false
                        JOIN dynamicbusiness.dynamic_field lib
                          ON lib.tenant_id = ? AND lib.code = src.field_code AND lib.deleted = false
                        WHERE src.tenant_id = ? AND src.model_id = model.id AND src.deleted = false
                          AND NOT EXISTS (
                              SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment own
                              WHERE own.tenant_id = ? AND own.model_id = ? AND own.field_code = src.field_code
                                AND own.deleted = false)
                        """,
                modelId, tenantId, TEMPLATE_TENANT_ID, catalogCode, tenantId, TEMPLATE_TENANT_ID, tenantId, modelId);
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
