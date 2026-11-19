package cn.cheers.x.module.dynamicbusiness.service.datapermission;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.CatalogOwnershipDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.ManagementMethodDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.CatalogOwnershipMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.ManagementMethodMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 「数据资源管理」第一个标签里整份保存可选的管理方式：数据能按哪几本目录管理，以及在表格里的列顺序。
 * 登记了以后，「数据管理方式」表格多一列，新建目录表单的「数据怎么管」多一项。
 *
 * <p>保存前核对，任何一项不满足就拒绝保存，不替它修：</p>
 * <ul>
 *   <li>每本目录都存在，不重复。</li>
 *   <li>新加的目录能对应到单位（是单位目录本身，或已按某种方式管理、一路能到单位）。</li>
 *   <li>要去掉的方式没有目录在用。在用就先在「数据管理方式」里把那些目录改掉。</li>
 * </ul>
 *
 * 删除数据目录时：它是在用的方式时，归属声明那边已经拒绝删除；否则去掉它这种方式。
 *
 * 不负责：哪本目录用哪种方式（见 {@link CatalogOwnershipService}）；列出还能加哪些目录（见 {@link CatalogManagementQueryService}）。
 * 禁止：在启动、查询、打开页面时补登记；按具体目录编码写分支。
 */
@Service
public class ManagementMethodService {

    @Resource
    private ManagementMethodMapper managementMethodMapper;
    @Resource
    private CatalogOwnershipMapper catalogOwnershipMapper;
    @Resource
    private CatalogOwnershipQueryService catalogOwnershipQueryService;
    @Resource
    private EntityTypeMapper entityTypeMapper;

    /**
     * 整份替换可选的管理方式，列表顺序就是表格列顺序。
     *
     * @param registryCodes 用来管理数据的目录注册编码；空列表表示一种都不登记（还有目录在用时拒绝）
     */
    @Transactional(rollbackFor = Exception.class)
    public void replaceAll(List<String> registryCodes) {
        Set<String> next = new LinkedHashSet<>();
        for (String code : registryCodes == null ? List.<String>of() : registryCodes) {
            if (!StringUtils.hasText(code) || !next.add(code.trim())) {
                throw new ServiceException(400, "管理方式不能为空，也不能重复：" + code);
            }
        }
        List<String> current = managementMethodMapper.selectRegistryCodesOrdered();
        for (String code : next) {
            EntityTypeDO catalog = entityTypeMapper.selectByCode(code);
            if (catalog == null) {
                throw new ServiceException(400, "数据目录不存在：" + code);
            }
            if (!current.contains(code) && !catalogOwnershipQueryService.reachesManagementUnit(code)) {
                throw new ServiceException(400, "「" + catalog.getName() + "」还对应不到任何组织，不能当管理方式");
            }
        }
        for (String removed : current) {
            if (!next.contains(removed)) {
                assertNotInUse(removed);
            }
        }
        managementMethodMapper.deleteAllRows();
        int sort = 1;
        for (String code : next) {
            ManagementMethodDO row = new ManagementMethodDO();
            row.setRegistryCode(code);
            row.setSort(sort++);
            managementMethodMapper.insert(row);
        }
    }

    /** 删除数据目录时调用：去掉以它为方式的那一项。在用时由归属声明那边先拒绝删除。 */
    @Transactional(rollbackFor = Exception.class)
    public void deleteMethodOfDeletedCatalog(String registryCode) {
        if (!StringUtils.hasText(registryCode)) {
            throw new ServiceException(400, "缺少数据目录注册编码");
        }
        String code = registryCode.trim();
        assertNotInUse(code);
        managementMethodMapper.deleteByRegistryCode(code);
    }

    private void assertNotInUse(String method) {
        List<String> users = catalogOwnershipMapper.selectListByTargetRegistryCode(method).stream()
                .map(CatalogOwnershipDO::getRegistryCode).distinct()
                .map(code -> {
                    EntityTypeDO catalog = entityTypeMapper.selectByCode(code);
                    return catalog != null && StringUtils.hasText(catalog.getName()) ? catalog.getName() : code;
                }).toList();
        if (!users.isEmpty()) {
            EntityTypeDO catalog = entityTypeMapper.selectByCode(method);
            String name = catalog != null && StringUtils.hasText(catalog.getName()) ? catalog.getName() : method;
            throw new ServiceException(400, "这些目录按" + name + "管理，先在「数据管理方式」里改掉：" + String.join("、", users));
        }
    }
}
