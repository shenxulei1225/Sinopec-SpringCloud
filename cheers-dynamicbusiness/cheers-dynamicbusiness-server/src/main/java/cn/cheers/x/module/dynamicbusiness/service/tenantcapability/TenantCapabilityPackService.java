package cn.cheers.x.module.dynamicbusiness.service.tenantcapability;

import java.util.List;

/**
 * 业务租户的业务能力包开通。
 * 负责：列出定义、读已开通、整份保存开通并编排目录与租户管理员查看动作。
 * 不负责：登录入口；不给普通角色自动勾权限。
 */
public interface TenantCapabilityPackService {

    List<BusinessCapabilityPackDefinition> listDefinitions();

    List<String> listOpenedPackCodes(Long tenantId);

    /**
     * 每个已开通业务，以及本租户勾上的页面。
     * 旧数据没按页面保存时，返回这一包当前的全部页面。
     */
    List<TenantPackPageSelection> listOpenedSelections(Long tenantId);

    /**
     * 已开通包编码，以及这些包里已勾页面、目录编码并集。
     * 平台租户或无效租户返回空列表。未勾的页面不进入范围。
     */
    TenantCapabilityOpenedScope resolveOpenedScope(Long tenantId);

    /**
     * 用包编码整份替换开通结果，每一包按全部页面开通。
     * 对新开通的包：从模板租户挂目录、给 tenant_admin 勾这些页面的查看、补目录授权。
     * 去掉的包只从开通表移除，不删已有目录与数据。
     */
    void replaceOpenedPacks(Long tenantId, List<String> packCodes);

    /**
     * 用「业务 + 勾选页面」整份替换开通结果。
     * pageCodes 为 null 的一项按该包全部页面开通。
     * 有页面的业务一项都没勾，则这一包不保留。
     */
    void replaceOpenedSelections(Long tenantId, List<TenantPackPageSelection> selections);
}
