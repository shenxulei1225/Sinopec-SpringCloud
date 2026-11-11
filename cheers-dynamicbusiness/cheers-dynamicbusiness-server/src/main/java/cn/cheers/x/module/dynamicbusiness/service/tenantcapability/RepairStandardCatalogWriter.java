package cn.cheers.x.module.dynamicbusiness.service.tenantcapability;

import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.service.dynamictable.DynamicTableService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.BaseFieldLibrarySyncService;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 刚创建报修目录时，写上标准字段，并按本租户已有的设备型号各建一种报修类型。
 *
 * 管什么：目录第一次插入后的字段、基础字段、报修类型、专用表；以及新建设备型号时补上对应的那一种报修类型。
 * 不管什么：已经有报修目录之后再开通。缺的报修类型不再扫一遍补。
 * 禁止：启动、查询、打开报修页、再次保存开通时调用。
 */
@Component
public class RepairStandardCatalogWriter {

    /** 报修数据目录。页面和写入都用这个编码。 */
    public static final String CATALOG = "maintanance";

    /** 设备目录。报修类型按这里的型号来建。 */
    public static final String EQUIPMENT = "equipment";

    private static final long TEMPLATE_TENANT_ID = 1L;

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private DynamicTableService dynamicTableService;
    @Resource
    private EntityTypeBaseFieldMapper baseFieldMapper;
    @Resource
    private BaseFieldLibrarySyncService baseFieldLibrarySyncService;

    /**
     * 报修类型编码。页面用同一规则，按设备型号编号找到报修类型。
     * 编号来自设备型号自己的 id，不抄别的租户的型号名称。
     */
    public static String repairModelCode(long equipmentModelId) {
        return "eqm-" + equipmentModelId;
    }

    /**
     * 只在目标租户刚刚插入报修目录行之后调用。
     * 标准字段来自模板租户的报修基础字段。报修类型来自这个租户自己的设备型号。
     */
    public void writeForNewCatalog(Long tenantId) {
        if (tenantId == null || tenantId <= 0 || tenantId == TEMPLATE_TENANT_ID) {
            return;
        }
        alignIdSequences();
        copyFieldLibrary(tenantId);
        copyBaseFields(tenantId);
        createRepairModelsFromEquipment(tenantId);
        createPhysicalColumns(tenantId);
    }

    /**
     * 只在刚插入一台设备型号之后调用。
     * 这个租户还没有报修目录时不写。已经有同编码报修类型时不改。
     */
    public void writeForNewEquipmentModel(Long equipmentModelId, String name) {
        if (equipmentModelId == null || equipmentModelId <= 0 || name == null || name.isBlank()) {
            return;
        }
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null || tenantId <= 0) {
            return;
        }
        Integer catalogs = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*) FROM dynamicbusiness.dynamic_entity_type
                        WHERE tenant_id = ? AND code = ? AND deleted = false
                        """,
                Integer.class, tenantId, CATALOG);
        if (catalogs == null || catalogs == 0) {
            return;
        }
        Long createdId = insertRepairModel(tenantId, equipmentModelId, name.trim(), 0);
        if (createdId == null) {
            return;
        }
        baseFieldLibrarySyncService.assignAllBaseFieldsToModel(createdId);
    }

    /** 序号落后时，下一次插入会撞上已有编号。创建前把序号拨到当前最大编号。 */
    private void alignIdSequences() {
        jdbcTemplate.queryForObject(
                "SELECT setval('dynamicbusiness.dynamic_field_id_seq', (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.dynamic_field))",
                Long.class);
        jdbcTemplate.queryForObject(
                "SELECT setval('dynamicbusiness.dynamic_model_id_seq', (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.dynamic_model))",
                Long.class);
        jdbcTemplate.queryForObject(
                "SELECT setval('dynamicbusiness.dynamic_model_field_assignment_id_seq', (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.dynamic_model_field_assignment))",
                Long.class);
        jdbcTemplate.queryForObject(
                "SELECT setval('dynamicbusiness.dynamic_entity_type_base_field_id_seq', (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.dynamic_entity_type_base_field))",
                Long.class);
    }

    private void copyFieldLibrary(Long tenantId) {
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
                tenantId, TEMPLATE_TENANT_ID, TEMPLATE_TENANT_ID, CATALOG, tenantId);
    }

    private void copyBaseFields(Long tenantId) {
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
                tenantId, tenantId, TEMPLATE_TENANT_ID, CATALOG, tenantId);
    }

    /** 本租户每一种设备型号，建一种同名报修类型。已经有同编码的不改。 */
    private void createRepairModelsFromEquipment(Long tenantId) {
        List<Map<String, Object>> equipmentModels = jdbcTemplate.queryForList(
                """
                        SELECT id, name, COALESCE(sort, 0) AS sort
                        FROM dynamicbusiness.dynamic_model
                        WHERE tenant_id = ? AND entity_type_code = ? AND deleted = false
                        """,
                tenantId, EQUIPMENT);
        List<Long> createdIds = new ArrayList<>();
        for (Map<String, Object> row : equipmentModels) {
            Object idValue = row.get("id");
            Object nameValue = row.get("name");
            if (!(idValue instanceof Number) || nameValue == null) {
                continue;
            }
            int sort = row.get("sort") instanceof Number number ? number.intValue() : 0;
            Long createdId = insertRepairModel(
                    tenantId, ((Number) idValue).longValue(), String.valueOf(nameValue), sort);
            if (createdId != null) {
                createdIds.add(createdId);
            }
        }
        if (createdIds.isEmpty()) {
            return;
        }
        TenantUtils.execute(tenantId, () -> {
            for (Long createdId : createdIds) {
                baseFieldLibrarySyncService.assignAllBaseFieldsToModel(createdId);
            }
        });
    }

    /** 插入一种报修类型。同编码已在时返回空，调用方不改原行。 */
    private Long insertRepairModel(Long tenantId, long equipmentModelId, String name, int sort) {
        String code = repairModelCode(equipmentModelId);
        String trimmed = name.length() > 200 ? name.substring(0, 200) : name;
        Integer exists = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*) FROM dynamicbusiness.dynamic_model
                        WHERE tenant_id = ? AND code = ? AND deleted = false
                        """,
                Integer.class, tenantId, code);
        if (exists != null && exists > 0) {
            return null;
        }
        return jdbcTemplate.queryForObject(
                """
                        INSERT INTO dynamicbusiness.dynamic_model (
                            code, name, entity_type_code, description, status, sort,
                            creator, create_time, updater, update_time, deleted, tenant_id,
                            governance_status)
                        VALUES (?, ?, ?, NULL, 1, ?, 'pack-open', CURRENT_TIMESTAMP, 'pack-open',
                                CURRENT_TIMESTAMP, false, ?, 'COMPANY')
                        RETURNING id
                        """,
                Long.class, code, trimmed, CATALOG, sort, tenantId);
    }

    /** 专用表和基础字段列与目录一起创建。表或列已经在时，创建方法自己跳过。 */
    private void createPhysicalColumns(Long tenantId) {
        String table = "ent_" + CATALOG + "_t" + tenantId;
        TenantUtils.execute(tenantId, () -> {
            dynamicTableService.createDynamicTableForEntityType(CATALOG, table, "报修");
            for (EntityTypeBaseFieldDO field : baseFieldMapper.selectByEntityTypeCode(CATALOG)) {
                dynamicTableService.addColumn(table, field);
            }
        });
    }
}
