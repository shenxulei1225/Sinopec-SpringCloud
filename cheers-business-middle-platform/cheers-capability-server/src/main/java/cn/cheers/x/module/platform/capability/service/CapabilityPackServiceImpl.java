package cn.cheers.x.module.platform.capability.service;

import cn.cheers.x.module.platform.capability.api.dto.CapabilityPackRespDTO;
import cn.cheers.x.module.platform.capability.dal.dataobject.CapabilityPackDO;
import cn.cheers.x.module.platform.capability.dal.mysql.CapabilityPackMapper;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static cn.cheers.x.module.platform.capability.enums.ErrorCodeConstants.CAPABILITY_PACK_NOT_FOUND;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
public class CapabilityPackServiceImpl implements CapabilityPackService {

    @Resource
    private CapabilityPackMapper capabilityPackMapper;

    @Override
    public List<CapabilityPackRespDTO> list(String domain) {
        return capabilityPackMapper.selectEnabledList(domain).stream()
                .map(CapabilityPackServiceImpl::toResp)
                .toList();
    }

    @Override
    public CapabilityPackRespDTO getById(String packId) {
        return toResp(requireById(packId));
    }

    @Override
    public CapabilityPackDO requireById(String packId) {
        CapabilityPackDO pack = capabilityPackMapper.selectById(packId);
        if (pack == null) {
            throw exception(CAPABILITY_PACK_NOT_FOUND);
        }
        return pack;
    }

    static CapabilityPackRespDTO toResp(CapabilityPackDO pack) {
        return CapabilityPackRespDTO.builder()
                .capabilityPackId(pack.getId())
                .displayName(pack.getDisplayName())
                .domain(pack.getDomain())
                .version(pack.getVersion())
                .requiredEngines(parseStringList(pack.getRequiredEngines()))
                .orchestrationRef(pack.getOrchestrationRef())
                .defaultPolicyTemplateId(pack.getDefaultPolicyTemplateId())
                .manifest(parseMap(pack.getManifest()))
                .build();
    }

    private static List<String> parseStringList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        return JSON.parseArray(json, String.class);
    }

    private static Map<String, Object> parseMap(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        return JSON.parseObject(json, new TypeReference<Map<String, Object>>() {
        });
    }
}
