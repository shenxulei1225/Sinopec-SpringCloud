package cn.cheers.x.module.dynamicbusiness.service.tenantcapability;

import java.util.List;

/**
 * 管理单元（及场站、管线段等锚点）上的业务能力启用。
 * 负责：在租户已开通前提下登记/查询/校验某锚点启用了哪些包。
 * 不负责：超级管理员租户开通；不负责职务与角色。
 */
public interface ManagementUnitCapabilityPackService {

    /** 当前租户已开通、且可按锚点启用的业务能力包定义。 */
    List<UnitEnablementPackOption> listEnablementOptionsForRegistry(String registryCode);

    List<String> listEnabledPackCodes(String registryCode, Long managementUnitId);

    void replaceEnabledPacks(String registryCode, Long managementUnitId, List<String> packCodes);

    /**
     * 查数、保存、报修受理经开通判断调用本方法：租户已开通该包，且这座场站已登记启用。
     * 不按场站开通的包不要调用本方法。
     */
    void assertPackEnabledAtAnchor(String packCode, String registryCode, Long managementUnitId);
}