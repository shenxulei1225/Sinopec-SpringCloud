package cn.cheers.x.module.dynamicbusiness.service.reference.provider.internal;

import cn.cheers.x.module.dynamicbusiness.controller.admin.reference.vo.ReferenceCandidateRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.reference.ReferenceProviderDO;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceBatchGetReq;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceProviderExecutor;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceValidateReq;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceValidationResult;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.system.api.user.AdminUserApi;
import cn.cheers.x.system.api.user.dto.AdminUserRespDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 内置 SYSTEM_USER Provider 执行器。
 */
@Component
@RequiredArgsConstructor
public class InternalSystemUserProviderExecutor implements ReferenceProviderExecutor {

    private final AdminUserApi adminUserApi;

    @Override
    public boolean supports(ReferenceProviderDO provider) {
        return provider != null
                && "INTERNAL".equalsIgnoreCase(provider.getProviderType())
                && "SYSTEM_USER".equalsIgnoreCase(provider.getProviderCode());
    }

    @Override
    public List<ReferenceCandidateRespVO> queryCandidates(ReferenceProviderDO provider, String keyword, Integer pageNo, Integer pageSize) {
        CommonResult<List<AdminUserRespDTO>> result = adminUserApi.getUserListByNickname(StringUtils.defaultString(keyword));
        List<AdminUserRespDTO> users = result == null || result.getData() == null ? List.of() : result.getData();

        List<ReferenceCandidateRespVO> candidates = users.stream()
                .filter(Objects::nonNull)
                .map(user -> {
                    Map<String, Object> meta = new HashMap<>();
                    meta.put("username", user.getUsername());
                    meta.put("mobile", user.getMobile());
                    return ReferenceCandidateRespVO.builder()
                            .id(String.valueOf(user.getId()))
                            .label(StringUtils.isNotBlank(user.getNickname()) ? user.getNickname() : user.getUsername())
                            .extra(meta.toString())
                            .build();
                })
                .collect(Collectors.toList());

        if (pageNo != null && pageSize != null && pageNo > 0 && pageSize > 0) {
            int fromIndex = Math.min((pageNo - 1) * pageSize, candidates.size());
            int toIndex = Math.min(fromIndex + pageSize, candidates.size());
            return new ArrayList<>(candidates.subList(fromIndex, toIndex));
        }
        return candidates;
    }

    @Override
    public List<ReferenceCandidateRespVO> batchGet(ReferenceProviderDO provider, ReferenceBatchGetReq req) {
        if (req == null || req.getIds() == null || req.getIds().isEmpty()) {
            return List.of();
        }
        List<Long> ids = req.getIds().stream()
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        if (ids.isEmpty()) {
            return List.of();
        }
        CommonResult<List<AdminUserRespDTO>> result = adminUserApi.getUserList(ids);
        List<AdminUserRespDTO> users = result == null ? null : result.getData();
        if (users == null || users.isEmpty()) {
            return List.of();
        }
        return users.stream().filter(Objects::nonNull).map(this::toCandidate).collect(Collectors.toList());
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
        Map<String, Object> meta = new HashMap<>();
        meta.put("username", user.getUsername());
        meta.put("mobile", user.getMobile());
        meta.put("email", user.getEmail());
        return ReferenceCandidateRespVO.builder()
                .id(String.valueOf(user.getId()))
                .label(StringUtils.isNotBlank(user.getNickname()) ? user.getNickname() : user.getUsername())
                .extra(meta.toString())
                .build();
    }
}
