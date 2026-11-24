package cn.cheers.x.module.dynamicbusiness.service.model.governance;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.ManagementUnitTypeMapper;
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
 * <p>删除权威：软删型号本体，并同一次撤掉它的管理单元类型登记、字段分配、分类关联。有实体占用则拒绝。
 * 登记留着已删除的型号时，组织树整页报错，部门管理和职务管理都打不开。
 * 禁止把删除落成 {@code status=0} 停用。启用和停用走型号状态。
 * 超级管理员可以改公司规格和场站本地型号的状态，也可以删除这两种型号。
 * 普通用户仍按原规则：公司规格不能在普通保存里改状态，场站本地型号只能由创建人在发起站场删除。</p>
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
    private final ManagementUnitTypeMapper managementUnitTypeMapper;

    @Override
    public void prepareForCreate(ModelDO model, String requestedGovernanceStatus,
                                 Long effectiveFacilityId, Long currentUserId) {
        if (model == null) {
            throw new ServiceException(400, "待创建型号不能为空");
        }
        if (currentUserId == null) {
            throw new ServiceException(401, "未获取到当前登录用户");
        }

        // 建公司级还是本站由登录人定：管公司的人建公司级，只管站的人建右上角选中站的本地型号。
        boolean companyCreator = capabilityChecker.canCreateCompanyStandard();
        boolean requestsCompany = GOVERNANCE_COMPANY.equals(normalizeGovernanceStatus(requestedGovernanceStatus));
        if (requestsCompany && !companyCreator) {
            throw new ServiceException(403, "无权创建公司规格");
        }
        model.setCreatorUserId(currentUserId);
        if (companyCreator) {
            model.setGovernanceStatus(GOVERNANCE_COMPANY);
            model.setOriginFacilityId(null);
            return;
        }
        if (effectiveFacilityId == null) {
            throw new ServiceException(400, "请先在右上角选择场站后再创建型号");
        }
        model.setGovernanceStatus(GOVERNANCE_LOCAL);
        model.setOriginFacilityId(effectiveFacilityId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteModel(Long modelId, Long effectiveFacilityId, Long currentUserId) {
        ModelDO model = requireModel(modelId);
        // 超级管理员：公司规格和场站本地型号都能删。下面的占用检查仍然生效。
        if (!SecurityFrameworkUtils.isSuperAdmin()) {
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
            } else if (!capabilityChecker.canManageNetworkModelData()) {
                // 公司规格或无发起站场的种子：须全网型号治理能力；删除仍是软删，不是停用
                throw new ServiceException(403, "无权删除公司规格");
            }
        }

        assertNotOccupied(model);
        // 这个型号若还登记为管理单元类型，同一次撤掉。不在打开组织树时再猜。
        managementUnitTypeMapper.deleteByModelId(modelId);
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
        // 启用、停用就是改型号状态。超级管理员对公司规格和场站本地型号都可以改。
        if (!SecurityFrameworkUtils.isSuperAdmin()
                && GOVERNANCE_COMPANY.equals(existingModel.getGovernanceStatus())
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
