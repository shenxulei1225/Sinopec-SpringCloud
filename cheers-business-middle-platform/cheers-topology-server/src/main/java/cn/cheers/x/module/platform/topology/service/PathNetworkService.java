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

    TopologyValidateRespDTO validate(PathNetworkDTO request);

    List<PathNetworkSummaryDTO> listDrafts(Long facilityId);

    /** 与路网管理同一份列表。排期仍走这个方法名，不再按发布状态另筛。 */
    List<PathNetworkSummaryDTO> listPublished(Long facilityId);

    PathNetworkDTO createDraft(PathNetworkCreateReqVO request);

    PathNetworkDTO updateMeta(String networkRef, PathNetworkMetaUpdateReqVO request);

    void deleteDraft(String networkRef);

    List<PortalDTO> listPortals(Long facilityId);

    List<PortalDTO> savePortals(Long facilityId, List<PortalDTO> portals);
}
