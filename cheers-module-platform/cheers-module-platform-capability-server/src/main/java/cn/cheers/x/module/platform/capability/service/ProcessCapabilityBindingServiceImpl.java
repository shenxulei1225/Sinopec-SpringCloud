package cn.cheers.x.module.platform.capability.service;

import cn.cheers.x.module.platform.capability.api.dto.ProcessCapabilityBindingRespDTO;
import cn.cheers.x.module.platform.capability.api.dto.ProcessCapabilityBindingSaveReqDTO;
import cn.cheers.x.module.platform.capability.dal.dataobject.CapabilityPackDO;
import cn.cheers.x.module.platform.capability.dal.dataobject.ProcessCapabilityBindingDO;
import cn.cheers.x.module.platform.capability.dal.mysql.ProcessCapabilityBindingMapper;
import cn.cheers.x.module.platform.capability.enums.BindingStatus;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

import static cn.cheers.x.module.platform.capability.enums.ErrorCodeConstants.BINDING_NOT_FOUND;
import static cn.cheers.x.module.platform.capability.enums.ErrorCodeConstants.BINDING_NOT_PUBLISHED;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
public class ProcessCapabilityBindingServiceImpl implements ProcessCapabilityBindingService {

    @Resource
    private ProcessCapabilityBindingMapper processCapabilityBindingMapper;
    @Resource
    private CapabilityPackService capabilityPackService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessCapabilityBindingRespDTO saveBinding(String entityTypeCode,
                                                       ProcessCapabilityBindingSaveReqDTO request) {
        CapabilityPackDO pack = capabilityPackService.requireById(request.getCapabilityPackId());
        String orchestrationRef = StringUtils.hasText(request.getOrchestrationRef())
                ? request.getOrchestrationRef()
                : pack.getOrchestrationRef();

        ProcessCapabilityBindingDO existing = processCapabilityBindingMapper.selectByEntityTypeCode(entityTypeCode);
        if (existing == null) {
            ProcessCapabilityBindingDO binding = ProcessCapabilityBindingDO.builder()
                    .entityTypeCode(entityTypeCode)
                    .capabilityPackId(request.getCapabilityPackId())
                    .orchestrationRef(orchestrationRef)
                    .policySetId(request.getPolicySetId())
                    .mappingProfileIds(toJsonArray(request.getMappingProfileIds()))
                    .engineBindings(toJsonObject(request.getEngineBindings()))
                    .status(BindingStatus.DRAFT.name())
                    .version(0)
                    .build();
            processCapabilityBindingMapper.insert(binding);
            return toResp(binding);
        }

        existing.setCapabilityPackId(request.getCapabilityPackId());
        existing.setOrchestrationRef(orchestrationRef);
        existing.setPolicySetId(request.getPolicySetId());
        existing.setMappingProfileIds(toJsonArray(request.getMappingProfileIds()));
        existing.setEngineBindings(toJsonObject(request.getEngineBindings()));
        existing.setStatus(BindingStatus.DRAFT.name());
        processCapabilityBindingMapper.updateById(existing);
        return toResp(existing);
    }

    @Override
    public ProcessCapabilityBindingRespDTO getBinding(String entityTypeCode) {
        return toResp(requireExisting(entityTypeCode));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessCapabilityBindingRespDTO publishBinding(String entityTypeCode) {
        ProcessCapabilityBindingDO binding = requireExisting(entityTypeCode);
        binding.setStatus(BindingStatus.PUBLISHED.name());
        binding.setVersion(binding.getVersion() + 1);
        processCapabilityBindingMapper.updateById(binding);
        return toResp(binding);
    }

    @Override
    public ProcessCapabilityBindingRespDTO getPublishedBinding(String entityTypeCode) {
        ProcessCapabilityBindingDO binding = requireExisting(entityTypeCode);
        if (!BindingStatus.PUBLISHED.name().equals(binding.getStatus())) {
            throw exception(BINDING_NOT_PUBLISHED);
        }
        return toResp(binding);
    }

    private ProcessCapabilityBindingDO requireExisting(String entityTypeCode) {
        ProcessCapabilityBindingDO binding = processCapabilityBindingMapper.selectByEntityTypeCode(entityTypeCode);
        if (binding == null) {
            throw exception(BINDING_NOT_FOUND);
        }
        return binding;
    }

    static ProcessCapabilityBindingRespDTO toResp(ProcessCapabilityBindingDO binding) {
        return ProcessCapabilityBindingRespDTO.builder()
                .entityTypeCode(binding.getEntityTypeCode())
                .capabilityPackId(binding.getCapabilityPackId())
                .orchestrationRef(binding.getOrchestrationRef())
                .policySetId(binding.getPolicySetId())
                .mappingProfileIds(parseStringList(binding.getMappingProfileIds()))
                .engineBindings(parseMap(binding.getEngineBindings()))
                .status(binding.getStatus())
                .version(binding.getVersion())
                .build();
    }

    private static String toJsonArray(List<String> values) {
        return JSON.toJSONString(values != null ? values : List.of());
    }

    private static String toJsonObject(Map<String, Object> values) {
        return JSON.toJSONString(values != null ? values : Map.of());
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
        return JSON.parseObject(json, new com.alibaba.fastjson2.TypeReference<Map<String, Object>>() {
        });
    }
}
