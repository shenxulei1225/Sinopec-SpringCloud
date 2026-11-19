package cn.cheers.x.module.dynamicbusiness.service.datapermission;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.ManagementUnitCatalogDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.ManagementUnitCatalogMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 可管目录的读取，以及由它推出的管理单元目录。
 *
 * <p>场景速查：</p>
 * <ul>
 *   <li>「数据资源管理」页打开时列出已登记的管理单元类型和各自能管的目录 → {@link #getRegistration}</li>
 *   <li>组织架构页、数据范围计算要知道哪本是管理单元目录 → {@link #findManagementUnitRegistryCode}</li>
 *   <li>判断某条任职能不能管某本目录 → {@link #loadCatalogsByModelId}</li>
 * </ul>
 *
 * <p>管理单元目录 = 已登记的管理单元类型（型号）所在的那本目录。一条都没登记时租户没有管理单元目录，
 * 返回 null，调用方显示空态或一条也不给看。登记的型号分在几本目录里、或型号已删除，说明配置坏了，报 400。</p>
 *
 * 不负责：写入和保存前核对（见 {@link ManagementUnitCatalogService}）；算可见记录（见 {@link DataScopeQueryService}）。
 * 禁止：按具体目录编码或型号名写分支。
 */
@Service
public class ManagementUnitCatalogQueryService {

    @Resource
    private ManagementUnitCatalogMapper managementUnitCatalogMapper;
    @Resource
    private ModelMapper modelMapper;

    /** 一个已登记的管理单元类型，以及它能管的目录。 */
    public record ManagementUnitType(Long modelId, String modelName, List<String> registryCodes) {
    }

    /**
     * 「数据资源管理」页的当前登记。
     *
     * @param managementUnitRegistryCode 管理单元目录；还没登记任何类型时为 null
     * @param types                      已登记的管理单元类型，按型号编号排序
     */
    public record Registration(String managementUnitRegistryCode, List<ManagementUnitType> types) {
    }

    public Registration getRegistration() {
        Map<Long, Set<String>> byModel = loadCatalogsByModelId();
        Map<Long, ModelDO> models = requireModels(byModel.keySet());
        List<ManagementUnitType> types = new ArrayList<>();
        byModel.forEach((modelId, codes) -> types.add(
                new ManagementUnitType(modelId, models.get(modelId).getName(), List.copyOf(codes))));
        return new Registration(unitCatalogOf(models), types);
    }

    /** 管理单元目录的注册编码；还没登记任何管理单元类型时返回 null。 */
    public String findManagementUnitRegistryCode() {
        Set<Long> modelIds = loadCatalogsByModelId().keySet();
        return unitCatalogOf(requireModels(modelIds));
    }

    /** 每个已登记的管理单元类型（型号编号）能管哪些目录。一次读全表，调用方在一次请求里复用。 */
    public Map<Long, Set<String>> loadCatalogsByModelId() {
        Map<Long, Set<String>> byModel = new LinkedHashMap<>();
        for (ManagementUnitCatalogDO row : managementUnitCatalogMapper.selectAllOrdered()) {
            byModel.computeIfAbsent(row.getManagementUnitModelId(), id -> new LinkedHashSet<>())
                    .add(row.getRegistryCode().trim());
        }
        return byModel;
    }

    private Map<Long, ModelDO> requireModels(Set<Long> modelIds) {
        Map<Long, ModelDO> models = new LinkedHashMap<>();
        if (modelIds.isEmpty()) {
            return models;
        }
        for (ModelDO model : modelMapper.selectBatchIds(modelIds)) {
            models.put(model.getId(), model);
        }
        for (Long modelId : modelIds) {
            if (!models.containsKey(modelId)) {
                throw new ServiceException(400, "可管目录登记的管理单元类型已不存在：型号 " + modelId);
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
            throw new ServiceException(400, "可管目录登记的管理单元类型分在几本目录里：" + catalogs);
        }
        return catalogs.isEmpty() ? null : catalogs.iterator().next();
    }
}
