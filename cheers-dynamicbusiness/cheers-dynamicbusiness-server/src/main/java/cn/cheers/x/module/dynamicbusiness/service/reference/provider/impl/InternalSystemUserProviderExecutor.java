package cn.cheers.x.module.dynamicbusiness.service.reference.provider.impl;

import cn.cheers.x.module.dynamicbusiness.controller.admin.reference.vo.ReferenceCandidateRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.reference.ReferenceProviderDO;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceBatchGetReq;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceProviderExecutor;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceValidateReq;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceValidationResult;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.UserAccountDTO;
import cn.cheers.x.module.dynamicbusiness.service.directory.UserAccountDirectoryQueryService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component("internalSystemUserProviderExecutorImpl")
@RequiredArgsConstructor
public class InternalSystemUserProviderExecutor implements ReferenceProviderExecutor {

    private final UserAccountDirectoryQueryService userAccountDirectoryQueryService;

    @Override
    public boolean supports(ReferenceProviderDO provider) {
        return provider != null && "INTERNAL".equalsIgnoreCase(provider.getProviderType())
                && "SYSTEM_USER".equalsIgnoreCase(provider.getProviderCode());
    }

    @Override
    public List<ReferenceCandidateRespVO> queryCandidates(ReferenceProviderDO provider, String keyword, Integer pageNo, Integer pageSize) {
        List<UserAccountDTO> users = userAccountDirectoryQueryService.listUserAccountsByNickname(keyword);
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        int safePageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize == null || pageSize < 1 ? 20 : pageSize;
        int from = (safePageNo - 1) * safePageSize;
        if (from >= users.size()) {
            return Collections.emptyList();
        }
        int to = Math.min(from + safePageSize, users.size());
        return users.subList(from, to).stream()
                .map(this::toCandidate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReferenceCandidateRespVO> batchGet(ReferenceProviderDO provider, ReferenceBatchGetReq req) {
        if (req == null || req.getIds() == null || req.getIds().isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> ids = req.getIds().stream()
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return userAccountDirectoryQueryService.listUserAccountsByIds(ids).stream()
                .filter(Objects::nonNull)
                .map(this::toCandidate)
                .collect(Collectors.toList());
    }

    @Override
    public ReferenceValidationResult validate(ReferenceProviderDO provider, ReferenceValidateReq req) {
        if (req == null || StringUtils.isBlank(req.getId())) {
            return ReferenceValidationResult.builder().valid(false).reason("id 不能为空").build();
        }
        try {
            userAccountDirectoryQueryService.getUserAccount(Long.valueOf(req.getId()));
        } catch (ServiceException ex) {
            return ReferenceValidationResult.builder().valid(false).reason("引用目标不存在").build();
        }
        return ReferenceValidationResult.builder().valid(true).build();
    }

    private ReferenceCandidateRespVO toCandidate(UserAccountDTO user) {
        return ReferenceCandidateRespVO.builder()
                .id(String.valueOf(user.getId()))
                .label(StringUtils.defaultIfBlank(user.getNickname(), user.getAccount()))
                .extra(String.format("{\"username\":\"%s\",\"mobile\":\"%s\"}",
                        StringUtils.defaultString(user.getAccount()),
                        StringUtils.defaultString(user.getMobile())))
                .build();
    }
}
