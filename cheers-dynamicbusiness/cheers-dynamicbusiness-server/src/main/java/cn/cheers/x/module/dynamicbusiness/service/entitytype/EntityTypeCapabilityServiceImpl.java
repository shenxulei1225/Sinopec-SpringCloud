package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeCapabilityItemRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeCapabilityRespVO;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * 目录能力开关实现。
 *
 * 设计说明：
 * - 只维护“目录-能力”的开关绑定，不把能力逻辑写死到目录 CRUD；
 * - 步骤树挂载写在该目录基础字段 type_config，不是第二种用途；
 * - 补列只走各能力的 ensureProvisioned，不另写 SQL 猜列；
 * - 实参配置只记开关，不补列、不改编标准步骤树。
 */
@Service
public class EntityTypeCapabilityServiceImpl implements EntityTypeCapabilityService {

    private static final String CAPABILITY_TABLE = "dynamic_entity_type_capability";
    private static final String ENTITY_TYPE_TABLE = "dynamic_entity_type";

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private EntityTypeStepStructureProvisionService entityTypeStepStructureProvisionService;
    @Resource
    private EntityTypeFlowGraphProvisionService entityTypeFlowGraphProvisionService;
    @Resource
    private EntityTypeParamSchemaProvisionService entityTypeParamSchemaProvisionService;
    @Resource
    private EntityTypeProtocolParserProvisionService entityTypeProtocolParserProvisionService;

    @Override
    public EntityTypeCapabilityRespVO getCapabilities(String entityTypeCode) {
        String normalizedCode = normalizeEntityTypeCode(entityTypeCode);
        List<EntityTypeCapabilityItemRespVO> items = listCapabilityItems(normalizedCode);
        EntityTypeCapabilityRespVO resp = new EntityTypeCapabilityRespVO();
        resp.setEntityTypeCode(normalizedCode);
        resp.setCapabilityItems(items);
        HangSnapshot hang = loadStepTreeHang(getRequiredTenantId(), normalizedCode);
        resp.setStepTreeHangableTypeCodes(hang.hangableTypeCodes);
        resp.setStepTreeAllowMixed(hang.allowMixed);
        return resp;
    }

    @Override
    public List<EntityTypeCapabilityItemRespVO> listCapabilityItems(String entityTypeCode) {
        String normalizedCode = normalizeEntityTypeCode(entityTypeCode);
        Long tenantId = getRequiredTenantId();
        ensureEntityTypeExists(tenantId, normalizedCode);
        Set<String> enabled = loadEnabledCapabilitySet(tenantId, normalizedCode);

        List<EntityTypeCapabilityItemRespVO> items = new ArrayList<>();
        items.add(new EntityTypeCapabilityItemRespVO(
                CAPABILITY_VERSION_MANAGEMENT,
                "版本管理",
                "启用后可按版本保存并发布；未启用则隐藏发布相关能力。",
                enabled.contains(CAPABILITY_VERSION_MANAGEMENT)
        ));
        items.add(new EntityTypeCapabilityItemRespVO(
                CAPABILITY_STEP_STRUCTURE,
                "步骤管理",
                "启用后按用途补一列步骤树，并在本目录写下能挂谁。关插件只卸面板，不删列。",
                enabled.contains(CAPABILITY_STEP_STRUCTURE)
        ));
        items.add(new EntityTypeCapabilityItemRespVO(
                CAPABILITY_ACTUAL_PARAM,
                "实参配置",
                "启用后，这个目录的详情给当前对象填写已有步骤/动作的真实参数。不给本目录补步骤树列，也不改编标准步骤树。",
                enabled.contains(CAPABILITY_ACTUAL_PARAM)
        ));
        items.add(new EntityTypeCapabilityItemRespVO(
                CAPABILITY_PARAM_SCHEMA,
                "参数定义",
                "启用后按用途补一列参数定义，供动作等目录维护参数槽。",
                enabled.contains(CAPABILITY_PARAM_SCHEMA)
        ));
        items.add(new EntityTypeCapabilityItemRespVO(
                CAPABILITY_FLOW_GRAPH,
                "流程图",
                "启用后按用途补一列流程图。",
                enabled.contains(CAPABILITY_FLOW_GRAPH)
        ));
        items.add(new EntityTypeCapabilityItemRespVO(
                CAPABILITY_PROTOCOL_PARSER,
                "协议解析",
                "启用后按用途补字段说明、指令 JSON（样例折叠在指令 JSON 下）。",
                enabled.contains(CAPABILITY_PROTOCOL_PARSER)
        ));
        return items;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveEnabledCapabilities(
            String entityTypeCode,
            List<String> enabledCapabilityCodes,
            List<String> stepTreeHangableTypeCodes,
            Boolean stepTreeAllowMixed) {
        String normalizedCode = normalizeEntityTypeCode(entityTypeCode);
        Long tenantId = getRequiredTenantId();
        ensureEntityTypeExists(tenantId, normalizedCode);

        Set<String> normalizedEnabled = normalizeAndFilterCapabilities(enabledCapabilityCodes);
        jdbcTemplate.update(
                "UPDATE " + CAPABILITY_TABLE
                        + " SET deleted = true, update_time = NOW()"
                        + " WHERE tenant_id = ? AND entity_type_code = ? AND deleted = false",
                tenantId, normalizedCode
        );
        for (String capabilityCode : normalizedEnabled) {
            jdbcTemplate.update(
                    "INSERT INTO " + CAPABILITY_TABLE
                            + " (tenant_id, entity_type_code, capability_code, enabled, deleted, create_time, update_time)"
                            + " VALUES (?, ?, ?, true, false, NOW(), NOW())",
                    tenantId, normalizedCode, capabilityCode
            );
        }
        if (normalizedEnabled.contains(CAPABILITY_STEP_STRUCTURE)) {
            entityTypeStepStructureProvisionService.ensureProvisioned(
                    normalizedCode, stepTreeHangableTypeCodes, stepTreeAllowMixed);
        }
        if (normalizedEnabled.contains(CAPABILITY_PARAM_SCHEMA)) {
            entityTypeParamSchemaProvisionService.ensureProvisioned(normalizedCode);
        }
        if (normalizedEnabled.contains(CAPABILITY_FLOW_GRAPH)) {
            entityTypeFlowGraphProvisionService.ensureProvisioned(normalizedCode);
        }
        if (normalizedEnabled.contains(CAPABILITY_PROTOCOL_PARSER)) {
            entityTypeProtocolParserProvisionService.ensureProvisioned(normalizedCode);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void provisionEnabledCapabilities() {
        Long tenantId = getRequiredTenantId();
        List<EnabledBinding> bindings = loadEnabledBindings(tenantId);
        if (bindings.isEmpty()) {
            return;
        }
        for (EnabledBinding binding : bindings) {
            provisionOneEnabled(binding.entityTypeCode(), binding.capabilityCode());
        }
    }

    private void provisionOneEnabled(String entityTypeCode, String capabilityCode) {
        if (CAPABILITY_STEP_STRUCTURE.equals(capabilityCode)) {
            entityTypeStepStructureProvisionService.ensureProvisioned(entityTypeCode, null, null);
            return;
        }
        if (CAPABILITY_PARAM_SCHEMA.equals(capabilityCode)) {
            entityTypeParamSchemaProvisionService.ensureProvisioned(entityTypeCode);
            return;
        }
        if (CAPABILITY_FLOW_GRAPH.equals(capabilityCode)) {
            entityTypeFlowGraphProvisionService.ensureProvisioned(entityTypeCode);
            return;
        }
        if (CAPABILITY_PROTOCOL_PARSER.equals(capabilityCode)) {
            entityTypeProtocolParserProvisionService.ensureProvisioned(entityTypeCode);
        }
    }

    private List<EnabledBinding> loadEnabledBindings(Long tenantId) {
        List<EnabledBinding> rows = jdbcTemplate.query(
                "SELECT entity_type_code, capability_code FROM " + CAPABILITY_TABLE
                        + " WHERE tenant_id = ? AND enabled = true AND deleted = false"
                        + " ORDER BY entity_type_code ASC, id ASC",
                (rs, rowNum) -> new EnabledBinding(
                        rs.getString("entity_type_code"),
                        normalizeCapabilityCode(rs.getString("capability_code"))
                ),
                tenantId
        );
        if (rows == null) {
            return List.of();
        }
        List<EnabledBinding> out = new ArrayList<>();
        for (EnabledBinding row : rows) {
            if (!StringUtils.hasText(row.entityTypeCode()) || !isSupportedCapability(row.capabilityCode())) {
                continue;
            }
            if (CAPABILITY_VERSION_MANAGEMENT.equals(row.capabilityCode())
                    || CAPABILITY_ACTUAL_PARAM.equals(row.capabilityCode())) {
                continue;
            }
            out.add(row);
        }
        return out;
    }

    private record EnabledBinding(String entityTypeCode, String capabilityCode) {
    }

    @Override
    public boolean hasCapability(String entityTypeCode, String capabilityCode) {
        String normalizedCode = normalizeEntityTypeCode(entityTypeCode);
        String normalizedCapabilityCode = normalizeCapabilityCode(capabilityCode);
        Long tenantId = getRequiredTenantId();
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM " + CAPABILITY_TABLE
                        + " WHERE tenant_id = ? AND entity_type_code = ? AND capability_code = ?"
                        + " AND enabled = true AND deleted = false",
                Integer.class,
                tenantId, normalizedCode, normalizedCapabilityCode
        );
        return count != null && count > 0;
    }

    private HangSnapshot loadStepTreeHang(Long tenantId, String entityTypeCode) {
        List<String> rows = jdbcTemplate.query(
                """
                SELECT type_config
                FROM dynamic_entity_type_base_field
                WHERE tenant_id = ? AND entity_type_code = ? AND deleted = false
                  AND field_code IN ('step_tree_json', 'action_tree_json')
                ORDER BY CASE field_code WHEN 'step_tree_json' THEN 0 ELSE 1 END, id ASC
                """,
                (rs, rowNum) -> rs.getString("type_config"),
                tenantId, entityTypeCode
        );
        if (rows == null) {
            return HangSnapshot.empty();
        }
        for (String raw : rows) {
            HangSnapshot parsed = HangSnapshot.parse(raw);
            if (parsed != null) {
                return parsed;
            }
        }
        return HangSnapshot.empty();
    }

    private Set<String> loadEnabledCapabilitySet(Long tenantId, String entityTypeCode) {
        List<String> rows = jdbcTemplate.query(
                "SELECT capability_code FROM " + CAPABILITY_TABLE
                        + " WHERE tenant_id = ? AND entity_type_code = ?"
                        + " AND enabled = true AND deleted = false"
                        + " ORDER BY id ASC",
                (rs, rowNum) -> rs.getString("capability_code"),
                tenantId, entityTypeCode
        );
        Set<String> out = new LinkedHashSet<>();
        if (rows == null) return out;
        for (String row : rows) {
            String code = normalizeCapabilityCode(row);
            if (isSupportedCapability(code)) {
                out.add(code);
            }
        }
        return out;
    }

    private Set<String> normalizeAndFilterCapabilities(List<String> rawCodes) {
        Set<String> out = new LinkedHashSet<>();
        if (rawCodes == null) return out;
        for (String raw : rawCodes) {
            String code = normalizeCapabilityCode(raw);
            if (isSupportedCapability(code)) {
                out.add(code);
            }
        }
        return out;
    }

    private boolean isSupportedCapability(String capabilityCode) {
        return Objects.equals(CAPABILITY_VERSION_MANAGEMENT, capabilityCode)
                || Objects.equals(CAPABILITY_STEP_STRUCTURE, capabilityCode)
                || Objects.equals(CAPABILITY_ACTUAL_PARAM, capabilityCode)
                || Objects.equals(CAPABILITY_PARAM_SCHEMA, capabilityCode)
                || Objects.equals(CAPABILITY_FLOW_GRAPH, capabilityCode)
                || Objects.equals(CAPABILITY_PROTOCOL_PARSER, capabilityCode);
    }

    private void ensureEntityTypeExists(Long tenantId, String entityTypeCode) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM " + ENTITY_TYPE_TABLE
                        + " WHERE tenant_id = ? AND code = ? AND deleted = false",
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

    private String normalizeCapabilityCode(String capabilityCode) {
        if (!StringUtils.hasText(capabilityCode)) {
            return "";
        }
        return capabilityCode.trim().toLowerCase(Locale.ROOT);
    }

    private Long getRequiredTenantId() {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        if (tenantId == null || tenantId <= 0) {
            throw new ServiceException(400, "缺少租户信息，无法维护能力开关");
        }
        return tenantId;
    }

    private record HangSnapshot(List<String> hangableTypeCodes, boolean allowMixed) {
        static HangSnapshot empty() {
            return new HangSnapshot(List.of(), false);
        }

        static HangSnapshot parse(String raw) {
            if (!StringUtils.hasText(raw)) {
                return null;
            }
            JSONObject obj = JSON.parseObject(raw);
            if (obj == null || !obj.containsKey("editorKind")) {
                return null;
            }
            List<String> codes = new ArrayList<>();
            JSONArray arr = obj.getJSONArray("hangableTypeCodes");
            if (arr != null) {
                for (int i = 0; i < arr.size(); i++) {
                    String code = arr.getString(i);
                    if (StringUtils.hasText(code)) {
                        codes.add(code.trim());
                    }
                }
            }
            return new HangSnapshot(codes, obj.getBooleanValue("allowMixed"));
        }
    }
}
