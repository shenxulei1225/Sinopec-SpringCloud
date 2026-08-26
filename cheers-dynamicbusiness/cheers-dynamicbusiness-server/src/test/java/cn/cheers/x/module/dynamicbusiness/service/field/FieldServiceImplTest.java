package cn.cheers.x.module.dynamicbusiness.service.field;

import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldPageReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.model.governance.MasterDataCapabilityChecker;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FieldServiceImplTest {

    @Test
    void pageHidesLocalFieldsFromOtherFacilities() {
        FieldMapper fieldMapper = mock(FieldMapper.class);
        MasterDataCapabilityChecker capabilityChecker = mock(MasterDataCapabilityChecker.class);
        FieldServiceImpl service = newService(fieldMapper, mock(ModelMapper.class), capabilityChecker);
        FieldDO company = FieldDO.builder().id(1L).governanceStatus("COMPANY").build();
        FieldDO localHere = FieldDO.builder().id(2L).governanceStatus("LOCAL").originFacilityId(10L).build();
        FieldDO localElsewhere = FieldDO.builder().id(3L).governanceStatus("LOCAL").originFacilityId(20L).build();
        when(fieldMapper.search(null, null, null, null))
                .thenReturn(java.util.List.of(company, localHere, localElsewhere));
        when(capabilityChecker.canManageNetworkModelData()).thenReturn(false);
        FieldPageReqVO request = new FieldPageReqVO();
        ReflectionTestUtils.setField(request, "effectiveFacilityId", 10L);

        PageResult<?> result = service.page(request);

        assertEquals(2, result.getList().size());
        assertEquals(2L, result.getTotal());
    }

    @Test
    void deleteRejectsCompanyField() {
        FieldMapper fieldMapper = mock(FieldMapper.class);
        FieldServiceImpl service = newService(
                fieldMapper, mock(ModelMapper.class), mock(MasterDataCapabilityChecker.class));
        FieldDO company = FieldDO.builder().id(1L).governanceStatus("COMPANY").source("USER").build();
        when(fieldMapper.selectById(1L)).thenReturn(company);

        ServiceException error = assertThrows(ServiceException.class, () -> service.deleteField(1L));

        assertEquals(403, error.getCode());
    }

    @Test
    void createBareCompanyFieldRequiresCapability() {
        MasterDataCapabilityChecker capabilityChecker = mock(MasterDataCapabilityChecker.class);
        when(capabilityChecker.canCreateCompanyStandard()).thenReturn(false);
        FieldServiceImpl service = newService(
                mock(FieldMapper.class), mock(ModelMapper.class), capabilityChecker);
        FieldCreateReqVO request = new FieldCreateReqVO();
        request.setName("公司字段");
        request.setType("TEXT");
        request.setSource("USER");

        try (MockedStatic<SecurityFrameworkUtils> security = mockStatic(SecurityFrameworkUtils.class)) {
            security.when(SecurityFrameworkUtils::getLoginUserId).thenReturn(100L);
            ServiceException error = assertThrows(ServiceException.class, () -> service.createField(request));
            assertEquals(403, error.getCode());
        }
    }

    @Test
    void createFieldForLocalModelWritesLocalGovernanceWithoutApprovalQueue() {
        FieldMapper fieldMapper = mock(FieldMapper.class);
        ModelMapper modelMapper = mock(ModelMapper.class);
        FieldServiceImpl service = newService(
                fieldMapper, modelMapper, mock(MasterDataCapabilityChecker.class));

        ModelDO localModel = new ModelDO();
        localModel.setId(20L);
        localModel.setGovernanceStatus("LOCAL");
        localModel.setOriginFacilityId(10L);
        when(modelMapper.selectById(20L)).thenReturn(localModel);
        doAnswer(invocation -> {
            FieldDO field = invocation.getArgument(0);
            field.setId(30L);
            return 1;
        }).when(fieldMapper).insert(any(FieldDO.class));

        FieldCreateReqVO request = new FieldCreateReqVO();
        request.setName("本地检查值");
        request.setType("TEXT");
        request.setSource("USER");
        request.setStatus(1);
        request.setIndexStrategy("GIN");
        request.setModelId(20L);

        try (MockedStatic<TenantContextHolder> tenant = mockStatic(TenantContextHolder.class);
             MockedStatic<SecurityFrameworkUtils> security = mockStatic(SecurityFrameworkUtils.class)) {
            tenant.when(TenantContextHolder::getRequiredTenantId).thenReturn(1L);
            security.when(SecurityFrameworkUtils::getLoginUserId).thenReturn(100L);

            assertEquals(30L, service.createField(request));
        }

        verify(fieldMapper).insert(org.mockito.ArgumentMatchers.<FieldDO>argThat(field ->
                "LOCAL".equals(field.getGovernanceStatus())
                        && Long.valueOf(10L).equals(field.getOriginFacilityId())
                        && Long.valueOf(100L).equals(field.getCreatorUserId())));
    }

    private static FieldServiceImpl newService(
            FieldMapper fieldMapper,
            ModelMapper modelMapper,
            MasterDataCapabilityChecker capabilityChecker) {
        FieldServiceImpl service = new FieldServiceImpl();
        ReflectionTestUtils.setField(service, "fieldMapper", fieldMapper);
        ReflectionTestUtils.setField(service, "modelMapper", modelMapper);
        ReflectionTestUtils.setField(service, "smartSearchableService", mock(SmartSearchableService.class));
        ReflectionTestUtils.setField(service, "stringRedisTemplate", mock(StringRedisTemplate.class));
        ReflectionTestUtils.setField(service, "masterDataCapabilityChecker", capabilityChecker);
        return service;
    }
}
