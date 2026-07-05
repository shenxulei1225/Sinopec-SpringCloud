package cn.cheers.x.module.platform.capability.service;

import cn.cheers.x.module.platform.capability.api.dto.MappingProfileRespDTO;
import cn.cheers.x.module.platform.capability.api.dto.MappingProfileSaveReqDTO;
import cn.cheers.x.module.platform.capability.api.dto.ResolveWorkItemsReqDTO;
import cn.cheers.x.module.platform.capability.dal.dataobject.MappingProfileDO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;

import java.util.List;

public interface MappingProfileService {

    MappingProfileRespDTO save(MappingProfileSaveReqDTO request);

    MappingProfileRespDTO get(String id);

    MappingProfileDO requireById(String id);

    List<WorkItemDTO> resolveWorkItems(String profileId, ResolveWorkItemsReqDTO request);
}
