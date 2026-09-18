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
 * 目录流程图能力的元数据写入服务。
 *
 * 负责：
 * - 在启用「流程图能力」时，补齐字段库 flow_graph_json；
 * - 补齐目录基础字段定义；
 * - 补齐实体专表物理列与型号字段分配（统一基础字段路径）。
 * 认表只问 {@link EntityRepository}，不拼 {@code ent_*}。
 */
@Service
public class EntityTypeFlowGraphProvisionService {

    private static final String FIELD_CODE_FLOW_GRAPH = "flow_graph_json";

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private EntityRepository entityRepository;

    public void ensureProvisioned(String entityTypeCode) {
        String normalizedTypeCode = normalizeEntityTypeCode(entityTypeCode);
        Long tenantId = getRequiredTenantId();
        ensureEntityTypeExists(tenantId, normalizedTypeCode);
        ensureFlowGraphFieldLibrary(tenantId);
        upsertEntityTypeBaseField(tenantId, normalizedTypeCode);
        ensurePhysicalFlowGraphColumns(normalizedTypeCode);
        upsertModelFieldAssignments(tenantId, normalizedTypeCode);
        evictCrudCache(normalizedTypeCode);
    }

    private void ensureFlowGraphFieldLibrary(Long tenantId) {
        jdbcTemplate.update(
                """
                INSERT INTO dynamic_field (
                  code, name, type, unit, description, source, status, max_relations,
                  index_strategy, options, provider_code, semantic_type, tenant_id, creator
                )
                VALUES (?, '流程图', 'TEXT', NULL, '通用流程图结构字段',
                        'BASE', 1, 1, 'NONE', NULL, NULL, ?, ?, 'capability-flow-graph')
                ON CONFLICT (code, tenant_id) WHERE deleted = false
                DO UPDATE SET
                  name = EXCLUDED.name,
                  type = EXCLUDED.type,
                  description = EXCLUDED.description,
                  source = EXCLUDED.source,
                  status = EXCLUDED.status,
                  semantic_type = EXCLUDED.semantic_type,
                  updater = 'capability-flow-graph',
                  update_time = NOW()
                """,
                FIELD_CODE_FLOW_GRAPH,
                StructuredFieldSemantics.FLOW_GRAPH,
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
                  ?, f.id, ?, '流程图', f.type,
                  false, '{}', '通用流程图结构字段',
                  '{"createVisible":false,"editVisible":false,"detailVisible":false}',
                  940, 1, ?, 'capability-flow-graph'
                FROM dynamic_field f
                WHERE f.deleted = false
                  AND f.tenant_id = ?
                  AND f.code = ?
                ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
                DO UPDATE SET
                  library_field_id = EXCLUDED.library_field_id,
                  field_name = EXCLUDED.field_name,
                  data_type = EXCLUDED.data_type,
                  required = EXCLUDED.required,
                  default_value = EXCLUDED.default_value,
                  description = EXCLUDED.description,
                  type_config = EXCLUDED.type_config,
                  sort_order = EXCLUDED.sort_order,
                  status = EXCLUDED.status,
                  updater = 'capability-flow-graph',
                  update_time = NOW()
                """,
                entityTypeCode,
                FIELD_CODE_FLOW_GRAPH,
                tenantId,
                tenantId,
                FIELD_CODE_FLOW_GRAPH
        );
    }

    private void ensurePhysicalFlowGraphColumns(String entityTypeCode) {
        String tableName = entityRepository.requireExistingPhysicalTable(entityTypeCode);
        jdbcTemplate.execute("""
                ALTER TABLE dynamicbusiness.%s
                ADD COLUMN IF NOT EXISTS flow_graph_json JSONB NOT NULL DEFAULT '{}'::jsonb
                """.formatted(tableName));
        jdbcTemplate.execute("""
                COMMENT ON COLUMN dynamicbusiness.%s.flow_graph_json IS '流程图权威 JSON（流程图能力基础字段）'
                """.formatted(tableName));
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
                  m.id, f.id, m.code, ?, false, false, false, false, 940,
                  '{}', NULL, 'BASE', m.tenant_id, 'capability-flow-graph'
                FROM dynamic_model m
                JOIN dynamic_field f
                  ON f.deleted = false
                 AND f.tenant_id = m.tenant_id
                 AND f.code = ?
                WHERE m.deleted = false
                  AND m.tenant_id = ?
                  AND m.entity_type_code = ?
                ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
                DO UPDATE SET
                  model_id = EXCLUDED.model_id,
                  field_id = EXCLUDED.field_id,
                  required = EXCLUDED.required,
                  is_searchable = EXCLUDED.is_searchable,
                  is_filterable = EXCLUDED.is_filterable,
                  is_sortable = EXCLUDED.is_sortable,
                  sort = EXCLUDED.sort,
                  default_value = EXCLUDED.default_value,
                  field_source = EXCLUDED.field_source,
                  updater = 'capability-flow-graph',
                  update_time = NOW()
                """,
                FIELD_CODE_FLOW_GRAPH,
                FIELD_CODE_FLOW_GRAPH,
                tenantId,
                entityTypeCode
        );
    }

    private void evictCrudCache(String entityTypeCode) {
        ModelCrudFormCacheEvictor.evictRegistryAndDomainCatalogs(jdbcTemplate, entityTypeCode);
    }

    private void ensureEntityTypeExists(Long tenantId, String entityTypeCode) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM dynamic_entity_type WHERE tenant_id = ? AND code = ? AND deleted = false",
                Integer.class,
                tenantId, entityTypeCode
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
            throw new ServiceException(400, "缺少租户信息，无法启用流程图能力");
        }
        return tenantId;
    }
}
