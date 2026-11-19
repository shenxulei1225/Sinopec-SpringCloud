package cn.cheers.x.module.dynamicbusiness.service.facility;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.ManagementMethodMapper;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogManagementQueryService.ManagementChoice;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogOwnershipQueryService;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogOwnershipService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 业务开通第一次创建「每条记录属于一座站」的目录时，把它设成「按场站管理」。
 * 和管理员在「数据资源管理」里选「按场站管理」走同一条保存（{@link CatalogOwnershipService#applyManagement}）：
 * 目录上缺「所属场站」就挂上，再写管理方式。
 *
 * <p>只在刚建好目录的那一刻调用（告警、告警规则、告警策略、安防这些目录的开通）。
 * 管理员在界面上新建的目录不走这里，他在新建表单或「数据资源管理」的「数据管理方式」里自己选。</p>
 *
 * 不负责：目录已经存在时补字段或补声明；给已有记录补所属场站；挑站。
 * 禁止：在启动、查询、打开页面、再次开通时调用。
 */
@Slf4j
@Service
public class StationOwnedCatalogSetup {

    @Resource
    private CatalogOwnershipService catalogOwnershipService;
    @Resource
    private CatalogOwnershipQueryService catalogOwnershipQueryService;
    @Resource
    private ManagementMethodMapper managementMethodMapper;

    /**
     * 刚建好的目录设成按场站管理。
     * 「按场站管理」还不可选（租户没登记这种方式，或场站对应不到单位）时，自有存储的目录仍挂上「所属场站」，
     * 只是先不写管理方式。等租户登记后，管理员在「数据管理方式」里勾上。
     *
     * @param registryCode 刚建好的目录注册编码
     * @param ownStorage   true 表示目录有自己的表；使用已有数据的目录跟底座走，底座上要已有指向场站的字段
     * @throws ServiceException 字段库里「所属场站」编码被别的字段占用、或底座没有指向场站的字段时 400，不跳过
     */
    @Transactional(rollbackFor = Exception.class)
    public void setUpNewCatalog(String registryCode, boolean ownStorage) {
        String station = FacilityOwningFieldCodes.TARGET_ENTITY_TYPE;
        boolean stationIsMethod = managementMethodMapper.selectRegistryCodesOrdered().contains(station);
        if (stationIsMethod && catalogOwnershipQueryService.reachesManagementUnit(station)) {
            catalogOwnershipService.applyManagement(registryCode, List.of(new ManagementChoice(station, null)));
            return;
        }
        log.warn("[StationOwnedCatalogSetup] 按场站管理还不可选（未登记为管理方式或对应不到单位），目录 {} 先不写管理方式", registryCode);
        if (ownStorage) {
            catalogOwnershipService.attachManagementField(registryCode, station);
        }
    }
}
