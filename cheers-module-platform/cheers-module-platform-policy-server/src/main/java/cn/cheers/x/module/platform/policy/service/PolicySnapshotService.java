package cn.cheers.x.module.platform.policy.service;

import cn.cheers.x.module.platform.policy.api.dto.PolicyResolveForRunReqDTO;
import cn.cheers.x.module.platform.policy.api.dto.PolicyRunContextDTO;
import cn.cheers.x.module.platform.policy.api.dto.PolicySnapshotRespDTO;
import cn.cheers.x.module.platform.policy.dal.dataobject.PolicySetDO;

public interface PolicySnapshotService {

    PolicyRunContextDTO resolveForRun(PolicyResolveForRunReqDTO request);

    PolicySnapshotRespDTO getSnapshot(String policySnapshotId);

    PolicySnapshotRespDTO createSnapshotForRun(PolicySetDO policySet);
}
