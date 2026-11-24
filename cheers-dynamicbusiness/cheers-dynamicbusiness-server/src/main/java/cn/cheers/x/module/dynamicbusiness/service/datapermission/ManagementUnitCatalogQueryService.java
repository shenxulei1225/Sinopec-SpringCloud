package cn.cheers.x.module.dynamicbusiness.service.datapermission;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.ManagementUnitCatalogDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.ManagementUnitRegistryDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.ManagementUnitCatalogMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.ManagementUnitRegistryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.ManagementUnitTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 管理单元类型登记、已添加的管理单元目录、按管理点读取可管目录，以及推出数据范围用的那一本管理单元目录。
 *
 * <p>不负责：写入（见 {@link ManagementUnitCatalogService}）；算可见记录（见 {@link DataScopeQueryService}）。</p>
 */
@Service
public class ManagementUnitCatalogQueryService {

    @Resource
    private ManagementUnitCatalogMapper managementUnitCatalogMapper;
    @Resource
    private ManagementUnitTypeMapper managementUnitTypeMapper;
    @Resource
    private ManagementUnitRegistryMapper managementUnitRegistryMapper;
    @Resource
    private ModelMapper modelMapper;

    /** 已登记的管理单元类型（不含可管目录清单，清单按管理点读）。 */
    public record ManagementUnitType(Long modelId, String modelName, List<String> registryCodes) {
    }

    public record Registration(String managementUnitRegistryCode, List<ManagementUnitType> types) {
    }

    public Registration getRegistration() {
        Set<Long> modelIds = managementUnitTypeMapper.selectRegisteredModelIds();
        Map<Long, ModelDO> models = requireModels(modelIds);
        List<ManagementUnitType> types = new ArrayList<>();
        for (Long modelId : modelIds) {
            ModelDO model = models.get(modelId);
            types.add(new ManagementUnitType(modelId, model.getName(), List.of()));
        }
        return new Registration(unitCatalogOf(models), types);
    }

    /** 业务开通已添加的数据目录，按添加顺序。空列表表示还没添加。 */
    public List<String> listRegistryCodes() {
        List<String> codes = new ArrayList<>();
        for (ManagementUnitRegistryDO row : managementUnitRegistryMapper.selectAllOrdered()) {
            if (StringUtils.hasText(row.getRegistryCode())) {
                codes.add(row.getRegistryCode().trim());
            }
        }
        return List.copyOf(codes);
    }

    public boolean isRegistryAdded(String registryCode) {
        if (!StringUtils.hasText(registryCode)) {
            return false;
        }
        String code = registryCode.trim();
        return listRegistryCodes().contains(code);
    }

    public String findManagementUnitRegistryCode() {
        Set<Long> modelIds = managementUnitTypeMapper.selectRegisteredModelIds();
        if (modelIds.isEmpty()) {
            return null;
        }
        return unitCatalogOf(requireModels(modelIds));
    }

    public boolean isModelRegistered(Long modelId) {
        return modelId != null && managementUnitTypeMapper.selectRegisteredModelIds().contains(modelId);
    }

    /** 某管理点登记的可管数据目录。 */
    public Set<String> loadCatalogsForUnit(String unitRegistryCode, Long managementUnitId) {
        if (!StringUtils.hasText(unitRegistryCode) || managementUnitId == null) {
            return Set.of();
        }
        String registry = unitRegistryCode.trim();
        Set<String> codes = new LinkedHashSet<>();
        for (ManagementUnitCatalogDO row : managementUnitCatalogMapper.selectByAnchor(registry, managementUnitId)) {
            if (StringUtils.hasText(row.getRegistryCode())) {
                codes.add(row.getRegistryCode().trim());
            }
        }
        return Set.copyOf(codes);
    }

    /** 列出某管理点已登记的可管目录（有序列表，供接口返回）。 */
    public List<String> listCatalogCodesForUnit(String unitRegistryCode, Long managementUnitId) {
        return new ArrayList<>(loadCatalogsForUnit(unitRegistryCode, managementUnitId));
    }

    private Map<Long, ModelDO> requireModels(Set<Long> modelIds) {
        Map<Long, ModelDO> models = new java.util.LinkedHashMap<>();
        if (modelIds.isEmpty()) {
            return models;
        }
        for (ModelDO model : modelMapper.selectBatchIds(modelIds)) {
            models.put(model.getId(), model);
        }
        for (Long modelId : modelIds) {
            if (!models.containsKey(modelId)) {
                throw new ServiceException(400, "登记的管理单元类型已不存在：型号 " + modelId);
            }
        }
        return models;
    }

    private static String unitCatalogOf(Map<Long, ModelDO> models) {
        Set<String> catalogs = new LinkedHashSet<>();
        for (ModelDO model : models.values()) {
            if (StringUtils.hasText(model.getEntityTypeCode())) {
                catalogs.add(model.getEntityTypeCode().trim());
            }
        }
        if (catalogs.size() > 1) {
            throw new ServiceException(400, "管理单元类型分在几本目录里：" + catalogs);
        }
        return catalogs.isEmpty() ? null : catalogs.iterator().next();
    }
}