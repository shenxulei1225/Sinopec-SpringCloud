package cn.cheers.x.module.platform.policy.service;

import cn.cheers.x.module.platform.policy.api.dto.InstantiatePolicySetReqDTO;
import cn.cheers.x.module.platform.policy.api.dto.PolicySetRespDTO;
import cn.cheers.x.module.platform.policy.dal.dataobject.PolicySetDO;
import cn.cheers.x.module.platform.policy.dal.dataobject.PolicyTemplateDO;
import cn.cheers.x.module.platform.policy.dal.mysql.PolicySetMapper;
import cn.cheers.x.module.platform.policy.dal.mysql.PolicyTemplateMapper;
import cn.cheers.x.module.platform.policy.enums.PolicySetStatus;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static cn.cheers.x.module.platform.policy.enums.ErrorCodeConstants.POLICY_SET_NOT_FOUND;
import static cn.cheers.x.module.platform.policy.enums.ErrorCodeConstants.POLICY_SET_NOT_PUBLISHED;
import static cn.cheers.x.module.platform.policy.enums.ErrorCodeConstants.POLICY_TEMPLATE_NOT_FOUND;
import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
public class PolicySetServiceImpl implements PolicySetService {

    @Resource
    private PolicyTemplateMapper policyTemplateMapper;
    @Resource
    private PolicySetMapper policySetMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PolicySetRespDTO instantiateFromTemplate(InstantiatePolicySetReqDTO request) {
        PolicyTemplateDO template = policyTemplateMapper.selectById(request.getTemplateId());
        if (template == null) {
            throw exception(POLICY_TEMPLATE_NOT_FOUND);
        }
        String mergedSpec = PolicySpecHelper.mergeTemplateWithParams(template.getDefaultSpec(), request.getParams());
        PolicySetDO policySet = PolicySetDO.builder()
                .id(UUID.randomUUID().toString())
                .entityTypeCode(request.getEntityTypeCode())
                .templateId(request.getTemplateId())
                .status(PolicySetStatus.DRAFT.name())
                .version(0)
                .specParams(mergedSpec)
                .build();
        policySetMapper.insert(policySet);
        return toResp(policySet);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PolicySetRespDTO publish(String policySetId) {
        PolicySetDO policySet = requireExisting(policySetId);
        policySet.setStatus(PolicySetStatus.PUBLISHED.name());
        policySet.setVersion(policySet.getVersion() + 1);
        policySetMapper.updateById(policySet);
        return toResp(policySet);
    }

    @Override
    public PolicySetRespDTO get(String policySetId) {
        return toResp(requireExisting(policySetId));
    }

    @Override
    public PolicySetDO requirePublishedDo(String policySetId) {
        PolicySetDO policySet = requireExisting(policySetId);
        if (!PolicySetStatus.PUBLISHED.name().equals(policySet.getStatus())) {
            throw exception(POLICY_SET_NOT_PUBLISHED);
        }
        return policySet;
    }

    private PolicySetDO requireExisting(String policySetId) {
        PolicySetDO policySet = policySetMapper.selectById(policySetId);
        if (policySet == null) {
            throw exception(POLICY_SET_NOT_FOUND);
        }
        return policySet;
    }

    private static PolicySetRespDTO toResp(PolicySetDO policySet) {
        return PolicySetRespDTO.builder()
                .policySetId(policySet.getId())
                .entityTypeCode(policySet.getEntityTypeCode())
                .templateId(policySet.getTemplateId())
                .status(policySet.getStatus())
                .version(policySet.getVersion())
                .build();
    }
}
