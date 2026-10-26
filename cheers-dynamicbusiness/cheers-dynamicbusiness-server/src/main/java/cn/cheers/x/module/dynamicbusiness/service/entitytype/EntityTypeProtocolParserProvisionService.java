package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.framework.field.StructuredFieldSemantics;
import cn.cheers.x.module.dynamicbusiness.service.capability.form.ModelCrudFormCacheEvictor;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 目录协议解析能力写入。
 *
 * 负责：开能力时补字段说明 / 指令 JSON / 样例，并写正式用途。字段类型写 JSON。
 * 这三列是型号扩展字段：只挂在报文本身是 JSON 的型号上，不进业务类型基础字段表，
 * 也不随类型继承到每一本型号。航线文件、JSBridge 不分配。
 * 不负责：报文解析算法；把样例升成第四个用户可见用途；把这三列登记成类型基础字段。
 * 认表只问 {@link EntityRepository}，不拼 {@code ent_*}。
 */
@Service
public class EntityTypeProtocolParserProvisionService {

    private record ProtocolSlot(String fieldCode, String name, String semantic, int sort) {
    }

    private static final List<ProtocolSlot> SLOTS = List.of(
            new ProtocolSlot("field_description_json", "字段说明", StructuredFieldSemantics.FIELD_DESCRIPTION, 950),
            new ProtocolSlot("command_json", "指令 JSON", StructuredFieldSemantics.COMMAND_JSON, 951),
            new ProtocolSlot("sample_json", "样例", StructuredFieldSemantics.SAMPLE_JSON, 952)
    );

    /**
     * 官网正文不是 JSON 报文的型号。扩展字段不挂到这些型号上。
     */
    private static final List<String> MODELS_WITHOUT_JSON_EXTENSION = List.of(
            "data_protocol_dji_dock_wayline",
            "data_protocol_dji_pilot_wayline",
            "data_protocol_dji_pilot_jsbridge"
    );

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private EntityRepository entityRepository;

    public void ensureProvisioned(String entityTypeCode) {
        String normalizedTypeCode = normalizeEntityTypeCode(entityTypeCode);
        Long tenantId = getRequiredTenantId();
        ensureEntityTypeExists(tenantId, normalizedTypeCode);
        for (ProtocolSlot slot : SLOTS) {
            ensureFieldLibrary(tenantId, slot);
            retireTypeBaseField(tenantId, normalizedTypeCode, slot);
            upsertExtensionAssignments(tenantId, normalizedTypeCode, slot);
            retireAssignmentsOnNonJsonModels(tenantId, slot);
        }
        ensurePhysicalColumns(normalizedTypeCode);
        evictCrudCache(normalizedTypeCode);
    }

    private void ensureFieldLibrary(Long tenantId, ProtocolSlot slot) {
        jdbcTemplate.update(
                """
                INSERT INTO dynamic_field (
                  code, name, type, unit, description, source, status, max_relations,
                  index_strategy, options, provider_code, semantic_type, tenant_id, creator
                )
                VALUES (?, ?, 'JSON', NULL, ?,
                        'USER', 1, 1, 'NONE', NULL, NULL, ?, ?, 'capability-protocol-parser')
                ON CONFLICT (code, tenant_id) WHERE deleted = false
                DO UPDATE SET
                  name = EXCLUDED.name,
                  type = EXCLUDED.type,
                  description = EXCLUDED.description,
                  source = EXCLUDED.source,
                  semantic_type = EXCLUDED.semantic_type,
                  updater = 'capability-protocol-parser',
                  update_time = NOW()
                """,
                slot.fieldCode(),
                slot.name(),
                slot.name(),
                slot.semantic(),
                tenantId
        );
    }

    /**
     * 从业务类型基础字段表拿掉。留在这张表里就会被全部型号继承，界面归到基础信息。
     * 不改专用表列名，已写入的 JSON 仍留在列上和扩展数据里。
     */
    private void retireTypeBaseField(Long tenantId, String entityTypeCode, ProtocolSlot slot) {
        jdbcTemplate.update(
                """
                UPDATE dynamic_entity_type_base_field
                SET status = 0,
                    deleted = true,
                    updater = 'capability-protocol-parser',
                    update_time = NOW()
                WHERE tenant_id = ?
                  AND entity_type_code = ?
                  AND field_code = ?
                  AND deleted = false
                """,
                tenantId, entityTypeCode, slot.fieldCode()
        );
    }

    /**
     * 扩展字段只挂在具体型号上，来源是扩展，不进基础字段表。
     * JSON 不能搜索、筛选、排序，所以三个开关都关着，不写索引。
     */
    private void upsertExtensionAssignments(Long tenantId, String entityTypeCode, ProtocolSlot slot) {
        jdbcTemplate.update(
                """
                INSERT INTO dynamic_model_field_assignment (
                  model_id, field_id, model_code, field_code,
                  required, is_searchable, is_filterable, is_sortable, sort,
                  default_value, target_entity_type, field_source, tenant_id, creator
                )
                SELECT
                  m.id, f.id, m.code, ?, false, false, false, false, ?,
                  '{}', NULL, 'CUSTOM', m.tenant_id, 'capability-protocol-parser'
                FROM dynamic_model m
                JOIN dynamic_field f
                  ON f.deleted = false AND f.tenant_id = m.tenant_id AND f.code = ?
                WHERE m.deleted = false AND m.tenant_id = ? AND m.entity_type_code = ?
                  AND m.code NOT IN (?, ?, ?)
                ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
                DO UPDATE SET
                  model_id = EXCLUDED.model_id,
                  field_id = EXCLUDED.field_id,
                  field_source = EXCLUDED.field_source,
                  is_searchable = false,
                  is_filterable = false,
                  is_sortable = false,
                  updater = 'capability-protocol-parser',
                  update_time = NOW()
                """,
                slot.fieldCode(), slot.sort(), slot.fieldCode(), tenantId, entityTypeCode,
                MODELS_WITHOUT_JSON_EXTENSION.get(0),
                MODELS_WITHOUT_JSON_EXTENSION.get(1),
                MODELS_WITHOUT_JSON_EXTENSION.get(2)
        );
    }

    /** 航线文件和 JSBridge 不是 JSON 报文，卸掉这三列的型号分配。不删指令上已经写下的正文。 */
    private void retireAssignmentsOnNonJsonModels(Long tenantId, ProtocolSlot slot) {
        jdbcTemplate.update(
                """
                UPDATE dynamic_model_field_assignment
                SET deleted = true,
                    updater = 'capability-protocol-parser',
                    update_time = NOW()
                WHERE tenant_id = ?
                  AND field_code = ?
                  AND deleted = false
                  AND model_code IN (?, ?, ?)
                """,
                tenantId, slot.fieldCode(),
                MODELS_WITHOUT_JSON_EXTENSION.get(0),
                MODELS_WITHOUT_JSON_EXTENSION.get(1),
                MODELS_WITHOUT_JSON_EXTENSION.get(2)
        );
    }

    private void ensurePhysicalColumns(String entityTypeCode) {
        String tableName = entityRepository.requireExistingPhysicalTable(entityTypeCode);
        for (ProtocolSlot slot : SLOTS) {
            jdbcTemplate.execute("""
                    ALTER TABLE dynamicbusiness.%s
                    ADD COLUMN IF NOT EXISTS %s JSONB NOT NULL DEFAULT '{}'::jsonb
                    """.formatted(tableName, slot.fieldCode()));
        }
    }

    private void evictCrudCache(String entityTypeCode) {
        ModelCrudFormCacheEvictor.evictRegistryAndDomainCatalogs(jdbcTemplate, entityTypeCode);
    }

    private void ensureEntityTypeExists(Long tenantId, String entityTypeCode) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM dynamic_entity_type WHERE tenant_id = ? AND code = ? AND deleted = false",
                Integer.class, tenantId, entityTypeCode
        );
        if (count == null || count <= 0) {
            throw new ServiceException(404, "数据目录不存在：" + entityTypeCode);
        }
    }

    private String normalizeEntityTypeCode(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        return entityTypeCode.trim();
    }

    private Long getRequiredTenantId() {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        if (tenantId == null || tenantId <= 0) {
            throw new ServiceException(400, "缺少租户信息，无法启用协议解析能力");
        }
        return tenantId;
    }
}
