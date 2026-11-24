package cn.cheers.x.module.dynamicbusiness.service.tenantcapability;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.tenantcapability.ManagementUnitBusinessCatalogDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.tenantcapability.ManagementUnitBusinessCatalogMapper;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.ManagementUnitCatalogQueryService;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 管理点业务勾选：界面是数据目录清单（与登记区同款），落库 management_unit_business_catalog。
 * 保存后按勾选目录反推业务能力包，写入 management_unit_capability_pack 供接口校验。
 */
@Service
public class ManagementUnitBusinessEnablementServiceImpl implements ManagementUnitBusinessEnablementService {

    @Resource
    private ManagementUnitBusinessCatalogMapper managementUnitBusinessCatalogMapper;
    @Resource
    private ManagementUnitCapabilityPackService managementUnitCapabilityPackService;
    @Resource
    private TenantCapabilityPackService tenantCapabilityPackService;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private EntityTableNameHandler entityTableNameHandler;
    @Resource
    private ManagementUnitCatalogQueryService managementUnitCatalogQueryService;

    @Override
    public List<String> listSelectableCatalogCodes() {
        Long tenantId = requireTenantId();
        Set<String> openedCatalogs = new LinkedHashSet<>(
                tenantCapabilityPackService.resolveOpenedScope(tenantId).catalogCodes());
        if (openedCatalogs.isEmpty()) {
            return List.of();
        }
        List<String> selectable = new ArrayList<>();
        for (EntityTypeDO row : entityTypeMapper.selectAllList()) {
            if (row == null || Boolean.TRUE.equals(row.getDeleted()) || !StringUtils.hasText(row.getCode())) {
                continue;
            }
            String code = row.getCode().trim();
            if (openedCatalogs.contains(code)) {
                selectable.add(code);
            }
        }
        return selectable;
    }

    @Override
    public List<String> listEnabledCatalogCodes(String managementUnitRegistryCode, Long managementUnitId) {
        String registry = requireRegistryCode(managementUnitRegistryCode);
        Long unitId = requireUnitId(managementUnitId);
        Long tenantId = requireTenantId();
        return managementUnitBusinessCatalogMapper.selectByAnchor(tenantId, registry, unitId).stream()
                .map(ManagementUnitBusinessCatalogDO::getCatalogRegistryCode)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceEnabledCatalogs(
            String managementUnitRegistryCode, Long managementUnitId, List<String> catalogCodes) {
        String registry = requireRegistryCode(managementUnitRegistryCode);
        Long unitId = requireUnitId(managementUnitId);
        Long tenantId = requireTenantId();
        if (!managementUnitCatalogQueryService.isRegistryAdded(registry)) {
            throw new ServiceException(400, "这本目录还没添加为管理单元");
        }
        assertAnchorEntityExists(tenantId, registry, unitId);

        Set<String> allowed = new LinkedHashSet<>(listSelectableCatalogCodes());
        Set<String> wanted = new LinkedHashSet<>();
        if (catalogCodes != null) {
            for (String raw : catalogCodes) {
                if (!StringUtils.hasText(raw)) {
                    continue;
                }
                String code = raw.trim();
                if (!allowed.contains(code)) {
                    throw new ServiceException(400, "本租户未开通该业务目录：" + code);
                }
                wanted.add(code);
            }
        }

        managementUnitBusinessCatalogMapper.deleteByAnchor(tenantId, registry, unitId);
        for (String catalogCode : wanted) {
            ManagementUnitBusinessCatalogDO row = new ManagementUnitBusinessCatalogDO();
            row.setTenantId(tenantId);
            row.setManagementUnitRegistryCode(registry);
            row.setManagementUnitId(unitId);
            row.setCatalogRegistryCode(catalogCode);
            managementUnitBusinessCatalogMapper.insert(row);
        }

        syncPackEnablement(registry, unitId, wanted);
    }

    private void syncPackEnablement(String registry, Long unitId, Set<String> enabledCatalogs) {
        Set<String> openedPacks = new LinkedHashSet<>(tenantCapabilityPackService.listOpenedPackCodes(requireTenantId()));
        List<String> packsToEnable = new ArrayList<>();
        for (BusinessCapabilityPackDefinition pack : BusinessCapabilityPackRegistry.all()) {
            if (!pack.requiresUnitEnablement()) {
                continue;
            }
            if (!pack.unitEnablementRegistryCodes().contains(registry)) {
                continue;
            }
            if (!openedPacks.contains(pack.code())) {
                continue;
            }
            boolean hit = false;
            for (String catalog : pack.catalogCodes()) {
                if (enabledCatalogs.contains(catalog)) {
                    hit = true;
                    break;
                }
            }
            if (hit) {
                packsToEnable.add(pack.code());
            }
        }
        managementUnitCapabilityPackService.replaceEnabledPacks(registry, unitId, packsToEnable);
    }

    private void assertAnchorEntityExists(Long tenantId, String registryCode, Long entityId) {
        String table = entityTableNameHandler.resolvePhysicalTableName(registryCode);
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM dynamicbusiness." + table + " WHERE id = ? AND deleted = false",
                Integer.class,
                entityId);
        if (count == null || count <= 0) {
            throw new ServiceException(400, "管理点不存在或已删除");
        }
    }

    private static Long requireTenantId() {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null || tenantId <= 0) {
            throw new ServiceException(400, "缺少租户");
        }
        return tenantId;
    }

    private static String requireRegistryCode(String registryCode) {
        if (!StringUtils.hasText(registryCode)) {
            throw new ServiceException(400, "缺少管理单元目录编码");
        }
        return registryCode.trim();
    }

    private static Long requireUnitId(Long managementUnitId) {
        if (managementUnitId == null || managementUnitId <= 0) {
            throw new ServiceException(400, "请选择管理点");
        }
        return managementUnitId;
    }
}