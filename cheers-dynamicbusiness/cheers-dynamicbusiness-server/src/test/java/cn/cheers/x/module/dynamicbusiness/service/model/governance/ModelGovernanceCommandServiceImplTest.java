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
    void deleteCompanyWithoutNetworkCapabilityFails() {
        ModelDO company = model(1L, "COMPANY", null, 100L);
        company.setEntityTypeCode("equipment");
        when(modelCoreService.get(1L)).thenReturn(company);
        when(capabilityChecker.canManageNetworkModelData()).thenReturn(false);

        assertThrows(ServiceException.class, () -> service.deleteModel(1L, null, 100L));

        verify(modelCoreService, never()).delete(1L);
    }

    @Test
    void deleteUnoccupiedCompanySucceeds() {
        ModelDO company = model(10L, "COMPANY", null, 100L);
        company.setEntityTypeCode("equipment");
        when(modelCoreService.get(10L)).thenReturn(company);
        when(capabilityChecker.canManageNetworkModelData()).thenReturn(true);
        when(entityTypeScopeResolver.resolveStorageEntityTypeCode("equipment")).thenReturn("equipment");
        when(entityRepository.existsByModelId(10L, "equipment")).thenReturn(false);

        service.deleteModel(10L, null, 100L);

        verify(modelFieldAssignmentMapper).deleteByModelId(10L);
        verify(modelCategoryRelationService).deleteAllByModelId(10L);
        verify(modelCoreService).delete(10L);
    }

    @Test
    void deleteOccupiedCompanyFails() {
        ModelDO company = model(11L, "COMPANY", null, 100L);
        company.setEntityTypeCode("equipment");
        when(modelCoreService.get(11L)).thenReturn(company);
        when(capabilityChecker.canManageNetworkModelData()).thenReturn(true);
        when(entityTypeScopeResolver.resolveStorageEntityTypeCode("equipment")).thenReturn("equipment");
        when(entityRepository.existsByModelId(11L, "equipment")).thenReturn(true);

        ServiceException error = assertThrows(ServiceException.class,
                () -> service.deleteModel(11L, null, 100L));

        assertEquals("仍有实体使用该型号，禁止删除", error.getMessage());
        verify(modelCoreService, never()).delete(11L);
    }

    @Test
    void deleteAnotherUsersLocalFails() {
        ModelDO local = model(2L, "LOCAL", 10L, 200L);
        when(modelCoreService.get(2L)).thenReturn(local);

        assertThrows(ServiceException.class, () -> service.deleteModel(2L, 10L, 100L));

        verify(modelCoreService, never()).delete(2L);
    }

    @Test
    void deleteOwnLocalAtAnotherFacilityFails() {
        ModelDO local = model(3L, "LOCAL", 20L, 100L);
        when(modelCoreService.get(3L)).thenReturn(local);

        assertThrows(ServiceException.class, () -> service.deleteModel(3L, 10L, 100L));

        verify(modelCoreService, never()).delete(3L);
    }

    @Test
    void deleteOccupiedOwnLocalFails() {
        ModelDO local = model(4L, "LOCAL", 10L, 100L);
        local.setEntityTypeCode("equipment");
        when(modelCoreService.get(4L)).thenReturn(local);
        when(entityTypeScopeResolver.resolveStorageEntityTypeCode("equipment")).thenReturn("equipment");
        when(entityRepository.existsByModelId(4L, "equipment")).thenReturn(true);

        assertThrows(ServiceException.class, () -> service.deleteModel(4L, 10L, 100L));

        verify(modelCoreService, never()).delete(4L);
    }

    @Test
    void deleteOwnUnoccupiedLocalSucceeds() {
        ModelDO local = model(5L, "LOCAL", 10L, 100L);
        local.setEntityTypeCode("equipment");
        when(modelCoreService.get(5L)).thenReturn(local);
        when(entityTypeScopeResolver.resolveStorageEntityTypeCode("equipment")).thenReturn("equipment");
        when(entityRepository.existsByModelId(5L, "equipment")).thenReturn(false);

        service.deleteModel(5L, 10L, 100L);

        verify(modelFieldAssignmentMapper).deleteByModelId(5L);
        verify(modelCategoryRelationService).deleteAllByModelId(5L);
        verify(modelCoreService).delete(5L);
    }

    @Test
    void deactivateCompanyIsRejectedAsDeprecated() {
        ServiceException error = assertThrows(ServiceException.class, () -> service.deactivateCompany(6L));
        assertEquals(400, error.getCode());
        verify(modelCoreService, never()).update(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void updateCannotFlipGovernanceStatus() {
        ModelDO company = model(8L, "COMPANY", null, 100L);

        assertThrows(ServiceException.class,
                () -> service.validateRegularUpdate(company, "LOCAL", 1));
    }

    @Test
    void updateCannotChangeCompanyStatusOutsideGovernanceCommand() {
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
