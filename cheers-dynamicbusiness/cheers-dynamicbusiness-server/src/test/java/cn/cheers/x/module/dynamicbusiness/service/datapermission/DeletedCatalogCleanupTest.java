package cn.cheers.x.module.dynamicbusiness.service.datapermission;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.CatalogOwnershipDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.CatalogOwnershipMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.ManagementUnitCatalogMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletedCatalogCleanupTest {

    @Mock
    private CatalogOwnershipMapper catalogOwnershipMapper;
    @Mock
    private ManagementUnitCatalogMapper managementUnitCatalogMapper;
    @Mock
    private ManagementUnitCatalogQueryService managementUnitCatalogQueryService;
    @InjectMocks
    private CatalogOwnershipService catalogOwnershipService;
    @InjectMocks
    private ManagementUnitCatalogService managementUnitCatalogService;

    private static CatalogOwnershipDO declaration(String source, String target) {
        CatalogOwnershipDO row = new CatalogOwnershipDO();
        row.setRegistryCode(source);
        row.setTargetRegistryCode(target);
        return row;
    }

    @Test
    void ownershipRejectsWhenAnotherCatalogPointsHere() {
        when(catalogOwnershipMapper.selectListByTargetRegistryCode("facility"))
                .thenReturn(List.of(declaration("equipment", "facility")));

        assertThatThrownBy(() -> catalogOwnershipService.deleteDeclarationsOfDeletedCatalog("facility"))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("equipment");
        verify(catalogOwnershipMapper, never()).deleteByRegistryCode(anyString());
    }

    @Test
    void ownershipDeletesOwnDeclarations() {
        when(catalogOwnershipMapper.selectListByTargetRegistryCode("billing")).thenReturn(List.of());

        catalogOwnershipService.deleteDeclarationsOfDeletedCatalog("billing");

        verify(catalogOwnershipMapper).deleteByRegistryCode("billing");
    }

    @Test
    void registrationRejectsManagementUnitCatalog() {
        when(managementUnitCatalogQueryService.findManagementUnitRegistryCode()).thenReturn("organization");

        assertThatThrownBy(() -> managementUnitCatalogService.deleteRegistrationsOfDeletedCatalog("organization"))
                .isInstanceOf(ServiceException.class);
        verify(managementUnitCatalogMapper, never()).deleteByRegistryCode(anyString());
    }

    @Test
    void registrationDropsOrdinaryCatalog() {
        when(managementUnitCatalogQueryService.findManagementUnitRegistryCode()).thenReturn("organization");

        managementUnitCatalogService.deleteRegistrationsOfDeletedCatalog("billing");

        verify(managementUnitCatalogMapper).deleteByRegistryCode("billing");
    }
}
