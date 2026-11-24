package cn.cheers.x.module.dynamicbusiness.service.tenantcapability;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.CatalogOwnershipDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogOwnershipQueryService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
import cn.cheers.x.module.dynamicbusiness.service.tenantcapability.LoginBusinessAccessQueryService.CatalogFacilityEnablement;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据目录的查数、详情、保存，以及报修这类页面动作，共用这一道开通判断。
 *
 * <p>这本目录属于按场站开通的业务，并且记录上有「所在场站」这一列时，这座站必须开通了该项业务。
 * 没开通：列表不返回这条，详情、保存、受理返回「这条记录尚未启用此业务」。</p>
 *
 * <p>目录没有指向场站的单值归属时，不在这里拒绝。管线的起点、终点是另外两个场站或油库，不是这条记录所在的那一座站。
 * 不负责：职务和角色够不够（页面授权、数据范围各自核对）；不补归属字段。</p>
 */
@Service
public class CatalogBusinessEnablementGate {

    private static final String FACILITY = "facility";
    private static final String DENIED = "这条记录尚未启用此业务";

    @Resource
    private LoginBusinessAccessQueryService loginBusinessAccessQueryService;
    @Resource
    private ManagementUnitCapabilityPackService managementUnitCapabilityPackService;
    @Resource
    private CatalogOwnershipQueryService catalogOwnershipQueryService;
    @Resource
    private EntityTableNameHandler entityTableNameHandler;
    @Resource
    private EntityDedicatedColumnService entityDedicatedColumnService;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private JdbcTemplate jdbcTemplate;

    /** 列表要附上的场站条件。apply 为 false 表示这本目录不按场站开通收窄。 */
    public record ListCut(boolean apply, String fieldCode, List<Long> allowedIds) {
        static ListCut none() {
            return new ListCut(false, null, List.of());
        }
    }

    public ListCut listCut(String registryCode) {
        if (skip(registryCode)) {
            return ListCut.none();
        }
        CatalogFacilityEnablement enablement = loginBusinessAccessQueryService.facilitiesEnabledForCatalog(registryCode);
        if (!enablement.restrict()) {
            return ListCut.none();
        }
        String catalog = registryCode.trim();
        if (FACILITY.equals(catalog)) {
            return new ListCut(true, "id", enablement.facilityIds());
        }
        CatalogOwnershipDO declaration = facilityDeclaration(catalog);
        if (declaration == null || Boolean.TRUE.equals(declaration.getMultiValued())) {
            return ListCut.none();
        }
        return new ListCut(true, declaration.getFieldCode(), enablement.facilityIds());
    }

    /** 页面按一座场站查列表时：这座站没开通这本目录所属业务就拒绝。没指定场站时不在这里拒绝。 */
    public void assertOpenedAtFacility(String registryCode, Long facilityId) {
        if (skip(registryCode) || facilityId == null) {
            return;
        }
        CatalogFacilityEnablement enablement = loginBusinessAccessQueryService.facilitiesEnabledForCatalog(registryCode);
        if (!enablement.restrict()) {
            return;
        }
        assertOnFacility(registryCode.trim(), facilityId);
    }

    /** 详情、删除、报修受理：这条记录所在的站没开通这项业务就拒绝。 */
    public void assertRecord(String registryCode, Long entityId) {
        if (skip(registryCode) || entityId == null || entityId <= 0) {
            return;
        }
        CatalogFacilityEnablement enablement = loginBusinessAccessQueryService.facilitiesEnabledForCatalog(registryCode);
        if (!enablement.restrict()) {
            return;
        }
        String catalog = registryCode.trim();
        // 没有「所在场站」这一列时，和列表一样不按场站拒绝。空的场站编号不能当成没开通。
        if (!anchoredOnOneFacility(catalog)) {
            return;
        }
        assertOnFacility(catalog, facilityIdOf(catalog, entityId));
    }

    /** 导出、树上的一批记录：只留所在场站开通了这项业务的。 */
    public List<Long> retain(String registryCode, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        ListCut cut = listCut(registryCode);
        if (!cut.apply()) {
            return ids;
        }
        if (cut.allowedIds().isEmpty()) {
            return List.of();
        }
        if ("id".equals(cut.fieldCode())) {
            Set<Long> allowed = new LinkedHashSet<>(cut.allowedIds());
            List<Long> kept = new ArrayList<>();
            for (Long id : ids) {
                if (allowed.contains(id)) {
                    kept.add(id);
                }
            }
            return kept;
        }
        CatalogOwnershipDO declaration = facilityDeclaration(registryCode.trim());
        String column = facilityColumn(registryCode.trim(), declaration);
        if (column == null) {
            return List.of();
        }
        String table = entityTableNameHandler.resolvePhysicalTableName(registryCode.trim());
        if (!safeCode(table) || table.indexOf('.') >= 0) {
            throw new ServiceException(403, DENIED);
        }
        List<Object> args = new ArrayList<>();
        String sql = "SELECT id FROM " + table + " WHERE deleted = false AND id IN ("
                + placeholders(ids, args) + ") AND " + column + " IN (" + placeholders(cut.allowedIds(), args) + ")";
        Set<Long> matched = new LinkedHashSet<>(jdbcTemplate.queryForList(sql, Long.class, args.toArray()));
        List<Long> kept = new ArrayList<>();
        for (Long id : ids) {
            if (matched.contains(id)) {
                kept.add(id);
            }
        }
        return kept;
    }

    /** 新建、修改：提交的场站或库里原来的场站，必须开通这项业务。 */
    public void assertWrite(String registryCode, Long storedEntityId,
                            Map<String, Object> baseFields, Map<String, Object> customFields) {
        if (skip(registryCode)) {
            return;
        }
        CatalogFacilityEnablement enablement = loginBusinessAccessQueryService.facilitiesEnabledForCatalog(registryCode);
        if (!enablement.restrict()) {
            return;
        }
        String catalog = registryCode.trim();
        // 新建场站时这条记录还不存在，不能要求它已经在开通表里。
        if (FACILITY.equals(catalog) && storedEntityId == null) {
            return;
        }
        if (!anchoredOnOneFacility(catalog)) {
            return;
        }
        Long facilityId = FACILITY.equals(catalog) ? storedEntityId : submittedFacilityId(catalog, baseFields, customFields);
        if (facilityId == null && storedEntityId != null) {
            facilityId = facilityIdOf(catalog, storedEntityId);
        }
        // 机关部门只挂组织、不写所在场站。没写场站就不按场站开通拒绝。
        if (facilityId == null || facilityId <= 0) {
            return;
        }
        assertOnFacility(catalog, facilityId);
    }

    /** 场站目录自己，或这本目录声明了指向场站的单值列。没有这一列就不按场站开通来拒绝。 */
    private boolean anchoredOnOneFacility(String catalog) {
        if (FACILITY.equals(catalog)) {
            return true;
        }
        return facilityColumn(catalog, facilityDeclaration(catalog)) != null;
    }

    /** 这本目录按场站开通时，写了所在场站才要求这座站开通业务。没写场站不拒绝。 */
    private void assertOnFacility(String catalog, Long facilityId) {
        List<String> packs = facilityPacks(catalog);
        if (packs.isEmpty() || facilityId == null || facilityId <= 0) {
            return;
        }
        ServiceException denied = null;
        for (String packCode : packs) {
            try {
                managementUnitCapabilityPackService.assertPackEnabledAtAnchor(packCode, FACILITY, facilityId);
                return;
            } catch (ServiceException ex) {
                if (ex.getCode() == null || ex.getCode() != 403) {
                    throw ex;
                }
                denied = ex;
            }
        }
        throw denied == null ? new ServiceException(403, DENIED) : denied;
    }

    private List<String> facilityPacks(String catalog) {
        List<String> packs = new ArrayList<>();
        for (BusinessCapabilityPackDefinition pack : BusinessCapabilityPackRegistry.all()) {
            if (!pack.requiresUnitEnablement() || !pack.catalogCodes().contains(catalog)) {
                continue;
            }
            if (pack.unitEnablementRegistryCodes().contains(FACILITY)) {
                packs.add(pack.code());
            }
        }
        return packs;
    }

    private static String placeholders(List<Long> ids, List<Object> args) {
        StringBuilder builder = new StringBuilder();
        for (Long id : ids) {
            if (!builder.isEmpty()) {
                builder.append(',');
            }
            builder.append('?');
            args.add(id);
        }
        return builder.toString();
    }

    private boolean skip(String registryCode) {
        return SecurityFrameworkUtils.getLoginUserId() == null || !StringUtils.hasText(registryCode);
    }

    private CatalogOwnershipDO facilityDeclaration(String catalog) {
        for (CatalogOwnershipDO row : catalogOwnershipQueryService.listByRegistryCode(catalog)) {
            if (row != null && FACILITY.equals(row.getTargetRegistryCode())) {
                return row;
            }
        }
        return null;
    }

    private Long facilityIdOf(String catalog, Long entityId) {
        if (FACILITY.equals(catalog)) {
            return entityId;
        }
        CatalogOwnershipDO declaration = facilityDeclaration(catalog);
        String column = facilityColumn(catalog, declaration);
        if (column == null) {
            return null;
        }
        String table = entityTableNameHandler.resolvePhysicalTableName(catalog);
        if (!safeCode(table) || table.indexOf('.') >= 0) {
            throw new ServiceException(403, DENIED);
        }
        List<Long> ids = jdbcTemplate.query(
                "SELECT " + column + " FROM " + table + " WHERE id = ? AND deleted = false",
                (rs, row) -> {
                    long value = rs.getLong(1);
                    return rs.wasNull() ? null : value;
                },
                entityId);
        return ids.isEmpty() ? null : ids.get(0);
    }

    private Long submittedFacilityId(String catalog, Map<String, Object> baseFields, Map<String, Object> customFields) {
        CatalogOwnershipDO declaration = facilityDeclaration(catalog);
        if (declaration == null) {
            return null;
        }
        String field = declaration.getFieldCode();
        if (baseFields != null && baseFields.containsKey(field)) {
            return longOf(baseFields.get(field));
        }
        if (customFields != null && customFields.containsKey(field)) {
            return longOf(customFields.get(field));
        }
        return null;
    }

    private Long longOf(Object raw) {
        if (raw instanceof Number number) {
            return number.longValue();
        }
        if (raw instanceof Map<?, ?> map) {
            return longOf(map.get("id"));
        }
        if (raw instanceof String text && !text.isBlank()) {
            try {
                return Long.valueOf(text.trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    /** 单值归属必须是专用列。列名只接受标识符，避免把声明里的字拼进语句。 */
    private String facilityColumn(String catalog, CatalogOwnershipDO declaration) {
        if (declaration == null || Boolean.TRUE.equals(declaration.getMultiValued())) {
            return null;
        }
        EntityTypeDO type = entityTypeMapper.selectByCode(catalog);
        if (type == null) {
            return null;
        }
        String storage = StringUtils.hasText(type.getBaseEntityTypeCode())
                ? type.getBaseEntityTypeCode().trim() : catalog;
        String column = entityDedicatedColumnService.resolvePhysicalColumn(storage, declaration.getFieldCode());
        return safeCode(column) ? column : null;
    }

    private static boolean safeCode(String code) {
        return code != null && code.matches("[A-Za-z_][A-Za-z0-9_]*");
    }
}
