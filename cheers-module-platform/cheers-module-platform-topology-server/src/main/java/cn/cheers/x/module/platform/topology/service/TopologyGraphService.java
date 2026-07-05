package cn.cheers.x.module.platform.topology.service;

import cn.cheers.x.module.platform.contract.dto.topology.TopologyGraphDTO;
import cn.cheers.x.module.platform.topology.api.dto.TopologyGraphSaveReqDTO;
import cn.cheers.x.module.platform.topology.api.dto.TopologyValidateRespDTO;

public interface TopologyGraphService {

    TopologyGraphDTO getGraph(String topologyRef);

    TopologyGraphDTO getDraftBySiteId(Long siteId);

    TopologyGraphDTO saveDraft(Long siteId, TopologyGraphSaveReqDTO request);

    TopologyGraphDTO publish(Long siteId);

    TopologyValidateRespDTO validate(Long siteId, TopologyGraphSaveReqDTO request);

    TopologyGraphDTO importLegacy(Long siteId);
}
