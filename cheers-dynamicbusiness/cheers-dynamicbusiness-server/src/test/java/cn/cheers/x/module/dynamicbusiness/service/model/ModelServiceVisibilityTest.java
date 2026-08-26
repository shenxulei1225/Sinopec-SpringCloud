package cn.cheers.x.module.dynamicbusiness.service.model;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.service.model.core.ModelCoreService;
import cn.cheers.x.module.dynamicbusiness.service.model.governance.MasterDataCapabilityChecker;
import cn.cheers.x.module.dynamicbusiness.service.model.governance.ModelGovernanceQueryService;
import cn.cheers.x.module.dynamicbusiness.service.model.governance.ModelGovernanceQueryServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ModelServiceVisibilityTest {

    @Test
    void getLocalModelFromOtherFacilityIsRejected() {
        ModelCoreService modelCoreService = mock(ModelCoreService.class);
        MasterDataCapabilityChecker capabilityChecker = mock(MasterDataCapabilityChecker.class);
        ModelGovernanceQueryService governanceQueryService = new ModelGovernanceQueryServiceImpl();
        ModelServiceImpl service = new ModelServiceImpl();
        ReflectionTestUtils.setField(service, "modelCoreService", modelCoreService);
        ReflectionTestUtils.setField(service, "modelGovernanceQueryService", governanceQueryService);
        ReflectionTestUtils.setField(service, "masterDataCapabilityChecker", capabilityChecker);
        ModelDO local = new ModelDO();
        local.setId(1L);
        local.setGovernanceStatus("LOCAL");
        local.setOriginFacilityId(20L);
        when(modelCoreService.get(1L)).thenReturn(local);
        when(capabilityChecker.canManageNetworkModelData()).thenReturn(false);

        ServiceException error = assertThrows(ServiceException.class, () -> service.getModel(1L, 10L));

        assertEquals(403, error.getCode());
    }
}
