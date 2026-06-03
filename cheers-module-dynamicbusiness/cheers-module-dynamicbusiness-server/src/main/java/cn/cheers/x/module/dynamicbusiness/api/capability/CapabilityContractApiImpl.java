package cn.cheers.x.module.dynamicbusiness.api.capability;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.cheers.x.module.dynamicbusiness.api.capability.dto.CapabilityInstanceSummaryDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.ComponentCapabilityViewRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.InstanceCapabilitySummaryRespVO;
import cn.cheers.x.module.dynamicbusiness.service.capability.CapabilityRegistryRebuildService;
import cn.cheers.x.module.dynamicbusiness.service.capability.CapabilityRegistryService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class CapabilityContractApiImpl implements CapabilityContractApi {

    @Resource
    private CapabilityRegistryService capabilityRegistryService;
    @Resource
    private CapabilityRegistryRebuildService capabilityRegistryRebuildService;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public CommonResult<List<CapabilityInstanceSummaryDTO>> listInstances(
            String businessTypeCode, String domain) {
        List<InstanceCapabilitySummaryRespVO> rows =
                capabilityRegistryService.listInstances(businessTypeCode, domain);
        return success(BeanUtils.toBean(rows, CapabilityInstanceSummaryDTO.class));
    }

    @Override
    public CommonResult<Map<String, Object>> getContract(String instanceKey, boolean rebuildIfMissing) {
        if (rebuildIfMissing) {
            return success(capabilityRegistryService.getContractOrRebuild(instanceKey));
        }
        return success(capabilityRegistryService.getContract(instanceKey));
    }

    @Override
    public CommonResult<Map<String, Object>> getComponentView(
            String instanceKey, String componentCode, boolean rebuildIfMissing) {
        ComponentCapabilityViewRespVO view =
                capabilityRegistryService.getComponentView(instanceKey, componentCode, rebuildIfMissing);
        Map<String, Object> body = objectMapper.convertValue(view, new TypeReference<Map<String, Object>>() {});
        return success(body);
    }

    @Override
    public CommonResult<Boolean> rebuildAllSystemCapabilities() {
        capabilityRegistryRebuildService.rebuildAllSystemCapabilities();
        return success(true);
    }

    @Override
    public CommonResult<Boolean> rebuildAllDynamicCapabilities() {
        capabilityRegistryRebuildService.rebuildAllDynamicCapabilities();
        return success(true);
    }
}
