package cn.cheers.x.module.platform.capability.service;

import cn.cheers.x.module.platform.capability.api.dto.MappingProfileRespDTO;
import cn.cheers.x.module.platform.capability.api.dto.MappingProfileSaveReqDTO;
import cn.cheers.x.module.platform.capability.api.dto.ResolveWorkItemsReqDTO;
import cn.cheers.x.module.platform.capability.dal.dataobject.MappingProfileDO;
import cn.cheers.x.module.platform.capability.dal.mysql.MappingProfileMapper;
import cn.cheers.x.module.platform.contract.ContractVersions;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.cheers.x.module.platform.capability.enums.ErrorCodeConstants.MAPPING_PROFILE_BUSINESS_TYPE_MISMATCH;
import static cn.cheers.x.module.platform.capability.enums.ErrorCodeConstants.MAPPING_PROFILE_NOT_FOUND;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
public class MappingProfileServiceImpl implements MappingProfileService {

    @Resource
    private MappingProfileMapper mappingProfileMapper;
    @Resource
    private WorkItemMappingResolver workItemMappingResolver;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MappingProfileRespDTO save(MappingProfileSaveReqDTO request) {
        MappingProfileDO existing = mappingProfileMapper.selectById(request.getId());
        MappingProfileDO profile = MappingProfileDO.builder()
                .id(request.getId())
                .entityTypeCode(request.getEntityTypeCode())
                .sourceModelCode(request.getSourceModelCode())
                .displayName(request.getDisplayName())
                .fieldMappings(toJsonObject(request.getFieldMappings()))
                .defaultDurationMinutes(defaultInt(request.getDefaultDurationMinutes(), 60))
                .defaultPriority(defaultInt(request.getDefaultPriority(), 5))
                .status("ACTIVE")
                .build();
        if (existing == null) {
            mappingProfileMapper.insert(profile);
        } else {
            mappingProfileMapper.updateById(profile);
        }
        return toResp(profile);
    }

    @Override
    public MappingProfileRespDTO get(String id) {
        return toResp(requireById(id));
    }

    @Override
    public MappingProfileDO requireById(String id) {
        MappingProfileDO profile = mappingProfileMapper.selectById(id);
        if (profile == null) {
            throw exception(MAPPING_PROFILE_NOT_FOUND);
        }
        return profile;
    }

    @Override
    public List<WorkItemDTO> resolveWorkItems(String profileId, ResolveWorkItemsReqDTO request) {
        MappingProfileDO profile = requireById(profileId);
        if (!profile.getEntityTypeCode().equals(request.getEntityTypeCode())) {
            throw exception(MAPPING_PROFILE_BUSINESS_TYPE_MISMATCH);
        }
        List<WorkItemDTO> items = new ArrayList<>();
        for (ResolveWorkItemsReqDTO.SourceInstanceInputDTO instance : request.getInstances()) {
            items.add(workItemMappingResolver.resolve(profile, request.getEntityTypeCode(), instance));
        }
        return items;
    }

    static MappingProfileRespDTO toResp(MappingProfileDO profile) {
        return MappingProfileRespDTO.builder()
                .id(profile.getId())
                .entityTypeCode(profile.getEntityTypeCode())
                .sourceModelCode(profile.getSourceModelCode())
                .displayName(profile.getDisplayName())
                .fieldMappings(parseStringMap(profile.getFieldMappings()))
                .defaultDurationMinutes(profile.getDefaultDurationMinutes())
                .defaultPriority(profile.getDefaultPriority())
                .status(profile.getStatus())
                .build();
    }

    private static int defaultInt(Integer value, int fallback) {
        return value != null ? value : fallback;
    }

    private static String toJsonObject(Map<String, String> values) {
        return JSON.toJSONString(values != null ? values : Map.of());
    }

    private static Map<String, String> parseStringMap(String json) {
        if (!StringUtils.hasText(json)) {
            return Map.of();
        }
        return JSON.parseObject(json, new com.alibaba.fastjson2.TypeReference<Map<String, String>>() {
        });
    }
}
