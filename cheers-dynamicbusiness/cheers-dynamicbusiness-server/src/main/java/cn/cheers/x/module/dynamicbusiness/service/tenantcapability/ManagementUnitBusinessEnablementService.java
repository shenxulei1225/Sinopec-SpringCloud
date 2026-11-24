package cn.cheers.x.module.dynamicbusiness.service.tenantcapability;

import java.util.List;

/**
 * 管理点上的业务勾选（数据目录粒度，与登记区同款列表）。
 * 保存后同步推导业务能力包启用行，供接口校验使用。
 */
public interface ManagementUnitBusinessEnablementService {

    /** 本租户已开通能力范围内、可在管理点上勾选的业务目录编码。 */
    List<String> listSelectableCatalogCodes();

    List<String> listEnabledCatalogCodes(String managementUnitRegistryCode, Long managementUnitId);

    void replaceEnabledCatalogs(String managementUnitRegistryCode, Long managementUnitId, List<String> catalogCodes);
}