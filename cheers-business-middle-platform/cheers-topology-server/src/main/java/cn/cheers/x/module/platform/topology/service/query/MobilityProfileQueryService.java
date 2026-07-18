package cn.cheers.x.module.platform.topology.service.query;

import cn.cheers.x.module.platform.contract.dto.network.MobilityProfileDTO;

import java.util.List;

public interface MobilityProfileQueryService {

    List<MobilityProfileDTO> listProfiles();

    MobilityProfileDTO getProfile(String profileId);
}
