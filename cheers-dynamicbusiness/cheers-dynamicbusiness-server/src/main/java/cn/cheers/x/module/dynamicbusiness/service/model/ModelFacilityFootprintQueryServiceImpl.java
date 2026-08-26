package cn.cheers.x.module.dynamicbusiness.service.model;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFacilityFootprintRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeResolver;
import cn.cheers.x.module.dynamicbusiness.service.model.core.ModelCoreService;
import cn.cheers.x.module.dynamicbusiness.service.model.governance.MasterDataCapabilityChecker;
import cn.cheers.x.module.dynamicbusiness.service.model.governance.ModelGovernanceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 型号设施覆盖范围查询实现。
 *
 * <p>型号表仅提供实体存储类型；覆盖范围必须由实体表 {@code facility_id} 聚合得出，
 * 禁止使用 {@code origin_facility_id} 补齐或推断。</p>
 */
@Service
@RequiredArgsConstructor
public class ModelFacilityFootprintQueryServiceImpl implements ModelFacilityFootprintQueryService {

    private final ModelCoreService modelCoreService;
    private final ModelGovernanceQueryService modelGovernanceQueryService;
    private final MasterDataCapabilityChecker masterDataCapabilityChecker;
    private final EntityTypeScopeResolver entityTypeScopeResolver;
    private final EntityRepository entityRepository;

    @Override
    public ModelFacilityFootprintRespVO getFacilityFootprint(Long modelId, Long effectiveFacilityId) {
        if (modelId == null) {
            throw new ServiceException(400, "modelId 不能为空");
        }
        ModelDO model = modelCoreService.get(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }
        modelGovernanceQueryService.assertVisible(
                model, effectiveFacilityId, masterDataCapabilityChecker.canManageNetworkModelData());
        String storageEntityTypeCode =
                entityTypeScopeResolver.resolveStorageEntityTypeCode(model.getEntityTypeCode());
        if (!entityRepository.hasFacilityIdColumn(storageEntityTypeCode)) {
            throw new ServiceException(400, "该实体类型没有所属场站列，不能统计型号落站范围");
        }
        long facilityCount =
                entityRepository.countDistinctFacilityIdsByModelId(modelId, storageEntityTypeCode);
        return new ModelFacilityFootprintRespVO(
                modelId, facilityCount, classify(facilityCount));
    }

    private static String classify(long facilityCount) {
        if (facilityCount == 0) {
            return "NONE";
        }
        if (facilityCount == 1) {
            return "SINGLE_FACILITY";
        }
        return "MULTI_FACILITY";
    }
}
