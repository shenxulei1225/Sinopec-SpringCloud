package cn.cheers.x.module.platform.topology.service;

import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PortalDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.topology.api.dto.PathNetworkSummaryDTO;
import cn.cheers.x.module.platform.topology.api.dto.TopologyValidateRespDTO;
import cn.cheers.x.module.platform.topology.controller.admin.vo.PathNetworkCreateReqVO;
import cn.cheers.x.module.platform.topology.controller.admin.vo.PathNetworkMetaUpdateReqVO;

import java.util.List;

public interface PathNetworkService {

    PathNetworkDTO getNetwork(String networkRef);

    /** @deprecated 按种类槽位读草稿；新 UI 走 list + getNetwork */
    PathNetworkDTO getDraft(Long facilityId, NetworkKind networkKind);

    PathNetworkDTO saveDraft(PathNetworkDTO request);

    /** 另存为草稿：新开一条 isDraft=true 的副本，可存多份 */
    PathNetworkDTO saveAsDraft(PathNetworkDTO request);

    TopologyValidateRespDTO validate(PathNetworkDTO request);

    /** @deprecated 按种类发布；新 UI 走 publishByRef */
    PathNetworkDTO publish(Long facilityId, NetworkKind networkKind);

    PathNetworkDTO publishByRef(String networkRef);

    List<PathNetworkSummaryDTO> listDrafts(Long facilityId);

    /** 设施下已发布路网摘要（供巡检选网等跨模块调用；不含草稿） */
    List<PathNetworkSummaryDTO> listPublished(Long facilityId);

    PathNetworkDTO createDraft(PathNetworkCreateReqVO request);

    PathNetworkDTO updateMeta(String networkRef, PathNetworkMetaUpdateReqVO request);

    void deleteDraft(String networkRef);

    List<PortalDTO> listPortals(Long facilityId);

    List<PortalDTO> savePortals(Long facilityId, List<PortalDTO> portals);
}
