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
 * 目录参数定义能力写入。
 *
 * 负责：开能力时补参数定义列并写正式用途 PARAM_SCHEMA。
 * 不负责：参数编辑交互；读路径用列名冒充用途。
 * 认表只问 {@link EntityRepository}，不拼 {@code ent_*}。
 */
@Service
public class EntityTypeParamSchemaProvisionService {

    private static final String FIELD_CODE = "param_slots_json";

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private EntityRepository entityRepository;

    public void ensureProvisioned(String entityTypeCode) {
        String normalizedTypeCode = normalizeEntityTypeCode(entityTypeCode);
        Long tenantId = getRequiredTenantId();
        ensureEntityTypeExists(tenantId, normalizedTypeCode);
        ensureFieldLibrary(tenantId);
        upsertEntityTypeBaseField(tenantId, normalizedTypeCode);
        ensurePhysicalColumns(normalizedTypeCode);
        upsertModelFieldAssignments(tenantId, normalizedTypeCode);
        evictCrudCache(normalizedTypeCode);
    }

    private void ensureFieldLibrary(Long tenantId) {
        jdbcTemplate.update(
                """
                INSERT INTO dynamic_field (
                  code, name, type, unit, description, source, status, max_relations,
                  index_strategy, options, provider_code, semantic_type, tenant_id, creator
                )
                VALUES (?, '参数定义', 'TEXT', NULL, '参数定义结构字段',
                        'BASE', 1, 1, 'NONE', NULL, NULL, ?, ?, 'capability-param-schema')
                ON CONFLICT (code, tenant_id) WHERE deleted = false
                DO UPDATE SET
                  name = EXCLUDED.name,
                  description = EXCLUDED.description,
                  semantic_type = EXCLUDED.semantic_type,
                  updater = 'capability-param-schema',
                  update_time = NOW()
                """,
                FIELD_CODE,
                StructuredFieldSemantics.PARAM_SCHEMA,
                tenantId
        );
    }

    private void upsertEntityTypeBaseField(Long tenantId, String entityTypeCode) {
        jdbcTemplate.update(
                """
                INSERT INTO dynamic_entity_type_base_field (
                  entity_type_code, library_field_id, field_code, field_name, data_type,
                  required, default_value, description, type_config, sort_order,
                  status, tenant_id, creator
                )
                SELECT
                  ?, f.id, ?, '参数定义', f.type,
                  false, '[]', '参数定义结构字段',
                  '{"createVisible":false,"editVisible":false,"detailVisible":false}',
                  920, 1, ?, 'capability-param-schema'
                FROM dynamic_field f
                WHERE f.deleted = false AND f.tenant_id = ? AND f.code = ?
                ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
                DO UPDATE SET
                  library_field_id = EXCLUDED.library_field_id,
                  field_name = EXCLUDED.field_name,
                  data_type = EXCLUDED.data_type,
                  description = EXCLUDED.description,
                  type_config = EXCLUDED.type_config,
                  sort_order = EXCLUDED.sort_order,
                  status = EXCLUDED.status,
                  updater = 'capability-param-schema',
                  update_time = NOW()
                """,
                entityTypeCode, FIELD_CODE, tenantId, tenantId, FIELD_CODE
        );
    }

    private void upsertModelFieldAssignments(Long tenantId, String entityTypeCode) {
        jdbcTemplate.update(
                """
                INSERT INTO dynamic_model_field_assignment (
                  model_id, field_id, model_code, field_code,
                  required, is_searchable, is_filterable, is_sortable, sort,
                  default_value, target_entity_type, field_source, tenant_id, creator
                )
                SELECT
                  m.id, f.id, m.code, ?, false, false, false, false, 920,
                  '[]', NULL, 'BASE', m.tenant_id, 'capability-param-schema'
                FROM dynamic_model m
                JOIN dynamic_field f
                  ON f.deleted = false AND f.tenant_id = m.tenant_id AND f.code = ?
                WHERE m.deleted = false AND m.tenant_id = ? AND m.entity_type_code = ?
                ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
                DO UPDATE SET
                  model_id = EXCLUDED.model_id,
                  field_id = EXCLUDED.field_id,
                  field_source = EXCLUDED.field_source,
                  updater = 'capability-param-schema',
                  update_time = NOW()
                """,
                FIELD_CODE, FIELD_CODE, tenantId, entityTypeCode
        );
    }

    private void ensurePhysicalColumns(String entityTypeCode) {
        String tableName = entityRepository.requireExistingPhysicalTable(entityTypeCode);
        jdbcTemplate.execute("""
                ALTER TABLE dynamicbusiness.%s
                ADD COLUMN IF NOT EXISTS param_slots_json JSONB NOT NULL DEFAULT '[]'::jsonb
                """.formatted(tableName));
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
            throw new ServiceException(400, "缺少租户信息，无法启用参数定义能力");
        }
        return tenantId;
    }
}
