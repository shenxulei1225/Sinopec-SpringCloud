package cn.cheers.x.module.platform.policy.service;

import cn.cheers.x.module.platform.policy.api.dto.InstantiatePolicySetReqDTO;
import cn.cheers.x.module.platform.policy.api.dto.PolicySetRespDTO;
import cn.cheers.x.module.platform.policy.dal.dataobject.PolicySetDO;

public interface PolicySetService {

    PolicySetRespDTO instantiateFromTemplate(InstantiatePolicySetReqDTO request);

    PolicySetRespDTO publish(String policySetId);

    PolicySetRespDTO get(String policySetId);

    PolicySetDO requirePublishedDo(String policySetId);
}
