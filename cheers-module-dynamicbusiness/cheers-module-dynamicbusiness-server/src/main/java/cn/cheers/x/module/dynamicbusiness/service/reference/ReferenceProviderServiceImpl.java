package cn.cheers.x.module.dynamicbusiness.service.reference;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.module.dynamicbusiness.controller.admin.reference.vo.ReferenceCandidateRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.reference.vo.ReferenceProviderCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.reference.vo.ReferenceProviderRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.reference.vo.ReferenceProviderUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.reference.ReferenceProviderDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.reference.ReferenceProviderMapper;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceBatchGetReq;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceProviderExecutor;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceProviderExecutorRegistry;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceValidateReq;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;

import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.*;

@Service
@Validated
@RequiredArgsConstructor
public class ReferenceProviderServiceImpl implements ReferenceProviderService {

    private final ReferenceProviderMapper referenceProviderMapper;
    private final FieldMapper fieldMapper;
    private final ReferenceProviderExecutorRegistry executorRegistry;

    @Override
    public Long create(ReferenceProviderCreateReqVO reqVO) {
        if (referenceProviderMapper.selectByProviderCode(reqVO.getProviderCode()) != null) {
            throw ServiceExceptionUtil.exception(REF_PROVIDER_CODE_EXISTS, reqVO.getProviderCode());
        }
        ReferenceProviderDO providerDO = BeanUtils.toBean(reqVO, ReferenceProviderDO.class);
        if (providerDO.getStatus() == null) {
            providerDO.setStatus(1);
        }
        referenceProviderMapper.insert(providerDO);
        return providerDO.getId();
    }

    @Override
    public void update(ReferenceProviderUpdateReqVO reqVO) {
        ReferenceProviderDO provider = referenceProviderMapper.selectById(reqVO.getId());
        if (provider == null) {
            throw ServiceExceptionUtil.exception(REF_PROVIDER_NOT_EXISTS);
        }
        ReferenceProviderDO update = BeanUtils.toBean(reqVO, ReferenceProviderDO.class);
        referenceProviderMapper.updateById(update);
    }

    @Override
    public void delete(Long id) {
        ReferenceProviderDO provider = referenceProviderMapper.selectById(id);
        if (provider == null) {
            throw ServiceExceptionUtil.exception(REF_PROVIDER_NOT_EXISTS);
        }
        referenceProviderMapper.deleteById(id);
    }

    @Override
    public ReferenceProviderRespVO get(Long id) {
        ReferenceProviderDO provider = referenceProviderMapper.selectById(id);
        if (provider == null) {
            throw ServiceExceptionUtil.exception(REF_PROVIDER_NOT_EXISTS);
        }
        return BeanUtils.toBean(provider, ReferenceProviderRespVO.class);
    }

    @Override
    public List<ReferenceProviderRespVO> listEnabled(String semanticType) {
        Long tenantId = SecurityFrameworkUtils.getLoginUser() == null ? null : SecurityFrameworkUtils.getLoginUser().getVisitTenantId();
        List<ReferenceProviderDO> list = referenceProviderMapper.selectBySemanticTypeAndTenantScope(semanticType, tenantId);
        return BeanUtils.toBean(list, ReferenceProviderRespVO.class);
    }

    @Override
    public List<ReferenceCandidateRespVO> queryCandidates(String providerCode, String keyword, Integer pageNo, Integer pageSize) {
        ReferenceProviderDO provider = referenceProviderMapper.selectByProviderCode(providerCode);
        if (provider == null || provider.getStatus() == null || provider.getStatus() == 0) {
            throw ServiceExceptionUtil.exception(REF_PROVIDER_NOT_EXISTS);
        }
        ReferenceProviderExecutor executor = executorRegistry.getRequiredExecutor(provider);
        return executor.queryCandidates(provider, keyword, pageNo, pageSize);
    }

    @Override
    public List<ReferenceCandidateRespVO> queryCandidatesByFieldCode(String fieldCode, String keyword, Integer pageNo, Integer pageSize) {
        if (StringUtils.isBlank(fieldCode)) {
            throw ServiceExceptionUtil.exception(REF_PROVIDER_FIELD_CODE_REQUIRED);
        }
        cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO field = fieldMapper.selectByCode(fieldCode);
        if (field == null) {
            throw ServiceExceptionUtil.exception(REF_PROVIDER_FIELD_NOT_EXISTS, fieldCode);
        }
        if (StringUtils.isBlank(field.getProviderCode())) {
            throw ServiceExceptionUtil.exception(REF_PROVIDER_FIELD_MISSING_PROVIDER, fieldCode);
        }
        return queryCandidates(field.getProviderCode(), keyword, pageNo, pageSize);
    }

    @Override
    public List<ReferenceCandidateRespVO> batchGetCandidates(String providerCode, List<String> ids) {
        ReferenceProviderDO provider = referenceProviderMapper.selectByProviderCode(providerCode);
        if (provider == null || provider.getStatus() == null || provider.getStatus() == 0) {
            throw ServiceExceptionUtil.exception(REF_PROVIDER_NOT_EXISTS);
        }
        ReferenceProviderExecutor executor = executorRegistry.getRequiredExecutor(provider);
        return executor.batchGet(provider, ReferenceBatchGetReq.builder().ids(ids).build());
    }

    @Override
    public boolean validateCandidate(String providerCode, String id) {
        ReferenceProviderDO provider = referenceProviderMapper.selectByProviderCode(providerCode);
        if (provider == null || provider.getStatus() == null || provider.getStatus() == 0) {
            throw ServiceExceptionUtil.exception(REF_PROVIDER_NOT_EXISTS);
        }
        ReferenceProviderExecutor executor = executorRegistry.getRequiredExecutor(provider);
        return Objects.requireNonNullElse(
                executor.validate(provider, ReferenceValidateReq.builder().id(id).build()).isValid(),
                false
        );
    }
}
