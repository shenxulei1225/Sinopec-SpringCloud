package cn.cheers.x.module.dynamicbusiness.service.capability;

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
}
