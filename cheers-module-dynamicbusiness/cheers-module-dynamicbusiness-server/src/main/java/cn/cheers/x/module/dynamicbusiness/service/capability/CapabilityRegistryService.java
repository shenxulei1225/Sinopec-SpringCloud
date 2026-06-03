package cn.cheers.x.module.dynamicbusiness.service.capability;

import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.ComponentCapabilityViewRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.InstanceCapabilitySummaryRespVO;

import java.util.List;
import java.util.Map;

/**
 * 实例能力注册表查询服务
 */
public interface CapabilityRegistryService {

    List<InstanceCapabilitySummaryRespVO> listInstances(String businessTypeCode, String domain);

    Map<String, Object> getContract(String instanceKey);

    List<Map<String, Object>> getFilters(String instanceKey);

    Map<String, Object> getContractOrRebuild(String instanceKey);

    /**
     * 按组件类型投影能力视图（list 仅 list/page + CRUD；tree 仅 tree 读端点）。
     */
    List<Map<String, Object>> getAsyncChecks(String instanceKey);

    ComponentCapabilityViewRespVO getComponentView(String instanceKey, String componentCode, boolean rebuildIfMissing);
}
