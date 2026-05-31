package cn.cheers.x.module.dynamicbusiness.service.reference.provider.impl;

import cn.cheers.x.module.dynamicbusiness.controller.admin.reference.vo.ReferenceCandidateRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.reference.ReferenceProviderDO;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceBatchGetReq;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceProviderExecutor;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceValidateReq;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceValidationResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
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

    private final AdminUserApi adminUserApi;

    @Override
    public boolean supports(ReferenceProviderDO provider) {
        return provider != null && "INTERNAL".equalsIgnoreCase(provider.getProviderType())
                && "SYSTEM_USER".equalsIgnoreCase(provider.getProviderCode());
    }

    @Override
    public List<ReferenceCandidateRespVO> queryCandidates(ReferenceProviderDO provider, String keyword, Integer pageNo, Integer pageSize) {
        CommonResult<List<AdminUserRespDTO>> result = adminUserApi.getUserListByNickname(StringUtils.defaultString(keyword));
        if (result == null || result.getData() == null || result.getData().isEmpty()) {
            return Collections.emptyList();
        }
        List<AdminUserRespDTO> users = result.getData();
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
        return adminUserApi.getUserList(ids).getData().stream()
                .filter(Objects::nonNull)
                .map(this::toCandidate)
                .collect(Collectors.toList());
    }

    @Override
    public ReferenceValidationResult validate(ReferenceProviderDO provider, ReferenceValidateReq req) {
        if (req == null || StringUtils.isBlank(req.getId())) {
            return ReferenceValidationResult.builder().valid(false).reason("id 不能为空").build();
        }
        CommonResult<AdminUserRespDTO> result = adminUserApi.getUser(Long.valueOf(req.getId()));
        if (result == null || result.getData() == null) {
            return ReferenceValidationResult.builder().valid(false).reason("引用目标不存在").build();
        }
        return ReferenceValidationResult.builder().valid(true).build();
    }

    private ReferenceCandidateRespVO toCandidate(AdminUserRespDTO user) {
        return ReferenceCandidateRespVO.builder()
                .id(String.valueOf(user.getId()))
                .label(StringUtils.defaultIfBlank(user.getNickname(), user.getUsername()))
                .extra(String.format("{\"username\":\"%s\",\"mobile\":\"%s\"}",
                        StringUtils.defaultString(user.getUsername()),
                        StringUtils.defaultString(user.getMobile())))
                .build();
    }
}
