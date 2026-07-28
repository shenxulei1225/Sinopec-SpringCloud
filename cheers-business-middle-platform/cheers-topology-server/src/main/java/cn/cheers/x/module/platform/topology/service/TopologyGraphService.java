package cn.cheers.x.module.platform.topology.service;

import cn.cheers.x.module.platform.contract.dto.topology.TopologyGraphDTO;
import cn.cheers.x.module.platform.topology.api.dto.TopologyGraphSaveReqDTO;
import cn.cheers.x.module.platform.topology.api.dto.TopologyValidateRespDTO;

public interface TopologyGraphService {

    TopologyGraphDTO getGraph(String topologyRef);

    TopologyGraphDTO getDraftByFacilityId(Long facilityId);

    TopologyGraphDTO saveDraft(Long facilityId, TopologyGraphSaveReqDTO request);

    TopologyGraphDTO publish(Long facilityId);

    TopologyValidateRespDTO validate(Long facilityId, TopologyGraphSaveReqDTO request);

    TopologyGraphDTO importLegacy(Long facilityId);
}
