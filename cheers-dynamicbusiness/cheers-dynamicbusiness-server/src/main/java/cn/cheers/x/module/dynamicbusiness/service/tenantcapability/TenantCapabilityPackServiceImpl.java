package cn.cheers.x.module.dynamicbusiness.service.tenantcapability;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.tenantcapability.TenantCapabilityPackDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.tenantcapability.TenantCapabilityPackMapper;
import cn.cheers.x.module.dynamicbusiness.service.cataloggrant.CatalogRoleGrantItem;
import cn.cheers.x.module.dynamicbusiness.service.cataloggrant.CatalogRoleGrantService;
import cn.cheers.x.module.dynamicbusiness.service.pagegrant.PageRoleGrantService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 开通编排：写开通表 → 挂目录 → 新报修目录写上标准字段和型号 → 租户管理员页面查看动作 → 目录资源授权。
 * 模板目录默认从租户 1 复制注册行；目标租户已有同编码目录则跳过，也不再补字段。
 */
@Service
public class TenantCapabilityPackServiceImpl implements TenantCapabilityPackService {

    private static final Logger log = LoggerFactory.getLogger(TenantCapabilityPackServiceImpl.class);

    /** 目录模板来源租户。现网智慧巡检目录最全。 */
    private static final long TEMPLATE_TENANT_ID = 1L;

    private static final List<String> CATALOG_SUBJECTS = List.of("category", "model", "entity", "detail");
    private static final List<String> CATALOG_OPS = List.of("query", "create", "update", "delete");

    @Resource
    private TenantCapabilityPackMapper tenantCapabilityPackMapper;
    @Resource
    private PageRoleGrantService pageRoleGrantService;
    @Resource
    private CatalogRoleGrantService catalogRoleGrantService;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private RepairStandardCatalogWriter repairStandardCatalogWriter;
    @Resource
    private VehicleCatalogWriter vehicleCatalogWriter;

    @Override
    public List<BusinessCapabilityPackDefinition> listDefinitions() {
        return List.copyOf(BusinessCapabilityPackRegistry.all());
    }

    @Override
    public List<String> listOpenedPackCodes(Long tenantId) {
        return listOpenedSelections(tenantId).stream().map(TenantPackPageSelection::packCode).toList();
    }

    @Override
    public List<TenantPackPageSelection> listOpenedSelections(Long tenantId) {
        assertBusinessTenant(tenantId);
        List<TenantCapabilityPackDO> rows = TenantUtils.execute(
                tenantId, () -> tenantCapabilityPackMapper.selectListByTenantId(tenantId));
        List<TenantPackPageSelection> selections = new ArrayList<>();
        for (TenantCapabilityPackDO row : rows) {
            BusinessCapabilityPackDefinition pack = BusinessCapabilityPackRegistry.find(row.getPackCode()).orElse(null);
            if (pack == null) {
                continue;
            }
            selections.add(new TenantPackPageSelection(pack.code(), pagesOf(pack, row.getOpenedPageCodes())));
        }
        return selections;
    }

    @Override
    public TenantCapabilityOpenedScope resolveOpenedScope(Long tenantId) {
        if (tenantId == null || tenantId <= 0 || isPlatformTenant(tenantId)) {
            return TenantCapabilityOpenedScope.empty();
        }
        List<TenantPackPageSelection> selections = listOpenedSelections(tenantId);
        LinkedHashSet<String> packCodes = new LinkedHashSet<>();
        LinkedHashSet<String> pages = new LinkedHashSet<>();
        LinkedHashSet<String> catalogs = new LinkedHashSet<>();
        for (TenantPackPageSelection selection : selections) {
            BusinessCapabilityPackDefinition pack = BusinessCapabilityPackRegistry.find(selection.packCode()).orElse(null);
            if (pack == null) {
                continue;
            }
            packCodes.add(pack.code());
            pages.addAll(selection.pageCodes());
            for (String catalogCode : pack.catalogCodes()) {
                if (StringUtils.hasText(catalogCode)) {
                    catalogs.add(catalogCode.trim());
                }
            }
        }
        return new TenantCapabilityOpenedScope(
                List.copyOf(packCodes),
                List.copyOf(pages),
                List.copyOf(catalogs));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceOpenedPacks(Long tenantId, List<String> packCodes) {
        List<TenantPackPageSelection> selections = new ArrayList<>();
        if (packCodes != null) {
            for (String raw : packCodes) {
                if (!StringUtils.hasText(raw)) {
                    continue;
                }
                selections.add(new TenantPackPageSelection(raw.trim(), null));
            }
        }
        replaceOpenedSelections(tenantId, selections);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceOpenedSelections(Long tenantId, List<TenantPackPageSelection> selections) {
        assertBusinessTenant(tenantId);
        Map<String, List<String>> wanted = normalizeSelections(selections);
        Map<String, List<String>> before = new LinkedHashMap<>();
        for (TenantPackPageSelection current : listOpenedSelections(tenantId)) {
            before.put(current.packCode(), current.pageCodes());
        }

        TenantUtils.execute(tenantId, () -> {
            tenantCapabilityPackMapper.deleteByTenantId(tenantId);
            for (Map.Entry<String, List<String>> entry : wanted.entrySet()) {
                TenantCapabilityPackDO row = new TenantCapabilityPackDO();
                row.setTenantId(tenantId);
                row.setPackCode(entry.getKey());
                row.setOpenedPageCodes(joinPages(entry.getValue()));
                tenantCapabilityPackMapper.insert(row);
            }
        });

        for (Map.Entry<String, List<String>> entry : wanted.entrySet()) {
            BusinessCapabilityPackDefinition pack = BusinessCapabilityPackRegistry.find(entry.getKey()).orElseThrow();
            List<String> pages = entry.getValue();
            if (!before.containsKey(entry.getKey())) {
                provisionPack(tenantId, pack, pages);
                continue;
            }
            List<String> added = new ArrayList<>();
            Set<String> had = new LinkedHashSet<>(before.get(entry.getKey()));
            for (String pageCode : pages) {
                if (!had.contains(pageCode)) {
                    added.add(pageCode);
                }
            }
            grantNewPages(tenantId, added);
        }
    }

    /**
     * null 页面列表表示整包。有页面的业务若一个都没留下，这一项不写入。
     * 页面不属于这一包时拒绝保存。
     */
    private Map<String, List<String>> normalizeSelections(List<TenantPackPageSelection> selections) {
        Map<String, List<String>> wanted = new LinkedHashMap<>();
        if (selections == null) {
            return wanted;
        }
        for (TenantPackPageSelection selection : selections) {
            if (selection == null || !StringUtils.hasText(selection.packCode())) {
                continue;
            }
            String code = selection.packCode().trim();
            BusinessCapabilityPackDefinition pack = BusinessCapabilityPackRegistry.find(code)
                    .orElseThrow(() -> new ServiceException(400, "未知业务：" + code));
            List<String> pages = selection.pageCodes() == null
                    ? new ArrayList<>(pack.pageCodes())
                    : keepPackPages(pack, selection.pageCodes());
            if (!pack.pageCodes().isEmpty() && pages.isEmpty()) {
                continue;
            }
            wanted.put(pack.code(), pages);
        }
        return wanted;
    }

    private List<String> keepPackPages(BusinessCapabilityPackDefinition pack, List<String> pageCodes) {
        Set<String> allowed = new LinkedHashSet<>(pack.pageCodes());
        List<String> kept = new ArrayList<>();
        for (String raw : pageCodes) {
            if (!StringUtils.hasText(raw)) {
                continue;
            }
            String pageCode = raw.trim();
            if (!allowed.contains(pageCode)) {
                throw new ServiceException(400, "页面不属于这个业务：" + pageCode);
            }
            if (!kept.contains(pageCode)) {
                kept.add(pageCode);
            }
        }
        return kept;
    }

    /** 旧行没有页面明细时，按这一包现在的全部页面。空字符串表示没有单独页面。 */
    private List<String> pagesOf(BusinessCapabilityPackDefinition pack, String stored) {
        if (stored == null) {
            return List.copyOf(pack.pageCodes());
        }
        if (!StringUtils.hasText(stored)) {
            return List.of();
        }
        Set<String> allowed = new LinkedHashSet<>(pack.pageCodes());
        List<String> kept = new ArrayList<>();
        for (String raw : stored.split(",")) {
            if (!StringUtils.hasText(raw)) {
                continue;
            }
            String pageCode = raw.trim();
            if (allowed.contains(pageCode) && !kept.contains(pageCode)) {
                kept.add(pageCode);
            }
        }
        return kept;
    }

    private String joinPages(List<String> pages) {
        if (pages == null || pages.isEmpty()) {
            return "";
        }
        return String.join(",", pages);
    }

    private void grantNewPages(Long tenantId, List<String> pageCodes) {
        if (pageCodes.isEmpty()) {
            return;
        }
        Long adminRoleId = findTenantAdminRoleId(tenantId);
        if (adminRoleId == null) {
            log.warn("[grantNewPages][tenantId={}] 没有租户管理员角色，跳过页面授权", tenantId);
            return;
        }
        TenantUtils.execute(tenantId, () -> pageRoleGrantService.mergeViewActions(adminRoleId, pageCodes));
    }

    private void provisionPack(Long tenantId, BusinessCapabilityPackDefinition pack, List<String> pageCodes) {
        for (String catalogCode : pack.catalogCodes()) {
            copyCatalogRowFromTemplate(tenantId, catalogCode);
        }
        Long adminRoleId = findTenantAdminRoleId(tenantId);
        if (adminRoleId == null) {
            log.warn("[provisionPack][tenantId={}][pack={}] 没有租户管理员角色，跳过页面与目录授权", tenantId, pack.code());
            return;
        }
        TenantUtils.execute(tenantId, () -> {
            pageRoleGrantService.mergeViewActions(adminRoleId, pageCodes);
            mergeCatalogGrants(adminRoleId, pack.catalogCodes());
        });
    }

    /**
     * 这次开通还没有该目录时，复制模板上的目录注册行。
     * 专用表名写成当前租户自己的表，不沿用模板租户的表名。
     * 报修目录是这次新插入的，同时写上模板上的标准字段和标准型号。
     * 已经有的目录不改。不建安防那组复用目录。
     */
    private void copyCatalogRowFromTemplate(Long targetTenantId, String catalogCode) {
        if (!StringUtils.hasText(catalogCode)) {
            return;
        }
        String code = catalogCode.trim();
        Integer exists = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(1) FROM dynamicbusiness.dynamic_entity_type
                        WHERE tenant_id = ? AND code = ? AND deleted = false
                        """,
                Integer.class,
                targetTenantId,
                code);
        if (exists != null && exists > 0) {
            return;
        }
        Integer templateCount = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(1) FROM dynamicbusiness.dynamic_entity_type
                        WHERE tenant_id = ? AND code = ? AND deleted = false
                        """,
                Integer.class,
                TEMPLATE_TENANT_ID,
                code);
        if (templateCount == null || templateCount <= 0) {
            log.warn("[copyCatalogRowFromTemplate][target={}][code={}] 模板租户没有该目录，跳过", targetTenantId, code);
            return;
        }
        // 用户目录是租户级登录身份：开通时强制 NETWORK，不跟模板里被误改成的 FACILITY 走。
        // 所属场站只属于租户内站场级目录，不进入平台租户管理的登录账号路径。
        int inserted = jdbcTemplate.update(
                """
                        INSERT INTO dynamicbusiness.dynamic_entity_type (
                            code, name, parent_id, description, icon, alias, sort, status, type_level,
                            association_fields, storage_type, dedicated_table_name, enable_rule_engine,
                            physical_column_mapping, creator, updater, deleted, tenant_id, group_name,
                            entry_kind, base_entity_type_code, domain, model_workbench_mode, work_scope,
                            data_layout_id, model_layout_id
                        )
                        SELECT
                            code, name, NULL, description, icon, alias, sort, status, type_level,
                            association_fields, storage_type,
                            CASE WHEN upper(storage_type) = 'DEDICATED'
                                 THEN 'ent_' || replace(lower(code), '-', '_') || '_t' || ?::text
                                 ELSE NULL END,
                            enable_rule_engine,
                            physical_column_mapping, 'pack-open', 'pack-open', false, ?, group_name,
                            entry_kind, base_entity_type_code, domain, model_workbench_mode,
                            CASE WHEN code = 'user_account' THEN 'NETWORK' ELSE work_scope END,
                            NULL, NULL
                        FROM dynamicbusiness.dynamic_entity_type
                        WHERE tenant_id = ? AND code = ? AND deleted = false
                        LIMIT 1
                        """,
                targetTenantId,
                targetTenantId,
                TEMPLATE_TENANT_ID,
                code);
        log.info("[copyCatalogRowFromTemplate][target={}][code={}][inserted={}]", targetTenantId, code, inserted);
        if (inserted > 0 && RepairStandardCatalogWriter.CATALOG.equals(code)) {
            repairStandardCatalogWriter.writeForNewCatalog(targetTenantId);
        }
        if (inserted > 0 && VehicleCatalogWriter.CATALOGS.contains(code)) {
            vehicleCatalogWriter.writeForNewCatalog(targetTenantId, code);
        }
    }

    private void mergeCatalogGrants(Long roleId, List<String> catalogCodes) {
        if (catalogCodes == null || catalogCodes.isEmpty()) {
            return;
        }
        List<CatalogRoleGrantItem> current = new ArrayList<>(catalogRoleGrantService.listByRoleId(roleId));
        Set<String> have = new LinkedHashSet<>();
        for (CatalogRoleGrantItem item : current) {
            have.add(item.registryCode() + "::" + item.subject());
        }
        for (String catalogCode : catalogCodes) {
            if (!StringUtils.hasText(catalogCode)) {
                continue;
            }
            String code = catalogCode.trim();
            for (String subject : CATALOG_SUBJECTS) {
                String key = code + "::" + subject;
                if (have.contains(key)) {
                    continue;
                }
                current.add(new CatalogRoleGrantItem(code, subject, CATALOG_OPS));
                have.add(key);
            }
        }
        catalogRoleGrantService.replaceRoleGrants(roleId, current);
    }

    private Long findTenantAdminRoleId(Long tenantId) {
        List<Long> ids = jdbcTemplate.query(
                """
                        SELECT id FROM public.system_role
                        WHERE tenant_id = ? AND code = 'tenant_admin' AND deleted = false
                        ORDER BY id
                        LIMIT 1
                        """,
                (rs, rowNum) -> rs.getLong(1),
                tenantId);
        return ids.isEmpty() ? null : ids.get(0);
    }

    private void assertBusinessTenant(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new ServiceException(400, "请选择业务租户");
        }
        if (isPlatformTenant(tenantId)) {
            throw new ServiceException(400, "不能给平台租户开通业务能力包");
        }
        String scope = jdbcTemplate.query(
                "SELECT scope FROM public.system_tenant WHERE id = ? AND deleted = false",
                rs -> rs.next() ? rs.getString(1) : null,
                tenantId);
        if (scope == null) {
            throw new ServiceException(400, "租户不存在");
        }
    }

    private boolean isPlatformTenant(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            return false;
        }
        String scope = jdbcTemplate.query(
                "SELECT scope FROM public.system_tenant WHERE id = ? AND deleted = false",
                rs -> rs.next() ? rs.getString(1) : null,
                tenantId);
        return scope != null && "PLATFORM".equalsIgnoreCase(scope.trim());
    }
}
