package cn.cheers.x.module.dynamicbusiness.service.model;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFacilityFootprintRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeResolver;
import cn.cheers.x.module.dynamicbusiness.service.model.core.ModelCoreService;
import cn.cheers.x.module.dynamicbusiness.service.model.governance.MasterDataCapabilityChecker;
import cn.cheers.x.module.dynamicbusiness.service.model.governance.ModelGovernanceQueryService;
import cn.cheers.x.module.dynamicbusiness.service.model.governance.ModelGovernanceQueryServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ModelFacilityFootprintQueryServiceImplTest {

    private final ModelCoreService modelCoreService = mock(ModelCoreService.class);
    private final ModelGovernanceQueryService modelGovernanceQueryService =
            new ModelGovernanceQueryServiceImpl();
    private final MasterDataCapabilityChecker capabilityChecker =
            mock(MasterDataCapabilityChecker.class);
    private final EntityTypeScopeResolver entityTypeScopeResolver = mock(EntityTypeScopeResolver.class);
    private final EntityRepository entityRepository = mock(EntityRepository.class);
    private final ModelFacilityFootprintQueryService service =
            new ModelFacilityFootprintQueryServiceImpl(
                    modelCoreService, modelGovernanceQueryService, capabilityChecker,
                    entityTypeScopeResolver, entityRepository);

    @Test
    void classifiesNoOwningFacilityAsNone() {
        stubFacilityCount(100L, 0L);

        ModelFacilityFootprintRespVO result = service.getFacilityFootprint(100L, 10L);

        assertEquals(100L, result.getModelId());
        assertEquals(0L, result.getFacilityCount());
        assertEquals("NONE", result.getClassification());
    }

    @Test
    void classifiesOneDistinctOwningFacilityAsSingleFacility() {
        stubFacilityCount(101L, 1L);

        ModelFacilityFootprintRespVO result = service.getFacilityFootprint(101L, 10L);

        assertEquals(1L, result.getFacilityCount());
        assertEquals("SINGLE_FACILITY", result.getClassification());
    }

    @Test
    void classifiesTwoDistinctOwningFacilitiesAsMultiFacility() {
        stubFacilityCount(102L, 2L);

        ModelFacilityFootprintRespVO result = service.getFacilityFootprint(102L, 10L);

        assertEquals(2L, result.getFacilityCount());
        assertEquals("MULTI_FACILITY", result.getClassification());
    }

    @Test
    void rejectsEntityTypeWithoutFacilityColumnClearly() {
        ModelDO model = new ModelDO();
        model.setId(103L);
        model.setEntityTypeCode("inspection-item");
        model.setGovernanceStatus("COMPANY");
        model.setStatus(1);
        when(modelCoreService.get(103L)).thenReturn(model);
        when(entityTypeScopeResolver.resolveStorageEntityTypeCode("inspection-item"))
                .thenReturn("inspection-item");
        when(entityRepository.hasFacilityIdColumn("inspection-item")).thenReturn(false);

        ServiceException error = assertThrows(
                ServiceException.class, () -> service.getFacilityFootprint(103L, 10L));

        assertEquals(400, error.getCode());
    }

    private void stubFacilityCount(Long modelId, long facilityCount) {
        ModelDO model = new ModelDO();
        model.setId(modelId);
        model.setEntityTypeCode("equipment-entry");
        model.setGovernanceStatus("COMPANY");
        model.setStatus(1);
        when(modelCoreService.get(modelId)).thenReturn(model);
        when(entityTypeScopeResolver.resolveStorageEntityTypeCode("equipment-entry"))
                .thenReturn("equipment");
        when(entityRepository.hasFacilityIdColumn("equipment")).thenReturn(true);
        when(entityRepository.countDistinctFacilityIdsByModelId(modelId, "equipment"))
                .thenReturn(facilityCount);
    }
}
