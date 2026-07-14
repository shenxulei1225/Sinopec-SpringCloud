package cn.cheers.x.module.platform.topology.service;

import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PortalDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.topology.api.dto.TopologyValidateRespDTO;

import java.util.List;

public interface PathNetworkService {

    PathNetworkDTO getNetwork(String networkRef);

    PathNetworkDTO getDraft(Long facilityId, NetworkKind networkKind);

    PathNetworkDTO saveDraft(PathNetworkDTO request);

    TopologyValidateRespDTO validate(PathNetworkDTO request);

    PathNetworkDTO publish(Long facilityId, NetworkKind networkKind);

    List<PortalDTO> listPortals(Long facilityId);

    List<PortalDTO> savePortals(Long facilityId, List<PortalDTO> portals);
}
