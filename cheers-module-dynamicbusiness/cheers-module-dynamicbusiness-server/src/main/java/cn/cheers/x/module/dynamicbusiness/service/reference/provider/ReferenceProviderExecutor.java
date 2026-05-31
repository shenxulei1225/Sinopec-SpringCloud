package cn.cheers.x.module.dynamicbusiness.service.reference.provider;

import cn.cheers.x.module.dynamicbusiness.controller.admin.reference.vo.ReferenceCandidateRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.reference.ReferenceProviderDO;

import java.util.List;

public interface ReferenceProviderExecutor {

    boolean supports(ReferenceProviderDO provider);

    List<ReferenceCandidateRespVO> queryCandidates(ReferenceProviderDO provider, String keyword, Integer pageNo, Integer pageSize);

    List<ReferenceCandidateRespVO> batchGet(ReferenceProviderDO provider, ReferenceBatchGetReq req);

    ReferenceValidationResult validate(ReferenceProviderDO provider, ReferenceValidateReq req);
}
