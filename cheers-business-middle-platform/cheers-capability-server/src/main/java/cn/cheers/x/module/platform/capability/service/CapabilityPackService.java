package cn.cheers.x.module.platform.capability.service;

import cn.cheers.x.module.platform.capability.api.dto.CapabilityPackRespDTO;
import cn.cheers.x.module.platform.capability.dal.dataobject.CapabilityPackDO;

import java.util.List;

public interface CapabilityPackService {

    List<CapabilityPackRespDTO> list(String domain);

    CapabilityPackRespDTO getById(String packId);

    CapabilityPackDO requireById(String packId);
}
