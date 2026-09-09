package cn.cheers.x.module.dynamicbusiness.service.model.governance;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeResolver;
import cn.cheers.x.module.dynamicbusiness.service.model.core.ModelCoreService;
import cn.cheers.x.module.dynamicbusiness.service.model.relation.ModelCategoryRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Objects;

/**
 * 型号治理命令实现。
 *
 * <p>治理身份只在创建和后续专用治理命令中写入。本实现不从角色码、默认站场或其它字段
 * 猜测身份；缺少当前用户或有效站场时直接拒绝本地型号写入。</p>
 *
 * <p>删除权威：软删型号本体并清字段分配/分类关联；有实体占用则拒绝。
 * 禁止把删除落成 {@code status=0} 停用。</p>
 */
@Service
@RequiredArgsConstructor
public class ModelGovernanceCommandServiceImpl implements ModelGovernanceCommandService {

    static final String GOVERNANCE_LOCAL = "LOCAL";
    static final String GOVERNANCE_COMPANY = "COMPANY";

    private final ModelCoreService modelCoreService;
    private final EntityTypeScopeResolver entityTypeScopeResolver;
    private final EntityRepository entityRepository;
    private final ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    private final ModelCategoryRelationService modelCategoryRelationService;
    private final MasterDataCapabilityChecker capabilityChecker;

    @Override
    public void prepareForCreate(ModelDO model, String requestedGovernanceStatus,
                                 Long effectiveFacilityId, Long currentUserId) {
        if (model == null) {
            throw new ServiceException(400, "待创建型号不能为空");
        }
        if (currentUserId == null) {
            throw new ServiceException(401, "未获取到当前登录用户");
        }

        boolean requestsCompany = GOVERNANCE_COMPANY.equals(normalizeGovernanceStatus(requestedGovernanceStatus));
        if (requestsCompany && !capabilityChecker.canCreateCompanyStandard()) {
            throw new ServiceException(403, "无权创建公司规格");
        }
        model.setCreatorUserId(currentUserId);
        if (requestsCompany) {
            model.setGovernanceStatus(GOVERNANCE_COMPANY);
            model.setOriginFacilityId(null);
            return;
        }
        if (effectiveFacilityId == null) {
            throw new ServiceException(400, "创建本地型号必须指定当前有效站场");
        }
        model.setGovernanceStatus(GOVERNANCE_LOCAL);
        model.setOriginFacilityId(effectiveFacilityId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteModel(Long modelId, Long effectiveFacilityId, Long currentUserId) {
        ModelDO model = requireModel(modelId);
        boolean local = GOVERNANCE_LOCAL.equals(normalizeGovernanceStatus(model.getGovernanceStatus()));
        if (local) {
            if (currentUserId == null || !Objects.equals(currentUserId, model.getCreatorUserId())) {
                throw new ServiceException(403, "只能删除自己创建的本地型号");
            }
            if (effectiveFacilityId == null) {
                throw new ServiceException(400, "删除本地型号必须指定当前有效站场");
            }
            if (!Objects.equals(effectiveFacilityId, model.getOriginFacilityId())) {
                throw new ServiceException(403, "只能在本地型号的发起站场删除");
            }
        } else {
            // 公司规格或无发起站场的种子：须全网型号治理能力；删除仍是软删，不是停用
            if (!capabilityChecker.canManageNetworkModelData()) {
                throw new ServiceException(403, "无权删除公司规格");
            }
        }

        assertNotOccupied(model);
        modelFieldAssignmentMapper.deleteByModelId(modelId);
        modelCategoryRelationService.deleteAllByModelId(modelId);
        modelCoreService.delete(modelId);
    }

    /**
     * @deprecated 停用不再作为删除替代；调用方应改走 {@link #deleteModel}。
     */
    @Override
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public void deactivateCompany(Long modelId) {
        throw new ServiceException(400, "型号停用已废弃，请使用删除；有实体占用时删除会被拒绝");
    }

    @Override
    public void validateRegularUpdate(ModelDO existingModel, String requestedGovernanceStatus,
                                      Integer requestedStatus) {
        if (existingModel == null) {
            throw new ServiceException(404, "模型不存在");
        }
        String requested = normalizeGovernanceStatus(requestedGovernanceStatus);
        if (requested != null && !Objects.equals(requested, existingModel.getGovernanceStatus())) {
            throw new ServiceException(400, "治理状态不能通过普通更新修改");
        }
        if (GOVERNANCE_COMPANY.equals(existingModel.getGovernanceStatus())
                && requestedStatus != null
                && !Objects.equals(requestedStatus, existingModel.getStatus())) {
            throw new ServiceException(400, "公司规格状态不能通过普通更新修改");
        }
    }

    private void assertNotOccupied(ModelDO model) {
        String storageEntityTypeCode =
                entityTypeScopeResolver.resolveStorageEntityTypeCode(model.getEntityTypeCode());
        if (!StringUtils.hasText(storageEntityTypeCode)) {
            throw new ServiceException(400, "型号缺少有效的存储类型编码");
        }
        if (entityRepository.existsByModelId(model.getId(), storageEntityTypeCode)) {
            throw new ServiceException(400, "仍有实体使用该型号，禁止删除");
        }
    }

    private ModelDO requireModel(Long modelId) {
        if (modelId == null) {
            throw new ServiceException(400, "型号 ID 不能为空");
        }
        ModelDO model = modelCoreService.get(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }
        return model;
    }

    private static String normalizeGovernanceStatus(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return raw.trim().toUpperCase(Locale.ROOT);
    }
}
