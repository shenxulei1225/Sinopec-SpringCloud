package cn.cheers.x.module.dynamicbusiness.service.reference.provider.impl;

import cn.cheers.x.module.dynamicbusiness.controller.admin.reference.vo.ReferenceCandidateRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.reference.ReferenceProviderDO;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceBatchGetReq;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceProviderExecutor;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceValidateReq;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceValidationResult;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.system.api.dept.DeptApi;
import cn.cheers.x.system.api.dept.dto.DeptRespDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InternalSystemDeptProviderExecutor implements ReferenceProviderExecutor {

    private final DeptApi deptApi;

    @Override
    public boolean supports(ReferenceProviderDO provider) {
        return provider != null && "INTERNAL".equalsIgnoreCase(provider.getProviderType())
                && "SYSTEM_DEPT".equalsIgnoreCase(provider.getProviderCode());
    }

    @Override
    public List<ReferenceCandidateRespVO> queryCandidates(ReferenceProviderDO provider, String keyword, Integer pageNo, Integer pageSize) {
        CommonResult<List<DeptRespDTO>> result = deptApi.getDeptList(Collections.emptyList());
        List<DeptRespDTO> rows = result == null ? null : result.getData();
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyList();
        }
        String kw = StringUtils.trimToEmpty(keyword).toLowerCase(Locale.ROOT);
        List<DeptRespDTO> filtered = rows.stream()
                .filter(Objects::nonNull)
                .filter(row -> StringUtils.isBlank(kw)
                        || StringUtils.containsIgnoreCase(row.getName(), kw)
                        || StringUtils.containsIgnoreCase(String.valueOf(row.getId()), kw))
                .collect(Collectors.toList());

        int safePageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize == null || pageSize < 1 ? 20 : pageSize;
        int from = (safePageNo - 1) * safePageSize;
        if (from >= filtered.size()) {
            return Collections.emptyList();
        }
        int to = Math.min(from + safePageSize, filtered.size());
        return filtered.subList(from, to).stream().map(this::toCandidate).collect(Collectors.toList());
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
        CommonResult<List<DeptRespDTO>> result = deptApi.getDeptList(ids);
        List<DeptRespDTO> rows = result == null ? null : result.getData();
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyList();
        }
        return rows.stream().filter(Objects::nonNull).map(this::toCandidate).collect(Collectors.toList());
    }

    @Override
    public ReferenceValidationResult validate(ReferenceProviderDO provider, ReferenceValidateReq req) {
        if (req == null || StringUtils.isBlank(req.getId())) {
            return ReferenceValidationResult.builder().valid(false).reason("id 不能为空").build();
        }
        CommonResult<DeptRespDTO> result = deptApi.getDept(Long.valueOf(req.getId()));
        if (result == null || result.getData() == null) {
            return ReferenceValidationResult.builder().valid(false).reason("引用目标不存在").build();
        }
        return ReferenceValidationResult.builder().valid(true).build();
    }

    private ReferenceCandidateRespVO toCandidate(DeptRespDTO row) {
        Map<String, Object> meta = new HashMap<>();
        meta.put("deptId", row.getId());
        meta.put("parentId", row.getParentId() == null ? 0L : row.getParentId());
        meta.put("status", row.getStatus());
        return ReferenceCandidateRespVO.builder()
                .id(String.valueOf(row.getId()))
                .label(StringUtils.defaultIfBlank(row.getName(), String.valueOf(row.getId())))
                .extra(meta.toString())
                .build();
    }
}
