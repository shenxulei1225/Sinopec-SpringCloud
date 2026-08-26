package cn.cheers.x.module.dynamicbusiness.service.model.governance;

/**
 * 全网主数据治理能力端口。
 *
 * <p>Task 10 将接入正式权限来源；当前查询链只依赖此端口，不读取角色码，
 * 也不允许在型号服务中散落临时白名单判断。</p>
 */
public interface MasterDataCapabilityChecker {

    boolean canPromotePackage();

    boolean canCreateCompanyStandard();

    boolean canDeactivateCompanyStandard();

    default boolean canManageNetworkModelData() {
        return canPromotePackage() || canCreateCompanyStandard();
    }
}
