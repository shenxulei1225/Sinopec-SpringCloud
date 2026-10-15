package cn.cheers.x.module.dynamicbusiness.service.model.relation;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelModelRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelModelRelationMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeResolver;
import cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantAssociationTableService;
import cn.cheers.x.module.dynamicbusiness.service.model.core.ModelCoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModelModelRelationServiceImplTest {

    @Mock
    private ModelModelRelationMapper relationMapper;
    @Mock
    private ModelCoreService modelCoreService;
    @Mock
    private EntityTypeScopeResolver entityTypeScopeResolver;
    @Mock
    private TenantAssociationTableService tenantAssociationTableService;

    @InjectMocks
    private ModelModelRelationServiceImpl service;

    @BeforeEach
    void storageCodeIsItself() {
        when(entityTypeScopeResolver.resolveStorageEntityTypeCode(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void sameTypeAndCrossTypeShareOneTable() {
        when(modelCoreService.filterExistingModelIds(any(), any())).thenReturn(Set.of(11L, 22L, 31L));
        when(relationMapper.selectActive(any(), any(), any(), any())).thenReturn(null);
        when(relationMapper.maxSort(any(), any(), any())).thenReturn(0);
        service.associate(11L, 22L, "equipment", "equipment");
        service.associate(11L, 31L, "equipment", "inspection_item");

        ArgumentCaptor<ModelModelRelationDO> rows = ArgumentCaptor.forClass(ModelModelRelationDO.class);
        verify(relationMapper, org.mockito.Mockito.times(2)).insert(rows.capture());
        ModelModelRelationDO same = rows.getAllValues().get(0);
        ModelModelRelationDO cross = rows.getAllValues().get(1);
        assertEquals("equipment", same.getSourceEntityTypeCode());
        assertEquals("equipment", same.getTargetEntityTypeCode());
        assertEquals("equipment", cross.getSourceEntityTypeCode());
        assertEquals("inspection_item", cross.getTargetEntityTypeCode());
    }

    @Test
    void modelCannotPointAtItself() {
        assertThrows(ServiceException.class,
                () -> service.associate(11L, 11L, "equipment", "equipment"));
        verify(relationMapper, never()).insert(any(ModelModelRelationDO.class));
    }

    @Test
    void reverseListReadsRowsPointingAtTheTarget() {
        when(relationMapper.selectByTarget(22L, "equipment", "equipment")).thenReturn(List.of(
                ModelModelRelationDO.builder()
                        .sourceEntityTypeCode("equipment")
                        .sourceModelId(11L)
                        .targetEntityTypeCode("equipment")
                        .targetModelId(22L)
                        .build()));

        assertEquals(List.of(11L), service.listSourceModelIds(22L, "equipment", "equipment"));
    }

    @Test
    void replaceTargetsDropsMissingAndAddsSubmitted() {
        when(relationMapper.selectBySource(11L, "equipment", "equipment")).thenReturn(List.of(
                ModelModelRelationDO.builder()
                        .sourceModelId(11L)
                        .targetModelId(22L)
                        .sourceEntityTypeCode("equipment")
                        .targetEntityTypeCode("equipment")
                        .build()));
        when(modelCoreService.filterExistingModelIds(any(), any())).thenReturn(Set.of(11L, 31L));
        when(relationMapper.selectActive(any(), any(), any(), any())).thenReturn(null);
        when(relationMapper.maxSort(any(), any(), any())).thenReturn(0);
        service.replaceTargets("equipment", 11L, List.of(31L));

        verify(relationMapper).deleteLink(11L, 22L, "equipment", "equipment");
        ArgumentCaptor<ModelModelRelationDO> inserted = ArgumentCaptor.forClass(ModelModelRelationDO.class);
        verify(relationMapper).insert(inserted.capture());
        assertEquals(31L, inserted.getValue().getTargetModelId());
    }

    @Test
    void replaceTargetsRejectsSelf() {
        assertThrows(ServiceException.class,
                () -> service.replaceTargets("equipment", 11L, List.of(11L)));
        verify(relationMapper, never()).deleteLink(any(), any(), any(), any());
    }
}
