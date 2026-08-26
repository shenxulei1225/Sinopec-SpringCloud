package cn.cheers.x.module.dynamicbusiness.service.model.governance;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeResolver;
import cn.cheers.x.module.dynamicbusiness.service.model.core.ModelCoreService;
import cn.cheers.x.module.dynamicbusiness.service.model.relation.ModelCategoryRelationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ModelGovernanceCommandServiceImplTest {

    private final ModelCoreService modelCoreService = mock(ModelCoreService.class);
    private final EntityTypeScopeResolver entityTypeScopeResolver = mock(EntityTypeScopeResolver.class);
    private final EntityRepository entityRepository = mock(EntityRepository.class);
    private final ModelFieldAssignmentMapper modelFieldAssignmentMapper = mock(ModelFieldAssignmentMapper.class);
    private final ModelCategoryRelationService modelCategoryRelationService = mock(ModelCategoryRelationService.class);
    private final MasterDataCapabilityChecker capabilityChecker = mock(MasterDataCapabilityChecker.class);

    private ModelGovernanceCommandService service;

    @BeforeEach
    void setUp() {
        service = new ModelGovernanceCommandServiceImpl(
                modelCoreService,
                entityTypeScopeResolver,
                entityRepository,
                modelFieldAssignmentMapper,
                modelCategoryRelationService,
                capabilityChecker);
    }

    @Test
    void companyCreateWithoutCapabilityIsRejected() {
        ModelDO model = new ModelDO();
        when(capabilityChecker.canCreateCompanyStandard()).thenReturn(false);

        ServiceException error = assertThrows(ServiceException.class,
                () -> service.prepareForCreate(model, "COMPANY", 10L, 100L));

        assertEquals(403, error.getCode());
        assertEquals("无权创建公司规格", error.getMessage());
    }

    @Test
    void companyCreateRequiresCapabilityAndHasNoOriginFacility() {
        ModelDO model = new ModelDO();
        when(capabilityChecker.canCreateCompanyStandard()).thenReturn(true);

        service.prepareForCreate(model, "COMPANY", 10L, 100L);

        assertEquals("COMPANY", model.getGovernanceStatus());
        assertNull(model.getOriginFacilityId());
        assertEquals(100L, model.getCreatorUserId());
    }

    @Test
    void localCreateRequiresEffectiveFacility() {
        ModelDO model = new ModelDO();

        assertThrows(ServiceException.class,
                () -> service.prepareForCreate(model, null, null, 100L));
    }

    @Test
    void deleteCompanyAlwaysFails() {
        ModelDO company = model(1L, "COMPANY", null, 100L);
        when(modelCoreService.get(1L)).thenReturn(company);

        assertThrows(ServiceException.class, () -> service.deleteOwnLocal(1L, 10L, 100L));

        verify(modelCoreService, never()).delete(1L);
    }

    @Test
    void deleteAnotherUsersLocalFails() {
        ModelDO local = model(2L, "LOCAL", 10L, 200L);
        when(modelCoreService.get(2L)).thenReturn(local);

        assertThrows(ServiceException.class, () -> service.deleteOwnLocal(2L, 10L, 100L));

        verify(modelCoreService, never()).delete(2L);
    }

    @Test
    void deleteOwnLocalAtAnotherFacilityFails() {
        ModelDO local = model(3L, "LOCAL", 20L, 100L);
        when(modelCoreService.get(3L)).thenReturn(local);

        assertThrows(ServiceException.class, () -> service.deleteOwnLocal(3L, 10L, 100L));

        verify(modelCoreService, never()).delete(3L);
    }

    @Test
    void deleteOccupiedOwnLocalFails() {
        ModelDO local = model(4L, "LOCAL", 10L, 100L);
        local.setEntityTypeCode("equipment");
        when(modelCoreService.get(4L)).thenReturn(local);
        when(entityTypeScopeResolver.resolveStorageEntityTypeCode("equipment")).thenReturn("equipment");
        when(entityRepository.existsByModelId(4L, "equipment")).thenReturn(true);

        assertThrows(ServiceException.class, () -> service.deleteOwnLocal(4L, 10L, 100L));

        verify(modelCoreService, never()).delete(4L);
    }

    @Test
    void deleteOwnUnoccupiedLocalSucceeds() {
        ModelDO local = model(5L, "LOCAL", 10L, 100L);
        local.setEntityTypeCode("equipment");
        when(modelCoreService.get(5L)).thenReturn(local);
        when(entityTypeScopeResolver.resolveStorageEntityTypeCode("equipment")).thenReturn("equipment");
        when(entityRepository.existsByModelId(5L, "equipment")).thenReturn(false);

        service.deleteOwnLocal(5L, 10L, 100L);

        verify(modelFieldAssignmentMapper).deleteByModelId(5L);
        verify(modelCategoryRelationService).deleteAllByModelId(5L);
        verify(modelCoreService).delete(5L);
    }

    @Test
    void deactivateCompanyRequiresCapability() {
        ModelDO company = model(6L, "COMPANY", null, 100L);
        when(modelCoreService.get(6L)).thenReturn(company);
        when(capabilityChecker.canDeactivateCompanyStandard()).thenReturn(false);

        assertThrows(ServiceException.class, () -> service.deactivateCompany(6L));

        verify(modelCoreService, never()).update(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deactivateCompanySetsStatusToDisabled() {
        ModelDO company = model(7L, "COMPANY", null, 100L);
        company.setStatus(1);
        when(modelCoreService.get(7L)).thenReturn(company);
        when(capabilityChecker.canDeactivateCompanyStandard()).thenReturn(true);

        service.deactivateCompany(7L);

        verify(modelCoreService).update(org.mockito.ArgumentMatchers.argThat(
                update -> update.getId().equals(7L) && update.getStatus().equals(0)));
    }

    @Test
    void updateCannotFlipGovernanceStatus() {
        ModelDO company = model(8L, "COMPANY", null, 100L);

        assertThrows(ServiceException.class,
                () -> service.validateRegularUpdate(company, "LOCAL", 1));
    }

    @Test
    void updateCannotDeactivateCompanyOutsideGovernanceCommand() {
        ModelDO company = model(9L, "COMPANY", null, 100L);
        company.setStatus(1);

        assertThrows(ServiceException.class,
                () -> service.validateRegularUpdate(company, "COMPANY", 0));
    }

    private static ModelDO model(Long id, String governanceStatus, Long originFacilityId, Long creatorUserId) {
        ModelDO model = new ModelDO();
        model.setId(id);
        model.setGovernanceStatus(governanceStatus);
        model.setOriginFacilityId(originFacilityId);
        model.setCreatorUserId(creatorUserId);
        return model;
    }
}
