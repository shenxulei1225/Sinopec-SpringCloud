package cn.cheers.x.module.dynamicbusiness.service.reference;

import cn.cheers.x.module.dynamicbusiness.controller.admin.reference.vo.ReferenceCandidateRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.reference.vo.ReferenceProviderCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.reference.vo.ReferenceProviderRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.reference.vo.ReferenceProviderUpdateReqVO;

import java.util.List;

public interface ReferenceProviderService {

    Long create(ReferenceProviderCreateReqVO reqVO);

    void update(ReferenceProviderUpdateReqVO reqVO);

    void delete(Long id);

    ReferenceProviderRespVO get(Long id);

    List<ReferenceProviderRespVO> listEnabled(String semanticType);

    List<ReferenceCandidateRespVO> queryCandidates(String providerCode, String keyword, Integer pageNo, Integer pageSize);

    List<ReferenceCandidateRespVO> queryCandidatesByFieldCode(String fieldCode, String keyword, Integer pageNo, Integer pageSize);

    List<ReferenceCandidateRespVO> batchGetCandidates(String providerCode, List<String> ids);

    boolean validateCandidate(String providerCode, String id);
}
