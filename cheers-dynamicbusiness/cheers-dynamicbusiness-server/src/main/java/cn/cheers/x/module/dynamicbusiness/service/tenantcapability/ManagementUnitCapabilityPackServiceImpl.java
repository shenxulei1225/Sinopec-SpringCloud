package cn.cheers.x.module.dynamicbusiness.service.tenantcapability;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.tenantcapability.ManagementUnitCapabilityPackDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.tenantcapability.ManagementUnitCapabilityPackMapper;
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
 * 租户开通之后，在具体管理单元/场站/管线段上登记哪些业务线上线。
 * 保存前核对：包已在租户级开通；包允许挂在这本目录；锚点记录存在。
 */
@Service
public class ManagementUnitCapabilityPackServiceImpl implements ManagementUnitCapabilityPackService {

    @Resource
    private ManagementUnitCapabilityPackMapper managementUnitCapabilityPackMapper;
    @Resource
    private TenantCapabilityPackService tenantCapabilityPackService;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private EntityTableNameHandler entityTableNameHandler;

    @Override
    public List<UnitEnablementPackOption> listEnablementOptionsForRegistry(String registryCode) {
        requireRegistryCode(registryCode);
        Long tenantId = requireTenantId();
        Set<String> opened = new LinkedHashSet<>(tenantCapabilityPackService.listOpenedPackCodes(tenantId));
        List<UnitEnablementPackOption> options = new ArrayList<>();
        for (BusinessCapabilityPackDefinition pack : BusinessCapabilityPackRegistry.all()) {
            if (!opened.contains(pack.code())) {
                continue;
            }
            options.add(new UnitEnablementPackOption(pack.code(), pack.name(), pack.description()));
        }
        return options;
    }

    @Override
    public List<String> listEnabledPackCodes(String registryCode, Long managementUnitId) {
        String code = requireRegistryCode(registryCode);
        Long unitId = requireUnitId(managementUnitId);
        Long tenantId = requireTenantId();
        return managementUnitCapabilityPackMapper.selectByAnchor(tenantId, code, unitId).stream()
                .map(ManagementUnitCapabilityPackDO::getPackCode)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceEnabledPacks(String registryCode, Long managementUnitId, List<String> packCodes) {
        String code = requireRegistryCode(registryCode);
        Long unitId = requireUnitId(managementUnitId);
        Long tenantId = requireTenantId();
        assertAnchorEntityExists(tenantId, code, unitId);

        List<String> wanted = normalizePackCodes(code, packCodes);
        managementUnitCapabilityPackMapper.deleteByAnchor(tenantId, code, unitId);
        for (String packCode : wanted) {
            ManagementUnitCapabilityPackDO row = new ManagementUnitCapabilityPackDO();
            row.setTenantId(tenantId);
            row.setPackCode(packCode);
            row.setManagementUnitRegistryCode(code);
            row.setManagementUnitId(unitId);
            managementUnitCapabilityPackMapper.insert(row);
        }
    }

    @Override
    public void assertPackEnabledAtAnchor(String packCode, String registryCode, Long managementUnitId) {
        if (!StringUtils.hasText(packCode)) {
            throw new ServiceException(400, "缺少业务能力包编码");
        }
        String pack = packCode.trim();
        Long tenantId = requireTenantId();
        if (!tenantCapabilityPackService.listOpenedPackCodes(tenantId).contains(pack)) {
            throw new ServiceException(403, "本租户未开通该业务能力");
        }
        if (BusinessCapabilityPackRegistry.find(pack).isEmpty()) {
            throw new ServiceException(400, "未知业务能力包");
        }
        if (!StringUtils.hasText(registryCode) || managementUnitId == null || managementUnitId <= 0) {
            throw new ServiceException(400, "该业务须在指定记录上启用后才能操作");
        }
        String registry = registryCode.trim();
        if (managementUnitCapabilityPackMapper.countEnabled(tenantId, pack, registry, managementUnitId) <= 0) {
            throw new ServiceException(403, "这条记录尚未启用此业务");
        }
    }

    private List<String> normalizePackCodes(String registryCode, List<String> packCodes) {
        Long tenantId = requireTenantId();
        Set<String> opened = new LinkedHashSet<>(tenantCapabilityPackService.listOpenedPackCodes(tenantId));
        Set<String> kept = new LinkedHashSet<>();
        if (packCodes != null) {
            for (String raw : packCodes) {
                if (!StringUtils.hasText(raw)) {
                    continue;
                }
                String packCode = raw.trim();
                if (BusinessCapabilityPackRegistry.find(packCode).isEmpty()) {
                    throw new ServiceException(400, "未知业务：" + packCode);
                }
                if (!opened.contains(packCode)) {
                    throw new ServiceException(400, "本租户尚未开通该业务：" + packCode);
                }
                kept.add(packCode);
            }
        }
        return List.copyOf(kept);
    }

    private void assertAnchorEntityExists(Long tenantId, String registryCode, Long entityId) {
        String table = entityTableNameHandler.resolvePhysicalTableName(registryCode);
        if (!StringUtils.hasText(table)) {
            throw new ServiceException(400, "数据目录不存在：" + registryCode);
        }
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM dynamicbusiness." + table + " WHERE id = ? AND deleted = false",
                Integer.class,
                entityId);
        if (count == null || count <= 0) {
            throw new ServiceException(400, "锚点记录不存在或已删除");
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
            throw new ServiceException(400, "缺少数据目录编码");
        }
        return registryCode.trim();
    }

    private static Long requireUnitId(Long managementUnitId) {
        if (managementUnitId == null || managementUnitId <= 0) {
            throw new ServiceException(400, "请选择管理单元或场站");
        }
        return managementUnitId;
    }
}