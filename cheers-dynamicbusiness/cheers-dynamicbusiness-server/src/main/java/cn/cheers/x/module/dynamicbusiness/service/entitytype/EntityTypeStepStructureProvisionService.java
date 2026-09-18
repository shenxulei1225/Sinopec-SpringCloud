package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.framework.field.StructuredFieldSemantics;
import cn.cheers.x.module.dynamicbusiness.service.capability.form.ModelCrudFormCacheEvictor;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 目录步骤树能力的元数据写入服务。
 *
 * 负责：开能力时补步骤树列、写下正式用途与挂载配置。
 * 不负责：前端编辑交互；读路径按列名猜用途或猜「只能挂动作」。
 * 认表只问 {@link EntityRepository}，不拼 {@code ent_*}。
 */
@Service
public class EntityTypeStepStructureProvisionService {

    private static final String FIELD_CODE_STEP_TREE = "step_tree_json";
    private static final String FIELD_CODE_ACTION_TREE = "action_tree_json";

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private EntityRepository entityRepository;

    public void ensureProvisioned(
            String entityTypeCode,
            List<String> hangableTypeCodes,
            Boolean allowMixed) {
        String normalizedTypeCode = normalizeEntityTypeCode(entityTypeCode);
        Long tenantId = getRequiredTenantId();
        ensureEntityTypeExists(tenantId, normalizedTypeCode);
        HangWrite hang = resolveHang(tenantId, normalizedTypeCode, hangableTypeCodes, allowMixed);
        String persistFieldCode = resolvePersistFieldCode(tenantId, normalizedTypeCode, hang.editorKind);
        ensureStepTreeFieldLibrary(tenantId, persistFieldCode);
        upsertEntityTypeBaseField(tenantId, normalizedTypeCode, persistFieldCode, hang);
        ensurePhysicalStepTreeColumns(normalizedTypeCode, persistFieldCode);
        upsertModelFieldAssignments(tenantId, normalizedTypeCode, persistFieldCode);
        hidePersistCompanionFields(tenantId, normalizedTypeCode, hang.editorKind);
        if (!FIELD_CODE_ACTION_TREE.equals(persistFieldCode)) {
            retireLegacyActionTreeAssignments(tenantId, normalizedTypeCode);
        }
        evictCrudCache(normalizedTypeCode);
    }

    private HangWrite resolveHang(
            Long tenantId,
            String entityTypeCode,
            List<String> requestedHangable,
            Boolean requestedAllowMixed) {
        JSONObject existing = loadExistingTypeConfig(tenantId, entityTypeCode);
        String editorKind = officialEditorKind(
                existing != null && StringUtils.hasText(existing.getString("editorKind"))
                        ? existing.getString("editorKind").trim()
                        : seedEditorKind(entityTypeCode));
        List<String> hangable;
        if (requestedHangable != null) {
            hangable = normalizeHangable(requestedHangable);
        } else if (existing != null) {
            hangable = readHangable(existing);
        } else {
            hangable = seedHangable(entityTypeCode);
        }
        boolean allowMixed;
        if (requestedAllowMixed != null) {
            allowMixed = requestedAllowMixed;
        } else if (existing != null) {
            allowMixed = existing.getBooleanValue("allowMixed");
        } else {
            allowMixed = seedAllowMixed(entityTypeCode);
        }
        List<JSONObject> packMethods = readOrSeedPackMethods(existing, entityTypeCode, editorKind);
        return new HangWrite(hangable, allowMixed, editorKind, packMethods);
    }

    /**
     * 历史存盘形态 methods_by_means 已收成步骤树包。只在写入时改正式名，不双写两套。
     */
    private static String officialEditorKind(String editorKind) {
        if ("methods_by_means".equals(editorKind)) {
            return "step_tree_pack";
        }
        return editorKind;
    }

    private static List<JSONObject> readOrSeedPackMethods(
            JSONObject existing, String entityTypeCode, String editorKind) {
        if (existing != null && existing.getJSONArray("packMethods") != null) {
            return readPackMethods(existing.getJSONArray("packMethods"));
        }
        return seedPackMethods(entityTypeCode, editorKind);
    }

    private static List<JSONObject> readPackMethods(JSONArray arr) {
        List<JSONObject> out = new ArrayList<>();
        if (arr == null) {
            return out;
        }
        for (int i = 0; i < arr.size(); i++) {
            JSONObject row = arr.getJSONObject(i);
            if (row == null) {
                continue;
            }
            String key = row.getString("key");
            if (!StringUtils.hasText(key)) {
                continue;
            }
            JSONObject item = new JSONObject();
            item.put("key", key.trim());
            String label = row.getString("label");
            item.put("label", StringUtils.hasText(label) ? label.trim() : key.trim());
            out.add(item);
        }
        return out;
    }

    /**
     * 检查项目录第一适配：人 / 无人机 / 机器人 / 摄像机。
     * 其它目录开步骤树包时必须自己写下 packMethods，这里不猜。
     */
    private static List<JSONObject> seedPackMethods(String entityTypeCode, String editorKind) {
        if (!"step_tree_pack".equals(editorKind)) {
            return List.of();
        }
        if (!"inspection_item".equals(entityTypeCode.trim().toLowerCase(Locale.ROOT))) {
            return List.of();
        }
        return List.of(
                packMethod("MANUAL", "人"),
                packMethod("UAV", "无人机"),
                packMethod("ROBOT", "机器人"),
                packMethod("FIXED_CAMERA", "摄像机")
        );
    }

    private static JSONObject packMethod(String key, String label) {
        JSONObject item = new JSONObject();
        item.put("key", key);
        item.put("label", label);
        return item;
    }

    private static boolean seedAllowMixed(String entityTypeCode) {
        return "task".equals(entityTypeCode.trim().toLowerCase(Locale.ROOT));
    }

    private String seedEditorKind(String entityTypeCode) {
        String code = entityTypeCode.trim().toLowerCase(Locale.ROOT);
        if ("action".equals(code)) {
            return "child_action_tree";
        }
        if ("inspection_item".equals(code)) {
            return "step_tree_pack";
        }
        return "step_nodes";
    }

    private List<String> seedHangable(String entityTypeCode) {
        String code = entityTypeCode.trim().toLowerCase(Locale.ROOT);
        if ("action".equals(code)) {
            return List.of("action");
        }
        if ("inspection_item".equals(code)) {
            return List.of("action");
        }
        if ("sop".equals(code)) {
            return List.of("inspection_item");
        }
        // 任务台账（底座 task；巡检任务是子数据类型，物理表仍是 ent_task）
        if ("task".equals(code)) {
            return List.of("inspection_item", "action");
        }
        return List.of();
    }

    private String resolvePersistFieldCode(Long tenantId, String entityTypeCode, String editorKind) {
        String existingOfficial = queryAssignedFieldWithSemantic(
                tenantId, entityTypeCode, StructuredFieldSemantics.STEP_TREE);
        if (StringUtils.hasText(existingOfficial)) {
            return existingOfficial;
        }
        if ("child_action_tree".equals(editorKind) && hasFieldCode(tenantId, FIELD_CODE_ACTION_TREE)) {
            return FIELD_CODE_ACTION_TREE;
        }
        return FIELD_CODE_STEP_TREE;
    }

    private String queryAssignedFieldWithSemantic(Long tenantId, String entityTypeCode, String semantic) {
        List<String> codes = jdbcTemplate.query(
                """
                SELECT a.field_code
                FROM dynamic_model_field_assignment a
                JOIN dynamic_model m ON m.id = a.model_id
                JOIN dynamic_field f ON f.id = a.field_id
                WHERE a.deleted = false AND m.deleted = false AND f.deleted = false
                  AND m.tenant_id = ? AND m.entity_type_code = ?
                  AND f.semantic_type = ?
                ORDER BY a.id ASC
                LIMIT 1
                """,
                (rs, rowNum) -> rs.getString("field_code"),
                tenantId, entityTypeCode, semantic
        );
        return codes == null || codes.isEmpty() ? null : codes.get(0);
    }

    private boolean hasFieldCode(Long tenantId, String fieldCode) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM dynamic_field WHERE tenant_id = ? AND code = ? AND deleted = false",
                Integer.class, tenantId, fieldCode
        );
        return count != null && count > 0;
    }

    private JSONObject loadExistingTypeConfig(Long tenantId, String entityTypeCode) {
        List<String> rows = jdbcTemplate.query(
                """
                SELECT type_config
                FROM dynamic_entity_type_base_field
                WHERE tenant_id = ? AND entity_type_code = ? AND deleted = false
                  AND field_code IN (?, ?)
                ORDER BY CASE field_code WHEN ? THEN 0 ELSE 1 END, id ASC
                """,
                (rs, rowNum) -> rs.getString("type_config"),
                tenantId, entityTypeCode, FIELD_CODE_STEP_TREE, FIELD_CODE_ACTION_TREE, FIELD_CODE_STEP_TREE
        );
        if (rows == null) {
            return null;
        }
        for (String raw : rows) {
            if (!StringUtils.hasText(raw)) {
                continue;
            }
            JSONObject obj = JSON.parseObject(raw);
            if (obj != null) {
                return obj;
            }
        }
        return null;
    }

    private void ensureStepTreeFieldLibrary(Long tenantId, String persistFieldCode) {
        jdbcTemplate.update(
                """
                INSERT INTO dynamic_field (
                  code, name, type, unit, description, source, status, max_relations,
                  index_strategy, options, provider_code, semantic_type, tenant_id, creator
                )
                VALUES (?, '步骤树', 'TEXT', NULL, '通用步骤树结构字段',
                        'BASE', 1, 1, 'NONE', NULL, NULL, ?, ?, 'capability-step-structure')
                ON CONFLICT (code, tenant_id) WHERE deleted = false
                DO UPDATE SET
                  name = EXCLUDED.name,
                  description = EXCLUDED.description,
                  source = EXCLUDED.source,
                  status = EXCLUDED.status,
                  semantic_type = EXCLUDED.semantic_type,
                  updater = 'capability-step-structure',
                  update_time = NOW()
                """,
                persistFieldCode,
                StructuredFieldSemantics.STEP_TREE,
                tenantId
        );
    }

    private void upsertModelFieldAssignments(Long tenantId, String entityTypeCode, String persistFieldCode) {
        jdbcTemplate.update(
                """
                INSERT INTO dynamic_model_field_assignment (
                  model_id, field_id, model_code, field_code,
                  required, is_searchable, is_filterable, is_sortable, sort,
                  default_value, target_entity_type, field_source, tenant_id, creator
                )
                SELECT
                  m.id, f.id, m.code, ?, false, false, false, false, 930,
                  '[]', NULL, 'BASE', m.tenant_id, 'capability-step-structure'
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
                  updater = 'capability-step-structure',
                  update_time = NOW()
                """,
                persistFieldCode,
                persistFieldCode,
                tenantId,
                entityTypeCode
        );
    }

    private void upsertEntityTypeBaseField(
            Long tenantId, String entityTypeCode, String persistFieldCode, HangWrite hang) {
        String typeConfig = toTypeConfigJson(hang);
        jdbcTemplate.update(
                """
                INSERT INTO dynamic_entity_type_base_field (
                  entity_type_code, library_field_id, field_code, field_name, data_type,
                  required, default_value, description, type_config, sort_order,
                  status, tenant_id, creator
                )
                SELECT
                  ?, f.id, ?, '步骤树', f.type,
                  false, '[]', '通用步骤树结构字段',
                  ?::jsonb,
                  930, 1, ?, 'capability-step-structure'
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
                  updater = 'capability-step-structure',
                  update_time = NOW()
                """,
                entityTypeCode,
                persistFieldCode,
                typeConfig,
                tenantId,
                tenantId,
                persistFieldCode
        );
    }

    private String toTypeConfigJson(HangWrite hang) {
        JSONObject obj = new JSONObject();
        obj.put("createVisible", false);
        obj.put("editVisible", false);
        obj.put("detailVisible", false);
        obj.put("hangableTypeCodes", hang.hangableTypeCodes);
        obj.put("allowMixed", hang.allowMixed);
        obj.put("editorKind", hang.editorKind);
        obj.put("packMethods", hang.packMethods);
        return obj.toJSONString();
    }

    /**
     * 开能力时补物理列。动作目录沿用 action_tree_json：可空、不写默认 []。
     * 现网子动作仍在 child_action_ids_json；填空数组会让编辑器以为快照已在、不再回退。
     * 只改当前租户那一张实体表，表名由仓库给出。
     */
    private void ensurePhysicalStepTreeColumns(String entityTypeCode, String persistFieldCode) {
        if (!FIELD_CODE_STEP_TREE.equals(persistFieldCode)
                && !FIELD_CODE_ACTION_TREE.equals(persistFieldCode)) {
            return;
        }
        String tableName = entityRepository.requireExistingPhysicalTable(entityTypeCode);
        if (FIELD_CODE_ACTION_TREE.equals(persistFieldCode)) {
            jdbcTemplate.execute("""
                    ALTER TABLE dynamicbusiness.%s
                    ADD COLUMN IF NOT EXISTS action_tree_json JSONB
                    """.formatted(tableName));
            jdbcTemplate.execute("""
                    COMMENT ON COLUMN dynamicbusiness.%s.action_tree_json IS '动作宿主步骤树快照（用途 STEP_TREE）'
                    """.formatted(tableName));
            return;
        }
        jdbcTemplate.execute("""
                ALTER TABLE dynamicbusiness.%s
                ADD COLUMN IF NOT EXISTS step_tree_json JSONB NOT NULL DEFAULT '[]'::jsonb
                """.formatted(tableName));
        jdbcTemplate.execute("""
                COMMENT ON COLUMN dynamicbusiness.%s.step_tree_json IS '步骤树权威 JSON（步骤树能力）'
                """.formatted(tableName));
    }

    /**
     * 现网编码列表 / 是否复合是步骤树存盘派生列，不是第二种用途。
     * 开能力时写下详情不可见，介绍区按这份规则藏，不按列名在页面再藏一遍。
     */
    private void hidePersistCompanionFields(Long tenantId, String entityTypeCode, String editorKind) {
        if (!"child_action_tree".equals(editorKind)) {
            return;
        }
        jdbcTemplate.update(
                """
                UPDATE dynamic_entity_type_base_field
                SET type_config = jsonb_strip_nulls(
                      COALESCE(type_config, '{}'::jsonb)
                      || '{"createVisible":false,"editVisible":false,"detailVisible":false}'::jsonb
                    ),
                    updater = 'capability-step-structure',
                    update_time = NOW()
                WHERE tenant_id = ?
                  AND entity_type_code = ?
                  AND deleted = false
                  AND field_code IN ('child_action_ids_json', 'is_composite')
                """,
                tenantId,
                entityTypeCode
        );
    }

    private void retireLegacyActionTreeAssignments(Long tenantId, String entityTypeCode) {
        jdbcTemplate.update(
                """
                UPDATE dynamic_model_field_assignment a
                SET deleted = true,
                    updater = 'capability-step-structure',
                    update_time = NOW()
                FROM dynamic_model m
                WHERE a.model_id = m.id
                  AND a.deleted = false
                  AND m.deleted = false
                  AND m.tenant_id = ?
                  AND m.entity_type_code = ?
                  AND a.field_code = ?
                """,
                tenantId,
                entityTypeCode,
                FIELD_CODE_ACTION_TREE
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
            throw new ServiceException(400, "缺少租户信息，无法启用步骤树能力");
        }
        return tenantId;
    }

    private static List<String> normalizeHangable(List<String> raw) {
        List<String> out = new ArrayList<>();
        if (raw == null) {
            return out;
        }
        for (String item : raw) {
            if (!StringUtils.hasText(item)) {
                continue;
            }
            String code = item.trim();
            if (!out.contains(code)) {
                out.add(code);
            }
        }
        return out;
    }

    private static List<String> readHangable(JSONObject existing) {
        JSONArray arr = existing.getJSONArray("hangableTypeCodes");
        if (arr == null) {
            return List.of();
        }
        List<String> out = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            String code = arr.getString(i);
            if (StringUtils.hasText(code)) {
                out.add(code.trim());
            }
        }
        return out;
    }

    private record HangWrite(
            List<String> hangableTypeCodes,
            boolean allowMixed,
            String editorKind,
            List<JSONObject> packMethods) {
    }
}
